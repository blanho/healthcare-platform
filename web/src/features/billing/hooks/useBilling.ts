import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { PageParams } from '@/types';
import { invoiceApi, claimApi, paymentApi, billingStatsApi } from '../api/billing.api';
import type { InvoiceParams, ClaimParams } from '../api/billing.api';
import type {
  CreateInvoiceRequest,
  RecordPaymentRequest,
  SubmitClaimRequest,
} from '../types/billing.types';

const INV_KEYS = {
  all: ['invoices'] as const,
  lists: () => [...INV_KEYS.all, 'list'] as const,
  list: (p: unknown) => [...INV_KEYS.lists(), p] as const,
  detail: (id: string) => [...INV_KEYS.all, 'detail', id] as const,
};

export function useInvoices(params?: InvoiceParams) {
  return useQuery({
    queryKey: INV_KEYS.list(params),
    queryFn: () => invoiceApi.list(params),
  });
}

export function useInvoice(id: string) {
  return useQuery({
    queryKey: INV_KEYS.detail(id),
    queryFn: () => invoiceApi.getById(id),
    enabled: !!id,
  });
}

export function usePatientInvoices(patientId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...INV_KEYS.lists(), 'patient', patientId, params],
    queryFn: () => invoiceApi.byPatient(patientId, params),
    enabled: !!patientId,
  });
}

export function useOverdueInvoices() {
  return useQuery({
    queryKey: [...INV_KEYS.lists(), 'overdue'],
    queryFn: () => invoiceApi.overdue(),
  });
}

export function useCreateInvoice() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateInvoiceRequest) => invoiceApi.create(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: INV_KEYS.lists() }),
  });
}

export function useInvoiceMutations() {
  const qc = useQueryClient();

  const sendInvoice = useMutation({
    mutationFn: (id: string) => invoiceApi.send(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: INV_KEYS.all }),
  });

  const cancelInvoice = useMutation({
    mutationFn: (id: string) => invoiceApi.cancel(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: INV_KEYS.all }),
  });

  const voidInvoice = useMutation({
    mutationFn: (id: string) => invoiceApi.void(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: INV_KEYS.all }),
  });

  const finalizeInvoice = useMutation({
    mutationFn: (id: string) => invoiceApi.finalize(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: INV_KEYS.all }),
  });

  const addItem = useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: { description: string; procedureCode?: string; quantity: number; unitPrice: number };
    }) => invoiceApi.addItem(id, data),
    onSuccess: (_, { id }) => qc.invalidateQueries({ queryKey: INV_KEYS.detail(id) }),
  });

  const removeItem = useMutation({
    mutationFn: ({ invoiceId, itemId }: { invoiceId: string; itemId: string }) =>
      invoiceApi.removeItem(invoiceId, itemId),
    onSuccess: (_, { invoiceId }) => qc.invalidateQueries({ queryKey: INV_KEYS.detail(invoiceId) }),
  });

  const applyDiscount = useMutation({
    mutationFn: ({ id, discount }: { id: string; discount: number }) =>
      invoiceApi.applyDiscount(id, discount),
    onSuccess: (_, { id }) => qc.invalidateQueries({ queryKey: INV_KEYS.detail(id) }),
  });

  const applyTax = useMutation({
    mutationFn: ({ id, tax }: { id: string; tax: number }) => invoiceApi.applyTax(id, tax),
    onSuccess: (_, { id }) => qc.invalidateQueries({ queryKey: INV_KEYS.detail(id) }),
  });

  return {
    sendInvoice,
    cancelInvoice,
    voidInvoice,
    finalizeInvoice,
    addItem,
    removeItem,
    applyDiscount,
    applyTax,
  };
}

const CLM_KEYS = {
  all: ['claims'] as const,
  lists: () => [...CLM_KEYS.all, 'list'] as const,
  list: (p: unknown) => [...CLM_KEYS.lists(), p] as const,
  detail: (id: string) => [...CLM_KEYS.all, 'detail', id] as const,
};

export function useClaims(params?: ClaimParams) {
  return useQuery({
    queryKey: CLM_KEYS.list(params),
    queryFn: () => claimApi.list(params),
  });
}

export function useClaim(id: string) {
  return useQuery({
    queryKey: CLM_KEYS.detail(id),
    queryFn: () => claimApi.getById(id),
    enabled: !!id,
  });
}

export function usePendingClaims(params?: PageParams) {
  return useQuery({
    queryKey: [...CLM_KEYS.lists(), 'pending', params],
    queryFn: () => claimApi.pending(params),
  });
}

export function useSubmitClaim() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: SubmitClaimRequest) => claimApi.submit(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: CLM_KEYS.lists() }),
  });
}

