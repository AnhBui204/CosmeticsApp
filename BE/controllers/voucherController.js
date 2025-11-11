import asyncHandler from 'express-async-handler';
import { Voucher } from '../models/voucherModel.js';
import { Cart } from '../models/cartModel.js';

/**
 * Helpers
 */
const calculateDiscount = (voucher, subTotal) => {
    let discountAmount = 0;
    if (voucher.discountType === 'fixed_amount') {
        discountAmount = voucher.discountValue;
    } else if (voucher.discountType === 'percentage') {
        discountAmount = (subTotal * voucher.discountValue) / 100;
        if (voucher.maxDiscountAmount && discountAmount > voucher.maxDiscountAmount) {
            discountAmount = voucher.maxDiscountAmount;
        }
    }
    return discountAmount;
};

/**
 * @desc    Lấy danh sách voucher có thể sử dụng
 * @route   GET /api/vouchers/available
 * @access  Private
 */
export const getAvailableVouchers = asyncHandler(async (req, res) => {
    // Lấy các voucher còn hoạt động, còn hạn, còn lượt dùng
    const now = new Date();
    const vouchers = await Voucher.find({
        isActive: true,
        endDate: { $gte: now },
        usageLimit: { $gt: 0 }
    }).sort({ startDate: 1 }); // Sắp xếp theo ngày bắt đầu

    res.json(vouchers);
});

/**
 * @desc    Áp dụng thử voucher vào giỏ hàng
 * @route   POST /api/vouchers/apply
 * @access  Private
 */
export const applyVoucherToCart = asyncHandler(async (req, res) => {
    const { code } = req.body;
    const userId = req.user._id;

    if (!code) {
        res.status(400);
        throw new Error('Vui lòng nhập mã voucher');
    }

    // 1. Lấy voucher
    const now = new Date();
    const voucher = await Voucher.findOne({
        code: code.toUpperCase(),
        isActive: true,
        endDate: { $gte: now },
        usageLimit: { $gt: 0 }
    });

    if (!voucher) {
        res.status(404);
        throw new Error('Mã giảm giá không hợp lệ, đã hết hạn hoặc hết lượt');
    }

    // 2. Lấy giỏ hàng (chỉ cần tổng tiền)
    const cart = await Cart.findOne({ userId: userId });
    if (!cart || cart.items.length === 0) {
        res.status(400);
        throw new Error('Giỏ hàng rỗng, không thể áp dụng mã');
    }
    
    // (Lưu ý: totalAmount trong cart có thể cũ, nên tính lại subTotal
    // Tạm thời dùng totalAmount đã lưu)
    const subTotal = cart.totalAmount; 

    // 3. Kiểm tra điều kiện
    if (subTotal < voucher.minOrderAmount) {
         res.status(400);
         throw new Error(`Đơn hàng tối thiểu ${voucher.minOrderAmount.toLocaleString('vi-VN')}đ để áp dụng voucher`);
    }

    // 4. Tính toán
    const discountAmount = calculateDiscount(voucher, subTotal);
    const finalTotal = subTotal - discountAmount;

    res.json({
        success: true,
        code: voucher.code,
        discountAmount,
        subTotal,
        finalTotal: finalTotal < 0 ? 0 : finalTotal
    });
});

/**
 * Admin / Seller CRUD
 */
// Create voucher
export const createVoucher = asyncHandler(async (req, res) => {
    const data = req.body;

    if (!data.code) {
        res.status(400);
        throw new Error('Mã voucher (code) là bắt buộc');
    }

    // Normalize
    data.code = data.code.toUpperCase();

    const exists = await Voucher.findOne({ code: data.code });
    if (exists) {
        res.status(400);
        throw new Error('Mã voucher đã tồn tại');
    }

    const voucher = await Voucher.create({
        code: data.code,
        title: data.title || '',
        description: data.description || '',
        discountType: data.discountType,
        discountValue: data.discountValue,
        maxDiscountAmount: data.maxDiscountAmount,
        minOrderAmount: data.minOrderAmount || 0,
        usageLimit: data.usageLimit || 1,
        startDate: data.startDate ? new Date(data.startDate) : new Date(),
        endDate: data.endDate ? new Date(data.endDate) : new Date(),
        isActive: typeof data.isActive === 'boolean' ? data.isActive : true
    });

    res.status(201).json(voucher);
});

// Get all vouchers (with optional query: active only, pagination)
export const getVouchers = asyncHandler(async (req, res) => {
    const { page = 1, limit = 50, active } = req.query;
    const query = {};
    if (typeof active !== 'undefined') {
        query.isActive = active === 'true';
    }

    const skip = (Number(page) - 1) * Number(limit);
    const total = await Voucher.countDocuments(query);
    const vouchers = await Voucher.find(query).sort({ startDate: -1 }).skip(skip).limit(Number(limit));

    res.json({ total, page: Number(page), limit: Number(limit), data: vouchers });
});

// Get single voucher
export const getVoucherById = asyncHandler(async (req, res) => {
    const voucher = await Voucher.findById(req.params.id);
    if (!voucher) {
        res.status(404);
        throw new Error('Không tìm thấy voucher');
    }
    res.json(voucher);
});

// Update voucher
export const updateVoucher = asyncHandler(async (req, res) => {
    const voucher = await Voucher.findById(req.params.id);
    if (!voucher) {
        res.status(404);
        throw new Error('Không tìm thấy voucher để cập nhật');
    }

    const data = req.body;
    if (data.code) voucher.code = data.code.toUpperCase();
    if (typeof data.title !== 'undefined') voucher.title = data.title;
    if (typeof data.description !== 'undefined') voucher.description = data.description;
    if (typeof data.discountType !== 'undefined') voucher.discountType = data.discountType;
    if (typeof data.discountValue !== 'undefined') voucher.discountValue = data.discountValue;
    if (typeof data.maxDiscountAmount !== 'undefined') voucher.maxDiscountAmount = data.maxDiscountAmount;
    if (typeof data.minOrderAmount !== 'undefined') voucher.minOrderAmount = data.minOrderAmount;
    if (typeof data.usageLimit !== 'undefined') voucher.usageLimit = data.usageLimit;
    if (typeof data.startDate !== 'undefined') voucher.startDate = new Date(data.startDate);
    if (typeof data.endDate !== 'undefined') voucher.endDate = new Date(data.endDate);
    if (typeof data.isActive !== 'undefined') voucher.isActive = data.isActive;

    const updated = await voucher.save();
    res.json(updated);
});

// Delete voucher
export const deleteVoucher = asyncHandler(async (req, res) => {
    const voucher = await Voucher.findById(req.params.id);
    if (!voucher) {
        res.status(404);
        throw new Error('Không tìm thấy voucher để xóa');
    }

    await voucher.remove();
    res.json({ success: true, message: 'Voucher đã được xóa' });
});
