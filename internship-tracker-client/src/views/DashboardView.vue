<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import ApplicationCard from '../components/ApplicationCard.vue'

const router = useRouter()

const applications = ref([])
const isLoading = ref(true)
const errorMessage = ref('')
const userName = ref('')

function loadUserProfile() {
  const token = localStorage.getItem('jwt_token')
  if (!token) return

  try {
    const payloadBase64 = token.split('.')[1]
    const decodedJson = atob(payloadBase64)
    const decoded = JSON.parse(decodedJson)

    const fullName =
      decoded.name ||
      decoded.given_name ||
      decoded.first_name ||
      (decoded.email ? decoded.email.split('@')[0] : null)

    userName.value = fullName || 'User Account'
  } catch (e) {
    userName.value = 'User Account'
  }
}

const userInitial = computed(() => {
  return userName.value ? userName.value.charAt(0).toUpperCase() : 'U'
})

function getAuthHeaders() {
  const token = localStorage.getItem('jwt_token')
  if (!token) {
    router.push('/login')
    return null
  }
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }
}

async function fetchApplications() {
  isLoading.value = true
  errorMessage.value = ''

  const headers = getAuthHeaders()
  if (!headers) return

  try {
    const response = await fetch('http://localhost:8080/applications/self', { headers })

    if (response.ok) {
      applications.value = await response.json()
    } else if (response.status === 401 || response.status === 403) {
      localStorage.removeItem('jwt_token')
      router.push('/login')
    } else {
      errorMessage.value = 'Failed to load applications.'
    }
  } catch (err) {
    errorMessage.value = 'Unable to reach the server. Make sure Spring Boot is running.'
  } finally {
    isLoading.value = false
  }
}

async function handleStatusChange({ id, newStatus }) {
  const headers = getAuthHeaders()
  if (!headers) return

  try {
    const response = await fetch(`http://localhost:8080/applications/${id}/status`, {
      method: 'PATCH',
      headers,
      body: JSON.stringify({ status: newStatus })
    })

    if (response.ok) {
      const updatedApp = await response.json()
      const index = applications.value.findIndex(a => a.id === id)
      if (index !== -1) {
        applications.value[index] = updatedApp
      }
    } else {
      alert('Failed to update status.')
    }
  } catch (err) {
    console.error(err)
  }
}

async function handleDelete(id) {
  if (!confirm('Are you sure you want to delete this application?')) return

  const headers = getAuthHeaders()
  if (!headers) return

  try {
    const response = await fetch(`http://localhost:8080/applications/${id}`, {
      method: 'DELETE',
      headers
    })

    if (response.ok) {
      applications.value = applications.value.filter(a => a.id !== id)
    } else {
      alert('Failed to delete application.')
    }
  } catch (err) {
    console.error(err)
  }
}

function handleLogout() {
  localStorage.removeItem('jwt_token')
  router.push('/login')
}

onMounted(() => {
  loadUserProfile()
  fetchApplications()
})
</script>

<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="sidebar-top">
        <h1 class="brand-wordmark">Next<span class="accent">Step</span></h1>

        <nav class="side-nav">
          <a class="nav-item is-active" href="#">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <rect x="3" y="7" width="18" height="13" rx="2" />
              <path d="M8 7V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
              <path d="M3 12h18" />
            </svg>
            Applications
          </a>
        </nav>
      </div>

      <div class="sidebar-bottom">
        <div class="account-row">
          <div class="avatar-circle">{{ userInitial }}</div>
          <span class="account-name">{{ userName }}</span>
        </div>
        <button @click="handleLogout" class="btn-logout">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <path d="M16 17l5-5-5-5" />
            <path d="M21 12H9" />
          </svg>
          Log out
        </button>
      </div>
    </aside>

    <main class="content">
      <div v-if="errorMessage" class="error-banner">
        {{ errorMessage }}
      </div>

      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading your applications…</p>
      </div>

      <div v-else-if="applications.length > 0" class="table-wrapper">
        <div class="table-section-title">Applications</div>

        <div class="table-header">
          <div class="th col-company">Company & role</div>
          <div class="th col-location">Location</div>
          <div class="th col-date">Applied</div>
          <div class="th col-status">Status</div>
          <div class="th col-link">Posting</div>
          <div class="th col-actions"></div>
        </div>

        <div class="table-body">
          <TransitionGroup name="table-row">
            <ApplicationCard
              v-for="app in applications"
              :key="app.id"
              :application="app"
              @status-change="handleStatusChange"
              @delete="handleDelete"
            />
          </TransitionGroup>
        </div>
      </div>

      <div v-else class="empty-state">
        <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="3" y="7" width="18" height="13" rx="2" />
          <path d="M8 7V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
          <path d="M3 12h18" />
        </svg>
        <p class="empty-title">Nothing tracked yet</p>
        <p class="empty-sub">Applications you add will show up here, sorted and ready to update.</p>
      </div>
    </main>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.shell {
  display: flex;
  min-height: 100vh;
  width: 100%;
  font-family: 'Plus Jakarta Sans', -apple-system, sans-serif;
}

