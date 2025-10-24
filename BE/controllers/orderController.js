import asyncHandler from 'express-async-handler';
import mongoose from 'mongoose';
import { Order } from '../models/orderModel.js';
import { Cart } from '../models/cartModel.js';
import { Product } from '../models/productModel.js';
import { User } from '../models/userModel.js';
import { Voucher } from '../models/voucherModel.js';

// Hàm helper
const generateOrderCode = () => {
    // Tạo mã đơn hàng ngẫu nhiên (ví dụ)
    return `MP${Math.floor(100000 + Math.random() * 900000)}`;
};

/**
 * @desc    Tạo đơn hàng mới (Checkout)
 * @route   POST /api/orders/checkout
 * @access  Private
 */
export const checkout = asyncHandler(async (req, res) => {
    const { shippingAddressId, paymentMethod, voucherCode } = req.body;
    const userId = req.user._id;

    if (!mongoose.Types.ObjectId.isValid(shippingAddressId)) {
        res.status(400);
        throw new Error('Địa chỉ giao hàng không hợp lệ');
    }

    // 1. Lấy giỏ hàng
    const cart = await Cart.findOne({ userId: userId }).populate('items.productId');
    if (!cart || cart.items.length === 0) {
        res.status(400);
        throw new Error('Giỏ hàng của bạn đang rỗng');
    }

    // 2. Lấy địa chỉ giao hàng
    const user = await User.findById(userId);
    const shippingAddress = user.addresses.id(shippingAddressId);
    if (!shippingAddress) {
        res.status(404);
        throw new Error('Không tìm thấy địa chỉ giao hàng');
    }

    let subTotal = 0; // Tổng tiền hàng
    let finalTotalAmount = 0; // Tổng tiền cuối cùng
    let shippingFee = 30000; // Ví dụ phí ship cố định
    let discountAmount = 0;

    // 3. Kiểm tra tồn kho và tính tổng tiền hàng
    const orderItems = [];
    for (const item of cart.items) {
        const product = item.productId;
        if (product.stockQuantity < item.quantity) {
            res.status(400);
            throw new Error(`Sản phẩm "${product.name}" không đủ tồn kho`);
        }
        
        const price = product.salePrice || product.price;
        subTotal += price * item.quantity;
        
        // Tạo item cho đơn hàng
        orderItems.push({
            productId: product._id,
            name: product.name,
            price: price,
            quantity: item.quantity,
            image: product.images[0] // Lấy ảnh đầu tiên
        });
    }
    
    finalTotalAmount = subTotal + shippingFee;

    // 4. Kiểm tra Voucher (nếu có)
    let voucher = null;
    if (voucherCode) {
        voucher = await Voucher.findOne({
            code: voucherCode.toUpperCase(),
            isActive: true,
            validTo: { $gte: Date.now() }, // $gte = Greater than or equal
            usageLimit: { $gt: 0 } // $gt = Greater than
        });

        if (!voucher) {
            res.status(400);
            throw new Error('Mã giảm giá không hợp lệ hoặc đã hết hạn/lượt sử dụng');
        }
        if (subTotal < voucher.minOrderAmount) {
             res.status(400);
             throw new Error(`Đơn hàng tối thiểu ${voucher.minOrderAmount.toLocaleString('vi-VN')}đ để áp dụng voucher`);
        }
        
        // Tính tiền giảm
        if (voucher.discountType === 'fixed_amount') {
            discountAmount = voucher.discountValue;
        } else if (voucher.discountType === 'percentage') {
            discountAmount = (subTotal * voucher.discountValue) / 100;
            if (voucher.maxDiscountAmount && discountAmount > voucher.maxDiscountAmount) {
                discountAmount = voucher.maxDiscountAmount;
            }
        }
        finalTotalAmount -= discountAmount;
    }

    // 5. Tạo đơn hàng
    const order = new Order({
        orderCode: generateOrderCode(),
        userId: userId,
        items: orderItems,
        totalAmount: finalTotalAmount,
        shippingAddress: shippingAddress.toObject(), // Sao chép địa chỉ
        paymentMethod: paymentMethod,
        shippingFee: shippingFee,
        voucherCode: voucher ? voucher.code : null,
        status: 'pending' // Chờ xử lý
    });

    // 6. Cập nhật tồn kho và voucher (Transaction)
    const session = await mongoose.startSession();
    session.startTransaction();
    try {
        const newOrder = await order.save({ session });

        // Trừ tồn kho
        for (const item of order.items) {
            await Product.findByIdAndUpdate(item.productId, {
                $inc: { stockQuantity: -item.quantity } // $inc = increment (giảm đi)
            }, { session, new: true }); // new: true để trả về document mới
        }
        
        // Trừ lượt dùng voucher
        if(voucher) {
            await Voucher.findByIdAndUpdate(voucher._id, {
                 $inc: { usageLimit: -1, usedCount: 1 }
            }, { session });
        }
        
        // Xóa giỏ hàng
        await Cart.findOneAndUpdate({ userId: userId }, { items: [], totalAmount: 0 }, { session });
        
        // Tạo thông báo cho user
        // await Notification.create([{ // Phải là array khi dùng create với session
        //     userId: userId,
        //     title: 'Đặt hàng thành công!',
        //     body: `Đơn hàng ${newOrder.orderCode} của bạn đã được tiếp nhận.`,
        //     type: 'order_status',
        //     relatedId: newOrder._id
        // }], { session });

        // Commit transaction
        await session.commitTransaction();
        session.endSession();
        
        res.status(201).json(newOrder);

    } catch (error) {
        // Rollback nếu có lỗi
        await session.abortTransaction();
        session.endSession();
        res.status(500);
        throw new Error('Tạo đơn hàng thất bại: ' + error.message);
    }
});

/**
 * @desc    Lấy lịch sử đơn hàng
 * @route   GET /api/orders
 * @access  Private
 */
export const getMyOrders = asyncHandler(async (req, res) => {
    const orders = await Order.find({ userId: req.user._id })
        .sort({ createdAt: -1 }); // Mới nhất lên đầu
    res.json(orders);
});

/**
 * @desc    Lấy chi tiết 1 đơn hàng
 * @route   GET /api/orders/:id
 * @access  Private
 */
export const getOrderDetails = asyncHandler(async (req, res) => {
    
    if (!mongoose.Types.ObjectId.isValid(req.params.id)) {
        res.status(400);
        throw new Error('OrderId không hợp lệ');
    }

    const order = await Order.findById(req.params.id).populate('items.productId', 'images'); // Lấy ảnh sản phẩm
    
    if (order && order.userId.toString() === req.user._id.toString()) {
        res.json(order);
    } else if (!order) {
        res.status(404);
        throw new Error('Không tìm thấy đơn hàng');
    } else {
        res.status(403); // Forbidden
        throw new Error('Bạn không có quyền xem đơn hàng này');
    }
});
