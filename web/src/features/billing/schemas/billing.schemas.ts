

import { z } from 'zod';
import { BILLING_VALIDATION } from '../constants';

export const invoiceStatusSchema = z.enum([
  'DRAFT',
  'PENDING',
  'PARTIALLY_PAID',
  'PAID',
  'OVERDUE',
  'CANCELLED',
  'REFUNDED',
  'WRITE_OFF',
]);

export const claimStatusSchema = z.enum([
  'SUBMITTED',
  'IN_REVIEW',
  'APPROVED',
  'PARTIALLY_APPROVED',
  'DENIED',
  'APPEALED',
  'PAID',
  'CLOSED',
  'INFORMATION_REQUESTED',
  'RESUBMITTED',
]);

export const paymentMethodSchema = z.enum([
  'CREDIT_CARD',
  'DEBIT_CARD',
  'CASH',
  'CHECK',
  'BANK_TRANSFER',
  'INSURANCE',
  'PAYMENT_PLAN',
  'ONLINE_PAYMENT',
  'MOBILE_PAYMENT',
]);

export const paymentStatusSchema = z.enum([
  'PENDING',
  'COMPLETED',
  'FAILED',
  'REFUNDED',
  'PARTIALLY_REFUNDED',
  'CANCELLED',
  'PROCESSING',
  'AUTHORIZED',
  'DECLINED',
  'VOIDED',
]);

export const invoiceItemSchema = z.object({
  description: z
    .string()
    .min(BILLING_VALIDATION.DESCRIPTION.MIN_LENGTH, 'Description is required')
    .max(BILLING_VALIDATION.DESCRIPTION.MAX_LENGTH),
  procedureCode: z
    .string()
    .max(BILLING_VALIDATION.PROCEDURE_CODE.MAX_LENGTH)
    .optional()
    .or(z.literal('')),
  quantity: z
    .number()
    .min(BILLING_VALIDATION.QUANTITY.MIN, 'Quantity must be at least 1')
    .max(BILLING_VALIDATION.QUANTITY.MAX),
  unitPrice: z
    .number()
    .min(BILLING_VALIDATION.UNIT_PRICE.MIN, 'Price cannot be negative')
    .max(BILLING_VALIDATION.UNIT_PRICE.MAX),
});

export type InvoiceItemFormValues = z.infer<typeof invoiceItemSchema>;

export const insuranceInfoSchema = z
  .object({
    provider: z.string().min(1, 'Provider is required').max(100),
    policyNumber: z
      .string()
      .min(BILLING_VALIDATION.POLICY_NUMBER.MIN_LENGTH, 'Policy number is required')
      .max(BILLING_VALIDATION.POLICY_NUMBER.MAX_LENGTH),
    groupNumber: z.string().max(50).optional().or(z.literal('')),
    subscriberName: z.string().max(100).optional().or(z.literal('')),
    subscriberId: z.string().max(50).optional().or(z.literal('')),
  })
  .optional();

