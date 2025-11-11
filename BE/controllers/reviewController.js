import asyncHandler from 'express-async-handler';
import { Review } from '../models/reviewModel.js';
import { Product } from '../models/productModel.js';
import { Order } from '../models/orderModel.js';

/**
 * @desc    Helper: Cập nhật rating trung bình cho sản phẩm
 */
const updateProductRating = async (productId) => {
    // Only consider visible reviews when calculating public rating
    const reviews = await Review.find({ productId: productId, isVisible: true });

    if (reviews.length > 0) {
        const totalRating = reviews.reduce((acc, item) => acc + Number(item.rating), 0);
        const average = totalRating / reviews.length;

        await Product.findByIdAndUpdate(productId, {
            'ratings.average': Number(average.toFixed(1)), // 1 decimal
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
    const { productId, productSku, orderId, rating, comment, images } = req.body;
    const userId = req.user._id;

    if (!productId && !productSku) {
        res.status(400);
        throw new Error('Thiếu thông tin productId hoặc productSku');
    }
    if (!orderId || typeof rating === 'undefined') {
        res.status(400);
        throw new Error('Thiếu thông tin orderId hoặc rating');
    }

    // 1. Kiểm tra xem user đã mua sản phẩm này chưa (nếu orderId is an ObjectId string)
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
        $or: [
            { productId: productId || null, orderId: orderId, userId },
            { productSku: productSku || null, orderId: orderId, userId }
        ]
    });

    if (existingReview) {
        res.status(400);
        throw new Error('Bạn đã đánh giá sản phẩm này cho đơn hàng này rồi.');
    }

    // 3. Tạo review mới, lấy email từ req.user
    const review = await Review.create({
        productId: productId || undefined,
        productSku: productSku || undefined,
        orderId: String(orderId),
        userId,
        customerEmail: req.user.email,
        rating: Number(rating),
        comment,
        images: images || [],
        isVisible: true
    });

    // 4. Cập nhật lại rating trung bình cho sản phẩm (chỉ tính reviews visible)
    if (productId) await updateProductRating(productId);

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

    // Public endpoint: only visible reviews
    const query = { productId: productId, isVisible: true };

    const count = await Review.countDocuments(query);
    const reviews = await Review.find(query)
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

// Admin: list all reviews with filters
export const listReviews = asyncHandler(async (req, res) => {
    const { page = 1, limit = 50, visible } = req.query;
    const query = {};
    if (typeof visible !== 'undefined') query.isVisible = visible === 'true';

    const skip = (Number(page) - 1) * Number(limit);
    const total = await Review.countDocuments(query);
    const data = await Review.find(query).sort({ createdAt: -1 }).skip(skip).limit(Number(limit)).populate('userId', 'fullName email');

    res.json({ total, page: Number(page), limit: Number(limit), data });
});

// Admin: toggle visibility
export const toggleReviewVisibility = asyncHandler(async (req, res) => {
    const review = await Review.findById(req.params.id);
    if (!review) {
        res.status(404);
        throw new Error('Không tìm thấy review');
    }

    review.isVisible = !review.isVisible;
    await review.save();

    // Update product rating if applicable
    if (review.productId) await updateProductRating(review.productId);

    res.json({ success: true, isVisible: review.isVisible });
});

// Admin: delete review
export const deleteReview = asyncHandler(async (req, res) => {
    const review = await Review.findById(req.params.id);
    if (!review) {
        res.status(404);
        throw new Error('Không tìm thấy review');
    }

    const productId = review.productId;
    await review.remove();
    if (productId) await updateProductRating(productId);

    res.json({ success: true, message: 'Review đã được xóa' });
});
