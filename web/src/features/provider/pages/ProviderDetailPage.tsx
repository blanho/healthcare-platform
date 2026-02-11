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
  Chip,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import {
  Edit as EditIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  Badge as BadgeIcon,
  School as SchoolIcon,
  AttachMoney as MoneyIcon,
  WorkHistory as ExperienceIcon,
  Event as ScheduleIcon,
} from '@mui/icons-material';
import { format, parseISO } from 'date-fns';
import { PageHeader } from '@/components/shared';
import {
  ProviderStatusChip,
  ProviderTypeChip,
  AcceptingPatientsChip,
  LicenseBadge,
  ScheduleGrid,
} from '../components';
import { useProvider } from '../hooks/useProvider';

export function ProviderDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: provider, isLoading, error } = useProvider(id!);

  if (error) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        Failed to load provider details. The provider may not exist or you don't have permission to
        view it.
      </Alert>
    );
  }

  const initials = provider ? `${provider.firstName[0]}${provider.lastName[0]}` : '';

  return (
    <>
      <PageHeader
        title={isLoading ? 'Loading...' : provider?.displayName || ''}
        subtitle={isLoading ? '' : provider?.providerNumber}
        breadcrumbs={[
          { label: 'Dashboard', href: '/app' },
          { label: 'Providers', href: '/app/providers' },
          { label: provider?.displayName || 'Detail' },
        ]}
        action={
          <Button
            variant="outlined"
            startIcon={<EditIcon />}
            onClick={() => navigate(`/app/providers/${id}/edit`)}
            sx={{ cursor: 'pointer' }}
          >
            Edit Provider
          </Button>
        }
      />

      <Grid container spacing={3}>
        {/* Main Info */}
        <Grid size={{ xs: 12, lg: 8 }}>
          {/* Profile Card */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 3, mb: 3 }}>
                {isLoading ? (
                  <Skeleton variant="circular" width={80} height={80} />
                ) : (
                  <Avatar
                    sx={{
                      width: 80,
                      height: 80,
                      bgcolor: 'primary.light',
                      color: 'primary.main',
                      fontSize: '1.5rem',
                      fontWeight: 600,
                    }}
                  >
                    {initials}
                  </Avatar>
                )}
                <Box sx={{ flex: 1 }}>
                  {isLoading ? (
                    <>
                      <Skeleton width={200} height={32} />
                      <Skeleton width={150} height={20} />
                    </>
                  ) : (
                    <>
                      <Typography variant="h2" sx={{ mb: 0.5 }}>
                        {provider?.displayName}
                      </Typography>
                      <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                        <ProviderStatusChip status={provider!.status} size="medium" />
                        <ProviderTypeChip type={provider!.providerType} />
                        <AcceptingPatientsChip accepting={provider!.acceptingPatients} />
                        {provider?.specialization && (
                          <Chip label={provider.specialization} variant="outlined" />
                        )}
                      </Stack>
                    </>
                  )}
                </Box>
              </Box>

              <Divider sx={{ my: 2 }} />

              {/* Contact Info */}
              <Grid container spacing={3}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="h4" sx={{ mb: 2 }}>
                    Contact Information
                  </Typography>
                  <List disablePadding>
                    <ListItem disablePadding sx={{ mb: 1.5 }}>
                      <ListItemIcon sx={{ minWidth: 40 }}>
                        <EmailIcon color="action" />
                      </ListItemIcon>
                      <ListItemText
                        primary={isLoading ? <Skeleton width={180} /> : provider?.email}
                        secondary="Email"
                      />
                    </ListItem>
                    <ListItem disablePadding sx={{ mb: 1.5 }}>
                      <ListItemIcon sx={{ minWidth: 40 }}>
                        <PhoneIcon color="action" />
                      </ListItemIcon>
                      <ListItemText
                        primary={
                          isLoading ? <Skeleton width={120} /> : provider?.phoneNumber || '—'
                        }
                        secondary="Phone"
                      />
                    </ListItem>
                  </List>
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Typography variant="h4" sx={{ mb: 2 }}>
                    Professional Details
                  </Typography>
                  <List disablePadding>
                    <ListItem disablePadding sx={{ mb: 1.5 }}>
                      <ListItemIcon sx={{ minWidth: 40 }}>
                        <BadgeIcon color="action" />
                      </ListItemIcon>
                      <ListItemText
                        primary={isLoading ? <Skeleton width={120} /> : provider?.npiNumber || '—'}
                        secondary="NPI Number"
                      />
                    </ListItem>
                    <ListItem disablePadding sx={{ mb: 1.5 }}>
                      <ListItemIcon sx={{ minWidth: 40 }}>
                        <SchoolIcon color="action" />
                      </ListItemIcon>
                      <ListItemText
                        primary={
                          isLoading ? <Skeleton width={150} /> : provider?.qualification || '—'
                        }
                        secondary="Qualification"
                      />
                    </ListItem>
                    <ListItem disablePadding sx={{ mb: 1.5 }}>
                      <ListItemIcon sx={{ minWidth: 40 }}>
                        <ExperienceIcon color="action" />
                      </ListItemIcon>
                      <ListItemText
                        primary={
                          isLoading ? (
                            <Skeleton width={80} />
                          ) : (
                            `${provider?.yearsOfExperience || '—'} years`
                          )
                        }
                        secondary="Experience"
                      />
                    </ListItem>
                    <ListItem disablePadding>
                      <ListItemIcon sx={{ minWidth: 40 }}>
                        <MoneyIcon color="action" />
                      </ListItemIcon>
                      <ListItemText
                        primary={
                          isLoading ? (
                            <Skeleton width={80} />
                          ) : provider?.consultationFee ? (
                            `$${provider.consultationFee}`
                          ) : (
                            '—'
                          )
                        }
                        secondary="Consultation Fee"
                      />
                    </ListItem>
                  </List>
                </Grid>
              </Grid>
            </CardContent>
          </Card>

          {/* Schedule */}
          {provider?.schedules && provider.schedules.length > 0 && (
            <ScheduleGrid schedules={provider.schedules} />
          )}
        </Grid>

        {/* Sidebar */}
        <Grid size={{ xs: 12, lg: 4 }}>
          {/* License Card */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography
                variant="h4"
                sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 1 }}
              >
                <BadgeIcon fontSize="small" /> License Information
              </Typography>
              {isLoading ? (
                <Skeleton height={100} />
              ) : provider?.license ? (
                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Status
                    </Typography>
                    <LicenseBadge
                      valid={provider.license.valid}
                      daysUntilExpiry={provider.license.daysUntilExpiry}
                      licenseState={provider.license.licenseState}
                    />
                  </Box>
                  <Box sx={{ mb: 1.5 }}>
                    <Typography variant="caption" color="text.secondary">
                      License Number
                    </Typography>
                    <Typography variant="body1" fontFamily="monospace">
                      {provider.license.licenseNumber}
                    </Typography>
                  </Box>
                  <Box sx={{ mb: 1.5 }}>
                    <Typography variant="caption" color="text.secondary">
                      State
                    </Typography>
                    <Typography variant="body1">{provider.license.licenseState}</Typography>
                  </Box>
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Expiry Date
                    </Typography>
                    <Typography variant="body1">
                      {format(parseISO(provider.license.expiryDate), 'MMMM d, yyyy')}
                    </Typography>
                  </Box>
                </Box>
              ) : (
                <Typography color="text.secondary">No license information</Typography>
              )}
            </CardContent>
          </Card>

          {/* Quick Actions */}
          <Card>
            <CardContent>
              <Typography variant="h4" sx={{ mb: 2 }}>
                Quick Actions
              </Typography>
              <Stack spacing={1}>
                <Button
                  variant="outlined"
                  fullWidth
                  startIcon={<ScheduleIcon />}
                  onClick={() => navigate(`/app/appointments?providerId=${id}`)}
                  sx={{ cursor: 'pointer', justifyContent: 'flex-start' }}
                >
                  View Appointments
                </Button>
                <Button
                  variant="outlined"
                  fullWidth
                  startIcon={<EditIcon />}
                  onClick={() => navigate(`/app/providers/${id}/edit`)}
                  sx={{ cursor: 'pointer', justifyContent: 'flex-start' }}
                >
                  Edit Provider
                </Button>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </>
  );
}
