import mongoose from 'mongoose';

const animationStyleSchema = new mongoose.Schema({
  key: { type: String, required: true, unique: true, index: true },
  name: { type: String, required: true },
  description: { type: String, default: '' },
  aspectRatios: { type: [String], default: [] },
  defaultAspectRatio: { type: String, required: true },
  prompt: { type: String, required: true },
  enabled: { type: Boolean, default: true }
}, { timestamps: true });

export default mongoose.model('AnimationStyle', animationStyleSchema);