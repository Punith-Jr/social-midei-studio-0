export const BASE_PROMPT = `You are a professional video script writer and storyboard director.
Output ONLY valid JSON. No markdown, no explanation.

Rules:
- "narration": Kannada (ಕನ್ನಡ), simple spoken language, natural for voice-over
- "image_prompt": English only, detailed, ends with the chosen style keywords
- Keep the same "character_sheet" description inside every image_prompt so characters look identical in all scenes
- Each scene duration must match narration length (about 2.5 Kannada words per second)

JSON schema:
{
  "title": "",
  "video_type": "",
  "aspect_ratio": "",
  "total_duration": 0,
  "character_sheet": [{"name":"","description":""}],
  "background_music_mood": "",
  "scenes": [{
    "id": 1,
    "narration": "",
    "image_prompt": "",
    "duration": 5,
    "camera": "",
    "transition": "",
    "on_screen_text": "",
    "sound_effect": ""
  }]
}`;

const type = (key, name, description, aspectRatios, defaultAspectRatio, rules) => ({
  key, name, description, aspectRatios, defaultAspectRatio,
  prompt: `${BASE_PROMPT}\n\n${rules}`
});

export const VIDEO_TYPES = [
  type('manga-story', 'Manga Story to Video', 'Emotional manga story with a cliffhanger.', ['16:9', '9:16'], '16:9', `VIDEO TYPE: Manga Story to Video | ASPECT: 16:9 or 9:16
Convert the story into a manga-style video with 10-15 scenes.
Every image_prompt ends with: "black and white manga style, bold ink lines, screentone shading, dramatic speed lines, high contrast, expressive eyes, Japanese manga panel". Add "spot color accent" only when color is requested.
Use close-up on eyes, action burst, impact frame, emotional silence panel. Use pan across panel, shake on impact, slow zoom on emotion. Transitions: panel-slide, flash-cut, ink-wipe. Use large Kannada manga sound effects in on_screen_text. End with a cliffhanger or strong emotional closing.`),
  type('youtube-long', 'YouTube Long Video', '8-15 minute chapter-based educational or story video.', ['16:9'], '16:9', `VIDEO TYPE: YouTube Long Video | ASPECT: 16:9 | DURATION: 8-15 min
Structure: hook (first 15 sec), intro (30 sec), 4-6 chapters with 6-10 scenes each, recap, and CTA to like, subscribe, and comment. Add "chapter" to every scene and YouTube chapter timestamps. Use conversational Kannada and a pattern interrupt every 30-40 seconds. Mix illustrations, infographics, maps, and timelines. Also output seo_title (60 chars), description (500 words), tags (15), thumbnail_ideas (3 English image prompts).`),
  type('facebook', 'Facebook Video', 'Fast, caption-first, shareable community video.', ['1:1', '4:5'], '1:1', `VIDEO TYPE: Facebook Video | ASPECT: 1:1 or 4:5 | DURATION: 45-90 sec
85% watch with sound off, so every scene needs large readable Kannada captions in on_screen_text. Stop the scroll in the first 3 seconds. Keep scenes 3-5 seconds, story-driven, local, emotional, and shareable. End with "ನಿಮ್ಮ ಅಭಿಪ್ರಾಯ ಕಾಮೆಂಟ್ ಮಾಡಿ". Also output post_caption (150 words), hashtags (5), and best_posting_time.`),
  type('instagram-feed', 'Instagram Feed Video', 'Polished aesthetic feed video with safe captions.', ['4:5'], '4:5', `VIDEO TYPE: Instagram Feed Video | ASPECT: 4:5 | DURATION: 30-60 sec
Keep a consistent palette. Every image_prompt ends with: "clean composition, soft cinematic light, vibrant yet harmonious colors, Instagram aesthetic". Use punchy Kannada narration and bold center-safe captions with 10% edge margins. Use fade, slide, and zoom transitions. Also output caption with hook line, hashtags (10), and cover_image_prompt.`),
  type('youtube-shorts', 'YouTube Shorts', 'Vertical loopable short with a rapid hook.', ['9:16'], '9:16', `VIDEO TYPE: YouTube Shorts | ASPECT: 9:16 | DURATION: 30-58 sec
Hook in first 2 seconds. Use 6-10 scenes, each 3-6 seconds, and max 100 Kannada words total. Use large word-by-word animated subtitles in the center-lower area. Every image_prompt includes "vertical 9:16 composition". Final scene loops to the hook. Also output title ending in #Shorts and hashtags (3).`),
  type('instagram-reels', 'Instagram Reels', 'High-energy beat-synced vertical reel.', ['9:16'], '9:16', `VIDEO TYPE: Instagram Reels | ASPECT: 9:16 | DURATION: 15-30 sec
Set background_music_mood to "upbeat trending / beat-driven". Use 5-8 scenes of 2-4 seconds, zoom punches, whip-pan, glitch, and flash transitions. Minimal narration; mostly bold Kannada on_screen_text. Keep content outside top 15% and bottom 20%. Every image_prompt ends with "vibrant, saturated, trendy social media aesthetic". Also output caption, hashtags (8), audio_suggestion_type, and cover_frame_idea.`),
  type('ai-cinematic', 'AI Cinematic Video', 'Film-directed cinematic sequence with motion prompts.', ['16:9', '2.39:1'], '16:9', `VIDEO TYPE: AI Cinematic Video | ASPECT: 16:9 (2.39:1 letterbox optional)
Direct with establishing, medium, close-up, over-the-shoulder, and reveal shots. Every image_prompt includes shot type, 35mm/50mm/85mm lens, lighting, color grading, and "cinematic, ultra detailed, film grain, depth of field, 8K". Use dolly-in, crane up, slow pan, handheld, and drone shot. Add motion_prompt to every scene. Use poetic minimal dramatic Kannada narration and orchestral swell, ambient wind, heartbeat, or silence.`),
  type('photo-to-video', 'Photo to Video', 'Occasion-aware slideshow using only uploaded photos.', ['16:9', '9:16', '1:1'], '16:9', `VIDEO TYPE: Photo to Video | INPUT: user-uploaded photos (N photos)
Receive photo descriptions and optional occasion. Create exactly one scene per photo; do not generate new images. Replace image_prompt with photo_index and image_enhance. Choose Ken Burns motion: zoom on faces, pan on landscapes, zoom-out on groups. Use 3-6 seconds per photo and emotional beginning-to-highlight-to-ending order. Use dissolve for emotional, slide for fun, flash for party. Add short heartfelt Kannada caption per photo, title card, ending card, and beat-matched music mood.`),
  type('animated-comic', 'Story to Animated Comic', 'Motion comic with pages, panels, bubbles, and voiced characters.', ['16:9', '9:16'], '16:9', `VIDEO TYPE: Animated Comic (Motion Comic) | ASPECT: 16:9 or 9:16
Divide into comic pages with 3-6 panels each. Add panel_layout, speech_bubbles [{character, kannada_text, bubble_type: speech/thought/shout, voice: child/elder/male/female}], and caption_box per scene. Every image_prompt ends with "colorful comic book style, thick outlines, flat vibrant colors, halftone dots, dynamic angles, no text, no speech bubbles in the image". Panels pop in one by one, camera moves panel to panel, and bubbles type in. Use comic sound effects such as "ಬೂಮ್!" and "ಜಂಪ್!".`),
  type('custom', 'Custom Video Creation', 'User-controlled video with strict settings.', ['16:9', '9:16', '1:1', '4:5'], '16:9', `VIDEO TYPE: Custom | The user provides all settings.
User settings JSON: {"topic":"","story":"","style":"","aspect_ratio":"","duration":"","language":"","voice":"","music":"","tone":"","target_audience":"","num_scenes":0,"special_instructions":""}
Follow every setting strictly; choose defaults only for empty fields. If 9:16, use short scenes and large captions; if 16:9, use detailed scenes and longer narration. Choose structure for educational, emotional, promotional, or entertaining tone. User special_instructions override these rules. Return the standard schema plus summary_of_choices in Kannada.`)
];

export function getVideoType(key) {
  return VIDEO_TYPES.find((item) => item.key === key) || VIDEO_TYPES.find((item) => item.key === 'custom');
}

export function catalogForDatabase() {
  return VIDEO_TYPES.map((item) => ({ ...item }));
}