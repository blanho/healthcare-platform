

import { z } from 'zod';
import { DOCUMENT_UPLOAD } from '../constants';

export const documentTypeSchema = z.enum([
  'LAB_RESULT',
  'IMAGING',
  'CONSENT_FORM',
  'INSURANCE',
  'PRESCRIPTION',
  'REFERRAL',
  'DISCHARGE_SUMMARY',
  'MEDICAL_HISTORY',
  'VACCINATION',
  'OTHER',
]);

export const fileSchema = z
  .instanceof(File)
  .refine(
    (file) => file.size <= DOCUMENT_UPLOAD.MAX_FILE_SIZE_BYTES,
    `File size must be less than ${DOCUMENT_UPLOAD.MAX_FILE_SIZE_LABEL}`
  )
  .refine(
    (file) => DOCUMENT_UPLOAD.ALLOWED_MIME_TYPES.includes(file.type as typeof DOCUMENT_UPLOAD.ALLOWED_MIME_TYPES[number]),
    `File type not allowed. Allowed: ${DOCUMENT_UPLOAD.ALLOWED_EXTENSIONS.join(', ')}`
  );

export const uploadDocumentSchema = z.object({
  file: fileSchema,
  documentType: documentTypeSchema,
  description: z
    .string()
    .max(500, 'Description must be less than 500 characters')
    .optional()
    .or(z.literal('')),
});

export type UploadDocumentFormValues = z.infer<typeof uploadDocumentSchema>;

export const documentFilterSchema = z.object({
  documentType: documentTypeSchema.optional(),
  page: z.number().min(0).optional(),
  size: z.number().min(1).max(100).optional(),
});

export type DocumentFilterValues = z.infer<typeof documentFilterSchema>;
