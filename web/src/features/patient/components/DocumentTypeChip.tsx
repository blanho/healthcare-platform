import { Chip } from '@mui/material';
import type { DocumentType } from '../types';
import { DOCUMENT_TYPE_LABELS, DOCUMENT_TYPE_COLORS } from '../constants';

interface DocumentTypeChipProps {
  type: DocumentType;
}

export function DocumentTypeChip({ type }: DocumentTypeChipProps) {
  return (
    <Chip
      label={DOCUMENT_TYPE_LABELS[type] ?? type}
      size="small"
      color={DOCUMENT_TYPE_COLORS[type] ?? 'default'}
      variant="outlined"
    />
  );
}