export function useClaimsByPatient(patientId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...CLM_KEYS.lists(), 'patient', patientId, params],
    queryFn: () => claimApi.byPatient(patientId, params),
    enabled: !!patientId,
  });
}

export function useClaimsByProvider(providerId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...CLM_KEYS.lists(), 'provider', providerId, params],
    queryFn: () => claimApi.byProvider(providerId, params),
    enabled: !!providerId,
  });
}

export function useClaimsByInvoice(invoiceId: string) {
  return useQuery({
    queryKey: [...CLM_KEYS.lists(), 'invoice', invoiceId],
    queryFn: () => claimApi.byInvoice(invoiceId),
    enabled: !!invoiceId,
  });
}

export function useClaimsByStatus(status: string, params?: PageParams) {
  return useQuery({
    queryKey: [...CLM_KEYS.lists(), 'status', status, params],
    queryFn: () => claimApi.byStatus(status, params),
    enabled: !!status,
  });
}

export function useClaimMutations() {
  const qc = useQueryClient();

  const processClaim = useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: import('../types/billing.types').ProcessClaimRequest;
    }) => claimApi.process(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: CLM_KEYS.all }),
  });

  const approveClaim = useMutation({
    mutationFn: ({ id, approvedAmount }: { id: string; approvedAmount?: number }) =>
      claimApi.approve(id, approvedAmount),
    onSuccess: () => qc.invalidateQueries({ queryKey: CLM_KEYS.all }),
  });

  const denyClaim = useMutation({
    mutationFn: ({ id, reason }: { id: string; reason?: string }) => claimApi.deny(id, reason),
    onSuccess: () => qc.invalidateQueries({ queryKey: CLM_KEYS.all }),
  });

  const markClaimPaid = useMutation({
    mutationFn: (id: string) => claimApi.markPaid(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: CLM_KEYS.all }),
  });

  const closeClaim = useMutation({
    mutationFn: (id: string) => claimApi.close(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: CLM_KEYS.all }),
  });

  const appealClaim = useMutation({
    mutationFn: ({ id, notes }: { id: string; notes?: string }) => claimApi.appeal(id, notes),
    onSuccess: () => qc.invalidateQueries({ queryKey: CLM_KEYS.all }),
  });

  return { processClaim, approveClaim, denyClaim, markClaimPaid, closeClaim, appealClaim };
}

const PAY_KEYS = {
  all: ['payments'] as const,
  lists: () => [...PAY_KEYS.all, 'list'] as const,
  detail: (id: string) => [...PAY_KEYS.all, 'detail', id] as const,
};

export function usePayment(id: string) {
  return useQuery({
    queryKey: PAY_KEYS.detail(id),
    queryFn: () => paymentApi.getById(id),
    enabled: !!id,
  });
}

export function useInvoicePayments(invoiceId: string) {
  return useQuery({
    queryKey: [...PAY_KEYS.lists(), 'invoice', invoiceId],
    queryFn: () => paymentApi.byInvoice(invoiceId),
    enabled: !!invoiceId,
  });
}

export function useRecordPayment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: RecordPaymentRequest) => paymentApi.record(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: PAY_KEYS.lists() });
      qc.invalidateQueries({ queryKey: INV_KEYS.lists() });
    },
  });
}

export function usePatientPayments(patientId: string, params?: PageParams) {
  return useQuery({
    queryKey: [...PAY_KEYS.lists(), 'patient', patientId, params],
    queryFn: () => paymentApi.byPatient(patientId, params),
    enabled: !!patientId,
  });
}

export function usePaymentsByDateRange(startDate: string, endDate: string, params?: PageParams) {
  return useQuery({
    queryKey: [...PAY_KEYS.lists(), 'date-range', startDate, endDate, params],
    queryFn: () => paymentApi.byDateRange(startDate, endDate, params),
    enabled: !!startDate && !!endDate,
  });
}

export function useRevenue(startDate: string, endDate: string) {
  return useQuery({
    queryKey: [...PAY_KEYS.all, 'revenue', startDate, endDate],
    queryFn: () => paymentApi.revenue(startDate, endDate),
    enabled: !!startDate && !!endDate,
  });
}

export function useRefundPayment() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, amount }: { id: string; amount?: number }) => paymentApi.refund(id, amount),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: PAY_KEYS.all });
      qc.invalidateQueries({ queryKey: INV_KEYS.all });
    },
  });
}

const STATS_KEYS = {
  all: ['billing-stats'] as const,
};

export function useBillingStats() {
  return useQuery({
    queryKey: STATS_KEYS.all,
    queryFn: () => billingStatsApi.get(),
    staleTime: 5 * 60 * 1000,
  });
}
