export const ANIMATION_BASE_PROMPT = `You are an expert animation director, storyboard artist and script writer.
Output ONLY valid JSON. No markdown, no explanation.

Rules:
- "narration": Kannada (ಕನ್ನಡ), simple spoken language, suitable for voice-over
- "dialogue": [{character, kannada_text, emotion, voice}] when characters speak
- "image_prompt": English only, always starts with the full character_sheet descriptions of characters present in the scene + the chosen animation style
- "motion_prompt": English, describes ONLY movement (character action, camera, background elements) for image-to-video models
- Keep characters, colors and clothes IDENTICAL in every scene
- Scene duration = voice length (about 2.5 Kannada words per second)
- Story structure: Beginning (introduce) → Problem → Struggle → Solution → Moral/Ending

JSON schema:
{
  "title": "",
  "animation_style": "",
  "aspect_ratio": "",
  "total_duration": 0,
  "moral": "",
  "character_sheet": [{"name":"","age":"","appearance":"","clothes":"","personality":"","voice_type":""}],
  "background_music_mood": "",
  "scenes": [{
    "id": 1,
    "location": "",
    "time_of_day": "",
    "narration": "",
    "dialogue": [],
    "image_prompt": "",
    "motion_prompt": "",
    "camera": "",
    "transition": "",
    "duration": 5,
    "on_screen_text": "",
    "sound_effect": ""
  }]
}`;

const style = (key, name, description, aspectRatios, defaultAspectRatio, rules) => ({
  key,
  name,
  description,
  aspectRatios,
  defaultAspectRatio,
  prompt: `${ANIMATION_BASE_PROMPT}\n\n${rules}`
});

export const ANIMATION_STYLES = [
  style('2d-cartoon', '2D Cartoon Animation', 'Playful children-friendly cartoon storytelling.', ['16:9'], '16:9', `STYLE: 2D Cartoon | ASPECT: 16:9 | 8-12 scenes
Every image_prompt ends with "2D cartoon animation style, thick clean outlines, flat vibrant colors, cute expressive characters, simple painted background, children-friendly".
Use bouncy movement, exaggerated expressions, squash and stretch, comedy timing, slow zoom, side pan, fade and swipe transitions. Use playful Kannada narration and end with a clear moral.`),
  style('3d-animation', '3D Animation', 'Warm cinematic 3D feature-film style.', ['16:9'], '16:9', `STYLE: 3D Animation | ASPECT: 16:9 | 8-12 scenes
Every image_prompt ends with "3D animated feature film style, soft global illumination, subsurface skin shading, detailed textures, expressive big eyes, depth of field, high quality render, warm cinematic lighting".
Use dolly, orbit, crane, emotional close-ups, and wide establishing shots. Motion prompts may include character walks forward naturally, cloth and hair moving softly, and slow camera orbit.`),
  style('anime', 'Anime Style Animation', 'Dramatic anime story for landscape or vertical video.', ['16:9', '9:16'], '16:9', `STYLE: Anime | ASPECT: 16:9 or 9:16 | 10-15 scenes
Every image_prompt ends with "anime style, cel shading, detailed eyes, dramatic lighting, vibrant sky, speed lines for action, soft bokeh background".
Use speed lines and impact frames for action; slow zoom, falling petals, or rain for emotion. Include hair and clothes flowing in wind in motion_prompt. Narration is emotional and dramatic Kannada.`),
  style('kids-rhyme', 'Kids Rhymes / ಮಕ್ಕಳ ಪದ್ಯ', 'Karaoke-style Kannada rhyme animation.', ['16:9'], '16:9', `STYLE: Kids Rhyme | ASPECT: 16:9 | 6-10 scenes | 1-3 min
The supplied Kannada rhyme or poem must not be changed. Use one scene per line or pair of lines, exact lyrics in on_screen_text with word-by-word karaoke highlighting. End image prompts with "super cute 2D cartoon, bright primary colors, round shapes, smiling animals, simple background". Characters dance and bounce to the beat. Set background_music_mood to "cheerful nursery rhyme, simple melody, claps" and use a sweet, slow, clear child-friendly voice.`),
  style('moral-story', 'Moral Story / ನೀತಿ ಕಥೆ', 'Panchatantra or folk tale with a clear lesson.', ['16:9'], '16:9', `STYLE: Moral Story (Panchatantra / folk tale) | ASPECT: 16:9 | 8-12 scenes
Use animals or villagers, Indian village and forest settings, and end image prompts with "watercolor storybook illustration, warm earthy colors, soft light, Indian rural setting, gentle 2D animation". Give each character a distinct voice and dialogue. The last scene must show the moral as one simple Kannada sentence in large on_screen_text and add a question for kids such as "ನೀವು ಏನು ಕಲಿತಿರಿ?".`),
  style('mythological', 'Mythological / ಪೌರಾಣಿಕ', 'Respectful devotional Indian mythological animation.', ['16:9'], '16:9', `STYLE: Mythological | ASPECT: 16:9 | 10-15 scenes
End image prompts with "Indian mythological art style, rich gold and deep red colors, temple architecture, divine glow, ornate jewelry, epic wide shots, traditional Indian painting influence". Use grand, respectful, devotional tone, slow majestic camera moves, divine light rays and glowing effects. Use pure poetic Kannada, remain faithful to the source text, do not invent events, and depict deities respectfully.`),
  style('whiteboard-explainer', 'Whiteboard / Explainer', 'Hand-drawn educational explainer animation.', ['16:9'], '16:9', `STYLE: Whiteboard Explainer | ASPECT: 16:9 | 8-15 scenes
End image prompts with "hand-drawn black line doodle on white background, simple icons, minimal blue and orange color accents". Each scene is one idea. Motion prompts must include lines drawing themselves progressively and a hand drawing. Show Kannada keywords as spoken. Structure: Problem → Explanation → Example → Summary. Suitable for education, government schemes, and awareness videos.`),
  style('motion-graphics', 'Motion Graphics / Infographic', 'Modern data-led infographic animation.', ['16:9', '9:16'], '16:9', `STYLE: Motion Graphics | ASPECT: 16:9 or 9:16
End image prompts with "clean flat vector design, geometric shapes, gradient background, modern icons, smooth infographic style". Animate numbers counting up, charts growing, and icons popping in. Keep a fixed four-color brand palette. on_screen_text carries the main message and narration supports it. Use slide, zoom, and shape-wipe transitions.`),
  style('stick-figure', 'Stick Figure / Simple Character', 'Fast, humorous simple-character stories.', ['16:9', '9:16'], '16:9', `STYLE: Stick Figure | ASPECT: 16:9 or 9:16 | 10-20 quick scenes
End image prompts with "simple stick figure drawing, black lines on solid pastel background, expressive faces, minimal props". Use humor, life lessons, conversational Kannada, very fast pacing, and a new visual every 3-4 seconds.`),
  style('clay-stop-motion', 'Clay / Stop Motion', 'Handmade claymation with charming movement.', ['16:9'], '16:9', `STYLE: Clay Stop Motion | ASPECT: 16:9 | 8-12 scenes
End image prompts with "claymation style, plasticine texture, visible fingerprints, handmade miniature set, warm studio lighting, tilt-shift". Use charming funny tone and motion prompts with "stop motion animation, 12 fps, slight jitter".`)
];

