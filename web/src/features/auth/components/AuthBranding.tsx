import { Box, Avatar, Typography, useTheme, alpha } from '@mui/material';
import { LocalHospital as HospitalIcon } from '@mui/icons-material';
import { Link } from 'react-router-dom';

interface AuthBrandingProps {
  title: string;
  subtitle?: string;
}

export function AuthBranding({ title, subtitle }: AuthBrandingProps) {
  const theme = useTheme();

  return (
    <Box sx={{ textAlign: 'center', mb: 4 }}>
      <Link to="/" style={{ textDecoration: 'none' }}>
        <Avatar
          sx={{
            width: 56,
            height: 56,
            mx: 'auto',
            mb: 2,
            background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
            boxShadow: `0 8px 24px ${alpha(theme.palette.primary.main, 0.3)}`,
            cursor: 'pointer',
            transition: 'transform 0.2s ease',
            '&:hover': {
              transform: 'scale(1.05)',
            },
          }}
        >
          <HospitalIcon sx={{ fontSize: 32 }} />
        </Avatar>
      </Link>
      <Typography variant="h4" component="h1" fontWeight={700} sx={{ color: '#134E4A' }}>
        {title}
      </Typography>
      {subtitle && (
        <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
          {subtitle}
        </Typography>
      )}
    </Box>
  );
}
