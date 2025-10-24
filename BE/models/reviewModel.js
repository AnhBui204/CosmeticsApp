import mongoose from 'mongoose';
const { Schema } = mongoose;

const reviewSchema = new Schema({
    productId: { type: Schema.Types.ObjectId, ref: 'Product', index: true },
    userId: { type: Schema.Types.ObjectId, ref: 'User', index: true },
    orderId: { type: Schema.Types.ObjectId, ref: 'Order' },
    rating: { type: Number, min: 1, max: 5, required: true },
    comment: { type: String },
    images: [{ type: String }],
    createdAt: { type: Date, default: Date.now }
});
export const Review = mongoose.model('Review', reviewSchema);