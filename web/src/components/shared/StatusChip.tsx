import { Chip, type ChipProps } from '@mui/material';
import { statusColors } from '@/theme';

interface StatusChipProps extends Omit<ChipProps, 'color'> {
  status: string;
  label?: string;
}

const formatLabel = (status: string): string =>
  status
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());

export function StatusChip({ status, label, ...rest }: StatusChipProps) {
  const color = (statusColors as Record<string, ChipProps['color']>)[status] ?? 'default';

  return (
    <Chip
      label={label ?? formatLabel(status)}
      color={color}
      size="small"
      variant="outlined"
      {...rest}
    />
  );
}
