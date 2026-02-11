import type { NotificationType, NotificationCategory, NotificationStatus } from '@/types';

export const notificationTypeConfigData: Record<
  NotificationType,
  { label: string; color: string }
> = {
  EMAIL: { label: 'Email', color: '#EA4335' },
  SMS: { label: 'SMS', color: '#34A853' },
  PUSH: { label: 'Push', color: '#FBBC04' },
  IN_APP: { label: 'In-App', color: '#4285F4' },
};

export const notificationStatusConfigData: Record<
  NotificationStatus,
  { label: string; color: 'default' | 'success' | 'warning' | 'error' | 'info' }
> = {
  PENDING: { label: 'Pending', color: 'warning' },
  SCHEDULED: { label: 'Scheduled', color: 'info' },
  SENT: { label: 'Sent', color: 'success' },
  DELIVERED: { label: 'Delivered', color: 'success' },
  READ: { label: 'Read', color: 'default' },
  FAILED: { label: 'Failed', color: 'error' },
  CANCELLED: { label: 'Cancelled', color: 'default' },
  RETRYING: { label: 'Retrying', color: 'warning' },
};

export const notificationCategoryConfigData: Record<
  NotificationCategory,
  { label: string; color: string }
> = {
  APPOINTMENT_REMINDER: { label: 'Appointment Reminder', color: '#0891B2' },
  APPOINTMENT_CONFIRMATION: { label: 'Appointment Confirmed', color: '#10B981' },
  APPOINTMENT_CANCELLATION: { label: 'Appointment Cancelled', color: '#EF4444' },
  APPOINTMENT_RESCHEDULED: { label: 'Appointment Rescheduled', color: '#F59E0B' },
  LAB_RESULTS: { label: 'Lab Results', color: '#8B5CF6' },
  PRESCRIPTION_READY: { label: 'Prescription Ready', color: '#059669' },
  PRESCRIPTION_REFILL: { label: 'Prescription Refill', color: '#14B8A6' },
  BILLING_INVOICE: { label: 'Invoice', color: '#6366F1' },
  BILLING_PAYMENT: { label: 'Payment', color: '#10B981' },
  BILLING_REMINDER: { label: 'Payment Reminder', color: '#D97706' },
  INSURANCE_CLAIM: { label: 'Insurance Claim', color: '#7C3AED' },
  INSURANCE_UPDATE: { label: 'Insurance Update', color: '#2563EB' },
  PROVIDER_MESSAGE: { label: 'Provider Message', color: '#0891B2' },
  PATIENT_MESSAGE: { label: 'Patient Message', color: '#059669' },
  SYSTEM_ALERT: { label: 'System Alert', color: '#6B7280' },
  SYSTEM_MAINTENANCE: { label: 'Maintenance', color: '#9CA3AF' },
  SECURITY_ALERT: { label: 'Security Alert', color: '#DC2626' },
  PASSWORD_RESET: { label: 'Password Reset', color: '#F59E0B' },
  ACCOUNT_ACTIVITY: { label: 'Account Activity', color: '#3B82F6' },
  HEALTH_REMINDER: { label: 'Health Reminder', color: '#10B981' },
  VACCINATION_REMINDER: { label: 'Vaccination', color: '#059669' },
  GENERAL: { label: 'General', color: '#6B7280' },
  EMERGENCY: { label: 'Emergency', color: '#DC2626' },
};

export { notificationCategoryConfigData as notificationCategoryConfig };
