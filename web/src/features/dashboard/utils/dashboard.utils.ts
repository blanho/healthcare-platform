

import { format, parseISO, isToday, isThisWeek, formatDistanceToNow } from 'date-fns';
import type { TrendDataPoint, RecentActivityItem, DashboardStatsResponse } from '../types/dashboard.types';
import { ACTIVITY_TYPE_LABELS, CHART_COLORS } from '../constants';

export function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(value);
}

export function formatCompactNumber(value: number): string {
  if (value >= 1_000_000) {
    return `${(value / 1_000_000).toFixed(1)}M`;
  }
  if (value >= 1_000) {
    return `${(value / 1_000).toFixed(1)}K`;
  }
  return value.toString();
}

export function formatPercentage(value: number, showSign = true): string {
  const sign = showSign && value > 0 ? '+' : '';
  return `${sign}${value.toFixed(1)}%`;
}

export function formatRelativeTime(timestamp: string): string {
  return formatDistanceToNow(parseISO(timestamp), { addSuffix: true });
}

export function formatActivityTime(timestamp: string): string {
  const date = parseISO(timestamp);
  if (isToday(date)) {
    return format(date, 'h:mm a');
  }
  if (isThisWeek(date)) {
    return format(date, 'EEE h:mm a');
  }
  return format(date, 'MMM d, h:mm a');
}

export function getActivityLabel(type: string): string {
  return ACTIVITY_TYPE_LABELS[type] ?? type;
}

export function calculatePercentageChange(current: number, previous: number): number {
  if (previous === 0) return current > 0 ? 100 : 0;
  return ((current - previous) / previous) * 100;
}

export function getTrendDirection(value: number): 'up' | 'down' | 'neutral' {
  if (value > 0) return 'up';
  if (value < 0) return 'down';
  return 'neutral';
}

export function getTrendColor(
  value: number,
  positiveIsGood = true
): 'success' | 'error' | 'default' {
  if (value === 0) return 'default';
  const isPositive = value > 0;
  if (positiveIsGood) {
    return isPositive ? 'success' : 'error';
  }
  return isPositive ? 'error' : 'success';
}

export function fillMissingDates(
  data: TrendDataPoint[],
  startDate: Date,
  endDate: Date
): TrendDataPoint[] {
  const filledData: TrendDataPoint[] = [];
  const dataMap = new Map(data.map((d) => [d.date, d]));

  const current = new Date(startDate);
  while (current <= endDate) {
    const dateStr = format(current, 'yyyy-MM-dd');
    filledData.push(
      dataMap.get(dateStr) ?? {
        date: dateStr,
        value: 0,
        label: format(current, 'MMM d'),
      }
    );
    current.setDate(current.getDate() + 1);
  }

  return filledData;
}

export function calculateMovingAverage(data: TrendDataPoint[], window = 7): TrendDataPoint[] {
  return data.map((point, index) => {
    const start = Math.max(0, index - window + 1);
    const windowData = data.slice(start, index + 1);
    const avg = windowData.reduce((sum, p) => sum + p.value, 0) / windowData.length;
    return {
      ...point,
      value: Math.round(avg * 100) / 100,
    };
  });
}

export function getChartColor(index: number): string {
  const colors = Object.values(CHART_COLORS);
  return colors[index % colors.length];
}

export function formatChartDateLabel(dateStr: string, period: string): string {
  const date = parseISO(dateStr);
  if (period === '7d' || period === '14d') {
    return format(date, 'EEE');
  }
  if (period === '30d') {
    return format(date, 'MMM d');
  }
  if (period === '90d' || period === '1y') {
    return format(date, 'MMM');
  }
  return format(date, 'MMM d');
}

export function calculateCompletionRate(completed: number, total: number): number {
  if (total === 0) return 0;
  return Math.round((completed / total) * 100);
}

export function calculateCollectionRate(collected: number, total: number): number {
  if (total === 0) return 100;
  return Math.round((collected / total) * 100);
}

export function getStatsSummary(stats: DashboardStatsResponse): string {
  const parts: string[] = [];
  if (stats.appointmentsToday > 0) {
    parts.push(`${stats.appointmentsToday} appointments today`);
  }
  if (stats.recordsPendingReview > 0) {
    parts.push(`${stats.recordsPendingReview} records pending review`);
  }
  if (stats.overdueInvoices > 0) {
    parts.push(`${stats.overdueInvoices} overdue invoices`);
  }
  return parts.join(' • ') || 'All caught up!';
}

export function groupActivitiesByDate(
  activities: RecentActivityItem[]
): Record<string, RecentActivityItem[]> {
  return activities.reduce(
    (acc, activity) => {
      const date = format(parseISO(activity.timestamp), 'yyyy-MM-dd');
      if (!acc[date]) {
        acc[date] = [];
      }
      acc[date].push(activity);
      return acc;
    },
    {} as Record<string, RecentActivityItem[]>
  );
}

export function getActivityRoute(activity: RecentActivityItem): string {
  const routes: Record<string, string> = {
    patient_registered: `/patients/${activity.entityId}`,
    appointment_scheduled: `/appointments/${activity.entityId}`,
    appointment_completed: `/appointments/${activity.entityId}`,
    record_created: `/medical-records/${activity.entityId}`,
    invoice_paid: `/billing/invoices/${activity.entityId}`,
    provider_added: `/providers/${activity.entityId}`,
  };
  return routes[activity.type] ?? '/';
}

export function filterActivitiesByType(
  activities: RecentActivityItem[],
  types: string[]
): RecentActivityItem[] {
  if (types.length === 0) return activities;
  return activities.filter((a) => types.includes(a.type));
}
