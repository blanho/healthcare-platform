import { apiClient } from '@/lib';
import type { PageResponse, PageParams } from '@/types';
import type {
  CreateMedicalRecordRequest,
  UpdateMedicalRecordRequest,
  AmendRecordRequest,
  VoidRecordRequest,
  VitalSignsRequest,
  SoapNoteRequest,
  DiagnosisRequest,
  MedicalRecordResponse,
  MedicalRecordSummaryResponse,
  MedicalRecordSearchCriteria,
} from '../types/medical-record.types';

const BASE = '/api/v1/medical-records';

export const medicalRecordApi = {
  create: (data: CreateMedicalRecordRequest) =>
    apiClient.post<MedicalRecordResponse>(BASE, data).then((r) => r.data),

  update: (id: string, data: UpdateMedicalRecordRequest) =>
    apiClient.put<MedicalRecordResponse>(`${BASE}/${id}`, data).then((r) => r.data),

  getById: (id: string) =>
    apiClient.get<MedicalRecordResponse>(`${BASE}/${id}`).then((r) => r.data),

  search: (criteria: MedicalRecordSearchCriteria & PageParams) =>
    apiClient
      .get<PageResponse<MedicalRecordSummaryResponse>>(BASE, { params: criteria })
      .then((r) => r.data),

  byPatient: (patientId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<MedicalRecordSummaryResponse>>(`${BASE}/patient/${patientId}`, { params })
      .then((r) => r.data),

  timeline: (patientId: string, limit = 10) =>
    apiClient
      .get<
        MedicalRecordSummaryResponse[]
      >(`${BASE}/patient/${patientId}/timeline`, { params: { limit } })
      .then((r) => r.data),

  finalize: (id: string) =>
    apiClient.patch<MedicalRecordResponse>(`${BASE}/${id}/finalize`).then((r) => r.data),

  amend: (id: string, data: AmendRecordRequest) =>
    apiClient.patch<MedicalRecordResponse>(`${BASE}/${id}/amend`, data).then((r) => r.data),

  void: (id: string, data: VoidRecordRequest) =>
    apiClient.patch<MedicalRecordResponse>(`${BASE}/${id}/void`, data).then((r) => r.data),

  updateVitals: (id: string, data: VitalSignsRequest) =>
    apiClient.patch<MedicalRecordResponse>(`${BASE}/${id}/vitals`, data).then((r) => r.data),

  updateSoapNote: (id: string, data: SoapNoteRequest) =>
    apiClient.patch<MedicalRecordResponse>(`${BASE}/${id}/soap-note`, data).then((r) => r.data),

  addDiagnosis: (id: string, data: DiagnosisRequest) =>
    apiClient.post<MedicalRecordResponse>(`${BASE}/${id}/diagnoses`, data).then((r) => r.data),

  getByRecordNumber: (recordNumber: string) =>
    apiClient.get<MedicalRecordResponse>(`${BASE}/number/${recordNumber}`).then((r) => r.data),

  byAppointment: (appointmentId: string) =>
    apiClient
      .get<MedicalRecordResponse>(`${BASE}/appointment/${appointmentId}`)
      .then((r) => r.data),

  byProvider: (providerId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<MedicalRecordSummaryResponse>>(`${BASE}/provider/${providerId}`, { params })
      .then((r) => r.data),

  drafts: (providerId: string, params?: PageParams) =>
    apiClient
      .get<
        PageResponse<MedicalRecordSummaryResponse>
      >(`${BASE}/provider/${providerId}/drafts`, { params })
      .then((r) => r.data),

  removeDiagnosis: (id: string, diagnosisCode: string) =>
    apiClient
      .delete<MedicalRecordResponse>(`${BASE}/${id}/diagnoses/${diagnosisCode}`)
      .then((r) => r.data),

  history: (id: string) =>
    apiClient.get<MedicalRecordResponse[]>(`${BASE}/${id}/history`).then((r) => r.data),
};
