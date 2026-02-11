import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { PageParams } from '@/types';
import { appointmentApi } from '../api/appointment.api';
import type {
  ScheduleAppointmentRequest,
  RescheduleAppointmentRequest,
  CancelAppointmentRequest,
  AppointmentSearchCriteria,
} from '../types/appointment.types';

const KEYS = {
  all: ['appointments'] as const,
  lists: () => [...KEYS.all, 'list'] as const,
  list: (p: unknown) => [...KEYS.lists(), p] as const,
  details: () => [...KEYS.all, 'detail'] as const,
  detail: (id: string) => [...KEYS.details(), id] as const,
  today: (providerId: string) => [...KEYS.all, 'today', providerId] as const,
};

export function useAppointments(criteria: AppointmentSearchCriteria & PageParams) {
  return useQuery({
    queryKey: KEYS.list(criteria),
    queryFn: () => appointmentApi.search(criteria),
  });
}

export function useAppointment(id: string) {
  return useQuery({
    queryKey: KEYS.detail(id),
    queryFn: () => appointmentApi.getById(id),
    enabled: !!id,
  });
}

export function useTodayAppointments(providerId: string) {
  return useQuery({
    queryKey: KEYS.today(providerId),
    queryFn: () => appointmentApi.todayByProvider(providerId),
    enabled: !!providerId,
  });
}

export function usePatientAppointments(patientId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...KEYS.lists(), 'patient', patientId, params],
    queryFn: () => appointmentApi.byPatient(patientId, params),
    enabled: !!patientId,
  });
}

export function useScheduleAppointment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: ScheduleAppointmentRequest) => appointmentApi.schedule(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.lists() }),
  });
}

export function useRescheduleAppointment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: RescheduleAppointmentRequest }) =>
      appointmentApi.reschedule(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useCancelAppointment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: CancelAppointmentRequest }) =>
      appointmentApi.cancel(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useConfirmAppointment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => appointmentApi.confirm(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useCompleteAppointment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, notes }: { id: string; notes?: string }) =>
      appointmentApi.complete(id, notes),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useAppointmentByNumber(appointmentNumber: string) {
  return useQuery({
    queryKey: [...KEYS.all, 'number', appointmentNumber],
    queryFn: () => appointmentApi.getByAppointmentNumber(appointmentNumber),
    enabled: !!appointmentNumber,
  });
}

export function useAppointmentsByDateRange(
  startDate: string,
  endDate: string,
  params?: PageParams,
) {
  return useQuery({
    queryKey: [...KEYS.lists(), 'date-range', startDate, endDate, params],
    queryFn: () => appointmentApi.byDateRange(startDate, endDate, params),
    enabled: !!startDate && !!endDate,
  });
}

export function useProviderAppointments(providerId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...KEYS.lists(), 'provider', providerId, params],
    queryFn: () => appointmentApi.byProvider(providerId, params),
    enabled: !!providerId,
  });
}

export function useCheckSlotAvailability(
  providerId: string,
  date: string,
  startTime: string,
  durationMinutes?: number,
) {
  return useQuery({
    queryKey: [...KEYS.all, 'slot', providerId, date, startTime, durationMinutes],
    queryFn: () => appointmentApi.checkSlot(providerId, date, startTime, durationMinutes),
    enabled: !!providerId && !!date && !!startTime,
  });
}

export function useCheckInAppointment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, notes }: { id: string; notes?: string }) =>
      appointmentApi.checkIn(id, notes),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useNoShowAppointment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => appointmentApi.noShow(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}
