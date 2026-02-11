

import type { RecordType, RecordStatus, DiagnosisType } from '@/types';

export const RECORD_TYPE_LABELS: Record<RecordType, string> = {
  CONSULTATION: 'Consultation',
  FOLLOW_UP: 'Follow-up',
  EMERGENCY: 'Emergency',
  LAB_RESULT: 'Lab Result',
  IMAGING: 'Imaging',
  SURGERY: 'Surgery',
  DISCHARGE_SUMMARY: 'Discharge Summary',
  PROGRESS_NOTE: 'Progress Note',
  ADMISSION: 'Admission',
  REFERRAL: 'Referral',
  VACCINATION: 'Vaccination',
  PRESCRIPTION: 'Prescription',
  PROCEDURE: 'Procedure',
  THERAPY: 'Therapy',
  OTHER: 'Other',
};

export const RECORD_TYPE_OPTIONS = Object.entries(RECORD_TYPE_LABELS).map(
  ([value, label]) => ({
    value: value as RecordType,
    label,
  })
);

export const RECORD_TYPE_COLORS: Record<
  RecordType,
  'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info' | 'default'
> = {
  CONSULTATION: 'primary',
  FOLLOW_UP: 'info',
  EMERGENCY: 'error',
  LAB_RESULT: 'secondary',
  IMAGING: 'secondary',
  SURGERY: 'error',
  DISCHARGE_SUMMARY: 'warning',
  PROGRESS_NOTE: 'default',
  ADMISSION: 'warning',
  REFERRAL: 'info',
  VACCINATION: 'success',
  PRESCRIPTION: 'primary',
  PROCEDURE: 'warning',
  THERAPY: 'info',
  OTHER: 'default',
};

export const RECORD_STATUS_LABELS: Record<RecordStatus, string> = {
  DRAFT: 'Draft',
  FINALIZED: 'Finalized',
  AMENDED: 'Amended',
  VOIDED: 'Voided',
};

export const RECORD_STATUS_OPTIONS = Object.entries(RECORD_STATUS_LABELS).map(
  ([value, label]) => ({
    value: value as RecordStatus,
    label,
  })
);

export const RECORD_STATUS_COLORS: Record<
  RecordStatus,
  'success' | 'warning' | 'error' | 'info' | 'default'
> = {
  DRAFT: 'warning',
  FINALIZED: 'success',
  AMENDED: 'info',
  VOIDED: 'error',
};

export const DIAGNOSIS_TYPE_LABELS: Record<DiagnosisType, string> = {
  PRIMARY: 'Primary',
  SECONDARY: 'Secondary',
  DIFFERENTIAL: 'Differential',
  RULE_OUT: 'Rule Out',
};

export const DIAGNOSIS_TYPE_OPTIONS = Object.entries(DIAGNOSIS_TYPE_LABELS).map(
  ([value, label]) => ({
    value: value as DiagnosisType,
    label,
  })
);

export const DIAGNOSIS_TYPE_COLORS: Record<
  DiagnosisType,
  'primary' | 'secondary' | 'warning' | 'info'
> = {
  PRIMARY: 'primary',
  SECONDARY: 'secondary',
  DIFFERENTIAL: 'warning',
  RULE_OUT: 'info',
};

export const VITAL_SIGN_RANGES = {
  BLOOD_PRESSURE: {
    SYSTOLIC: { MIN: 70, MAX: 200, NORMAL_MIN: 90, NORMAL_MAX: 120 },
    DIASTOLIC: { MIN: 40, MAX: 130, NORMAL_MIN: 60, NORMAL_MAX: 80 },
  },
  HEART_RATE: { MIN: 30, MAX: 200, NORMAL_MIN: 60, NORMAL_MAX: 100 },
  RESPIRATORY_RATE: { MIN: 8, MAX: 40, NORMAL_MIN: 12, NORMAL_MAX: 20 },
  TEMPERATURE: { MIN: 95.0, MAX: 106.0, NORMAL_MIN: 97.8, NORMAL_MAX: 99.1 },
  OXYGEN_SATURATION: { MIN: 70, MAX: 100, NORMAL_MIN: 95, NORMAL_MAX: 100 },
  WEIGHT: { MIN: 1, MAX: 500 },
  HEIGHT: { MIN: 30, MAX: 250 },
  PAIN_LEVEL: { MIN: 0, MAX: 10 },
} as const;

export const VITAL_SIGN_UNITS = {
  BLOOD_PRESSURE: 'mmHg',
  HEART_RATE: 'bpm',
  RESPIRATORY_RATE: 'breaths/min',
  TEMPERATURE: '°F',
  OXYGEN_SATURATION: '%',
  WEIGHT: 'kg',
  HEIGHT: 'cm',
  PAIN_LEVEL: '/10',
} as const;

export const RECORD_VALIDATION = {
  CHIEF_COMPLAINT: {
    MAX_LENGTH: 500,
  },
  NOTES: {
    MAX_LENGTH: 10000,
  },
  SOAP_SECTION: {
    MAX_LENGTH: 5000,
  },
  DIAGNOSIS_CODE: {
    MAX_LENGTH: 20,
    PATTERN: /^[A-Z]\d{2}(\.\d{1,4})?$/,
  },
  DIAGNOSIS_DESCRIPTION: {
    MAX_LENGTH: 500,
  },
  AMEND_REASON: {
    MIN_LENGTH: 10,
    MAX_LENGTH: 1000,
  },
  VOID_REASON: {
    MIN_LENGTH: 10,
    MAX_LENGTH: 1000,
  },
} as const;

export const RECORD_LIST_DEFAULTS = {
  PAGE_SIZE: 10,
  SORT_BY: 'recordDate',
  SORT_DIRECTION: 'desc' as const,
} as const;

export const SOAP_SECTION_LABELS = {
  subjective: 'Subjective',
  objective: 'Objective',
  assessment: 'Assessment',
  plan: 'Plan',
} as const;

export const SOAP_SECTION_DESCRIPTIONS = {
  subjective: "Patient's symptoms, concerns, and history as reported",
  objective: 'Clinical findings, vital signs, and examination results',
  assessment: 'Diagnoses and clinical impressions',
  plan: 'Treatment plan, medications, follow-up, and recommendations',
} as const;
