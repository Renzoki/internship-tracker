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

[data-theme='nord'] {
  --bg-grad-1: #3b4252;
  --bg-grad-2: #2e3440;
  --bg-surface: rgba(59, 66, 82, 0.6);
  --border-soft: rgba(236, 239, 244, 0.08);
  --border-softer: rgba(236, 239, 244, 0.06);
  --text-primary: #eceff4;
  --text-secondary: #d8dee9;
  --text-muted: #9aa5b1;
  --text-faint: #6b7484;
  --accent: #88c0d0;
  --accent-hover: #6ba7b8;
  --accent-contrast: #2e3440;
  --accent-soft: rgba(136, 192, 208, 0.1);
  --accent-soft-strong: rgba(136, 192, 208, 0.15);
  --tag-color: #ebcb8b;
  --error-bg: rgba(191, 97, 106, 0.12);
  --error-border: rgba(191, 97, 106, 0.4);
  --error-text: #e5989f;
  --status-blue: #81a1c1;
  --status-yellow: #ebcb8b;
  --status-green: #a3be8c;
  --status-red: #bf616a;
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
