(() => {
  'use strict';

  const STORAGE_KEY = 'creator-studio-todos-v1';
  const form = document.getElementById('add-form');
  const input = document.getElementById('task-input');
  const list = document.getElementById('task-list');
  const emptyState = document.getElementById('empty-state');
  const count = document.getElementById('task-count');
  const summary = document.getElementById('completion-summary');
  const clearButton = document.getElementById('clear-completed');
  const filterButtons = [...document.querySelectorAll('[data-filter]')];
  let filter = 'all';
  let tasks = loadTasks();

  function loadTasks() {
    try {
      const stored = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
      if (!Array.isArray(stored)) return [];
      return stored.filter(task => task && typeof task.id === 'string' && typeof task.text === 'string')
        .map(task => ({ id: task.id, text: task.text, completed: Boolean(task.completed), createdAt: Number(task.createdAt) || Date.now() }));
    } catch (error) {
      console.warn('Could not read saved tasks from local storage.', error);
      return [];
    }
  }

  function saveTasks() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(tasks));
    } catch (error) {
      console.error('Could not save tasks to local storage.', error);
      summary.textContent = 'Could not save changes in this browser.';
    }
  }

  function createId() {
    return typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function'
      ? crypto.randomUUID()
      : `${Date.now()}-${Math.random().toString(36).slice(2)}`;
  }

  function render() {
    const visibleTasks = tasks.filter(task => filter === 'all' || (filter === 'active' ? !task.completed : task.completed));
    const remaining = tasks.filter(task => !task.completed).length;
    const completed = tasks.length - remaining;

    list.replaceChildren();
    for (const task of visibleTasks) {
      const item = document.createElement('li');
      item.className = `task-item${task.completed ? ' is-completed' : ''}`;

      const toggle = document.createElement('button');
      toggle.type = 'button';
      toggle.className = 'task-check';
      toggle.setAttribute('aria-label', task.completed ? `Mark ${task.text} as active` : `Complete ${task.text}`);
      toggle.setAttribute('aria-pressed', String(task.completed));
      toggle.addEventListener('click', () => {
        task.completed = !task.completed;
        saveTasks();
        render();
      });

      const text = document.createElement('span');
      text.className = 'task-text';
      text.textContent = task.text;

      const remove = document.createElement('button');
      remove.type = 'button';
      remove.className = 'delete-button';
      remove.textContent = '×';
      remove.setAttribute('aria-label', `Delete ${task.text}`);
      remove.addEventListener('click', () => {
        tasks = tasks.filter(candidate => candidate.id !== task.id);
        saveTasks();
        render();
      });

      item.append(toggle, text, remove);
      list.append(item);
    }

    emptyState.hidden = visibleTasks.length !== 0;
    count.textContent = `${remaining} ${remaining === 1 ? 'task' : 'tasks'} left`;
    summary.textContent = tasks.length === 0
      ? 'A fresh start.'
      : `${completed} of ${tasks.length} completed`;
    clearButton.disabled = completed === 0;
  }

  form.addEventListener('submit', event => {
    event.preventDefault();
    const text = input.value.trim();
    if (!text) {
      input.focus();
      return;
    }
    tasks.unshift({ id: createId(), text, completed: false, createdAt: Date.now() });
    saveTasks();
    input.value = '';
    render();
    input.focus();
  });

  filterButtons.forEach(button => {
    button.addEventListener('click', () => {
      filter = button.dataset.filter;
      filterButtons.forEach(option => {
        const selected = option === button;
        option.classList.toggle('is-active', selected);
        option.setAttribute('aria-pressed', String(selected));
      });
      render();
    });
  });

  clearButton.addEventListener('click', () => {
    tasks = tasks.filter(task => !task.completed);
    saveTasks();
    render();
  });

  window.addEventListener('storage', event => {
    if (event.key === STORAGE_KEY || event.key === null) {
      tasks = loadTasks();
      render();
    }
  });

  render();
})();
