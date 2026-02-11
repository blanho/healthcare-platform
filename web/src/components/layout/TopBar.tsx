import { useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Avatar,
  Box,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  Badge,
  Divider,
  Tooltip,
} from '@mui/material';
import {
  Notifications as NotificationsIcon,
  Person as PersonIcon,
  Logout as LogoutIcon,
  Settings as SettingsIcon,
} from '@mui/icons-material';
import { useState } from 'react';
import { useAuthStore } from '@/stores';

export function TopBar() {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleLogout = () => {
    setAnchorEl(null);
    logout();
    navigate('/login');
  };

  const initials = user
    ? `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase()
    : '?';

  return (
    <AppBar position="sticky" sx={{ zIndex: (t) => t.zIndex.drawer - 1 }}>
      <Toolbar sx={{ justifyContent: 'flex-end', gap: 1 }}>
        {}
        <Tooltip title="Notifications" arrow>
          <IconButton
            onClick={() => navigate('/notifications')}
            sx={{ cursor: 'pointer' }}
            aria-label="Notifications"
          >
            <Badge badgeContent={0} color="error">
              <NotificationsIcon />
            </Badge>
          </IconButton>
        </Tooltip>

        {}
        <Box
          onClick={(e) => setAnchorEl(e.currentTarget)}
          sx={{
            display: 'flex',
            alignItems: 'center',
            gap: 1,
            cursor: 'pointer',
            borderRadius: 2,
            px: 1,
            py: 0.5,
            '&:hover': { backgroundColor: 'action.hover' },
            transition: 'background-color 150ms ease',
          }}
          role="button"
          tabIndex={0}
          aria-label="User menu"
        >
          <Avatar
            sx={{
              width: 36,
              height: 36,
              bgcolor: 'primary.main',
              fontSize: '0.875rem',
            }}
          >
            {initials}
          </Avatar>
          <Box sx={{ display: { xs: 'none', sm: 'block' } }}>
            <Typography variant="body2" fontWeight={600} lineHeight={1.2}>
              {user?.fullName || 'User'}
            </Typography>
            <Typography variant="caption" color="text.secondary" lineHeight={1}>
              {user?.roles?.[0]?.replace('ROLE_', '') || 'Staff'}
            </Typography>
          </Box>
        </Box>

        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={() => setAnchorEl(null)}
          transformOrigin={{ horizontal: 'right', vertical: 'top' }}
          anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          slotProps={{
            paper: {
              sx: { width: 220, mt: 1 },
            },
          }}
        >
          <MenuItem onClick={() => { setAnchorEl(null); navigate('/profile'); }}>
            <ListItemIcon><PersonIcon fontSize="small" /></ListItemIcon>
            <ListItemText>Profile</ListItemText>
          </MenuItem>
          <MenuItem onClick={() => { setAnchorEl(null); navigate('/settings'); }}>
            <ListItemIcon><SettingsIcon fontSize="small" /></ListItemIcon>
            <ListItemText>Settings</ListItemText>
          </MenuItem>
          <Divider />
          <MenuItem onClick={handleLogout}>
            <ListItemIcon><LogoutIcon fontSize="small" /></ListItemIcon>
            <ListItemText>Logout</ListItemText>
          </MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
}
