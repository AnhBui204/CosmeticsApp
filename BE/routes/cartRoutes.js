import express from 'express';
import {
    getCart,
    addItemToCart,
    removeItemFromCart,
    updateItemQuantity
} from '../controllers/cartController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

router.use(isAuthenticated);

// GET /api/cart/
router.get('/', getCart);

// POST /api/cart/items
router.post('/items', addItemToCart);

// PUT /api/cart/items/:itemId
router.put('/items/:itemId', updateItemQuantity);

// DELETE /api/cart/items/:itemId
router.delete('/items/:itemId', removeItemFromCart);

export default router;
