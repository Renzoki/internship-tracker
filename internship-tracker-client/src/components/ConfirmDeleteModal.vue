<script setup>
defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  companyName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['close', 'confirm'])

function handleClose() {
  emit('close')
}

function handleConfirm() {
  emit('confirm')
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-backdrop" @click.self="handleClose">
        <div class="modal-card">
          <span class="tag">Internship tracker</span>
          <h2>Delete application?</h2>

          <p class="modal-message">
            <template v-if="companyName">
              This will permanently remove your <strong>{{ companyName }}</strong> application. This can't be undone.
            </template>
            <template v-else>
              This will permanently remove this application. This can't be undone.
            </template>
          </p>

          <div class="modal-actions">
            <button type="button" @click="handleClose" class="btn-cancel">Cancel</button>
            <button type="button" @click="handleConfirm" class="btn-delete-confirm">Delete</button>
          </div>
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
  background: var(--bg-surface);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-soft);
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 420px;
}

.tag {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.65rem;
  letter-spacing: 2px;
  color: var(--tag-color);
  font-weight: 700;
  text-transform: uppercase;
}

.modal-card h2 {
  color: var(--text-primary);
  margin: 0.5rem 0 1rem 0;
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.modal-message {
  color: var(--text-secondary);
  font-size: 0.88rem;
  line-height: 1.5;
  margin: 0 0 1.75rem 0;
}

.modal-message strong {
  color: var(--text-primary);
  font-weight: 600;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
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

.btn-delete-confirm {
  background-color: var(--status-red);
  color: #ffffff;
  border: none;
  padding: 0.7rem 1.2rem;
  border-radius: 8px;
  font-family: inherit;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 0.3px;
  cursor: pointer;
  transition: opacity 0.15s ease;
}

.btn-delete-confirm:hover {
  opacity: 0.85;
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
