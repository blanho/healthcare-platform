import type { InvoiceStatus, ClaimStatus, PaymentStatus, PaymentMethod } from '@/types';

export const invoiceStatusConfig: Record<
  InvoiceStatus,
  { label: string; color: 'default' | 'success' | 'warning' | 'error' | 'info' | 'primary' }
> = {
  DRAFT: { label: 'Draft', color: 'default' },
  PENDING: { label: 'Pending', color: 'warning' },
  PARTIALLY_PAID: { label: 'Partial', color: 'primary' },
  PAID: { label: 'Paid', color: 'success' },
  OVERDUE: { label: 'Overdue', color: 'error' },
  CANCELLED: { label: 'Cancelled', color: 'default' },
  REFUNDED: { label: 'Refunded', color: 'info' },
  WRITE_OFF: { label: 'Write Off', color: 'default' },
};

export const claimStatusConfig: Record<
  ClaimStatus,
  { label: string; color: 'default' | 'success' | 'warning' | 'error' | 'info' | 'primary' }
> = {
  SUBMITTED: { label: 'Submitted', color: 'info' },
  IN_REVIEW: { label: 'In Review', color: 'primary' },
  APPROVED: { label: 'Approved', color: 'success' },
  PARTIALLY_APPROVED: { label: 'Partial', color: 'primary' },
  DENIED: { label: 'Denied', color: 'error' },
  APPEALED: { label: 'Appealed', color: 'warning' },
  PAID: { label: 'Paid', color: 'success' },
  CLOSED: { label: 'Closed', color: 'default' },
  INFORMATION_REQUESTED: { label: 'Info Requested', color: 'warning' },
  RESUBMITTED: { label: 'Resubmitted', color: 'info' },
};

export const paymentStatusConfig: Record<
  PaymentStatus,
  { label: string; color: 'default' | 'success' | 'warning' | 'error' | 'info' }
> = {
  PENDING: { label: 'Pending', color: 'warning' },
  PROCESSING: { label: 'Processing', color: 'info' },
  COMPLETED: { label: 'Completed', color: 'success' },
  FAILED: { label: 'Failed', color: 'error' },
  REFUNDED: { label: 'Refunded', color: 'default' },
  PARTIALLY_REFUNDED: { label: 'Partial Refund', color: 'info' },
  CANCELLED: { label: 'Cancelled', color: 'default' },
  AUTHORIZED: { label: 'Authorized', color: 'info' },
  DECLINED: { label: 'Declined', color: 'error' },
  VOIDED: { label: 'Voided', color: 'default' },
};

export const paymentMethodConfig: Record<
  PaymentMethod,
  { label: string; color: string; abbreviation: string }
> = {
  CREDIT_CARD: { label: 'Credit Card', color: '#0891B2', abbreviation: 'CC' },
  DEBIT_CARD: { label: 'Debit Card', color: '#7C3AED', abbreviation: 'DC' },
  CASH: { label: 'Cash', color: '#10B981', abbreviation: 'CA' },
  CHECK: { label: 'Check', color: '#6366F1', abbreviation: 'CK' },
  BANK_TRANSFER: { label: 'Bank Transfer', color: '#059669', abbreviation: 'BT' },
  INSURANCE: { label: 'Insurance', color: '#DC2626', abbreviation: 'IN' },
  PAYMENT_PLAN: { label: 'Payment Plan', color: '#D97706', abbreviation: 'PP' },
  ONLINE_PAYMENT: { label: 'Online Payment', color: '#EC4899', abbreviation: 'OP' },
  MOBILE_PAYMENT: { label: 'Mobile Payment', color: '#2563EB', abbreviation: 'MP' },
};
