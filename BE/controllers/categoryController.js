import asyncHandler from 'express-async-handler';
import { Category } from '../models/categoryModel.js';

// @desc    Get all categories
// @route   GET /api/categories
// @access  Public
export const getAllCategories = asyncHandler(async (req, res) => {
    const categories = await Category.find().select('_id name').lean();
    res.json(categories);
});

export default { getAllCategories };
