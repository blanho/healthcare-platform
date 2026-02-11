import type { ProviderStatus, ProviderType } from '@/types';

export const providerStatusConfig: Record<
  ProviderStatus,
  {
    label: string;
    color: 'default' | 'success' | 'warning' | 'error' | 'info';
  }
> = {
  ACTIVE: { label: 'Active', color: 'success' },
  INACTIVE: { label: 'Inactive', color: 'default' },
  PENDING_VERIFICATION: { label: 'Pending', color: 'warning' },
  SUSPENDED: { label: 'Suspended', color: 'error' },
  ON_LEAVE: { label: 'On Leave', color: 'info' },
  RETIRED: { label: 'Retired', color: 'default' },
};

export const providerTypeConfig: Record<
  ProviderType,
  { label: string; color: string; abbreviation: string }
> = {
  PHYSICIAN: { label: 'Physician', color: '#0891B2', abbreviation: 'MD' },
  SURGEON: { label: 'Surgeon', color: '#DC2626', abbreviation: 'SG' },
  SPECIALIST: { label: 'Specialist', color: '#D97706', abbreviation: 'SP' },
  DENTIST: { label: 'Dentist', color: '#7C3AED', abbreviation: 'DT' },
  OPTOMETRIST: { label: 'Optometrist', color: '#059669', abbreviation: 'OD' },
  PSYCHIATRIST: { label: 'Psychiatrist', color: '#6366F1', abbreviation: 'PS' },
  DERMATOLOGIST: { label: 'Dermatologist', color: '#EC4899', abbreviation: 'DM' },
  CARDIOLOGIST: { label: 'Cardiologist', color: '#EF4444', abbreviation: 'CD' },
  NEUROLOGIST: { label: 'Neurologist', color: '#8B5CF6', abbreviation: 'NE' },
  PEDIATRICIAN: { label: 'Pediatrician', color: '#10B981', abbreviation: 'PD' },
  RADIOLOGIST: { label: 'Radiologist', color: '#2563EB', abbreviation: 'RD' },
  GENERAL_PRACTITIONER: { label: 'General Practitioner', color: '#14B8A6', abbreviation: 'GP' },
};
