import { Chip, Box, Typography } from '@mui/material';
import type { InvoiceStatus, ClaimStatus, PaymentStatus, PaymentMethod } from '@/types';
import {
  invoiceStatusConfig,
  claimStatusConfig,
  paymentStatusConfig,
  paymentMethodConfig,
} from './billing-chip-config';

interface InvoiceStatusChipProps {
  status: InvoiceStatus;
  size?: 'small' | 'medium';
}

export function InvoiceStatusChip({ status, size = 'small' }: InvoiceStatusChipProps) {
  const config = invoiceStatusConfig[status] || { label: status, color: 'default' };
  return <Chip label={config.label} color={config.color} size={size} />;
}

interface ClaimStatusChipProps {
  status: ClaimStatus;
  size?: 'small' | 'medium';
}

export function ClaimStatusChip({ status, size = 'small' }: ClaimStatusChipProps) {
  const config = claimStatusConfig[status] || { label: status, color: 'default' };
  return <Chip label={config.label} color={config.color} size={size} />;
}

interface PaymentStatusChipProps {
  status: PaymentStatus;
  size?: 'small' | 'medium';
}

export function PaymentStatusChip({ status, size = 'small' }: PaymentStatusChipProps) {
  const config = paymentStatusConfig[status] || { label: status, color: 'default' };
  return <Chip label={config.label} color={config.color} size={size} />;
}

interface PaymentMethodChipProps {
  method: PaymentMethod;
  cardLastFour?: string | null;
  cardBrand?: string | null;
}

export function PaymentMethodChip({ method, cardLastFour, cardBrand }: PaymentMethodChipProps) {
  const config = paymentMethodConfig[method] || { label: method, abbreviation: '?' };
  const label = cardLastFour ? `${cardBrand || 'Card'} ****${cardLastFour}` : config.label;

  return <Chip label={label} size="small" variant="outlined" />;
}

interface CurrencyDisplayProps {
  amount: number;
  size?: 'small' | 'medium' | 'large';
  color?: 'success' | 'error' | 'primary' | 'text';
  showSign?: boolean;
}

export function CurrencyDisplay({
  amount,
  size = 'medium',
  color = 'text',
  showSign = false,
}: CurrencyDisplayProps) {
  const formatted = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);

  const sizeStyles = {
    small: { fontSize: '0.875rem', fontWeight: 500 },
    medium: { fontSize: '1rem', fontWeight: 600 },
    large: { fontSize: '1.5rem', fontWeight: 700 },
  };

  const colorMap = {
    success: 'success.main',
    error: 'error.main',
    primary: 'primary.main',
    text: 'text.primary',
  };

  const displayValue = showSign && amount > 0 ? `+${formatted}` : formatted;

  return (
    <Typography sx={{ ...sizeStyles[size], color: colorMap[color] }}>{displayValue}</Typography>
  );
}

interface BalanceDueBadgeProps {
  balance: number;
  isOverdue?: boolean;
}

export function BalanceDueBadge({ balance, isOverdue = false }: BalanceDueBadgeProps) {
  if (balance <= 0) {
    return <Chip label="Paid in Full" color="success" size="small" />;
  }

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
      <CurrencyDisplay amount={balance} color={isOverdue ? 'error' : 'primary'} />
      {isOverdue && <Chip label="Overdue" color="error" size="small" />}
    </Box>
  );
}
