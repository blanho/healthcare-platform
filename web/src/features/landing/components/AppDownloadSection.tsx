import { Box, Container, Typography, Grid, Button, Stack, useTheme, alpha } from '@mui/material';
import {
  Apple as AppleIcon,
  Shop as PlayStoreIcon,
  QrCode2 as QrCodeIcon,
  Star as StarIcon,
  Download as DownloadIcon,
} from '@mui/icons-material';

export function AppDownloadSection() {
  const theme = useTheme();

  const features = [
    'Book appointments in seconds',
    'Video consultation from home',
    'Digital health records',
    'Medicine reminders & tracking',
    'Family health management',
    '24/7 customer support',
  ];

  return (
    <Box
      component="section"
      py={10}
      sx={{
        background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${alpha(theme.palette.primary.dark, 0.95)} 100%)`,
        position: 'relative',
        overflow: 'hidden',
      }}
    >
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

      <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 1 }}>
        <Grid container spacing={6} alignItems="center">
          {}
          <Grid size={{ xs: 12, md: 7 }}>
            <Stack spacing={4}>
              <Box>
                <Typography variant="h3" fontWeight={800} sx={{ color: 'white', mb: 2 }}>
                  Download Our App
                </Typography>
                <Typography
                  variant="h6"
                  sx={{
                    color: alpha('#ffffff', 0.9),
                    fontWeight: 400,
                    lineHeight: 1.6,
                  }}
                >
                  Get the full healthcare experience on your mobile device. Book appointments,
                  consult doctors, and manage your health records all in one place.
                </Typography>
              </Box>

              {}
              <Grid container spacing={1.5}>
                {features.map((feature, index) => (
                  <Grid key={index} size={{ xs: 12, sm: 6 }}>
                    <Stack direction="row" alignItems="center" spacing={1.5}>
                      <Box
                        sx={{
                          width: 24,
                          height: 24,
                          borderRadius: '50%',
                          bgcolor: alpha('#ffffff', 0.2),
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                        }}
                      >
                        <StarIcon sx={{ fontSize: 14, color: 'white' }} />
                      </Box>
                      <Typography variant="body2" sx={{ color: alpha('#ffffff', 0.9) }}>
                        {feature}
                      </Typography>
                    </Stack>
                  </Grid>
                ))}
              </Grid>

              {}
              <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                <Button
                  variant="contained"
                  size="large"
                  startIcon={<AppleIcon />}
                  sx={{
                    bgcolor: 'white',
                    color: 'primary.main',
                    fontWeight: 600,
                    borderRadius: 2,
                    px: 3,
                    py: 1.5,
                    '&:hover': {
                      bgcolor: alpha('#ffffff', 0.9),
                    },
                  }}
                >
                  <Box textAlign="left">
                    <Typography variant="caption" sx={{ display: 'block', lineHeight: 1 }}>
                      Download on the
                    </Typography>
                    <Typography variant="subtitle1" fontWeight={700}>
                      App Store
                    </Typography>
                  </Box>
                </Button>

                <Button
                  variant="contained"
                  size="large"
                  startIcon={<PlayStoreIcon />}
                  sx={{
                    bgcolor: 'white',
                    color: 'primary.main',
                    fontWeight: 600,
                    borderRadius: 2,
                    px: 3,
                    py: 1.5,
                    '&:hover': {
                      bgcolor: alpha('#ffffff', 0.9),
                    },
                  }}
                >
                  <Box textAlign="left">
                    <Typography variant="caption" sx={{ display: 'block', lineHeight: 1 }}>
                      Get it on
                    </Typography>
                    <Typography variant="subtitle1" fontWeight={700}>
                      Google Play
                    </Typography>
                  </Box>
                </Button>
              </Stack>

              {}
              <Stack direction="row" spacing={4}>
                <Stack direction="row" alignItems="center" spacing={1}>
                  <DownloadIcon sx={{ color: alpha('#ffffff', 0.7) }} />
                  <Box>
                    <Typography variant="h6" fontWeight={700} sx={{ color: 'white' }}>
                      500K+
                    </Typography>
                    <Typography variant="caption" sx={{ color: alpha('#ffffff', 0.7) }}>
                      Downloads
                    </Typography>
                  </Box>
                </Stack>
                <Stack direction="row" alignItems="center" spacing={1}>
                  <StarIcon sx={{ color: '#FBBF24' }} />
                  <Box>
                    <Typography variant="h6" fontWeight={700} sx={{ color: 'white' }}>
                      4.8
                    </Typography>
                    <Typography variant="caption" sx={{ color: alpha('#ffffff', 0.7) }}>
                      App Rating
                    </Typography>
                  </Box>
                </Stack>
              </Stack>
            </Stack>
          </Grid>

          {}
          <Grid size={{ xs: 12, md: 5 }}>
            <Stack alignItems="center" spacing={3}>
              {}
              <Box
                sx={{
                  bgcolor: 'white',
                  borderRadius: 4,
                  p: 4,
                  textAlign: 'center',
                  boxShadow: `0 24px 64px ${alpha('#000000', 0.3)}`,
                }}
              >
                <Box
                  sx={{
                    width: 200,
                    height: 200,
                    borderRadius: 2,
                    bgcolor: alpha(theme.palette.primary.main, 0.05),
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    mb: 2,
                    border: `2px solid ${theme.palette.divider}`,
                  }}
                >
                  <QrCodeIcon
                    sx={{
                      fontSize: 160,
                      color: theme.palette.text.primary,
                    }}
                  />
                </Box>
                <Typography variant="subtitle1" fontWeight={600}>
                  Scan to Download
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Point your camera at the QR code
                </Typography>
              </Box>
            </Stack>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}
