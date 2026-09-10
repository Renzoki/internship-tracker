<script setup></script>

<template>
  <main class="app-container">
    <router-view />
  </main>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

body {
  font-family: 'Plus Jakarta Sans', system-ui, -apple-system, sans-serif;
  background: radial-gradient(circle at 50% 30%, #162a23 0%, #0f172a 80%);
  background-attachment: fixed;
  color: #f8fafc;
}

#app {
  min-height: 100%;
}

.app-container {
  min-height: 100vh;
  box-sizing: border-box;
}
</style>
