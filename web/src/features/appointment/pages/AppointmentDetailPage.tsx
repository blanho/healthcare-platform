import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Divider,
  Button,
  Stack,
  Avatar,
  Skeleton,
  Alert,
} from '@mui/material';
import {
  Delete as DeleteIcon,
  CheckCircle as CheckInIcon,
  Done as CompleteIcon,
  Schedule as RescheduleIcon,
  Person as PersonIcon,
  LocalHospital as ProviderIcon,
  AccessTime as TimeIcon,
  CalendarToday as DateIcon,
  Notes as NotesIcon,
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { PageHeader, ConfirmDialog } from '@/components/shared';
import { useState } from 'react';
import {
  useAppointment,
  useCancelAppointment,
  useConfirmAppointment,
  useCompleteAppointment,
} from '../hooks/useAppointment';
import { AppointmentStatusChip, AppointmentTypeChip, TimeSlot } from '../components';

export function AppointmentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: appointment, isLoading, error } = useAppointment(id!);
  const cancelMutation = useCancelAppointment();
  const confirmMutation = useConfirmAppointment();
  const completeMutation = useCompleteAppointment();

  const [cancelDialogOpen, setCancelDialogOpen] = useState(false);

  if (error) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        Failed to load appointment details. The appointment may not exist or you don't have
        permission to view it.
      </Alert>
    );
  }

  const canConfirm = appointment?.status === 'SCHEDULED';
  const canComplete = appointment?.status === 'IN_PROGRESS';
  const canReschedule = ['SCHEDULED', 'CONFIRMED'].includes(appointment?.status || '');
  const canCancel = ['SCHEDULED', 'CONFIRMED', 'CHECKED_IN'].includes(appointment?.status || '');

  const handleConfirm = () => {
    if (id) {
      confirmMutation.mutate(id);
    }
  };

  const handleComplete = () => {
    if (id) {
      completeMutation.mutate({ id });
    }
  };

  const handleCancel = () => {
    if (id) {
      cancelMutation.mutate(
        { id, data: { reason: 'Cancelled by staff', cancelledByPatient: false } },
        { onSuccess: () => setCancelDialogOpen(false) },
      );
    }
  };

  return (
    <>
      <PageHeader
        title={isLoading ? 'Loading...' : `Appointment ${appointment?.appointmentNumber}`}
        subtitle={
          isLoading
            ? ''
            : `Scheduled for ${format(parseISO(appointment!.scheduledDate), 'EEEE, MMMM d, yyyy')}`
        }
        breadcrumbs={[
          { label: 'Dashboard', href: '/app' },
          { label: 'Appointments', href: '/app/appointments' },
          { label: appointment?.appointmentNumber || 'Detail' },
        ]}
        action={
          <Stack direction="row" spacing={1}>
            {canConfirm && (
              <Button
                variant="outlined"
                color="success"
                startIcon={<CheckInIcon />}
                onClick={handleConfirm}
                disabled={confirmMutation.isPending}
                sx={{ cursor: 'pointer' }}
              >
                Confirm
              </Button>
            )}
            {canComplete && (
              <Button
                variant="contained"
                color="success"
                startIcon={<CompleteIcon />}
                onClick={handleComplete}
                disabled={completeMutation.isPending}
                sx={{ cursor: 'pointer' }}
              >
                Complete
              </Button>
            )}
            {canReschedule && (
              <Button
                variant="outlined"
                startIcon={<RescheduleIcon />}
                onClick={() => navigate(`/app/appointments/${id}/reschedule`)}
                sx={{ cursor: 'pointer' }}
              >
                Reschedule
              </Button>
            )}
            {canCancel && (
              <Button
                variant="outlined"
                color="error"
                startIcon={<DeleteIcon />}
                onClick={() => setCancelDialogOpen(true)}
                sx={{ cursor: 'pointer' }}
              >
                Cancel
              </Button>
            )}
          </Stack>
        }
      />

      <Grid container spacing={3}>
        {/* Main Info */}
        <Grid size={{ xs: 12, lg: 8 }}>
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
                <Typography variant="h3">Appointment Details</Typography>
                {isLoading ? (
                  <Skeleton width={100} />
                ) : (
                  <Stack direction="row" spacing={1}>
                    <AppointmentStatusChip status={appointment!.status} size="medium" />
                    <AppointmentTypeChip type={appointment!.appointmentType} />
                  </Stack>
                )}
              </Box>

              <Grid container spacing={3}>
                {/* Date & Time */}
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
                    <Avatar sx={{ bgcolor: 'primary.light', color: 'primary.main' }}>
                      <DateIcon />
                    </Avatar>
                    <Box>
                      <Typography variant="caption" color="text.secondary">
                        Date
                      </Typography>
                      {isLoading ? (
                        <Skeleton width={120} />
                      ) : (
                        <Typography variant="body1" fontWeight={500}>
                          {format(parseISO(appointment!.scheduledDate), 'EEEE, MMM d, yyyy')}
                        </Typography>
                      )}
                    </Box>
                  </Box>
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
                    <Avatar sx={{ bgcolor: 'success.light', color: 'success.main' }}>
                      <TimeIcon />
                    </Avatar>
                    <Box>
                      <Typography variant="caption" color="text.secondary">
                        Time
                      </Typography>
                      {isLoading ? (
                        <Skeleton width={100} />
                      ) : (
                        <TimeSlot
                          startTime={appointment!.startTime}
                          endTime={appointment!.endTime}
                        />
                      )}
                    </Box>
                  </Box>
                </Grid>

                {/* Duration */}
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="caption" color="text.secondary" display="block">
                    Duration
                  </Typography>
                  {isLoading ? (
                    <Skeleton width={80} />
                  ) : (
                    <Typography variant="body1">{appointment!.durationMinutes} minutes</Typography>
                  )}
                </Grid>
              </Grid>

              <Divider sx={{ my: 3 }} />

              {/* Reason & Notes */}
              <Typography
                variant="h4"
                sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}
              >
                <NotesIcon fontSize="small" /> Reason & Notes
              </Typography>

              <Box sx={{ bgcolor: 'grey.50', p: 2, borderRadius: 1, mb: 2 }}>
                <Typography variant="caption" color="text.secondary" display="block">
                  Reason for Visit
                </Typography>
                {isLoading ? (
                  <Skeleton width="60%" />
                ) : (
                  <Typography variant="body1">
                    {appointment!.reasonForVisit || 'No reason provided'}
                  </Typography>
                )}
              </Box>

              {appointment?.notes && (
                <Box sx={{ bgcolor: 'grey.50', p: 2, borderRadius: 1 }}>
                  <Typography variant="caption" color="text.secondary" display="block">
                    Notes
                  </Typography>
                  <Typography variant="body1">{appointment.notes}</Typography>
                </Box>
              )}

              {/* Cancellation Info */}
              {appointment?.cancellation && (
                <>
                  <Divider sx={{ my: 3 }} />
                  <Alert severity="error">
                    <Typography variant="subtitle2" fontWeight={600}>
                      Cancelled
                    </Typography>
                    <Typography variant="body2">
                      Reason: {appointment.cancellation.reason}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {format(parseISO(appointment.cancellation.cancelledAt), 'MMM d, yyyy h:mm a')}{' '}
                      • {appointment.cancellation.cancelledByPatient ? 'By Patient' : 'By Staff'}
                    </Typography>
                  </Alert>
                </>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Sidebar */}
        <Grid size={{ xs: 12, lg: 4 }}>
          {/* Patient Card */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography
                variant="h4"
                sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}
              >
                <PersonIcon fontSize="small" /> Patient
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Avatar
                  sx={{ width: 48, height: 48, bgcolor: 'primary.light', color: 'primary.main' }}
                >
                  PA
                </Avatar>
                <Box>
                  {isLoading ? (
                    <Skeleton width={120} />
                  ) : (
                    <>
                      <Typography variant="body1" fontWeight={500}>
                        Patient #{appointment!.patientId.slice(0, 8)}
                      </Typography>
                      <Button
                        size="small"
                        onClick={() => navigate(`/app/patients/${appointment!.patientId}`)}
                        sx={{ p: 0, minWidth: 0, textTransform: 'none', cursor: 'pointer' }}
                      >
                        View Patient
                      </Button>
                    </>
                  )}
                </Box>
              </Box>
            </CardContent>
          </Card>

          {/* Provider Card */}
          <Card>
            <CardContent>
              <Typography
                variant="h4"
                sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}
              >
                <ProviderIcon fontSize="small" /> Provider
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Avatar
                  sx={{
                    width: 48,
                    height: 48,
                    bgcolor: 'secondary.light',
                    color: 'secondary.main',
                  }}
                >
                  DR
                </Avatar>
                <Box>
                  {isLoading ? (
                    <Skeleton width={120} />
                  ) : (
                    <>
                      <Typography variant="body1" fontWeight={500}>
                        Provider #{appointment!.providerId.slice(0, 8)}
                      </Typography>
                      <Button
                        size="small"
                        onClick={() => navigate(`/app/providers/${appointment!.providerId}`)}
                        sx={{ p: 0, minWidth: 0, textTransform: 'none', cursor: 'pointer' }}
                      >
                        View Provider
                      </Button>
                    </>
                  )}
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Cancel Dialog */}
      <ConfirmDialog
        open={cancelDialogOpen}
        title="Cancel Appointment"
        message="Are you sure you want to cancel this appointment? This action cannot be undone."
        confirmLabel="Cancel Appointment"
        confirmColor="error"
        onConfirm={handleCancel}
        onCancel={() => setCancelDialogOpen(false)}
        loading={cancelMutation.isPending}
      />
    </>
  );
}
