import asyncHandler from 'express-async-handler';
import { User } from '../models/userModel.js';
import { Product } from '../models/productModel.js'; // Cần model Product để populate wishlist

/**
 * @desc    Lấy thông tin profile
 * @route   GET /api/users/me
 * @access  Private
 */
export const getMyProfile = asyncHandler(async (req, res) => {
    // req.user được gán từ middleware 'isAuthenticated'
    // .select('-password') để không trả về mật khẩu
    const user = await User.findById(req.user._id).select('-password');
    if (!user) {
        res.status(404);
        throw new Error('Không tìm thấy người dùng');
    }
    res.json(user);
});

/**
 * @desc    Cập nhật thông tin (tên, sđt, địa chỉ)
 * @route   PUT /api/users/me
 * @access  Private
 */
export const updateMyProfile = asyncHandler(async (req, res) => {
    const user = await User.findById(req.user._id);

    if (user) {
        user.fullName = req.body.fullName || user.fullName;
        user.phoneNumber = req.body.phoneNumber || user.phoneNumber;

        // Logic để thêm địa chỉ mới (nếu có)
        // Body gửi lên có thể chứa: { "newAddress": { "street": "...", "city": "..." } }
        if (req.body.newAddress) {
            user.addresses.push(req.body.newAddress);
        }
        // (Bạn có thể mở rộng logic để sửa/xóa địa chỉ)

        const updatedUser = await user.save();
        
        // Trả về thông tin đã cập nhật (không có mật khẩu)
        res.json({
            _id: updatedUser._id,
            fullName: updatedUser.fullName,
            email: updatedUser.email,
            phoneNumber: updatedUser.phoneNumber,
            addresses: updatedUser.addresses,
            role: updatedUser.role,
        });
    } else {
        res.status(404);
        throw new Error('Không tìm thấy người dùng');
    }
});

/**
 * @desc    Lưu FCM token (cho notification)
 * @route   POST /api/users/fcm-token
 * @access  Private
 */
export const updateFcmToken = asyncHandler(async (req, res) => {
    const { fcmToken } = req.body;
    
    // Tìm user và cập nhật token
    await User.findByIdAndUpdate(req.user._id, { fcmToken: fcmToken });
    
    res.status(200).json({ message: 'Đã cập nhật FCM token' });
});

/**
 * @desc    Lấy danh sách yêu thích
 * @route   GET /api/users/wishlist
 * @access  Private
 */
export const getWishlist = asyncHandler(async (req, res) => {
    const user = await User.findById(req.user._id).populate({
        path: 'wishlist',
        model: 'Product', // Tên model Product của bạn (phải import Product model)
        select: 'name price salePrice images ratings' // Chọn các trường cần thiết
    });

    if (!user) {
        res.status(404);
        throw new Error('Không tìm thấy người dùng');
    }
    
    res.json(user.wishlist);
});

/**
 * @desc    Thêm/Xóa sản phẩm khỏi wishlist
 * @route   POST /api/users/wishlist
 * @access  Private
 */
export const toggleWishlist = asyncHandler(async (req, res) => {
    const { productId } = req.body;

    if (!productId) {
        res.status(400);
        throw new Error('Không có productId');
    }
    
    // (Nên) Kiểm tra xem sản phẩm có tồn tại không
    const product = await Product.findById(productId);
    if (!product) {
         res.status(404);
         throw new Error('Không tìm thấy sản phẩm');
    }

    const user = await User.findById(req.user._id);
    // Tìm vị trí của productId trong mảng wishlist
    const index = user.wishlist.indexOf(productId);

    let isAdded;
    if (index > -1) {
        // Đã có -> Xóa đi
        user.wishlist.splice(index, 1);
        isAdded = false;
    } else {
        // Chưa có -> Thêm vào
        user.wishlist.push(productId);
        isAdded = true;
    }

    await user.save();
    res.status(200).json({ success: true, isAdded: isAdded });
});
