

import { useState } from 'react';
import { Box, Paper, Typography, Button, Grid, Alert, Divider, useTheme } from '@mui/material';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import DownloadIcon from '@mui/icons-material/Download';
import CheckIcon from '@mui/icons-material/Check';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';

interface BackupCodesListProps {
  codes: string[];
  message?: string;
  onDone?: () => void;
}

export function BackupCodesList({ codes, message, onDone }: BackupCodesListProps) {
  const theme = useTheme();
  const [copied, setCopied] = useState(false);
  const [downloaded, setDownloaded] = useState(false);

  const handleCopyAll = async () => {
    try {
      const text = codes.join('\n');
      await navigator.clipboard.writeText(text);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      console.error('Failed to copy codes');
    }
  };

  const handleDownload = () => {
    const text = [
      'Healthcare Platform - Backup Codes',
      '='.repeat(40),
      '',
      'Keep these codes safe. Each code can only be used once.',
      '',
      ...codes.map((code, i) => `${i + 1}. ${code}`),
      '',
      `Generated: ${new Date().toISOString()}`,
    ].join('\n');

    const blob = new Blob([text], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'healthcare-backup-codes.txt';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    setDownloaded(true);
  };

  return (
    <Box>
      {}
      <Alert severity="warning" icon={<WarningAmberIcon />} sx={{ mb: 3, borderRadius: 2 }}>
        <Typography variant="body2" fontWeight={500}>
          Save these backup codes
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {message || 'Store these codes securely. Each code can only be used once.'}
        </Typography>
      </Alert>

      {}
      <Paper
        variant="outlined"
        sx={{
          p: 3,
          borderRadius: 3,
          backgroundColor: theme.palette.background.default,
        }}
      >
        <Grid container spacing={1}>
          {codes.map((code, index) => (
            <Grid key={index} size={{ xs: 6 }}>
              <Box
                sx={{
                  py: 1,
                  px: 2,
                  backgroundColor: theme.palette.action.hover,
                  borderRadius: 1,
                  fontFamily: 'monospace',
                  fontSize: '0.95rem',
                  fontWeight: 500,
                  letterSpacing: '0.05em',
                  textAlign: 'center',
                }}
              >
                {code}
              </Box>
            </Grid>
          ))}
        </Grid>

        <Divider sx={{ my: 2 }} />

        {}
        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
          <Button
            variant="outlined"
            size="small"
            startIcon={copied ? <CheckIcon /> : <ContentCopyIcon />}
            onClick={handleCopyAll}
            color={copied ? 'success' : 'primary'}
            sx={{ cursor: 'pointer' }}
          >
            {copied ? 'Copied!' : 'Copy All'}
          </Button>
          <Button
            variant="outlined"
            size="small"
            startIcon={downloaded ? <CheckIcon /> : <DownloadIcon />}
            onClick={handleDownload}
            color={downloaded ? 'success' : 'primary'}
            sx={{ cursor: 'pointer' }}
          >
            {downloaded ? 'Downloaded!' : 'Download'}
          </Button>
        </Box>
      </Paper>

      {}
      {onDone && (
        <Box sx={{ mt: 3, textAlign: 'center' }}>
          <Alert severity="info" sx={{ mb: 2, borderRadius: 2 }}>
            <Typography variant="body2">
              Make sure you've saved these codes before continuing. You won't be able to see them
              again.
            </Typography>
          </Alert>
          <Button
            variant="contained"
            onClick={onDone}
            disabled={!copied && !downloaded}
            sx={{ cursor: 'pointer' }}
          >
            I've Saved My Codes
          </Button>
          {!copied && !downloaded && (
            <Typography variant="caption" color="text.secondary" display="block" sx={{ mt: 1 }}>
              Please copy or download your codes first
            </Typography>
          )}
        </Box>
      )}
    </Box>
  );
}
