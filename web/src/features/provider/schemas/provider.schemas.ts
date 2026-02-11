

import { z } from 'zod';
import { PROVIDER_VALIDATION, SCHEDULE_DEFAULTS } from '../constants';

export const providerTypeSchema = z.enum([
  'PHYSICIAN',
  'SURGEON',
  'SPECIALIST',
  'DENTIST',
  'OPTOMETRIST',
  'PSYCHIATRIST',
  'DERMATOLOGIST',
  'CARDIOLOGIST',
  'NEUROLOGIST',
  'PEDIATRICIAN',
  'RADIOLOGIST',
  'GENERAL_PRACTITIONER',
]);

export const providerStatusSchema = z.enum([
  'ACTIVE',
  'INACTIVE',
  'ON_LEAVE',
  'SUSPENDED',
  'PENDING_VERIFICATION',
  'RETIRED',
]);

export const dayOfWeekSchema = z.enum([
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY',
]);

const nameSchema = z
  .string()
  .min(PROVIDER_VALIDATION.NAME.MIN_LENGTH, 'Name is required')
  .max(PROVIDER_VALIDATION.NAME.MAX_LENGTH, `Name cannot exceed ${PROVIDER_VALIDATION.NAME.MAX_LENGTH} characters`);

const optionalNameSchema = z
  .string()
  .max(PROVIDER_VALIDATION.NAME.MAX_LENGTH)
  .optional()
  .or(z.literal(''));

const emailSchema = z
  .string()
  .email('Please enter a valid email address')
  .max(PROVIDER_VALIDATION.EMAIL.MAX_LENGTH);

const phoneSchema = z
  .string()
  .min(PROVIDER_VALIDATION.PHONE.MIN_LENGTH, 'Phone number must be at least 10 characters')
  .max(PROVIDER_VALIDATION.PHONE.MAX_LENGTH)
  .regex(PROVIDER_VALIDATION.PHONE.PATTERN, 'Please enter a valid phone number')
  .optional()
  .or(z.literal(''));

const npiSchema = z
  .string()
  .regex(PROVIDER_VALIDATION.NPI.PATTERN, 'NPI must be exactly 10 digits')
  .optional()
  .or(z.literal(''));

export const licenseSchema = z.object({
  licenseNumber: z
    .string()
    .min(PROVIDER_VALIDATION.LICENSE_NUMBER.MIN_LENGTH, 'License number is required')
    .max(PROVIDER_VALIDATION.LICENSE_NUMBER.MAX_LENGTH),
  licenseState: z.string().min(2, 'State is required').max(2),
  expiryDate: z.string().min(1, 'Expiry date is required'),
});

export const partialLicenseSchema = z.object({
  licenseNumber: z.string().max(PROVIDER_VALIDATION.LICENSE_NUMBER.MAX_LENGTH).optional(),
  licenseState: z.string().max(2).optional(),
  expiryDate: z.string().optional(),
});

export const scheduleSchema = z.object({
  dayOfWeek: dayOfWeekSchema,
  startTime: z.string().regex(/^\d{2}:\d{2}$/, 'Time must be in HH:MM format'),
  endTime: z.string().regex(/^\d{2}:\d{2}$/, 'Time must be in HH:MM format'),
  slotDurationMinutes: z
    .number()
    .min(SCHEDULE_DEFAULTS.MIN_SLOT_DURATION, 'Slot duration must be at least 15 minutes')
    .max(SCHEDULE_DEFAULTS.MAX_SLOT_DURATION, 'Slot duration cannot exceed 2 hours'),
});

export type ScheduleFormValues = z.infer<typeof scheduleSchema>;

export const createProviderSchema = z.object({
  firstName: nameSchema,
  middleName: optionalNameSchema,
  lastName: nameSchema,
  email: emailSchema,
  phoneNumber: phoneSchema,
  providerType: providerTypeSchema,
  specialization: z
    .string()
    .max(PROVIDER_VALIDATION.SPECIALIZATION.MAX_LENGTH)
    .optional()
    .or(z.literal('')),
  license: licenseSchema,
  npiNumber: npiSchema,
  qualification: z
    .string()
    .max(PROVIDER_VALIDATION.QUALIFICATION.MAX_LENGTH)
    .optional()
    .or(z.literal('')),
  yearsOfExperience: z
    .number()
    .min(PROVIDER_VALIDATION.EXPERIENCE.MIN_YEARS)
    .max(PROVIDER_VALIDATION.EXPERIENCE.MAX_YEARS)
    .optional(),
  consultationFee: z
    .number()
    .min(PROVIDER_VALIDATION.CONSULTATION_FEE.MIN)
    .max(PROVIDER_VALIDATION.CONSULTATION_FEE.MAX)
    .optional(),
  acceptingPatients: z.boolean().optional().default(true),
});

export type CreateProviderFormValues = z.infer<typeof createProviderSchema>;

export const updateProviderSchema = createProviderSchema.partial().extend({
  license: partialLicenseSchema.optional(),
});

export type UpdateProviderFormValues = z.infer<typeof updateProviderSchema>;

export const providerSearchSchema = z.object({
  name: z.string().optional(),
  email: z.string().optional(),
  providerType: providerTypeSchema.optional(),
  specialization: z.string().optional(),
  status: providerStatusSchema.optional(),
  acceptingPatients: z.boolean().optional(),
});

export type ProviderSearchFormValues = z.infer<typeof providerSearchSchema>;
