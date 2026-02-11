

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Container,
  Card,
  CardContent,
  CardHeader,
  Button,
  Alert,
  Stack,
  Typography,
  CircularProgress,
  Box,
  Divider,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { useChangePassword } from '../hooks/usePassword';
import { PasswordInput, PasswordStrengthMeter } from '../components';
import { changePasswordSchema, type ChangePasswordFormValues } from '../schemas';

export function ChangePasswordPage() {
  const [showSuccess, setShowSuccess] = useState(false);
  const changePasswordMutation = useChangePassword();

  const { control, handleSubmit, watch, reset } = useForm<ChangePasswordFormValues>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
  });

  const watchedPassword = watch('newPassword');

  const onSubmit = (data: ChangePasswordFormValues) => {
    changePasswordMutation.mutate(
      {
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      },
      {
        onSuccess: () => {
          setShowSuccess(true);
          reset();
        },
      },
    );
  };

  return (
    <Container maxWidth="sm" sx={{ py: 4 }}>
      <Card
        elevation={0}
        sx={{
          borderRadius: 4,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
          border: '1px solid',
          borderColor: 'divider',
        }}
      >
        <CardHeader
          avatar={
            <Box
              sx={{
                width: 48,
                height: 48,
                borderRadius: '50%',
                backgroundColor: 'primary.light',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <LockOutlinedIcon sx={{ color: 'primary.main' }} />
            </Box>
          }
          title={
            <Typography variant="h6" fontWeight={600}>
              Change Password
            </Typography>
          }
          subheader="Update your password to keep your account secure"
        />
        <Divider />
        <CardContent sx={{ p: 3 }}>
          {showSuccess && (
            <Alert
              severity="success"
              icon={<CheckCircleIcon />}
              sx={{ mb: 3, borderRadius: 2 }}
              onClose={() => setShowSuccess(false)}
            >
              Your password has been changed successfully.
            </Alert>
          )}

          {changePasswordMutation.isError && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {(changePasswordMutation.error as Error)?.message ||
                'Failed to change password. Please check your current password.'}
            </Alert>
          )}

          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <Stack spacing={2.5}>
              <PasswordInput<ChangePasswordFormValues>
                name="currentPassword"
                control={control}
                label="Current Password"
                autoComplete="current-password"
              />

              <Box>
                <PasswordInput<ChangePasswordFormValues>
                  name="newPassword"
                  control={control}
                  label="New Password"
                  autoComplete="new-password"
                />
                <PasswordStrengthMeter password={watchedPassword || ''} />
              </Box>

              <PasswordInput<ChangePasswordFormValues>
                name="confirmPassword"
                control={control}
                label="Confirm New Password"
                autoComplete="new-password"
              />

              <Alert severity="info" sx={{ borderRadius: 2 }}>
                <Typography variant="body2">
                  Your password must be at least 8 characters long and contain uppercase, lowercase,
                  and numeric characters.
                </Typography>
              </Alert>

              <Button
                type="submit"
                variant="contained"
                fullWidth
                size="large"
                disabled={changePasswordMutation.isPending}
                sx={{
                  mt: 1,
                  py: 1.5,
                  borderRadius: 2,
                  fontWeight: 600,
                  cursor: 'pointer',
                }}
              >
                {changePasswordMutation.isPending ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  'Change Password'
                )}
              </Button>
            </Stack>
          </form>
        </CardContent>
      </Card>
    </Container>
  );
}
