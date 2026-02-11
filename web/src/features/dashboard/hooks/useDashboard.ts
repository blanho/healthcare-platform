import { useQuery } from '@tanstack/react-query';
import { dashboardApi } from '../api/dashboard.api';

const KEYS = {
  all: ['dashboard'] as const,
  stats: () => [...KEYS.all, 'stats'] as const,
  appointmentTrends: (period: string) => [...KEYS.all, 'trends', 'appointments', period] as const,
  revenueTrends: (period: string) => [...KEYS.all, 'trends', 'revenue', period] as const,
  activity: (limit: number) => [...KEYS.all, 'activity', limit] as const,
  upcomingAppointments: (limit: number) => [...KEYS.all, 'upcoming', limit] as const,
};

export function useDashboardStats() {
  return useQuery({
    queryKey: KEYS.stats(),
    queryFn: () => dashboardApi.getStats(),
    staleTime: 30 * 1000,
    refetchInterval: 60 * 1000,
  });
}

export function useAppointmentTrends(period: 'week' | 'month' | 'quarter' = 'month') {
  return useQuery({
    queryKey: KEYS.appointmentTrends(period),
    queryFn: () => dashboardApi.getAppointmentTrends(period),
    staleTime: 5 * 60 * 1000,
  });
}

export function useRevenueTrends(period: 'week' | 'month' | 'quarter' = 'month') {
  return useQuery({
    queryKey: KEYS.revenueTrends(period),
    queryFn: () => dashboardApi.getRevenueTrends(period),
    staleTime: 5 * 60 * 1000,
  });
}

export function useRecentActivity(limit: number = 10) {
  return useQuery({
    queryKey: KEYS.activity(limit),
    queryFn: () => dashboardApi.getRecentActivity(limit),
    staleTime: 30 * 1000,
    refetchInterval: 60 * 1000,
  });
}

export function useUpcomingAppointments(limit: number = 5) {
  return useQuery({
    queryKey: KEYS.upcomingAppointments(limit),
    queryFn: () => dashboardApi.getUpcomingAppointments(limit),
    staleTime: 30 * 1000,
    refetchInterval: 60 * 1000,
  });
}
