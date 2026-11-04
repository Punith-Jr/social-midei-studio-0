import { getVideoType } from './videoTypeCatalog.js';

const CLAUDE_URL = 'https://api.anthropic.com/v1/messages';

function parseJson(text) {
  const cleaned = text.trim().replace(/^```(?:json)?\s*/i, '').replace(/\s*```$/, '');
  return JSON.parse(cleaned);
}

function addCharacterSheetToPrompts(result) {
  const sheet = Array.isArray(result.character_sheet) ? result.character_sheet : [];
  const sheetText = sheet.map(({ name, description }) => `${name}: ${description}`).join('; ');
  if (!sheetText || !Array.isArray(result.scenes)) return result;
  result.scenes = result.scenes.map((scene) => ({
    ...scene,
    image_prompt: scene.image_prompt?.includes(sheetText)
      ? scene.image_prompt
      : `${scene.image_prompt || ''}. Character sheet: ${sheetText}`
  }));
  return result;
}

async function requestClaude(apiKey, system, userMessage) {
  const response = await fetch(CLAUDE_URL, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
      'x-api-key': apiKey,
      'anthropic-version': '2023-06-01'
    },
    body: JSON.stringify({ model: process.env.CLAUDE_MODEL || 'claude-sonnet-4-5', max_tokens: 12000, system, messages: [{ role: 'user', content: userMessage }] })
  });
  const body = await response.json();
  if (!response.ok) throw new Error(body.error?.message || `Claude API error ${response.status}`);
  return body.content?.map((part) => part.text || '').join('') || '';
}

export async function generateClaudeVideo({ videoTypeKey, videoTypeOverride, userMessage, settings = {} }) {
  const apiKey = process.env.CLAUDE_API_KEY;
  if (!apiKey) throw new Error('CLAUDE_API_KEY is not configured');
  const videoType = videoTypeOverride || getVideoType(videoTypeKey);
  const system = `${videoType.prompt}\n\nSelected defaults: video_type=${videoType.name}; aspect_ratio=${settings.aspect_ratio || videoType.defaultAspectRatio}.`;
  let raw;
  try {
    raw = await requestClaude(apiKey, system, userMessage);
    return addCharacterSheetToPrompts(parseJson(raw));
  } catch (firstError) {
    raw = await requestClaude(apiKey, `${system}\nYour previous answer was invalid JSON. Return only one valid JSON object matching the schema.`, userMessage);
    try {
      return addCharacterSheetToPrompts(parseJson(raw));
    } catch {
      throw new Error(`Claude returned invalid JSON after retry: ${firstError.message}`);
    }
  }
}