

import { format, parseISO, differenceInDays } from 'date-fns';
import type { RecordType, RecordStatus, DiagnosisType } from '@/types';
import type {
  MedicalRecordResponse,
  VitalSignsResponse,
  SoapNoteResponse,
  DiagnosisResponse,
} from '../types/medical-record.types';
import {
  RECORD_TYPE_LABELS,
  RECORD_STATUS_LABELS,
  DIAGNOSIS_TYPE_LABELS,
  VITAL_SIGN_RANGES,
  VITAL_SIGN_UNITS,
} from '../constants';

export function getRecordTypeLabel(type: RecordType): string {
  return RECORD_TYPE_LABELS[type];
}

export function getRecordStatusLabel(status: RecordStatus): string {
  return RECORD_STATUS_LABELS[status];
}

export function getDiagnosisTypeLabel(type: DiagnosisType): string {
  return DIAGNOSIS_TYPE_LABELS[type];
}

export function formatRecordDate(date: string, formatStr = 'MMM d, yyyy'): string {
  return format(parseISO(date), formatStr);
}

export function formatRecordDateTime(date: string): string {
  return format(parseISO(date), 'MMM d, yyyy h:mm a');
}

export function getDaysSinceRecord(date: string): number {
  return differenceInDays(new Date(), parseISO(date));
}

export function formatBloodPressure(systolic: number | null, diastolic: number | null): string {
  if (systolic === null || diastolic === null) return 'N/A';
  return `${systolic}/${diastolic} ${VITAL_SIGN_UNITS.BLOOD_PRESSURE}`;
}

export function formatVitalSign(
  value: number | null,
  type: keyof typeof VITAL_SIGN_UNITS
): string {
  if (value === null) return 'N/A';
  return `${value}${VITAL_SIGN_UNITS[type]}`;
}

export function isVitalSignNormal(
  value: number | null,
  type: 'HEART_RATE' | 'RESPIRATORY_RATE' | 'TEMPERATURE' | 'OXYGEN_SATURATION'
): boolean {
  if (value === null) return true;
  const range = VITAL_SIGN_RANGES[type];
  return value >= range.NORMAL_MIN && value <= range.NORMAL_MAX;
}

export function isBloodPressureNormal(
  systolic: number | null,
  diastolic: number | null
): boolean {
  if (systolic === null || diastolic === null) return true;
  const sysRange = VITAL_SIGN_RANGES.BLOOD_PRESSURE.SYSTOLIC;
  const diaRange = VITAL_SIGN_RANGES.BLOOD_PRESSURE.DIASTOLIC;
  return (
    systolic >= sysRange.NORMAL_MIN &&
    systolic <= sysRange.NORMAL_MAX &&
    diastolic >= diaRange.NORMAL_MIN &&
    diastolic <= diaRange.NORMAL_MAX
  );
}

export function getVitalSignStatus(
  value: number | null,
  type: 'HEART_RATE' | 'RESPIRATORY_RATE' | 'TEMPERATURE' | 'OXYGEN_SATURATION'
): 'normal' | 'high' | 'low' | 'critical' | 'unknown' {
  if (value === null) return 'unknown';
  const range = VITAL_SIGN_RANGES[type];

  if (value < range.MIN || value > range.MAX) return 'critical';
  if (value < range.NORMAL_MIN) return 'low';
  if (value > range.NORMAL_MAX) return 'high';
  return 'normal';
}

export function calculateBMI(weightKg: number | null, heightCm: number | null): number | null {
  if (weightKg === null || heightCm === null || heightCm === 0) return null;
  const heightM = heightCm / 100;
  return Math.round((weightKg / (heightM * heightM)) * 10) / 10;
}

export function getBMICategory(bmi: number | null): string {
  if (bmi === null) return 'Unknown';
  if (bmi < 18.5) return 'Underweight';
  if (bmi < 25) return 'Normal';
  if (bmi < 30) return 'Overweight';
  return 'Obese';
}

export function hasCriticalVitals(vitals: VitalSignsResponse | null): boolean {
  return vitals?.hasCriticalValue ?? false;
}

