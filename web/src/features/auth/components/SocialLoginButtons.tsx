import { Stack, Divider, Button, Typography, alpha, useTheme } from '@mui/material';
import { Google as GoogleIcon } from '@mui/icons-material';

interface SocialLoginButtonsProps {
  onGoogleLogin?: () => void;
  disabled?: boolean;
  mode?: 'login' | 'register';
}

export function SocialLoginButtons({
  onGoogleLogin,
  disabled,
  mode = 'login',
}: SocialLoginButtonsProps) {
  const theme = useTheme();

  const actionText = mode === 'login' ? 'Sign in' : 'Sign up';

  return (
    <Stack spacing={2}>
      <Divider>
        <Typography variant="body2" color="text.secondary" sx={{ px: 2 }}>
          or {actionText.toLowerCase()} with
        </Typography>
      </Divider>

      <Button
        variant="outlined"
        fullWidth
        size="large"
        startIcon={<GoogleIcon />}
        disabled={disabled}
        onClick={onGoogleLogin}
        sx={{
          borderRadius: 2,
          py: 1.25,
          borderColor: theme.palette.divider,
          color: 'text.primary',
          fontWeight: 500,
          cursor: 'pointer',
          '&:hover': {
            borderColor: theme.palette.primary.main,
            bgcolor: alpha(theme.palette.primary.main, 0.04),
          },
        }}
      >
        {actionText} with Google
      </Button>
    </Stack>
  );
}
