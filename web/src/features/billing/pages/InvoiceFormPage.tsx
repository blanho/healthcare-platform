import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  TextField,
  Stack,
  Divider,
  IconButton,
  Alert,
  Autocomplete,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Add as AddIcon,
  Delete as DeleteIcon,
  Save as SaveIcon,
} from '@mui/icons-material';
import { DatePicker } from '@mui/x-date-pickers';
import { PageHeader } from '@/components/shared';
import { useCreateInvoice } from '../hooks/useBilling';
import { usePatients } from '@/features/patient/hooks/usePatient';
import { useForm, Controller, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { addDays } from 'date-fns';
import { invoiceFormSchema, type InvoiceFormValues } from '../schemas';

export function InvoiceFormPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = !!id;

  const { mutate: createInvoice, isPending: isCreating } = useCreateInvoice();
  const { data: patientsData } = usePatients({ page: 0, size: 100 });
  const patients = patientsData?.content || [];

  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<InvoiceFormValues>({
    resolver: zodResolver(invoiceFormSchema),
    defaultValues: {
      patientId: '',
      dueDate: addDays(new Date(), 30),
      taxRate: 0,
      discountAmount: 0,
      notes: '',
      items: [{ description: '', procedureCode: '', quantity: 1, unitPrice: 0 }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  const items = watch('items');
  const taxRate = watch('taxRate') || 0;
  const discountAmount = watch('discountAmount') || 0;

  const subtotal = items.reduce((sum, item) => sum + item.quantity * item.unitPrice, 0);
  const taxAmount = subtotal * (taxRate / 100);
  const total = subtotal + taxAmount - discountAmount;

  const onSubmit = (data: InvoiceFormValues) => {
    createInvoice(
      {
        ...data,
        dueDate: data.dueDate.toISOString().split('T')[0],
        items: data.items.map((item) => ({
          ...item,
          procedureCode: item.procedureCode || undefined,
        })),
      },
      {
        onSuccess: (invoice) => {
          navigate(`/app/billing/invoices/${invoice.id}`);
        },
      },
    );
  };

  return (
    <Box>
      <PageHeader
        title={isEdit ? 'Edit Invoice' : 'New Invoice'}
        subtitle={isEdit ? 'Update invoice details' : 'Create a new invoice for a patient'}
        breadcrumbs={[
          { label: 'Billing', href: '/app/billing' },
          { label: isEdit ? 'Edit' : 'New Invoice' },
        ]}
        action={
          <Button
            variant="outlined"
            startIcon={<BackIcon />}
            onClick={() => navigate(-1)}
            sx={{ minHeight: 44 }}
          >
            Cancel
          </Button>
        }
      />

      <form onSubmit={handleSubmit(onSubmit)}>
        <Grid container spacing={3}>
          <Grid size={{ xs: 12, lg: 8 }}>
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 3 }}>
                  Invoice Details
                </Typography>

                <Grid container spacing={3}>
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="patientId"
                      control={control}
                      render={({ field }) => (
                        <Autocomplete
                          options={patients}
                          getOptionLabel={(option) => option.fullName}
                          value={patients.find((p) => p.id === field.value) || null}
                          onChange={(_, newValue) => field.onChange(newValue?.id || '')}
                          renderInput={(params) => (
                            <TextField
                              {...params}
                              label="Patient"
                              error={!!errors.patientId}
                              helperText={errors.patientId?.message}
                              required
                            />
                          )}
                        />
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="dueDate"
                      control={control}
                      render={({ field }) => (
                        <DatePicker
                          label="Due Date"
                          value={field.value}
                          onChange={field.onChange}
                          slotProps={{
                            textField: {
                              fullWidth: true,
                              error: !!errors.dueDate,
                              helperText: errors.dueDate?.message,
                              required: true,
                            },
                          }}
                        />
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={{ xs: 12 }}>
                    <Controller
                      name="notes"
                      control={control}
                      render={({ field }) => (
                        <TextField {...field} label="Notes" multiline rows={2} fullWidth />
                      )}
                    />
                  </Grid>
                </Grid>
              </CardContent>
            </Card>

            {}
            <Card>
              <CardContent>
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    mb: 3,
                  }}
                >
                  <Typography variant="h4">Line Items</Typography>
                  <Button
                    variant="outlined"
                    size="small"
                    startIcon={<AddIcon />}
                    onClick={() =>
                      append({ description: '', procedureCode: '', quantity: 1, unitPrice: 0 })
                    }
                    sx={{ minHeight: 44 }}
                  >
                    Add Item
                  </Button>
                </Box>

                {errors.items && !Array.isArray(errors.items) && (
                  <Alert severity="error" sx={{ mb: 2 }}>
                    {errors.items.message}
                  </Alert>
                )}

                <Stack spacing={2}>
                  {fields.map((field, index) => (
                    <Box
                      key={field.id}
                      sx={{
                        p: 2,
                        border: 1,
                        borderColor: 'divider',
                        borderRadius: 1,
                        bgcolor: 'grey.50',
                      }}
                    >
                      <Grid container spacing={2} alignItems="center">
                        <Grid size={{ xs: 12, sm: 4 }}>
                          <Controller
                            name={`items.${index}.description`}
                            control={control}
                            render={({ field }) => (
                              <TextField
                                {...field}
                                label="Description"
                                fullWidth
                                size="small"
                                error={!!errors.items?.[index]?.description}
                                helperText={errors.items?.[index]?.description?.message}
                                required
                              />
                            )}
                          />
                        </Grid>
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <Controller
                            name={`items.${index}.procedureCode`}
                            control={control}
                            render={({ field }) => (
                              <TextField
                                {...field}
                                label="Code"
                                fullWidth
                                size="small"
                                placeholder="CPT/ICD"
                              />
                            )}
                          />
                        </Grid>
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <Controller
                            name={`items.${index}.quantity`}
                            control={control}
                            render={({ field }) => (
                              <TextField
                                {...field}
                                label="Qty"
                                type="number"
                                fullWidth
                                size="small"
                                error={!!errors.items?.[index]?.quantity}
                              />
                            )}
                          />
                        </Grid>
                        <Grid size={{ xs: 6, sm: 2 }}>
                          <Controller
                            name={`items.${index}.unitPrice`}
                            control={control}
                            render={({ field }) => (
                              <TextField
                                {...field}
                                label="Price"
                                type="number"
                                fullWidth
                                size="small"
                                error={!!errors.items?.[index]?.unitPrice}
                              />
                            )}
                          />
                        </Grid>
                        <Grid size={{ xs: 5, sm: 1 }}>
                          <Typography variant="body2" fontWeight={600}>
                            ${(items[index]?.quantity * items[index]?.unitPrice || 0).toFixed(2)}
                          </Typography>
                        </Grid>
                        <Grid size={{ xs: 1, sm: 1 }}>
                          <IconButton
                            onClick={() => remove(index)}
                            disabled={fields.length === 1}
                            color="error"
                            size="small"
                            aria-label="Remove item"
                          >
                            <DeleteIcon />
                          </IconButton>
                        </Grid>
                      </Grid>
                    </Box>
                  ))}
                </Stack>
              </CardContent>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, lg: 4 }}>
            <Card sx={{ position: 'sticky', top: 24 }}>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 3 }}>
                  Summary
                </Typography>

                <Stack spacing={2}>
                  {}
                  <Controller
                    name="taxRate"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Tax Rate (%)"
                        type="number"
                        fullWidth
                        size="small"
                        slotProps={{ htmlInput: { min: 0, max: 100, step: 0.1 } }}
                      />
                    )}
                  />

                  {}
                  <Controller
                    name="discountAmount"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Discount ($)"
                        type="number"
                        fullWidth
                        size="small"
                        slotProps={{ htmlInput: { min: 0, step: 0.01 } }}
                      />
                    )}
                  />

                  <Divider />

                  {}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography>Subtotal</Typography>
                    <Typography fontWeight={500}>${subtotal.toFixed(2)}</Typography>
                  </Box>

                  {taxAmount > 0 && (
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Typography>Tax ({taxRate}%)</Typography>
                      <Typography>${taxAmount.toFixed(2)}</Typography>
                    </Box>
                  )}

                  {discountAmount > 0 && (
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Typography color="success.main">Discount</Typography>
                      <Typography color="success.main">-${discountAmount.toFixed(2)}</Typography>
                    </Box>
                  )}

                  <Divider />

                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="h4" fontWeight={700}>
                      Total
                    </Typography>
                    <Typography variant="h4" fontWeight={700} color="primary.main">
                      ${total.toFixed(2)}
                    </Typography>
                  </Box>

                  <Button
                    type="submit"
                    variant="contained"
                    size="large"
                    fullWidth
                    startIcon={<SaveIcon />}
                    disabled={isCreating}
                    sx={{ mt: 2, minHeight: 48 }}
                  >
                    {isCreating ? 'Creating...' : 'Create Invoice'}
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}
