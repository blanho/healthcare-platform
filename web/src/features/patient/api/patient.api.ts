import { apiClient } from '@/lib';
import type { PageResponse, PageParams } from '@/types';
import type {
  CreatePatientRequest,
  UpdatePatientRequest,
  PatientResponse,
  PatientSummaryResponse,
  PatientSearchCriteria,
} from '../types/patient.types';

const BASE = '/api/v1/patients';

export const patientApi = {
  create: (data: CreatePatientRequest) =>
    apiClient.post<PatientResponse>(BASE, data).then((r) => r.data),

  getById: (id: string) => apiClient.get<PatientResponse>(`${BASE}/${id}`).then((r) => r.data),

  getByMrn: (mrn: string) =>
    apiClient.get<PatientResponse>(`${BASE}/mrn/${mrn}`).then((r) => r.data),

  list: (params?: PageParams) =>
    apiClient.get<PageResponse<PatientSummaryResponse>>(BASE, { params }).then((r) => r.data),

  search: (criteria: PatientSearchCriteria & PageParams) =>
    apiClient
      .get<PageResponse<PatientSummaryResponse>>(`${BASE}/search`, { params: criteria })
      .then((r) => r.data),

  update: (id: string, data: UpdatePatientRequest) =>
    apiClient.put<PatientResponse>(`${BASE}/${id}`, data).then((r) => r.data),

  activate: (id: string) =>
    apiClient.patch<PatientResponse>(`${BASE}/${id}/activate`).then((r) => r.data),

  deactivate: (id: string) =>
    apiClient.patch<PatientResponse>(`${BASE}/${id}/deactivate`).then((r) => r.data),

  delete: (id: string) => apiClient.delete(`${BASE}/${id}`),

  canSchedule: (id: string) =>
    apiClient.get<boolean>(`${BASE}/${id}/can-schedule`).then((r) => r.data),
};
