import express from 'express';
import { getAllCategories, getProductsByCategory } from '../controllers/categoryController.js';

const router = express.Router();

// GET /api/categories
router.get('/', getAllCategories);

// GET /api/categories/:id/products
router.get('/:id/products', getProductsByCategory);

export default router;
