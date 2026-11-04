import { getAnimationStyle } from './animationPromptService.js';

const CLAUDE_URL = 'https://api.anthropic.com/v1/messages';

function parseJson(text) {
  const cleaned = text.trim().replace(/^```(?:json)?\s*/i, '').replace(/\s*```$/, '');
  return JSON.parse(cleaned);
}

function normalizeAnimationResult(result) {
  const sheet = Array.isArray(result.character_sheet) ? result.character_sheet : [];
  const sheetText = sheet.map((character) => `${character.name}: age ${character.age}; ${character.appearance}; clothes ${character.clothes}; personality ${character.personality}; voice ${character.voice_type}`).join(' | ');
  if (!sheetText || !Array.isArray(result.scenes)) return result;
  return {
    ...result,
    scenes: result.scenes.map((scene) => ({
      ...scene,
      dialogue: Array.isArray(scene.dialogue) ? scene.dialogue : [],
      motion_prompt: scene.motion_prompt || 'Subtle natural character movement, gentle camera motion, background elements moving softly, no morphing',
      image_prompt: scene.image_prompt?.startsWith(sheetText)
        ? scene.image_prompt
        : `${sheetText}, ${scene.image_prompt || ''}`
    }))
  };
}

async function requestClaude(apiKey, system, userMessage) {
  const response = await fetch(CLAUDE_URL, {
    method: 'POST',
    headers: { 'content-type': 'application/json', 'x-api-key': apiKey, 'anthropic-version': '2023-06-01' },
    body: JSON.stringify({ model: process.env.CLAUDE_MODEL || 'claude-sonnet-4-5', max_tokens: 16000, system, messages: [{ role: 'user', content: userMessage }] })
  });
  const body = await response.json();
  if (!response.ok) throw new Error(body.error?.message || `Claude API error ${response.status}`);
  return body.content?.map((part) => part.text || '').join('') || '';
}

export async function generateClaudeAnimation({ styleKey, styleOverride, userMessage, settings = {} }) {
  const apiKey = process.env.CLAUDE_API_KEY;
  if (!apiKey) throw new Error('CLAUDE_API_KEY is not configured');
  const selectedStyle = styleOverride || getAnimationStyle(styleKey);
  const system = `${selectedStyle.prompt}\n\nSelected defaults: animation_style=${selectedStyle.name}; aspect_ratio=${settings.aspect_ratio || selectedStyle.defaultAspectRatio}.`;
  try {
    return normalizeAnimationResult(parseJson(await requestClaude(apiKey, system, userMessage)));
  } catch (firstError) {
    try {
      const retryPrompt = `${system}\nYour previous answer was invalid JSON. Return only one valid JSON object matching the animation schema. Do not use markdown fences.`;
      return normalizeAnimationResult(parseJson(await requestClaude(apiKey, retryPrompt, userMessage)));
    } catch {
      throw new Error(`Claude returned invalid animation JSON after retry: ${firstError.message}`);
    }
  }
}