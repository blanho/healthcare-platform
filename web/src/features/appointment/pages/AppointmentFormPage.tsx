import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Box, Card, CardContent, Grid, Typography, Button, Stack, Alert } from '@mui/material';
import { DatePicker, TimePicker } from '@mui/x-date-pickers';
import { format } from 'date-fns';
import { PageHeader, FormFieldWrapper } from '@/components/shared';
import { useScheduleAppointment } from '../hooks/useAppointment';
import { appointmentFormSchema, type AppointmentFormValues } from '../schemas';
import { APPOINTMENT_TYPE_OPTIONS, APPOINTMENT_DURATION_OPTIONS } from '../constants';

export function AppointmentFormPage() {
  const navigate = useNavigate();
  const scheduleMutation = useScheduleAppointment();

  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<AppointmentFormValues>({
    resolver: zodResolver(appointmentFormSchema),
    defaultValues: {
      durationMinutes: 30,
      appointmentType: 'FOLLOW_UP',
    },
  });

  const onSubmit = (data: AppointmentFormValues) => {
    scheduleMutation.mutate(
      {
        patientId: data.patientId,
        providerId: data.providerId,
        scheduledDate: format(data.scheduledDate, 'yyyy-MM-dd'),
        startTime: format(data.startTime, 'HH:mm'),
        durationMinutes: data.durationMinutes,
        appointmentType: data.appointmentType,
        reasonForVisit: data.reasonForVisit,
        notes: data.notes,
      },
      {
        onSuccess: (result) => {
          navigate(`/app/appointments/${result.id}`);
        },
      },
    );
  };

  return (
    <>
      <PageHeader
        title="Schedule Appointment"
        subtitle="Create a new appointment for a patient"
        breadcrumbs={[
          { label: 'Dashboard', href: '/app' },
          { label: 'Appointments', href: '/app/appointments' },
          { label: 'New Appointment' },
        ]}
      />

      <form onSubmit={handleSubmit(onSubmit)}>
        <Grid container spacing={3}>
          <Grid size={{ xs: 12, lg: 8 }}>
            <Card>
              <CardContent>
                <Typography variant="h3" sx={{ mb: 3 }}>
                  Appointment Details
                </Typography>

                {scheduleMutation.error && (
                  <Alert severity="error" sx={{ mb: 3 }}>
                    Failed to schedule appointment. Please check the details and try again.
                  </Alert>
                )}

                <Grid container spacing={3}>
                  {}
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="patientId"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper label="Patient" required error={errors.patientId?.message}>
                          <Box
                            component="input"
                            type="text"
                            placeholder="Enter Patient ID"
                            {...field}
                            style={{
                              width: '100%',
                              padding: '14px',
                              border: '1px solid #ccc',
                              borderRadius: '4px',
                              fontSize: '16px',
                            }}
                          />
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="providerId"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper label="Provider" required error={errors.providerId?.message}>
                          <Box
                            component="input"
                            type="text"
                            placeholder="Enter Provider ID"
                            {...field}
                            style={{
                              width: '100%',
                              padding: '14px',
                              border: '1px solid #ccc',
                              borderRadius: '4px',
                              fontSize: '16px',
                            }}
                          />
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="scheduledDate"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper label="Date" required error={errors.scheduledDate?.message}>
                          <DatePicker
                            value={field.value || null}
                            onChange={field.onChange}
                            disablePast
                            slotProps={{
                              textField: {
                                fullWidth: true,
                                error: !!errors.scheduledDate,
                              },
                            }}
                          />
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="startTime"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper label="Start Time" required error={errors.startTime?.message}>
                          <TimePicker
                            value={field.value || null}
                            onChange={field.onChange}
                            slotProps={{
                              textField: {
                                fullWidth: true,
                                error: !!errors.startTime,
                              },
                            }}
                          />
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="durationMinutes"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper label="Duration" error={errors.durationMinutes?.message}>
                          <Box
                            component="select"
                            {...field}
                            onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
                              field.onChange(Number(e.target.value))
                            }
                            style={{
                              width: '100%',
                              padding: '14px',
                              border: '1px solid #ccc',
                              borderRadius: '4px',
                              fontSize: '16px',
                            }}
                          >
                            {APPOINTMENT_DURATION_OPTIONS.map((d) => (
                              <option key={d.value} value={d.value}>
                                {d.label}
                              </option>
                            ))}
                          </Box>
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={{ xs: 12, sm: 6 }}>
                    <Controller
                      name="appointmentType"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper
                          label="Appointment Type"
                          required
                          error={errors.appointmentType?.message}
                        >
                          <Box
                            component="select"
                            {...field}
                            style={{
                              width: '100%',
                              padding: '14px',
                              border: '1px solid #ccc',
                              borderRadius: '4px',
                              fontSize: '16px',
                            }}
                          >
                            {APPOINTMENT_TYPE_OPTIONS.map((t) => (
                              <option key={t.value} value={t.value}>
                                {t.label}
                              </option>
                            ))}
                          </Box>
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={12}>
                    <Controller
                      name="reasonForVisit"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper label="Reason for Visit" error={errors.reasonForVisit?.message}>
                          <Box
                            component="textarea"
                            rows={3}
                            placeholder="Describe the reason for this visit..."
                            {...field}
                            style={{
                              width: '100%',
                              padding: '14px',
                              border: '1px solid #ccc',
                              borderRadius: '4px',
                              fontSize: '16px',
                              fontFamily: 'inherit',
                              resize: 'vertical',
                            }}
                          />
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>

                  {}
                  <Grid size={12}>
                    <Controller
                      name="notes"
                      control={control}
                      render={({ field }) => (
                        <FormFieldWrapper label="Internal Notes" error={errors.notes?.message}>
                          <Box
                            component="textarea"
                            rows={2}
                            placeholder="Optional notes for staff..."
                            {...field}
                            style={{
                              width: '100%',
                              padding: '14px',
                              border: '1px solid #ccc',
                              borderRadius: '4px',
                              fontSize: '16px',
                              fontFamily: 'inherit',
                              resize: 'vertical',
                            }}
                          />
                        </FormFieldWrapper>
                      )}
                    />
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, lg: 4 }}>
            <Card>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 2 }}>
                  Actions
                </Typography>
                <Stack spacing={2}>
                  <Button
                    type="submit"
                    variant="contained"
                    size="large"
                    fullWidth
                    disabled={isSubmitting || scheduleMutation.isPending}
                    sx={{ cursor: 'pointer' }}
                  >
                    {scheduleMutation.isPending ? 'Scheduling...' : 'Schedule Appointment'}
                  </Button>
                  <Button
                    variant="outlined"
                    size="large"
                    fullWidth
                    onClick={() => navigate('/app/appointments')}
                    sx={{ cursor: 'pointer' }}
                  >
                    Cancel
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </form>
    </>
  );
}