export const createInvoiceSchema = z.object({
  patientId: z.string().uuid('Please select a valid patient'),
  appointmentId: z.string().uuid().optional().or(z.literal('')),
  items: z.array(invoiceItemSchema).min(1, 'At least one item is required'),
  taxRate: z
    .number()
    .min(BILLING_VALIDATION.TAX_RATE.MIN)
    .max(BILLING_VALIDATION.TAX_RATE.MAX)
    .optional()
    .default(0),
  discountAmount: z.number().min(0).optional(),
  discountPercentage: z
    .number()
    .min(BILLING_VALIDATION.DISCOUNT.MIN)
    .max(BILLING_VALIDATION.DISCOUNT.MAX)
    .optional(),
  dueDate: z.string().min(1, 'Due date is required'),
  notes: z.string().max(BILLING_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
  insuranceInfo: insuranceInfoSchema,
});

export type CreateInvoiceFormValues = z.infer<typeof createInvoiceSchema>;

export const submitClaimSchema = z.object({
  invoiceId: z.string().uuid('Please select a valid invoice'),
  insuranceProvider: z.string().min(1, 'Insurance provider is required').max(100),
  policyNumber: z
    .string()
    .min(BILLING_VALIDATION.POLICY_NUMBER.MIN_LENGTH, 'Policy number is required')
    .max(BILLING_VALIDATION.POLICY_NUMBER.MAX_LENGTH),
  groupNumber: z.string().max(50).optional().or(z.literal('')),
  subscriberName: z.string().max(100).optional().or(z.literal('')),
  subscriberId: z.string().max(50).optional().or(z.literal('')),
  billedAmount: z.number().min(0.01, 'Amount must be greater than 0'),
  serviceDate: z.string().min(1, 'Service date is required'),
});

export type SubmitClaimFormValues = z.infer<typeof submitClaimSchema>;

export const processClaimSchema = z.object({
  action: z.string().min(1, 'Action is required'),
  allowedAmount: z.number().min(0).optional(),
  paidAmount: z.number().min(0).optional(),
  patientResponsibility: z.number().min(0).optional(),
  copayAmount: z.number().min(0).optional(),
  deductibleAmount: z.number().min(0).optional(),
  coinsuranceAmount: z.number().min(0).optional(),
  denialCode: z.string().max(20).optional().or(z.literal('')),
  denialReason: z.string().max(500).optional().or(z.literal('')),
  notes: z.string().max(BILLING_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
  eobReference: z.string().max(100).optional().or(z.literal('')),
});

export type ProcessClaimFormValues = z.infer<typeof processClaimSchema>;

export const recordPaymentSchema = z.object({
  invoiceId: z.string().uuid('Please select a valid invoice'),
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  paymentMethod: paymentMethodSchema,
  referenceNumber: z.string().max(100).optional().or(z.literal('')),
  notes: z.string().max(BILLING_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
});

export type RecordPaymentFormValues = z.infer<typeof recordPaymentSchema>;

export const invoiceSearchSchema = z.object({
  patientId: z.string().uuid().optional().or(z.literal('')),
  status: invoiceStatusSchema.optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  minAmount: z.number().min(0).optional(),
  maxAmount: z.number().min(0).optional(),
});

export type InvoiceSearchFormValues = z.infer<typeof invoiceSearchSchema>;

export const invoiceItemFormSchema = z.object({
  description: z.string().min(1, 'Description is required'),
  procedureCode: z.string().optional().or(z.literal('')),
  quantity: z.number().min(1, 'Quantity must be at least 1'),
  unitPrice: z.number().min(0, 'Unit price must be positive'),
});

export type InvoiceItemFormSchemaValues = z.infer<typeof invoiceItemFormSchema>;

export const invoiceFormSchema = z.object({
  patientId: z.string().min(1, 'Patient is required'),
  dueDate: z.date({ message: 'Due date is required' }),
  taxRate: z.number().min(0).max(100).optional().default(0),
  discountAmount: z.number().min(0).optional().default(0),
  notes: z.string().max(BILLING_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
  items: z.array(invoiceItemFormSchema).min(1, 'At least one item is required'),
});

export type InvoiceFormValues = z.infer<typeof invoiceFormSchema>;

export const paymentFormSchema = z.object({
  amount: z.number().min(0.01, 'Amount must be greater than 0'),
  paymentMethod: paymentMethodSchema,
  referenceNumber: z.string().max(100).optional().or(z.literal('')),
  notes: z.string().max(BILLING_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
});

export type PaymentFormValues = z.infer<typeof paymentFormSchema>;

export const paymentDialogSchema = z.object({
  amount: z.number().positive('Amount must be positive'),
  paymentMethod: z.string().min(1, 'Payment method is required'),
  cardLastFour: z.string().optional(),
  notes: z.string().optional(),
});

export type PaymentDialogFormValues = z.infer<typeof paymentDialogSchema>;
