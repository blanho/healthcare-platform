

import { format, parseISO, differenceInDays, isPast } from 'date-fns';
import type { ProviderStatus, ProviderType, DayOfWeek } from '@/types';
import type { ProviderResponse, LicenseResponse, ScheduleResponse } from '../types/provider.types';
import {
  PROVIDER_TYPE_LABELS,
  PROVIDER_STATUS_LABELS,
  DAY_OF_WEEK_LABELS,
  DAY_OF_WEEK_SHORT_LABELS,
} from '../constants';

export function formatProviderName(
  firstName: string,
  lastName: string,
  middleName?: string | null
): string {
  if (middleName) {
    return `${firstName} ${middleName} ${lastName}`;
  }
  return `${firstName} ${lastName}`;
}

export function formatProviderDisplayName(
  firstName: string,
  lastName: string,
  providerType?: ProviderType
): string {
  const isMedicalDoctor = providerType && ['PHYSICIAN', 'SURGEON', 'SPECIALIST', 'PSYCHIATRIST', 'CARDIOLOGIST', 'NEUROLOGIST', 'PEDIATRICIAN', 'DERMATOLOGIST', 'RADIOLOGIST', 'GENERAL_PRACTITIONER'].includes(providerType);
  const prefix = isMedicalDoctor ? 'Dr. ' : '';
  return `${prefix}${firstName} ${lastName}`;
}

export function getProviderInitials(firstName: string, lastName: string): string {
  return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
}

export function getProviderTypeLabel(type: ProviderType): string {
  return PROVIDER_TYPE_LABELS[type];
}

export function getProviderStatusLabel(status: ProviderStatus): string {
  return PROVIDER_STATUS_LABELS[status];
}

export function getDayLabel(day: DayOfWeek, short = false): string {
  return short ? DAY_OF_WEEK_SHORT_LABELS[day] : DAY_OF_WEEK_LABELS[day];
}

export function formatConsultationFee(fee: number | null): string {
  if (fee === null) return 'Not specified';
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(fee);
}

export function formatExperience(years: number | null): string {
  if (years === null) return 'Not specified';
  if (years === 0) return 'New practitioner';
  if (years === 1) return '1 year';
  return `${years} years`;
}

export function isLicenseExpired(expiryDate: string): boolean {
  return isPast(parseISO(expiryDate));
}

export function getDaysUntilExpiry(expiryDate: string): number {
  return differenceInDays(parseISO(expiryDate), new Date());
}

export function isLicenseExpiringSoon(expiryDate: string, thresholdDays = 90): boolean {
  const days = getDaysUntilExpiry(expiryDate);
  return days > 0 && days <= thresholdDays;
}

export function formatLicenseStatus(license: LicenseResponse): string {
  if (!license.valid) return 'Expired';
  if (license.daysUntilExpiry <= 30) return `Expires in ${license.daysUntilExpiry} days`;
  if (license.daysUntilExpiry <= 90) return 'Expiring soon';
  return 'Valid';
}

export function getLicenseStatusSeverity(
  license: LicenseResponse
): 'success' | 'warning' | 'error' {
  if (!license.valid) return 'error';
  if (license.daysUntilExpiry <= 30) return 'error';
  if (license.daysUntilExpiry <= 90) return 'warning';
  return 'success';
}

export function formatTime24to12(time: string): string {
  const [hours, minutes] = time.split(':');
  const h = parseInt(hours);
  const ampm = h >= 12 ? 'PM' : 'AM';
  const hour12 = h % 12 || 12;
  return `${hour12}:${minutes} ${ampm}`;
}

export function formatScheduleTime(startTime: string, endTime: string): string {
  const formatTime = (time: string) => {
    const [hours, minutes] = time.split(':').map(Number);
    const date = new Date();
    date.setHours(hours, minutes, 0, 0);
    return format(date, 'h:mm a');
  };
  return `${formatTime(startTime)} - ${formatTime(endTime)}`;
}

export function getActiveSchedules(schedules: ScheduleResponse[]): ScheduleResponse[] {
  return schedules.filter((s) => s.active);
}

export function sortSchedulesByDay(schedules: ScheduleResponse[]): ScheduleResponse[] {
  const dayOrder: DayOfWeek[] = [
    'MONDAY',
    'TUESDAY',
    'WEDNESDAY',
    'THURSDAY',
    'FRIDAY',
    'SATURDAY',
    'SUNDAY',
  ];
  return [...schedules].sort(
    (a, b) =>
      dayOrder.indexOf(a.dayOfWeek as DayOfWeek) - dayOrder.indexOf(b.dayOfWeek as DayOfWeek)
  );
}

export function getWeeklyAvailability(
  schedules: ScheduleResponse[]
): { day: DayOfWeek; available: boolean }[] {
  const days: DayOfWeek[] = [
    'MONDAY',
    'TUESDAY',
    'WEDNESDAY',
    'THURSDAY',
    'FRIDAY',
    'SATURDAY',
    'SUNDAY',
  ];
  const activeSchedules = getActiveSchedules(schedules);
  return days.map((day) => ({
    day,
    available: activeSchedules.some((s) => s.dayOfWeek === day),
  }));
}

export function canAcceptPatients(provider: ProviderResponse): boolean {
  return provider.status === 'ACTIVE' && provider.acceptingPatients;
}

export function isAvailableForAppointments(provider: ProviderResponse): boolean {
  return provider.status === 'ACTIVE' && provider.license.valid;
}

export function isInGoodStanding(provider: ProviderResponse): boolean {
  return (
    provider.status === 'ACTIVE' &&
    provider.license.valid &&
    provider.license.daysUntilExpiry > 30
  );
}

export function filterBySpecialization<T extends { specialization: string | null }>(
  providers: T[],
  specialization: string
): T[] {
  const searchTerm = specialization.toLowerCase();
  return providers.filter(
    (p) => p.specialization?.toLowerCase().includes(searchTerm) ?? false
  );
}

export function filterByType<T extends { providerType: ProviderType }>(
  providers: T[],
  type: ProviderType
): T[] {
  return providers.filter((p) => p.providerType === type);
}

export function filterAcceptingPatients<T extends { acceptingPatients: boolean }>(
  providers: T[]
): T[] {
  return providers.filter((p) => p.acceptingPatients);
}

export function searchByName<T extends { fullName?: string; displayName?: string }>(
  providers: T[],
  searchTerm: string
): T[] {
  const term = searchTerm.toLowerCase();
  return providers.filter(
    (p) =>
      p.fullName?.toLowerCase().includes(term) ||
      p.displayName?.toLowerCase().includes(term)
  );
}
