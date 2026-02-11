import { apiClient } from '@/lib';
import type { PageResponse, PageParams } from '@/types';
import type {
  PatientDocumentResponse,
  DocumentType,
  DownloadUrlResponse,
} from '../types/document.types';

const getBase = (patientId: string) => `/api/v1/patients/${patientId}/documents`;

export const documentApi = {
  upload: (
    patientId: string,
    file: File,
    documentType: DocumentType,
    description?: string
  ): Promise<PatientDocumentResponse> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('documentType', documentType);
    if (description) {
      formData.append('description', description);
    }

    return apiClient
      .post<PatientDocumentResponse>(getBase(patientId), formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((r) => r.data);
  },

  list: (
    patientId: string,
    params?: PageParams & { documentType?: DocumentType }
  ): Promise<PageResponse<PatientDocumentResponse>> =>
    apiClient.get<PageResponse<PatientDocumentResponse>>(getBase(patientId), { params }).then((r) => r.data),

  getById: (patientId: string, documentId: string): Promise<PatientDocumentResponse> =>
    apiClient.get<PatientDocumentResponse>(`${getBase(patientId)}/${documentId}`).then((r) => r.data),

  getDownloadUrl: (patientId: string, documentId: string): Promise<string> =>
    apiClient
      .get<DownloadUrlResponse>(`${getBase(patientId)}/${documentId}/download-url`)
      .then((r) => r.data.downloadUrl),

  delete: (patientId: string, documentId: string): Promise<void> =>
    apiClient.delete(`${getBase(patientId)}/${documentId}`),
};
