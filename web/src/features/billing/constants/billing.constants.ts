

import type { InvoiceStatus, ClaimStatus, PaymentMethod, PaymentStatus } from '@/types';

export const INVOICE_STATUS_LABELS: Record<InvoiceStatus, string> = {
  DRAFT: 'Draft',
  PENDING: 'Pending',
  PARTIALLY_PAID: 'Partially Paid',
  PAID: 'Paid',
  OVERDUE: 'Overdue',
  CANCELLED: 'Cancelled',
  REFUNDED: 'Refunded',
  WRITE_OFF: 'Write-off',
};

export const INVOICE_STATUS_OPTIONS = Object.entries(INVOICE_STATUS_LABELS).map(
  ([value, label]) => ({
    value: value as InvoiceStatus,
    label,
  })
);

export const INVOICE_STATUS_COLORS: Record<
  InvoiceStatus,
  'success' | 'warning' | 'error' | 'info' | 'default'
> = {
  DRAFT: 'default',
  PENDING: 'info',
  PARTIALLY_PAID: 'warning',
  PAID: 'success',
  OVERDUE: 'error',
  CANCELLED: 'default',
  REFUNDED: 'info',
  WRITE_OFF: 'error',
};

export const CLAIM_STATUS_LABELS: Record<ClaimStatus, string> = {
  SUBMITTED: 'Submitted',
  IN_REVIEW: 'In Review',
  APPROVED: 'Approved',
  PARTIALLY_APPROVED: 'Partially Approved',
  DENIED: 'Denied',
  APPEALED: 'Appealed',
  PAID: 'Paid',
  CLOSED: 'Closed',
  INFORMATION_REQUESTED: 'Info Requested',
  RESUBMITTED: 'Resubmitted',
};

export const CLAIM_STATUS_OPTIONS = Object.entries(CLAIM_STATUS_LABELS).map(
  ([value, label]) => ({
    value: value as ClaimStatus,
    label,
  })
);

export const CLAIM_STATUS_COLORS: Record<
  ClaimStatus,
  'success' | 'warning' | 'error' | 'info' | 'default' | 'primary' | 'secondary'
> = {
  SUBMITTED: 'info',
  IN_REVIEW: 'warning',
  APPROVED: 'success',
  PARTIALLY_APPROVED: 'warning',
  DENIED: 'error',
  APPEALED: 'secondary',
  PAID: 'success',
  CLOSED: 'default',
  INFORMATION_REQUESTED: 'warning',
  RESUBMITTED: 'info',
};

export const PAYMENT_METHOD_LABELS: Record<PaymentMethod, string> = {
  CREDIT_CARD: 'Credit Card',
  DEBIT_CARD: 'Debit Card',
  CASH: 'Cash',
  CHECK: 'Check',
  BANK_TRANSFER: 'Bank Transfer',
  INSURANCE: 'Insurance',
  PAYMENT_PLAN: 'Payment Plan',
  ONLINE_PAYMENT: 'Online Payment',
  MOBILE_PAYMENT: 'Mobile Payment',
};

export const PAYMENT_METHOD_OPTIONS = Object.entries(PAYMENT_METHOD_LABELS).map(
  ([value, label]) => ({
    value: value as PaymentMethod,
    label,
  })
);

export const PAYMENT_STATUS_LABELS: Record<PaymentStatus, string> = {
  PENDING: 'Pending',
  COMPLETED: 'Completed',
  FAILED: 'Failed',
  REFUNDED: 'Refunded',
  PARTIALLY_REFUNDED: 'Partially Refunded',
  CANCELLED: 'Cancelled',
  PROCESSING: 'Processing',
  AUTHORIZED: 'Authorized',
  DECLINED: 'Declined',
  VOIDED: 'Voided',
};

export const PAYMENT_STATUS_OPTIONS = Object.entries(PAYMENT_STATUS_LABELS).map(
  ([value, label]) => ({
    value: value as PaymentStatus,
    label,
  })
);

export const PAYMENT_STATUS_COLORS: Record<
  PaymentStatus,
  'success' | 'warning' | 'error' | 'info' | 'default'
> = {
  PENDING: 'warning',
  COMPLETED: 'success',
  FAILED: 'error',
  REFUNDED: 'info',
  PARTIALLY_REFUNDED: 'info',
  CANCELLED: 'default',
  PROCESSING: 'warning',
  AUTHORIZED: 'info',
  DECLINED: 'error',
  VOIDED: 'default',
};

export const BILLING_VALIDATION = {
  DESCRIPTION: {
    MIN_LENGTH: 1,
    MAX_LENGTH: 500,
  },
  PROCEDURE_CODE: {
    MAX_LENGTH: 20,
  },
  QUANTITY: {
    MIN: 1,
    MAX: 999,
  },
  UNIT_PRICE: {
    MIN: 0,
    MAX: 100000,
  },
  TAX_RATE: {
    MIN: 0,
    MAX: 100,
  },
  DISCOUNT: {
    MIN: 0,
    MAX: 100,
  },
  NOTES: {
    MAX_LENGTH: 2000,
  },
  POLICY_NUMBER: {
    MIN_LENGTH: 5,
    MAX_LENGTH: 50,
  },
} as const;

export const INVOICE_LIST_DEFAULTS = {
  PAGE_SIZE: 10,
  SORT_BY: 'invoiceDate',
  SORT_DIRECTION: 'desc' as const,
} as const;

export const CLAIM_LIST_DEFAULTS = {
  PAGE_SIZE: 10,
  SORT_BY: 'submittedDate',
  SORT_DIRECTION: 'desc' as const,
} as const;

export const PAYMENT_LIST_DEFAULTS = {
  PAGE_SIZE: 10,
  SORT_BY: 'paymentDate',
  SORT_DIRECTION: 'desc' as const,
} as const;

export const DENIAL_CODES = [
  { code: '1', description: 'Deductible Amount' },
  { code: '2', description: 'Coinsurance Amount' },
  { code: '3', description: 'Co-payment Amount' },
  { code: '4', description: 'Procedure Not Covered' },
  { code: '5', description: 'Maximum Benefit Reached' },
  { code: '6', description: 'Prior Authorization Required' },
  { code: '7', description: 'Duplicate Claim' },
  { code: '8', description: 'Invalid Member ID' },
  { code: '9', description: 'Service Not Medically Necessary' },
  { code: '10', description: 'Out of Network Provider' },
] as const;

export const PROCEDURE_CODE_PATTERNS = {
  CPT: /^\d{5}$/,
  HCPCS: /^[A-Z]\d{4}$/,
  ICD10: /^[A-Z]\d{2}(\.\d{1,4})?$/,
} as const;
