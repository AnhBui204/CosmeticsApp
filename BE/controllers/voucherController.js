import asyncHandler from 'express-async-handler';
import { Voucher } from '../models/voucherModel.js';
import { Cart } from '../models/cartModel.js';

/**
 * @desc    Lấy danh sách voucher có thể sử dụng
 * @route   GET /api/vouchers/available
 * @access  Private
 */
export const getAvailableVouchers = asyncHandler(async (req, res) => {
    // Lấy các voucher còn hoạt động, còn hạn, còn lượt dùng
    const vouchers = await Voucher.find({
        isActive: true,
        validTo: { $gte: Date.now() },
        usageLimit: { $gt: 0 }
    }).sort({ validFrom: 1 }); // Sắp xếp theo ngày bắt đầu

    res.json(vouchers);
});

/**
 * @desc    Áp dụng thử voucher vào giỏ hàng
 * @route   POST /api/vouchers/apply
 * @access  Private
 */
export const applyVoucherToCart = asyncHandler(async (req, res) => {
    const { code } = req.body;
    const userId = req.user._id;

    if (!code) {
        res.status(400);
        throw new Error('Vui lòng nhập mã voucher');
    }

    // 1. Lấy voucher
    const voucher = await Voucher.findOne({
        code: code.toUpperCase(),
        isActive: true,
        validTo: { $gte: Date.now() },
        usageLimit: { $gt: 0 }
    });

    if (!voucher) {
        res.status(404);
        throw new Error('Mã giảm giá không hợp lệ, đã hết hạn hoặc hết lượt');
    }

    // 2. Lấy giỏ hàng (chỉ cần tổng tiền)
    const cart = await Cart.findOne({ userId: userId });
    if (!cart || cart.items.length === 0) {
        res.status(400);
        throw new Error('Giỏ hàng rỗng, không thể áp dụng mã');
    }
    
    // (Lưu ý: totalAmount trong cart có thể cũ, nên tính lại subTotal
    // Tạm thời dùng totalAmount đã lưu)
    const subTotal = cart.totalAmount; 

    // 3. Kiểm tra điều kiện
    if (subTotal < voucher.minOrderAmount) {
         res.status(400);
         throw new Error(`Đơn hàng tối thiểu ${voucher.minOrderAmount.toLocaleString('vi-VN')}đ để áp dụng voucher`);
    }

    // 4. Tính toán
    let discountAmount = 0;
    if (voucher.discountType === 'fixed_amount') {
        discountAmount = voucher.discountValue;
    } else if (voucher.discountType === 'percentage') {
        discountAmount = (subTotal * voucher.discountValue) / 100;
        if (voucher.maxDiscountAmount && discountAmount > voucher.maxDiscountAmount) {
            discountAmount = voucher.maxDiscountAmount;
        }
    }
    
    const finalTotal = subTotal - discountAmount;

    res.json({
        success: true,
        code: voucher.code,
        discountAmount: discountAmount,
        subTotal: subTotal,
        finalTotal: finalTotal < 0 ? 0 : finalTotal
    });
});