export function isSoapNoteComplete(soapNote: SoapNoteResponse | null): boolean {
  return soapNote?.isComplete ?? false;
}

export function getSoapNoteCompletion(soapNote: SoapNoteResponse | null): number {
  if (!soapNote) return 0;
  const sections = ['subjective', 'objective', 'assessment', 'plan'] as const;
  const filled = sections.filter((s) => soapNote[s] && soapNote[s]!.length > 0).length;
  return Math.round((filled / 4) * 100);
}

export function truncateSoapSection(text: string | null, maxLength = 200): string {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return `${text.substring(0, maxLength)}...`;
}

export function getPrimaryDiagnosis(
  diagnoses: DiagnosisResponse[]
): DiagnosisResponse | undefined {
  return diagnoses.find((d) => d.primary);
}

export function getSecondaryDiagnoses(diagnoses: DiagnosisResponse[]): DiagnosisResponse[] {
  return diagnoses.filter((d) => !d.primary);
}

export function getActiveDiagnoses(diagnoses: DiagnosisResponse[]): DiagnosisResponse[] {
  return diagnoses.filter((d) => !d.isResolved);
}

export function getChronicDiagnoses(diagnoses: DiagnosisResponse[]): DiagnosisResponse[] {
  return diagnoses.filter((d) => d.isChronic);
}

export function formatDiagnosis(diagnosis: DiagnosisResponse): string {
  return `${diagnosis.code}: ${diagnosis.description}`;
}

export function groupDiagnosesByType(
  diagnoses: DiagnosisResponse[]
): Record<DiagnosisType, DiagnosisResponse[]> {
  return diagnoses.reduce(
    (acc, d) => {
      if (!acc[d.type]) {
        acc[d.type] = [];
      }
      acc[d.type].push(d);
      return acc;
    },
    {} as Record<DiagnosisType, DiagnosisResponse[]>
  );
}

export function canEditRecord(status: RecordStatus): boolean {
  return status === 'DRAFT';
}

export function canFinalizeRecord(status: RecordStatus): boolean {
  return status === 'DRAFT';
}

export function canAmendRecord(status: RecordStatus): boolean {
  return status === 'FINALIZED' || status === 'AMENDED';
}

export function canVoidRecord(status: RecordStatus): boolean {
  return status !== 'VOIDED';
}

export function isRecordFinalized(status: RecordStatus): boolean {
  return ['FINALIZED', 'AMENDED', 'VOIDED'].includes(status);
}

export function sortByRecordDate<T extends { recordDate: string }>(
  records: T[],
  direction: 'asc' | 'desc' = 'desc'
): T[] {
  return [...records].sort((a, b) => {
    const compare = a.recordDate.localeCompare(b.recordDate);
    return direction === 'asc' ? compare : -compare;
  });
}

export function filterByRecordType<T extends { recordType: RecordType }>(
  records: T[],
  types: RecordType[]
): T[] {
  if (types.length === 0) return records;
  return records.filter((r) => types.includes(r.recordType));
}

export function filterByRecordStatus<T extends { status: RecordStatus }>(
  records: T[],
  statuses: RecordStatus[]
): T[] {
  if (statuses.length === 0) return records;
  return records.filter((r) => statuses.includes(r.status));
}

export function filterByDateRange<T extends { recordDate: string }>(
  records: T[],
  startDate: string,
  endDate: string
): T[] {
  return records.filter((r) => r.recordDate >= startDate && r.recordDate <= endDate);
}

export function getRecordSummary(record: MedicalRecordResponse): string {
  const parts: string[] = [getRecordTypeLabel(record.recordType)];
  if (record.chiefComplaint) {
    parts.push(`- ${record.chiefComplaint}`);
  }
  return parts.join(' ');
}

export function countByRecordType<T extends { recordType: RecordType }>(
  records: T[]
): Record<RecordType, number> {
  return records.reduce(
    (acc, r) => {
      acc[r.recordType] = (acc[r.recordType] || 0) + 1;
      return acc;
    },
    {} as Record<RecordType, number>
  );
}
