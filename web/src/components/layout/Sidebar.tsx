import { useLocation, useNavigate } from 'react-router-dom';
import {
  Box,
  Drawer,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Typography,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  LocalHospital as ProviderIcon,
  CalendarMonth as AppointmentIcon,
  Description as RecordIcon,
  Receipt as BillingIcon,
  Notifications as NotificationIcon,
  AdminPanelSettings as AdminIcon,
  ChevronLeft as CollapseIcon,
  ChevronRight as ExpandIcon,
  MonitorHeart as LogoIcon,
} from '@mui/icons-material';
import { useSidebarStore, useAuthStore } from '@/stores';

const SIDEBAR_WIDTH = 260;
const SIDEBAR_COLLAPSED_WIDTH = 72;

interface NavItem {
  label: string;
  path: string;
  icon: React.ReactNode;
  permission?: string;
  role?: string;
}

const navItems: NavItem[] = [
  { label: 'Dashboard', path: '/', icon: <DashboardIcon /> },
  { label: 'Patients', path: '/patients', icon: <PeopleIcon />, permission: 'patient:read' },
  { label: 'Providers', path: '/providers', icon: <ProviderIcon />, permission: 'provider:read' },
  {
    label: 'Appointments',
    path: '/appointments',
    icon: <AppointmentIcon />,
    permission: 'appointment:read',
  },
  {
    label: 'Medical Records',
    path: '/medical-records',
    icon: <RecordIcon />,
    permission: 'medical_record:read',
  },
  { label: 'Billing', path: '/billing', icon: <BillingIcon />, permission: 'billing:read' },
  {
    label: 'Notifications',
    path: '/notifications',
    icon: <NotificationIcon />,
    permission: 'notification:read',
  },
  { label: 'Administration', path: '/admin', icon: <AdminIcon />, role: 'ROLE_ADMIN' },
];

export function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const { isCollapsed, setCollapsed } = useSidebarStore();
  const { hasPermission, hasRole } = useAuthStore();

  const width = isCollapsed ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_WIDTH;

  const visibleItems = navItems.filter((item) => {
    if (item.permission && !hasPermission(item.permission as never)) return false;
    if (item.role && !hasRole(item.role as never)) return false;
    return true;
  });

  return (
    <Drawer
      variant="permanent"
      sx={{
        width,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width,
          transition: 'width 200ms ease',
          overflowX: 'hidden',
        },
      }}
    >
      {}
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 1.5,
          px: 2,
          py: 2,
          minHeight: 64,
        }}
      >
        <LogoIcon sx={{ color: 'primary.main', fontSize: 32 }} />
        {!isCollapsed && (
          <Typography
            variant="h6"
            sx={{
              fontWeight: 700,
              color: 'primary.main',
              whiteSpace: 'nowrap',
              fontSize: '1rem',
            }}
          >
            HealthCare
          </Typography>
        )}
      </Box>

      <Divider />

      {}
      <List sx={{ px: 1, py: 1, flexGrow: 1 }}>
        {visibleItems.map((item) => {
          const isActive =
            item.path === '/'
              ? location.pathname === '/'
              : location.pathname.startsWith(item.path);

          const button = (
            <ListItemButton
              key={item.path}
              onClick={() => navigate(item.path)}
              selected={isActive}
              sx={{
                borderRadius: 2,
                mb: 0.5,
                minHeight: 44,
                px: isCollapsed ? 2 : 2,
                justifyContent: isCollapsed ? 'center' : 'flex-start',
                '&.Mui-selected': {
                  backgroundColor: 'primary.main',
                  color: 'white',
                  '& .MuiListItemIcon-root': { color: 'white' },
                  '&:hover': {
                    backgroundColor: 'primary.dark',
                  },
                },
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: isCollapsed ? 0 : 40,
                  justifyContent: 'center',
                  color: isActive ? 'inherit' : 'text.secondary',
                }}
              >
                {item.icon}
              </ListItemIcon>
              {!isCollapsed && (
                <ListItemText
                  primary={item.label}
                  primaryTypographyProps={{
                    fontSize: '0.875rem',
                    fontWeight: isActive ? 600 : 400,
                  }}
                />
              )}
            </ListItemButton>
          );

          return isCollapsed ? (
            <Tooltip key={item.path} title={item.label} placement="right" arrow>
              {button}
            </Tooltip>
          ) : (
            button
          );
        })}
      </List>

      <Divider />

      {}
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 1 }}>
        <IconButton
          onClick={() => setCollapsed(!isCollapsed)}
          size="small"
          sx={{ cursor: 'pointer' }}
          aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {isCollapsed ? <ExpandIcon /> : <CollapseIcon />}
        </IconButton>
      </Box>
    </Drawer>
  );
}
