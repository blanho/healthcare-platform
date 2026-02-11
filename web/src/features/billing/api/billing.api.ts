import { apiClient } from '@/lib';
import type { PageResponse, PageParams, InvoiceStatus, ClaimStatus } from '@/types';
import type {
  CreateInvoiceRequest,
  InvoiceResponse,
  InvoiceSummaryResponse,
  SubmitClaimRequest,
  ProcessClaimRequest,
  ClaimResponse,
  RecordPaymentRequest,
  PaymentResponse,
  BillingStatisticsResponse,
} from '../types/billing.types';

const INV = '/api/v1/invoices';

export interface InvoiceParams extends PageParams {
  status?: InvoiceStatus;
  patientId?: string;
}

export const invoiceApi = {
  list: (params?: InvoiceParams) =>
    apiClient.get<PageResponse<InvoiceSummaryResponse>>(INV, { params }).then((r) => r.data),
  create: (data: CreateInvoiceRequest) =>
    apiClient.post<InvoiceResponse>(INV, data).then((r) => r.data),
  getById: (id: string) => apiClient.get<InvoiceResponse>(`${INV}/${id}`).then((r) => r.data),
  getByInvoiceNumber: (invoiceNumber: string) =>
    apiClient.get<InvoiceResponse>(`${INV}/number/${invoiceNumber}`).then((r) => r.data),
  byPatient: (patientId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<InvoiceSummaryResponse>>(`${INV}/patient/${patientId}`, { params })
      .then((r) => r.data),
  byStatus: (status: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<InvoiceSummaryResponse>>(`${INV}/status/${status}`, { params })
      .then((r) => r.data),
  overdue: () => apiClient.get<InvoiceSummaryResponse[]>(`${INV}/overdue`).then((r) => r.data),
  addItem: (
    id: string,
    data: { description: string; procedureCode?: string; quantity: number; unitPrice: number },
  ) => apiClient.post<InvoiceResponse>(`${INV}/${id}/items`, data).then((r) => r.data),
  removeItem: (invoiceId: string, itemId: string) =>
    apiClient.delete<InvoiceResponse>(`${INV}/${invoiceId}/items/${itemId}`).then((r) => r.data),
  applyDiscount: (id: string, discount: number) =>
    apiClient
      .post<InvoiceResponse>(`${INV}/${id}/discount`, null, { params: { discount } })
      .then((r) => r.data),
  applyTax: (id: string, tax: number) =>
    apiClient
      .post<InvoiceResponse>(`${INV}/${id}/tax`, null, { params: { tax } })
      .then((r) => r.data),
  finalize: (id: string) =>
    apiClient.post<InvoiceResponse>(`${INV}/${id}/finalize`).then((r) => r.data),
  send: (id: string) => apiClient.post<InvoiceResponse>(`${INV}/${id}/send`).then((r) => r.data),
  cancel: (id: string) =>
    apiClient.post<InvoiceResponse>(`${INV}/${id}/cancel`).then((r) => r.data),
  void: (id: string) => apiClient.post<InvoiceResponse>(`${INV}/${id}/void`).then((r) => r.data),
  patientBalance: (patientId: string) =>
    apiClient.get<number>(`${INV}/patient/${patientId}/balance`).then((r) => r.data),
  getPaymentHistory: (id: string) =>
    apiClient.get<PaymentResponse[]>(`${INV}/${id}/payments`).then((r) => r.data),
};

const CLM = '/api/v1/claims';

export interface ClaimParams extends PageParams {
  status?: ClaimStatus;
  patientId?: string;
}

export const claimApi = {
  list: (params?: ClaimParams) =>
    apiClient.get<PageResponse<ClaimResponse>>(CLM, { params }).then((r) => r.data),
  submit: (data: SubmitClaimRequest) =>
    apiClient.post<ClaimResponse>(CLM, data).then((r) => r.data),
  getById: (id: string) => apiClient.get<ClaimResponse>(`${CLM}/${id}`).then((r) => r.data),
  getByClaimNumber: (claimNumber: string) =>
    apiClient.get<ClaimResponse>(`${CLM}/number/${claimNumber}`).then((r) => r.data),
  byInvoice: (invoiceId: string) =>
    apiClient.get<ClaimResponse[]>(`${CLM}/invoice/${invoiceId}`).then((r) => r.data),
  byPatient: (patientId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<ClaimResponse>>(`${CLM}/patient/${patientId}`, { params })
      .then((r) => r.data),
  byProvider: (providerId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<ClaimResponse>>(`${CLM}/provider/${providerId}`, { params })
      .then((r) => r.data),
  pending: (params?: PageParams) =>
    apiClient.get<PageResponse<ClaimResponse>>(`${CLM}/pending`, { params }).then((r) => r.data),
  byStatus: (status: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<ClaimResponse>>(`${CLM}/status/${status}`, { params })
      .then((r) => r.data),
  process: (id: string, data: ProcessClaimRequest) =>
    apiClient.post<ClaimResponse>(`${CLM}/${id}/process`, data).then((r) => r.data),
  approve: (id: string, approvedAmount?: number) =>
    apiClient
      .post<ClaimResponse>(`${CLM}/${id}/approve`, null, { params: { approvedAmount } })
      .then((r) => r.data),
  deny: (id: string, reason?: string) =>
    apiClient
      .post<ClaimResponse>(`${CLM}/${id}/deny`, null, { params: { reason } })
      .then((r) => r.data),
  markPaid: (id: string) => apiClient.post<ClaimResponse>(`${CLM}/${id}/paid`).then((r) => r.data),
  close: (id: string) => apiClient.post<ClaimResponse>(`${CLM}/${id}/close`).then((r) => r.data),
  appeal: (id: string, notes?: string) =>
    apiClient
      .post<ClaimResponse>(`${CLM}/${id}/appeal`, null, { params: { appealNotes: notes } })
      .then((r) => r.data),
  getHistory: (id: string) =>
    apiClient
      .get<Array<{ timestamp: string; status: string; notes?: string }>>(`${CLM}/${id}/history`)
      .then((r) => r.data),
};

const PAY = '/api/v1/payments';

export const paymentApi = {
  record: (data: RecordPaymentRequest) =>
    apiClient.post<PaymentResponse>(PAY, data).then((r) => r.data),
  getById: (id: string) => apiClient.get<PaymentResponse>(`${PAY}/${id}`).then((r) => r.data),
  getByReference: (reference: string) =>
    apiClient.get<PaymentResponse>(`${PAY}/reference/${reference}`).then((r) => r.data),
  byInvoice: (invoiceId: string) =>
    apiClient.get<PaymentResponse[]>(`${PAY}/invoice/${invoiceId}`).then((r) => r.data),
  byPatient: (patientId: string, params?: PageParams) =>
    apiClient
      .get<PageResponse<PaymentResponse>>(`${PAY}/patient/${patientId}`, { params })
      .then((r) => r.data),
  byDateRange: (startDate: string, endDate: string, params?: PageParams) =>
    apiClient
      .get<
        PageResponse<PaymentResponse>
      >(`${PAY}/date-range`, { params: { startDate, endDate, ...params } })
      .then((r) => r.data),
  refund: (id: string, amount?: number) =>
    apiClient
      .post<PaymentResponse>(`${PAY}/${id}/refund`, null, { params: { amount } })
      .then((r) => r.data),
  revenue: (startDate: string, endDate: string) =>
    apiClient.get<number>(`${PAY}/revenue`, { params: { startDate, endDate } }).then((r) => r.data),
  getReceipt: (id: string) =>
    apiClient.get<{ receiptUrl: string }>(`${PAY}/${id}/receipt`).then((r) => r.data),
};

const STATS = '/api/v1/billing/statistics';

export const billingStatsApi = {
  get: () => apiClient.get<BillingStatisticsResponse>(STATS).then((r) => r.data),
};
