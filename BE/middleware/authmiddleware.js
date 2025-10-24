import jwt from 'jsonwebtoken';
import asyncHandler from 'express-async-handler';
import { User } from '../models/userModel.js'; // Đảm bảo đường dẫn này đúng

/**
 * @desc Middleware kiểm tra token (Xác thực)
 */
export const isAuthenticated = asyncHandler(async (req, res, next) => {
    let token;

    // Đọc JWT từ 'Authorization' header
    if (req.headers.authorization && req.headers.authorization.startsWith('Bearer')) {
        try {
            // Lấy token (loại bỏ 'Bearer ')
            token = req.headers.authorization.split(' ')[1];

            // Xác thực token
            const decoded = jwt.verify(token, process.env.JWT_SECRET); 

            // Lấy user từ token (loại bỏ password) và gán vào req.user
            req.user = await User.findById(decoded.id).select('-password');

            if (!req.user) {
                 res.status(401);
                 throw new Error('Không tìm thấy người dùng cho token này');
            }

            next(); // Chuyển sang middleware/controller tiếp theo
        } catch (error) {
            console.error(error);
            res.status(401);
            throw new Error('Xác thực thất bại, token không hợp lệ');
        }
    }

    if (!token) {
        res.status(401);
        throw new Error('Xác thực thất bại, không tìm thấy token');
    }
});

/**
 * @desc Middleware kiểm tra quyền Admin (Phân quyền)
 */
export const isAdmin = (req, res, next) => {
    if (req.user && req.user.role === 'admin') {
        next();
    } else {
        res.status(403); // 403 Forbidden
        throw new Error('Không có quyền truy cập, yêu cầu quyền Admin');
    }
};
