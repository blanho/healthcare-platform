

import { apiClient } from '@/lib/api-client';
import type {
  AuditEventResponse,
  AuditEventSummary,
  AuditSearchParams,
  DateRangeParams,
  UserActivitySummary,
  PatientAccessHistory,
  HipaaComplianceReport,
  SecurityReport,
  AccessReport,
  DailyAuditSummary,
  MonthlyAuditSummary,
  PageResponse,
} from '../types/audit.types';

const BASE = '/api/v1/audit';

export const auditApi = {

  getById: (id: string) =>
    apiClient.get<AuditEventResponse>(`${BASE}/events/${id}`).then((r) => r.data),

  search: (params: AuditSearchParams) =>
    apiClient
      .get<PageResponse<AuditEventSummary>>(`${BASE}/events`, { params })
      .then((r) => r.data),

  getRecent: (limit = 50) =>
    apiClient
      .get<AuditEventSummary[]>(`${BASE}/events/recent`, { params: { limit } })
      .then((r) => r.data),

  getUserTrail: (userId: string, params?: DateRangeParams & { page?: number; size?: number }) =>
    apiClient
      .get<PageResponse<AuditEventSummary>>(`${BASE}/users/${userId}/trail`, { params })
      .then((r) => r.data),

  getUserActivitySummary: (userId: string, params?: DateRangeParams) =>
    apiClient
      .get<UserActivitySummary>(`${BASE}/users/${userId}/activity-summary`, { params })
      .then((r) => r.data),

  countUserEvents: (userId: string, days = 30) =>
    apiClient
      .get<number>(`${BASE}/users/${userId}/event-count`, { params: { days } })
      .then((r) => r.data),

  countFailedLogins: (userId: string, days = 7) =>
    apiClient
      .get<number>(`${BASE}/users/${userId}/failed-logins`, { params: { days } })
      .then((r) => r.data),

  checkUserAnomaly: (userId: string, hours = 24) =>
    apiClient
      .get<boolean>(`${BASE}/users/${userId}/anomaly-check`, { params: { hours } })
      .then((r) => r.data),

  getPatientAccessHistory: (patientId: string, params?: DateRangeParams) =>
    apiClient
      .get<PatientAccessHistory>(`${BASE}/patients/${patientId}/access-history`, { params })
      .then((r) => r.data),

  getPatientTrail: (
    patientId: string,
    params?: DateRangeParams & { page?: number; size?: number },
  ) =>
    apiClient
      .get<PageResponse<AuditEventSummary>>(`${BASE}/patients/${patientId}/trail`, { params })
      .then((r) => r.data),

  getResourceTrail: (
    category: string,
    resourceId: string,
    params?: { page?: number; size?: number },
  ) =>
    apiClient
      .get<PageResponse<AuditEventSummary>>(`${BASE}/resources/${category}/${resourceId}/trail`, {
        params,
      })
      .then((r) => r.data),

  getSecurityEvents: (severity?: string, params?: { page?: number; size?: number }) =>
    apiClient
      .get<PageResponse<AuditEventSummary>>(`${BASE}/security-events`, {
        params: { severity, ...params },
      })
      .then((r) => r.data),

  getHipaaReport: (params: DateRangeParams) =>
    apiClient.get<HipaaComplianceReport>(`${BASE}/reports/hipaa`, { params }).then((r) => r.data),

  getSecurityReport: (params: DateRangeParams) =>
    apiClient.get<SecurityReport>(`${BASE}/reports/security`, { params }).then((r) => r.data),

  getAccessReport: (params: DateRangeParams) =>
    apiClient.get<AccessReport>(`${BASE}/reports/access`, { params }).then((r) => r.data),

  getDailySummary: (date: string) =>
    apiClient
      .get<DailyAuditSummary>(`${BASE}/reports/daily`, { params: { date } })
      .then((r) => r.data),

  getMonthlySummary: (year: number, month: number) =>
    apiClient
      .get<MonthlyAuditSummary>(`${BASE}/reports/monthly`, { params: { year, month } })
      .then((r) => r.data),
};

export default auditApi;
