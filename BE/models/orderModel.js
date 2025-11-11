import mongoose from 'mongoose';
const { Schema } = mongoose;

const orderSchema = new Schema({
    orderCode: { type: String, required: true, unique: true },
    userId: { type: Schema.Types.ObjectId, ref: 'User' },
    sellerId: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    items: [{
        productId: { type: Schema.Types.ObjectId, ref: 'Product' },
        name: String,
        price: Number,
        quantity: Number,
     sellerId: { type: Schema.Types.ObjectId, ref: 'User' }
    }],
    totalAmount: { type: Number, required: true },
    shippingAddress: { type: Object, required: true },
    paymentMethod: { type: String, required: true },
    status: { type: String, enum: ['pending', 'processing', 'shipped', 'delivered', 'cancelled'], default: 'pending' },
    voucherCode: { type: String },
    createdAt: { type: Date, default: Date.now }
});
export const Order = mongoose.model('Order', orderSchema);