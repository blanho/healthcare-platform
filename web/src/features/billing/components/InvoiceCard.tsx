import {
  Card,
  CardContent,
  CardActionArea,
  Box,
  Typography,
  Stack,
  Divider,
  Chip,
  Avatar,
} from '@mui/material';
import { Receipt as InvoiceIcon } from '@mui/icons-material';
import { format, parseISO, isPast } from 'date-fns';
import { InvoiceStatusChip, CurrencyDisplay, BalanceDueBadge } from './BillingChips';
import type { InvoiceSummaryResponse } from '../types/billing.types';

interface InvoiceCardProps {
  invoice: InvoiceSummaryResponse;
  onClick?: () => void;
  compact?: boolean;
}

export function InvoiceCard({ invoice, onClick, compact = false }: InvoiceCardProps) {
  const isOverdue =
    invoice.status === 'OVERDUE' || (invoice.balanceDue > 0 && isPast(parseISO(invoice.dueDate)));

  return (
    <Card
      sx={{
        height: '100%',
        transition: 'box-shadow 200ms, transform 200ms',
        borderLeft: isOverdue ? '4px solid' : undefined,
        borderColor: isOverdue ? 'error.main' : undefined,
        '&:hover': onClick ? { boxShadow: 3, transform: 'translateY(-2px)' } : undefined,
      }}
    >
      <CardActionArea
        onClick={onClick}
        disabled={!onClick}
        sx={{ height: '100%', cursor: onClick ? 'pointer' : 'default' }}
      >
        <CardContent>
          {}
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
              mb: 2,
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Avatar
                sx={{ width: 36, height: 36, bgcolor: 'primary.light', color: 'primary.main' }}
              >
                <InvoiceIcon fontSize="small" />
              </Avatar>
              <Box>
                <Typography variant="body1" fontWeight={600} fontFamily="monospace">
                  {invoice.invoiceNumber}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {invoice.itemCount} item{invoice.itemCount !== 1 ? 's' : ''}
                </Typography>
              </Box>
            </Box>
            <InvoiceStatusChip status={invoice.status} />
          </Box>

          {}
          <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block">
                Invoice Date
              </Typography>
              <Typography variant="body2">
                {format(parseISO(invoice.invoiceDate), 'MMM d, yyyy')}
              </Typography>
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block">
                Due Date
              </Typography>
              <Typography variant="body2" color={isOverdue ? 'error.main' : 'text.primary'}>
                {format(parseISO(invoice.dueDate), 'MMM d, yyyy')}
              </Typography>
            </Box>
          </Stack>

          {!compact && <Divider sx={{ my: 1.5 }} />}

          {}
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Box>
              <Typography variant="caption" color="text.secondary" display="block">
                Total
              </Typography>
              <CurrencyDisplay amount={invoice.totalAmount} size="medium" />
            </Box>
            <Box sx={{ textAlign: 'right' }}>
              <Typography variant="caption" color="text.secondary" display="block">
                Balance Due
              </Typography>
              <BalanceDueBadge balance={invoice.balanceDue} isOverdue={isOverdue} />
            </Box>
          </Box>

          {}
          {invoice.hasInsuranceClaim && (
            <Chip
              label="Insurance Claim"
              size="small"
              variant="outlined"
              color="info"
              sx={{ mt: 2 }}
            />
          )}
        </CardContent>
      </CardActionArea>
    </Card>
  );
}
