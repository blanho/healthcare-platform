

import { z } from 'zod';
import { APPOINTMENT_VALIDATION } from '../constants';

const appointmentTypeSchema = z.enum([
  'CONSULTATION',
  'FOLLOW_UP',
  'CHECKUP',
  'EMERGENCY',
  'SURGERY',
  'LAB_TEST',
  'IMAGING',
  'VACCINATION',
  'PHYSICAL_THERAPY',
  'MENTAL_HEALTH',
  'DENTAL',
  'TELEMEDICINE',
  'OTHER',
]);

const appointmentStatusSchema = z.enum([
  'SCHEDULED',
  'CONFIRMED',
  'CHECKED_IN',
  'IN_PROGRESS',
  'COMPLETED',
  'CANCELLED',
  'NO_SHOW',
  'RESCHEDULED',
]);

export const scheduleAppointmentSchema = z.object({
  patientId: z.string().uuid('Please select a valid patient'),
  providerId: z.string().uuid('Please select a valid provider'),
  scheduledDate: z.string().min(1, 'Date is required'),
  startTime: z.string().regex(/^\d{2}:\d{2}$/, 'Time must be in HH:MM format'),
  durationMinutes: z
    .number()
    .min(APPOINTMENT_VALIDATION.MIN_DURATION_MINUTES, 'Duration must be at least 15 minutes')
    .max(APPOINTMENT_VALIDATION.MAX_DURATION_MINUTES, 'Duration cannot exceed 8 hours')
    .optional()
    .default(30),
  appointmentType: appointmentTypeSchema,
  reasonForVisit: z
    .string()
    .max(APPOINTMENT_VALIDATION.REASON_MAX_LENGTH, 'Reason cannot exceed 500 characters')
    .optional(),
  notes: z
    .string()
    .max(APPOINTMENT_VALIDATION.NOTES_MAX_LENGTH, 'Notes cannot exceed 2000 characters')
    .optional(),
});

export type ScheduleAppointmentFormValues = z.infer<typeof scheduleAppointmentSchema>;

export const rescheduleAppointmentSchema = z.object({
  newDate: z.string().min(1, 'New date is required'),
  newStartTime: z.string().regex(/^\d{2}:\d{2}$/, 'Time must be in HH:MM format'),
  durationMinutes: z
    .number()
    .min(APPOINTMENT_VALIDATION.MIN_DURATION_MINUTES)
    .max(APPOINTMENT_VALIDATION.MAX_DURATION_MINUTES)
    .optional(),
});

export type RescheduleAppointmentFormValues = z.infer<typeof rescheduleAppointmentSchema>;

export const cancelAppointmentSchema = z.object({
  reason: z
    .string()
    .min(1, 'Cancellation reason is required')
    .max(APPOINTMENT_VALIDATION.CANCELLATION_REASON_MAX_LENGTH, 'Reason cannot exceed 500 characters'),
  cancelledByPatient: z.boolean(),
});

export type CancelAppointmentFormValues = z.infer<typeof cancelAppointmentSchema>;

export const appointmentSearchSchema = z.object({
  patientId: z.string().uuid().optional().or(z.literal('')),
  providerId: z.string().uuid().optional().or(z.literal('')),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
  appointmentType: appointmentTypeSchema.optional(),
  status: appointmentStatusSchema.optional(),
});

export type AppointmentSearchFormValues = z.infer<typeof appointmentSearchSchema>;

export const checkInSchema = z.object({
  notes: z
    .string()
    .max(APPOINTMENT_VALIDATION.NOTES_MAX_LENGTH, 'Notes cannot exceed 2000 characters')
    .optional(),
});

export type CheckInFormValues = z.infer<typeof checkInSchema>;

export const completeAppointmentSchema = z.object({
  completionNotes: z
    .string()
    .max(APPOINTMENT_VALIDATION.NOTES_MAX_LENGTH, 'Notes cannot exceed 2000 characters')
    .optional(),
});

export type CompleteAppointmentFormValues = z.infer<typeof completeAppointmentSchema>;

export const appointmentFormSchema = z.object({
  patientId: z.string().min(1, 'Patient is required'),
  providerId: z.string().min(1, 'Provider is required'),
  scheduledDate: z.date({ message: 'Date is required' }),
  startTime: z.date({ message: 'Time is required' }),
  durationMinutes: z.number().min(15, 'Minimum 15 minutes').max(240, 'Maximum 4 hours'),
  appointmentType: appointmentTypeSchema,
  reasonForVisit: z
    .string()
    .max(APPOINTMENT_VALIDATION.REASON_MAX_LENGTH)
    .optional()
    .or(z.literal('')),
  notes: z
    .string()
    .max(APPOINTMENT_VALIDATION.NOTES_MAX_LENGTH)
    .optional()
    .or(z.literal('')),
});

export type AppointmentFormValues = z.infer<typeof appointmentFormSchema>;
