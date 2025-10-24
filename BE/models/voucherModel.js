import mongoose from 'mongoose';
const { Schema } = mongoose;

const voucherSchema = new Schema({
    code: { type: String, required: true, unique: true, uppercase: true },
    description: { type: String },
    discountType: { type: String, enum: ['percentage', 'fixed_amount'], required: true },
    discountValue: { type: Number, required: true },
    maxDiscountAmount: { type: Number },
    minOrderAmount: { type: Number, default: 0 },
    usageLimit: { type: Number, required: true },
    usedCount: { type: Number, default: 0 },
    validFrom: { type: Date, required: true },
    validTo: { type: Date, required: true },
    isActive: { type: Boolean, default: true }
});
export const Voucher = mongoose.model('Voucher', voucherSchema);