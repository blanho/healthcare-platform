

import type { ProviderStatus, ProviderType, DayOfWeek } from '@/types';

export const PROVIDER_TYPE_LABELS: Record<ProviderType, string> = {
  PHYSICIAN: 'Physician',
  SURGEON: 'Surgeon',
  SPECIALIST: 'Specialist',
  DENTIST: 'Dentist',
  OPTOMETRIST: 'Optometrist',
  PSYCHIATRIST: 'Psychiatrist',
  DERMATOLOGIST: 'Dermatologist',
  CARDIOLOGIST: 'Cardiologist',
  NEUROLOGIST: 'Neurologist',
  PEDIATRICIAN: 'Pediatrician',
  RADIOLOGIST: 'Radiologist',
  GENERAL_PRACTITIONER: 'General Practitioner',
};

export const PROVIDER_TYPE_OPTIONS = Object.entries(PROVIDER_TYPE_LABELS).map(
  ([value, label]) => ({
    value: value as ProviderType,
    label,
  })
);

export const PROVIDER_STATUS_LABELS: Record<ProviderStatus, string> = {
  ACTIVE: 'Active',
  INACTIVE: 'Inactive',
  ON_LEAVE: 'On Leave',
  SUSPENDED: 'Suspended',
  PENDING_VERIFICATION: 'Pending Verification',
  RETIRED: 'Retired',
};

export const PROVIDER_STATUS_OPTIONS = Object.entries(PROVIDER_STATUS_LABELS).map(
  ([value, label]) => ({
    value: value as ProviderStatus,
    label,
  })
);

export const PROVIDER_STATUS_COLORS: Record<
  ProviderStatus,
  'success' | 'warning' | 'error' | 'info' | 'default'
> = {
  ACTIVE: 'success',
  INACTIVE: 'default',
  ON_LEAVE: 'warning',
  SUSPENDED: 'error',
  PENDING_VERIFICATION: 'info',
  RETIRED: 'default',
};

export const DAY_OF_WEEK_LABELS: Record<DayOfWeek, string> = {
  MONDAY: 'Monday',
  TUESDAY: 'Tuesday',
  WEDNESDAY: 'Wednesday',
  THURSDAY: 'Thursday',
  FRIDAY: 'Friday',
  SATURDAY: 'Saturday',
  SUNDAY: 'Sunday',
};

export const DAY_OF_WEEK_SHORT_LABELS: Record<DayOfWeek, string> = {
  MONDAY: 'Mon',
  TUESDAY: 'Tue',
  WEDNESDAY: 'Wed',
  THURSDAY: 'Thu',
  FRIDAY: 'Fri',
  SATURDAY: 'Sat',
  SUNDAY: 'Sun',
};

export const DAY_OF_WEEK_OPTIONS = Object.entries(DAY_OF_WEEK_LABELS).map(
  ([value, label]) => ({
    value: value as DayOfWeek,
    label,
  })
);

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
  { value: 'DC', label: 'District of Columbia' },
] as const;

export const PROVIDER_VALIDATION = {
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
  LICENSE_NUMBER: {
    MIN_LENGTH: 5,
    MAX_LENGTH: 50,
  },
  NPI: {
    LENGTH: 10,
    PATTERN: /^\d{10}$/,
  },
  SPECIALIZATION: {
    MAX_LENGTH: 100,
  },
  QUALIFICATION: {
    MAX_LENGTH: 200,
  },
  EXPERIENCE: {
    MIN_YEARS: 0,
    MAX_YEARS: 60,
  },
  CONSULTATION_FEE: {
    MIN: 0,
    MAX: 10000,
  },
} as const;

export const SCHEDULE_DEFAULTS = {
  DEFAULT_SLOT_DURATION: 30,
  MIN_SLOT_DURATION: 15,
  MAX_SLOT_DURATION: 120,
  DEFAULT_START_TIME: '09:00',
  DEFAULT_END_TIME: '17:00',
} as const;

export const PROVIDER_LIST_DEFAULTS = {
  PAGE_SIZE: 10,
  SORT_BY: 'lastName',
  SORT_DIRECTION: 'asc' as const,
} as const;

export const DAY_ORDER: DayOfWeek[] = [
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY',
];
