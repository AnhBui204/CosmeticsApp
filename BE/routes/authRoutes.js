import express from 'express';
import { registerUser, loginUser } from '../controllers/authController.js';

const router = express.Router();

// Đường dẫn sẽ là: POST /api/auth/register
router.post('/register', registerUser);

// Đường dẫn sẽ là: POST /api/auth/login
router.post('/login', loginUser);

export default router;