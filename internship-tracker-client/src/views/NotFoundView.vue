<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goBack() {
  router.back()
}
</script>

<template>
  <div class="wrapper">
    <header class="brand-hero">
      <span class="brand-badge">404 ERROR</span>
      <h1 class="main-title">Page Not <span class="accent">Found</span></h1>
      <p class="subtitle">The page you are looking for doesn't exist or has been moved.</p>
    </header>

    <button @click="goBack" class="btn-box-vue">GO BACK</button>
  </div>
</template>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 600px;
  width: 100%;
  margin: 0 auto;
  text-align: center;
}

.brand-hero {
  margin-bottom: 2rem;
}

.brand-badge {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.7rem;
  letter-spacing: 2px;
  color: #42b883;
  font-weight: 700;
  display: inline-block;
  margin-bottom: 0.75rem;
}

.main-title {
  font-size: 3.5rem;
  font-weight: 800;
  color: #ffffff;
  margin: 0;
  letter-spacing: -1.5px;
  line-height: 1.1;
}

.main-title .accent {
  color: #42b883;
}

.subtitle {
  font-size: 0.95rem;
  color: #94a3b8;
  margin-top: 1rem;
  font-weight: 500;
}

.btn-box-vue {
  background-color: #42b883;
  color: #0f172a;
  padding: 0.85rem 2rem;
  border-radius: 8px;
  border: 1px solid transparent;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
  width: 100%;
  max-width: 320px;
  box-sizing: border-box;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-box-vue:hover {
  background-color: #33a06f;
  transform: translateY(-1px);
}
</style>
