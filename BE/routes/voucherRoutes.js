import express from 'express';
import {
    getAvailableVouchers,
    applyVoucherToCart,
    // CRUD
    createVoucher,
    getVouchers,
    getVoucherById,
    updateVoucher,
    deleteVoucher
} from '../controllers/voucherController.js';
import { isAuthenticated, isAdmin } from '../middleware/authmiddleware.js';

const router = express.Router();

// Các endpoint cần đăng nhập
router.use(isAuthenticated);

// GET /api/vouchers/available
router.get('/available', getAvailableVouchers);

// POST /api/vouchers/apply
router.post('/apply', applyVoucherToCart);

// Admin / Seller routes (require admin role)
router.post('/', isAdmin, createVoucher);
router.get('/', isAdmin, getVouchers);
router.get('/:id', isAdmin, getVoucherById);
router.put('/:id', isAdmin, updateVoucher);
router.delete('/:id', isAdmin, deleteVoucher);

export default router;
