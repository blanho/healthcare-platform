

import { z } from 'zod';
import { RECORD_VALIDATION, VITAL_SIGN_RANGES } from '../constants';

export const recordTypeSchema = z.enum([
  'CONSULTATION',
  'FOLLOW_UP',
  'EMERGENCY',
  'LAB_RESULT',
  'IMAGING',
  'SURGERY',
  'DISCHARGE_SUMMARY',
  'PROGRESS_NOTE',
  'ADMISSION',
  'REFERRAL',
  'VACCINATION',
  'PRESCRIPTION',
  'PROCEDURE',
  'THERAPY',
  'OTHER',
]);

export const recordStatusSchema = z.enum(['DRAFT', 'FINALIZED', 'AMENDED', 'VOIDED']);

export const diagnosisTypeSchema = z.enum(['PRIMARY', 'SECONDARY', 'DIFFERENTIAL', 'RULE_OUT']);

export const vitalSignsSchema = z
  .object({
    systolicBp: z
      .number()
      .min(VITAL_SIGN_RANGES.BLOOD_PRESSURE.SYSTOLIC.MIN)
      .max(VITAL_SIGN_RANGES.BLOOD_PRESSURE.SYSTOLIC.MAX)
      .optional(),
    diastolicBp: z
      .number()
      .min(VITAL_SIGN_RANGES.BLOOD_PRESSURE.DIASTOLIC.MIN)
      .max(VITAL_SIGN_RANGES.BLOOD_PRESSURE.DIASTOLIC.MAX)
      .optional(),
    heartRate: z
      .number()
      .min(VITAL_SIGN_RANGES.HEART_RATE.MIN)
      .max(VITAL_SIGN_RANGES.HEART_RATE.MAX)
      .optional(),
    respiratoryRate: z
      .number()
      .min(VITAL_SIGN_RANGES.RESPIRATORY_RATE.MIN)
      .max(VITAL_SIGN_RANGES.RESPIRATORY_RATE.MAX)
      .optional(),
    temperature: z
      .number()
      .min(VITAL_SIGN_RANGES.TEMPERATURE.MIN)
      .max(VITAL_SIGN_RANGES.TEMPERATURE.MAX)
      .optional(),
    oxygenSaturation: z
      .number()
      .min(VITAL_SIGN_RANGES.OXYGEN_SATURATION.MIN)
      .max(VITAL_SIGN_RANGES.OXYGEN_SATURATION.MAX)
      .optional(),
    weightKg: z
      .number()
      .min(VITAL_SIGN_RANGES.WEIGHT.MIN)
      .max(VITAL_SIGN_RANGES.WEIGHT.MAX)
      .optional(),
    heightCm: z
      .number()
      .min(VITAL_SIGN_RANGES.HEIGHT.MIN)
      .max(VITAL_SIGN_RANGES.HEIGHT.MAX)
      .optional(),
    painLevel: z
      .number()
      .min(VITAL_SIGN_RANGES.PAIN_LEVEL.MIN)
      .max(VITAL_SIGN_RANGES.PAIN_LEVEL.MAX)
      .optional(),
    recordedAt: z.string().optional(),
  })
  .optional();

export type VitalSignsFormValues = z.infer<typeof vitalSignsSchema>;

export const soapNoteSchema = z
  .object({
    subjective: z.string().max(RECORD_VALIDATION.SOAP_SECTION.MAX_LENGTH).optional().or(z.literal('')),
    objective: z.string().max(RECORD_VALIDATION.SOAP_SECTION.MAX_LENGTH).optional().or(z.literal('')),
    assessment: z.string().max(RECORD_VALIDATION.SOAP_SECTION.MAX_LENGTH).optional().or(z.literal('')),
    plan: z.string().max(RECORD_VALIDATION.SOAP_SECTION.MAX_LENGTH).optional().or(z.literal('')),
  })
  .optional();

export type SoapNoteFormValues = z.infer<typeof soapNoteSchema>;

export const diagnosisSchema = z.object({
  code: z
    .string()
    .min(1, 'Diagnosis code is required')
    .max(RECORD_VALIDATION.DIAGNOSIS_CODE.MAX_LENGTH)
    .regex(RECORD_VALIDATION.DIAGNOSIS_CODE.PATTERN, 'Invalid ICD-10 code format'),
  description: z
    .string()
    .min(1, 'Description is required')
    .max(RECORD_VALIDATION.DIAGNOSIS_DESCRIPTION.MAX_LENGTH),
  type: diagnosisTypeSchema.optional().default('PRIMARY'),
  primary: z.boolean().optional().default(false),
  onsetDate: z.string().optional(),
  resolvedDate: z.string().optional(),
  notes: z.string().max(500).optional().or(z.literal('')),
});

export type DiagnosisFormValues = z.infer<typeof diagnosisSchema>;

export const createMedicalRecordSchema = z.object({
  patientId: z.string().uuid('Please select a valid patient'),
  providerId: z.string().uuid('Please select a valid provider'),
  appointmentId: z.string().uuid().optional().or(z.literal('')),
  recordType: recordTypeSchema,
  recordDate: z.string().min(1, 'Record date is required'),
  chiefComplaint: z
    .string()
    .max(RECORD_VALIDATION.CHIEF_COMPLAINT.MAX_LENGTH)
    .optional()
    .or(z.literal('')),
  notes: z.string().max(RECORD_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
  vitalSigns: vitalSignsSchema,
  soapNote: soapNoteSchema,
  diagnoses: z.array(diagnosisSchema).optional(),
});

export type CreateMedicalRecordFormValues = z.infer<typeof createMedicalRecordSchema>;

export const updateMedicalRecordSchema = z.object({
  chiefComplaint: z
    .string()
    .max(RECORD_VALIDATION.CHIEF_COMPLAINT.MAX_LENGTH)
    .optional()
    .or(z.literal('')),
  notes: z.string().max(RECORD_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
  vitalSigns: vitalSignsSchema,
  soapNote: soapNoteSchema,
  diagnoses: z.array(diagnosisSchema).optional(),
});

export type UpdateMedicalRecordFormValues = z.infer<typeof updateMedicalRecordSchema>;

export const amendRecordSchema = z.object({
  reason: z
    .string()
    .min(RECORD_VALIDATION.AMEND_REASON.MIN_LENGTH, 'Please provide a detailed reason (at least 10 characters)')
    .max(RECORD_VALIDATION.AMEND_REASON.MAX_LENGTH),
  additionalNotes: z.string().max(RECORD_VALIDATION.NOTES.MAX_LENGTH).optional().or(z.literal('')),
});

export type AmendRecordFormValues = z.infer<typeof amendRecordSchema>;

export const voidRecordSchema = z.object({
  reason: z
    .string()
    .min(RECORD_VALIDATION.VOID_REASON.MIN_LENGTH, 'Please provide a detailed reason (at least 10 characters)')
    .max(RECORD_VALIDATION.VOID_REASON.MAX_LENGTH),
});

export type VoidRecordFormValues = z.infer<typeof voidRecordSchema>;

export const recordSearchSchema = z.object({
  patientId: z.string().uuid().optional().or(z.literal('')),
  providerId: z.string().uuid().optional().or(z.literal('')),
  recordType: recordTypeSchema.optional(),
  status: recordStatusSchema.optional(),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
});

export type RecordSearchFormValues = z.infer<typeof recordSearchSchema>;
