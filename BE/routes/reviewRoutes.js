import express from 'express';
import { createReview } from '../controllers/reviewController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

// POST /api/reviews/
router.post('/', isAuthenticated, createReview);

export default router;
