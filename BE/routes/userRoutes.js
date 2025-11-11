import express from 'express';
import {
    getMyProfile,
    updateMyProfile,
    updateFcmToken,
    getWishlist,
    addToWishlist,
    removeFromWishlist,
    changePassword
} from '../controllers/userController.js';
import { isAuthenticated } from '../middleware/authmiddleware.js';

const router = express.Router();

// Áp dụng middleware cho tất cả routes bên dưới
router.use(isAuthenticated);

// GET /api/users/me
router.get('/me', getMyProfile);

// PUT /api/users/me
router.put('/me', updateMyProfile);
router.put('/change-password', changePassword);
// POST /api/users/fcm-token
router.post('/fcm-token', updateFcmToken);

// Wishlist for current user
router.get('/wishlist', getWishlist);
router.post('/wishlist', addToWishlist);
router.delete('/wishlist/:productId', removeFromWishlist);

// Seller wishlist routes
router.get('/:userId/wishlist', getWishlist);
router.post('/:userId/wishlist', addToWishlist);
router.delete('/:userId/wishlist/:productId', removeFromWishlist);




export default router;
