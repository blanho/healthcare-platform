import type { InvoiceStatus, ClaimStatus, PaymentMethod, PaymentStatus } from '@/types';

export interface CreateInvoiceRequest {
  patientId: string;
  appointmentId?: string;
  items: InvoiceItemRequest[];
  taxRate?: number;
  discountAmount?: number;
  discountPercentage?: number;
  dueDate: string;
  notes?: string;
  insuranceInfo?: InsuranceInfoRequest;
}

export interface InvoiceItemRequest {
  description: string;
  procedureCode?: string;
  quantity: number;
  unitPrice: number;
}

export interface InsuranceInfoRequest {
  provider: string;
  policyNumber: string;
  groupNumber?: string;
  subscriberName?: string;
  subscriberId?: string;
}

export interface InvoiceResponse {
  id: string;
  invoiceNumber: string;
  patientId: string;
  appointmentId: string | null;
  subtotal: number;
  taxAmount: number;
  discountAmount: number;
  totalAmount: number;
  paidAmount: number;
  balanceDue: number;
  invoiceDate: string;
  dueDate: string;
  paidDate: string | null;
  status: InvoiceStatus;
  insuranceClaimNumber: string | null;
  insuranceAmount: number | null;
  notes: string | null;
  items: InvoiceItemResponse[];
  createdAt: string;
  updatedAt: string;
  createdBy: string;
}

export interface InvoiceItemResponse {
  id: string;
  description: string;
  procedureCode: string | null;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface InvoiceSummaryResponse {
  id: string;
  invoiceNumber: string;
  patientId: string;
  totalAmount: number;
  balanceDue: number;
  invoiceDate: string;
  dueDate: string;
  status: InvoiceStatus;
  itemCount: number;
  hasInsuranceClaim: boolean;
}

export interface SubmitClaimRequest {
  invoiceId: string;
  insuranceProvider: string;
  policyNumber: string;
  groupNumber?: string;
  subscriberName?: string;
  subscriberId?: string;
  billedAmount: number;
  serviceDate: string;
}

export interface ProcessClaimRequest {
  action: string;
  allowedAmount?: number;
  paidAmount?: number;
  patientResponsibility?: number;
  copayAmount?: number;
  deductibleAmount?: number;
  coinsuranceAmount?: number;
  denialCode?: string;
  denialReason?: string;
  notes?: string;
  eobReference?: string;
}

export interface ClaimResponse {
  id: string;
  claimNumber: string;
  invoiceId: string;
  patientId: string;
  insuranceProvider: string;
  policyNumber: string;
  groupNumber: string | null;
  subscriberName: string | null;
  subscriberId: string | null;
  billedAmount: number;
  allowedAmount: number | null;
  paidAmount: number | null;
  patientResponsibility: number | null;
  copayAmount: number | null;
  deductibleAmount: number | null;
  coinsuranceAmount: number | null;
  status: ClaimStatus;
  submittedAt: string;
  processedAt: string | null;
  serviceDate: string;
  denialReason: string | null;
  denialCode: string | null;
  adjudicationNotes: string | null;
  eobReference: string | null;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
}

export interface RecordPaymentRequest {
  invoiceId: string;
  amount: number;
  paymentMethod: PaymentMethod;
  cardLastFour?: string;
  cardBrand?: string;
  notes?: string;
}

export interface PaymentResponse {
  id: string;
  referenceNumber: string;
  invoiceId: string;
  patientId: string;
  amount: number;
  paymentMethod: PaymentMethod;
  status: PaymentStatus;
  transactionId: string | null;
  authorizationCode: string | null;
  cardLastFour: string | null;
  cardBrand: string | null;
  failureReason: string | null;
  notes: string | null;
  paymentDate: string;
  processedAt: string | null;
  refundedAt: string | null;
  refundAmount: number | null;
  createdAt: string;
  createdBy: string;
}

export interface BillingStatisticsResponse {
  totalRevenue: number;
  outstandingBalance: number;
  totalInvoices: number;
  paidInvoices: number;
  overdueInvoices: number;
  pendingClaims: number;
  deniedClaims: number;
  insurancePayments: number;
  patientPayments: number;
  paymentMethodBreakdown: Record<string, { count: number; totalAmount: number }>;
  insuranceProviderBreakdown: Record<
    string,
    { claimCount: number; billedAmount: number; paidAmount: number; denialRate: number }
  >;
}
