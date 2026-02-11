

import { useState } from 'react';
import {
  Container,
  Card,
  CardContent,
  CardHeader,
  Button,
  Alert,
  Stack,
  Typography,
  CircularProgress,
  Box,
  Divider,
  Tabs,
  Tab,
  Skeleton,
} from '@mui/material';
import DevicesIcon from '@mui/icons-material/Devices';
import HistoryIcon from '@mui/icons-material/History';
import LogoutIcon from '@mui/icons-material/Logout';
import {
  useSessions,
  useSessionCount,
  useLoginHistory,
  useRevokeSession,
  useRevokeOtherSessions,
  useRevokeAllSessions,
} from '../hooks/useSessions';
import { SessionCard, SessionsEmptyState, LoginHistoryItem } from '../components';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel({ children, value, index, ...other }: TabPanelProps) {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`sessions-tabpanel-${index}`}
      aria-labelledby={`sessions-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );
}

export function SessionsPage() {
  const [tabValue, setTabValue] = useState(0);
  const [confirmRevokeAll, setConfirmRevokeAll] = useState(false);

  const { data: sessions, isLoading: sessionsLoading, error: sessionsError } = useSessions();
  const { data: sessionCount } = useSessionCount();
  const { data: loginHistory, isLoading: historyLoading } = useLoginHistory(20);

  const revokeMutation = useRevokeSession();
  const revokeOthersMutation = useRevokeOtherSessions();
  const revokeAllMutation = useRevokeAllSessions();

  const handleRevokeSession = (sessionId: string) => {
    revokeMutation.mutate(sessionId);
  };

  const handleRevokeOthers = () => {
    revokeOthersMutation.mutate();
  };

  const handleRevokeAll = () => {
    if (confirmRevokeAll) {
      revokeAllMutation.mutate();
    } else {
      setConfirmRevokeAll(true);
      setTimeout(() => setConfirmRevokeAll(false), 5000);
    }
  };

  const otherSessionsCount = sessions?.filter((s) => !s.current).length || 0;

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Card
        elevation={0}
        sx={{
          borderRadius: 4,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
          border: '1px solid',
          borderColor: 'divider',
        }}
      >
        <CardHeader
          avatar={
            <Box
              sx={{
                width: 48,
                height: 48,
                borderRadius: '50%',
                backgroundColor: 'primary.light',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <DevicesIcon sx={{ color: 'primary.main' }} />
            </Box>
          }
          title={
            <Typography variant="h6" fontWeight={600}>
              Sessions & Security
            </Typography>
          }
          subheader={
            sessionCount
              ? `${sessionCount.count} active session${sessionCount.count !== 1 ? 's' : ''}`
              : 'Manage your active sessions'
          }
          action={
            otherSessionsCount > 0 && (
              <Button
                variant="outlined"
                color="error"
                size="small"
                startIcon={<LogoutIcon />}
                onClick={handleRevokeOthers}
                disabled={revokeOthersMutation.isPending}
                sx={{ cursor: 'pointer' }}
              >
                {revokeOthersMutation.isPending ? (
                  <CircularProgress size={16} />
                ) : (
                  `Sign out other devices (${otherSessionsCount})`
                )}
              </Button>
            )
          }
        />
        <Divider />
        <CardContent sx={{ p: 0 }}>
          <Tabs
            value={tabValue}
            onChange={(_, newValue) => setTabValue(newValue)}
            aria-label="sessions tabs"
            sx={{ px: 3, borderBottom: 1, borderColor: 'divider' }}
          >
            <Tab
              icon={<DevicesIcon fontSize="small" />}
              iconPosition="start"
              label="Active Sessions"
              sx={{ cursor: 'pointer' }}
            />
            <Tab
              icon={<HistoryIcon fontSize="small" />}
              iconPosition="start"
              label="Login History"
              sx={{ cursor: 'pointer' }}
            />
          </Tabs>

          {}
          <TabPanel value={tabValue} index={0}>
            <Box sx={{ px: 3 }}>
              {revokeOthersMutation.isSuccess && (
                <Alert severity="success" sx={{ mb: 3, borderRadius: 2 }}>
                  All other sessions have been signed out.
                </Alert>
              )}

              {sessionsError && (
                <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
                  Failed to load sessions. Please try again.
                </Alert>
              )}

              {sessionsLoading ? (
                <Stack spacing={2}>
                  {[1, 2, 3].map((i) => (
                    <Skeleton key={i} variant="rounded" height={80} />
                  ))}
                </Stack>
              ) : sessions && sessions.length > 0 ? (
                <Stack spacing={2}>
                  {sessions.map((session) => (
                    <SessionCard
                      key={session.id}
                      session={session}
                      onRevoke={handleRevokeSession}
                      isRevoking={revokeMutation.isPending}
                    />
                  ))}
                </Stack>
              ) : (
                <SessionsEmptyState />
              )}

              {}
              <Box sx={{ mt: 4, pt: 3, borderTop: 1, borderColor: 'divider' }}>
                <Alert severity="warning" sx={{ mb: 2, borderRadius: 2 }}>
                  <Typography variant="body2" fontWeight={500}>
                    Sign out of all devices
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    This will sign you out of all devices, including this one.
                  </Typography>
                </Alert>
                <Button
                  variant={confirmRevokeAll ? 'contained' : 'outlined'}
                  color="error"
                  onClick={handleRevokeAll}
                  disabled={revokeAllMutation.isPending}
                  sx={{ cursor: 'pointer' }}
                >
                  {revokeAllMutation.isPending ? (
                    <CircularProgress size={24} color="inherit" />
                  ) : confirmRevokeAll ? (
                    'Click again to confirm'
                  ) : (
                    'Sign out everywhere'
                  )}
                </Button>
              </Box>
            </Box>
          </TabPanel>

          {}
          <TabPanel value={tabValue} index={1}>
            <Box sx={{ px: 3 }}>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                Recent login activity for your account
              </Typography>

              {historyLoading ? (
                <Stack spacing={2}>
                  {[1, 2, 3, 4, 5].map((i) => (
                    <Skeleton key={i} variant="rounded" height={64} />
                  ))}
                </Stack>
              ) : loginHistory && loginHistory.length > 0 ? (
                <Stack spacing={2}>
                  {loginHistory.map((attempt) => (
                    <LoginHistoryItem key={attempt.id} attempt={attempt} />
                  ))}
                </Stack>
              ) : (
                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{ textAlign: 'center', py: 4 }}
                >
                  No login history available
                </Typography>
              )}
            </Box>
          </TabPanel>
        </CardContent>
      </Card>
    </Container>
  );
}
