import asyncHandler from 'express-async-handler';
import { Review } from '../models/reviewModel.js';
import { Product } from '../models/productModel.js';
import { Order } from '../models/orderModel.js';

/**
 * @desc    Helper: Cập nhật rating trung bình cho sản phẩm
 */
const updateProductRating = async (productId) => {
    const reviews = await Review.find({ productId: productId });
    
    if (reviews.length > 0) {
        const totalRating = reviews.reduce((acc, item) => acc + item.rating, 0);
        const average = totalRating / reviews.length;
        
        await Product.findByIdAndUpdate(productId, {
            'ratings.average': average.toFixed(1), // Làm tròn 1 chữ số
            'ratings.count': reviews.length,
        });
    } else {
         await Product.findByIdAndUpdate(productId, {
            'ratings.average': 0,
            'ratings.count': 0,
        });
    }
};

/**
 * @desc    Tạo review mới
 * @route   POST /api/reviews
 * @access  Private
 */
export const createReview = asyncHandler(async (req, res) => {
    const { productId, orderId, rating, comment, images } = req.body;
    const userId = req.user._id;

    if (!productId || !orderId || !rating) {
        res.status(400);
        throw new Error('Thiếu thông tin productId, orderId hoặc rating');
    }

    // 1. Kiểm tra xem user đã mua sản phẩm này chưa
    const order = await Order.findOne({
        _id: orderId,
        userId: userId,
        status: 'delivered', // Chỉ cho review khi đã giao hàng
        'items.productId': productId
    });
    
    if (!order) {
        res.status(403); // Forbidden
        throw new Error('Bạn không thể đánh giá sản phẩm này (chưa mua hoặc đơn hàng chưa hoàn thành).');
    }

    // 2. Kiểm tra xem user đã review sản phẩm này cho đơn hàng này chưa
    const existingReview = await Review.findOne({
        productId: productId,
        userId: userId,
        orderId: orderId
    });

    if(existingReview) {
        res.status(400);
        throw new Error('Bạn đã đánh giá sản phẩm này cho đơn hàng này rồi.');
    }

    // 3. Tạo review mới
    const review = await Review.create({
        productId,
        orderId,
        userId,
        rating: Number(rating),
        comment,
        images: images || []
    });

    // 4. Cập nhật lại rating trung bình cho sản phẩm (quan trọng)
    await updateProductRating(productId);

    res.status(201).json(review);
});

/**
 * @desc    Lấy review của 1 sản phẩm
 * @route   GET /api/products/:id/reviews
 * @access  Public
 */
export const getReviewsForProduct = asyncHandler(async (req, res) => {
    const productId = req.params.id;
    const pageSize = parseInt(req.query.limit) || 10;
    const page = parseInt(req.query.page) || 1;

    const count = await Review.countDocuments({ productId: productId });
    const reviews = await Review.find({ productId: productId })
        .populate('userId', 'fullName') // Lấy tên của người review
        .sort({ createdAt: -1 }) // Mới nhất lên đầu
        .limit(pageSize)
        .skip(pageSize * (page - 1));

    res.json({
        reviews,
        page,
        totalPages: Math.ceil(count / pageSize),
        totalReviews: count
    });
});
