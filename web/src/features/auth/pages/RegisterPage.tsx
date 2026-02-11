import { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Card, CardContent, Button, Link, Alert, Stack, Box, Typography } from '@mui/material';
import { FormField } from '@/components/shared';
import { useRegister } from '../hooks/useAuth';
import {
  AuthLayout,
  AuthBranding,
  PasswordInput,
  PasswordStrengthMeter,
  SocialLoginButtons,
  TermsCheckbox,
} from '../components';
import { registerSchema, type RegisterFormValues } from '../schemas';
import { AUTH_ROUTES } from '../constants';

export function RegisterPage() {
  const [password, setPassword] = useState('');
  const registerMutation = useRegister();

  const { control, handleSubmit, watch } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      confirmPassword: '',
      acceptTerms: false,
    },
  });

  const watchedPassword = watch('password');
  if (watchedPassword !== password) {
    setPassword(watchedPassword);
  }

  const onSubmit = (data: RegisterFormValues) => {
    const { confirmPassword: _, acceptTerms: __, ...payload } = data;
    registerMutation.mutate(payload);
  };

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
          <AuthBranding title="Create Account" subtitle="Join HealthCare Platform today" />

          {registerMutation.isError && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {(registerMutation.error as Error)?.message ||
                'Registration failed. Please try again.'}
            </Alert>
          )}

          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <Stack spacing={2.5}>
              {}
              <Box sx={{ display: 'flex', gap: 2 }}>
                <FormField<RegisterFormValues>
                  name="firstName"
                  control={control}
                  label="First Name"
                  autoComplete="given-name"
                />
                <FormField<RegisterFormValues>
                  name="lastName"
                  control={control}
                  label="Last Name"
                  autoComplete="family-name"
                />
              </Box>

              <FormField<RegisterFormValues>
                name="username"
                control={control}
                label="Username"
                autoComplete="username"
              />

              <FormField<RegisterFormValues>
                name="email"
                control={control}
                label="Email"
                type="email"
                autoComplete="email"
              />

              <Box>
                <PasswordInput<RegisterFormValues>
                  name="password"
                  control={control}
                  label="Password"
                  autoComplete="new-password"
                />
                <PasswordStrengthMeter password={password} />
              </Box>

              <PasswordInput<RegisterFormValues>
                name="confirmPassword"
                control={control}
                label="Confirm Password"
                autoComplete="new-password"
              />

              <TermsCheckbox<RegisterFormValues> name="acceptTerms" control={control} />

              <Button
                type="submit"
                variant="contained"
                fullWidth
                size="large"
                disabled={registerMutation.isPending}
                sx={{
                  mt: 1,
                  py: 1.5,
                  borderRadius: 2,
                  fontWeight: 600,
                  cursor: 'pointer',
                }}
              >
                {registerMutation.isPending ? 'Creating account…' : 'Create Account'}
              </Button>

              <SocialLoginButtons mode="register" />
            </Stack>
          </form>

          <Typography variant="body2" sx={{ mt: 3, textAlign: 'center' }} color="text.secondary">
            Already have an account?{' '}
            <Link
              component={RouterLink}
              to={AUTH_ROUTES.LOGIN}
              fontWeight={600}
              sx={{ cursor: 'pointer' }}
            >
              Sign in
            </Link>
          </Typography>
        </CardContent>
      </Card>
    </AuthLayout>
  );
}
