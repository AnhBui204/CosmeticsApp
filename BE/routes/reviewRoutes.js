import express from 'express';
import {
	createReview,
	getReviewsForProduct,
	listReviews,
	toggleReviewVisibility,
	deleteReview
} from '../controllers/reviewController.js';
import { isAuthenticated, isAdmin } from '../middleware/authmiddleware.js';

const router = express.Router();

// POST /api/reviews/ (customer)
router.post('/', isAuthenticated, createReview);

// Public: product reviews endpoint is mounted under product routes in many apps,
// but we also expose it here for convenience: GET /api/reviews/product/:id
router.get('/product/:id', getReviewsForProduct);

// Admin endpoints
router.get('/', isAuthenticated, isAdmin, listReviews);
router.patch('/:id/visibility', isAuthenticated, isAdmin, toggleReviewVisibility);
router.delete('/:id', isAuthenticated, isAdmin, deleteReview);

export default router;
