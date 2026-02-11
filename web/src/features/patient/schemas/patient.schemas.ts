

import { z } from 'zod';
import { PATIENT_VALIDATION } from '../constants';

export const nameSchema = z
  .string()
  .min(PATIENT_VALIDATION.NAME.MIN_LENGTH, 'Name is required')
  .max(PATIENT_VALIDATION.NAME.MAX_LENGTH, `Name must be less than ${PATIENT_VALIDATION.NAME.MAX_LENGTH} characters`);

export const optionalNameSchema = z
  .string()
  .max(PATIENT_VALIDATION.NAME.MAX_LENGTH, `Name must be less than ${PATIENT_VALIDATION.NAME.MAX_LENGTH} characters`)
  .optional()
  .or(z.literal(''));

export const emailSchema = z
  .string()
  .min(1, 'Email is required')
  .email('Please enter a valid email address')
  .max(PATIENT_VALIDATION.EMAIL.MAX_LENGTH, `Email must be less than ${PATIENT_VALIDATION.EMAIL.MAX_LENGTH} characters`);

export const phoneSchema = z
  .string()
  .min(PATIENT_VALIDATION.PHONE.MIN_LENGTH, 'Phone number is required')
  .max(PATIENT_VALIDATION.PHONE.MAX_LENGTH, `Phone must be less than ${PATIENT_VALIDATION.PHONE.MAX_LENGTH} characters`)
  .regex(PATIENT_VALIDATION.PHONE.PATTERN, 'Please enter a valid phone number');

export const optionalPhoneSchema = z
  .string()
  .max(PATIENT_VALIDATION.PHONE.MAX_LENGTH, `Phone must be less than ${PATIENT_VALIDATION.PHONE.MAX_LENGTH} characters`)
  .regex(PATIENT_VALIDATION.PHONE.PATTERN, 'Please enter a valid phone number')
  .optional()
  .or(z.literal(''));

export const ssnSchema = z
  .string()
  .regex(PATIENT_VALIDATION.SSN.PATTERN, 'SSN must be in format XXX-XX-XXXX')
  .optional()
  .or(z.literal(''));

export const dateOfBirthSchema = z
  .string()
  .min(1, 'Date of birth is required')
  .refine((val) => {
    const date = new Date(val);
    return !isNaN(date.getTime());
  }, 'Please enter a valid date')
  .refine((val) => {
    const date = new Date(val);
    return date <= new Date();
  }, 'Date of birth cannot be in the future');

export const zipCodeSchema = z
  .string()
  .min(PATIENT_VALIDATION.ZIP_CODE.MIN_LENGTH, 'Zip code must be at least 5 characters')
  .max(PATIENT_VALIDATION.ZIP_CODE.MAX_LENGTH, 'Zip code must be less than 10 characters')
  .regex(PATIENT_VALIDATION.ZIP_CODE.PATTERN, 'Please enter a valid zip code')
  .optional()
  .or(z.literal(''));

export const addressSchema = z.object({
  street: z.string().max(255).optional().or(z.literal('')),
  city: z.string().max(100).optional().or(z.literal('')),
  state: z.string().max(50).optional().or(z.literal('')),
  zipCode: zipCodeSchema,
  country: z.string().max(100).optional().or(z.literal('')),
}).optional();

export const insuranceSchema = z.object({
  providerName: z.string().max(100).optional().or(z.literal('')),
  policyNumber: z.string().max(50).optional().or(z.literal('')),
  groupNumber: z.string().max(50).optional().or(z.literal('')),
  holderName: z.string().max(100).optional().or(z.literal('')),
  holderRelationship: z.string().max(50).optional().or(z.literal('')),
  effectiveDate: z.string().optional().or(z.literal('')),
  expirationDate: z.string().optional().or(z.literal('')),
  isActive: z.boolean().optional(),
}).optional();

