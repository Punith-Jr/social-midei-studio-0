# Creator Studio - Automated 10-Minute Video Generator & Uploader Backend

## 🌟 ವೈಶಿಷ್ಟ್ಯಗಳು (Features)
1. **Google AI Studio Gemini Script Engine**: ಪ್ರತಿ 10 ನಿಮಿಷಕ್ಕೆ ಆಕರ್ಷಕ ಸ್ಕ್ರಿಪ್ಟ್, ಶೀರ್ಷಿಕೆ ಮತ್ತು 3 ದೃಶ್ಯ ಪ್ರಾಂಪ್ಟ್ ಜನರೇಟ್ ಮಾಡುತ್ತದೆ.
2. **Text-To-Speech (TTS)**: ಧ್ವನಿ ವಾಯ್ಸ್‌ಓವರ್ ಸಿದ್ಧಪಡಿಸುತ್ತದೆ.
3. **Pexels & AI Scene Fetcher**: ವೀಡಿಯೊಗಾಗಿ 3 ಉತ್ತಮ ಗುಣಮಟ್ಟದ ದೃಶ್ಯಗಳನ್ನು ಡೌನ್‌ಲೋಡ್ ಮಾಡುತ್ತದೆ.
4. **FFmpeg Video Renderer**: ಚಿತ್ರಗಳು ಮತ್ತು ಆಡಿಯೋ ಸೇರಿಸಿ 1080p MP4 ವೀಡಿಯೊ ರೆಂಡರ್ ಮಾಡುತ್ತದೆ.
5. **YouTube & Meta OAuth Publishing**: YouTube Shorts, Instagram Reels ಮತ್ತು Facebook ಗೆ ಆಟೋ-ಅಪ್‌ಲೋಡ್ ಮಾಡುತ್ತದೆ.
6. **Crash-Proof Cron Scheduler**: ಪ್ರತಿ 10 ನಿಮಿಷಕ್ಕೊಮ್ಮೆ ನಿರಂತರವಾಗಿ ಹಿನ್ನೆಲೆಯಲ್ಲಿ ರನ್ ಆಗುತ್ತದೆ.
7. **10 Video Type Prompt Catalog**: Manga, Long Video, Facebook, Instagram, Shorts, Reels, Cinematic, Photo, Comic ಮತ್ತು Custom prompts MongoDB ಯಲ್ಲಿ admin-editable ಆಗಿವೆ.
8. **10 Animation Style Prompt Catalog**: 2D, 3D, Anime, Kids Rhyme, Moral Story, Mythological, Whiteboard, Motion Graphics, Stick Figure ಮತ್ತು Clay Stop Motion styles MongoDB ಯಲ್ಲಿ admin-editable ಆಗಿವೆ.

---

## 🚀 ಸೆಟಪ್ ವಿಧಾನ (Setup & Installation)

### 1. ಅಗತ್ಯವಿರುವ ಸಾಫ್ಟ್‌ವೇರ್ (Prerequisites)
- **Node.js**: v18+
- **FFmpeg**:
  - Ubuntu/Debian: `sudo apt-get install ffmpeg`
  - macOS: `brew install ffmpeg`
  - Windows: `winget install Gyan.FFmpeg`
- **MongoDB**: Local MongoDB ಅಥವಾ MongoDB Atlas Cloud

### 2. ಇನ್‌ಸ್ಟಾಲೇಶನ್ (Install Dependencies)
```bash
cd backend
npm install
```

### 3. ಎನ್ವಿರಾನ್‌ಮೆಂಟ್ ಕಾನ್ಫಿಗರೇಶನ್ (Configure .env)
`.env.example` ಅನ್ನು `.env` ಎಂದು ನಕಲಿಸಿ ಮತ್ತು ನಿಮ್ಮ ಕೀಗಳನ್ನು ನಮೂದಿಸಿ:
```bash
cp .env.example .env
```

Claude generation ಬಳಸಲು ಈ environment variable ಅಗತ್ಯ:
```bash
CLAUDE_API_KEY=your_anthropic_key
# Optional; default: claude-sonnet-4-5
CLAUDE_MODEL=claude-sonnet-4-5
```

