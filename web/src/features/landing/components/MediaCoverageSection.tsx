import { Box, Container, Typography, Stack, useTheme, alpha } from '@mui/material';

export function MediaCoverageSection() {
  const theme = useTheme();

  const mediaOutlets = [
    { name: 'Forbes', width: 100 },
    { name: 'TechCrunch', width: 130 },
    { name: 'Reuters', width: 100 },
    { name: 'Bloomberg', width: 120 },
    { name: 'CNBC', width: 80 },
    { name: 'WSJ', width: 60 },
  ];

  return (
    <Box component="section" py={8} bgcolor="background.paper">
      <Container maxWidth="lg">
        <Stack alignItems="center" spacing={4}>
          <Typography variant="body1" color="text.secondary" textAlign="center" fontWeight={500}>
            As Featured In
          </Typography>

          <Stack
            direction="row"
            flexWrap="wrap"
            justifyContent="center"
            alignItems="center"
            gap={{ xs: 4, md: 6 }}
          >
            {mediaOutlets.map((outlet, index) => (
              <Box
                key={index}
                sx={{
                  opacity: 0.4,
                  transition: 'all 0.3s ease',
                  cursor: 'pointer',
                  '&:hover': {
                    opacity: 0.8,
                  },
                }}
              >
                {}
                <Typography
                  variant="h5"
                  sx={{
                    fontWeight: 800,
                    letterSpacing: '-0.02em',
                    color: theme.palette.text.primary,
                    fontFamily: 'Georgia, serif',
                    minWidth: outlet.width,
                    textAlign: 'center',
                  }}
                >
                  {outlet.name}
                </Typography>
              </Box>
            ))}
          </Stack>

          <Box
            sx={{
              mt: 2,
              px: 4,
              py: 2,
              borderRadius: 3,
              bgcolor: alpha(theme.palette.primary.main, 0.05),
              border: `1px dashed ${alpha(theme.palette.primary.main, 0.2)}`,
            }}
          >
            <Typography
              variant="body2"
              color="text.secondary"
              textAlign="center"
              sx={{ fontStyle: 'italic' }}
            >
              "Revolutionizing healthcare access in the digital age"
              <Typography
                component="span"
                variant="body2"
                color="primary.main"
                sx={{ ml: 1, fontWeight: 600, fontStyle: 'normal' }}
              >
                — Forbes Health
              </Typography>
            </Typography>
          </Box>
        </Stack>
      </Container>
    </Box>
  );
}