export const emergencyContactSchema = z.object({
  name: z.string().max(100).optional().or(z.literal('')),
  relationship: z.string().max(50).optional().or(z.literal('')),
  phoneNumber: optionalPhoneSchema,
  email: z.string().email('Please enter a valid email').optional().or(z.literal('')),
}).optional();

export const createPatientSchema = z.object({
  firstName: nameSchema,
  middleName: optionalNameSchema,
  lastName: nameSchema,
  dateOfBirth: dateOfBirthSchema,
  gender: z.enum(['MALE', 'FEMALE', 'NON_BINARY', 'OTHER', 'PREFER_NOT_TO_SAY'], {
    error: 'Gender is required',
  }),
  bloodType: z.enum([
    'A_POSITIVE', 'A_NEGATIVE',
    'B_POSITIVE', 'B_NEGATIVE',
    'AB_POSITIVE', 'AB_NEGATIVE',
    'O_POSITIVE', 'O_NEGATIVE',
    'UNKNOWN',
  ]).optional(),
  email: emailSchema,
  phoneNumber: phoneSchema,
  secondaryPhone: optionalPhoneSchema,
  socialSecurityNumber: ssnSchema,
  address: addressSchema,
  insurance: insuranceSchema,
  emergencyContact: emergencyContactSchema,
});

export type CreatePatientFormValues = z.infer<typeof createPatientSchema>;

export const updatePatientSchema = createPatientSchema.partial().extend({
  firstName: nameSchema.optional(),
  lastName: nameSchema.optional(),
  dateOfBirth: dateOfBirthSchema.optional(),
  gender: z.enum(['MALE', 'FEMALE', 'NON_BINARY', 'OTHER', 'PREFER_NOT_TO_SAY']).optional(),
  email: emailSchema.optional(),
  phoneNumber: phoneSchema.optional(),
});

export type UpdatePatientFormValues = z.infer<typeof updatePatientSchema>;

export const patientSearchSchema = z.object({
  name: z.string().optional(),
  email: z.string().optional(),
  phoneNumber: z.string().optional(),
  medicalRecordNumber: z.string().optional(),
  status: z.enum(['ACTIVE', 'INACTIVE', 'DECEASED', 'TRANSFERRED', 'DISCHARGED']).optional(),
});

export type PatientSearchFormValues = z.infer<typeof patientSearchSchema>;

export const patientFormSchema = z.object({

  firstName: nameSchema,
  middleName: optionalNameSchema,
  lastName: nameSchema,
  dateOfBirth: z.string().min(1, 'Required'),
  gender: z.enum(['MALE', 'FEMALE', 'NON_BINARY', 'PREFER_NOT_TO_SAY', 'OTHER']),
  bloodType: z.enum([
    'A_POSITIVE', 'A_NEGATIVE',
    'B_POSITIVE', 'B_NEGATIVE',
    'AB_POSITIVE', 'AB_NEGATIVE',
    'O_POSITIVE', 'O_NEGATIVE',
    'UNKNOWN',
  ]).optional().or(z.literal('')),

  email: emailSchema,
  phoneNumber: z.string().min(1, 'Required'),
  secondaryPhone: optionalPhoneSchema,

  street: z.string().max(255).optional().or(z.literal('')),
  city: z.string().max(100).optional().or(z.literal('')),
  state: z.string().max(50).optional().or(z.literal('')),
  zipCode: z.string().max(10).optional().or(z.literal('')),
  country: z.string().max(100).optional().or(z.literal('')),

  insuranceProviderName: z.string().max(100).optional().or(z.literal('')),
  insurancePolicyNumber: z.string().max(50).optional().or(z.literal('')),

  emergencyName: z.string().max(100).optional().or(z.literal('')),
  emergencyRelationship: z.string().max(50).optional().or(z.literal('')),
  emergencyPhone: optionalPhoneSchema,
});

export type PatientFormValues = z.infer<typeof patientFormSchema>;
