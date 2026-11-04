import fetch from 'node-fetch';
import fs from 'fs';
import path from 'path';

export async function fetchImagesForPrompt(query, count = 3) {
  const apiKey = process.env.PEXELS_API_KEY;
  const downloadDir = path.join(process.cwd(), 'downloads');
  if (!fs.existsSync(downloadDir)) {
    fs.mkdirSync(downloadDir, { recursive: true });
  }

  const imagePaths = [];

  // If Pexels API key is present, fetch high-quality photos
  if (apiKey && apiKey !== "ನಿಮ್ಮ_Pexels_API_Key_ಇಲ್ಲಿ_ಹಾಕಿ") {
    try {
      const url = `https://api.pexels.com/v1/search?query=${encodeURIComponent(query)}&per_page=${count}&orientation=portrait`;
      const response = await fetch(url, {
        headers: { Authorization: apiKey }
      });

      if (response.ok) {
        const data = await response.json();
        for (let i = 0; i < Math.min(count, data.photos.length); i++) {
          const imgUrl = data.photos[i].src.medium;
          const imgRes = await fetch(imgUrl);
          const buffer = await imgRes.buffer();
          const filePath = path.join(downloadDir, `image_${Date.now()}_${i}.jpg`);
          fs.writeFileSync(filePath, buffer);
          imagePaths.push(filePath);
        }
        if (imagePaths.length > 0) {
          console.log(`✅ ${imagePaths.length} Pexels ಚಿತ್ರಗಳು ಡೌನ್ಲೋಡ್ ಆಗಿವೆ.`);
          return imagePaths;
        }
      }
    } catch (apiErr) {
      console.warn("⚠️ Pexels API fetch failed, switching to fallback images:", apiErr.message);
    }
  }

  // Fallback: Generate valid lightweight frames so rendering still works offline.
  const colors = ["#E11D48", "#7C3AED", "#0284C7"];
  for (let i = 0; i < count; i++) {
    const filePath = path.join(downloadDir, `scene_frame_${Date.now()}_${i}.ppm`);
    fs.writeFileSync(filePath, createFallbackFrame(colors[i % colors.length]));
    imagePaths.push(filePath);
  }

  console.log(`✅ ${imagePaths.length} fallback ದೃಶ್ಯಗಳು ಸಿದ್ಧವಾಗಿವೆ.`);
  return imagePaths;
}

function createFallbackFrame(hexColor, width = 720, height = 1280) {
  const red = parseInt(hexColor.slice(1, 3), 16);
  const green = parseInt(hexColor.slice(3, 5), 16);
  const blue = parseInt(hexColor.slice(5, 7), 16);
  const pixels = Buffer.alloc(width * height * 3);

  for (let y = 0; y < height; y++) {
    const shade = 0.55 + (y / height) * 0.45;
    for (let x = 0; x < width; x++) {
      const offset = (y * width + x) * 3;
      pixels[offset] = Math.round(red * shade);
      pixels[offset + 1] = Math.round(green * shade);
      pixels[offset + 2] = Math.round(blue * shade);
    }
  }

  return Buffer.concat([
    Buffer.from(`P6\n${width} ${height}\n255\n`),
    pixels
  ]);
}
