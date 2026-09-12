<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  application: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const form = ref({
  companyName: '',
  positionTitle: '',
  location: '',
  workMode: 'HYBRID',
  applicationUrl: ''
})
const formError = ref('')

watch(() => props.isOpen, (newVal) => {
  if (newVal && props.application) {
    form.value = {
      companyName: props.application.companyName || '',
      positionTitle: props.application.positionTitle || '',
      location: props.application.location || '',
      workMode: props.application.workMode || 'HYBRID',
      applicationUrl: props.application.applicationUrl || ''
    }
    formError.value = ''
  }
})

function handleClose() {
  emit('close')
}

function handleSubmit() {
  formError.value = ''

  if (!form.value.companyName.trim()) {
    formError.value = 'Company name cannot be blank.'
    return
  }
  if (!form.value.positionTitle.trim()) {
    formError.value = 'Position title cannot be blank.'
    return
  }
  if (!form.value.location.trim()) {
    formError.value = 'Location cannot be blank.'
    return
  }
  if (!form.value.applicationUrl.trim()) {
    formError.value = 'Application URL cannot be blank.'
    return
  }

  emit('save', { id: props.application.id, ...form.value })
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-backdrop" @click.self="handleClose">
        <div class="modal-card">
          <div class="modal-header">
            <span class="tag">Internship tracker</span>
            <h2>Edit Application</h2>
            <button @click="handleClose" class="btn-close" title="Close" aria-label="Close">&times;</button>
          </div>

          <form @submit.prevent="handleSubmit">
            <div v-if="formError" class="error-banner">
              {{ formError }}
            </div>

            <div class="input-group">
              <label>COMPANY NAME</label>
              <input
                v-model="form.companyName"
                type="text"
                maxlength="100"
                placeholder="e.g. Oracle"
                required
              />
            </div>

            <div class="input-group">
              <label>POSITION TITLE</label>
              <input
                v-model="form.positionTitle"
                type="text"
                maxlength="50"
                placeholder="e.g. Software Engineer Intern"
                required
              />
            </div>

            <div class="row">
              <div class="input-group">
                <label>LOCATION</label>
                <input
                  v-model="form.location"
                  type="text"
                  maxlength="150"
                  placeholder="e.g. Makati"
                  required
                />
              </div>

              <div class="input-group">
                <label>WORK MODE</label>
                <select v-model="form.workMode" required>
                  <option value="HYBRID">Hybrid</option>
                  <option value="REMOTE">Remote</option>
                  <option value="ONSITE">Onsite</option>
                </select>
              </div>
            </div>

            <div class="input-group">
              <label>APPLICATION URL</label>
              <input
                v-model="form.applicationUrl"
                type="url"
                placeholder="https://company.com/careers/job-123"
                required
              />
            </div>

            <div class="modal-actions">
              <button type="button" @click="handleClose" class="btn-cancel">Cancel</button>
              <button type="submit" class="btn-submit">Save changes</button>
            </div>
          </form>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1.5rem;
}

.modal-card {
  position: relative;
  background: var(--bg-surface);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-soft);
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  position: relative;
  margin-bottom: 1.5rem;
}

.tag {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 2px;
  color: var(--tag-color);
  font-weight: 700;
  text-transform: uppercase;
}

.modal-header h2 {
  color: var(--text-primary);
  margin: 0.5rem 0 0 0;
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.btn-close {
  position: absolute;
  top: -0.25rem;
  right: -0.25rem;
  background: transparent;
  border: none;
  font-size: 1.5rem;
  line-height: 1;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0.25rem;
}

.btn-close:hover {
  color: var(--text-primary);
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

form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.input-group label {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 1px;
  color: var(--text-secondary);
  font-weight: 500;
}

input,
select {
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

select {
  cursor: pointer;
}

input:focus,
select:focus {
  border-color: var(--accent);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 0.25rem;
}

.btn-cancel {
  background: transparent;
  border: 1px solid var(--border-soft);
  color: var(--text-secondary);
  padding: 0.7rem 1.1rem;
  border-radius: 8px;
  font-family: inherit;
  font-size: 0.83rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-cancel:hover {
  background: var(--border-softer);
  color: var(--text-primary);
}

.btn-submit {
  background-color: var(--accent);
  color: var(--accent-contrast);
  border: none;
  padding: 0.7rem 1.2rem;
  border-radius: 8px;
  font-family: inherit;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 0.3px;
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.btn-submit:hover {
  background-color: var(--accent-hover);
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.96);
}
</style>
