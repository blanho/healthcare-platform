import { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Card,
  CardContent,
  Button,
  Link,
  Alert,
  Stack,
  FormControlLabel,
  Checkbox,
  Typography,
} from '@mui/material';
import { FormField } from '@/components/shared';
import { useLogin } from '../hooks/useAuth';
import { AuthLayout, AuthBranding, PasswordInput, SocialLoginButtons } from '../components';
import { loginSchema, type LoginFormValues } from '../schemas';
import { AUTH_ROUTES } from '../constants';

export function LoginPage() {
  const [rememberMe, setRememberMe] = useState(false);
  const loginMutation = useLogin();

  const { control, handleSubmit } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      usernameOrEmail: '',
      password: '',
      rememberMe: false,
    },
  });

  const onSubmit = (data: LoginFormValues) => {
    loginMutation.mutate(data);
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
          <AuthBranding title="Welcome Back" subtitle="Sign in to your HealthCare account" />

          {loginMutation.isError && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {(loginMutation.error as Error)?.message || 'Invalid credentials. Please try again.'}
            </Alert>
          )}

          <form onSubmit={handleSubmit(onSubmit)} noValidate>
            <Stack spacing={2.5}>
              <FormField<LoginFormValues>
                name="usernameOrEmail"
                control={control}
                label="Username or Email"
                autoComplete="username"
                autoFocus
              />

              <PasswordInput<LoginFormValues>
                name="password"
                control={control}
                label="Password"
                autoComplete="current-password"
              />

              <Stack direction="row" justifyContent="space-between" alignItems="center">
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}
                      size="small"
                      sx={{ cursor: 'pointer' }}
                    />
                  }
                  label={
                    <Typography variant="body2" color="text.secondary">
                      Remember me
                    </Typography>
                  }
                />
                <Link
                  component={RouterLink}
                  to={AUTH_ROUTES.FORGOT_PASSWORD}
                  variant="body2"
                  sx={{ cursor: 'pointer' }}
                >
                  Forgot password?
                </Link>
              </Stack>

              <Button
                type="submit"
                variant="contained"
                fullWidth
                size="large"
                disabled={loginMutation.isPending}
                sx={{
                  mt: 1,
                  py: 1.5,
                  borderRadius: 2,
                  fontWeight: 600,
                  cursor: 'pointer',
                }}
              >
                {loginMutation.isPending ? 'Signing in…' : 'Sign In'}
              </Button>

              <SocialLoginButtons mode="login" />
            </Stack>
          </form>

          <Typography variant="body2" sx={{ mt: 3, textAlign: 'center' }} color="text.secondary">
            Don't have an account?{' '}
            <Link
              component={RouterLink}
              to={AUTH_ROUTES.REGISTER}
              fontWeight={600}
              sx={{ cursor: 'pointer' }}
            >
              Create one
            </Link>
          </Typography>
        </CardContent>
      </Card>
    </AuthLayout>
  );
}
