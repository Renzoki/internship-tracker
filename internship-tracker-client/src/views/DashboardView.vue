<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import ApplicationCard from '../components/ApplicationCard.vue'
import { getStoredTheme, applyTheme, nextTheme } from '../theme.js'

const router = useRouter()

const applications = ref([])
const isLoading = ref(true)
const errorMessage = ref('')
const userName = ref('')
const currentTheme = ref(getStoredTheme())

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

function handleThemeToggle() {
  const next = nextTheme(currentTheme.value)
  applyTheme(next)
  currentTheme.value = next
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
          <a class="nav-item is-active" href="#" title="Applications">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <rect x="3" y="7" width="18" height="13" rx="2" />
              <path d="M8 7V5a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
              <path d="M3 12h18" />
            </svg>
            <span>Applications</span>
          </a>
        </nav>
      </div>

      <div class="sidebar-bottom">
        <div class="account-row">
          <div class="avatar-circle">{{ userInitial }}</div>
          <span class="account-name">{{ userName }}</span>
        </div>

        <button @click="handleThemeToggle" class="btn-theme" title="Change theme">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <circle cx="12" cy="12" r="9" />
            <path d="M12 3a6 6 0 0 0 0 12 6 6 0 0 1 0 6 9 9 0 0 1 0-18z" fill="currentColor" stroke="none" />
          </svg>
          <span>Change theme</span>
        </button>

        <button @click="handleLogout" class="btn-logout" title="Log out">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <path d="M16 17l5-5-5-5" />
            <path d="M21 12H9" />
          </svg>
          <span>Log out</span>
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

.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: 248px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: var(--border-softer);
  border-right: 1px solid var(--border-softer);
  padding: 1.75rem 1.25rem;
  transition: width 0.15s ease, padding 0.15s ease;
}

.sidebar-top {
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

.brand-wordmark {
  font-size: 1.35rem;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
  padding-left: 0.5rem;
  letter-spacing: -0.4px;
  white-space: nowrap;
  overflow: hidden;
}

.brand-wordmark .accent {
  color: var(--accent);
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
  color: var(--text-secondary);
  font-size: 0.88rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.15s ease;
  white-space: nowrap;
}

.nav-item svg {
  width: 17px;
  height: 17px;
  flex-shrink: 0;
}

.nav-item:hover {
  color: var(--text-primary);
  background: var(--border-softer);
}

.nav-item.is-active {
  color: var(--text-primary);
  background: var(--accent-soft);
  box-shadow: inset 2px 0 0 var(--accent);
}

.sidebar-bottom {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  padding-top: 1.25rem;
  border-top: 1px solid var(--border-softer);
}

.account-row {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding: 0 0.5rem;
  margin-bottom: 0.4rem;
}

.avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--accent-soft-strong);
  border: 1px solid var(--accent-soft-strong);
  color: var(--accent);
  font-weight: 700;
  font-size: 0.82rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.account-name {
  color: var(--text-primary);
  font-size: 0.85rem;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-theme,
.btn-logout {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-soft);
  padding: 0.6rem 0.75rem;
  border-radius: 8px;
  font-size: 0.83rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
  font-family: inherit;
  white-space: nowrap;
}

.btn-theme svg,
.btn-logout svg {
  width: 15px;
  height: 15px;
  flex-shrink: 0;
}

.btn-theme:hover,
.btn-logout:hover {
  color: var(--text-primary);
  border-color: var(--border-soft);
  background: var(--border-softer);
}

.content {
  flex: 1;
  margin-left: 248px;
  padding: 3rem 3rem 3.5rem;
  max-width: 1100px;
  transition: margin-left 0.15s ease;
}

.table-wrapper {
  display: flex;
  flex-direction: column;
}

.table-section-title {
  color: var(--text-primary);
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
  border-bottom: 1px solid var(--border-soft);
  font-size: 0.75rem;
  color: var(--text-muted);
  font-weight: 600;
}

.table-body {
  display: flex;
  flex-direction: column;
}

.error-banner {
  background-color: var(--error-bg);
  border: 1px solid var(--error-border);
  color: var(--error-text);
  padding: 0.8rem 1.1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  margin-bottom: 1.5rem;
}

.loading-state {
  text-align: center;
  padding: 5rem 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.spinner {
  width: 28px;
  height: 28px;
  border: 3px solid var(--accent-soft);
  border-top-color: var(--accent);
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
  color: var(--text-faint);
  margin-bottom: 1rem;
}

.empty-title {
  color: var(--text-primary);
  font-size: 1.05rem;
  font-weight: 600;
  margin: 0 0 0.4rem 0;
}

.empty-sub {
  color: var(--text-secondary);
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

@media (max-width: 1000px) {
  .sidebar {
    width: 84px;
    align-items: center;
    padding: 1.75rem 0.75rem;
  }

  .brand-wordmark {
    font-size: 0;
  }

  .brand-wordmark::before {
    content: 'N';
    font-size: 1.35rem;
  }

  .sidebar-top {
    align-items: center;
  }

  .nav-item {
    justify-content: center;
    padding: 0.65rem;
    width: 100%;
  }

  .nav-item span {
    display: none;
  }

  .sidebar-bottom {
    align-items: center;
  }

  .account-row {
    justify-content: center;
    padding: 0;
  }

  .account-name {
    display: none;
  }

  .btn-theme,
  .btn-logout {
    justify-content: center;
    padding: 0.6rem;
    width: 100%;
  }

  .btn-theme span,
  .btn-logout span {
    display: none;
  }

  .content {
    margin-left: 84px;
  }
}

@media (max-width: 1100px) {
  .shell {
    flex-direction: column;
  }

  .sidebar {
    position: static;
    width: 100%;
    height: auto;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0.85rem 1.25rem;
    gap: 1rem;
  }

  .brand-wordmark {
    font-size: 1.35rem;
  }

  .brand-wordmark::before {
    content: '';
  }

  .sidebar-top {
    flex-direction: row;
    align-items: center;
    gap: 1.5rem;
  }

  .side-nav {
    flex-direction: row;
  }

  .nav-item {
    width: auto;
    padding: 0.55rem;
  }

  .sidebar-bottom {
    flex-direction: row;
    align-items: center;
    gap: 0.5rem;
    padding-top: 0;
    border-top: none;
  }

  .account-row {
    margin-bottom: 0;
  }

  .btn-theme,
  .btn-logout {
    width: auto;
    padding: 0.55rem;
  }

  .content {
    margin-left: 0;
    max-width: 100%;
    padding: 2rem 1.5rem;
  }

  .table-header {
    display: none;
  }
}

@media (max-width: 480px) {
  .sidebar {
    padding: 0.75rem 1rem;
  }

  .content {
    padding: 1.5rem 1rem;
  }
}
</style>
