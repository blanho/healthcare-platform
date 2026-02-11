

import {
  format,
  parseISO,
  addMinutes,
  isAfter,
  isBefore,
  isSameDay,
  startOfDay,
  endOfDay,
} from 'date-fns';
import type { AppointmentStatus, AppointmentType } from '@/types';
import {
  APPOINTMENT_STATUS_LABELS,
  APPOINTMENT_TYPE_LABELS,
  TIME_SLOT_CONFIG,
} from '../constants';

export function formatAppointmentDate(date: string, formatStr = 'MMM d, yyyy'): string {
  return format(parseISO(date), formatStr);
}

export function formatAppointmentTime(time: string): string {
  const [hours, minutes] = time.split(':').map(Number);
  const date = new Date();
  date.setHours(hours, minutes, 0, 0);
  return format(date, 'h:mm a');
}

export function formatAppointmentTimeRange(startTime: string, endTime: string): string {
  return `${formatAppointmentTime(startTime)} - ${formatAppointmentTime(endTime)}`;
}

export function formatDuration(minutes: number): string {
  if (minutes < 60) {
    return `${minutes} min`;
  }
  const hours = Math.floor(minutes / 60);
  const remainingMinutes = minutes % 60;
  if (remainingMinutes === 0) {
    return hours === 1 ? '1 hour' : `${hours} hours`;
  }
  return `${hours}h ${remainingMinutes}m`;
}

export function getStatusLabel(status: AppointmentStatus): string {
  return APPOINTMENT_STATUS_LABELS[status];
}

export function getTypeLabel(type: AppointmentType): string {
  return APPOINTMENT_TYPE_LABELS[type];
}

export function canCancel(status: AppointmentStatus): boolean {
  return ['SCHEDULED', 'CONFIRMED'].includes(status);
}

export function canReschedule(status: AppointmentStatus): boolean {
  return ['SCHEDULED', 'CONFIRMED'].includes(status);
}

export function canCheckIn(status: AppointmentStatus): boolean {
  return status === 'CONFIRMED';
}

export function canStart(status: AppointmentStatus): boolean {
  return status === 'CHECKED_IN';
}

export function canComplete(status: AppointmentStatus): boolean {
  return status === 'IN_PROGRESS';
}

export function isTerminalStatus(status: AppointmentStatus): boolean {
  return ['COMPLETED', 'CANCELLED', 'NO_SHOW'].includes(status);
}

export function isActiveStatus(status: AppointmentStatus): boolean {
  return ['SCHEDULED', 'CONFIRMED', 'CHECKED_IN', 'IN_PROGRESS'].includes(status);
}

export function generateTimeSlots(
  startHour = TIME_SLOT_CONFIG.START_HOUR,
  endHour = TIME_SLOT_CONFIG.END_HOUR,
  intervalMinutes = TIME_SLOT_CONFIG.SLOT_INTERVAL_MINUTES
): string[] {
  const slots: string[] = [];
  const baseDate = new Date();
  baseDate.setHours(startHour, 0, 0, 0);

  const endDate = new Date();
  endDate.setHours(endHour, 0, 0, 0);

  let current = baseDate;
  while (isBefore(current, endDate)) {
    slots.push(format(current, 'HH:mm'));
    current = addMinutes(current, intervalMinutes);
  }

  return slots;
}

export function calculateEndTime(startTime: string, durationMinutes: number): string {
  const [hours, minutes] = startTime.split(':').map(Number);
  const date = new Date();
  date.setHours(hours, minutes, 0, 0);
  return format(addMinutes(date, durationMinutes), 'HH:mm');
}

export function doTimesOverlap(
  start1: string,
  end1: string,
  start2: string,
  end2: string
): boolean {
  const toMinutes = (time: string) => {
    const [h, m] = time.split(':').map(Number);
    return h * 60 + m;
  };

  const s1 = toMinutes(start1);
  const e1 = toMinutes(end1);
  const s2 = toMinutes(start2);
  const e2 = toMinutes(end2);

  return s1 < e2 && s2 < e1;
}

export function filterByDateRange<T extends { scheduledDate: string }>(
  appointments: T[],
  startDate: Date,
  endDate: Date
): T[] {
  return appointments.filter((apt) => {
    const date = parseISO(apt.scheduledDate);
    return (
      (isAfter(date, startOfDay(startDate)) || isSameDay(date, startDate)) &&
      (isBefore(date, endOfDay(endDate)) || isSameDay(date, endDate))
    );
  });
}

export function groupByDate<T extends { scheduledDate: string }>(
  appointments: T[]
): Record<string, T[]> {
  return appointments.reduce(
    (acc, apt) => {
      const dateKey = apt.scheduledDate;
      if (!acc[dateKey]) {
        acc[dateKey] = [];
      }
      acc[dateKey].push(apt);
      return acc;
    },
    {} as Record<string, T[]>
  );
}

export function groupByStatus<T extends { status: AppointmentStatus }>(
  appointments: T[]
): Record<AppointmentStatus, T[]> {
  return appointments.reduce(
    (acc, apt) => {
      if (!acc[apt.status]) {
        acc[apt.status] = [];
      }
      acc[apt.status].push(apt);
      return acc;
    },
    {} as Record<AppointmentStatus, T[]>
  );
}

export function sortByDateTime<T extends { scheduledDate: string; startTime: string }>(
  appointments: T[],
  direction: 'asc' | 'desc' = 'asc'
): T[] {
  return [...appointments].sort((a, b) => {
    const dateCompare = a.scheduledDate.localeCompare(b.scheduledDate);
    if (dateCompare !== 0) {
      return direction === 'asc' ? dateCompare : -dateCompare;
    }
    const timeCompare = a.startTime.localeCompare(b.startTime);
    return direction === 'asc' ? timeCompare : -timeCompare;
  });
}

export function countByStatus<T extends { status: AppointmentStatus }>(
  appointments: T[]
): Record<AppointmentStatus, number> {
  const counts = {} as Record<AppointmentStatus, number>;
  appointments.forEach((apt) => {
    counts[apt.status] = (counts[apt.status] || 0) + 1;
  });
  return counts;
}

export function getTodaysAppointments<T extends { scheduledDate: string }>(
  appointments: T[]
): T[] {
  const today = new Date();
  return appointments.filter((apt) => isSameDay(parseISO(apt.scheduledDate), today));
}

export function getUpcomingAppointments<T extends { scheduledDate: string }>(
  appointments: T[]
): T[] {
  const today = startOfDay(new Date());
  return appointments
    .filter((apt) => isAfter(parseISO(apt.scheduledDate), today))
    .sort((a, b) => a.scheduledDate.localeCompare(b.scheduledDate));
}
