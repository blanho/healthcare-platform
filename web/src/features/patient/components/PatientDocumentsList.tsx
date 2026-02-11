import { useState, useCallback } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Tooltip,
  TextField,
  MenuItem,
  Stack,
} from '@mui/material';
import {
  CloudUpload as UploadIcon,
  Download as DownloadIcon,
  Delete as DeleteIcon,
  Description as FileIcon,
  Image as ImageIcon,
  PictureAsPdf as PdfIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { RbacGuard } from '@/components/auth';
import { ConfirmDialog, EmptyState } from '@/components/shared';
import { usePatientDocuments, useDeleteDocument, useDocumentDownloadUrl } from '../hooks/usePatientDocuments';
import { DocumentUploadDialog } from './DocumentUploadDialog';
import { DocumentTypeChip } from './DocumentTypeChip';
import { DOCUMENT_TYPE_OPTIONS, DOCUMENT_LIST_DEFAULTS } from '../constants';
import { formatFileSize, extractDocumentTypeFromKey } from '../utils';
import type { DocumentType, PatientDocumentResponse } from '../types';

interface PatientDocumentsListProps {
  patientId: string;
}

export function PatientDocumentsList({ patientId }: PatientDocumentsListProps) {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState<number>(DOCUMENT_LIST_DEFAULTS.PAGE_SIZE);
  const [typeFilter, setTypeFilter] = useState<DocumentType | ''>('');
  const [uploadOpen, setUploadOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<PatientDocumentResponse | null>(null);

  const { data, isLoading, refetch } = usePatientDocuments(patientId, {
    page,
    size: pageSize,
    documentType: typeFilter || undefined,
  });

  const deleteMutation = useDeleteDocument(patientId);
  const downloadUrlMutation = useDocumentDownloadUrl();

  const documents = data?.content ?? [];
  const totalElements = data?.totalElements ?? 0;

  const handleDownload = useCallback(
    async (doc: PatientDocumentResponse) => {
      try {
        const url = await downloadUrlMutation.mutateAsync({
          patientId,
          documentId: doc.id,
        });
        window.open(url, '_blank', 'noopener,noreferrer');
      } catch {

      }
    },
    [patientId, downloadUrlMutation]
  );

  const handleDelete = useCallback(async () => {
    if (!deleteTarget) return;
    await deleteMutation.mutateAsync(deleteTarget.id);
    setDeleteTarget(null);
  }, [deleteTarget, deleteMutation]);

  const getFileIcon = (contentType: string) => {
    if (contentType.includes('pdf')) return <PdfIcon color="error" />;
    if (contentType.startsWith('image/')) return <ImageIcon color="primary" />;
    return <FileIcon color="action" />;
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h4">Documents</Typography>
          <Stack direction="row" spacing={1}>
            <Tooltip title="Refresh">
              <IconButton onClick={() => refetch()} sx={{ cursor: 'pointer' }}>
                <RefreshIcon />
              </IconButton>
            </Tooltip>
            <RbacGuard permission="patient:document:write">
              <Button
                variant="contained"
                startIcon={<UploadIcon />}
                onClick={() => setUploadOpen(true)}
                sx={{ cursor: 'pointer', minHeight: 44 }}
              >
                Upload
              </Button>
            </RbacGuard>
          </Stack>
        </Box>

        {}
        <Box sx={{ mb: 2 }}>
          <TextField
            select
            size="small"
            label="Filter by Type"
            value={typeFilter}
            onChange={(e) => {
              setTypeFilter(e.target.value as DocumentType | '');
              setPage(0);
            }}
            sx={{ minWidth: 180 }}
          >
            <MenuItem value="">All Types</MenuItem>
            {DOCUMENT_TYPE_OPTIONS.map((opt) => (
              <MenuItem key={opt.value} value={opt.value}>
                {opt.label}
              </MenuItem>
            ))}
          </TextField>
        </Box>

        {}
        {documents.length === 0 && !isLoading ? (
          <EmptyState
            icon={<FileIcon sx={{ fontSize: 64 }} />}
            title="No documents"
            message="Upload documents to keep patient records organized"
            actionLabel="Upload First Document"
            onAction={() => setUploadOpen(true)}
          />
        ) : (
          <>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>File</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>Size</TableCell>
                    <TableCell>Uploaded</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {documents.map((doc) => (
                    <TableRow
                      key={doc.id}
                      hover
                      sx={{ cursor: 'pointer', '&:hover': { bgcolor: 'action.hover' } }}
                    >
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          {getFileIcon(doc.contentType)}
                          <Box>
                            <Typography variant="body2" fontWeight={500}>
                              {doc.fileName}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {doc.contentType}
                            </Typography>
                          </Box>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <DocumentTypeChip type={extractDocumentTypeFromKey(doc.objectKey)} />
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2">{formatFileSize(doc.size)}</Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2">
                          {format(parseISO(doc.uploadedAt), 'MMM d, yyyy h:mm a')}
                        </Typography>
                      </TableCell>
                      <TableCell align="right">
                        <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                          <Tooltip title="Download">
                            <IconButton
                              size="small"
                              onClick={() => handleDownload(doc)}
                              sx={{ cursor: 'pointer' }}
                            >
                              <DownloadIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          <RbacGuard permission="patient:document:delete">
                            <Tooltip title="Delete">
                              <IconButton
                                size="small"
                                color="error"
                                onClick={() => setDeleteTarget(doc)}
                                sx={{ cursor: 'pointer' }}
                              >
                                <DeleteIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>
                          </RbacGuard>
                        </Stack>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>

            <TablePagination
              component="div"
              count={totalElements}
              page={page}
              onPageChange={(_, p) => setPage(p)}
              rowsPerPage={pageSize}
              onRowsPerPageChange={(e) => {
                setPageSize(parseInt(e.target.value, 10));
                setPage(0);
              }}
              rowsPerPageOptions={[5, 10, 25, 50]}
            />
          </>
        )}
      </CardContent>

      {}
      <DocumentUploadDialog
        open={uploadOpen}
        patientId={patientId}
        onClose={() => setUploadOpen(false)}
      />

      {}
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Document"
        message={`Are you sure you want to delete "${deleteTarget?.fileName}"? This action cannot be undone.`}
        confirmLabel="Delete"
        confirmColor="error"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleteMutation.isPending}
      />
    </Card>
  );
}
