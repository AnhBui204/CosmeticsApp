import asyncHandler from 'express-async-handler';
import mongoose from 'mongoose';
import { Category } from '../models/categoryModel.js';
import { Product } from '../models/productModel.js';

// @desc    Get all categories
// @route   GET /api/categories
// @access  Public
export const getAllCategories = asyncHandler(async (req, res) => {
    const categories = await Category.find().select('_id name description').lean();
    res.json(categories);
});

/**
 * @desc    Get products by category id (includes products in descendant categories)
 * @route   GET /api/categories/:id/products
 * @access  Public
 */
export const getProductsByCategory = asyncHandler(async (req, res) => {
    const categoryId = req.params.id;

    if (!mongoose.Types.ObjectId.isValid(categoryId)) {
        res.status(400);
        throw new Error('Category id không hợp lệ');
    }

    const root = await Category.findById(categoryId);
    if (!root) {
        res.status(404);
        throw new Error('Không tìm thấy danh mục');
    }

    const ids = [root._id];
    const queue = [root._id];
    while (queue.length) {
        const parent = queue.shift();
        const children = await Category.find({ parentCategory: parent }).select('_id').lean();
        for (const c of children) {
            ids.push(c._id);
            queue.push(c._id);
        }
    }

    const pageSize = parseInt(req.query.limit) || 20;
    const page = parseInt(req.query.page) || 1;
    let sortOptions = {};
    const sort = req.query.sort;
    if (sort === 'price_asc') sortOptions.price = 1;
    else if (sort === 'price_desc') sortOptions.price = -1;
    else sortOptions.createdAt = -1;

    const count = await Product.countDocuments({ category: { $in: ids } });
    const products = await Product.find({ category: { $in: ids } })
        .sort(sortOptions)
        .limit(pageSize)
        .skip(pageSize * (page - 1))
        .populate('category', 'name');

    res.json({
        products,
        page,
        totalPages: Math.ceil(count / pageSize),
        totalProducts: count
    });
});
