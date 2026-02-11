import { apiClient } from '@/lib';
import type { PageResponse, PageParams } from '@/types';
import type {
  ScheduleAppointmentRequest,
  RescheduleAppointmentRequest,
  CancelAppointmentRequest,
  AppointmentResponse,
  AppointmentSummaryResponse,
  AppointmentSearchCriteria,
  SlotAvailabilityResponse,
} from '../types/appointment.types';

const BASE = '/api/v1/appointments';

export const appointmentApi = {
  schedule: (data: ScheduleAppointmentRequest) =>
    apiClient.post<AppointmentResponse>(BASE, data).then((r) => r.data),

  reschedule: (id: string, data: RescheduleAppointmentRequest) =>
    apiClient.put<AppointmentResponse>(`${BASE}/${id}/reschedule`, data).then((r) => r.data),

  getById: (id: string) => apiClient.get<AppointmentResponse>(`${BASE}/${id}`).then((r) => r.data),

  getByAppointmentNumber: (appointmentNumber: string) =>
    apiClient
      .get<AppointmentResponse>(`${BASE}/by-number/${appointmentNumber}`)
      .then((r) => r.data),

  search: (criteria: AppointmentSearchCriteria & PageParams) =>
    apiClient
      .get<PageResponse<AppointmentSummaryResponse>>(BASE, { params: criteria })
      .then((r) => r.data),

  byPatient: (patientId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<AppointmentSummaryResponse>>(`${BASE}/patient/${patientId}`, { params })
      .then((r) => r.data),

  byProvider: (providerId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<AppointmentSummaryResponse>>(`${BASE}/provider/${providerId}`, { params })
      .then((r) => r.data),

  byDateRange: (startDate: string, endDate: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<AppointmentSummaryResponse>>(`${BASE}/date-range`, {
        params: { startDate, endDate, ...params },
      })
      .then((r) => r.data),

  todayByProvider: (providerId: string) =>
    apiClient
      .get<AppointmentSummaryResponse[]>(`${BASE}/provider/${providerId}/today`)
      .then((r) => r.data),

  checkSlot: (providerId: string, date: string, startTime: string, durationMinutes?: number) =>
    apiClient
      .get<SlotAvailabilityResponse>(`${BASE}/slot-availability`, {
        params: { providerId, date, startTime, durationMinutes },
      })
      .then((r) => r.data),

  confirm: (id: string) =>
    apiClient.patch<AppointmentResponse>(`${BASE}/${id}/confirm`).then((r) => r.data),

  checkIn: (id: string, notes?: string) =>
    apiClient
      .patch<AppointmentResponse>(`${BASE}/${id}/check-in`, null, { params: { notes } })
      .then((r) => r.data),

  complete: (id: string, notes?: string) =>
    apiClient
      .patch<AppointmentResponse>(`${BASE}/${id}/complete`, null, { params: { notes } })
      .then((r) => r.data),

  cancel: (id: string, data: CancelAppointmentRequest) =>
    apiClient.patch<AppointmentResponse>(`${BASE}/${id}/cancel`, data).then((r) => r.data),

  noShow: (id: string) =>
    apiClient.patch<AppointmentResponse>(`${BASE}/${id}/no-show`).then((r) => r.data),
};
