import mongoose from 'mongoose';
const { Schema } = mongoose;

const voucherSchema = new Schema({
    code: { type: String, required: true, unique: true, uppercase: true, index: true },
    title: { type: String },
    description: { type: String },
    // Discount configuration
    discountType: { type: String, enum: ['percentage', 'fixed_amount'], required: true },
    discountValue: { type: Number, required: true },
    maxDiscountAmount: { type: Number },
    minOrderAmount: { type: Number, default: 0 },

    // Usage tracking
    usageLimit: { type: Number, required: true },
    usedCount: { type: Number, default: 0 },

    // Validity window (FE uses `endDate` string; store as Date here)
    startDate: { type: Date, required: true },
    endDate: { type: Date, required: true },

    // Status flags
    isActive: { type: Boolean, default: true },
    // Human-readable status for FE: 'active' | 'inactive' | 'expired'
    status: { type: String, enum: ['active', 'inactive', 'expired'], default: 'active' }
}, { timestamps: true });

// Keep `status` consistent with `isActive` and `endDate`
voucherSchema.pre('save', function (next) {
    if (!this.isActive) {
        this.status = 'inactive';
        return next();
    }

    if (this.endDate && this.endDate < new Date()) {
        this.status = 'expired';
    } else {
        this.status = 'active';
    }

    return next();
});

// Optional virtual for FE compatibility: expose endDate as ISO string if needed
voucherSchema.methods.toDTO = function () {
    const obj = this.toObject({ getters: true });
    obj.endDate = obj.endDate ? obj.endDate.toISOString() : null;
    obj.startDate = obj.startDate ? obj.startDate.toISOString() : null;
    return obj;
};

export const Voucher = mongoose.model('Voucher', voucherSchema);