

import { DOCUMENT_UPLOAD } from '../constants';
import type { DocumentType, PatientDocumentResponse } from '../types';

export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B';

  const units = ['B', 'KB', 'MB', 'GB'];
  const k = 1024;
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(1))} ${units[i]}`;
}

export function getFileExtension(filename: string): string {
  const parts = filename.split('.');
  return parts.length > 1 ? `.${parts.pop()?.toLowerCase()}` : '';
}

export function getFileIconType(contentType: string): 'pdf' | 'image' | 'document' | 'other' {
  if (contentType.includes('pdf')) return 'pdf';
  if (contentType.startsWith('image/')) return 'image';
  if (contentType.includes('word') || contentType.includes('document')) return 'document';
  return 'other';
}

export function validateFile(file: File): { valid: boolean; error?: string } {
  if (file.size > DOCUMENT_UPLOAD.MAX_FILE_SIZE_BYTES) {
    return {
      valid: false,
      error: `File size exceeds ${DOCUMENT_UPLOAD.MAX_FILE_SIZE_LABEL} limit`,
    };
  }

  const isAllowedType = DOCUMENT_UPLOAD.ALLOWED_MIME_TYPES.includes(
    file.type as typeof DOCUMENT_UPLOAD.ALLOWED_MIME_TYPES[number]
  );

  if (!isAllowedType) {
    return {
      valid: false,
      error: `File type not allowed. Allowed: ${DOCUMENT_UPLOAD.ALLOWED_EXTENSIONS.join(', ')}`,
    };
  }

  return { valid: true };
}

export function extractDocumentTypeFromKey(objectKey: string): DocumentType {
  const match = objectKey.match(/documents\/([A-Z_]+)\//);
  return (match?.[1] as DocumentType) ?? 'OTHER';
}

export function generateUniqueFilename(originalName: string): string {
  const ext = getFileExtension(originalName);
  const baseName = originalName.replace(ext, '');
  const timestamp = Date.now();
  const random = Math.random().toString(36).substring(2, 8);

  return `${baseName}-${timestamp}-${random}${ext}`;
}

export function groupDocumentsByType(
  documents: PatientDocumentResponse[]
): Map<DocumentType, PatientDocumentResponse[]> {
  const grouped = new Map<DocumentType, PatientDocumentResponse[]>();

  for (const doc of documents) {
    const type = extractDocumentTypeFromKey(doc.objectKey);
    const existing = grouped.get(type) ?? [];
    existing.push(doc);
    grouped.set(type, existing);
  }

  return grouped;
}

export function sortDocumentsByDate(
  documents: PatientDocumentResponse[],
  direction: 'asc' | 'desc' = 'desc'
): PatientDocumentResponse[] {
  return [...documents].sort((a, b) => {
    const dateA = new Date(a.uploadedAt).getTime();
    const dateB = new Date(b.uploadedAt).getTime();
    return direction === 'desc' ? dateB - dateA : dateA - dateB;
  });
}

export function filterDocumentsByQuery(
  documents: PatientDocumentResponse[],
  query: string
): PatientDocumentResponse[] {
  if (!query.trim()) return documents;

  const lowerQuery = query.toLowerCase();
  return documents.filter((doc) =>
    doc.fileName.toLowerCase().includes(lowerQuery)
  );
}

export function calculateTotalStorageUsed(documents: PatientDocumentResponse[]): number {
  return documents.reduce((total, doc) => total + doc.size, 0);
}

export function isDownloadUrlExpired(uploadedAt: string, expirationMinutes: number = 15): boolean {
  const uploadTime = new Date(uploadedAt).getTime();
  const now = Date.now();
  const expirationBuffer = (expirationMinutes - 1) * 60 * 1000;

  return now - uploadTime > expirationBuffer;
}

export function createUploadFormData(
  file: File,
  documentType: DocumentType,
  description?: string
): FormData {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('documentType', documentType);
  if (description) {
    formData.append('description', description);
  }
  return formData;
}
