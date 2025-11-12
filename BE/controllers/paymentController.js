import mongoose from "mongoose";
import crypto from "crypto";
import axios from "axios";
import { Order } from "../models/orderModel.js";
import { Cart } from "../models/cartModel.js";

const PAYOS_SANDBOX_API = "https://api-merchant.payos.vn/v2/payment-requests";
const PAYOS_PROD_API = "https://api-merchant.payos.vn/v2/payment-requests";

export const createOrder = async (req, res) => {
  try {
    const { userId, shippingAddress, paymentMethod, voucherCode } = req.body;

    // check for missing fields
    const missingFields = [];
    if (!userId) missingFields.push("userId");
    if (!shippingAddress) missingFields.push("shippingAddress");
    if (!paymentMethod) missingFields.push("paymentMethod");

    if (missingFields.length > 0) {
      console.warn("Missing required fields:", missingFields);
      return res.status(400).json({
        message: "Missing required fields",
        missingFields,
      });
    }

    // Lấy cart của user
    const cart = await Cart.findOne({ userId }).populate("items.productId");
    if (!cart || cart.items.length === 0) {
      return res.status(400).json({ message: "Cart is empty" });
    }

    // if that cart exists, get first sellerId from cart items
    const firstSellerId = cart.items[0]?.productId?.sellerId;
    if (!firstSellerId) {
      return res.status(400).json({ message: "Cart items missing sellerId" });
    }

    // Tính tổng tiền
    const totalAmount = cart.items.reduce(
      (sum, item) => sum + item.priceAtAdd * item.quantity,
      0
    );

    // Tạo orderCode
    const orderCode = Math.floor(Date.now() / 1000);

    // Tạo Order record với status Pending
    const newOrder = await Order.create({
      orderCode,
      userId,
      sellerId: firstSellerId,
      items: cart.items.map((i) => ({
        productId: i.productId._id,
        name: i.productId.name,
        price: i.priceAtAdd,
        quantity: i.quantity,
        sellerId: i.productId.sellerId
      })),
      totalAmount,
      shippingAddress,
      paymentMethod,
      voucherCode,
      status: "pending",
    });
    const payosAmount = 2000;
    // Tạo payload cho PayOS
    const useSandbox = process.env.PAYOS_USE_SANDBOX === "true";
    const PAYOS_API = useSandbox ? PAYOS_SANDBOX_API : PAYOS_PROD_API;

    const returnUrl = `${process.env.PAYOS_RETURN_URL}&orderCode=${orderCode}`;
    const cancelUrl = `${process.env.PAYOS_CANCEL_URL}&orderCode=${orderCode}`;

    const rawSignature = `amount=${payosAmount}&cancelUrl=${cancelUrl}&description=Order ${orderCode}&orderCode=${orderCode}&returnUrl=${returnUrl}`;
    const signature = crypto
      .createHmac("sha256", process.env.PAYOS_CHECKSUM_KEY)
      .update(rawSignature)
      .digest("hex");

    const payload = {
      orderCode,
      amount: payosAmount,
      description: `Order ${orderCode}`,
      cancelUrl,
      returnUrl,
      signature,
    };

    const response = await axios.post(PAYOS_API, payload, {
      headers: {
        "x-client-id": process.env.PAYOS_CLIENT_ID,
        "x-api-key": process.env.PAYOS_API_KEY,
      },
    });

    const checkoutUrl = response.data?.data?.checkoutUrl;
    if (!checkoutUrl) {
      console.log("PayOS raw response:", response.data);
      throw new Error("PayOS response invalid: no checkoutUrl returned");
    }

    res.json({ url: checkoutUrl });
  } catch (error) {
    console.error("❌ PayOS Error:", error.response?.data || error.message);
    return res.status(500).json({
      message: "Failed to create payment",
      details: error.response?.data || error.message,
    });
  }
};

/** Webhook PayOS */

export const handleWebhook = async (req, res) => {
  try {
    const payload = req.body;
    const data = payload.data || {};
    const orderCode = data.orderCode;
    const status = payload.success ? "paid" : "failed"; // lowercase cho nhất quán

    if (orderCode) {
      // Map status vào DB
      let newStatus;
      switch (status) {
        case "paid":
          newStatus = "processing";
          break;
        case "failed":
        case "cancelled":
          newStatus = "cancelled";
          break;
        default:
          newStatus = "pending";
      }

      // Update trực tiếp trong DB
      await Order.findOneAndUpdate(
        { orderCode },
        { status: newStatus },
        { new: true }
      );

      console.log(`Order ${orderCode} updated to ${newStatus}`);
    }

    res.status(200).send('OK');
  } catch (error) {
    console.error('Webhook error:', error);
    res.status(500).send('Server error');
  }
};

export const updateOrderStatus = async (req, res) => {
  try {
    const { orderCode, status } = req.body;
    console.log("updateOrderStatus body:", req.body);
    if (!orderCode || !status) {
      return res.status(400).json({ message: "Missing orderCode or status" });
    }

    // Map status FE -> DB status
    let newStatus;
    switch (status.toLowerCase()) {
      case "paid":
        newStatus = "processing";
        break;
      case "cancelled":
        newStatus = "cancelled";
        break;
      case "failed":
        newStatus = "cancelled"
      default:
        return res.status(400).json({ message: "Invalid status value" });
    }

    const updatedOrder = await Order.findOneAndUpdate(
      { orderCode },
      { status: newStatus },
      { new: true }
    );

    if (!updatedOrder) {
      return res.status(404).json({ message: "Order not found" });
    }

    return res.json({
      message: `Order ${orderCode} updated to ${newStatus}`,
      order: updatedOrder,
    });
  } catch (error) {
    console.error("❌ updateOrderStatusFromFE error:", error);
    return res.status(500).json({ message: "Server error", details: error.message });
  }
};