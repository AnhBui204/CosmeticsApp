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
import { isAuthenticated, isAdmin, isSellerOrAdmin } from '../middleware/authmiddleware.js';

const router = express.Router();

// Các endpoint cần đăng nhập
router.use(isAuthenticated);

// GET /api/vouchers/available
router.get('/available', getAvailableVouchers);

// POST /api/vouchers/apply
router.post('/apply', applyVoucherToCart);

// Seller/Admin routes: allow sellers or admins to manage vouchers
router.post('/', isSellerOrAdmin, createVoucher);
router.get('/', isSellerOrAdmin, getVouchers);
router.get('/:id', isSellerOrAdmin, getVoucherById);
router.put('/:id', isSellerOrAdmin, updateVoucher);
router.delete('/:id', isSellerOrAdmin, deleteVoucher);

export default router;
