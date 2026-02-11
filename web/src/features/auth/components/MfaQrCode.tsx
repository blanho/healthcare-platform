

import { useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  IconButton,
  Tooltip,
  Collapse,
  Button,
  Skeleton,
  useTheme,
} from '@mui/material';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import CheckIcon from '@mui/icons-material/Check';
import QrCode2Icon from '@mui/icons-material/QrCode2';
import { formatMfaSecret } from '../utils';

interface MfaQrCodeProps {
  qrCodeUri: string;
  secret: string;
  accountName: string;
  issuer: string;
  isLoading?: boolean;
}

export function MfaQrCode({
  qrCodeUri,
  secret,
  accountName,
  issuer,
  isLoading = false,
}: MfaQrCodeProps) {
  const theme = useTheme();
  const [showSecret, setShowSecret] = useState(false);
  const [copied, setCopied] = useState(false);

  const qrCodeImageUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(qrCodeUri)}`;

  const handleCopySecret = async () => {
    try {
      await navigator.clipboard.writeText(secret);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      console.error('Failed to copy secret');
    }
  };

  if (isLoading) {
    return (
      <Paper
        variant="outlined"
        sx={{
          p: 3,
          textAlign: 'center',
          borderRadius: 3,
        }}
      >
        <Skeleton
          variant="rectangular"
          width={200}
          height={200}
          sx={{ mx: 'auto', borderRadius: 2 }}
        />
        <Skeleton width="60%" sx={{ mx: 'auto', mt: 2 }} />
        <Skeleton width="40%" sx={{ mx: 'auto', mt: 1 }} />
      </Paper>
    );
  }

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 3,
        textAlign: 'center',
        borderRadius: 3,
        backgroundColor: theme.palette.background.default,
      }}
    >
      {}
      <Box
        sx={{
          display: 'inline-block',
          p: 2,
          backgroundColor: 'white',
          borderRadius: 2,
          mb: 2,
        }}
      >
        <img
          src={qrCodeImageUrl}
          alt="MFA QR Code"
          width={200}
          height={200}
          style={{ display: 'block' }}
        />
      </Box>

      {}
      <Typography variant="body2" color="text.secondary" gutterBottom>
        {issuer}
      </Typography>
      <Typography variant="body1" fontWeight={500}>
        {accountName}
      </Typography>

      {}
      <Box sx={{ mt: 3 }}>
        <Button
          size="small"
          startIcon={showSecret ? <VisibilityOffIcon /> : <VisibilityIcon />}
          onClick={() => setShowSecret(!showSecret)}
          sx={{ textTransform: 'none', cursor: 'pointer' }}
        >
          {showSecret ? 'Hide' : 'Show'} manual entry key
        </Button>

        <Collapse in={showSecret}>
          <Paper
            variant="outlined"
            sx={{
              mt: 2,
              p: 2,
              borderRadius: 2,
              backgroundColor: theme.palette.action.hover,
            }}
          >
            <Typography variant="caption" color="text.secondary" display="block" gutterBottom>
              Can't scan? Enter this key manually:
            </Typography>
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 1,
              }}
            >
              <Typography
                variant="body2"
                sx={{
                  fontFamily: 'monospace',
                  fontWeight: 600,
                  letterSpacing: '0.1em',
                  wordBreak: 'break-all',
                }}
              >
                {formatMfaSecret(secret)}
              </Typography>
              <Tooltip title={copied ? 'Copied!' : 'Copy to clipboard'}>
                <IconButton
                  size="small"
                  onClick={handleCopySecret}
                  color={copied ? 'success' : 'default'}
                  sx={{ cursor: 'pointer' }}
                >
                  {copied ? <CheckIcon fontSize="small" /> : <ContentCopyIcon fontSize="small" />}
                </IconButton>
              </Tooltip>
            </Box>
          </Paper>
        </Collapse>
      </Box>
    </Paper>
  );
}

// Empty state for when MFA is not set up
export function MfaEmptyState({ onSetup }: { onSetup: () => void }) {
  const theme = useTheme();

  return (
    <Paper
      variant="outlined"
      sx={{
        p: 4,
        textAlign: 'center',
        borderRadius: 3,
        borderStyle: 'dashed',
      }}
    >
      <Box
        sx={{
          width: 64,
          height: 64,
          borderRadius: '50%',
          backgroundColor: theme.palette.action.hover,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          mx: 'auto',
          mb: 2,
        }}
      >
        <QrCode2Icon sx={{ fontSize: 32, color: theme.palette.text.secondary }} />
      </Box>
      <Typography variant="h6" gutterBottom>
        Two-Factor Authentication
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3, maxWidth: 320, mx: 'auto' }}>
        Add an extra layer of security to your account by requiring a verification code in addition
        to your password.
      </Typography>
      <Button variant="contained" onClick={onSetup} sx={{ cursor: 'pointer' }}>
        Set Up 2FA
      </Button>
    </Paper>
  );
}
