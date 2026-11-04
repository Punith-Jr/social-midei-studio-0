const state = {
  scenes: [],
  videoTypes: [],
  animationStyles: []
};

const $ = (selector) => document.querySelector(selector);
const form = $('#storyForm');
const videoTypeSelect = $('#videoType');
const animationStyleSelect = $('#animationStyle');
const aspectRatioSelect = $('#aspectRatio');
const sceneList = $('#sceneList');
const emptyState = $('#emptyState');
const formMessage = $('#formMessage');
const generateButton = $('#generateButton');

function setMessage(message, isError = false) {
  formMessage.textContent = message;
  formMessage.classList.toggle('error', isError);
}

async function requestJson(url, options = {}) {
  const response = await fetch(url, options);
  const body = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(body.message || `Request failed (${response.status})`);
  return body;
}

function populateSelect(select, items, fallbackLabel) {
  select.innerHTML = '';
  if (!items.length) {
    select.add(new Option(fallbackLabel, 'custom'));
    return;
  }
  items.forEach((item) => {
    const option = new Option(item.name, item.key);
    option.title = item.description || '';
    select.add(option);
  });
}

async function loadOptions() {
  try {
    const [videoTypes, animationStyles] = await Promise.all([
      requestJson('/api/video-types'),
      requestJson('/api/animation-styles')
    ]);
    state.videoTypes = videoTypes.data || [];
    state.animationStyles = animationStyles.data || [];
    populateSelect(videoTypeSelect, state.videoTypes, 'Custom video');
    populateSelect(animationStyleSelect, state.animationStyles, 'Custom animation');
    $('#serviceStatus').textContent = 'Studio ready';
  } catch (error) {
    $('#serviceStatus').textContent = 'Local mode';
    populateSelect(videoTypeSelect, [], 'Custom video');
    populateSelect(animationStyleSelect, [], 'Custom animation');
  }
}

function sceneValue(scene, key) {
  return scene[key] || '';
}

function renderScenes() {
  const count = state.scenes.length;
  $('#sceneCount').textContent = `${count} scene${count === 1 ? '' : 's'}`;
  emptyState.hidden = count > 0;
  sceneList.innerHTML = '';

  state.scenes.forEach((scene, index) => {
    const card = document.createElement('article');
    card.className = 'scene-card';
    card.style.animationDelay = `${index * 45}ms`;
    card.innerHTML = `
      <div class="scene-card-header">
        <div>
          <div class="scene-index">SCENE ${String(index + 1).padStart(2, '0')}</div>
          <h3 class="scene-title">${escapeHtml(scene.title || `Scene ${index + 1}`)}</h3>
        </div>
        <span class="scene-duration">${escapeHtml(String(scene.duration || 5))} sec</span>
      </div>
      <div class="scene-fields">
        <label>Narration<textarea data-index="${index}" data-key="narration">${escapeHtml(scene.narration)}</textarea></label>
        <label>Image prompt<textarea data-index="${index}" data-key="image_prompt">${escapeHtml(scene.image_prompt)}</textarea></label>
      </div>
      <div class="scene-meta">
        <span class="meta-tag">${escapeHtml(scene.camera || 'camera')}</span>
        <span class="meta-tag">${escapeHtml(scene.transition || 'fade')}</span>
        ${scene.on_screen_text ? `<span class="meta-tag">${escapeHtml(scene.on_screen_text)}</span>` : ''}
      </div>`;
    sceneList.appendChild(card);
  });
}

function escapeHtml(value) {
  return String(value || '').replace(/[&<>'"]/g, (character) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
  }[character]));
}

async function generateScenes(event) {
  event.preventDefault();
  generateButton.disabled = true;
  setMessage('Claude ಕಥೆಯನ್ನು ದೃಶ್ಯಗಳಾಗಿ ವಿಭಜಿಸುತ್ತಿದೆ...');

  const selectedStyle = state.animationStyles.find((item) => item.key === animationStyleSelect.value);
  const selectedType = state.videoTypes.find((item) => item.key === videoTypeSelect.value);
  const settings = {
    aspect_ratio: aspectRatioSelect.value,
    duration: $('#duration').value,
    language: $('#language').value,
    animation_style: selectedStyle?.name || animationStyleSelect.value,
    style_prompt: selectedStyle?.prompt || ''
  };

  try {
    const result = await requestJson(`/api/video-types/${encodeURIComponent(videoTypeSelect.value)}/generate`, {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        topic: $('#topic').value.trim(),
        story: $('#story').value.trim(),
        settings
      })
    });
    state.scenes = Array.isArray(result.data?.scenes) ? result.data.scenes : [];
    renderScenes();
    setMessage(state.scenes.length ? `${state.scenes.length} ದೃಶ್ಯಗಳು ಸಿದ್ಧವಾಗಿವೆ. ನೀವು ಈಗ edit ಮಾಡಬಹುದು.` : 'No scenes returned.', !state.scenes.length);
  } catch (error) {
    setMessage(error.message, true);
  } finally {
    generateButton.disabled = false;
  }
}

function updateScene(event) {
  const target = event.target;
  const index = Number(target.dataset.index);
  const key = target.dataset.key;
  if (!Number.isInteger(index) || !key || !state.scenes[index]) return;
  state.scenes[index][key] = target.value;
}

async function loadLibrary() {
  const grid = $('#libraryGrid');
  grid.innerHTML = '<p class="muted-message">Loading projects...</p>';
  try {
    const result = await requestJson('/api/videos?limit=6');
    const videos = result.data || [];
    if (!videos.length) {
      grid.innerHTML = '<p class="muted-message">No generated projects yet.</p>';
      return;
    }
    grid.innerHTML = videos.map((video) => `
      <article class="library-card">
        <h3>${escapeHtml(video.title)}</h3>
        <p>${escapeHtml(video.topic || video.description || 'Generated video')}</p>
        <div class="library-meta"><span>${escapeHtml(video.format || '9:16 Shorts')}</span><span>${escapeHtml(video.uploadStatus || 'draft')}</span></div>
      </article>`).join('');
  } catch (error) {
    grid.innerHTML = '<p class="muted-message">Library needs a connected MongoDB database.</p>';
  }
}

$('#imageUpload').addEventListener('change', (event) => {
  const count = event.target.files.length;
  $('#uploadStatus').textContent = count ? `${count} image${count === 1 ? '' : 's'} selected` : 'Optional assets';
});
$('#audioUpload').addEventListener('change', (event) => {
  if (event.target.files[0]) $('#uploadStatus').textContent = `${event.target.files[0].name} selected`;
});
videoTypeSelect.addEventListener('change', () => {
  const selected = state.videoTypes.find((item) => item.key === videoTypeSelect.value);
  if (selected?.defaultAspectRatio) aspectRatioSelect.value = selected.defaultAspectRatio;
});
form.addEventListener('submit', generateScenes);
sceneList.addEventListener('input', updateScene);
$('#loadLibraryButton').addEventListener('click', loadLibrary);
$('#refreshButton').addEventListener('click', loadLibrary);

loadOptions();
loadLibrary();
