import { Box, Container, Typography, Stack, Avatar, useTheme, alpha } from '@mui/material';

interface Partner {
  name: string;
  shortName: string;
}

export function PartnerHospitalsSection() {
  const theme = useTheme();

  const partners: Partner[] = [
    { name: 'University Medical Center', shortName: 'UMC' },
    { name: 'Dermatology Hospital', shortName: 'DL' },
    { name: 'Trưng Vương Hospital', shortName: 'TV' },
    { name: 'Children Hospital 1', shortName: 'CH1' },
    { name: 'Eye Hospital', shortName: 'EYE' },
    { name: 'Orthopedic Hospital', shortName: 'ORT' },
    { name: 'District 2 Hospital', shortName: 'D2H' },
    { name: 'Thống Nhất Hospital', shortName: 'TN' },
  ];

  return (
    <Box component="section" py={6} bgcolor="background.paper">
      <Container maxWidth="lg">
        <Stack spacing={4}>
          <Typography
            variant="overline"
            textAlign="center"
            sx={{
              color: 'text.secondary',
              letterSpacing: 2,
              fontWeight: 600,
            }}
          >
            Trusted Partners & Collaborators
          </Typography>

          {}
          <Box
            sx={{
              display: 'flex',
              gap: 4,
              overflowX: 'auto',
              py: 2,
              px: 1,
              scrollbarWidth: 'none',
              '&::-webkit-scrollbar': { display: 'none' },
              maskImage:
                'linear-gradient(to right, transparent, black 10%, black 90%, transparent)',
            }}
          >
            {[...partners, ...partners].map((partner, index) => (
              <Box
                key={index}
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: 1,
                  minWidth: 100,
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                  '&:hover': {
                    transform: 'scale(1.05)',
                    '& .partner-avatar': {
                      borderColor: theme.palette.primary.main,
                    },
                  },
                }}
              >
                <Avatar
                  className="partner-avatar"
                  sx={{
                    width: 64,
                    height: 64,
                    bgcolor: alpha(theme.palette.primary.main, 0.1),
                    color: 'primary.main',
                    fontWeight: 700,
                    fontSize: '1.25rem',
                    border: `2px solid ${theme.palette.divider}`,
                    transition: 'border-color 0.2s ease',
                  }}
                >
                  {partner.shortName}
                </Avatar>
                <Typography
                  variant="caption"
                  textAlign="center"
                  sx={{
                    maxWidth: 100,
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                    color: 'text.secondary',
                  }}
                >
                  {partner.name}
                </Typography>
              </Box>
            ))}
          </Box>
        </Stack>
      </Container>
    </Box>
  );
}
