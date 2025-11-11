import express from 'express';
import {
    getCart,
    addItemToCart,
    removeItemFromCart,
    updateItemQuantity
    , addItemToCartForUser, removeItemFromCartForUser,
    getCartByUser
} from '../controllers/cartController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

router.use(isAuthenticated);

// GET /api/cart/
router.get('/', getCart);



// POST /api/cart/items
router.post('/items', addItemToCart);


// Seller: GET /api/cart/:userId
router.get('/:userId', getCartByUser);

// Seller: POST /api/cart/:userId/items
router.post('/:userId/items', addItemToCartForUser);

// Seller: DELETE /api/cart/:userId/items/:itemId
router.delete('/:userId/items/:itemId', removeItemFromCartForUser);

// PUT /api/cart/items/:itemId
router.put('/items/:itemId', updateItemQuantity);

// DELETE /api/cart/items/:itemId
router.delete('/items/:itemId', removeItemFromCart);


export default router;
