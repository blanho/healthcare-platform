import type { RecordStatus, RecordType } from '@/types';

export const recordStatusConfig: Record<
  RecordStatus,
  {
    label: string;
    color: 'default' | 'success' | 'warning' | 'error' | 'info' | 'primary';
  }
> = {
  DRAFT: { label: 'Draft', color: 'warning' },
  FINALIZED: { label: 'Finalized', color: 'success' },
  AMENDED: { label: 'Amended', color: 'info' },
  VOIDED: { label: 'Voided', color: 'error' },
};

export const recordTypeConfig: Record<RecordType, { label: string; color: string; icon: string }> = {
  CONSULTATION: { label: 'Consultation', color: '#0891B2', icon: 'CO' },
  FOLLOW_UP: { label: 'Follow Up', color: '#059669', icon: 'FU' },
  EMERGENCY: { label: 'Emergency', color: '#DC2626', icon: 'EM' },
  LAB_RESULT: { label: 'Lab Result', color: '#EC4899', icon: 'LR' },
  IMAGING: { label: 'Imaging', color: '#6366F1', icon: 'IM' },
  SURGERY: { label: 'Surgery', color: '#D97706', icon: 'SU' },
  DISCHARGE_SUMMARY: { label: 'Discharge', color: '#2563EB', icon: 'DS' },
  PROGRESS_NOTE: { label: 'Progress Note', color: '#7C3AED', icon: 'PN' },
  ADMISSION: { label: 'Admission', color: '#F59E0B', icon: 'AD' },
  REFERRAL: { label: 'Referral', color: '#14B8A6', icon: 'RF' },
  VACCINATION: { label: 'Vaccination', color: '#10B981', icon: 'VA' },
  PRESCRIPTION: { label: 'Prescription', color: '#8B5CF6', icon: 'RX' },
  PROCEDURE: { label: 'Procedure', color: '#EF4444', icon: 'PR' },
  THERAPY: { label: 'Therapy', color: '#06B6D4', icon: 'TH' },
  OTHER: { label: 'Other', color: '#6B7280', icon: 'OT' },
};
