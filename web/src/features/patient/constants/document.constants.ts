

import type { DocumentType } from '../types';

export const DOCUMENT_TYPE_LABELS: Record<DocumentType, string> = {
  LAB_RESULT: 'Lab Result',
  IMAGING: 'Imaging',
  CONSENT_FORM: 'Consent Form',
  INSURANCE: 'Insurance',
  PRESCRIPTION: 'Prescription',
  REFERRAL: 'Referral',
  DISCHARGE_SUMMARY: 'Discharge Summary',
  MEDICAL_HISTORY: 'Medical History',
  VACCINATION: 'Vaccination',
  OTHER: 'Other',
};

export const DOCUMENT_TYPE_OPTIONS = Object.entries(DOCUMENT_TYPE_LABELS).map(([value, label]) => ({
  value: value as DocumentType,
  label,
}));

export const DOCUMENT_TYPE_COLORS: Record<DocumentType, 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info' | 'default'> = {
  LAB_RESULT: 'info',
  IMAGING: 'secondary',
  CONSENT_FORM: 'default',
  INSURANCE: 'primary',
  PRESCRIPTION: 'success',
  REFERRAL: 'warning',
  DISCHARGE_SUMMARY: 'error',
  MEDICAL_HISTORY: 'primary',
  VACCINATION: 'success',
  OTHER: 'default',
};

export const DOCUMENT_UPLOAD = {
  MAX_FILE_SIZE_BYTES: 50 * 1024 * 1024,
  MAX_FILE_SIZE_LABEL: '50MB',
  ALLOWED_MIME_TYPES: [
    'application/pdf',
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/webp',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  ] as const,
  ALLOWED_EXTENSIONS: ['.pdf', '.jpg', '.jpeg', '.png', '.gif', '.webp', '.doc', '.docx'] as const,
} as const;

export const DOCUMENT_LIST_DEFAULTS = {
  PAGE_SIZE: 10,
  SORT_BY: 'uploadedAt',
  SORT_DIRECTION: 'desc' as const,
} as const;
