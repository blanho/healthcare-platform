import type { RecordType, RecordStatus, DiagnosisType } from '@/types';

export interface CreateMedicalRecordRequest {
  patientId: string;
  providerId: string;
  appointmentId?: string;
  recordType: RecordType;
  recordDate: string;
  chiefComplaint?: string;
  notes?: string;
  vitalSigns?: VitalSignsRequest;
  soapNote?: SoapNoteRequest;
  diagnoses?: DiagnosisRequest[];
}

export interface UpdateMedicalRecordRequest {
  chiefComplaint?: string;
  notes?: string;
  vitalSigns?: VitalSignsRequest;
  soapNote?: SoapNoteRequest;
  diagnoses?: DiagnosisRequest[];
}

export interface VitalSignsRequest {
  systolicBp?: number;
  diastolicBp?: number;
  heartRate?: number;
  respiratoryRate?: number;
  temperature?: number;
  oxygenSaturation?: number;
  weightKg?: number;
  heightCm?: number;
  painLevel?: number;
  recordedAt?: string;
}

export interface SoapNoteRequest {
  subjective?: string;
  objective?: string;
  assessment?: string;
  plan?: string;
}

export interface DiagnosisRequest {
  code: string;
  description: string;
  type?: DiagnosisType;
  primary?: boolean;
  onsetDate?: string;
  resolvedDate?: string;
  notes?: string;
}

export interface AmendRecordRequest {
  reason: string;
  additionalNotes?: string;
}
export interface VoidRecordRequest {
  reason: string;
}

export interface MedicalRecordResponse {
  id: string;
  recordNumber: string;
  patientId: string;
  providerId: string;
  appointmentId: string | null;
  recordType: RecordType;
  recordDate: string;
  chiefComplaint: string | null;
  notes: string | null;
  vitalSigns: VitalSignsResponse | null;
  soapNote: SoapNoteResponse | null;
  diagnoses: DiagnosisResponse[];
  status: RecordStatus;
  finalizedAt: string | null;
  finalizedBy: string | null;
  attachmentsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface VitalSignsResponse {
  systolicBp: number | null;
  diastolicBp: number | null;
  bloodPressure: string | null;
  heartRate: number | null;
  respiratoryRate: number | null;
  temperature: number | null;
  oxygenSaturation: number | null;
  weightKg: number | null;
  heightCm: number | null;
  bmi: number | null;
  painLevel: number | null;
  recordedAt: string | null;
  hasCriticalValue: boolean;
}

export interface SoapNoteResponse {
  subjective: string | null;
  objective: string | null;
  assessment: string | null;
  plan: string | null;
  isComplete: boolean;
}

export interface DiagnosisResponse {
  code: string;
  description: string;
  type: DiagnosisType;
  primary: boolean;
  onsetDate: string | null;
  resolvedDate: string | null;
  notes: string | null;
  isResolved: boolean;
  isChronic: boolean;
}

export interface MedicalRecordSummaryResponse {
  id: string;
  recordNumber: string;
  patientId: string;
  providerId: string;
  recordType: RecordType;
  recordDate: string;
  chiefComplaint: string | null;
  primaryDiagnosisCode: string | null;
  primaryDiagnosisDescription: string | null;
  status: RecordStatus;
  attachmentsCount: number;
  createdAt: string;
}

export interface MedicalRecordSearchCriteria {
  patientId?: string;
  providerId?: string;
  appointmentId?: string;
  recordType?: RecordType;
  status?: RecordStatus;
  startDate?: string;
  endDate?: string;
  diagnosisCode?: string;
}
