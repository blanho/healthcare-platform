import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  Divider,
  Stack,
  Chip,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  Paper,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Skeleton,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Print as PrintIcon,
  Payment as PaymentIcon,
  Send as SendIcon,
  Cancel as CancelIcon,
  AttachMoney as MoneyIcon,
} from '@mui/icons-material';
import { PageHeader } from '@/components/shared';
import { useInvoice, useRecordPayment, useInvoiceMutations } from '../hooks/useBilling';
import { InvoiceStatusChip, CurrencyDisplay } from '../components';
import { format, parseISO, isPast } from 'date-fns';
import { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { paymentDialogSchema, type PaymentDialogFormValues } from '../schemas';
import type { PaymentMethod } from '@/types';

export function InvoiceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [paymentDialogOpen, setPaymentDialogOpen] = useState(false);

  const { data: invoice, isLoading, isError } = useInvoice(id!);
  const { mutate: recordPayment, isPending: isRecordingPayment } = useRecordPayment();
  const { sendInvoice, cancelInvoice, voidInvoice: _voidInvoice } = useInvoiceMutations();

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
  } = useForm<PaymentDialogFormValues>({
    resolver: zodResolver(paymentDialogSchema),
    defaultValues: {
      amount: invoice?.balanceDue || 0,
      paymentMethod: '',
      cardLastFour: '',
      notes: '',
    },
  });

  const paymentMethod = watch('paymentMethod');

  const handleRecordPayment = (data: PaymentDialogFormValues) => {
    if (!invoice) return;
    recordPayment(
      {
        invoiceId: invoice.id,
        amount: data.amount,
        paymentMethod: data.paymentMethod as PaymentMethod,
        cardLastFour: data.cardLastFour,
        notes: data.notes,
      },
      {
        onSuccess: () => {
          setPaymentDialogOpen(false);
          reset();
        },
      },
    );
  };

  if (isLoading) {
    return (
      <Box>
        <Skeleton variant="text" width={200} height={40} />
        <Skeleton variant="rectangular" height={400} sx={{ mt: 2, borderRadius: 2 }} />
      </Box>
    );
  }

  if (isError || !invoice) {
    return (
      <Box>
        <Alert severity="error">Invoice not found</Alert>
        <Button startIcon={<BackIcon />} onClick={() => navigate(-1)} sx={{ mt: 2 }}>
          Go Back
        </Button>
      </Box>
    );
  }

  const isOverdue =
    invoice.status === 'OVERDUE' || (invoice.balanceDue > 0 && isPast(parseISO(invoice.dueDate)));
  const canRecordPayment = invoice.balanceDue > 0 && invoice.status !== 'CANCELLED';
  const canSend = invoice.status === 'DRAFT';
  const canCancel = !['PAID', 'CANCELLED', 'REFUNDED'].includes(invoice.status);

  return (
    <Box>
      <PageHeader
        title={`Invoice ${invoice.invoiceNumber}`}
        subtitle={`Created on ${format(parseISO(invoice.invoiceDate), 'MMMM d, yyyy')}`}
        breadcrumbs={[{ label: 'Billing', href: '/app/billing' }, { label: invoice.invoiceNumber }]}
        action={
          <Stack direction="row" spacing={2}>
            <Button
              variant="outlined"
              startIcon={<BackIcon />}
              onClick={() => navigate(-1)}
              sx={{ minHeight: 44 }}
            >
              Back
            </Button>
            <Button
              variant="outlined"
              startIcon={<PrintIcon />}
              onClick={() => window.print()}
              sx={{ minHeight: 44 }}
            >
              Print
            </Button>
            {canRecordPayment && (
              <Button
                variant="contained"
                color="success"
                startIcon={<PaymentIcon />}
                onClick={() => setPaymentDialogOpen(true)}
                sx={{ minHeight: 44 }}
              >
                Record Payment
              </Button>
            )}
          </Stack>
        }
      />

      {isOverdue && (
        <Alert severity="error" sx={{ mb: 3 }}>
          This invoice is overdue. Please follow up on payment collection.
        </Alert>
      )}

      <Grid container spacing={3}>
        {}
        <Grid size={{ xs: 12, lg: 8 }}>
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Box
                sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'flex-start',
                  mb: 3,
                }}
              >
                <Box>
                  <Typography variant="h4" fontFamily="monospace" sx={{ mb: 1 }}>
                    {invoice.invoiceNumber}
                  </Typography>
                  <InvoiceStatusChip status={invoice.status} size="medium" />
                </Box>
                <Box sx={{ textAlign: 'right' }}>
                  <Typography variant="caption" color="text.secondary" display="block">
                    Due Date
                  </Typography>
                  <Typography variant="h4" color={isOverdue ? 'error.main' : 'text.primary'}>
                    {format(parseISO(invoice.dueDate), 'MMMM d, yyyy')}
                  </Typography>
                </Box>
              </Box>

              <Divider sx={{ my: 2 }} />

              {}
              <TableContainer component={Paper} variant="outlined">
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Description</TableCell>
                      <TableCell>Code</TableCell>
                      <TableCell align="right">Qty</TableCell>
                      <TableCell align="right">Unit Price</TableCell>
                      <TableCell align="right">Total</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {invoice.items.map((item) => (
                      <TableRow key={item.id}>
                        <TableCell>{item.description}</TableCell>
                        <TableCell>
                          {item.procedureCode && (
                            <Chip label={item.procedureCode} size="small" variant="outlined" />
                          )}
                        </TableCell>
                        <TableCell align="right">{item.quantity}</TableCell>
                        <TableCell align="right">
                          <CurrencyDisplay amount={item.unitPrice} size="small" />
                        </TableCell>
                        <TableCell align="right">
                          <CurrencyDisplay amount={item.totalPrice} size="small" />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>

              {}
              <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
                <Box sx={{ width: 300 }}>
                  <Stack spacing={1}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Typography>Subtotal</Typography>
                      <CurrencyDisplay amount={invoice.subtotal} />
                    </Box>
                    {invoice.taxAmount > 0 && (
                      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Typography>Tax</Typography>
                        <CurrencyDisplay amount={invoice.taxAmount} />
                      </Box>
                    )}
                    {invoice.discountAmount > 0 && (
                      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Typography color="success.main">Discount</Typography>
                        <Typography color="success.main">
                          -<CurrencyDisplay amount={invoice.discountAmount} />
                        </Typography>
                      </Box>
                    )}
                    <Divider />
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Typography fontWeight={600}>Total</Typography>
                      <CurrencyDisplay amount={invoice.totalAmount} size="large" />
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Typography>Paid</Typography>
                      <CurrencyDisplay amount={invoice.paidAmount} color="success" />
                    </Box>
                    <Divider />
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', pt: 1 }}>
                      <Typography fontWeight={700} variant="h4">
                        Balance Due
                      </Typography>
                      <CurrencyDisplay
                        amount={invoice.balanceDue}
                        size="large"
                        color={invoice.balanceDue > 0 ? 'error' : 'success'}
                      />
                    </Box>
                  </Stack>
                </Box>
              </Box>
            </CardContent>
          </Card>

          {}
          {invoice.notes && (
            <Card>
              <CardContent>
                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                  Notes
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {invoice.notes}
                </Typography>
              </CardContent>
            </Card>
          )}
        </Grid>

        {}
        <Grid size={{ xs: 12, lg: 4 }}>
          {}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="subtitle2" sx={{ mb: 2 }}>
                Actions
              </Typography>
              <Stack spacing={1.5}>
                {canSend && (
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<SendIcon />}
                    onClick={() => sendInvoice.mutate(invoice.id)}
                    sx={{ minHeight: 44 }}
                  >
                    Send Invoice
                  </Button>
                )}
                {canCancel && (
                  <Button
                    fullWidth
                    variant="outlined"
                    color="error"
                    startIcon={<CancelIcon />}
                    onClick={() => cancelInvoice.mutate(invoice.id)}
                    sx={{ minHeight: 44 }}
                  >
                    Cancel Invoice
                  </Button>
                )}
              </Stack>
            </CardContent>
          </Card>

          {}
          {invoice.insuranceClaimNumber && (
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Typography variant="subtitle2" sx={{ mb: 2 }}>
                  Insurance Claim
                </Typography>
                <Stack spacing={2}>
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Claim Number
                    </Typography>
                    <Typography variant="body2" fontFamily="monospace">
                      {invoice.insuranceClaimNumber}
                    </Typography>
                  </Box>
                  {invoice.insuranceAmount && (
                    <Box>
                      <Typography variant="caption" color="text.secondary">
                        Insurance Amount
                      </Typography>
                      <CurrencyDisplay amount={invoice.insuranceAmount} />
                    </Box>
                  )}
                </Stack>
              </CardContent>
            </Card>
          )}

          {}
          <Card>
            <CardContent>
              <Typography variant="subtitle2" sx={{ mb: 2 }}>
                Timeline
              </Typography>
              <Stack spacing={2}>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Created
                  </Typography>
                  <Typography variant="body2">
                    {format(parseISO(invoice.createdAt), 'MMM d, yyyy h:mm a')}
                  </Typography>
                </Box>
                {invoice.paidDate && (
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Paid
                    </Typography>
                    <Typography variant="body2" color="success.main">
                      {format(parseISO(invoice.paidDate), 'MMM d, yyyy h:mm a')}
                    </Typography>
                  </Box>
                )}
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {}
      <Dialog
        open={paymentDialogOpen}
        onClose={() => setPaymentDialogOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <form onSubmit={handleSubmit(handleRecordPayment)}>
          <DialogTitle>Record Payment</DialogTitle>
          <DialogContent>
            <Stack spacing={3} sx={{ mt: 1 }}>
              <Controller
                name="amount"
                control={control}
                render={({ field }) => (
                  <TextField
                    {...field}
                    label="Amount"
                    type="number"
                    fullWidth
                    error={!!errors.amount}
                    helperText={
                      errors.amount?.message || `Balance due: $${invoice.balanceDue.toFixed(2)}`
                    }
                    slotProps={{
                      input: {
                        startAdornment: <MoneyIcon sx={{ mr: 1, color: 'text.secondary' }} />,
                      },
                    }}
                  />
                )}
              />

              <Controller
                name="paymentMethod"
                control={control}
                render={({ field }) => (
                  <FormControl fullWidth error={!!errors.paymentMethod}>
                    <InputLabel>Payment Method</InputLabel>
                    <Select {...field} label="Payment Method">
                      <MenuItem value="CASH">Cash</MenuItem>
                      <MenuItem value="CREDIT_CARD">Credit Card</MenuItem>
                      <MenuItem value="DEBIT_CARD">Debit Card</MenuItem>
                      <MenuItem value="CHECK">Check</MenuItem>
                      <MenuItem value="BANK_TRANSFER">Bank Transfer</MenuItem>
                    </Select>
                  </FormControl>
                )}
              />

              {(paymentMethod === 'CREDIT_CARD' || paymentMethod === 'DEBIT_CARD') && (
                <Controller
                  name="cardLastFour"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Card Last 4 Digits"
                      placeholder="1234"
                      fullWidth
                      inputProps={{ maxLength: 4 }}
                    />
                  )}
                />
              )}

              <Controller
                name="notes"
                control={control}
                render={({ field }) => (
                  <TextField {...field} label="Notes" multiline rows={2} fullWidth />
                )}
              />
            </Stack>
          </DialogContent>
          <DialogActions sx={{ px: 3, pb: 2 }}>
            <Button onClick={() => setPaymentDialogOpen(false)} sx={{ minHeight: 44 }}>
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              color="success"
              disabled={isRecordingPayment}
              sx={{ minHeight: 44 }}
            >
              Record Payment
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
}
