import express from 'express';
import {
    checkout,
    getMyOrders,
    getOrderDetails,getOrderByCode, getSellerOrders
} from '../controllers/orderController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

router.use(isAuthenticated);

// POST /api/orders/checkout
router.post('/checkout', checkout);

// GET /api/orders/
router.get('/', getMyOrders);

// GET /api/orders/:id
router.get('/by-code/:orderCode', getOrderByCode);
router.get('/:id', getOrderDetails);
router.get('/seller', getSellerOrders);

export default router;
