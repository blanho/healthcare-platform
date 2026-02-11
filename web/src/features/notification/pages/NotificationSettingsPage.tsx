import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Switch,
  FormControlLabel,
  Stack,
  Alert,
  Grid,
  TextField,
  Chip,
  Skeleton,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Save as SaveIcon,
  Email as EmailIcon,
  Sms as SmsIcon,
  NotificationsActive as PushIcon,
  Inbox as InAppIcon,
} from '@mui/icons-material';
import { PageHeader } from '@/components/shared';
import { useNotificationPreferences, useUpdatePreferences } from '../hooks/useNotification';
import { useForm, Controller } from 'react-hook-form';
import { useEffect } from 'react';
import type { NotificationCategory } from '@/types';
import { notificationCategoryConfigData as notificationCategoryConfig } from '../components/notification-chip-config';

interface PreferencesFormData {
  emailEnabled: boolean;
  smsEnabled: boolean;
  pushEnabled: boolean;
  inAppEnabled: boolean;
  mutedCategories: NotificationCategory[];
  quietHoursStart: number | undefined;
  quietHoursEnd: number | undefined;
  timezone: string | undefined;
}

export function NotificationSettingsPage() {
  const navigate = useNavigate();
  const { data: preferences, isLoading } = useNotificationPreferences();
  const { mutate: updatePreferences, isPending: isSaving } = useUpdatePreferences();

  const { control, handleSubmit, reset, watch, setValue } = useForm<PreferencesFormData>({
    defaultValues: {
      emailEnabled: true,
      smsEnabled: false,
      pushEnabled: true,
      inAppEnabled: true,
      mutedCategories: [],
      quietHoursStart: undefined,
      quietHoursEnd: undefined,
      timezone: undefined,
    },
  });

  useEffect(() => {
    if (preferences) {
      reset({
        emailEnabled: preferences.emailEnabled,
        smsEnabled: preferences.smsEnabled,
        pushEnabled: preferences.pushEnabled,
        inAppEnabled: preferences.inAppEnabled,
        mutedCategories: preferences.mutedCategories,
        quietHoursStart: preferences.quietHoursStart ?? undefined,
        quietHoursEnd: preferences.quietHoursEnd ?? undefined,
        timezone: preferences.timezone ?? undefined,
      });
    }
  }, [preferences, reset]);

  const mutedCategories = watch('mutedCategories');

  const onSubmit = (data: PreferencesFormData) => {
    updatePreferences(data);
  };

  const toggleCategoryMute = (category: NotificationCategory) => {
    const current = mutedCategories || [];
    if (current.includes(category)) {
      setValue(
        'mutedCategories',
        current.filter((c) => c !== category),
      );
    } else {
      setValue('mutedCategories', [...current, category]);
    }
  };

  if (isLoading) {
    return (
      <Box>
        <Skeleton variant="text" width={300} height={50} />
        <Skeleton variant="rectangular" height={400} sx={{ mt: 2, borderRadius: 2 }} />
      </Box>
    );
  }

  return (
    <Box>
      <PageHeader
        title="Notification Settings"
        subtitle="Customize how and when you receive notifications"
        breadcrumbs={[{ label: 'Notifications', href: '/app/notifications' }, { label: 'Settings' }]}
        action={
          <Button
            variant="outlined"
            startIcon={<BackIcon />}
            onClick={() => navigate(-1)}
            sx={{ minHeight: 44 }}
          >
            Back
          </Button>
        }
      />

      <form onSubmit={handleSubmit(onSubmit)}>
        <Grid container spacing={3}>
          {}
          <Grid size={{ xs: 12, md: 6 }}>
            <Card>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 3 }}>
                  Notification Channels
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  Choose how you want to receive notifications
                </Typography>

                <Stack spacing={2}>
                  <Controller
                    name="emailEnabled"
                    control={control}
                    render={({ field }) => (
                      <FormControlLabel
                        control={<Switch {...field} checked={field.value} />}
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <EmailIcon color="action" />
                            <Box>
                              <Typography>Email Notifications</Typography>
                              <Typography variant="caption" color="text.secondary">
                                Receive notifications via email
                              </Typography>
                            </Box>
                          </Box>
                        }
                        sx={{ m: 0, p: 1.5, border: 1, borderColor: 'divider', borderRadius: 1 }}
                      />
                    )}
                  />

                  <Controller
                    name="smsEnabled"
                    control={control}
                    render={({ field }) => (
                      <FormControlLabel
                        control={<Switch {...field} checked={field.value} />}
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <SmsIcon color="action" />
                            <Box>
                              <Typography>SMS Notifications</Typography>
                              <Typography variant="caption" color="text.secondary">
                                Receive text message alerts
                              </Typography>
                            </Box>
                          </Box>
                        }
                        sx={{ m: 0, p: 1.5, border: 1, borderColor: 'divider', borderRadius: 1 }}
                      />
                    )}
                  />

                  <Controller
                    name="pushEnabled"
                    control={control}
                    render={({ field }) => (
                      <FormControlLabel
                        control={<Switch {...field} checked={field.value} />}
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <PushIcon color="action" />
                            <Box>
                              <Typography>Push Notifications</Typography>
                              <Typography variant="caption" color="text.secondary">
                                Receive browser push notifications
                              </Typography>
                            </Box>
                          </Box>
                        }
                        sx={{ m: 0, p: 1.5, border: 1, borderColor: 'divider', borderRadius: 1 }}
                      />
                    )}
                  />

                  <Controller
                    name="inAppEnabled"
                    control={control}
                    render={({ field }) => (
                      <FormControlLabel
                        control={<Switch {...field} checked={field.value} />}
                        label={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <InAppIcon color="action" />
                            <Box>
                              <Typography>In-App Notifications</Typography>
                              <Typography variant="caption" color="text.secondary">
                                Show notifications within the app
                              </Typography>
                            </Box>
                          </Box>
                        }
                        sx={{ m: 0, p: 1.5, border: 1, borderColor: 'divider', borderRadius: 1 }}
                      />
                    )}
                  />
                </Stack>
              </CardContent>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12, md: 6 }}>
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 3 }}>
                  Category Preferences
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  Mute notifications from specific categories
                </Typography>

                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                  {(
                    Object.entries(notificationCategoryConfig) as [
                      NotificationCategory,
                      { label: string; color: string },
                    ][]
                  ).map(([key, { label, color }]) => {
                    const isMuted = mutedCategories?.includes(key);
                    return (
                      <Chip
                        key={key}
                        label={label}
                        onClick={() => toggleCategoryMute(key)}
                        variant={isMuted ? 'filled' : 'outlined'}
                        sx={{
                          bgcolor: isMuted ? 'grey.300' : 'transparent',
                          color: isMuted ? 'text.disabled' : color,
                          borderColor: isMuted ? 'grey.300' : color,
                          textDecoration: isMuted ? 'line-through' : 'none',
                          cursor: 'pointer',
                          '&:hover': {
                            bgcolor: isMuted ? 'grey.400' : `${color}14`,
                          },
                        }}
                      />
                    );
                  })}
                </Box>

                {(mutedCategories?.length || 0) > 0 && (
                  <Alert severity="info" sx={{ mt: 2 }}>
                    You have muted {mutedCategories?.length} categories. You won't receive
                    notifications from these categories.
                  </Alert>
                )}
              </CardContent>
            </Card>

            {/* Quiet Hours */}
            <Card>
              <CardContent>
                <Typography variant="h4" sx={{ mb: 3 }}>
                  Quiet Hours
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  Don't send notifications during these hours
                </Typography>

                <Stack direction="row" spacing={2}>
                  <Controller
                    name="quietHoursStart"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) =>
                          field.onChange(e.target.value ? parseInt(e.target.value) : null)
                        }
                        type="number"
                        label="Start Hour (0-23)"
                        fullWidth
                        size="small"
                        slotProps={{ htmlInput: { min: 0, max: 23 } }}
                      />
                    )}
                  />
                  <Controller
                    name="quietHoursEnd"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) =>
                          field.onChange(e.target.value ? parseInt(e.target.value) : null)
                        }
                        type="number"
                        label="End Hour (0-23)"
                        fullWidth
                        size="small"
                        slotProps={{ htmlInput: { min: 0, max: 23 } }}
                      />
                    )}
                  />
                </Stack>
              </CardContent>
            </Card>
          </Grid>

          {}
          <Grid size={{ xs: 12 }}>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                type="submit"
                variant="contained"
                size="large"
                startIcon={<SaveIcon />}
                disabled={isSaving}
                sx={{ minHeight: 48, minWidth: 160 }}
              >
                {isSaving ? 'Saving...' : 'Save Changes'}
              </Button>
            </Box>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
}
