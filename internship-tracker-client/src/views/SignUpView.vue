<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const firstName = ref('')
const lastName = ref('')
const email = ref('')
const password = ref('')
const errorMessage = ref('')

async function handleSignUp() {
  errorMessage.value = ''

  try {
    const response = await fetch('http://localhost:8080/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        password: password.value
      })
    })

    if (response.ok) {
      console.log(`%c HTTP ${response.status} ${response.statusText}`, 'color: #42b883; font-weight: bold;')

      const createdUser = await response.json()
      console.log('User created successfully:', createdUser)

      router.push('/')
    } else if (response.status === 409) {
      errorMessage.value = 'An account with this email address already exists.'
    } else {
      console.warn(`Server responded with HTTP Status: ${response.status}`)
      const data = await response.json().catch(() => null)
      errorMessage.value = data?.message || 'Registration failed. Please check your inputs.'
    }
  } catch (err) {
    console.error('Request failed:', err)
    errorMessage.value = 'Unable to reach the server. Make sure Spring Boot is running.'
  }
}
</script>

<template>
  <div class="card-form">
    <div class="header">
      <span class="tag">INTERNSHIP TRACKER</span>
      <h2>Create Account</h2>
    </div>

    <div v-if="errorMessage" class="error-banner">
      {{ errorMessage }}
    </div>

    <form @submit.prevent="handleSignUp">
      <div class="row">
        <div class="input-group">
          <label>FIRST NAME</label>
          <input
            v-model="firstName"
            placeholder="Alex"
            maxlength="50"
            required
          />
        </div>

        <div class="input-group">
          <label>LAST NAME</label>
          <input
            v-model="lastName"
            placeholder="Morgan"
            maxlength="50"
            required
          />
        </div>
      </div>

      <div class="input-group">
        <label>EMAIL ADDRESS</label>
        <input
          v-model="email"
          type="email"
          placeholder="alex@example.com"
          maxlength="120"
          required
        />
      </div>

      <div class="input-group">
        <label>PASSWORD</label>
        <input
          v-model="password"
          type="password"
          placeholder="Min. 8 characters"
          minlength="8"
          required
        />
      </div>

      <button type="submit" class="btn-submit">REGISTER</button>
    </form>

    <router-link to="/" class="back-link">← Cancel and return home</router-link>
  </div>
</template>

<style scoped>
.card-form {
  background: var(--bg-surface);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-soft);
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 440px;
}

.tag {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 2px;
  color: var(--tag-color);
  font-weight: 700;
}

h2 {
  color: var(--text-primary);
  margin: 0.5rem 0 1.5rem 0;
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.error-banner {
  background-color: var(--error-bg);
  border: 1px solid var(--error-border);
  color: var(--error-text);
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-size: 0.8rem;
  margin-bottom: 1.25rem;
}

form { display: flex; flex-direction: column; gap: 1.25rem; }

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.input-group { display: flex; flex-direction: column; gap: 0.4rem; }
.input-group label {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 1px;
  color: var(--text-secondary);
  font-weight: 500;
}

input {
  background-color: var(--bg-grad-2);
  border: 1px solid var(--border-soft);
  color: var(--text-primary);
  padding: 0.8rem 1rem;
  border-radius: 8px;
  font-family: inherit;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.15s ease;
  width: 100%;
  box-sizing: border-box;
}
input:focus { border-color: var(--accent); }

.btn-submit {
  margin-top: 0.5rem;
  background-color: var(--accent);
  color: var(--accent-contrast);
  border: none;
  padding: 0.85rem;
  border-radius: 8px;
  font-family: inherit;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 0.5px;
  cursor: pointer;
  transition: background-color 0.15s ease;
}
.btn-submit:hover { background-color: var(--accent-hover); }

.back-link {
  display: block;
  text-align: center;
  margin-top: 1.5rem;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.8rem;
  font-weight: 500;
}
.back-link:hover { color: var(--text-primary); }
</style>
