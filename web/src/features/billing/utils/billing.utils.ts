

import { format, parseISO, differenceInDays, isPast, isToday } from 'date-fns';
import type { InvoiceStatus, ClaimStatus, PaymentStatus, PaymentMethod } from '@/types';
import type { InvoiceResponse, InvoiceItemResponse, ClaimResponse } from '../types/billing.types';
import {
  INVOICE_STATUS_LABELS,
  CLAIM_STATUS_LABELS,
  PAYMENT_METHOD_LABELS,
  PAYMENT_STATUS_LABELS,
} from '../constants';

export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
}

export function formatCompactCurrency(amount: number): string {
  if (amount >= 1_000_000) {
    return `$${(amount / 1_000_000).toFixed(1)}M`;
  }
  if (amount >= 1_000) {
    return `$${(amount / 1_000).toFixed(1)}K`;
  }
  return formatCurrency(amount);
}

export function parseCurrency(value: string): number {
  const cleaned = value.replace(/[^0-9.-]/g, '');
  return parseFloat(cleaned) || 0;
}

export function getInvoiceStatusLabel(status: InvoiceStatus): string {
  return INVOICE_STATUS_LABELS[status];
}

export function getClaimStatusLabel(status: ClaimStatus): string {
  return CLAIM_STATUS_LABELS[status];
}

export function getPaymentMethodLabel(method: PaymentMethod): string {
  return PAYMENT_METHOD_LABELS[method];
}

export function getPaymentStatusLabel(status: PaymentStatus): string {
  return PAYMENT_STATUS_LABELS[status];
}

export function calculateSubtotal(items: InvoiceItemResponse[]): number {
  return items.reduce((sum, item) => sum + item.totalPrice, 0);
}

export function calculateTax(subtotal: number, taxRate: number): number {
  return Math.round(subtotal * (taxRate / 100) * 100) / 100;
}

export function calculateTotal(
  subtotal: number,
  taxAmount: number,
  discountAmount: number
): number {
  return Math.max(0, subtotal + taxAmount - discountAmount);
}

export function calculateDiscountAmount(subtotal: number, percentage: number): number {
  return Math.round(subtotal * (percentage / 100) * 100) / 100;
}

export function calculatePaymentProgress(paidAmount: number, totalAmount: number): number {
  if (totalAmount === 0) return 100;
  return Math.min(100, Math.round((paidAmount / totalAmount) * 100));
}

export function isOverdue(dueDate: string, status: InvoiceStatus): boolean {
  if (['PAID', 'CANCELLED', 'REFUNDED', 'WRITE_OFF'].includes(status)) {
    return false;
  }
  return isPast(parseISO(dueDate)) && !isToday(parseISO(dueDate));
}

export function getDaysUntilDue(dueDate: string): number {
  return differenceInDays(parseISO(dueDate), new Date());
}

export function getDaysOverdue(dueDate: string): number {
  const days = getDaysUntilDue(dueDate);
  return days < 0 ? Math.abs(days) : 0;
}

export function getDueDateStatus(dueDate: string, status: InvoiceStatus): string {
  if (['PAID', 'CANCELLED', 'REFUNDED'].includes(status)) {
    return '';
  }
  const days = getDaysUntilDue(dueDate);
  if (days < 0) {
    return `${Math.abs(days)} day${Math.abs(days) === 1 ? '' : 's'} overdue`;
  }
  if (days === 0) {
    return 'Due today';
  }
  if (days === 1) {
    return 'Due tomorrow';
  }
  if (days <= 7) {
    return `Due in ${days} days`;
  }
  return format(parseISO(dueDate), 'MMM d, yyyy');
}

export function canEditInvoice(status: InvoiceStatus): boolean {
  return status === 'DRAFT';
}

export function canAcceptPayment(status: InvoiceStatus): boolean {
  return ['PENDING', 'PARTIALLY_PAID', 'OVERDUE'].includes(status);
}

export function canCancelInvoice(status: InvoiceStatus): boolean {
  return ['DRAFT', 'PENDING'].includes(status);
}

export function canRefundInvoice(status: InvoiceStatus): boolean {
  return ['PAID', 'PARTIALLY_PAID'].includes(status);
}

export function canEditClaim(status: ClaimStatus): boolean {
  return status === 'SUBMITTED';
}

export function canAppealClaim(status: ClaimStatus): boolean {
  return ['DENIED', 'PARTIALLY_APPROVED'].includes(status);
}

export function isClaimClosed(status: ClaimStatus): boolean {
  return ['PAID', 'CLOSED'].includes(status);
}

export function calculateClaimPayoutPercentage(claim: ClaimResponse): number {
  if (!claim.paidAmount || claim.billedAmount === 0) return 0;
  return Math.round((claim.paidAmount / claim.billedAmount) * 100);
}

export function formatInvoiceNumber(invoiceNumber: string): string {
  return `INV-${invoiceNumber}`;
}

export function formatClaimNumber(claimNumber: string): string {
  return `CLM-${claimNumber}`;
}

export function formatBillingDate(date: string, formatStr = 'MMM d, yyyy'): string {
  return format(parseISO(date), formatStr);
}

export function formatProcedureCode(code: string | null): string {
  if (!code) return 'N/A';
  return code.toUpperCase();
}

export function filterByStatus<T extends { status: string }>(
  items: T[],
  statuses: string[]
): T[] {
  if (statuses.length === 0) return items;
  return items.filter((item) => statuses.includes(item.status));
}

export function filterOverdueInvoices(invoices: InvoiceResponse[]): InvoiceResponse[] {
  return invoices.filter((inv) => isOverdue(inv.dueDate, inv.status));
}

export function sortByDate<T extends { invoiceDate?: string; submittedDate?: string }>(
  items: T[],
  direction: 'asc' | 'desc' = 'desc'
): T[] {
  return [...items].sort((a, b) => {
    const dateA = a.invoiceDate ?? a.submittedDate ?? '';
    const dateB = b.invoiceDate ?? b.submittedDate ?? '';
    const compare = dateA.localeCompare(dateB);
    return direction === 'asc' ? compare : -compare;
  });
}

export function calculateTotalOutstanding(invoices: InvoiceResponse[]): number {
  return invoices.reduce((sum, inv) => sum + inv.balanceDue, 0);
}

export function calculateTotalCollected(invoices: InvoiceResponse[]): number {
  return invoices.reduce((sum, inv) => sum + inv.paidAmount, 0);
}

export function getItemCount(invoice: InvoiceResponse): number {
  return invoice.items.length;
}
