import mongoose from 'mongoose';
const { Schema } = mongoose;

const reviewSchema = new Schema({
    // Reference to product (optional) and snapshot SKU for FE
    productId: { type: Schema.Types.ObjectId, ref: 'Product', index: true },
    productSku: { type: String, index: true },

    // Reference to user and snapshot of customer email
    userId: { type: Schema.Types.ObjectId, ref: 'User', index: true },
    customerEmail: { type: String, index: true },

    // Front-end expects orderId as string
    orderId: { type: String, index: true },

    // Rating as float allowed (0-5), required
    rating: { type: Number, min: 0, max: 5, required: true },
    comment: { type: String },
    images: [{ type: String }],

    // Visibility control so admin/customer support can hide/show reviews
    isVisible: { type: Boolean, default: true }
}, { timestamps: true });

export const Review = mongoose.model('Review', reviewSchema);