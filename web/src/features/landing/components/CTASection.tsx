import { Link } from 'react-router-dom';
import { Box, Container, Typography, Button, Stack, useTheme, alpha } from '@mui/material';
import { East as ArrowRightIcon } from '@mui/icons-material';

export function CTASection() {
  const theme = useTheme();

  return (
    <Box
      component="section"
      py={12}
      sx={{
        background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.primary.dark} 50%, ${theme.palette.secondary.dark} 100%)`,
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      {}
      <Box
        sx={{
          position: 'absolute',
          top: 0,
          left: 0,
          width: 400,
          height: 400,
          bgcolor: alpha('#fff', 0.1),
          borderRadius: '50%',
          filter: 'blur(80px)',
        }}
      />
      <Box
        sx={{
          position: 'absolute',
          bottom: 0,
          right: 0,
          width: 400,
          height: 400,
          bgcolor: alpha('#fff', 0.1),
          borderRadius: '50%',
          filter: 'blur(80px)',
        }}
      />

      <Container maxWidth="md" sx={{ position: 'relative', textAlign: 'center' }}>
        <Typography
          variant="h2"
          sx={{ color: 'white', fontSize: { xs: '2rem', md: '2.75rem' }, fontWeight: 700, mb: 3 }}
        >
          Ready to Transform Your Practice?
        </Typography>
        <Typography
          sx={{
            color: alpha('#fff', 0.85),
            fontSize: '1.125rem',
            mb: 5,
            maxWidth: 600,
            mx: 'auto',
          }}
        >
          Join thousands of healthcare providers who have already modernized their patient care
          experience.
        </Typography>

        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} justifyContent="center">
          <Button
            component={Link}
            to="/register"
            variant="contained"
            size="large"
            endIcon={<ArrowRightIcon />}
            sx={{
              py: 2,
              px: 5,
              borderRadius: 3,
              bgcolor: 'white',
              color: 'primary.main',
              fontWeight: 600,
              fontSize: '1rem',
              '&:hover': { bgcolor: alpha('#fff', 0.9) },
            }}
          >
            Start 14-Day Free Trial
          </Button>
          <Button
            variant="outlined"
            size="large"
            sx={{
              py: 2,
              px: 5,
              borderRadius: 3,
              borderColor: alpha('#fff', 0.4),
              color: 'white',
              fontWeight: 600,
              fontSize: '1rem',
              '&:hover': {
                borderColor: 'white',
                bgcolor: alpha('#fff', 0.1),
              },
            }}
          >
            Schedule a Demo
          </Button>
        </Stack>

        <Typography sx={{ color: alpha('#fff', 0.7), mt: 3, fontSize: '0.875rem' }}>
          No credit card required • Cancel anytime • Full HIPAA compliance
        </Typography>
      </Container>
    </Box>
  );
}
