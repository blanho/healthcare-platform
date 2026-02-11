import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Avatar,
  Divider,
  Button,
  Chip,
  Stack,
  Tabs,
  Tab,
} from '@mui/material';
import {
  Edit as EditIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  CalendarMonth as CalendarIcon,
  Person as PersonIcon,
  Event as AppointmentIcon,
  MedicalServices as MedicalIcon,
  Receipt as BillingIcon,
  Folder as DocumentsIcon,
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { PageHeader, StatusChip, LoadingSkeleton, DataTable } from '@/components/shared';
import { RbacGuard } from '@/components/auth';
import { usePatient } from '../hooks/usePatient';
import { usePatientAppointments } from '@/features/appointment/hooks/useAppointment';
import { usePatientRecords } from '@/features/medical-record/hooks/useMedicalRecord';
import { usePatientInvoices } from '@/features/billing/hooks/useBilling';
import { AppointmentStatusChip, AppointmentTypeChip } from '@/features/appointment/components';
import { RecordStatusChip, RecordTypeChip } from '@/features/medical-record/components';
import { InvoiceStatusChip, CurrencyDisplay } from '@/features/billing/components';
import { PatientDocumentsList } from '../components';
import type {
  AppointmentStatus,
  AppointmentType,
  RecordStatus,
  RecordType,
  InvoiceStatus,
} from '@/types';

function InfoRow({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <Box sx={{ display: 'flex', py: 1 }}>
      <Typography variant="body2" color="text.secondary" sx={{ width: 180, flexShrink: 0 }}>
        {label}
      </Typography>
      <Typography variant="body2">{value ?? '—'}</Typography>
    </Box>
  );
}

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel({ children, value, index }: TabPanelProps) {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`patient-tabpanel-${index}`}
      aria-labelledby={`patient-tab-${index}`}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

export function PatientDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState(0);

  const { data: patient, isLoading } = usePatient(id!);
  const { data: appointmentsData, isLoading: appointmentsLoading } = usePatientAppointments(id!, {
    page: 0,
    size: 10,
  });
  const { data: recordsData, isLoading: recordsLoading } = usePatientRecords(id!, {
    page: 0,
    size: 10,
  });
  const { data: invoicesData, isLoading: invoicesLoading } = usePatientInvoices(id!, {
    page: 0,
    size: 10,
  });

  if (isLoading) return <LoadingSkeleton variant="detail" />;
  if (!patient) return null;

  const initials = `${patient.firstName[0]}${patient.lastName[0]}`.toUpperCase();
  const appointments = appointmentsData?.content || [];
  const records = recordsData?.content || [];
  const invoices = invoicesData?.content || [];

  const appointmentColumns = [
    {
      field: 'appointmentDateTime',
      headerName: 'Date & Time',
      width: 180,
      renderCell: (params: { row: { appointmentDateTime: string } }) =>
        format(parseISO(params.row.appointmentDateTime), 'MMM d, yyyy h:mm a'),
    },
    {
      field: 'type',
      headerName: 'Type',
      width: 150,
      renderCell: (params: { row: { type: string } }) => (
        <AppointmentTypeChip type={params.row.type as AppointmentType} />
      ),
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 130,
      renderCell: (params: { row: { status: string } }) => (
        <AppointmentStatusChip status={params.row.status as AppointmentStatus} />
      ),
    },
    { field: 'providerName', headerName: 'Provider', width: 180 },
    { field: 'locationName', headerName: 'Location', width: 150 },
  ];

  const recordColumns = [
    {
      field: 'recordDate',
      headerName: 'Date',
      width: 130,
      renderCell: (params: { row: { recordDate: string } }) =>
        format(parseISO(params.row.recordDate), 'MMM d, yyyy'),
    },
    {
      field: 'recordType',
      headerName: 'Type',
      width: 150,
      renderCell: (params: { row: { recordType: string } }) => (
        <RecordTypeChip type={params.row.recordType as RecordType} />
      ),
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 130,
      renderCell: (params: { row: { status: string } }) => (
        <RecordStatusChip status={params.row.status as RecordStatus} />
      ),
    },
    { field: 'providerName', headerName: 'Provider', width: 180 },
    { field: 'chiefComplaint', headerName: 'Chief Complaint', width: 200 },
  ];

  const invoiceColumns = [
    { field: 'invoiceNumber', headerName: 'Invoice #', width: 140 },
    {
      field: 'invoiceDate',
      headerName: 'Date',
      width: 120,
      renderCell: (params: { row: { invoiceDate: string } }) =>
        format(parseISO(params.row.invoiceDate), 'MMM d, yyyy'),
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 120,
      renderCell: (params: { row: { status: string } }) => (
        <InvoiceStatusChip status={params.row.status as InvoiceStatus} />
      ),
    },
    {
      field: 'totalAmount',
      headerName: 'Total',
      width: 120,
      renderCell: (params: { row: { totalAmount: number } }) => (
        <CurrencyDisplay amount={params.row.totalAmount} />
      ),
    },
    {
      field: 'balanceDue',
      headerName: 'Balance',
      width: 120,
      renderCell: (params: { row: { balanceDue: number } }) => (
        <CurrencyDisplay
          amount={params.row.balanceDue}
          color={params.row.balanceDue > 0 ? 'error' : 'success'}
        />
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title={patient.fullName}
        breadcrumbs={[
          { label: 'Dashboard', href: '/app' },
          { label: 'Patients', href: '/app/patients' },
          { label: patient.fullName },
        ]}
        action={
          <RbacGuard permission="patient:write">
            <Button
              variant="outlined"
              startIcon={<EditIcon />}
              onClick={() => navigate(`/app/patients/${id}/edit`)}
              sx={{ cursor: 'pointer', minHeight: 44 }}
            >
              Edit
            </Button>
          </RbacGuard>
        }
      />

      {}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
            <Avatar sx={{ width: 72, height: 72, bgcolor: 'primary.main', fontSize: '1.5rem' }}>
              {initials}
            </Avatar>
            <Box sx={{ flex: 1 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 0.5 }}>
                <Typography variant="h2">{patient.fullName}</Typography>
                <StatusChip status={patient.status} />
                {patient.isMinor && (
                  <Chip label="Minor" size="small" color="warning" variant="outlined" />
                )}
              </Box>
              <Stack direction="row" spacing={3} sx={{ mt: 1 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <EmailIcon fontSize="small" color="action" />
                  <Typography variant="body2" color="text.secondary">
                    {patient.email}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <PhoneIcon fontSize="small" color="action" />
                  <Typography variant="body2" color="text.secondary">
                    {patient.phoneNumber}
                  </Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <CalendarIcon fontSize="small" color="action" />
                  <Typography variant="body2" color="text.secondary">
                    MRN: {patient.medicalRecordNumber}
                  </Typography>
                </Box>
              </Stack>
            </Box>
          </Box>
        </CardContent>
      </Card>

      {}
      <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
        <Tabs
          value={activeTab}
          onChange={(_, v) => setActiveTab(v)}
          aria-label="Patient information tabs"
        >
          <Tab icon={<PersonIcon />} iconPosition="start" label="Overview" sx={{ minHeight: 48 }} />
          <Tab
            icon={<AppointmentIcon />}
            iconPosition="start"
            label={`Appointments (${appointments.length})`}
            sx={{ minHeight: 48 }}
          />
          <Tab
            icon={<MedicalIcon />}
            iconPosition="start"
            label={`Medical Records (${records.length})`}
            sx={{ minHeight: 48 }}
          />
          <Tab
            icon={<BillingIcon />}
            iconPosition="start"
            label={`Billing (${invoices.length})`}
            sx={{ minHeight: 48 }}
          />
          <Tab
            icon={<DocumentsIcon />}
            iconPosition="start"
            label="Documents"
            sx={{ minHeight: 48 }}
          />
        </Tabs>
      </Box>

      {}
      <TabPanel value={activeTab} index={0}>
        <Grid container spacing={3}>
          {}
          <Grid size={{ xs: 12, md: 6 }}>
            <Card>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 2 }}>
                  Personal Information
                </Typography>
                <Divider sx={{ mb: 1 }} />
                <InfoRow label="Date of Birth" value={patient.dateOfBirth} />
                <InfoRow label="Age" value={`${patient.age} years`} />
                <InfoRow label="Gender" value={patient.gender?.replace(/_/g, ' ')} />
                <InfoRow label="Blood Type" value={patient.bloodType?.replace(/_/g, ' ')} />
                <InfoRow label="Secondary Phone" value={patient.secondaryPhone} />
              </CardContent>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, md: 6 }}>
            <Card>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 2 }}>
                  Address
                </Typography>
                <Divider sx={{ mb: 1 }} />
                {patient.address ? (
                  <>
                    <InfoRow label="Street" value={patient.address.street} />
                    <InfoRow label="City" value={patient.address.city} />
                    <InfoRow label="State" value={patient.address.state} />
                    <InfoRow label="Zip Code" value={patient.address.zipCode} />
                    <InfoRow label="Country" value={patient.address.country} />
                  </>
                ) : (
                  <Typography variant="body2" color="text.secondary">
                    No address on file
                  </Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, md: 6 }}>
            <Card>
              <CardContent>
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    mb: 2,
                  }}
                >
                  <Typography variant="h4">Insurance</Typography>
                  {patient.hasActiveInsurance && (
                    <Chip label="Active" size="small" color="success" variant="outlined" />
                  )}
                </Box>
                <Divider sx={{ mb: 1 }} />
                {patient.insurance ? (
                  <>
                    <InfoRow label="Provider" value={patient.insurance.providerName} />
                    <InfoRow label="Policy #" value={patient.insurance.policyNumber} />
                    <InfoRow label="Group #" value={patient.insurance.groupNumber} />
                    <InfoRow label="Holder" value={patient.insurance.holderName} />
                    <InfoRow label="Effective" value={patient.insurance.effectiveDate} />
                    <InfoRow label="Expiration" value={patient.insurance.expirationDate} />
                  </>
                ) : (
                  <Typography variant="body2" color="text.secondary">
                    No insurance on file
                  </Typography>
                )}
              </CardContent>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, md: 6 }}>
            <Card>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 2 }}>
                  Emergency Contact
                </Typography>
                <Divider sx={{ mb: 1 }} />
                {patient.emergencyContact ? (
                  <>
                    <InfoRow label="Name" value={patient.emergencyContact.name} />
                    <InfoRow label="Relationship" value={patient.emergencyContact.relationship} />
                    <InfoRow label="Phone" value={patient.emergencyContact.phoneNumber} />
                    <InfoRow label="Email" value={patient.emergencyContact.email} />
                  </>
                ) : (
                  <Typography variant="body2" color="text.secondary">
                    No emergency contact on file
                  </Typography>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </TabPanel>

      {}
      <TabPanel value={activeTab} index={1}>
        <Card>
          <CardContent>
            <Box
              sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}
            >
              <Typography variant="h4">Appointments</Typography>
              <Button
                variant="contained"
                size="small"
                onClick={() => navigate(`/app/appointments/new?patientId=${id}`)}
                sx={{ minHeight: 36 }}
              >
                New Appointment
              </Button>
            </Box>
            <DataTable
              rows={appointments}
              columns={appointmentColumns}
              isLoading={appointmentsLoading}
              onRowClick={(row) => navigate(`/app/appointments/${row.id}`)}
              getRowId={(row) => row.id}
            />
          </CardContent>
        </Card>
      </TabPanel>

      {}
      <TabPanel value={activeTab} index={2}>
        <Card>
          <CardContent>
            <Box
              sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}
            >
              <Typography variant="h4">Medical Records</Typography>
              <Button
                variant="contained"
                size="small"
                onClick={() => navigate(`/app/medical-records/new?patientId=${id}`)}
                sx={{ minHeight: 36 }}
              >
                New Record
              </Button>
            </Box>
            <DataTable
              rows={records}
              columns={recordColumns}
              isLoading={recordsLoading}
              onRowClick={(row) => navigate(`/app/medical-records/${row.id}`)}
              getRowId={(row) => row.id}
            />
          </CardContent>
        </Card>
      </TabPanel>

      {}
      <TabPanel value={activeTab} index={3}>
        <Card>
          <CardContent>
            <Box
              sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}
            >
              <Typography variant="h4">Billing</Typography>
              <Button
                variant="contained"
                size="small"
                onClick={() => navigate(`/app/billing/new?patientId=${id}`)}
                sx={{ minHeight: 36 }}
              >
                New Invoice
              </Button>
            </Box>
            <DataTable
              rows={invoices}
              columns={invoiceColumns}
              isLoading={invoicesLoading}
              onRowClick={(row) => navigate(`/app/billing/invoices/${row.id}`)}
              getRowId={(row) => row.id}
            />
          </CardContent>
        </Card>
      </TabPanel>

      {}
      <TabPanel value={activeTab} index={4}>
        <PatientDocumentsList patientId={id!} />
      </TabPanel>
    </>
  );
}
