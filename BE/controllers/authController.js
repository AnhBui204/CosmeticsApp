import asyncHandler from 'express-async-handler';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { User } from '../models/userModel.js';
import { Cart } from '../models/cartModel.js';
import { OAuth2Client } from 'google-auth-library';
import nodemailer from 'nodemailer';
import { OTP } from '../models/otpModel.js';
import crypto from 'crypto';

const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);
// --- Helper tạo token ---
const generateToken = (id) => {
    return jwt.sign({ id }, process.env.JWT_SECRET, { expiresIn: '30d' });
};

// --- Đăng ký ---
export const registerUser = asyncHandler(async (req, res) => {
    const { fullName, email, password, phoneNumber,role } = req.body;

    if (!fullName || !email || !password) {
        res.status(400);
        throw new Error('Vui lòng nhập đầy đủ thông tin bắt buộc');
    }

    const userExists = await User.findOne({ email });
    if (userExists) {
        res.status(400);
        throw new Error('Email đã được đăng ký');
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const user = await User.create({
        fullName,
        email,
        password: hashedPassword,
        phoneNumber,
        role
    });

    await Cart.create({ userId: user._id, items: [] });

    res.status(201).json({
        success: true,
        message: 'Đăng ký thành công',
        data: {
            _id: user._id,
            fullName: user.fullName,
            email: user.email,
            role: user.role,
            token: generateToken(user._id),
        }
    });
});

// --- Đăng nhập ---
export const loginUser = asyncHandler(async (req, res) => {
    const { email, password } = req.body;

    const user = await User.findOne({ email });

    if (user && await bcrypt.compare(password, user.password)) {
        res.json({
            success: true,
            message: 'Đăng nhập thành công',
            data: {
                _id: user._id,
                fullName: user.fullName,
                email: user.email,
                role: user.role,
                phoneNumber:user.phoneNumber,
                wishlist: user.wishlist,
                token: generateToken(user._id),
            }
        });
    } else {
        res.status(401).json({
            success: false,
            message: 'Email hoặc mật khẩu không chính xác'
        });
    }
});

export const googleLogin = asyncHandler(async (req, res) => {
    const { idToken,role } = req.body;

    console.log("Received Google login request:", req.body); // log body từ FE

    try {
        const ticket = await client.verifyIdToken({
            idToken,
            audience: process.env.GOOGLE_CLIENT_ID,
        });
        const payload = ticket.getPayload();

        const { email, name, sub: googleId } = payload;

        let user = await User.findOne({ email });

  if (!user) {
    // Tạo mới user
    user = await User.create({
        fullName: name,
        email,
        password: Math.random().toString(36).slice(-8),
        role,
        googleId,
        loginProvider: 'google',
    });
    await Cart.create({ userId: user._id, items: [] });
} else if (!user.loginProvider || user.loginProvider === 'email') {
    // Nếu user tồn tại nhưng chưa có googleId / loginProvider thì update
    user.googleId = googleId;
    user.loginProvider = 'google';
    await user.save();
}


        const responseData = {
            success: true,
            message: 'Đăng nhập Google thành công',
            data: {
                _id: user._id,
                fullName: user.fullName,
                email: user.email,
                role: user.role,
                  phoneNumber: user.phoneNumber,
                loginProvider: user.loginProvider,
                token: generateToken(user._id),
            },
        };

        console.log("Sending Google login response:", responseData); // log trước khi gửi về FE

        res.json(responseData);
    } catch (error) {
        console.error("Google login error:", error);
        res.status(401).json({ success: false, message: 'Token không hợp lệ' });
    }
});


export const forgotPassword = asyncHandler(async (req, res) => {
    const { email } = req.body;
    const user = await User.findOne({ email });
    if (!user) {
        res.status(404);
        throw new Error('Email chưa đăng ký');
    }

    const otpCode = Math.floor(1000 + Math.random() * 9000).toString(); // 4 số
    const expiresAt = new Date(Date.now() + 10 * 60 * 1000); // 10 phút

    await OTP.deleteMany({ email });
    await OTP.create({ email, code: otpCode, expiresAt });
    await sendOTPEmail(email, otpCode);

    res.json({ success: true, message: 'Mã OTP đã được gửi đến email của bạn' });
});

// --- Verify OTP (mới) ---
export const verifyOTP = asyncHandler(async (req, res) => {
    const { email, code } = req.body;
      console.log('Received verifyOTP request:', { email, code });

    const otpEntry = await OTP.findOne({ email, code });

    if (!otpEntry || otpEntry.expiresAt < new Date()) {
        res.status(400);
        throw new Error('Mã OTP không hợp lệ hoặc đã hết hạn');
    }

    // Tạo resetToken random
   const resetToken = crypto.randomBytes(32).toString('hex');
    const expiresAt = new Date(Date.now() + 10 * 60 * 1000); // token 10 phút
    otpEntry.resetToken = resetToken;
    otpEntry.resetTokenExpiresAt = expiresAt;
    await otpEntry.save();
  console.log('OTP verified, sending resetToken for:', email,resetToken);
    res.json({ success: true, message: 'OTP hợp lệ', resetToken });
});

// --- Reset password ---
export const resetPassword = asyncHandler(async (req, res) => {
    const { resetToken, newPassword } = req.body;
console.log("Data from FE:",resetToken,newPassword)
    const otpEntry = await OTP.findOne({ resetToken });
    if (!otpEntry || otpEntry.resetTokenExpiresAt < new Date()) {
        res.status(400);
        throw new Error('Reset token không hợp lệ hoặc đã hết hạn');
    }

    const user = await User.findOne({ email: otpEntry.email });
    if (!user) {
        res.status(404);
        throw new Error('User không tồn tại');
    }

    user.password = await bcrypt.hash(newPassword, 10);
    await user.save();

    // Xoá OTP và token sau khi dùng
    await OTP.deleteMany({ email: otpEntry.email });

    res.json({ success: true, message: 'Mật khẩu đã được đặt lại thành công' });
});

export const sendOTPEmail = async (email, code) => {
  const transporter = nodemailer.createTransport({
    service: 'Gmail',
    auth: {
      user: process.env.EMAIL_USER,
      pass: process.env.EMAIL_PASS,
    },
  });

  const mailOptions = {
    from: process.env.EMAIL_USER,
    to: email,
    subject: 'Mã OTP đặt lại mật khẩu',
    text: `Mã OTP của bạn là: ${code}. Mã có hiệu lực trong 10 phút.`,
  };

  await transporter.sendMail(mailOptions);
};



