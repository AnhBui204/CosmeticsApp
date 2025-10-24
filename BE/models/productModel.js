import mongoose from 'mongoose';
const { Schema } = mongoose;

const productSchema = new Schema({
    name: { type: String, required: true, index: true },
    sku: { type: String, required: true, unique: true },
    description: { type: String, required: true },
    price: { type: Number, required: true },
    salePrice: { type: Number },
    stockQuantity: { type: Number, required: true, default: 0 },
    images: [{ type: String }],
    category: { type: Schema.Types.ObjectId, ref: 'Category', required: true },
    brand: { type: String, index: true },
    attributes: {
        volume: String,
        skinType: String,
        origin: String
    },
    // Tính năng: Reviews & Ratings
    ratings: {
        average: { type: Number, default: 0 },
        count: { type: Number, default: 0 }
    }
});
export const Product = mongoose.model('Product', productSchema);