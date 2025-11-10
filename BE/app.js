import express from 'express';
import dotenv from 'dotenv';
import cors from 'cors';
import mongoose from 'mongoose';

// --- Import tất cả các file routes ---
import authRoutes from './routes/authRoutes.js';
import userRoutes from './routes/userRoutes.js';
import productRoutes from './routes/productRoutes.js';
import cartRoutes from './routes/cartRoutes.js';
import orderRoutes from './routes/orderRoutes.js';
import reviewRoutes from './routes/reviewRoutes.js';
import voucherRoutes from './routes/voucherRoutes.js';
import paymentRoutes from './routes/paymentRoutes.js';
// import adminRoutes from './routes/adminRoutes.js';

// --- Middleware xử lý lỗi (nên đặt ở file riêng) ---
import { notFound, errorHandler } from './middleware/errormiddleware.js';

// --- Cấu hình ---
dotenv.config(); // Load biến môi trường từ file .env

const app = express();

// --- Sử dụng các Middleware cơ bản ---
app.use(cors()); // Cho phép cross-origin
app.use(express.json()); // Parse JSON body

// --- Gắn (MOUNT) CÁC ROUTES ---
// Đây chính là phần bạn yêu cầu
app.use('/api/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/products', productRoutes);
app.use('/api/cart', cartRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/reviews', reviewRoutes);
app.use('/api/vouchers', voucherRoutes);
// app.use('/api/admin', adminRoutes);
app.use('/api/payment', paymentRoutes);
// --- Route test ---
app.get('/', (req, res) => {
    res.send('API cho App Mỹ phẩm đang chạy...');
});

// --- Xử lý lỗi (phải đặt cuối cùng) ---
app.use(notFound); // Bắt lỗi 404
app.use(errorHandler); // Bắt tất cả lỗi khác

// --- Khởi động Server ---
const PORT = process.env.PORT || 5000;
mongoose
    .connect(process.env.MONGO_URI)
    .then(() => {
        console.log('✔ MongoDB connected');
        app.listen(PORT, () => {
            console.log(`Server đang chạy trên cổng ${PORT}`);
        });
    })
    .catch((err) => console.error('✖ DB Error:', err));

export default app;