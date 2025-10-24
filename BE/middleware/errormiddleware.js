/**
 * @desc Middleware bắt lỗi 404 (Không tìm thấy route)
 */
export const notFound = (req, res, next) => {
    const error = new Error(`Không tìm thấy - ${req.originalUrl}`);
    res.status(404);
    next(error);
};

/**
 * @desc Middleware xử lý lỗi chung (Bắt tất cả lỗi)
 */
export const errorHandler = (err, req, res, next) => {
    // Đôi khi lỗi có status 200, ta chuyển về 500
    const statusCode = res.statusCode === 200 ? 500 : res.statusCode;
    res.status(statusCode);

    console.error(err.stack); // Log lỗi ra console

    res.json({
        message: err.message,
        // Chỉ hiện stack trace khi ở môi trường development
        stack: process.env.NODE_ENV === 'production' ? null : err.stack,
    });
};
