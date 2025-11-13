import { Order } from './../models/orderModel.js';
import mongoose from 'mongoose';

export const getSellerRevenueForChart = async (req, res) => {
    try {
        const { sellerId } = req.query;
        if (!sellerId) {
            return res.status(400).json({ message: 'Thiếu sellerId' });
        }

        // --- PHẦN LOGIC THÊM DATA MẪU (PADDING) ---

        // 1. Tạo 7 mốc ngày (từ 6 ngày trước đến hôm nay)
        const allDates = [];
        const sevenDaysAgo = new Date();
        sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 6); // Bắt đầu từ 6 ngày trước
        sevenDaysAgo.setHours(0, 0, 0, 0);

        for (let i = 0; i < 7; i++) {
            const date = new Date(sevenDaysAgo);
            date.setDate(date.getDate() + i);
            // Thêm vào mảng với định dạng YYYY-MM-DD
            allDates.push(date.toISOString().split('T')[0]);
        }
        // allDates giờ là: ["2025-11-06", "2025-11-07", ..., "2025-11-12"]

        // 2. Lấy dữ liệu thật từ CSDL (như code của bạn)
        const revenueData = await Order.aggregate([
            {
                $match: {
                    sellerId: new mongoose.Types.ObjectId(sellerId),
                    status: 'delivered',
                    createdAt: { $gte: sevenDaysAgo } // Lấy từ mốc 7 ngày
                }
            },
            {
                $group: {
                    _id: { $dateToString: { format: "%Y-%m-%d", date: "$createdAt" } },
                    dailyRevenue: { $sum: "$totalAmount" }
                }
            },
            {
                $project: {
                    _id: 0,
                    date: "$_id",
                    revenue: "$dailyRevenue"
                }
            }
        ]);
        // revenueData có thể là: [{ date: "2025-11-08", revenue: 150 }]

        // 3. Tạo một Map để tra cứu doanh thu đã có
        const revenueMap = new Map();
        for (const item of revenueData) {
            revenueMap.set(item.date, item.revenue);
        }

        // 4. Lấp đầy mảng 7 ngày
        const fullRevenueData = allDates.map(dateStr => {
            // Lấy doanh thu từ Map, nếu không có thì gán là 0
            const revenue = revenueMap.get(dateStr) || 0;
            return {
                date: dateStr,
                revenue: revenue
            };
        });

        // --- KẾT THÚC PHẦN LOGIC PADDING ---


        // SỬA LỖI: Dùng res.json() thay vì return
        res.status(200).json(fullRevenueData);

    } catch (error) {
        console.error(error);
        // SỬA LỖI: Trả về lỗi JSON
        res.status(500).json({ message: 'Error getting seller chart data', error: error.message });
    }
};

export const getTopSellingProducts = async (req, res) => {
    try {
        const { sellerId } = req.query;
        if (!sellerId) {
            return res.status(400).json({ message: 'Thiếu sellerId' });
        }

        const topProducts = await Order.aggregate([
            {
                // 1. Lọc đơn hàng của seller và đã giao thành công
                $match: {
                    sellerId: new mongoose.Types.ObjectId(sellerId),
                    status: { $in: ['delivered', 'processing'] }
                }
            },
            {
                // 2. Bung (unwind) mảng items ra
                $unwind: '$items'
            },
            {
                // 3. Nhóm theo productId và đếm tổng số lượng
                $group: {
                    _id: '$items.productId',
                    name: { $first: '$items.name' }, // Lấy tên sản phẩm
                    totalQuantity: { $sum: '$items.quantity' }, // Cộng dồn số lượng
                    totalRevenue: { $sum: { $multiply: ['$items.quantity', '$items.price'] } } // Tính tổng doanh thu
                }
            },
            {
                // 4. Sắp xếp giảm dần
                $sort: { totalQuantity: -1 }
            },
            {
                // 5. Giới hạn 5 sản phẩm
                $limit: 5
            },
            {
                // 6. Định dạng lại output
                $project: {
                    _id: 0,
                    productId: '$_id',
                    name: '$name',
                    totalQuantity: '$totalQuantity',
                    totalRevenue: '$totalRevenue'

                }
            }
        ]);

        res.status(200).json(topProducts);

    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Lỗi khi lấy top sản phẩm', error: error.message });
    }
};