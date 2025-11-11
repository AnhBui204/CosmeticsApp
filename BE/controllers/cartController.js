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
 * @desc    Add/update item to a specific user's cart (admin or owner)
 * @route   POST /api/cart/:userId/items
 * @access  Private (owner or admin)
 */
export const addItemToCartForUser = asyncHandler(async (req, res) => {
    const { productId, quantity } = req.body;
    const targetUserId = req.params.userId;

    // permission: either same user or admin
    if (req.user._id.toString() !== targetUserId && req.user.role !== 'admin') {
        res.status(403);
        throw new Error('Không có quyền thao tác trên giỏ hàng của người dùng khác');
    }

    if (!mongoose.Types.ObjectId.isValid(productId) || !mongoose.Types.ObjectId.isValid(targetUserId)) {
        res.status(400);
        throw new Error('productId hoặc userId không hợp lệ');
    }

    const numQuantity = parseInt(quantity) || 1;
    const product = await Product.findById(productId);
    if (!product) {
        res.status(404);
        throw new Error('Không tìm thấy sản phẩm');
    }

    let cart = await Cart.findOne({ userId: targetUserId });
    if (!cart) {
        cart = await Cart.create({ userId: targetUserId, items: [] });
    }

    const priceAtAdd = product.salePrice || product.price;
    const itemIndex = cart.items.findIndex(item => item.productId.toString() === productId);

    if (itemIndex > -1) {
        const newQuantity = cart.items[itemIndex].quantity + numQuantity;
        if (product.stockQuantity < newQuantity) {
            res.status(400);
            throw new Error('Số lượng tồn kho không đủ');
        }
        cart.items[itemIndex].quantity = newQuantity;
        cart.items[itemIndex].priceAtAdd = priceAtAdd;
    } else {
        if (product.stockQuantity < numQuantity) {
            res.status(400);
            throw new Error('Số lượng tồn kho không đủ');
        }
        cart.items.push({ productId, quantity: numQuantity, priceAtAdd });
    }

    cart.totalAmount = await calculateCartTotal(cart.items);
    cart.updatedAt = Date.now();
    await cart.save();

    await cart.populate({
        path: 'items.productId',
        model: 'Product',
        select: 'name images price salePrice'
    });

    res.status(200).json(cart);
});

/**
 * @desc    Remove item from a specific user's cart (admin or owner)
 * @route   DELETE /api/cart/:userId/items/:itemId
 * @access  Private (owner or admin)
 */
export const removeItemFromCartForUser = asyncHandler(async (req, res) => {
    const { itemId, userId: targetUserId } = req.params;

    // permission
    if (req.user._id.toString() !== targetUserId && req.user.role !== 'admin') {
        res.status(403);
        throw new Error('Không có quyền thao tác trên giỏ hàng của người dùng khác');
    }

    const cart = await Cart.findOne({ userId: targetUserId });
    if (!cart) {
        res.status(404);
        throw new Error('Không tìm thấy giỏ hàng của người dùng');
    }

    // Try subdocument id first
    let item = cart.items.id(itemId);
    if (!item) {
        // Fallback: caller may have passed productId instead of itemId
        const itemByProduct = cart.items.find(it => it.productId && it.productId.toString() === itemId);
        if (itemByProduct) {
            // remove by index to avoid calling subdocument.remove() on plain objects
            cart.items = cart.items.filter(it => !(it._id && it._id.toString() === itemByProduct._id.toString()));
            cart.totalAmount = await calculateCartTotal(cart.items);
            await cart.save();

            await cart.populate({
                path: 'items.productId',
                model: 'Product',
                select: 'name images price salePrice stockQuantity'
            });

            return res.json(cart);
        }

        res.status(404);
        throw new Error('Không tìm thấy sản phẩm trong giỏ hàng');
    }

    // remove item safely (works whether item is a mongoose subdoc or plain object)
    if (typeof item.remove === 'function') {
        item.remove();
    } else {
        cart.items = cart.items.filter(it => !(it._id && it._id.toString() === item._id.toString()));
    }
    cart.totalAmount = await calculateCartTotal(cart.items);
    await cart.save();

    await cart.populate({
        path: 'items.productId',
        model: 'Product',
        select: 'name images price salePrice stockQuantity'
    });

    res.json(cart);
});

/**
 * @desc    Get cart for a specific user (owner or admin)
 * @route   GET /api/cart/:userId
 * @access  Private (owner or admin)
 */
export const getCartByUser = asyncHandler(async (req, res) => {
    const targetUserId = req.params.userId;

    if (!mongoose.Types.ObjectId.isValid(targetUserId)) {
        res.status(400);
        throw new Error('userId không hợp lệ');
    }

    // permission: owner or admin
    if (req.user._id.toString() !== targetUserId && req.user.role !== 'admin') {
        res.status(403);
        throw new Error('Không có quyền truy cập giỏ hàng của người dùng khác');
    }

    let cart = await Cart.findOne({ userId: targetUserId })
        .populate({
            path: 'items.productId',
            model: 'Product',
            select: 'name images price salePrice stockQuantity'
        });

    if (!cart) {
        cart = await Cart.create({ userId: targetUserId, items: [] });
    }

    // Recalculate total for display (don't save)
    cart.totalAmount = await calculateCartTotal(cart.items);

    res.json(cart);
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
    if (!cart) {
         res.status(404);
         throw new Error('Không tìm thấy giỏ hàng');
    }

    // Try locate by subdocument id first
    let item = cart.items.id(itemId);
    if (!item) {
        // Fallback: maybe caller passed productId instead of itemId — try find by productId
        const itemByProduct = cart.items.find(it => it.productId && it.productId.toString() === itemId);
        if (itemByProduct) {
                // safe remove by index
                cart.items = cart.items.filter(it => !(it._id && it._id.toString() === itemByProduct._id.toString()));
            cart.totalAmount = await calculateCartTotal(cart.items);
            await cart.save();
            await cart.populate({
                path: 'items.productId',
                model: 'Product',
                select: 'name images price salePrice stockQuantity'
            });
            return res.json(cart);
        }

        res.status(404);
        throw new Error('Không tìm thấy sản phẩm trong giỏ hàng');
    }
    
    // Xóa item khỏi mảng (safe)
    if (typeof item.remove === 'function') {
        item.remove();
    } else {
        cart.items = cart.items.filter(it => !(it._id && it._id.toString() === item._id.toString()));
    }

    cart.totalAmount = await calculateCartTotal(cart.items);
    await cart.save();
    
    await cart.populate({
        path: 'items.productId',
        model: 'Product',
        select: 'name images price salePrice stockQuantity'
    });

    res.json(cart);
});

