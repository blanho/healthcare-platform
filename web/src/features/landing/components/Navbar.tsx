import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Avatar,
  Stack,
  Box,
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  useTheme,
  alpha,
  Divider,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Close as CloseIcon,
  LocalHospital as HospitalIcon,
  Phone as PhoneIcon,
} from '@mui/icons-material';

export function Navbar() {
  const theme = useTheme();
  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 50);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const navItems = [
    { label: 'Find Doctors', href: '#doctors' },
    { label: 'Services', href: '#services' },
    { label: 'Health Packages', href: '#packages' },
    { label: 'About Us', href: '#about' },
  ];

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  return (
    <>
      <AppBar
        position="fixed"
        elevation={isScrolled ? 2 : 0}
        sx={{
          top: { xs: 0, md: isScrolled ? 0 : 16 },
          left: { xs: 0, md: isScrolled ? 0 : 16 },
          right: { xs: 0, md: isScrolled ? 0 : 16 },
          width: { xs: '100%', md: isScrolled ? '100%' : 'auto' },
          borderRadius: { xs: 0, md: isScrolled ? 0 : 4 },
          transition: 'all 0.3s ease',
          bgcolor: isScrolled
            ? alpha(theme.palette.background.paper, 0.98)
            : alpha(theme.palette.background.paper, 0.9),
          backdropFilter: 'blur(20px)',
          border: isScrolled ? 'none' : `1px solid ${alpha(theme.palette.primary.main, 0.1)}`,
        }}
      >
        <Toolbar sx={{ justifyContent: 'space-between', px: { xs: 2, md: 3 }, py: 1 }}>
          {}
          <NavbarBrand />

          {}
          <Stack direction="row" spacing={1} sx={{ display: { xs: 'none', lg: 'flex' } }}>
            {navItems.map((item) => (
              <Button
                key={item.label}
                href={item.href}
                sx={{
                  color: 'text.primary',
                  fontWeight: 500,
                  px: 2,
                  borderRadius: 2,
                  '&:hover': {
                    bgcolor: alpha(theme.palette.primary.main, 0.08),
                    color: 'primary.main',
                  },
                }}
              >
                {item.label}
              </Button>
            ))}
          </Stack>

          {}
          <Stack
            direction="row"
            spacing={2}
            alignItems="center"
            sx={{ display: { xs: 'none', md: 'flex' } }}
          >
            {}
            <Stack direction="row" alignItems="center" spacing={1}>
              <Box
                sx={{
                  width: 36,
                  height: 36,
                  borderRadius: 2,
                  bgcolor: alpha(theme.palette.primary.main, 0.1),
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <PhoneIcon sx={{ fontSize: 18, color: 'primary.main' }} />
              </Box>
              <Box>
                <Typography
                  variant="caption"
                  color="text.secondary"
                  display="block"
                  lineHeight={1.2}
                >
                  Hotline 24/7
                </Typography>
                <Typography variant="subtitle2" fontWeight={700} color="primary.main">
                  1900-1234
                </Typography>
              </Box>
            </Stack>

            <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />

            <Button
              component={Link}
              to="/login"
              sx={{
                fontWeight: 600,
                color: 'text.primary',
                '&:hover': { bgcolor: alpha(theme.palette.primary.main, 0.08) },
              }}
            >
              Sign In
            </Button>
            <Button
              component={Link}
              to="/register"
              variant="contained"
              sx={{
                borderRadius: 2.5,
                px: 3,
                py: 1,
                fontWeight: 600,
                boxShadow: `0 4px 14px ${alpha(theme.palette.primary.main, 0.35)}`,
              }}
            >
              Get Started
            </Button>
          </Stack>

          {}
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{
              display: { md: 'none' },
              color: 'text.primary',
            }}
          >
            <MenuIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

      {}
      <Drawer
        anchor="right"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        sx={{
          display: { md: 'none' },
          '& .MuiDrawer-paper': {
            boxSizing: 'border-box',
            width: 300,
            bgcolor: 'background.paper',
          },
        }}
      >
        <MobileMenu navItems={navItems} onClose={handleDrawerToggle} />
      </Drawer>
    </>
  );
}

function NavbarBrand() {
  const theme = useTheme();

  return (
    <Link to="/" style={{ textDecoration: 'none' }}>
      <Stack direction="row" spacing={1.5} alignItems="center" sx={{ cursor: 'pointer' }}>
        <Avatar
          sx={{
            width: 42,
            height: 42,
            background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
            boxShadow: `0 4px 12px ${alpha(theme.palette.primary.main, 0.3)}`,
          }}
        >
          <HospitalIcon sx={{ fontSize: 24 }} />
        </Avatar>
        <Box>
          <Typography
            variant="h6"
            fontWeight={800}
            sx={{
              color: '#134E4A',
              lineHeight: 1.1,
              letterSpacing: '-0.02em',
            }}
          >
            HealthCare
          </Typography>
          <Typography
            variant="caption"
            sx={{
              color: 'primary.main',
              fontWeight: 600,
              letterSpacing: '0.05em',
            }}
          >
            BOOKING PLATFORM
          </Typography>
        </Box>
      </Stack>
    </Link>
  );
}

interface MobileMenuProps {
  navItems: { label: string; href: string }[];
  onClose: () => void;
}

function MobileMenu({ navItems, onClose }: MobileMenuProps) {
  const theme = useTheme();

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {}
      <Stack
        direction="row"
        justifyContent="space-between"
        alignItems="center"
        sx={{ p: 2, borderBottom: `1px solid ${theme.palette.divider}` }}
      >
        <NavbarBrand />
        <IconButton onClick={onClose} sx={{ color: 'text.primary' }}>
          <CloseIcon />
        </IconButton>
      </Stack>

      {}
      <List sx={{ flex: 1, py: 2 }}>
        {navItems.map((item) => (
          <ListItem key={item.label} disablePadding>
            <ListItemButton
              href={item.href}
              onClick={onClose}
              sx={{
                py: 1.5,
                px: 3,
                '&:hover': { bgcolor: alpha(theme.palette.primary.main, 0.08) },
              }}
            >
              <ListItemText primary={item.label} primaryTypographyProps={{ fontWeight: 500 }} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      {}
      <Box sx={{ p: 3, borderTop: `1px solid ${theme.palette.divider}` }}>
        <Stack spacing={2}>
          {}
          <Stack direction="row" alignItems="center" spacing={2}>
            <Box
              sx={{
                width: 44,
                height: 44,
                borderRadius: 2,
                bgcolor: alpha(theme.palette.primary.main, 0.1),
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <PhoneIcon sx={{ color: 'primary.main' }} />
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary">
                Hotline 24/7
              </Typography>
              <Typography variant="h6" fontWeight={700} color="primary.main">
                1900-1234
              </Typography>
            </Box>
          </Stack>

          {}
          <Button
            component={Link}
            to="/login"
            fullWidth
            variant="outlined"
            onClick={onClose}
            sx={{ borderRadius: 2, py: 1.25, fontWeight: 600 }}
          >
            Sign In
          </Button>
          <Button
            component={Link}
            to="/register"
            fullWidth
            variant="contained"
            onClick={onClose}
            sx={{ borderRadius: 2, py: 1.25, fontWeight: 600 }}
          >
            Get Started
          </Button>
        </Stack>
      </Box>
    </Box>
  );
}