/* Sidebar */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 248px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.02);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  padding: 1.75rem 1.25rem;
}

.sidebar-top {
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

.brand-wordmark {
  font-size: 1.35rem;
  font-weight: 800;
  color: #f8fafc;
  margin: 0;
  padding-left: 0.5rem;
  letter-spacing: -0.4px;
}

.brand-wordmark .accent {
  color: #d4a24c;
}

.side-nav {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  padding: 0.65rem 0.75rem;
  border-radius: 8px;
  color: #94a3b8;
  font-size: 0.88rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.15s ease;
}

.nav-item svg {
  width: 17px;
  height: 17px;
  flex-shrink: 0;
}

.nav-item:hover {
  color: #f1f5f9;
  background: rgba(255, 255, 255, 0.04);
}

.nav-item.is-active {
  color: #f8fafc;
  background: rgba(212, 162, 76, 0.1);
  box-shadow: inset 2px 0 0 #d4a24c;
}

.sidebar-bottom {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding-top: 1.25rem;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.account-row {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding: 0 0.5rem;
}

.avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(212, 162, 76, 0.15);
  border: 1px solid rgba(212, 162, 76, 0.4);
  color: #d4a24c;
  font-weight: 700;
  font-size: 0.82rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.account-name {
  color: #e2e8f0;
  font-size: 0.85rem;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-logout {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: transparent;
  color: #94a3b8;
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 0.6rem 0.75rem;
  border-radius: 8px;
  font-size: 0.83rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
  font-family: inherit;
}

.btn-logout svg {
  width: 15px;
  height: 15px;
}

.btn-logout:hover {
  color: #f8fafc;
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.03);
}

/* Main content */
.content {
  flex: 1;
  margin-left: 248px;
  padding: 3rem 3rem 3.5rem;
  max-width: 1100px;
}

.table-wrapper {
  display: flex;
  flex-direction: column;
}

.table-section-title {
  color: #f8fafc;
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  letter-spacing: -0.3px;
}

.table-header {
  display: grid;
  grid-template-columns: 2.2fr 1.6fr 1.1fr 1.3fr 1fr 1.4fr;
  gap: 1rem;
  padding: 0 1.25rem 0.7rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 600;
}

.table-body {
  display: flex;
  flex-direction: column;
}

.error-banner {
  background-color: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.4);
  color: #fca5a5;
  padding: 0.8rem 1.1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  margin-bottom: 1.5rem;
}

.loading-state {
  text-align: center;
  padding: 5rem 0;
  color: #94a3b8;
  font-size: 0.9rem;
}

.spinner {
  width: 28px;
  height: 28px;
  border: 3px solid rgba(212, 162, 76, 0.2);
  border-top-color: #d4a24c;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state {
  padding: 4.5rem 2rem;
  text-align: center;
}

.empty-icon {
  width: 40px;
  height: 40px;
  color: #475569;
  margin-bottom: 1rem;
}

.empty-title {
  color: #f8fafc;
  font-size: 1.05rem;
  font-weight: 600;
  margin: 0 0 0.4rem 0;
}

.empty-sub {
  color: #94a3b8;
  font-size: 0.85rem;
  margin: 0;
  max-width: 320px;
  margin-inline: auto;
}

.table-row-enter-active,
.table-row-leave-active {
  transition: all 0.2s ease;
}

.table-row-enter-from,
.table-row-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}

@media (max-width: 720px) {
  .sidebar {
    width: 100%;
    height: auto;
    position: static;
    flex-direction: row;
    align-items: center;
  }
  .content {
    margin-left: 0;
    padding: 2rem 1.25rem;
  }
}
</style>
