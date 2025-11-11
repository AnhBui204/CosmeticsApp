import express from 'express';
import { createOrder, handleWebhook,updateOrderStatus } from '../controllers/paymentController.js';

const router = express.Router();

router.post('/create-order', createOrder);
router.post('/webhook', handleWebhook);
router.post('/update-order',updateOrderStatus)

export default router;
