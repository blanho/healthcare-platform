

import type { AppointmentStatus, AppointmentType } from '@/types';

export const APPOINTMENT_STATUS_LABELS: Record<AppointmentStatus, string> = {
  SCHEDULED: 'Scheduled',
  CONFIRMED: 'Confirmed',
  CHECKED_IN: 'Checked In',
  IN_PROGRESS: 'In Progress',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
  NO_SHOW: 'No Show',
  RESCHEDULED: 'Rescheduled',
};

export const APPOINTMENT_STATUS_OPTIONS = Object.entries(APPOINTMENT_STATUS_LABELS).map(
  ([value, label]) => ({
    value: value as AppointmentStatus,
    label,
  })
);

export const APPOINTMENT_STATUS_COLORS: Record<
  AppointmentStatus,
  'success' | 'warning' | 'error' | 'info' | 'default' | 'primary' | 'secondary'
> = {
  SCHEDULED: 'info',
  CONFIRMED: 'primary',
  CHECKED_IN: 'success',
  IN_PROGRESS: 'warning',
  COMPLETED: 'success',
  CANCELLED: 'error',
  NO_SHOW: 'error',
  RESCHEDULED: 'secondary',
};

export const APPOINTMENT_TYPE_LABELS: Record<AppointmentType, string> = {
  CONSULTATION: 'Consultation',
  FOLLOW_UP: 'Follow Up',
  CHECKUP: 'Checkup',
  EMERGENCY: 'Emergency',
  SURGERY: 'Surgery',
  LAB_TEST: 'Lab Test',
  IMAGING: 'Imaging',
  VACCINATION: 'Vaccination',
  PHYSICAL_THERAPY: 'Physical Therapy',
  MENTAL_HEALTH: 'Mental Health',
  DENTAL: 'Dental',
  TELEMEDICINE: 'Telemedicine',
  OTHER: 'Other',
};

export const APPOINTMENT_TYPE_OPTIONS = Object.entries(APPOINTMENT_TYPE_LABELS).map(
  ([value, label]) => ({
    value: value as AppointmentType,
    label,
  })
);

export const APPOINTMENT_TYPE_COLORS: Record<
  AppointmentType,
  'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info' | 'default'
> = {
  CONSULTATION: 'primary',
  FOLLOW_UP: 'info',
  CHECKUP: 'success',
  EMERGENCY: 'error',
  SURGERY: 'error',
  LAB_TEST: 'secondary',
  IMAGING: 'secondary',
  VACCINATION: 'success',
  PHYSICAL_THERAPY: 'info',
  MENTAL_HEALTH: 'primary',
  DENTAL: 'info',
  TELEMEDICINE: 'primary',
  OTHER: 'default',
};

export const APPOINTMENT_DURATION_OPTIONS = [
  { value: 15, label: '15 minutes' },
  { value: 30, label: '30 minutes' },
  { value: 45, label: '45 minutes' },
  { value: 60, label: '1 hour' },
  { value: 90, label: '1.5 hours' },
  { value: 120, label: '2 hours' },
] as const;

export const DEFAULT_APPOINTMENT_DURATION = 30;

export const TIME_SLOT_CONFIG = {
  START_HOUR: 8,
  END_HOUR: 18,
  SLOT_INTERVAL_MINUTES: 15,
} as const;

export const APPOINTMENT_LIST_DEFAULTS = {
  PAGE_SIZE: 10,
  SORT_BY: 'scheduledDate',
  SORT_DIRECTION: 'asc' as const,
} as const;

export const APPOINTMENT_VALIDATION = {
  REASON_MAX_LENGTH: 500,
  NOTES_MAX_LENGTH: 2000,
  CANCELLATION_REASON_MAX_LENGTH: 500,
  MIN_DURATION_MINUTES: 15,
  MAX_DURATION_MINUTES: 480,
  MAX_ADVANCE_BOOKING_DAYS: 90,
} as const;
