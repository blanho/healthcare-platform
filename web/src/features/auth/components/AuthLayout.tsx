import { Box, Container, Grid, Typography, useTheme, alpha } from '@mui/material';
import type { ReactNode } from 'react';

interface AuthLayoutProps {
  children: ReactNode;
  showSidePanel?: boolean;
}

export function AuthLayout({ children, showSidePanel = true }: AuthLayoutProps) {
  const theme = useTheme();

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        bgcolor: '#F0FDFA',
      }}
    >
      <Grid container sx={{ flex: 1 }}>
        {}
        {showSidePanel && (
          <Grid
            size={{ xs: 12, lg: 6 }}
            sx={{
              display: { xs: 'none', lg: 'flex' },
              position: 'relative',
              overflow: 'hidden',
              background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.primary.dark} 100%)`,
            }}
          >
            <AuthSidePanel />
          </Grid>
        )}

        {}
        <Grid
          size={{ xs: 12, lg: showSidePanel ? 6 : 12 }}
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <Container maxWidth="sm" sx={{ py: 4 }}>
            {children}
          </Container>
        </Grid>
      </Grid>
    </Box>
  );
}

function AuthSidePanel() {
  const features = [
    {
      title: 'Book Appointments',
      description: 'Schedule visits with top doctors in minutes',
    },
    {
      title: 'Video Consultations',
      description: 'Connect with specialists from home',
    },
    {
      title: 'Health Records',
      description: 'Access your medical history anytime',
    },
    {
      title: 'Secure & Private',
      description: 'HIPAA-compliant data protection',
    },
  ];

  return (
    <Box
      sx={{
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        height: '100%',
        p: 6,
        color: 'white',
      }}
    >
      {}
      <Box
        sx={{
          position: 'absolute',
          inset: 0,
          opacity: 0.1,
          backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.4'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
        }}
      />

      {}
      <Box
        sx={{
          position: 'absolute',
          top: -100,
          right: -100,
          width: 400,
          height: 400,
          borderRadius: '50%',
          background: alpha('#ffffff', 0.05),
        }}
      />
      <Box
        sx={{
          position: 'absolute',
          bottom: -150,
          left: -100,
          width: 500,
          height: 500,
          borderRadius: '50%',
          background: alpha('#ffffff', 0.03),
        }}
      />

      {}
      <Box sx={{ position: 'relative', zIndex: 1 }}>
        <Typography variant="h3" fontWeight={800} sx={{ mb: 2, lineHeight: 1.2 }}>
          Your Health,
          <br />
          Our Priority
        </Typography>
        <Typography
          variant="h6"
          sx={{
            opacity: 0.9,
            fontWeight: 400,
            mb: 6,
            maxWidth: 400,
          }}
        >
          Join millions of patients who trust us for their healthcare needs.
        </Typography>

        {}
        <Box component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
          {features.map((feature, index) => (
            <Box
              key={index}
              component="li"
              sx={{
                display: 'flex',
                alignItems: 'flex-start',
                gap: 2,
                mb: 3,
              }}
            >
              <Box
                sx={{
                  width: 32,
                  height: 32,
                  borderRadius: 2,
                  bgcolor: alpha('#ffffff', 0.2),
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0,
                  fontSize: '1rem',
                }}
              >
                ✓
              </Box>
              <Box>
                <Typography variant="subtitle1" fontWeight={600}>
                  {feature.title}
                </Typography>
                <Typography variant="body2" sx={{ opacity: 0.8 }}>
                  {feature.description}
                </Typography>
              </Box>
            </Box>
          ))}
        </Box>
      </Box>

      {}
      <Box
        sx={{
          position: 'absolute',
          bottom: 32,
          left: 48,
          right: 48,
        }}
      >
        <Typography variant="body2" sx={{ opacity: 0.7 }}>
          Trusted by 4M+ patients • 2,500+ doctors • 100+ hospitals
        </Typography>
      </Box>
    </Box>
  );
}
