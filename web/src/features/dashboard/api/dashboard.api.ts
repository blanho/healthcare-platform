import { apiClient } from '@/lib/api-client';
import type {
  DashboardStatsResponse,
  AppointmentTrendResponse,
  RevenueTrendResponse,
  RecentActivityItem,
  UpcomingAppointment,
} from '../types/dashboard.types';

const BASE_URL = '/api/v1/dashboard';

export const dashboardApi = {
  getStats: () =>
    apiClient.get<DashboardStatsResponse>(`${BASE_URL}/stats`).then((res) => res.data),

  getAppointmentTrends: (period: 'week' | 'month' | 'quarter' = 'month') =>
    apiClient
      .get<AppointmentTrendResponse>(`${BASE_URL}/trends/appointments`, {
        params: { period },
      })
      .then((res) => res.data),

  getRevenueTrends: (period: 'week' | 'month' | 'quarter' = 'month') =>
    apiClient
      .get<RevenueTrendResponse>(`${BASE_URL}/trends/revenue`, { params: { period } })
      .then((res) => res.data),

  getRecentActivity: (limit: number = 10) =>
    apiClient
      .get<RecentActivityItem[]>(`${BASE_URL}/activity`, { params: { limit } })
      .then((res) => res.data),

  getUpcomingAppointments: (limit: number = 5) =>
    apiClient
      .get<UpcomingAppointment[]>(`${BASE_URL}/appointments/upcoming`, {
        params: { limit },
      })
      .then((res) => res.data),
};
