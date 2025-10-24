import express from 'express';
import {
    getAllProducts,
    getProductById,
    getRelatedProducts,
} from '../controllers/productController.js';
import { getReviewsForProduct } from '../controllers/reviewController.js';

const router = express.Router();

// GET /api/products/
router.get('/', getAllProducts);

// GET /api/products/:id
router.get('/:id', getProductById);

// GET /api/products/:id/related
router.get('/:id/related', getRelatedProducts);

// GET /api/products/:id/reviews
router.get('/:id/reviews', getReviewsForProduct);

export default router;
