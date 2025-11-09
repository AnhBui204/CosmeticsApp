import express from 'express';
import {
    getMyProfile,
    updateMyProfile,
    updateFcmToken,
    getWishlist,
    toggleWishlist,changePassword
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

// GET /api/users/wishlist
router.get('/wishlist', getWishlist);

// POST /api/users/wishlist
router.post('/wishlist', toggleWishlist);

export default router;
