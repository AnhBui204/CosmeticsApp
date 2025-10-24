import mongoose from 'mongoose';

const categorySchema = new mongoose.Schema(
    {
        name: {
            type: String,
            required: [true, 'Tên danh mục là bắt buộc'],
            trim: true,
            unique: true,
        },
        description: {
            type: String,
            trim: true,
        },
        iconUrl: {
            type: String,
            default: 'default-icon.png',
        },
        // Đây là phần quan trọng để làm danh mục đa cấp (Cha-Con)
        parentCategory: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Category', // Tự tham chiếu đến chính nó
            default: null,
        }
    },
    {
        timestamps: true, // Tự động thêm createdAt và updatedAt
    }
);

export const Category = mongoose.model('Category', categorySchema);
