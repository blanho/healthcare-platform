

import { useEffect, useState } from 'react';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Card,
  CardContent,
  Button,
  Link,
  Alert,
  Stack,
  Typography,
  CircularProgress,
  Box,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useResetPassword } from '../hooks/usePassword';
import { AuthLayout, AuthBranding, PasswordInput, PasswordStrengthMeter } from '../components';
import { resetPasswordSchema, type ResetPasswordFormValues } from '../schemas';
import { AUTH_ROUTES } from '../constants';

export function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [password, setPassword] = useState('');

  const resetPasswordMutation = useResetPassword();

  const { control, handleSubmit, watch, setValue } = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: {
      token: token || '',
      password: '',
      confirmPassword: '',
    },
  });

  useEffect(() => {
    if (token) {
      setValue('token', token);
    }
  }, [token, setValue]);

  const watchedPassword = watch('password');
  useEffect(() => {
    setPassword(watchedPassword || '');
  }, [watchedPassword]);

  const onSubmit = (data: ResetPasswordFormValues) => {
    resetPasswordMutation.mutate({
      token: data.token || token || '',
      newPassword: data.password,
    });
  };

  if (!token) {
    return (
      <AuthLayout>
        <Card
          elevation={0}
          sx={{
            borderRadius: 4,
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
            border: '1px solid',
            borderColor: 'divider',
          }}
        >
          <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
            <AuthBranding
              title="Invalid Link"
              subtitle="The password reset link is invalid or has expired"
            />
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              Please request a new password reset link.
            </Alert>
            <Button
              component={RouterLink}
              to={AUTH_ROUTES.FORGOT_PASSWORD}
              variant="contained"
              fullWidth
              sx={{ cursor: 'pointer' }}
            >
              Request New Link
            </Button>
          </CardContent>
        </Card>
      </AuthLayout>
    );
  }

  if (resetPasswordMutation.isSuccess) {
    return (
      <AuthLayout>
        <Card
          elevation={0}
          sx={{
            borderRadius: 4,
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
            border: '1px solid',
            borderColor: 'divider',
          }}
        >
          <CardContent sx={{ p: { xs: 3, sm: 4 }, textAlign: 'center' }}>
            <Box
              sx={{
                width: 64,
                height: 64,
                borderRadius: '50%',
                backgroundColor: 'success.light',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mx: 'auto',
                mb: 3,
              }}
            >
              <CheckCircleIcon sx={{ fontSize: 32, color: 'success.main' }} />
            </Box>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              Password Reset
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Your password has been reset successfully. You can now sign in with your new password.
            </Typography>
            <Button
              component={RouterLink}
              to={AUTH_ROUTES.LOGIN}
              variant="contained"
              fullWidth
              sx={{ cursor: 'pointer' }}
            >
              Sign In
            </Button>
          </CardContent>
        </Card>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout>
      <Card
        elevation={0}
        sx={{
          borderRadius: 4,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
          border: '1px solid',
          borderColor: 'divider',
        }}
      >
        <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
          <AuthBranding title="Reset Password" subtitle="Enter your new password below" />

          {resetPasswordMutation.isError && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {(resetPasswordMutation.error as Error)?.message ||
                'Failed to reset password. The link may have expired.'}
            </Alert>
          )}

          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <Stack spacing={2.5}>
              <Box>
                <PasswordInput<ResetPasswordFormValues>
                  name="password"
                  control={control}
                  label="New Password"
                  autoComplete="new-password"
                  autoFocus
                />
                <PasswordStrengthMeter password={password} />
              </Box>

              <PasswordInput<ResetPasswordFormValues>
                name="confirmPassword"
                control={control}
                label="Confirm New Password"
                autoComplete="new-password"
              />

              <Button
                type="submit"
                variant="contained"
                fullWidth
                size="large"
                disabled={resetPasswordMutation.isPending}
                sx={{
                  mt: 1,
                  py: 1.5,
                  borderRadius: 2,
                  fontWeight: 600,
                  cursor: 'pointer',
                }}
              >
                {resetPasswordMutation.isPending ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  'Reset Password'
                )}
              </Button>
            </Stack>
          </form>

          <Typography variant="body2" sx={{ mt: 3, textAlign: 'center' }} color="text.secondary">
            Remember your password?{' '}
            <Link
              component={RouterLink}
              to={AUTH_ROUTES.LOGIN}
              fontWeight={600}
              sx={{ cursor: 'pointer' }}
            >
              Sign In
            </Link>
          </Typography>
        </CardContent>
      </Card>
    </AuthLayout>
  );
}
