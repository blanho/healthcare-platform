import { Box, Typography, LinearProgress, Stack, useTheme } from '@mui/material';
import { checkPasswordStrength, type PasswordStrength } from '../utils/password.utils';

interface PasswordStrengthMeterProps {
  password: string;
  showFeedback?: boolean;
}

export function PasswordStrengthMeter({
  password,
  showFeedback = true,
}: PasswordStrengthMeterProps) {
  const theme = useTheme();

  if (!password) return null;

  const strength: PasswordStrength = checkPasswordStrength(password);
  const progress = (strength.score / 4) * 100;

  return (
    <Box sx={{ mt: 1 }}>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 0.5 }}>
        <Typography variant="caption" color="text.secondary">
          Password strength
        </Typography>
        <Typography variant="caption" fontWeight={600} sx={{ color: strength.color }}>
          {strength.label}
        </Typography>
      </Stack>

      <LinearProgress
        variant="determinate"
        value={progress}
        sx={{
          height: 6,
          borderRadius: 3,
          bgcolor: theme.palette.grey[200],
          '& .MuiLinearProgress-bar': {
            bgcolor: strength.color,
            borderRadius: 3,
            transition: 'all 0.3s ease',
          },
        }}
      />

      {showFeedback && strength.feedback.length > 0 && (
        <Stack spacing={0.5} sx={{ mt: 1 }}>
          {strength.feedback.map((tip, index) => (
            <Typography
              key={index}
              variant="caption"
              color="text.secondary"
              sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}
            >
              <Box
                component="span"
                sx={{
                  width: 4,
                  height: 4,
                  borderRadius: '50%',
                  bgcolor: 'text.disabled',
                }}
              />
              {tip}
            </Typography>
          ))}
        </Stack>
      )}
    </Box>
  );
}
