import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Button,
  Stack,
  Avatar,
  Skeleton,
  Alert,
  Chip,
} from '@mui/material';
import {
  Edit as EditIcon,
  CheckCircle as FinalizeIcon,
  History as AmendIcon,
  Person as PatientIcon,
  LocalHospital as ProviderIcon,
  CalendarToday as DateIcon,
  AttachFile as AttachmentIcon,
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { PageHeader, ConfirmDialog } from '@/components/shared';
import { useState } from 'react';
import { useMedicalRecord, useFinalizeMedicalRecord } from '../hooks/useMedicalRecord';
import {
  RecordStatusChip,
  RecordTypeChip,
  VitalsCard,
  SoapNoteCard,
  DiagnosisList,
} from '../components';

export function MedicalRecordDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: record, isLoading, error } = useMedicalRecord(id!);
  const finalizeMutation = useFinalizeMedicalRecord();

  const [finalizeDialogOpen, setFinalizeDialogOpen] = useState(false);

  if (error) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        Failed to load medical record. The record may not exist or you don't have permission to view
        it.
      </Alert>
    );
  }

  const canFinalize = record?.status === 'DRAFT';
  const canAmend = record?.status === 'FINALIZED';
  const canEdit = record?.status === 'DRAFT';

  const handleFinalize = () => {
    if (id) {
      finalizeMutation.mutate(id, {
        onSuccess: () => setFinalizeDialogOpen(false),
      });
    }
  };

  return (
    <>
      <PageHeader
        title={isLoading ? 'Loading...' : `Medical Record ${record?.recordNumber}`}
        subtitle={isLoading ? '' : `${record?.chiefComplaint || 'No chief complaint'}`}
        breadcrumbs={[
          { label: 'Dashboard', href: '/app' },
          { label: 'Medical Records', href: '/app/medical-records' },
          { label: record?.recordNumber || 'Detail' },
        ]}
        action={
          <Stack direction="row" spacing={1}>
            {canEdit && (
              <Button
                variant="outlined"
                startIcon={<EditIcon />}
                onClick={() => navigate(`/app/medical-records/${id}/edit`)}
                sx={{ cursor: 'pointer' }}
              >
                Edit
              </Button>
            )}
            {canFinalize && (
              <Button
                variant="contained"
                color="success"
                startIcon={<FinalizeIcon />}
                onClick={() => setFinalizeDialogOpen(true)}
                sx={{ cursor: 'pointer' }}
              >
                Finalize
              </Button>
            )}
            {canAmend && (
              <Button
                variant="outlined"
                startIcon={<AmendIcon />}
                onClick={() => navigate(`/app/medical-records/${id}/amend`)}
                sx={{ cursor: 'pointer' }}
              >
                Amend
              </Button>
            )}
          </Stack>
        }
      />

      <Grid container spacing={3}>
        {/* Main Content */}
        <Grid size={{ xs: 12, lg: 8 }}>
          {/* Header Card */}
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
                  {isLoading ? (
                    <Skeleton width={200} height={32} />
                  ) : (
                    <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
                      <RecordStatusChip status={record!.status} size="medium" showIcon />
                      <RecordTypeChip type={record!.recordType} />
                    </Stack>
                  )}
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <DateIcon fontSize="small" color="action" />
                  {isLoading ? (
                    <Skeleton width={100} />
                  ) : (
                    <Typography variant="body2">
                      {format(parseISO(record!.recordDate), 'MMMM d, yyyy')}
                    </Typography>
                  )}
                </Box>
              </Box>

              {/* Chief Complaint */}
              <Box sx={{ bgcolor: 'grey.50', p: 2, borderRadius: 1, mb: 2 }}>
                <Typography variant="caption" color="text.secondary" display="block">
                  Chief Complaint
                </Typography>
                {isLoading ? (
                  <Skeleton width="60%" />
                ) : (
                  <Typography variant="body1" fontWeight={500}>
                    {record?.chiefComplaint || 'No chief complaint recorded'}
                  </Typography>
                )}
              </Box>

              {/* Notes */}
              {record?.notes && (
                <Box>
                  <Typography variant="caption" color="text.secondary" display="block">
                    Notes
                  </Typography>
                  <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                    {record.notes}
                  </Typography>
                </Box>
              )}

              {/* Attachments */}
              {record?.attachmentsCount && record.attachmentsCount > 0 && (
                <Box sx={{ mt: 2, display: 'flex', alignItems: 'center', gap: 1 }}>
                  <AttachmentIcon fontSize="small" color="action" />
                  <Chip
                    label={`${record.attachmentsCount} attachment${record.attachmentsCount > 1 ? 's' : ''}`}
                    size="small"
                    variant="outlined"
                  />
                </Box>
              )}

              {/* Finalization Info */}
              {record?.finalizedAt && (
                <Box sx={{ mt: 2, p: 1.5, bgcolor: 'success.lighter', borderRadius: 1 }}>
                  <Typography variant="caption" color="success.main">
                    Finalized on {format(parseISO(record.finalizedAt), 'MMMM d, yyyy h:mm a')}
                  </Typography>
                </Box>
              )}
            </CardContent>
          </Card>

          {/* Vital Signs */}
          <Box sx={{ mb: 3 }}>
            {isLoading ? (
              <Skeleton variant="rectangular" height={200} sx={{ borderRadius: 1 }} />
            ) : (
              <VitalsCard vitals={record?.vitalSigns || null} />
            )}
          </Box>

          {/* SOAP Note */}
          <Box sx={{ mb: 3 }}>
            {isLoading ? (
              <Skeleton variant="rectangular" height={300} sx={{ borderRadius: 1 }} />
            ) : (
              <SoapNoteCard soapNote={record?.soapNote || null} />
            )}
          </Box>

          {/* Diagnoses */}
          {isLoading ? (
            <Skeleton variant="rectangular" height={200} sx={{ borderRadius: 1 }} />
          ) : (
            <DiagnosisList diagnoses={record?.diagnoses || []} />
          )}
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
                <PatientIcon fontSize="small" /> Patient
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
                        Patient #{record!.patientId.slice(0, 8)}
                      </Typography>
                      <Button
                        size="small"
                        onClick={() => navigate(`/app/patients/${record!.patientId}`)}
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
          <Card sx={{ mb: 3 }}>
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
                        Provider #{record!.providerId.slice(0, 8)}
                      </Typography>
                      <Button
                        size="small"
                        onClick={() => navigate(`/app/providers/${record!.providerId}`)}
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

          {/* Appointment Link */}
          {record?.appointmentId && (
            <Card>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 2 }}>
                  Related Appointment
                </Typography>
                <Button
                  variant="outlined"
                  fullWidth
                  onClick={() => navigate(`/app/appointments/${record.appointmentId}`)}
                  sx={{ cursor: 'pointer' }}
                >
                  View Appointment
                </Button>
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>

      {/* Finalize Dialog */}
      <ConfirmDialog
        open={finalizeDialogOpen}
        title="Finalize Medical Record"
        message="Are you sure you want to finalize this record? Once finalized, the record cannot be edited directly and will require an amendment for any changes."
        confirmLabel="Finalize"
        confirmColor="primary"
        onConfirm={handleFinalize}
        onCancel={() => setFinalizeDialogOpen(false)}
        loading={finalizeMutation.isPending}
      />
    </>
  );
}
