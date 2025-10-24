import asyncHandler from 'express-async-handler';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { User } from '../models/userModel.js';
import { Cart } from '../models/cartModel.js';

// Hàm helper tạo token
const generateToken = (id) => {
    // Đảm bảo bạn đã định nghĩa JWT_SECRET trong file .env
    return jwt.sign({ id }, process.env.JWT_SECRET, {
        expiresIn: '30d', // Hết hạn sau 30 ngày
    });
};

/**
 * @desc    Đăng ký người dùng mới
 * @route   POST /api/auth/register
 * @access  Public
 */
export const registerUser = asyncHandler(async (req, res) => {
    const { fullName, email, password, phoneNumber } = req.body;

    if (!fullName || !email || !password) {
        res.status(400);
        throw new Error('Vui lòng nhập đầy đủ thông tin bắt buộc');
    }

    // 1. Kiểm tra email tồn tại
    const userExists = await User.findOne({ email });
    if (userExists) {
        res.status(400);
        throw new Error('Email đã được đăng ký');
    }

    // 2. Mã hóa mật khẩu
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // 3. Tạo user mới
    const user = await User.create({
        fullName,
        email,
        password: hashedPassword,
        phoneNumber,
    });

    // 4. Tạo giỏ hàng rỗng cho user
    await Cart.create({ userId: user._id, items: [] });

    if (user) {
        // 5. Trả về thông tin user và token
        res.status(201).json({
            _id: user._id,
            fullName: user.fullName,
            email: user.email,
            role: user.role,
            token: generateToken(user._id),
        });
    } else {
        res.status(400);
        throw new Error('Dữ liệu người dùng không hợp lệ');
    }
});

/**
 * @desc    Đăng nhập
 * @route   POST /api/auth/login
 * @access  Public
 */
export const loginUser = asyncHandler(async (req, res) => {
    const { email, password } = req.body;

    // 1. Tìm user theo email
    const user = await User.findOne({ email });

    // 2. Kiểm tra user tồn tại và so sánh mật khẩu
    if (user && (await bcrypt.compare(password, user.password))) {
        // 3. Trả về thông tin user và token
        res.json({
            _id: user._id,
            fullName: user.fullName,
            email: user.email,
            role: user.role,
            wishlist: user.wishlist,
            token: generateToken(user._id),
        });
    } else {
        res.status(401); // Unauthorized
        throw new Error('Email hoặc mật khẩu không chính xác');
    }
});
