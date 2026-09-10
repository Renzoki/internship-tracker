<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  status: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['change'])

const isOpen = ref(false)
const dropdownRef = ref(null)

const statusOptions = [
  { value: 'APPLIED', label: 'Applied' },
  { value: 'IN_PROGRESS', label: 'In Progress' },
  { value: 'WAITING_TO_HEAR_BACK', label: 'Waiting to Hear Back' },
  { value: 'HIRED', label: 'Hired' },
  { value: 'REJECTED', label: 'Rejected' }
]

const availableStatusOptions = computed(() => {
  return statusOptions.filter(option => option.value !== props.status)
})

const currentStatusLabel = computed(() => {
  const matched = statusOptions.find(o => o.value === props.status)
  if (matched) return matched.label
  return props.status ? props.status.replace(/_/g, ' ') : 'Select Status'
})

function toggleDropdown() {
  isOpen.value = !isOpen.value
}

function selectStatus(statusValue) {
  emit('change', statusValue)
  isOpen.value = false
}

function handleClickOutside(event) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    isOpen.value = false
  }
}

onMounted(() => {
  window.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="custom-select-wrapper" ref="dropdownRef">
    <button
      type="button"
      class="custom-select-trigger"
      @click="toggleDropdown"
      :aria-expanded="isOpen"
      title="Change status"
    >
      <span class="trigger-label">{{ currentStatusLabel }}</span>
      <svg class="chevron-icon" :class="{ 'is-open': isOpen }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M6 9l6 6 6-6"/>
      </svg>
    </button>

    <Transition name="dropdown-anim">
      <ul v-if="isOpen && availableStatusOptions.length > 0" class="custom-select-menu">
        <li
          v-for="option in availableStatusOptions"
          :key="option.value"
          class="option-item"
          @click="selectStatus(option.value)"
        >
          <span class="option-dot" :class="`dot-${option.value.toLowerCase()}`"></span>
          {{ option.label }}
        </li>
      </ul>
    </Transition>
  </div>
</template>

<style scoped>
.custom-select-wrapper {
  position: relative;
  display: inline-block;
}

.custom-select-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.4rem;
  background-color: var(--border-softer);
  border: 1px solid var(--border-soft);
  color: var(--text-primary);
  padding: 0.32rem 0.6rem;
  border-radius: 6px;
  font-size: 0.76rem;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.15s ease;
  width: 125px;
}

.custom-select-trigger:hover {
  border-color: var(--accent);
  background-color: var(--border-soft);
}

.trigger-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chevron-icon {
  width: 12px;
  height: 12px;
  stroke: var(--text-muted);
  transition: transform 0.2s ease;
  flex-shrink: 0;
}

.chevron-icon.is-open {
  transform: rotate(180deg);
}

.custom-select-menu {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  min-width: 160px;
  max-height: 220px;
  overflow-y: auto;
  background-color: color-mix(in srgb, var(--border-softer) 95%, black);
  backdrop-filter: blur(8px);
  border: 1px solid var(--border-soft);
  border-radius: 6px;
  padding: 0.25rem;
  margin: 0;
  list-style: none;
  box-shadow: 0 8px 20px -4px rgba(0, 0, 0, 0.4);
  z-index: 50;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 0.45rem;
  padding: 0.35rem 0.55rem;
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-primary);
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.12s ease, color 0.12s ease;
}

.option-item:hover {
  background: var(--border-soft);
  color: var(--accent);
}

.option-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot-applied { background-color: var(--status-blue, #3b82f6); }
.dot-in_progress { background-color: var(--status-yellow, #eab308); }
.dot-waiting_to_hear_back { background-color: #f97316; }
.dot-hired { background-color: #10b981; }
.dot-rejected { background-color: var(--status-red, #ef4444); }

.dropdown-anim-enter-active,
.dropdown-anim-leave-active {
  transition: opacity 0.12s ease, transform 0.12s ease;
}

.dropdown-anim-enter-from,
.dropdown-anim-leave-to {
  opacity: 0;
  transform: translateY(-3px);
}
</style>
