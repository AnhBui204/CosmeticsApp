import express from 'express';
import {
    checkout,
    getMyOrders,
    getOrderDetails
} from '../controllers/orderController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

router.use(isAuthenticated);

// POST /api/orders/checkout
router.post('/checkout', checkout);

// GET /api/orders/
router.get('/', getMyOrders);

// GET /api/orders/:id
router.get('/:id', getOrderDetails);

export default router;
