import { Order } from './../models/orderModel.js';
import mongoose from 'mongoose';

export const getSellerRevenueForChart = async (req, res) => {
    try {
        const { sellerId } = req.query;
        if (!sellerId) {
            return res.status(400).json({ message: 'Thiếu sellerId' });
        }

        // --- PHẦN LOGIC THÊM DATA MẪU (PADDING) ---

        // Use an explicit timezone so date boundaries match between JS and Mongo aggregation.
        // Set TIMEZONE to an IANA name (e.g. 'Asia/Ho_Chi_Minh') via env or default to VN timezone.
        const TIMEZONE = process.env.TIMEZONE || 'Asia/Ho_Chi_Minh';

        // 1. Tạo 7 mốc ngày (từ 6 ngày trước đến hôm nay) in the specified timezone
        const allDates = [];
        const today = new Date();
        // calculate the start date (6 days before today) using local timezone formatting
        const startDate = new Date(today);
        startDate.setDate(startDate.getDate() - 6);

        const fmt = new Intl.DateTimeFormat('en-CA', { timeZone: TIMEZONE }); // en-CA -> YYYY-MM-DD
        for (let i = 0; i < 7; i++) {
            const d = new Date(startDate);
            d.setDate(startDate.getDate() + i);
            // format in YYYY-MM-DD according to TIMEZONE
            const dateStr = fmt.format(d);
            allDates.push(dateStr);
        }

        // compute start and end date strings for the range (YYYY-MM-DD) in the timezone
        const startDateStr = allDates[0];
        const endDateStr = allDates[allDates.length - 1];
        console.log(`[revenueController] TIMEZONE=${TIMEZONE}, start=${startDateStr}, end=${endDateStr}`);

        // DEBUG: dump the latest orders for this seller so we can verify what's stored in DB
        try {
            const recentOrders = await Order.find({ sellerId: new mongoose.Types.ObjectId(sellerId) })
                .sort({ createdAt: -1 })
                .limit(10)
                .lean();
            console.log('[revenueController] recentOrders sample:', recentOrders.map(o => ({
                _id: o._id,
                orderCode: o.orderCode,
                status: o.status,
                createdAt: o.createdAt,
                totalAmount: o.totalAmount,
                itemsCount: (o.items && o.items.length) || 0
            })));
        } catch (dbgErr) {
            console.error('[revenueController] failed to read recentOrders for debug', dbgErr);
        }

        // 2. Lấy dữ liệu thật từ CSDL (như code của bạn)
        const revenueData = await Order.aggregate([
            // Match delivered orders whose createdAt date (in TIMEZONE) falls between startDateStr and endDateStr
            {
                $match: {
                    sellerId: new mongoose.Types.ObjectId(sellerId),
                    status: 'delivered',
                    $expr: {
                        $and: [
                            { $gte: [ { $dateToString: { format: "%Y-%m-%d", date: "$createdAt", timezone: TIMEZONE } }, startDateStr ] },
                            { $lte: [ { $dateToString: { format: "%Y-%m-%d", date: "$createdAt", timezone: TIMEZONE } }, endDateStr ] }
                        ]
                    }
                }
            },
            {
                $group: {
                    _id: { $dateToString: { format: "%Y-%m-%d", date: "$createdAt", timezone: TIMEZONE } },
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
                    status: {$in: ['processing', 'delivered']}
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
                    image: { $first: '$items.image' }, // Lấy ảnh (nếu order lưu ảnh sản phẩm khi tạo order)
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
                // 6. Lookup product to obtain images (fallback when orders didn't capture image)
                $lookup: {
                    from: 'products',
                    localField: '_id',
                    foreignField: '_id',
                    as: 'productInfo'
                }
            },
            {
                $unwind: { path: '$productInfo', preserveNullAndEmptyArrays: true }
            },
            {
                // 7. Định dạng lại output, prefer productInfo.images[0] over the image stored in order.items
                $project: {
                    _id: 0,
                    productId: '$_id',
                    name: '$name',
                    image: { $ifNull: [ { $arrayElemAt: ['$productInfo.images', 0] }, '$image' ] },
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