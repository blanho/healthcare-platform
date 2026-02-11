

import { useEffect } from 'react';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import { Card, CardContent, Button, Alert, Typography, CircularProgress, Box } from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import EmailIcon from '@mui/icons-material/Email';
import { useVerifyEmail, useResendVerificationEmail } from '../hooks/useEmailVerification';
import { AuthLayout, AuthBranding } from '../components';
import { AUTH_ROUTES } from '../constants';

export function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const verifyEmailMutation = useVerifyEmail();
  const resendMutation = useResendVerificationEmail();

  useEffect(() => {
    if (token && !verifyEmailMutation.isSuccess && !verifyEmailMutation.isPending) {
      verifyEmailMutation.mutate({ token });
    }
  }, [token]);

  if (verifyEmailMutation.isPending) {
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
            <CircularProgress size={48} sx={{ mb: 3 }} />
            <Typography variant="h6" fontWeight={600}>
              Verifying your email...
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Please wait while we verify your email address.
            </Typography>
          </CardContent>
        </Card>
      </AuthLayout>
    );
  }

  if (verifyEmailMutation.isSuccess) {
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
              Email Verified!
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Your email address has been verified successfully. You can now access all features.
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

  if (verifyEmailMutation.isError) {
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
                backgroundColor: 'error.light',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mx: 'auto',
                mb: 3,
              }}
            >
              <ErrorOutlineIcon sx={{ fontSize: 32, color: 'error.main' }} />
            </Box>
            <Typography variant="h5" fontWeight={600} gutterBottom>
              Verification Failed
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              {(verifyEmailMutation.error as Error)?.message ||
                'The verification link is invalid or has expired.'}
            </Typography>
            <Button
              variant="contained"
              fullWidth
              onClick={() => resendMutation.mutate()}
              disabled={resendMutation.isPending}
              sx={{ mb: 2, cursor: 'pointer' }}
            >
              {resendMutation.isPending ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                'Resend Verification Email'
              )}
            </Button>
            {resendMutation.isSuccess && (
              <Alert severity="success" sx={{ borderRadius: 2 }}>
                Verification email has been sent. Please check your inbox.
              </Alert>
            )}
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
        <CardContent sx={{ p: { xs: 3, sm: 4 }, textAlign: 'center' }}>
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
              mb: 3,
            }}
          >
            <EmailIcon sx={{ fontSize: 32, color: 'primary.main' }} />
          </Box>
          <AuthBranding
            title="Check Your Email"
            subtitle="We've sent a verification link to your email address"
          />
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            Click the link in the email to verify your account. If you don't see it, check your spam
            folder.
          </Typography>
          <Button
            variant="outlined"
            fullWidth
            onClick={() => resendMutation.mutate()}
            disabled={resendMutation.isPending}
            sx={{ mb: 2, cursor: 'pointer' }}
          >
            {resendMutation.isPending ? (
              <CircularProgress size={24} color="inherit" />
            ) : (
              'Resend Email'
            )}
          </Button>
          {resendMutation.isSuccess && (
            <Alert severity="success" sx={{ borderRadius: 2 }}>
              Verification email has been sent.
            </Alert>
          )}
        </CardContent>
      </Card>
    </AuthLayout>
  );
}
