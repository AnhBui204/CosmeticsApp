import express from 'express';
import { registerUser, loginUser,googleLogin,forgotPassword,verifyOTP,resetPassword} from '../controllers/authController.js';

const router = express.Router();
router.post('/google-login', googleLogin);
// Đường dẫn sẽ là: POST /api/auth/register
router.post('/register', registerUser);   

// Đường dẫn sẽ là: POST /api/auth/login
router.post('/login', loginUser);
router.post('/forgot-password', forgotPassword);

router.post('/verify-otp', verifyOTP);        
router.post('/reset-password', resetPassword);
export default router;