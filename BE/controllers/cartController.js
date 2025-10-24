import asyncHandler from 'express-async-handler';
import { Cart } from '../models/cartModel.js';
import { Product } from '../models/productModel.js';
import mongoose from 'mongoose';

/**
 * @desc    Hàm helper: Tính toán tổng tiền giỏ hàng
 * @note    Hàm này NÊN lấy giá mới nhất từ DB
 */
const calculateCartTotal = async (cartItems) => {
    let total = 0;
    for (const item of cartItems) {
        const product = await Product.findById(item.productId);
        if (product) {
            const price = product.salePrice || product.price;
            total += price * item.quantity;
            
            // Cập nhật lại giá trong giỏ hàng (phòng khi giá thay đổi)
            item.priceAtAdd = price; 
        }
    }
    return total;
};

/**
 * @desc    Lấy giỏ hàng
 * @route   GET /api/cart
 * @access  Private
 */
export const getCart = asyncHandler(async (req, res) => {
    let cart = await Cart.findOne({ userId: req.user._id })
        .populate({
            path: 'items.productId',
            model: 'Product',
            select: 'name images price salePrice stockQuantity'
        });

    if (!cart) {
        // Trường hợp user mới đăng ký mà chưa có giỏ hàng
        cart = await Cart.create({ userId: req.user._id, items: [] });
    }
    
    // Luôn tính toán lại tổng tiền khi get giỏ hàng để đảm bảo giá đúng
    cart.totalAmount = await calculateCartTotal(cart.items);
    // (Lưu ý: việc này chưa save(), chỉ để hiển thị. 
    // `addItemToCart` và `updateItemQuantity` MỚI là hàm save() giá trị)
    
    res.json(cart);
});

/**
 * @desc    Thêm/Cập nhật sản phẩm trong giỏ
 * @route   POST /api/cart/items
 * @access  Private
 */
export const addItemToCart = asyncHandler(async (req, res) => {
    const { productId, quantity } = req.body;
    const userId = req.user._id;

    if (!mongoose.Types.ObjectId.isValid(productId)) {
        res.status(400);
        throw new Error('ProductId không hợp lệ');
    }
    
    const numQuantity = parseInt(quantity) || 1;

    // 1. Lấy thông tin sản phẩm
    const product = await Product.findById(productId);
    if (!product) {
        res.status(404);
        throw new Error('Không tìm thấy sản phẩm');
    }
   
    // 2. Lấy giỏ hàng
    const cart = await Cart.findOne({ userId });
    const priceAtAdd = product.salePrice || product.price;

    // 3. Kiểm tra sản phẩm đã có trong giỏ chưa
    const itemIndex = cart.items.findIndex(item => item.productId.toString() === productId);

    if (itemIndex > -1) {
        // Đã có -> Cập nhật số lượng
        const newQuantity = cart.items[itemIndex].quantity + numQuantity;
         if (product.stockQuantity < newQuantity) {
            res.status(400);
            throw new Error('Số lượng tồn kho không đủ');
        }
        cart.items[itemIndex].quantity = newQuantity;
        cart.items[itemIndex].priceAtAdd = priceAtAdd; // Cập nhật lại giá
    } else {
        // Chưa có -> Thêm mới
         if (product.stockQuantity < numQuantity) {
            res.status(400);
            throw new Error('Số lượng tồn kho không đủ');
        }
        cart.items.push({ productId, quantity: numQuantity, priceAtAdd });
    }

    cart.totalAmount = await calculateCartTotal(cart.items);
    cart.updatedAt = Date.now();
    
    // Lưu lại các thay đổi giá (nếu có) từ calculateCartTotal
    await cart.save();
    
    // Populate lại để trả về thông tin chi tiết
    await cart.populate({
        path: 'items.productId',
        model: 'Product',
        select: 'name images price salePrice'
    });

    res.status(200).json(cart);
});

/**
 * @desc    Cập nhật số lượng (thay đổi trực tiếp)
 * @route   PUT /api/cart/items/:itemId
 * @access  Private
 */
export const updateItemQuantity = asyncHandler(async (req, res) => {
    const { itemId } = req.params; // itemId ở đây là _id của item trong mảng items
    const { quantity } = req.body; // Số lượng MỚI (ví dụ: 3)
    const userId = req.user._id;

    const numQuantity = parseInt(quantity);
    if (isNaN(numQuantity) || numQuantity <= 0) {
        // Nếu số lượng <= 0, coi như là xóa
        return removeItemFromCart(req, res);
    }

    const cart = await Cart.findOne({ userId: userId });
    const item = cart.items.id(itemId);

    if (item) {
        // Kiểm tra tồn kho
        const product = await Product.findById(item.productId);
        if (product.stockQuantity < numQuantity) {
             res.status(400);
             throw new Error('Số lượng tồn kho không đủ');
        }
        item.quantity = numQuantity;
        item.priceAtAdd = product.salePrice || product.price; // Cập nhật giá
        
        cart.totalAmount = await calculateCartTotal(cart.items);
        await cart.save();
        
        await cart.populate({
            path: 'items.productId',
            model: 'Product',
            select: 'name images price salePrice stockQuantity'
        });
        res.json(cart);
    } else {
         res.status(404);
         throw new Error('Không tìm thấy sản phẩm trong giỏ hàng');
    }
});


/**
 * @desc    Xóa sản phẩm khỏi giỏ
 * @route   DELETE /api/cart/items/:itemId
 * @access  Private
 */
export const removeItemFromCart = asyncHandler(async (req, res) => {
    const { itemId } = req.params; // itemId là _id của item trong mảng
    const userId = req.user._id;
    
    const cart = await Cart.findOne({ userId: userId });
    
    const item = cart.items.id(itemId);
    if (!item) {
         res.status(404);
         throw new Error('Không tìm thấy sản phẩm trong giỏ hàng');
    }
    
    // Xóa item khỏi mảng
    item.remove();
    
    cart.totalAmount = await calculateCartTotal(cart.items);
    await cart.save();
    
    await cart.populate({
        path: 'items.productId',
        model: 'Product',
        select: 'name images price salePrice stockQuantity'
    });

    res.json(cart);
});
