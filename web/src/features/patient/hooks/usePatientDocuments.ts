import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { documentApi } from '../api/document.api';
import type { PageParams } from '@/types';
import type { DocumentType } from '../types/document.types';

const DOCUMENT_KEYS = {
  all: ['patient-documents'] as const,
  list: (patientId: string, params?: PageParams & { documentType?: DocumentType }) =>
    [...DOCUMENT_KEYS.all, 'list', patientId, params] as const,
  detail: (patientId: string, documentId: string) =>
    [...DOCUMENT_KEYS.all, 'detail', patientId, documentId] as const,
};

export function usePatientDocuments(
  patientId: string,
  params?: PageParams & { documentType?: DocumentType }
) {
  return useQuery({
    queryKey: DOCUMENT_KEYS.list(patientId, params),
    queryFn: () => documentApi.list(patientId, params),
    enabled: !!patientId,
  });
}

export function usePatientDocument(patientId: string, documentId: string) {
  return useQuery({
    queryKey: DOCUMENT_KEYS.detail(patientId, documentId),
    queryFn: () => documentApi.getById(patientId, documentId),
    enabled: !!patientId && !!documentId,
  });
}

export function useUploadDocument(patientId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      file,
      documentType,
      description,
    }: {
      file: File;
      documentType: DocumentType;
      description?: string;
    }) => documentApi.upload(patientId, file, documentType, description),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: DOCUMENT_KEYS.list(patientId) });
    },
  });
}

export function useDeleteDocument(patientId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (documentId: string) => documentApi.delete(patientId, documentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: DOCUMENT_KEYS.list(patientId) });
    },
  });
}

export function useDocumentDownloadUrl() {
  return useMutation({
    mutationFn: ({ patientId, documentId }: { patientId: string; documentId: string }) =>
      documentApi.getDownloadUrl(patientId, documentId),
  });
}
