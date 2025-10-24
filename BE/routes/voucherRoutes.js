import express from 'express';
import {
    getAvailableVouchers,
    applyVoucherToCart
} from '../controllers/voucherController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

router.use(isAuthenticated);

// GET /api/vouchers/available
router.get('/available', getAvailableVouchers);

// POST /api/vouchers/apply
router.post('/apply', applyVoucherToCart);

export default router;
