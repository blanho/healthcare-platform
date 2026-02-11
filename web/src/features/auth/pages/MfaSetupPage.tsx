

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  Stepper,
  Step,
  StepLabel,
} from '@mui/material';
import SecurityIcon from '@mui/icons-material/Security';
import { useMfaSetup, useMfaEnable } from '../hooks/useMfa';
import { MfaQrCode, OtpInput, BackupCodesList } from '../components';
import type { MfaSetupResponse, BackupCodesResponse } from '../types/auth.types';

const STEPS = ['Install App', 'Scan QR Code', 'Verify Code', 'Save Backup Codes'];

export function MfaSetupPage() {
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [setupData, setSetupData] = useState<MfaSetupResponse | null>(null);
  const [backupCodes, setBackupCodes] = useState<BackupCodesResponse | null>(null);
  const [code, setCode] = useState('');
  const [codeError, setCodeError] = useState('');

  const setupMutation = useMfaSetup();
  const enableMutation = useMfaEnable();

  const handleStartSetup = async () => {
    try {
      const data = await setupMutation.mutateAsync();
      setSetupData(data);
      setActiveStep(1);
    } catch (error) {
      console.error('Failed to start MFA setup', error);
    }
  };

  const handleVerifyCode = async () => {
    if (code.length !== 6) {
      setCodeError('Please enter a 6-digit code');
      return;
    }

    try {
      const result = await enableMutation.mutateAsync({
        secret: setupData!.secret,
        code,
      });
      setBackupCodes(result);
      setActiveStep(3);
    } catch {
      setCodeError('Invalid code. Please try again.');
      setCode('');
    }
  };

  const handleComplete = () => {
    navigate('/settings/security');
  };

  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box sx={{ textAlign: 'center', py: 2 }}>
            <Typography variant="body1" gutterBottom>
              You'll need an authenticator app to complete setup.
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Download and install one of these apps on your phone:
            </Typography>
            <Stack spacing={1} sx={{ mb: 3 }}>
              <Typography variant="body2">• Google Authenticator</Typography>
              <Typography variant="body2">• Microsoft Authenticator</Typography>
              <Typography variant="body2">• Authy</Typography>
              <Typography variant="body2">• 1Password</Typography>
            </Stack>
            <Button
              variant="contained"
              onClick={handleStartSetup}
              disabled={setupMutation.isPending}
              sx={{ cursor: 'pointer' }}
            >
              {setupMutation.isPending ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                'Continue'
              )}
            </Button>
          </Box>
        );

      case 1:
        return (
          <Box sx={{ py: 2 }}>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3, textAlign: 'center' }}>
              Open your authenticator app and scan this QR code:
            </Typography>
            {setupData && (
              <MfaQrCode
                qrCodeUri={setupData.qrCodeUri}
                secret={setupData.secret}
                accountName={setupData.accountName}
                issuer={setupData.issuer}
              />
            )}
            <Box sx={{ textAlign: 'center', mt: 3 }}>
              <Button
                variant="contained"
                onClick={() => setActiveStep(2)}
                sx={{ cursor: 'pointer' }}
              >
                Continue
              </Button>
            </Box>
          </Box>
        );

      case 2:
        return (
          <Box sx={{ py: 2, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Enter the 6-digit code from your authenticator app:
            </Typography>
            <Box sx={{ mb: 3 }}>
              <OtpInput
                value={code}
                onChange={(value) => {
                  setCode(value);
                  setCodeError('');
                }}
                error={!!codeError}
                helperText={codeError}
                autoFocus
              />
            </Box>
            {enableMutation.isError && (
              <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
                Invalid code. Please check your authenticator app and try again.
              </Alert>
            )}
            <Stack direction="row" spacing={2} justifyContent="center">
              <Button
                variant="outlined"
                onClick={() => setActiveStep(1)}
                sx={{ cursor: 'pointer' }}
              >
                Back
              </Button>
              <Button
                variant="contained"
                onClick={handleVerifyCode}
                disabled={code.length !== 6 || enableMutation.isPending}
                sx={{ cursor: 'pointer' }}
              >
                {enableMutation.isPending ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  'Verify'
                )}
              </Button>
            </Stack>
          </Box>
        );

      case 3:
        return (
          <Box sx={{ py: 2 }}>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3, textAlign: 'center' }}>
              Two-factor authentication is now enabled. Save these backup codes:
            </Typography>
            {backupCodes && (
              <BackupCodesList
                codes={backupCodes.backupCodes}
                message={backupCodes.message}
                onDone={handleComplete}
              />
            )}
          </Box>
        );

      default:
        return null;
    }
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
              <SecurityIcon sx={{ color: 'primary.main' }} />
            </Box>
          }
          title={
            <Typography variant="h6" fontWeight={600}>
              Set Up Two-Factor Authentication
            </Typography>
          }
          subheader="Add an extra layer of security to your account"
        />
        <Divider />
        <CardContent sx={{ p: 3 }}>
          {setupMutation.isError && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              Failed to start MFA setup. Please try again.
            </Alert>
          )}

          <Stepper activeStep={activeStep} alternativeLabel sx={{ mb: 4 }}>
            {STEPS.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {renderStepContent()}
        </CardContent>
      </Card>
    </Container>
  );
}
