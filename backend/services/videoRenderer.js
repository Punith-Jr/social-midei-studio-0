import ffmpeg from 'fluent-ffmpeg';
import fs from 'fs';
import path from 'path';

// ಈ ಫಂಕ್ಷನ್ ಚಿತ್ರಗಳು ಮತ್ತು ವಾಯ್ಸ್ಓವರ್ ಬಳಸಿ ವೀಡಿಯೊ ಸಿದ್ಧಪಡಿಸುತ್ತದೆ
export async function renderVideo(imagePaths, audioPath, outputPath) {
  return new Promise((resolve, reject) => {
    if (!imagePaths || imagePaths.length === 0) {
      return reject(new Error("No input images provided for rendering"));
    }

    const missingImage = imagePaths.find((imagePath) => !fs.existsSync(imagePath));
    if (missingImage) {
      return reject(new Error(`Input image does not exist: ${missingImage}`));
    }

    const hasAudio = Boolean(audioPath && fs.existsSync(audioPath));
    const command = ffmpeg();

    imagePaths.forEach((imagePath) => {
      command
        .input(imagePath)
        .inputOptions(['-loop 1', '-t 3']);
    });

    if (hasAudio) {
      command.input(audioPath);
    }

    const sceneStreams = imagePaths.map((_, index) => `[${index}:v]`).join('');
    const filters = [`${sceneStreams}concat=n=${imagePaths.length}:v=1:a=0[v]`];
    const outputOptions = [
      '-map [v]',
      '-c:v libx264',
      '-pix_fmt yuv420p',
      '-r 30',
      '-movflags +faststart'
    ];

    if (hasAudio) {
      outputOptions.push(
        `-map ${imagePaths.length}:a`,
        '-c:a aac',
        '-shortest'
      );
    }

    const outputDirectory = path.dirname(outputPath);
    fs.mkdirSync(outputDirectory, { recursive: true });

    command
      .complexFilter(filters)
      .outputOptions(outputOptions)
      .save(outputPath)
      .on('end', () => {
        if (!fs.existsSync(outputPath) || fs.statSync(outputPath).size === 0) {
          reject(new Error(`FFmpeg completed without creating output: ${outputPath}`));
          return;
        }
        console.log('✅ ವೀಡಿಯೊ ರೆಂಡರಿಂಗ್ ಯಶಸ್ವಿಯಾಗಿ ಮುಗಿದಿದೆ:', outputPath);
        resolve(outputPath);
      })
      .on('error', (err) => {
        console.error('❌ ವೀಡಿಯೊ ರೆಂಡರಿಂಗ್ ದೋಷ:', err.message);
        reject(err);
      });
  });
}
