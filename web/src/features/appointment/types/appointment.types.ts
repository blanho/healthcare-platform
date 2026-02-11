import type { AppointmentType, AppointmentStatus } from '@/types';

export interface ScheduleAppointmentRequest {
  patientId: string;
  providerId: string;
  scheduledDate: string;
  startTime: string;
  durationMinutes?: number;
  appointmentType: AppointmentType;
  reasonForVisit?: string;
  notes?: string;
}

export interface RescheduleAppointmentRequest {
  newDate: string;
  newStartTime: string;
  durationMinutes?: number;
}

export interface CancelAppointmentRequest {
  reason: string;
  cancelledByPatient: boolean;
}

export interface AppointmentResponse {
  id: string;
  appointmentNumber: string;
  patientId: string;
  providerId: string;
  scheduledDate: string;
  startTime: string;
  endTime: string;
  durationMinutes: number;
  appointmentType: AppointmentType;
  status: AppointmentStatus;
  reasonForVisit: string | null;
  notes: string | null;
  cancellation: CancellationInfo | null;
  checkIn: CheckInInfo | null;
  completedAt: string | null;
  completionNotes: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CancellationInfo {
  reason: string;
  cancelledAt: string;
  cancelledByPatient: boolean;
}

export interface CheckInInfo {
  checkedInAt: string;
  notes: string | null;
}

export interface AppointmentSummaryResponse {
  id: string;
  appointmentNumber: string;
  patientId: string;
  providerId: string;
  scheduledDate: string;
  startTime: string;
  endTime: string;
  appointmentType: AppointmentType;
  status: AppointmentStatus;
}

export interface AppointmentSearchCriteria {
  patientId?: string;
  providerId?: string;
  startDate?: string;
  endDate?: string;
  appointmentType?: AppointmentType;
  status?: AppointmentStatus;
}

export interface SlotAvailabilityResponse {
  available: boolean;
  providerId: string;
  date: string;
  startTime: string;
  endTime: string;
}
