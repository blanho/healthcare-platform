import { Box, Container, Typography, Grid, Avatar, Stack, alpha } from '@mui/material';
import {
  Favorite as HeartIcon,
  Security as ShieldIcon,
  Lock as LockIcon,
} from '@mui/icons-material';

interface FooterLinks {
  [category: string]: string[];
}

export function Footer() {
  const links: FooterLinks = {
    Product: ['Features', 'Pricing', 'Integrations', 'API', 'Security'],
    Company: ['About', 'Careers', 'Blog', 'Press', 'Partners'],
    Resources: ['Documentation', 'Help Center', 'Webinars', 'Case Studies', 'Status'],
    Legal: ['Privacy', 'Terms', 'HIPAA', 'BAA', 'Cookie Policy'],
  };

  return (
    <Box component="footer" py={8} sx={{ bgcolor: '#0F172A', color: 'white' }}>
      <Container maxWidth="lg">
        <Grid container spacing={4} mb={6}>
          <Grid size={{ xs: 12, md: 3 }}>
            <FooterBrand />
          </Grid>

          {Object.entries(links).map(([category, items]) => (
            <Grid key={category} size={{ xs: 6, sm: 3, md: 2.25 }}>
              <FooterLinkColumn category={category} items={items} />
            </Grid>
          ))}
        </Grid>

        <FooterBottom />
      </Container>
    </Box>
  );
}

function FooterBrand() {
  return (
    <>
      <Stack direction="row" spacing={1} alignItems="center" mb={2}>
        <Avatar sx={{ bgcolor: 'primary.main', width: 40, height: 40 }}>
          <HeartIcon />
        </Avatar>
        <Typography variant="h6" fontWeight={700}>
          HealthCare
        </Typography>
      </Stack>
      <Typography variant="body2" sx={{ color: alpha('#fff', 0.6) }}>
        Modern healthcare platform for modern practices.
      </Typography>
    </>
  );
}

interface FooterLinkColumnProps {
  category: string;
  items: string[];
}

function FooterLinkColumn({ category, items }: FooterLinkColumnProps) {
  return (
    <>
      <Typography fontWeight={600} mb={2}>
        {category}
      </Typography>
      <Stack spacing={1.5}>
        {items.map((item) => (
          <Typography
            key={item}
            component="a"
            href="#"
            variant="body2"
            sx={{
              color: alpha('#fff', 0.6),
              textDecoration: 'none',
              cursor: 'pointer',
              transition: 'color 0.2s',
              '&:hover': { color: 'white' },
            }}
          >
            {item}
          </Typography>
        ))}
      </Stack>
    </>
  );
}

function FooterBottom() {
  return (
    <Box
      sx={{
        borderTop: `1px solid ${alpha('#fff', 0.1)}`,
        pt: 4,
        display: 'flex',
        flexDirection: { xs: 'column', md: 'row' },
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: 2,
      }}
    >
      <Typography variant="body2" sx={{ color: alpha('#fff', 0.5) }}>
        © 2026 HealthCare Platform. All rights reserved.
      </Typography>
      <Stack direction="row" spacing={3}>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ color: alpha('#fff', 0.5) }}>
          <ShieldIcon fontSize="small" />
          <Typography variant="body2">HIPAA Compliant</Typography>
        </Stack>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ color: alpha('#fff', 0.5) }}>
          <LockIcon fontSize="small" />
          <Typography variant="body2">SOC 2 Certified</Typography>
        </Stack>
      </Stack>
    </Box>
  );
}
