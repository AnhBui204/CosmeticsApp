import express from 'express';
import { getReviewsForProduct, createReview } from '../controllers/reviewController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

// GET /api/reviews/
router.get('/', isAuthenticated, getReviewsForProduct);
// POST /api/reviews/
router.post('/', isAuthenticated, createReview);

export default router;
