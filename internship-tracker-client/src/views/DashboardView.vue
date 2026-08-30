<script setup>
</script>

<template>
  <div class="dashboard-container">
    <p class="message">Applications to be added soon</p>
    <router-link to="/" class="btn-home">← Return Home</router-link>
  </div>
</template>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
  text-align: center;
}

.message {
  color: #f8fafc;
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0;
}

.btn-home {
  background-color: transparent;
  color: #42b883;
  padding: 0.75rem 1.75rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
  transition: all 0.15s ease;
  border: 1px solid #42b883;
}

.btn-home:hover {
  background-color: rgba(66, 184, 131, 0.1);
  transform: translateY(-1px);
}
</style>
