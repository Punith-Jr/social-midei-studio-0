import ffmpeg from 'fluent-ffmpeg';
import fs from 'fs';
import path from 'path';

// ಈ ಫಂಕ್ಷನ್ ಚಿತ್ರಗಳು ಮತ್ತು ವಾಯ್ಸ್ಓವರ್ ಬಳಸಿ ವೀಡಿಯೊ ಸಿದ್ಧಪಡಿಸುತ್ತದೆ
export async function renderVideo(imagePaths, audioPath, outputPath) {
  return new Promise((resolve, reject) => {
    if (!imagePaths || imagePaths.length === 0) {
      return reject(new Error("No input images provided for rendering"));
    }

    let command = ffmpeg();

    // ಚಿತ್ರಗಳನ್ನು ಇನ್ಪುಟ್ ಆಗಿ ಸೇರಿಸುವುದು
    imagePaths.forEach((img) => {
      command = command.input(img).loop(3); // ಪ್ರತಿ ಚಿತ್ರವು 3 ಸೆಕೆಂಡುಗಳ ಕಾಲ ಇರುತ್ತದೆ
    });

    // ಧ್ವನಿ (Audio) ಫೈಲ್ ಅನ್ನು ಇನ್ಪುಟ್ ಮಾಡುವುದು
    if (audioPath && fs.existsSync(audioPath)) {
      command = command.input(audioPath);
    }

    command
      .complexFilter([
        // ಎಲ್ಲಾ ಚಿತ್ರಗಳನ್ನು ಒಟ್ಟುಗೂಡಿಸಿ ವೀಡಿಯೊ ಸ್ಟ್ರೀಮ್ ಮಾಡುವುದು
        `concat=n=${imagePaths.length}:v=1:a=0[v]`
      ])
      .outputOptions([
        '-map [v]',
        '-map ' + (imagePaths.length), // ಆಡಿಯೋ ಮ್ಯಾಪಿಂಗ್
        '-c:v libx264',
        '-pix_fmt yuv420p',
        '-c:a aac',
        '-shortest' // ಆಡಿಯೋ ಮುಗಿದ ತಕ್ಷಣ ವೀಡಿಯೊ ಮುಕ್ತಾಯಗೊಳ್ಳುವುದು
      ])
      .save(outputPath)
      .on('end', () => {
        console.log('✅ ವೀಡಿಯೊ ರೆಂಡರಿಂಗ್ ಯಶಸ್ವಿಯಾಗಿ ಮುಗಿದಿದೆ:', outputPath);
        resolve(outputPath);
      })
      .on('error', (err) => {
        console.error('❌ ವೀಡಿಯೊ ರೆಂಡರಿಂಗ್ ದೋಷ:', err.message);
        reject(err);
      });
  });
}
