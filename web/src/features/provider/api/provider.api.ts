import { apiClient } from '@/lib';
import type { PageResponse, PageParams } from '@/types';
import type {
  CreateProviderRequest,
  UpdateProviderRequest,
  ProviderResponse,
  ProviderSummaryResponse,
  ProviderSearchCriteria,
  ScheduleRequest,
} from '../types/provider.types';

const BASE = '/api/v1/providers';

export const providerApi = {
  create: (data: CreateProviderRequest) =>
    apiClient.post<ProviderResponse>(BASE, data).then((r) => r.data),

  getById: (id: string) => apiClient.get<ProviderResponse>(`${BASE}/${id}`).then((r) => r.data),

  getByProviderNumber: (providerNumber: string) =>
    apiClient.get<ProviderResponse>(`${BASE}/number/${providerNumber}`).then((r) => r.data),

  list: (params?: PageParams) =>
    apiClient.get<PageResponse<ProviderSummaryResponse>>(BASE, { params }).then((r) => r.data),

  search: (criteria: ProviderSearchCriteria & PageParams) =>
    apiClient
      .get<PageResponse<ProviderSummaryResponse>>(`${BASE}/search`, { params: criteria })
      .then((r) => r.data),

  acceptingPatients: (params?: PageParams) =>
    apiClient
      .get<PageResponse<ProviderSummaryResponse>>(`${BASE}/accepting-patients`, { params })
      .then((r) => r.data),

  specializations: () => apiClient.get<string[]>(`${BASE}/specializations`).then((r) => r.data),

  update: (id: string, data: UpdateProviderRequest) =>
    apiClient.put<ProviderResponse>(`${BASE}/${id}`, data).then((r) => r.data),

  activate: (id: string) =>
    apiClient.patch<ProviderResponse>(`${BASE}/${id}/activate`).then((r) => r.data),

  deactivate: (id: string) =>
    apiClient.patch<ProviderResponse>(`${BASE}/${id}/deactivate`).then((r) => r.data),

  putOnLeave: (id: string) =>
    apiClient.patch<ProviderResponse>(`${BASE}/${id}/on-leave`).then((r) => r.data),

  returnFromLeave: (id: string) =>
    apiClient.patch<ProviderResponse>(`${BASE}/${id}/return-from-leave`).then((r) => r.data),

  suspend: (id: string) =>
    apiClient.patch<ProviderResponse>(`${BASE}/${id}/suspend`).then((r) => r.data),

  delete: (id: string) => apiClient.delete(`${BASE}/${id}`),

  addSchedule: (id: string, data: ScheduleRequest) =>
    apiClient.post<ProviderResponse>(`${BASE}/${id}/schedules`, data).then((r) => r.data),

  updateSchedule: (providerId: string, scheduleId: string, data: ScheduleRequest) =>
    apiClient
      .put<ProviderResponse>(`${BASE}/${providerId}/schedules/${scheduleId}`, data)
      .then((r) => r.data),

  removeSchedule: (providerId: string, scheduleId: string) =>
    apiClient.delete(`${BASE}/${providerId}/schedules/${scheduleId}`),
};
