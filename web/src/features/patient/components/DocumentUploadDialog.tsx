import { useState, useCallback, useRef } from 'react';
import {
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Typography,
  LinearProgress,
  Alert,
} from '@mui/material';
import { CloudUpload as UploadIcon, Close as CloseIcon } from '@mui/icons-material';
import { useUploadDocument } from '../hooks/usePatientDocuments';
import { DOCUMENT_TYPE_OPTIONS, DOCUMENT_UPLOAD } from '../constants';
import { validateFile, formatFileSize } from '../utils';
import type { DocumentType } from '../types';

interface DocumentUploadDialogProps {
  open: boolean;
  patientId: string;
  onClose: () => void;
  onSuccess?: () => void;
}

export function DocumentUploadDialog({
  open,
  patientId,
  onClose,
  onSuccess,
}: DocumentUploadDialogProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [file, setFile] = useState<File | null>(null);
  const [documentType, setDocumentType] = useState<DocumentType>('OTHER');
  const [description, setDescription] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [dragOver, setDragOver] = useState(false);

  const uploadMutation = useUploadDocument(patientId);

  const resetForm = useCallback(() => {
    setFile(null);
    setDocumentType('OTHER');
    setDescription('');
    setError(null);
    setDragOver(false);
  }, []);

  const handleClose = useCallback(() => {
    resetForm();
    onClose();
  }, [resetForm, onClose]);

  const handleFileSelect = useCallback(
    (files: FileList | null) => {
      if (!files || files.length === 0) return;
      const selectedFile = files[0];
      const validation = validateFile(selectedFile);
      if (!validation.valid) {
        setError(validation.error ?? 'Invalid file');
        return;
      }
      setError(null);
      setFile(selectedFile);
    },
    []
  );

  const handleDrop = useCallback(
    (e: React.DragEvent) => {
      e.preventDefault();
      setDragOver(false);
      handleFileSelect(e.dataTransfer.files);
    },
    [handleFileSelect]
  );

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(true);
  }, []);

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);
  }, []);

  const handleUpload = useCallback(async () => {
    if (!file) return;

    try {
      await uploadMutation.mutateAsync({
        file,
        documentType,
        description: description || undefined,
      });
      handleClose();
      onSuccess?.();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Upload failed');
    }
  }, [file, documentType, description, uploadMutation, handleClose, onSuccess]);

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        Upload Document
        <Button
          onClick={handleClose}
          sx={{ minWidth: 'auto', p: 1, cursor: 'pointer' }}
          aria-label="Close dialog"
        >
          <CloseIcon />
        </Button>
      </DialogTitle>

      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
            {error}
          </Alert>
        )}

        {}
        <Box
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onClick={() => fileInputRef.current?.click()}
          sx={{
            border: '2px dashed',
            borderColor: dragOver ? 'primary.main' : 'divider',
            borderRadius: 2,
            p: 4,
            textAlign: 'center',
            cursor: 'pointer',
            bgcolor: dragOver ? 'action.hover' : 'background.paper',
            transition: 'all 200ms ease',
            '&:hover': {
              borderColor: 'primary.main',
              bgcolor: 'action.hover',
            },
          }}
        >
          <input
            ref={fileInputRef}
            type="file"
            hidden
            accept={DOCUMENT_UPLOAD.ALLOWED_MIME_TYPES.join(',')}
            onChange={(e) => handleFileSelect(e.target.files)}
          />
          <UploadIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 1 }} />
          {file ? (
            <Box>
              <Typography variant="body1" fontWeight={600}>
                {file.name}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {formatFileSize(file.size)}
              </Typography>
            </Box>
          ) : (
            <Box>
              <Typography variant="body1" fontWeight={600}>
                Drop file here or click to browse
              </Typography>
              <Typography variant="body2" color="text.secondary">
                PDF, images, or Word documents up to {DOCUMENT_UPLOAD.MAX_FILE_SIZE_LABEL}
              </Typography>
            </Box>
          )}
        </Box>

        {}
        <TextField
          select
          fullWidth
          label="Document Type"
          value={documentType}
          onChange={(e) => setDocumentType(e.target.value as DocumentType)}
          sx={{ mt: 3 }}
        >
          {DOCUMENT_TYPE_OPTIONS.map((opt) => (
            <MenuItem key={opt.value} value={opt.value}>
              {opt.label}
            </MenuItem>
          ))}
        </TextField>

        {}
        <TextField
          fullWidth
          multiline
          rows={2}
          label="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="e.g., Annual blood work results"
          inputProps={{ maxLength: 500 }}
          sx={{ mt: 2 }}
        />

        {}
        {uploadMutation.isPending && (
          <Box sx={{ mt: 2 }}>
            <LinearProgress />
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1, textAlign: 'center' }}>
              Uploading...
            </Typography>
          </Box>
        )}
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} sx={{ cursor: 'pointer', minHeight: 44 }}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleUpload}
          disabled={!file || uploadMutation.isPending}
          startIcon={<UploadIcon />}
          sx={{ cursor: 'pointer', minHeight: 44 }}
        >
          Upload
        </Button>
      </DialogActions>
    </Dialog>
  );
}
