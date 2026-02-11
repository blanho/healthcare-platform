import type { ProviderType, ProviderStatus, DayOfWeek } from '@/types';

export interface CreateProviderRequest {
  firstName: string;
  middleName?: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  providerType: ProviderType;
  specialization?: string;
  license: LicenseRequest;
  npiNumber?: string;
  qualification?: string;
  yearsOfExperience?: number;
  consultationFee?: number;
  acceptingPatients?: boolean;
}

export interface LicenseRequest {
  licenseNumber: string;
  licenseState: string;
  expiryDate: string;
}

export interface UpdateProviderRequest {
  firstName?: string;
  middleName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  providerType?: ProviderType;
  specialization?: string;
  license?: Partial<LicenseRequest>;
  npiNumber?: string;
  qualification?: string;
  yearsOfExperience?: number;
  consultationFee?: number;
  acceptingPatients?: boolean;
}

export interface ProviderResponse {
  id: string;
  providerNumber: string;
  firstName: string;
  middleName: string | null;
  lastName: string;
  fullName: string;
  displayName: string;
  email: string;
  phoneNumber: string | null;
  providerType: ProviderType;
  specialization: string | null;
  license: LicenseResponse;
  npiNumber: string | null;
  qualification: string | null;
  yearsOfExperience: number | null;
  consultationFee: number | null;
  acceptingPatients: boolean;
  status: ProviderStatus;
  schedules: ScheduleResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface LicenseResponse {
  licenseNumber: string;
  licenseState: string;
  expiryDate: string;
  valid: boolean;
  daysUntilExpiry: number;
}

export interface ScheduleResponse {
  id: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  slotDurationMinutes: number;
  availableSlotCount: number;
  active: boolean;
}

export interface ProviderSummaryResponse {
  id: string;
  providerNumber: string;
  displayName: string;
  email: string;
  providerType: ProviderType;
  specialization: string | null;
  acceptingPatients: boolean;
  status: ProviderStatus;
}

export interface ScheduleRequest {
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
  slotDurationMinutes: number;
}

export interface ProviderSearchCriteria {
  name?: string;
  email?: string;
  providerType?: ProviderType;
  specialization?: string;
  status?: ProviderStatus;
  acceptingPatients?: boolean;
}
