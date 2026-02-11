import type { AppointmentStatus, AppointmentType } from '@/types';

export const appointmentStatusConfig: Record<
  AppointmentStatus,
  {
    label: string;
    color: 'default' | 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';
  }
> = {
  SCHEDULED: { label: 'Scheduled', color: 'primary' },
  CONFIRMED: { label: 'Confirmed', color: 'success' },
  CHECKED_IN: { label: 'Checked In', color: 'info' },
  IN_PROGRESS: { label: 'In Progress', color: 'warning' },
  COMPLETED: { label: 'Completed', color: 'success' },
  CANCELLED: { label: 'Cancelled', color: 'error' },
  NO_SHOW: { label: 'No Show', color: 'error' },
  RESCHEDULED: { label: 'Rescheduled', color: 'info' },
};

export const appointmentTypeConfig: Record<
  AppointmentType,
  { label: string; color: string; icon: string }
> = {
  CONSULTATION: { label: 'Consultation', color: '#0891B2', icon: 'CO' },
  FOLLOW_UP: { label: 'Follow Up', color: '#059669', icon: 'FU' },
  CHECKUP: { label: 'Checkup', color: '#7C3AED', icon: 'CK' },
  EMERGENCY: { label: 'Emergency', color: '#DC2626', icon: 'EM' },
  SURGERY: { label: 'Surgery', color: '#D97706', icon: 'SU' },
  LAB_TEST: { label: 'Lab Test', color: '#EC4899', icon: 'LT' },
  IMAGING: { label: 'Imaging', color: '#6366F1', icon: 'IM' },
  VACCINATION: { label: 'Vaccination', color: '#10B981', icon: 'VA' },
  PHYSICAL_THERAPY: { label: 'Physical Therapy', color: '#8B5CF6', icon: 'PT' },
  MENTAL_HEALTH: { label: 'Mental Health', color: '#14B8A6', icon: 'MH' },
  DENTAL: { label: 'Dental', color: '#F59E0B', icon: 'DE' },
  TELEMEDICINE: { label: 'Telemedicine', color: '#2563EB', icon: 'TM' },
  OTHER: { label: 'Other', color: '#6B7280', icon: 'OT' },
};
