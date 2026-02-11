

import type { PatientStatus, Gender, BloodType } from '@/types';

export const PATIENT_VALIDATION = {
  NAME: {
    MIN_LENGTH: 1,
    MAX_LENGTH: 100,
  },
  EMAIL: {
    MAX_LENGTH: 255,
  },
  PHONE: {
    MIN_LENGTH: 10,
    MAX_LENGTH: 20,
    PATTERN: /^[\d\s\-+()]+$/,
  },
  SSN: {
    LENGTH: 11,
    PATTERN: /^\d{3}-\d{2}-\d{4}$/,
  },
  ZIP_CODE: {
    MIN_LENGTH: 5,
    MAX_LENGTH: 10,
    PATTERN: /^\d{5}(-\d{4})?$/,
  },
} as const;

export const PATIENT_STATUS_LABELS: Record<PatientStatus, string> = {
  ACTIVE: 'Active',
  INACTIVE: 'Inactive',
  DECEASED: 'Deceased',
  TRANSFERRED: 'Transferred',
  DISCHARGED: 'Discharged',
};

export const PATIENT_STATUS_COLORS: Record<PatientStatus, 'success' | 'warning' | 'error' | 'info' | 'default'> = {
  ACTIVE: 'success',
  INACTIVE: 'warning',
  DECEASED: 'error',
  TRANSFERRED: 'info',
  DISCHARGED: 'default',
};

export const GENDER_LABELS: Record<Gender, string> = {
  MALE: 'Male',
  FEMALE: 'Female',
  NON_BINARY: 'Non-binary',
  OTHER: 'Other',
  PREFER_NOT_TO_SAY: 'Prefer not to say',
};

export const GENDER_OPTIONS = Object.entries(GENDER_LABELS).map(([value, label]) => ({
  value: value as Gender,
  label,
}));

export const BLOOD_TYPE_LABELS: Record<BloodType, string> = {
  A_POSITIVE: 'A+',
  A_NEGATIVE: 'A-',
  B_POSITIVE: 'B+',
  B_NEGATIVE: 'B-',
  AB_POSITIVE: 'AB+',
  AB_NEGATIVE: 'AB-',
  O_POSITIVE: 'O+',
  O_NEGATIVE: 'O-',
  UNKNOWN: 'Unknown',
};

export const BLOOD_TYPE_OPTIONS = Object.entries(BLOOD_TYPE_LABELS).map(([value, label]) => ({
  value: value as BloodType,
  label,
}));

export const RELATIONSHIP_OPTIONS = [
  { value: 'SPOUSE', label: 'Spouse' },
  { value: 'PARENT', label: 'Parent' },
  { value: 'CHILD', label: 'Child' },
  { value: 'SIBLING', label: 'Sibling' },
  { value: 'FRIEND', label: 'Friend' },
  { value: 'OTHER', label: 'Other' },
] as const;

export const US_STATES = [
  { value: 'AL', label: 'Alabama' },
  { value: 'AK', label: 'Alaska' },
  { value: 'AZ', label: 'Arizona' },
  { value: 'AR', label: 'Arkansas' },
  { value: 'CA', label: 'California' },
  { value: 'CO', label: 'Colorado' },
  { value: 'CT', label: 'Connecticut' },
  { value: 'DE', label: 'Delaware' },
  { value: 'FL', label: 'Florida' },
  { value: 'GA', label: 'Georgia' },
  { value: 'HI', label: 'Hawaii' },
  { value: 'ID', label: 'Idaho' },
  { value: 'IL', label: 'Illinois' },
  { value: 'IN', label: 'Indiana' },
  { value: 'IA', label: 'Iowa' },
  { value: 'KS', label: 'Kansas' },
  { value: 'KY', label: 'Kentucky' },
  { value: 'LA', label: 'Louisiana' },
  { value: 'ME', label: 'Maine' },
  { value: 'MD', label: 'Maryland' },
  { value: 'MA', label: 'Massachusetts' },
  { value: 'MI', label: 'Michigan' },
  { value: 'MN', label: 'Minnesota' },
  { value: 'MS', label: 'Mississippi' },
  { value: 'MO', label: 'Missouri' },
  { value: 'MT', label: 'Montana' },
  { value: 'NE', label: 'Nebraska' },
  { value: 'NV', label: 'Nevada' },
  { value: 'NH', label: 'New Hampshire' },
  { value: 'NJ', label: 'New Jersey' },
  { value: 'NM', label: 'New Mexico' },
  { value: 'NY', label: 'New York' },
  { value: 'NC', label: 'North Carolina' },
  { value: 'ND', label: 'North Dakota' },
  { value: 'OH', label: 'Ohio' },
  { value: 'OK', label: 'Oklahoma' },
  { value: 'OR', label: 'Oregon' },
  { value: 'PA', label: 'Pennsylvania' },
  { value: 'RI', label: 'Rhode Island' },
  { value: 'SC', label: 'South Carolina' },
  { value: 'SD', label: 'South Dakota' },
  { value: 'TN', label: 'Tennessee' },
  { value: 'TX', label: 'Texas' },
  { value: 'UT', label: 'Utah' },
  { value: 'VT', label: 'Vermont' },
  { value: 'VA', label: 'Virginia' },
  { value: 'WA', label: 'Washington' },
  { value: 'WV', label: 'West Virginia' },
  { value: 'WI', label: 'Wisconsin' },
  { value: 'WY', label: 'Wyoming' },
] as const;

export const PATIENT_LIST_DEFAULTS = {
  PAGE_SIZE: 20,
  SORT_BY: 'lastName',
  SORT_DIRECTION: 'asc' as const,
} as const;
