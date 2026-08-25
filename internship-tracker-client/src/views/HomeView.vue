<script setup>
import { ref, onMounted } from 'vue'

const users = ref([])

onMounted(async () => {
  const res = await fetch('http://localhost:8080/users')
  users.value = await res.json()
})
</script>

<template>
  <div class="wrapper">
    <header class="brand-hero">
      <span class="brand-badge">INTERNSHIP TRACKER</span>
      <h1 class="main-title">Next<span class="accent">Step</span></h1>
      <p class="subtitle">Track applications. Stay organized. Get hired.</p>
    </header>

    <div class="card">
      <div class="actions">
        <router-link to="/signup" class="btn-box-vue">CREATE AN ACCOUNT</router-link>
        <button class="btn-text-disabled" disabled>LOG IN (COMING SOON)</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 600px;
  width: 100%;
  text-align: center;
}

.brand-hero {
  margin-bottom: 2.5rem;
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
  font-size: 4.5rem;
  font-weight: 800;
  color: #ffffff;
  margin: 0;
  letter-spacing: -1.5px;
  line-height: 1;
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

.card {
  background: rgba(30, 41, 59, 0.6);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  width: 100%;
  padding: 2.5rem;
  box-sizing: border-box;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
}

.btn-box-vue {
  background-color: #42b883;
  color: #0f172a;
  padding: 0.85rem 2rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
  width: 100%;
  max-width: 320px;
  box-sizing: border-box;
  transition: all 0.15s ease;
  border: 1px solid transparent;
}

.btn-box-vue:hover {
  background-color: #33a06f;
  transform: translateY(-1px);
}

.btn-text-disabled {
  background: none;
  border: none;
  color: #64748b;
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.75rem;
  letter-spacing: 1px;
  font-weight: 500;
  cursor: not-allowed;
  margin-top: 0.5rem;
}
</style>
