import { Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Card, CardContent, Button, Link, Alert, Stack, Typography } from '@mui/material';
import { ArrowBack as ArrowBackIcon } from '@mui/icons-material';
import { FormField } from '@/components/shared';
import { AuthLayout, AuthBranding } from '../components';
import { forgotPasswordSchema, type ForgotPasswordFormValues } from '../schemas';
import { AUTH_ROUTES } from '../constants';
import { useState } from 'react';

export function ForgotPasswordPage() {
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [submittedEmail, setSubmittedEmail] = useState('');

  const { control, handleSubmit } = useForm<ForgotPasswordFormValues>({
    resolver: zodResolver(forgotPasswordSchema),
    defaultValues: {
      email: '',
    },
  });

  const onSubmit = (data: ForgotPasswordFormValues) => {

    setSubmittedEmail(data.email);
    setIsSubmitted(true);
  };

  return (
    <AuthLayout showSidePanel={false}>
      <Card
        elevation={0}
        sx={{
          maxWidth: 440,
          mx: 'auto',
          borderRadius: 4,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
          border: '1px solid',
          borderColor: 'divider',
        }}
      >
        <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
          <AuthBranding
            title={isSubmitted ? 'Check Your Email' : 'Forgot Password'}
            subtitle={
              isSubmitted
                ? `We've sent password reset instructions to ${submittedEmail}`
                : "Enter your email and we'll send you reset instructions"
            }
          />

          {isSubmitted ? (
            <Stack spacing={3}>
              <Alert severity="success" sx={{ borderRadius: 2 }}>
                If an account exists with this email, you will receive a password reset link
                shortly.
              </Alert>

              <Button
                component={RouterLink}
                to={AUTH_ROUTES.LOGIN}
                variant="contained"
                fullWidth
                size="large"
                sx={{
                  py: 1.5,
                  borderRadius: 2,
                  fontWeight: 600,
                  cursor: 'pointer',
                }}
              >
                Back to Sign In
              </Button>

              <Typography variant="body2" textAlign="center" color="text.secondary">
                Didn't receive the email?{' '}
                <Link
                  component="button"
                  onClick={() => setIsSubmitted(false)}
                  sx={{ cursor: 'pointer' }}
                >
                  Try again
                </Link>
              </Typography>
            </Stack>
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} noValidate>
              <Stack spacing={2.5}>
                <FormField<ForgotPasswordFormValues>
                  name="email"
                  control={control}
                  label="Email Address"
                  type="email"
                  autoComplete="email"
                  autoFocus
                />

                <Button
                  type="submit"
                  variant="contained"
                  fullWidth
                  size="large"
                  sx={{
                    py: 1.5,
                    borderRadius: 2,
                    fontWeight: 600,
                    cursor: 'pointer',
                  }}
                >
                  Send Reset Link
                </Button>

                <Button
                  component={RouterLink}
                  to={AUTH_ROUTES.LOGIN}
                  variant="text"
                  fullWidth
                  startIcon={<ArrowBackIcon />}
                  sx={{
                    color: 'text.secondary',
                    cursor: 'pointer',
                  }}
                >
                  Back to Sign In
                </Button>
              </Stack>
            </form>
          )}
        </CardContent>
      </Card>
    </AuthLayout>
  );
}
