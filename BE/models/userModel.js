import mongoose from 'mongoose';
const { Schema } = mongoose;

// --- User Model ---
const userSchema = new Schema({
    fullName: { type: String, required: true },
    email: { type: String, required: true, unique: true, index: true },
    password: { type: String, required: true }, // Sẽ được hash
    phoneNumber: { type: String },
    addresses: [{
        street: String,
        city: String,
        district: String,
        ward: String,
        isDefault: { type: Boolean, default: false }
    }],
    role: { type: String, enum: ['customer', 'admin','seller'], default: 'customer' },
    wishlist: [{ type: Schema.Types.ObjectId, ref: 'Product' }], // Tính năng: Wishlist
    fcmToken: { type: String }, // Tính năng: Notification
     loginProvider: { type: String, enum: ['email', 'google'], default: 'email' },
    createdAt: { type: Date, default: Date.now }
});
export const User = mongoose.model('User', userSchema);