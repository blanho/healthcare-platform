
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface PageParams {
  page?: number;
  size?: number;
  sort?: string;
}

export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance?: string;
  errorCode: string;
  timestamp: string;
  fieldErrors?: Record<string, string>;
  violations?: Record<string, string>;
}

export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'LOCKED';
export type Gender = 'MALE' | 'FEMALE' | 'NON_BINARY' | 'PREFER_NOT_TO_SAY' | 'OTHER';
export type BloodType =
  | 'A_POSITIVE'
  | 'A_NEGATIVE'
  | 'B_POSITIVE'
  | 'B_NEGATIVE'
  | 'AB_POSITIVE'
  | 'AB_NEGATIVE'
  | 'O_POSITIVE'
  | 'O_NEGATIVE'
  | 'UNKNOWN';

export type PatientStatus = 'ACTIVE' | 'INACTIVE' | 'DECEASED' | 'TRANSFERRED' | 'DISCHARGED';

export type ProviderType =
  | 'PHYSICIAN'
  | 'SURGEON'
  | 'SPECIALIST'
  | 'DENTIST'
  | 'OPTOMETRIST'
  | 'PSYCHIATRIST'
  | 'DERMATOLOGIST'
  | 'CARDIOLOGIST'
  | 'NEUROLOGIST'
  | 'PEDIATRICIAN'
  | 'RADIOLOGIST'
  | 'GENERAL_PRACTITIONER';

export type ProviderStatus =
  | 'ACTIVE'
  | 'INACTIVE'
  | 'ON_LEAVE'
  | 'SUSPENDED'
  | 'PENDING_VERIFICATION'
  | 'RETIRED';

export type AppointmentStatus =
  | 'SCHEDULED'
  | 'CONFIRMED'
  | 'CHECKED_IN'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'NO_SHOW'
  | 'RESCHEDULED';

export type AppointmentType =
  | 'CONSULTATION'
  | 'FOLLOW_UP'
  | 'CHECKUP'
  | 'EMERGENCY'
  | 'SURGERY'
  | 'LAB_TEST'
  | 'IMAGING'
  | 'VACCINATION'
  | 'PHYSICAL_THERAPY'
  | 'MENTAL_HEALTH'
  | 'DENTAL'
  | 'TELEMEDICINE'
  | 'OTHER';

export type RecordType =
  | 'CONSULTATION'
  | 'FOLLOW_UP'
  | 'EMERGENCY'
  | 'LAB_RESULT'
  | 'IMAGING'
  | 'SURGERY'
  | 'DISCHARGE_SUMMARY'
  | 'PROGRESS_NOTE'
  | 'ADMISSION'
  | 'REFERRAL'
  | 'VACCINATION'
  | 'PRESCRIPTION'
  | 'PROCEDURE'
  | 'THERAPY'
  | 'OTHER';

export type RecordStatus = 'DRAFT' | 'FINALIZED' | 'AMENDED' | 'VOIDED';
export type DiagnosisType = 'PRIMARY' | 'SECONDARY' | 'DIFFERENTIAL' | 'RULE_OUT';

export type InvoiceStatus =
  | 'DRAFT'
  | 'PENDING'
  | 'PARTIALLY_PAID'
  | 'PAID'
  | 'OVERDUE'
  | 'CANCELLED'
  | 'REFUNDED'
  | 'WRITE_OFF';

export type ClaimStatus =
  | 'SUBMITTED'
  | 'IN_REVIEW'
  | 'APPROVED'
  | 'PARTIALLY_APPROVED'
  | 'DENIED'
  | 'APPEALED'
  | 'PAID'
  | 'CLOSED'
  | 'INFORMATION_REQUESTED'
  | 'RESUBMITTED';

export type PaymentMethod =
  | 'CREDIT_CARD'
  | 'DEBIT_CARD'
  | 'CASH'
  | 'CHECK'
  | 'BANK_TRANSFER'
  | 'INSURANCE'
  | 'PAYMENT_PLAN'
  | 'ONLINE_PAYMENT'
  | 'MOBILE_PAYMENT';

export type PaymentStatus =
  | 'PENDING'
  | 'COMPLETED'
  | 'FAILED'
  | 'REFUNDED'
  | 'PARTIALLY_REFUNDED'
  | 'CANCELLED'
  | 'PROCESSING'
  | 'AUTHORIZED'
  | 'DECLINED'
  | 'VOIDED';

export type NotificationType = 'EMAIL' | 'SMS' | 'PUSH' | 'IN_APP';

export type NotificationCategory =
  | 'APPOINTMENT_REMINDER'
  | 'APPOINTMENT_CONFIRMATION'
  | 'APPOINTMENT_CANCELLATION'
  | 'APPOINTMENT_RESCHEDULED'
  | 'LAB_RESULTS'
  | 'PRESCRIPTION_READY'
  | 'PRESCRIPTION_REFILL'
  | 'BILLING_INVOICE'
  | 'BILLING_PAYMENT'
  | 'BILLING_REMINDER'
  | 'INSURANCE_CLAIM'
  | 'INSURANCE_UPDATE'
  | 'PROVIDER_MESSAGE'
  | 'PATIENT_MESSAGE'
  | 'SYSTEM_ALERT'
  | 'SYSTEM_MAINTENANCE'
  | 'SECURITY_ALERT'
  | 'PASSWORD_RESET'
  | 'ACCOUNT_ACTIVITY'
  | 'HEALTH_REMINDER'
  | 'VACCINATION_REMINDER'
  | 'GENERAL'
  | 'EMERGENCY';

export type NotificationStatus =
  | 'PENDING'
  | 'SENT'
  | 'DELIVERED'
  | 'READ'
  | 'FAILED'
  | 'CANCELLED'
  | 'SCHEDULED'
  | 'RETRYING';

export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export type Role =
  | 'ROLE_ADMIN'
  | 'ROLE_DOCTOR'
  | 'ROLE_NURSE'
  | 'ROLE_RECEPTIONIST'
  | 'ROLE_BILLING'
  | 'ROLE_PATIENT'
  | 'ROLE_LAB_TECH'
  | 'ROLE_PHARMACIST';

export type Permission =
  | 'patient:read'
  | 'patient:write'
  | 'patient:delete'
  | 'patient:document:read'
  | 'patient:document:write'
  | 'patient:document:delete'
  | 'provider:read'
  | 'provider:write'
  | 'provider:delete'
  | 'appointment:read'
  | 'appointment:write'
  | 'appointment:delete'
  | 'medical_record:read'
  | 'medical_record:write'
  | 'medical_record:delete'
  | 'billing:read'
  | 'billing:write'
  | 'billing:delete'
  | 'notification:read'
  | 'notification:write'
  | 'prescription:read'
  | 'prescription:write'
  | 'audit:read'
  | 'audit:admin';
