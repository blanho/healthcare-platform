import { Card, CardContent, Typography, Stack, Button, Box } from '@mui/material';
import {
  PersonAdd as PersonAddIcon,
  Event as EventIcon,
  Description as RecordIcon,
  Receipt as InvoiceIcon,
  LocalHospital as ProviderIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { RbacGuard } from '@/components/auth';
import type { Permission } from '@/types';

interface QuickAction {
  label: string;
  icon: React.ReactNode;
  route: string;
  permission: Permission;
  color: string;
}

const quickActions: QuickAction[] = [
  {
    label: 'New Patient',
    icon: <PersonAddIcon />,
    route: '/app/patients/new',
    permission: 'patient:write',
    color: '#0891B2',
  },
  {
    label: 'Schedule Appointment',
    icon: <EventIcon />,
    route: '/app/appointments/new',
    permission: 'appointment:write',
    color: '#059669',
  },
  {
    label: 'Create Record',
    icon: <RecordIcon />,
    route: '/app/medical-records/new',
    permission: 'medical_record:write',
    color: '#D97706',
  },
  {
    label: 'Create Invoice',
    icon: <InvoiceIcon />,
    route: '/app/billing/invoices/new',
    permission: 'billing:write',
    color: '#DC2626',
  },
  {
    label: 'Add Provider',
    icon: <ProviderIcon />,
    route: '/app/providers/new',
    permission: 'provider:write',
    color: '#7C3AED',
  },
];

export function QuickActions() {
  const navigate = useNavigate();

  return (
    <Card>
      <CardContent>
        <Typography variant="h3" sx={{ fontSize: '1.1rem', fontWeight: 600, mb: 2 }}>
          Quick Actions
        </Typography>
        <Stack direction="row" spacing={2} flexWrap="wrap" useFlexGap>
          {quickActions.map((action) => (
            <RbacGuard key={action.label} permission={action.permission}>
              <Button
                variant="outlined"
                startIcon={<Box sx={{ color: action.color }}>{action.icon}</Box>}
                onClick={() => navigate(action.route)}
                sx={{
                  borderColor: 'divider',
                  color: 'text.primary',
                  cursor: 'pointer',
                  '&:hover': {
                    borderColor: action.color,
                    backgroundColor: `${action.color}08`,
                  },
                }}
              >
                {action.label}
              </Button>
            </RbacGuard>
          ))}
        </Stack>
      </CardContent>
    </Card>
  );
}
