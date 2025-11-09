import mongoose from 'mongoose';

const otpSchema = mongoose.Schema(
  {
    email: { type: String, required: true },
    code: { type: String, required: true },
    expiresAt: { type: Date, required: true },
     resetToken: { type: String },              
    resetTokenExpiresAt: { type: Date }     
  },
  
  { timestamps: true }
);

export const OTP = mongoose.model('OTP', otpSchema);
