

export type DocumentType =
  | 'LAB_RESULT'
  | 'IMAGING'
  | 'CONSENT_FORM'
  | 'INSURANCE'
  | 'PRESCRIPTION'
  | 'REFERRAL'
  | 'DISCHARGE_SUMMARY'
  | 'MEDICAL_HISTORY'
  | 'VACCINATION'
  | 'OTHER';

export interface UploadDocumentRequest {
  documentType: DocumentType;
  description?: string;
}

export interface PatientDocumentResponse {
  id: string;
  patientId: string;
  fileName: string;
  objectKey: string;
  contentType: string;
  size: number;
  uploadedAt: string;
  downloadUrl: string;
}

export interface DownloadUrlResponse {
  downloadUrl: string;
}

export interface DocumentFilterParams {
  documentType?: DocumentType;
  page?: number;
  size?: number;
}

export interface DocumentUploadState {
  file: File | null;
  documentType: DocumentType;
  description: string;
  isUploading: boolean;
  error: string | null;
  progress: number;
}
