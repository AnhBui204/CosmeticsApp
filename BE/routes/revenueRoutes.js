import express from 'express';
import { getSellerRevenueForChart, getTopSellingProducts } from '../controllers/revenueController.js';
const router = express.Router();

router.get('/seller-revenue', getSellerRevenueForChart);
router.get('/top-selling-products', getTopSellingProducts);
export default router;