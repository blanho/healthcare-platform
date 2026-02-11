import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { PageParams } from '@/types';
import { providerApi } from '../api/provider.api';
import type {
  CreateProviderRequest,
  UpdateProviderRequest,
  ProviderSearchCriteria,
} from '../types/provider.types';

const KEYS = {
  all: ['providers'] as const,
  lists: () => [...KEYS.all, 'list'] as const,
  list: (p: unknown) => [...KEYS.lists(), p] as const,
  details: () => [...KEYS.all, 'detail'] as const,
  detail: (id: string) => [...KEYS.details(), id] as const,
  specializations: () => [...KEYS.all, 'specializations'] as const,
};

export function useProviders(params?: PageParams) {
  return useQuery({ queryKey: KEYS.list(params), queryFn: () => providerApi.list(params) });
}

export function useProviderSearch(criteria: ProviderSearchCriteria & PageParams) {
  return useQuery({
    queryKey: KEYS.list(criteria),
    queryFn: () => providerApi.search(criteria),
    enabled: Object.values(criteria).some((v) => v !== undefined && v !== ''),
  });
}

export function useProvider(id: string) {
  return useQuery({
    queryKey: KEYS.detail(id),
    queryFn: () => providerApi.getById(id),
    enabled: !!id,
  });
}

export function useSpecializations() {
  return useQuery({
    queryKey: KEYS.specializations(),
    queryFn: () => providerApi.specializations(),
  });
}

export function useCreateProvider() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateProviderRequest) => providerApi.create(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: KEYS.lists() }),
  });
}

export function useUpdateProvider() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateProviderRequest }) =>
      providerApi.update(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useProviderByNumber(providerNumber: string) {
  return useQuery({
    queryKey: [...KEYS.all, 'number', providerNumber],
    queryFn: () => providerApi.getByProviderNumber(providerNumber),
    enabled: !!providerNumber,
  });
}

export function useActivateProvider() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => providerApi.activate(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useDeactivateProvider() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => providerApi.deactivate(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function usePutProviderOnLeave() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => providerApi.putOnLeave(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useReturnProviderFromLeave() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => providerApi.returnFromLeave(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useSuspendProvider() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => providerApi.suspend(id),
    onSuccess: (_, id) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useDeleteProvider() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => providerApi.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: KEYS.lists() });
    },
  });
}

export function useAddSchedule() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: import('../types/provider.types').ScheduleRequest;
    }) => providerApi.addSchedule(id, data),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(id) });
    },
  });
}

export function useUpdateSchedule() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      providerId,
      scheduleId,
      data,
    }: {
      providerId: string;
      scheduleId: string;
      data: import('../types/provider.types').ScheduleRequest;
    }) => providerApi.updateSchedule(providerId, scheduleId, data),
    onSuccess: (_, { providerId }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(providerId) });
    },
  });
}

export function useRemoveSchedule() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ providerId, scheduleId }: { providerId: string; scheduleId: string }) =>
      providerApi.removeSchedule(providerId, scheduleId),
    onSuccess: (_, { providerId }) => {
      qc.invalidateQueries({ queryKey: KEYS.detail(providerId) });
    },
  });
}
