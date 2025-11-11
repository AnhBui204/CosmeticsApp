import express from 'express';
import {
    getAllProducts,
    getProductById,
    getRelatedProducts,
    createProduct,
    updateProduct,
    deleteProduct
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

// POST /api/products/
router.post('/', createProduct);

// PUT /api/products/:id
router.put('/:id', updateProduct);

// DELETE /api/products/:id
router.delete('/:id', deleteProduct);

export default router;
