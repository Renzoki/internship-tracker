<script setup>
import { computed } from 'vue'

const props = defineProps({
  application: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['status-change', 'delete'])

const formattedDate = computed(() => {
  if (!props.application.dateApplied) return '—'
  const date = new Date(props.application.dateApplied)
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  }).format(date)
})

const statusClass = computed(() => {
  const status = props.application.status?.toUpperCase() || ''
  if (status.includes('OFFER')) return 'status-offered'
  if (status.includes('INTERVIEW')) return 'status-interview'
  if (status.includes('REJECT')) return 'status-rejected'
  return 'status-applied'
})

const formatWorkMode = (mode) => {
  if (!mode) return ''
  return mode.replace('_', ' ').toLowerCase().replace(/^\w/, c => c.toUpperCase())
}

function handleStatusChange(event) {
  emit('status-change', {
    id: props.application.id,
    newStatus: event.target.value
  })
}
</script>

<template>
  <div class="table-row" :class="statusClass">
    <div class="row-accent"></div>

    <div class="col col-company">
      <span class="company-name">{{ application.companyName }}</span>
      <span class="position-title">{{ application.positionTitle }}</span>
    </div>

    <div class="col col-location">
      <span class="location-text">
        <svg class="icon-pin" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
          <circle cx="12" cy="9" r="2.5"/>
        </svg>
        {{ application.location }}
      </span>
      <span v-if="application.workMode" class="badge-workmode">{{ formatWorkMode(application.workMode) }}</span>
    </div>

    <div class="col col-date">
      {{ formattedDate }}
    </div>

    <div class="col col-status">
      <div class="status-pill">
        <span class="status-dot"></span>
        {{ application.status }}
      </div>
    </div>

    <div class="col col-link">
      <a
        v-if="application.applicationUrl"
        :href="application.applicationUrl"
        target="_blank"
        rel="noopener noreferrer"
        class="link-external"
      >
        View posting
      </a>
      <span v-else class="no-link">—</span>
    </div>

    <div class="col col-actions">
      <select
        :value="application.status"
        @change="handleStatusChange"
        class="status-select"
      >
        <option value="APPLIED">Applied</option>
        <option value="INTERVIEWING">Interviewing</option>
        <option value="OFFERED">Offered</option>
        <option value="REJECTED">Rejected</option>
      </select>

      <button @click="emit('delete', application.id)" class="btn-delete" title="Delete application" aria-label="Delete application">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 6h18"/>
          <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
          <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
          <path d="M10 11v6M14 11v6"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<style scoped>
.table-row {
  position: relative;
  display: grid;
  grid-template-columns: 2.2fr 1.6fr 1.1fr 1.3fr 1fr 1.4fr;
  align-items: center;
  gap: 1rem;
  padding: 1.05rem 1.25rem 1.05rem 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: background 0.15s ease;
}

.table-row:hover {
  background: rgba(255, 255, 255, 0.02);
}

.row-accent {
  position: absolute;
  left: 0;
  top: 0.6rem;
  bottom: 0.6rem;
  width: 3px;
  border-radius: 2px;
  background: currentColor;
  opacity: 0.5;
}

.status-applied .row-accent { color: #60a5fa; }
.status-interview .row-accent { color: #fbbf24; }
.status-offered .row-accent { color: #34d399; }
.status-rejected .row-accent { color: #f87171; }

.col {
  display: flex;
  align-items: center;
  min-width: 0;
}

/* Company & role */
.col-company {
  flex-direction: column;
  align-items: flex-start;
  gap: 0.15rem;
}

.company-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #ffffff;
}

.position-title {
  font-size: 0.8rem;
  color: #94a3b8;
}

/* Location */
.col-location {
  flex-direction: column;
  align-items: flex-start;
  gap: 0.3rem;
}

.location-text {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.82rem;
  color: #cbd5e1;
}

.icon-pin {
  width: 13px;
  height: 13px;
  stroke: #64748b;
  flex-shrink: 0;
}

.badge-workmode {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 0.1rem 0.5rem;
  border-radius: 20px;
  font-size: 0.68rem;
  color: #94a3b8;
}

/* Date */
.col-date {
  font-size: 0.82rem;
  color: #94a3b8;
}

/* Status */
.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.72rem;
  font-weight: 600;
  padding: 0.3rem 0.7rem;
  border-radius: 20px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: currentColor;
}

.status-applied .status-pill {
  background: rgba(96, 165, 250, 0.12);
  color: #60a5fa;
}

.status-interview .status-pill {
  background: rgba(251, 191, 36, 0.12);
  color: #fbbf24;
}

.status-offered .status-pill {
  background: rgba(52, 211, 153, 0.12);
  color: #34d399;
}

.status-rejected .status-pill {
  background: rgba(248, 113, 113, 0.12);
  color: #f87171;
}

.link-external {
  color: #d4a24c;
  text-decoration: none;
  font-size: 0.8rem;
  font-weight: 500;
}

.link-external:hover {
  text-decoration: underline;
}

.no-link {
  color: #475569;
  font-size: 0.8rem;
}

/* Actions */
.col-actions {
  gap: 0.6rem;
  justify-content: flex-end;
}

.status-select {
  background-color: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
  padding: 0.4rem 0.55rem;
  border-radius: 6px;
  font-size: 0.75rem;
  outline: none;
  cursor: pointer;
  transition: border-color 0.15s ease;
}

.status-select:hover,
.status-select:focus {
  border-color: rgba(66, 184, 131, 0.5);
}

.btn-delete {
  background: transparent;
  border: none;
  cursor: pointer;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  transition: all 0.15s ease;
  flex-shrink: 0;
}

.btn-delete svg {
  width: 15px;
  height: 15px;
}

.btn-delete:hover {
  color: #f87171;
  background: rgba(248, 113, 113, 0.1);
}
</style>
