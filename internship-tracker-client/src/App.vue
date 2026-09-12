<script setup>
import { onMounted } from 'vue'
import { getStoredTheme, applyTheme } from './theme.js'

onMounted(() => {
  applyTheme(getStoredTheme())
})
</script>

<template>
  <main class="app-container">
    <router-view />
  </main>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

:root {
  --bg-grad-1: #32302f;
  --bg-grad-2: #282828;
  --bg-surface: rgba(60, 56, 54, 0.6);
  --border-soft: rgba(235, 219, 178, 0.08);
  --border-softer: rgba(235, 219, 178, 0.06);
  --text-primary: #ebdbb2;
  --text-secondary: #a89984;
  --text-muted: #928374;
  --text-faint: #665c54;
  --accent: #fe8019;
  --accent-hover: #e0651a;
  --accent-contrast: #282828;
  --accent-soft: rgba(254, 128, 25, 0.1);
  --accent-soft-strong: rgba(254, 128, 25, 0.15);
  --tag-color: #fabd2f;
  --error-bg: rgba(251, 73, 52, 0.12);
  --error-border: rgba(251, 73, 52, 0.4);
  --error-text: #fb9490;
  --status-blue: #83a598;
  --status-yellow: #fabd2f;
  --status-green: #b8bb26;
  --status-red: #fb4934;
}

[data-theme='mint'] {
  --bg-grad-1: #2a302c;
  --bg-grad-2: #1e2320;
  --bg-surface: rgba(113, 119, 109, 0.25);
  --border-soft: rgba(190, 190, 190, 0.12);
  --border-softer: rgba(190, 190, 190, 0.06);
  --text-primary: #e6e8e5;
  --text-secondary: #bebebe;
  --text-muted: #71776d;
  --text-faint: #52574e;
  --accent: #79ed91;
  --accent-hover: #5be376;
  --accent-contrast: #1e2320;
  --accent-soft: rgba(121, 237, 145, 0.12);
  --accent-soft-strong: rgba(121, 237, 145, 0.22);
  --tag-color: #4dbe55;
  --error-bg: rgba(239, 68, 68, 0.12);
  --error-border: rgba(239, 68, 68, 0.4);
  --error-text: #fca5a5;
  --status-blue: #698696;
  --status-yellow: #dbe078;
  --status-green: #79ed91;
  --status-red: #ef5350;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

body {
  font-family: 'Plus Jakarta Sans', system-ui, -apple-system, sans-serif;
  background: radial-gradient(circle at 50% 30%, var(--bg-grad-1) 0%, var(--bg-grad-2) 80%);
  background-attachment: fixed;
  color: var(--text-primary);
  transition: background 0.2s ease, color 0.2s ease;
}

#app {
  min-height: 100%;
}

.app-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  box-sizing: border-box;
}
</style>