export const ANIMATION_PART_C_PROMPTS = {
  characterDesign: 'Character reference sheet of [name], [age], [appearance], wearing [clothes], shown in front view, side view and back view, plus 4 expressions (happy, sad, angry, surprised), neutral background, [animation style], consistent design, full body.',
  sceneImage: '[character_sheet description], [action], in [location], [time of day], [camera angle: wide / medium / close-up], [lighting], [animation style keywords], detailed background, no text, no watermark',
  negative: 'blurry, low quality, extra fingers, extra limbs, deformed face, bad anatomy, text, watermark, logo, inconsistent character, cropped head',
  motion: '[Subject] [movement], gentle camera [dolly in / pan left / orbit], background [wind in trees / clouds moving / water flowing], smooth natural motion, consistent character, no morphing, 5 seconds',
  voice: 'Assign voices: narrator = warm adult storyteller, child characters = bright young voice, elder = deep slow voice, villain = low dramatic voice. Speaking speed: 0.9x for kids, 1.0x for general. Add short pauses (0.5 sec) after each sentence. Emotion tags: [happy] [sad] [excited] [whisper].',
  music: 'Instrumental only, [mood: cheerful / emotional / epic / calm], Indian folk instruments (flute, tabla, veena) mixed with soft orchestral strings, [tempo] BPM, loopable, no vocals, 2 minutes',
  soundEffects: 'For each scene suggest 1-3 sound effects with timing, such as "bird chirping (0-5s)", "footsteps on grass (2-4s)", "door creak (3s)", "thunder (start of scene 6)". Keep volume lower than the voice.',
  subtitles: 'Split the Kannada narration into subtitle lines of at most 6 words, with start and end time in seconds, output as SRT. Do not change the words.',
  thumbnail: 'YouTube thumbnail, [main character] with a big surprised expression, bright contrasting colors, bold simple background, space on the left for text, [animation style], high detail, no text in image',
  storyIdea: 'Write an original short story for [age group] about [topic] in simple Kannada, 250-400 words, with 2-3 characters, a clear problem, a solution and a moral. Avoid violence and copyrighted characters.'
};

export function animationCatalogForDatabase() {
  return ANIMATION_STYLES.map((item) => ({ ...item }));
}

export function getAnimationStyle(key) {
  return ANIMATION_STYLES.find((item) => item.key === key) || ANIMATION_STYLES[0];
}