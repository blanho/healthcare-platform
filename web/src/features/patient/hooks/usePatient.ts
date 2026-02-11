import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { PageParams } from '@/types';
import { patientApi } from '../api/patient.api';
import type {
  CreatePatientRequest,
  UpdatePatientRequest,
  PatientSearchCriteria,
} from '../types/patient.types';

const KEYS = {
  all: ['patients'] as const,
  lists: () => [...KEYS.all, 'list'] as const,
  list: (params: unknown) => [...KEYS.lists(), params] as const,
  details: () => [...KEYS.all, 'detail'] as const,
  detail: (id: string) => [...KEYS.details(), id] as const,
};

export function usePatients(params?: PageParams) {
  return useQuery({
    queryKey: KEYS.list(params),
    queryFn: () => patientApi.list(params),
  });
}

export function usePatientSearch(criteria: PatientSearchCriteria & PageParams) {
  return useQuery({
    queryKey: KEYS.list(criteria),
    queryFn: () => patientApi.search(criteria),
    enabled: Object.values(criteria).some((v) => v !== undefined && v !== ''),
  });
}

export function usePatient(id: string) {
  return useQuery({
    queryKey: KEYS.detail(id),
    queryFn: () => patientApi.getById(id),
    enabled: !!id,
  });
}

export function useCreatePatient() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreatePatientRequest) => patientApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useUpdatePatient() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdatePatientRequest }) =>
      patientApi.update(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: KEYS.detail(id) });
      queryClient.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useDeletePatient() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => patientApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function usePatientByMrn(mrn: string) {
  return useQuery({
    queryKey: [...KEYS.all, 'mrn', mrn],
    queryFn: () => patientApi.getByMrn(mrn),
    enabled: !!mrn,
  });
}

export function useActivatePatient() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => patientApi.activate(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: KEYS.detail(id) });
      queryClient.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useDeactivatePatient() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => patientApi.deactivate(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: KEYS.detail(id) });
      queryClient.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useCanSchedulePatient(id: string) {
  return useQuery({
    queryKey: [...KEYS.all, 'can-schedule', id],
    queryFn: () => patientApi.canSchedule(id),
    enabled: !!id,
  });
}
