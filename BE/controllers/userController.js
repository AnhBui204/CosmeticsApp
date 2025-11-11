import asyncHandler from 'express-async-handler';
import { User } from '../models/userModel.js';
import { Product } from '../models/productModel.js'; // Cần model Product để populate wishlist
import bcrypt from 'bcryptjs';
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
       if (req.body.addresses && Array.isArray(req.body.addresses)) {
            user.addresses = req.body.addresses;
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
// CHANGE PASSWORD
/**
 * @desc    Đổi mật khẩu người dùng
 * @route   PUT /api/users/change-password
 * @access  Private
 */
export const changePassword = asyncHandler(async (req, res) => {
    const { oldPassword, newPassword } = req.body;

    if (!oldPassword || !newPassword) {
        res.status(400);
        throw new Error('Vui lòng cung cấp đầy đủ thông tin');
    }

    const user = await User.findById(req.user._id);
    if (!user) {
        res.status(404);
        throw new Error('Người dùng không tồn tại');
    }

    // Kiểm tra mật khẩu cũ
    const isMatch = await bcrypt.compare(oldPassword, user.password);
    if (!isMatch) {
        res.status(401);
        throw new Error('Mật khẩu cũ không đúng');
    }

    // Mã hóa mật khẩu mới
    const salt = await bcrypt.genSalt(10);
    user.password = await bcrypt.hash(newPassword, salt);

    await user.save();

    res.status(200).json({ message: 'Đổi mật khẩu thành công' });
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
 * @route   GET 
 * @access  Private
 */

export const getWishlist = asyncHandler(async (req, res) => {
    const targetUserId = req.params.userId || req.user._id.toString();

    // permission: owner or admin
    if (req.params.userId && req.user._id.toString() !== targetUserId && req.user.role !== 'admin') {
        res.status(403);
        throw new Error('Không có quyền xem wishlist của người dùng khác');
    }

    const user = await User.findById(targetUserId).populate({
        path: 'wishlist',
        model: 'Product',
        select: 'name price salePrice images ratings'
    });

    if (!user) {
        res.status(404);
        throw new Error('Không tìm thấy người dùng');
    }

    res.json(user.wishlist || []);
});


/**
 * @desc    Thêm sản phẩm vào danh sách yêu thích
 * @route   POST 
 * @access  Private
 */
export const addToWishlist = asyncHandler(async (req, res) => {
    const { productId } = req.body;
    const targetUserId = req.params.userId || req.user._id.toString();

    if (!productId) {
        res.status(400);
        throw new Error('Thiếu productId');
    }

    // permission
    if (req.params.userId && req.user._id.toString() !== targetUserId && req.user.role !== 'admin') {
        res.status(403);
        throw new Error('Không có quyền thao tác wishlist của người dùng khác');
    }

    // validate product exists
    const product = await Product.findById(productId);
    if (!product) {
        res.status(404);
        throw new Error('Không tìm thấy sản phẩm');
    }

    const user = await User.findById(targetUserId);
    if (!user) {
        res.status(404);
        throw new Error('Không tìm thấy người dùng');
    }

    // avoid duplicates
    if (user.wishlist && user.wishlist.find(id => id.toString() === productId)) {
        return res.status(200).json({ success: true, message: 'Sản phẩm đã có trong wishlist' });
    }

    user.wishlist.push(productId);
    await user.save();

    // return populated wishlist
    await user.populate({ path: 'wishlist', select: 'name price salePrice images ratings' });
    res.status(201).json(user.wishlist);
});


/**
 * @desc    Xóa sản phẩm khỏi danh sách yêu thích
 * @route   DELETE 
 * @access  Private
 */
export const removeFromWishlist = asyncHandler(async (req, res) => {
    const productId = req.params.productId || req.body.productId;
    const targetUserId = req.params.userId || req.user._id.toString();

    if (!productId) {
        res.status(400);
        throw new Error('Thiếu productId');
    }

    // permission
    if (req.params.userId && req.user._id.toString() !== targetUserId && req.user.role !== 'admin') {
        res.status(403);
        throw new Error('Không có quyền thao tác wishlist của người dùng khác');
    }

    const user = await User.findById(targetUserId);
    if (!user) {
        res.status(404);
        throw new Error('Không tìm thấy người dùng');
    }

    const idx = user.wishlist.findIndex(id => id.toString() === productId);
    if (idx === -1) {
        res.status(404);
        throw new Error('Sản phẩm không có trong wishlist');
    }

    user.wishlist.splice(idx, 1);
    await user.save();

    await user.populate({ path: 'wishlist', select: 'name price salePrice images ratings' });
    res.json(user.wishlist);
});

