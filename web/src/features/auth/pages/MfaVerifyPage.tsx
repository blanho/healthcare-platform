

import { useState } from 'react';
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom';
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
import SecurityIcon from '@mui/icons-material/Security';
import { useAuthStore } from '@/stores';
import { authApi } from '../api/auth.api';
import { AuthLayout, AuthBranding, OtpInput } from '../components';
import { AUTH_ROUTES } from '../constants';
import type { AuthUser } from '@/stores/auth.store';
import type { Role, Permission } from '@/types';

export function MfaVerifyPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuthStore();

  const [code, setCode] = useState('');
  const [useBackupCode, setUseBackupCode] = useState(false);
  const [error, setError] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);

  const mfaToken = location.state?.mfaToken;
  const tokenResponse = location.state?.tokenResponse;

  const handleVerify = async () => {
    if (!code) {
      setError('Please enter a code');
      return;
    }

    if (!useBackupCode && code.length !== 6) {
      setError('Please enter a 6-digit code');
      return;
    }

    setIsVerifying(true);
    setError('');

    try {

      const result = await authApi.mfa.verify({
        code,
        useBackupCode,
      });

      if (result.valid && tokenResponse) {

        const me = await authApi.getMe();
        const user: AuthUser = {
          id: me.id,
          username: me.username,
          email: me.email,
          firstName: me.firstName,
          lastName: me.lastName,
          fullName: me.fullName,
          roles: me.roles as Role[],
          permissions: me.permissions as Permission[],
          patientId: me.patientId,
          providerId: me.providerId,
        };
        login(tokenResponse.accessToken, tokenResponse.refreshToken, user);
        navigate('/');
      } else {
        setError('Invalid code. Please try again.');
        setCode('');
      }
    } catch (err) {
      setError((err as Error)?.message || 'Verification failed. Please try again.');
      setCode('');
    } finally {
      setIsVerifying(false);
    }
  };

  if (!mfaToken) {
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
            <AuthBranding title="Session Expired" subtitle="Please sign in again" />
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
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Box
              sx={{
                width: 64,
                height: 64,
                borderRadius: '50%',
                backgroundColor: 'primary.light',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mx: 'auto',
                mb: 2,
              }}
            >
              <SecurityIcon sx={{ fontSize: 32, color: 'primary.main' }} />
            </Box>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              Two-Factor Authentication
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {useBackupCode
                ? 'Enter one of your backup codes'
                : 'Enter the 6-digit code from your authenticator app'}
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {error}
            </Alert>
          )}

          <Stack spacing={3}>
            <Box sx={{ textAlign: 'center' }}>
              {useBackupCode ? (
                <Box
                  component="input"
                  type="text"
                  value={code}
                  onChange={(e) => setCode(e.target.value.toUpperCase())}
                  placeholder="XXXXXXXX"
                  maxLength={8}
                  autoFocus
                  sx={{
                    width: '100%',
                    maxWidth: 200,
                    p: 2,
                    fontSize: '1.25rem',
                    fontFamily: 'monospace',
                    fontWeight: 600,
                    letterSpacing: '0.1em',
                    textAlign: 'center',
                    textTransform: 'uppercase',
                    border: '2px solid',
                    borderColor: 'divider',
                    borderRadius: 2,
                    outline: 'none',
                    '&:focus': {
                      borderColor: 'primary.main',
                    },
                  }}
                />
              ) : (
                <OtpInput
                  value={code}
                  onChange={(value) => {
                    setCode(value);
                    setError('');
                  }}
                  onComplete={handleVerify}
                  error={!!error}
                  autoFocus
                />
              )}
            </Box>

            <Button
              variant="contained"
              fullWidth
              size="large"
              onClick={handleVerify}
              disabled={
                isVerifying ||
                (!useBackupCode && code.length !== 6) ||
                (useBackupCode && code.length !== 8)
              }
              sx={{
                py: 1.5,
                borderRadius: 2,
                fontWeight: 600,
                cursor: 'pointer',
              }}
            >
              {isVerifying ? <CircularProgress size={24} color="inherit" /> : 'Verify'}
            </Button>

            <Box sx={{ textAlign: 'center' }}>
              <Button
                variant="text"
                size="small"
                onClick={() => {
                  setUseBackupCode(!useBackupCode);
                  setCode('');
                  setError('');
                }}
                sx={{ cursor: 'pointer', textTransform: 'none' }}
              >
                {useBackupCode ? 'Use authenticator app' : 'Use backup code instead'}
              </Button>
            </Box>
          </Stack>

          <Typography variant="body2" sx={{ mt: 3, textAlign: 'center' }} color="text.secondary">
            Having trouble?{' '}
            <Link
              component={RouterLink}
              to={AUTH_ROUTES.LOGIN}
              fontWeight={600}
              sx={{ cursor: 'pointer' }}
            >
              Try signing in again
            </Link>
          </Typography>
        </CardContent>
      </Card>
    </AuthLayout>
  );
}
