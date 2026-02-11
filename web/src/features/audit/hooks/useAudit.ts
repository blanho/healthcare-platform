

import { useQuery } from '@tanstack/react-query';
import { auditApi } from '../api/audit.api';
import type { AuditSearchParams, DateRangeParams } from '../types/audit.types';

export const auditKeys = {
  all: ['audit'] as const,
  events: () => [...auditKeys.all, 'events'] as const,
  event: (id: string) => [...auditKeys.events(), id] as const,
  search: (params: AuditSearchParams) => [...auditKeys.events(), 'search', params] as const,
  recent: (limit: number) => [...auditKeys.events(), 'recent', limit] as const,
  userTrail: (userId: string, params?: DateRangeParams) =>
    [...auditKeys.all, 'users', userId, 'trail', params] as const,
  userActivity: (userId: string, params?: DateRangeParams) =>
    [...auditKeys.all, 'users', userId, 'activity', params] as const,
  userEventCount: (userId: string, days: number) =>
    [...auditKeys.all, 'users', userId, 'count', days] as const,
  userFailedLogins: (userId: string, days: number) =>
    [...auditKeys.all, 'users', userId, 'failed-logins', days] as const,
  userAnomaly: (userId: string, hours: number) =>
    [...auditKeys.all, 'users', userId, 'anomaly', hours] as const,
  patientAccess: (patientId: string, params?: DateRangeParams) =>
    [...auditKeys.all, 'patients', patientId, 'access', params] as const,
  patientTrail: (patientId: string, params?: DateRangeParams) =>
    [...auditKeys.all, 'patients', patientId, 'trail', params] as const,
  resourceTrail: (category: string, resourceId: string) =>
    [...auditKeys.all, 'resources', category, resourceId] as const,
  securityEvents: (severity?: string) => [...auditKeys.all, 'security', severity] as const,
  hipaaReport: (params: DateRangeParams) => [...auditKeys.all, 'reports', 'hipaa', params] as const,
  securityReport: (params: DateRangeParams) =>
    [...auditKeys.all, 'reports', 'security', params] as const,
  accessReport: (params: DateRangeParams) =>
    [...auditKeys.all, 'reports', 'access', params] as const,
  dailySummary: (date: string) => [...auditKeys.all, 'reports', 'daily', date] as const,
  monthlySummary: (year: number, month: number) =>
    [...auditKeys.all, 'reports', 'monthly', year, month] as const,
};

export function useAuditEvent(id: string) {
  return useQuery({
    queryKey: auditKeys.event(id),
    queryFn: () => auditApi.getById(id),
    enabled: !!id,
  });
}

export function useAuditSearch(params: AuditSearchParams) {
  return useQuery({
    queryKey: auditKeys.search(params),
    queryFn: () => auditApi.search(params),
  });
}

export function useRecentAuditEvents(limit = 50) {
  return useQuery({
    queryKey: auditKeys.recent(limit),
    queryFn: () => auditApi.getRecent(limit),
  });
}

export function useUserAuditTrail(
  userId: string,
  params?: DateRangeParams & { page?: number; size?: number },
) {
  return useQuery({
    queryKey: auditKeys.userTrail(userId, params),
    queryFn: () => auditApi.getUserTrail(userId, params),
    enabled: !!userId,
  });
}

export function useUserActivitySummary(userId: string, params?: DateRangeParams) {
  return useQuery({
    queryKey: auditKeys.userActivity(userId, params),
    queryFn: () => auditApi.getUserActivitySummary(userId, params),
    enabled: !!userId,
  });
}

export function useUserEventCount(userId: string, days = 30) {
  return useQuery({
    queryKey: auditKeys.userEventCount(userId, days),
    queryFn: () => auditApi.countUserEvents(userId, days),
    enabled: !!userId,
  });
}

export function useUserFailedLogins(userId: string, days = 7) {
  return useQuery({
    queryKey: auditKeys.userFailedLogins(userId, days),
    queryFn: () => auditApi.countFailedLogins(userId, days),
    enabled: !!userId,
  });
}

export function useUserAnomalyCheck(userId: string, hours = 24) {
  return useQuery({
    queryKey: auditKeys.userAnomaly(userId, hours),
    queryFn: () => auditApi.checkUserAnomaly(userId, hours),
    enabled: !!userId,
    refetchInterval: 5 * 60 * 1000,
  });
}

export function usePatientAccessHistory(patientId: string, params?: DateRangeParams) {
  return useQuery({
    queryKey: auditKeys.patientAccess(patientId, params),
    queryFn: () => auditApi.getPatientAccessHistory(patientId, params),
    enabled: !!patientId,
  });
}

export function usePatientAuditTrail(
  patientId: string,
  params?: DateRangeParams & { page?: number; size?: number },
) {
  return useQuery({
    queryKey: auditKeys.patientTrail(patientId, params),
    queryFn: () => auditApi.getPatientTrail(patientId, params),
    enabled: !!patientId,
  });
}

export function useResourceAuditTrail(
  category: string,
  resourceId: string,
  params?: { page?: number; size?: number },
) {
  return useQuery({
    queryKey: auditKeys.resourceTrail(category, resourceId),
    queryFn: () => auditApi.getResourceTrail(category, resourceId, params),
    enabled: !!category && !!resourceId,
  });
}

export function useSecurityEvents(severity?: string, params?: { page?: number; size?: number }) {
  return useQuery({
    queryKey: auditKeys.securityEvents(severity),
    queryFn: () => auditApi.getSecurityEvents(severity, params),
  });
}

export function useHipaaReport(params: DateRangeParams, enabled = true) {
  return useQuery({
    queryKey: auditKeys.hipaaReport(params),
    queryFn: () => auditApi.getHipaaReport(params),
    enabled: enabled && !!params.startDate && !!params.endDate,
    staleTime: 10 * 60 * 1000,
  });
}

export function useSecurityReport(params: DateRangeParams, enabled = true) {
  return useQuery({
    queryKey: auditKeys.securityReport(params),
    queryFn: () => auditApi.getSecurityReport(params),
    enabled: enabled && !!params.startDate && !!params.endDate,
    staleTime: 10 * 60 * 1000,
  });
}

export function useAccessReport(params: DateRangeParams, enabled = true) {
  return useQuery({
    queryKey: auditKeys.accessReport(params),
    queryFn: () => auditApi.getAccessReport(params),
    enabled: enabled && !!params.startDate && !!params.endDate,
    staleTime: 10 * 60 * 1000,
  });
}

export function useDailyAuditSummary(date: string) {
  return useQuery({
    queryKey: auditKeys.dailySummary(date),
    queryFn: () => auditApi.getDailySummary(date),
    enabled: !!date,
    staleTime: 5 * 60 * 1000,
  });
}

export function useMonthlyAuditSummary(year: number, month: number) {
  return useQuery({
    queryKey: auditKeys.monthlySummary(year, month),
    queryFn: () => auditApi.getMonthlySummary(year, month),
    enabled: !!year && !!month,
    staleTime: 30 * 60 * 1000,
  });
}
