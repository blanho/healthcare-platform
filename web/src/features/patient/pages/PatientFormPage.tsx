import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Card,
  CardContent,
  Box,
  Button,
  Grid,
  MenuItem,
  Stack,
  Typography,
  Alert,
  Divider,
} from '@mui/material';
import { Save as SaveIcon } from '@mui/icons-material';
import { PageHeader, FormField, LoadingSkeleton } from '@/components/shared';
import { usePatient, useCreatePatient, useUpdatePatient } from '../hooks/usePatient';
import { patientFormSchema, type PatientFormValues } from '../schemas';
import { GENDER_OPTIONS, BLOOD_TYPE_OPTIONS } from '../constants';
import type { BloodType } from '@/types';

export function PatientFormPage() {
  const { id } = useParams<{ id: string }>();
  const isEdit = !!id;
  const navigate = useNavigate();
  const { data: existing, isLoading } = usePatient(id ?? '');
  const createMutation = useCreatePatient();
  const updateMutation = useUpdatePatient();
  const mutation = isEdit ? updateMutation : createMutation;

  const { control, handleSubmit } = useForm<PatientFormValues, unknown, PatientFormValues>({
    resolver: zodResolver(patientFormSchema) as never,
    values:
      isEdit && existing
        ? {
            firstName: existing.firstName,
            middleName: existing.middleName ?? '',
            lastName: existing.lastName,
            dateOfBirth: existing.dateOfBirth,
            gender: existing.gender,
            bloodType: existing.bloodType ?? '',
            email: existing.email,
            phoneNumber: existing.phoneNumber,
            secondaryPhone: existing.secondaryPhone ?? '',
            street: existing.address?.street ?? '',
            city: existing.address?.city ?? '',
            state: existing.address?.state ?? '',
            zipCode: existing.address?.zipCode ?? '',
            country: existing.address?.country ?? '',
            insuranceProviderName: existing.insurance?.providerName ?? '',
            insurancePolicyNumber: existing.insurance?.policyNumber ?? '',
            emergencyName: existing.emergencyContact?.name ?? '',
            emergencyRelationship: existing.emergencyContact?.relationship ?? '',
            emergencyPhone: existing.emergencyContact?.phoneNumber ?? '',
          }
        : undefined,
  });

  if (isEdit && isLoading) return <LoadingSkeleton variant="form" />;

  const onSubmit = (values: PatientFormValues) => {
    const payload = {
      firstName: values.firstName,
      middleName: values.middleName || undefined,
      lastName: values.lastName,
      dateOfBirth: values.dateOfBirth,
      gender: values.gender,
      bloodType: (values.bloodType || undefined) as BloodType | undefined,
      email: values.email,
      phoneNumber: values.phoneNumber,
      secondaryPhone: values.secondaryPhone || undefined,
      address:
        values.street || values.city
          ? {
              street: values.street || undefined,
              city: values.city || undefined,
              state: values.state || undefined,
              zipCode: values.zipCode || undefined,
              country: values.country || undefined,
            }
          : undefined,
      insurance: values.insuranceProviderName
        ? {
            providerName: values.insuranceProviderName,
            policyNumber: values.insurancePolicyNumber || undefined,
          }
        : undefined,
      emergencyContact: values.emergencyName
        ? {
            name: values.emergencyName,
            relationship: values.emergencyRelationship || undefined,
            phoneNumber: values.emergencyPhone || undefined,
          }
        : undefined,
    };

    if (isEdit) {
      updateMutation.mutate(
        { id: id!, data: payload },
        { onSuccess: () => navigate(`/patients/${id}`) },
      );
    } else {
      createMutation.mutate(payload, {
        onSuccess: (created) => navigate(`/patients/${created.id}`),
      });
    }
  };

  return (
    <>
      <PageHeader
        title={isEdit ? 'Edit Patient' : 'New Patient'}
        breadcrumbs={[
          { label: 'Dashboard', href: '/' },
          { label: 'Patients', href: '/patients' },
          { label: isEdit ? 'Edit' : 'New' },
        ]}
      />

      {mutation.isError && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {(mutation.error as Error)?.message || 'Failed to save patient.'}
        </Alert>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Stack spacing={3}>
          {}
          <Card>
            <CardContent>
              <Typography variant="h4" sx={{ mb: 2 }}>
                Personal Information
              </Typography>
              <Divider sx={{ mb: 3 }} />
              <Grid container spacing={2.5}>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues>
                    name="firstName"
                    control={control}
                    label="First Name"
                    required
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues>
                    name="middleName"
                    control={control}
                    label="Middle Name"
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues>
                    name="lastName"
                    control={control}
                    label="Last Name"
                    required
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues>
                    name="dateOfBirth"
                    control={control}
                    label="Date of Birth"
                    type="date"
                    required
                    slotProps={{ inputLabel: { shrink: true } }}
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues>
                    name="gender"
                    control={control}
                    label="Gender"
                    select
                    required
                  >
                    {GENDER_OPTIONS.map(({ value, label }) => (
                      <MenuItem key={value} value={value}>
                        {label}
                      </MenuItem>
                    ))}
                  </FormField>
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues>
                    name="bloodType"
                    control={control}
                    label="Blood Type"
                    select
                  >
                    <MenuItem value="">Unknown</MenuItem>
                    {BLOOD_TYPE_OPTIONS.map(({ value, label }) => (
                      <MenuItem key={value} value={value}>
                        {label}
                      </MenuItem>
                    ))}
                  </FormField>
                </Grid>
              </Grid>
            </CardContent>
          </Card>

          {}
          <Card>
            <CardContent>
              <Typography variant="h4" sx={{ mb: 2 }}>
                Contact Information
              </Typography>
              <Divider sx={{ mb: 3 }} />
              <Grid container spacing={2.5}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <FormField<PatientFormValues>
                    name="email"
                    control={control}
                    label="Email"
                    type="email"
                    required
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 3 }}>
                  <FormField<PatientFormValues>
                    name="phoneNumber"
                    control={control}
                    label="Phone"
                    required
                  />
                </Grid>
                <Grid size={{ xs: 12, sm: 3 }}>
                  <FormField<PatientFormValues>
                    name="secondaryPhone"
                    control={control}
                    label="Secondary Phone"
                  />
                </Grid>
              </Grid>
            </CardContent>
          </Card>

          {}
          <Card>
            <CardContent>
              <Typography variant="h4" sx={{ mb: 2 }}>
                Address
              </Typography>
              <Divider sx={{ mb: 3 }} />
              <Grid container spacing={2.5}>
                <Grid size={{ xs: 12 }}>
                  <FormField<PatientFormValues> name="street" control={control} label="Street" />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues> name="city" control={control} label="City" />
                </Grid>
                <Grid size={{ xs: 12, sm: 4 }}>
                  <FormField<PatientFormValues> name="state" control={control} label="State" />
                </Grid>
                <Grid size={{ xs: 12, sm: 2 }}>
                  <FormField<PatientFormValues> name="zipCode" control={control} label="Zip Code" />
                </Grid>
                <Grid size={{ xs: 12, sm: 2 }}>
                  <FormField<PatientFormValues> name="country" control={control} label="Country" />
                </Grid>
              </Grid>
            </CardContent>
          </Card>

          {}
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
            <Button onClick={() => navigate(-1)} sx={{ cursor: 'pointer' }}>
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              startIcon={<SaveIcon />}
              disabled={mutation.isPending}
              sx={{ cursor: 'pointer' }}
            >
              {mutation.isPending ? 'Saving…' : isEdit ? 'Update Patient' : 'Create Patient'}
            </Button>
          </Box>
        </Stack>
      </form>
    </>
  );
}