## Video type prompt API

- `GET /api/video-types` - Create ಪುಟದ cards/dropdownಗಾಗಿ enabled types ಮತ್ತು default aspect ratios.
- `PUT /api/video-types/:key` - admin prompt edit (`prompt`, `defaultAspectRatio`, `enabled`, ಮುಂತಾದ fields).
- `POST /api/video-types/:key/generate` - selected prompt ಅನ್ನು Claude `system` message ಆಗಿ ಮತ್ತು story/settings ಅನ್ನು `user` message ಆಗಿ ಕಳುಹಿಸುತ್ತದೆ.

Generation request ಉದಾಹರಣೆ:
```json
{
  "topic": "ಮಳೆಗಾಲದಲ್ಲಿ ರೈತನ ಕಥೆ",
  "story": "ಒಬ್ಬ ರೈತ ತನ್ನ ಹೊಲವನ್ನು ಉಳಿಸಲು ಹೋರಾಡುತ್ತಾನೆ...",
  "settings": { "aspect_ratio": "9:16", "duration": "45 sec", "language": "kn" }
}
```

Claude ಉತ್ತರವನ್ನು JSON ಆಗಿ parse ಮಾಡಲಾಗುತ್ತದೆ. Parse ವಿಫಲವಾದರೆ ಒಂದೇ ಬಾರಿ JSON-only retry ನಡೆಯುತ್ತದೆ. ನಂತರ `character_sheet` ವಿವರಣೆಯನ್ನು ಪ್ರತಿ scene ನ `image_prompt` ಗೆ server ಸ್ವಯಂಚಾಲಿತವಾಗಿ ಸೇರಿಸುತ್ತದೆ.

## Animation prompt API

- `GET /api/animation-styles` - animation style cards/dropdown ಮತ್ತು default aspect ratios.
- `PUT /api/animation-styles/:key` - admin style prompt edit.
- `GET /api/animation-prompts` - character design, scene image, negative, motion, voice, music, sound effects, subtitle, thumbnail, and story idea prompts.
- `POST /api/animation-styles/:key/generate` - animation style ಅನ್ನು Claude system prompt ಆಗಿ ಮತ್ತು story/settings ಅನ್ನು user message ಆಗಿ ಕಳುಹಿಸುತ್ತದೆ.

Animation generation responseನಲ್ಲಿ `character_sheet`, Kannada `narration`/`dialogue`, English `image_prompt`/`motion_prompt`, location, camera, transition, duration, sound effects ಮತ್ತು five-part story flow ಇರುತ್ತದೆ. `image_prompt` ಆರಂಭದಲ್ಲಿ full character sheet ಸೇರಿಸಲಾಗುತ್ತದೆ.

### 4. ಸರ್ವರ್ ಚಲಾಯಿಸಿ (Run Server)
```bash
# Production mode
npm start

# Development mode (auto-reload)
npm run dev
```

---

## ☁️ ಡಿಪ್ಲಾಯ್‌ಮೆಂಟ್ (Deployment Guide)

### Render.com ಅಥವಾ Railway.app ನಲ್ಲಿ ಡಿಪ್ಲಾಯ್ ಮಾಡಲು:
1. ನಿಮ್ಮ GitHub ರೆಪೊಸಿಟರಿಯನ್ನು Render / Railway ಗೆ ಸಂಪರ್ಕಿಸಿ.
2. Environment: Node
3. Build Command: `npm install`
4. Start Command: `node server.js`
5. Render ಡ್ಯಾಶ್‌ಬೋರ್ಡ್‌ನಲ್ಲಿ **Environment Variables** ಸೆಕ್ಷನ್‌ಗೆ ಹೋಗಿ ನಿಮ್ಮ `.env` ಕೀಗಳನ್ನು ಸೇರಿಸಿ.
6. Render/Railway ನಲ್ಲಿ FFmpeg ಸಪೋರ್ಟ್ ಇರುವುದರಿಂದ 24/7 ನಿರಂತರವಾಗಿ 10 ನಿಮಿಷದ ಶೆಡ್ಯೂಲರ್ ರನ್ ಆಗುತ್ತದೆ!
