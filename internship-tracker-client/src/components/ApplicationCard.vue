<script setup>
import { computed } from 'vue'
import StatusDropdown from './StatusDropdown.vue'

const props = defineProps({
  application: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['status-change', 'delete'])

function handleStatusChange(newStatus) {
  emit('status-change', {
    id: props.application.id,
    newStatus
  })
}

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
  if (status === 'IN_PROGRESS' || status.includes('INTERVIEW')) return 'status-in-progress'
  if (status === 'WAITING_TO_HEAR_BACK') return 'status-waiting'
  if (status === 'HIRED' || status.includes('OFFER')) return 'status-hired'
  if (status === 'REJECTED' || status.includes('REJECT')) return 'status-rejected'
  return 'status-applied'
})

const formatWorkMode = (mode) => {
  if (!mode) return ''
  return mode.replace('_', ' ').toLowerCase().replace(/^\w/, c => c.toUpperCase())
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

    <div class="col col-status">
      <StatusDropdown
        :status="application.status"
        @change="handleStatusChange"
      />
    </div>

    <div class="col col-actions">
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
  grid-template-columns: 2.2fr 1.6fr 1.1fr 1fr 1.3fr 40px;
  align-items: center;
  gap: 1rem;
  padding: 1.05rem 1.25rem 1.05rem 1.5rem;
  border-bottom: 1px solid var(--border-softer);
  transition: background 0.15s ease;
}

.table-row:hover {
  background: var(--border-softer);
}

.row-accent {
  position: absolute;
  left: 0;
  top: 0.6rem;
  bottom: 0.6rem;
  width: 3px;
  border-radius: 2px;
  background: currentColor;
  opacity: 0.6;
}

.status-applied .row-accent { color: var(--status-blue, #3b82f6); }
.status-in-progress .row-accent { color: var(--status-yellow, #eab308); }
.status-waiting .row-accent { color: #f97316; }
.status-hired .row-accent { color: #10b981; }
.status-rejected .row-accent { color: var(--status-red, #ef4444); }

.col {
  display: flex;
  align-items: center;
  min-width: 0;
}

.col-company {
  flex-direction: column;
  align-items: flex-start;
  gap: 0.15rem;
}

.company-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text-primary);
}

.position-title {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

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
  color: var(--text-primary);
}

.icon-pin {
  width: 13px;
  height: 13px;
  stroke: var(--text-muted);
  flex-shrink: 0;
}

.badge-workmode {
  background: var(--border-softer);
  border: 1px solid var(--border-soft);
  padding: 0.1rem 0.5rem;
  border-radius: 20px;
  font-size: 0.68rem;
  color: var(--text-secondary);
}

.col-date {
  font-size: 0.82rem;
  color: var(--text-secondary);
}

.link-external {
  color: var(--accent);
  text-decoration: none;
  font-size: 0.8rem;
  font-weight: 500;
  white-space: nowrap;
}

.link-external:hover {
  text-decoration: underline;
}

.no-link {
  color: var(--text-faint);
  font-size: 0.8rem;
}

.col-actions {
  justify-content: flex-end;
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
  color: var(--text-muted);
  transition: all 0.15s ease;
  flex-shrink: 0;
}

.btn-delete svg {
  width: 15px;
  height: 15px;
}

.btn-delete:hover {
  color: var(--status-red, #ef4444);
  background: color-mix(in srgb, var(--status-red, #ef4444) 10%, transparent);
}

@media (max-width: 880px) {
  .table-row {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.6rem;
    padding: 1.1rem 1.1rem 1.1rem 1.5rem;
  }

  .col {
    width: 100%;
    justify-content: flex-start;
  }

  .col-company {
    order: 1;
    padding-right: 2.5rem;
  }

  .col-status {
    order: 2;
  }

  .col-location {
    order: 3;
    flex-direction: row;
    align-items: center;
    gap: 0.6rem;
  }

  .col-date {
    order: 4;
    color: var(--text-muted);
  }

  .col-date::before {
    content: 'Applied: ';
    color: var(--text-faint);
    margin-right: 0.25rem;
  }

  .col-link {
    order: 5;
  }

  .col-actions {
    order: unset;
    position: absolute;
    top: 1rem;
    right: 1rem;
    width: auto;
    padding-top: 0;
    border-top: none;
  }
}

@media (max-width: 380px) {
  .table-row {
    padding: 1rem 0.85rem 1rem 1.25rem;
  }
}
</style>
