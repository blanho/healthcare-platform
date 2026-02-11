

import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Paper,
  Card,
  CardContent,
  Grid,
  Button,
  TablePagination,
  Alert,
  CircularProgress,
  Skeleton,
  Stack,
  Divider,
  LinearProgress,
} from '@mui/material';
import { ArrowBack, Person, Security, Warning } from '@mui/icons-material';
import { useState } from 'react';
import { AuditEventCard } from '../components/AuditEventCard';
import {
  useUserAuditTrail,
  useUserActivitySummary,
  useUserFailedLogins,
  useUserAnomalyCheck,
} from '../hooks/useAudit';
import { subDays, format } from 'date-fns';

export function UserAuditTrailPage() {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);

  const dateRange = {
    startDate: format(subDays(new Date(), 30), 'yyyy-MM-dd'),
    endDate: format(new Date(), 'yyyy-MM-dd'),
  };

  const {
    data: trailData,
    isLoading: trailLoading,
    error: trailError,
  } = useUserAuditTrail(userId!, { ...dateRange, page, size });

  const { data: activitySummary, isLoading: summaryLoading } = useUserActivitySummary(
    userId!,
    dateRange,
  );

  const { data: failedLogins } = useUserFailedLogins(userId!, 7);
  const { data: hasAnomaly } = useUserAnomalyCheck(userId!, 24);

  const handlePageChange = (_: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleRowsPerPageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSize(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleViewResource = (category: string, resourceId: string) => {
    const routeMap: Record<string, string> = {
      PATIENT: `/app/patients/${resourceId}`,
      MEDICAL_RECORD: `/app/medical-records/${resourceId}`,
      APPOINTMENT: `/app/appointments/${resourceId}`,
    };
    const route = routeMap[category];
    if (route) navigate(route);
  };

  const handleViewPatient = (patientId: string) => {
    navigate(`/app/patients/${patientId}`);
  };

  if (!userId) {
    return <Alert severity="error">User ID is required</Alert>;
  }

  return (
    <Box sx={{ p: 3 }}>
      {}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Button startIcon={<ArrowBack />} onClick={() => navigate('/app/audit')}>
          Back to Audit Logs
        </Button>
      </Box>

      <Typography variant="h4" component="h1" fontWeight={600} gutterBottom>
        User Audit Trail
      </Typography>

      {}
      {hasAnomaly && (
        <Alert severity="warning" icon={<Warning />} sx={{ mb: 3 }}>
          Anomalous activity detected for this user in the last 24 hours. Review the audit trail for
          suspicious patterns.
        </Alert>
      )}

      {}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        {}
        <Grid size={{ xs: 12, md: 6, lg: 4 }}>
          <Card variant="outlined" sx={{ height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <Person color="primary" />
                <Typography variant="h6">Activity Summary</Typography>
              </Box>

              {summaryLoading ? (
                <Stack spacing={1}>
                  {[1, 2, 3, 4].map((i) => (
                    <Skeleton key={i} variant="rectangular" height={24} />
                  ))}
                </Stack>
              ) : activitySummary ? (
                <Stack spacing={1}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="body2" color="text.secondary">
                      Username
                    </Typography>
                    <Typography variant="body2" fontWeight={500}>
                      {activitySummary.username}
                    </Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="body2" color="text.secondary">
                      Total Events
                    </Typography>
                    <Typography variant="body2" fontWeight={500}>
                      {activitySummary.totalEvents.toLocaleString()}
                    </Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="body2" color="text.secondary">
                      Successful
                    </Typography>
                    <Typography variant="body2" color="success.main">
                      {activitySummary.successfulEvents.toLocaleString()}
                    </Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="body2" color="text.secondary">
                      Failed
                    </Typography>
                    <Typography variant="body2" color="warning.main">
                      {activitySummary.failedEvents}
                    </Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="body2" color="text.secondary">
                      Denied
                    </Typography>
                    <Typography variant="body2" color="error.main">
                      {activitySummary.deniedEvents}
                    </Typography>
                  </Box>
                  <Divider />
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="body2" color="text.secondary">
                      Last Activity
                    </Typography>
                    <Typography variant="body2">
                      {new Date(activitySummary.lastActivityAt).toLocaleString()}
                    </Typography>
                  </Box>
                </Stack>
              ) : (
                <Typography color="text.secondary">No activity data</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        {}
        <Grid size={{ xs: 12, md: 6, lg: 4 }}>
          <Card variant="outlined" sx={{ height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <Security color="error" />
                <Typography variant="h6">Security</Typography>
              </Box>

              <Stack spacing={2}>
                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                    <Typography variant="body2" color="text.secondary">
                      Failed Logins (7 days)
                    </Typography>
                    <Typography
                      variant="body2"
                      fontWeight={500}
                      color={failedLogins && failedLogins > 3 ? 'error.main' : 'text.primary'}
                    >
                      {failedLogins ?? 0}
                    </Typography>
                  </Box>
                  {failedLogins !== undefined && failedLogins > 3 && (
                    <Alert severity="warning" sx={{ py: 0.5 }}>
                      Above normal threshold
                    </Alert>
                  )}
                </Box>

                <Box>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Anomaly Detection (24h)
                  </Typography>
                  {hasAnomaly ? (
                    <Alert severity="error" sx={{ py: 0.5 }}>
                      Anomalous activity detected
                    </Alert>
                  ) : (
                    <Alert severity="success" sx={{ py: 0.5 }}>
                      No anomalies detected
                    </Alert>
                  )}
                </Box>
              </Stack>
            </CardContent>
          </Card>
        </Grid>

        {}
        <Grid size={{ xs: 12, lg: 4 }}>
          <Card variant="outlined" sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Top Actions
              </Typography>

              {summaryLoading ? (
                <Stack spacing={1}>
                  {[1, 2, 3].map((i) => (
                    <Skeleton key={i} variant="rectangular" height={32} />
                  ))}
                </Stack>
              ) : activitySummary?.topActions?.length ? (
                <Stack spacing={1.5}>
                  {activitySummary.topActions.slice(0, 5).map((action) => {
                    const maxCount = activitySummary.topActions[0]?.count ?? 1;
                    return (
                      <Box key={action.action}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                          <Typography variant="body2">{action.action.replace('_', ' ')}</Typography>
                          <Typography variant="body2" fontWeight={500}>
                            {action.count}
                          </Typography>
                        </Box>
                        <LinearProgress
                          variant="determinate"
                          value={(action.count / maxCount) * 100}
                          sx={{ height: 6, borderRadius: 3 }}
                        />
                      </Box>
                    );
                  })}
                </Stack>
              ) : (
                <Typography color="text.secondary">No action data</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {}
      <Paper variant="outlined" sx={{ p: 2 }}>
        <Typography variant="h6" gutterBottom>
          Audit Trail (Last 30 Days)
        </Typography>

        {trailError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            Failed to load audit trail.
          </Alert>
        )}

        {trailLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        )}

        {!trailLoading && trailData && (
          <>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              Showing {trailData.content.length} of {trailData.page.totalElements.toLocaleString()}{' '}
              events
            </Typography>

            {trailData.content.length > 0 ? (
              <Box>
                {trailData.content.map((event) => (
                  <AuditEventCard
                    key={event.id}
                    event={event as any}
                    onViewResource={handleViewResource}
                    onViewPatient={handleViewPatient}
                  />
                ))}
              </Box>
            ) : (
              <Box sx={{ textAlign: 'center', py: 4 }}>
                <Typography color="text.secondary">No audit events found for this user.</Typography>
              </Box>
            )}

            <TablePagination
              component="div"
              count={trailData.page.totalElements}
              page={page}
              onPageChange={handlePageChange}
              rowsPerPage={size}
              onRowsPerPageChange={handleRowsPerPageChange}
              rowsPerPageOptions={[10, 20, 50, 100]}
            />
          </>
        )}
      </Paper>
    </Box>
  );
}

export default UserAuditTrailPage;
