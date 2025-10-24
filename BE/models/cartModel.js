import mongoose from 'mongoose';
const { Schema } = mongoose;

const cartSchema = new Schema({
    userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, unique: true },
    items: [{
        productId: { type: Schema.Types.ObjectId, ref: 'Product' },
        quantity: { type: Number, required: true, min: 1 },
        priceAtAdd: { type: Number, required: true } // Lưu giá tại thời điểm thêm
    }],
    updatedAt: { type: Date, default: Date.now }
});
export const Cart = mongoose.model('Cart', cartSchema);