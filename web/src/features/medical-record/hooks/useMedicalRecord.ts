import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { PageParams } from '@/types';
import { medicalRecordApi } from '../api/medical-record.api';
import type {
  CreateMedicalRecordRequest,
  UpdateMedicalRecordRequest,
  MedicalRecordSearchCriteria,
} from '../types/medical-record.types';

const KEYS = {
  all: ['medical-records'] as const,
  lists: () => [...KEYS.all, 'list'] as const,
  list: (p: unknown) => [...KEYS.lists(), p] as const,
  details: () => [...KEYS.all, 'detail'] as const,
  detail: (id: string) => [...KEYS.details(), id] as const,
  timeline: (patientId: string) => [...KEYS.all, 'timeline', patientId] as const,
};

export function useMedicalRecords(criteria: MedicalRecordSearchCriteria & PageParams) {
  return useQuery({
    queryKey: KEYS.list(criteria),
    queryFn: () => medicalRecordApi.search(criteria),
  });
}

export function useMedicalRecord(id: string) {
  return useQuery({
    queryKey: KEYS.detail(id),
    queryFn: () => medicalRecordApi.getById(id),
    enabled: !!id,
  });
}

export function usePatientTimeline(patientId: string) {
  return useQuery({
    queryKey: KEYS.timeline(patientId),
    queryFn: () => medicalRecordApi.timeline(patientId),
    enabled: !!patientId,
  });
}

export function useCreateMedicalRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateMedicalRecordRequest) => medicalRecordApi.create(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.lists() }),
  });
}

export function useUpdateMedicalRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateMedicalRecordRequest }) =>
      medicalRecordApi.update(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function usePatientRecords(patientId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...KEYS.lists(), 'patient', patientId, params],
    queryFn: () => medicalRecordApi.byPatient(patientId, params),
    enabled: !!patientId,
  });
}

export function useFinalizeMedicalRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => medicalRecordApi.finalize(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useMedicalRecordByNumber(recordNumber: string) {
  return useQuery({
    queryKey: [...KEYS.all, 'number', recordNumber],
    queryFn: () => medicalRecordApi.getByRecordNumber(recordNumber),
    enabled: !!recordNumber,
  });
}

export function useMedicalRecordByAppointment(appointmentId: string) {
  return useQuery({
    queryKey: [...KEYS.all, 'appointment', appointmentId],
    queryFn: () => medicalRecordApi.byAppointment(appointmentId),
    enabled: !!appointmentId,
  });
}

export function useProviderRecords(providerId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...KEYS.lists(), 'provider', providerId, params],
    queryFn: () => medicalRecordApi.byProvider(providerId, params),
    enabled: !!providerId,
  });
}

export function useProviderDrafts(providerId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...KEYS.lists(), 'drafts', providerId, params],
    queryFn: () => medicalRecordApi.drafts(providerId, params),
    enabled: !!providerId,
  });
}

export function useMedicalRecordHistory(id: string) {
  return useQuery({
    queryKey: [...KEYS.all, 'history', id],
    queryFn: () => medicalRecordApi.history(id),
    enabled: !!id,
  });
}

export function useAmendMedicalRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: Parameters<typeof medicalRecordApi.amend>[1];
    }) => medicalRecordApi.amend(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useVoidMedicalRecord() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Parameters<typeof medicalRecordApi.void>[1] }) =>
      medicalRecordApi.void(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useUpdateVitals() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: Parameters<typeof medicalRecordApi.updateVitals>[1];
    }) => medicalRecordApi.updateVitals(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
    },
  });
}

export function useUpdateSoapNote() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: Parameters<typeof medicalRecordApi.updateSoapNote>[1];
    }) => medicalRecordApi.updateSoapNote(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
    },
  });
}

export function useAddDiagnosis() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: Parameters<typeof medicalRecordApi.addDiagnosis>[1];
    }) => medicalRecordApi.addDiagnosis(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
    },
  });
}

export function useRemoveDiagnosis() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, diagnosisCode }: { id: string; diagnosisCode: string }) =>
      medicalRecordApi.removeDiagnosis(id, diagnosisCode),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
    },
  });
}
