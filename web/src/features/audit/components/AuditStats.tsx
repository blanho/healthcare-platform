

import {
  Box,
  Card,
  CardContent,
  Typography,
  Skeleton,
  Stack,
  LinearProgress,
  Tooltip,
} from '@mui/material';
import {
  Security,
  VerifiedUser,
  Warning,
  Error as ErrorIcon,
  Shield,
  TrendingUp,
  TrendingDown,
} from '@mui/icons-material';
import type { DailyAuditSummary, MonthlyAuditSummary } from '../types/audit.types';

interface StatCardProps {
  title: string;
  value: number | string;
  subtitle?: string;
  icon: React.ReactNode;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info';
  trend?: { value: number; label: string };
  loading?: boolean;
}

function StatCard({
  title,
  value,
  subtitle,
  icon,
  color = 'primary',
  trend,
  loading,
}: StatCardProps) {
  const colorMap = {
    primary: 'primary.main',
    success: 'success.main',
    warning: 'warning.main',
    error: 'error.main',
    info: 'info.main',
  };

  return (
    <Card variant="outlined" sx={{ height: '100%' }}>
      <CardContent>
        <Box
          sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}
        >
          <Typography variant="body2" color="text.secondary">
            {title}
          </Typography>
          <Box sx={{ color: colorMap[color], display: 'flex', alignItems: 'center' }}>{icon}</Box>
        </Box>

        {loading ? (
          <Skeleton variant="text" width={100} height={40} />
        ) : (
          <Typography variant="h4" component="div" fontWeight={600}>
            {typeof value === 'number' ? value.toLocaleString() : value}
          </Typography>
        )}

        {subtitle && (
          <Typography variant="caption" color="text.secondary">
            {subtitle}
          </Typography>
        )}

        {trend && (
          <Box sx={{ display: 'flex', alignItems: 'center', mt: 0.5, gap: 0.5 }}>
            {trend.value >= 0 ? (
              <TrendingUp fontSize="small" color="success" />
            ) : (
              <TrendingDown fontSize="small" color="error" />
            )}
            <Typography variant="caption" color={trend.value >= 0 ? 'success.main' : 'error.main'}>
              {trend.value >= 0 ? '+' : ''}
              {trend.value}% {trend.label}
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );
}

interface DailyAuditStatsProps {
  summary?: DailyAuditSummary;
  loading?: boolean;
}

export function DailyAuditStats({ summary, loading }: DailyAuditStatsProps) {
  const successRate = summary ? Math.round((summary.successCount / summary.totalEvents) * 100) : 0;

  return (
    <Box
      sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 2 }}
    >
      <StatCard
        title="Total Events"
        value={summary?.totalEvents ?? 0}
        subtitle={`${summary?.uniqueUsers ?? 0} unique users`}
        icon={<Security />}
        loading={loading}
      />

      <StatCard
        title="Successful"
        value={summary?.successCount ?? 0}
        subtitle={`${successRate}% success rate`}
        icon={<VerifiedUser />}
        color="success"
        loading={loading}
      />

      <StatCard
        title="Failed"
        value={summary?.failureCount ?? 0}
        icon={<Warning />}
        color="warning"
        loading={loading}
      />

      <StatCard
        title="Access Denied"
        value={summary?.deniedCount ?? 0}
        icon={<ErrorIcon />}
        color="error"
        loading={loading}
      />

      <StatCard
        title="Critical Events"
        value={summary?.criticalEvents ?? 0}
        icon={<Shield />}
        color={summary?.criticalEvents && summary.criticalEvents > 0 ? 'error' : 'info'}
        loading={loading}
      />
    </Box>
  );
}

interface MonthlyAuditStatsProps {
  summary?: MonthlyAuditSummary;
  loading?: boolean;
}

export function MonthlyAuditStats({ summary, loading }: MonthlyAuditStatsProps) {
  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Monthly Overview
          {summary && (
            <Typography component="span" variant="body2" color="text.secondary" sx={{ ml: 1 }}>
              {new Date(summary.year, summary.month - 1).toLocaleString('default', {
                month: 'long',
                year: 'numeric',
              })}
            </Typography>
          )}
        </Typography>

        {loading ? (
          <Stack spacing={2}>
            {[1, 2, 3, 4].map((i) => (
              <Skeleton key={i} variant="rectangular" height={40} />
            ))}
          </Stack>
        ) : summary ? (
          <Stack spacing={2}>
            {}
            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                <Typography variant="body2">Total Events</Typography>
                <Typography variant="body2" fontWeight={600}>
                  {summary.totalEvents.toLocaleString()}
                </Typography>
              </Box>
              <Typography variant="caption" color="text.secondary">
                Avg: {Math.round(summary.dailyAverageEvents).toLocaleString()}/day
              </Typography>
            </Box>

            {}
            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                <Typography variant="body2">PHI Access Events</Typography>
                <Typography variant="body2" fontWeight={600}>
                  {summary.phiAccessCount.toLocaleString()}
                </Typography>
              </Box>
            </Box>

            {}
            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                <Typography variant="body2">Security Incidents</Typography>
                <Typography
                  variant="body2"
                  fontWeight={600}
                  color={summary.securityIncidents > 0 ? 'error.main' : 'success.main'}
                >
                  {summary.securityIncidents}
                </Typography>
              </Box>
            </Box>

            {}
            <Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                <Typography variant="body2">Compliance Score</Typography>
                <Typography variant="body2" fontWeight={600}>
                  {summary.complianceScore}%
                </Typography>
              </Box>
              <Tooltip title={`${summary.complianceScore}% compliant`}>
                <LinearProgress
                  variant="determinate"
                  value={summary.complianceScore}
                  color={
                    summary.complianceScore >= 90
                      ? 'success'
                      : summary.complianceScore >= 70
                        ? 'warning'
                        : 'error'
                  }
                  sx={{ height: 8, borderRadius: 4 }}
                />
              </Tooltip>
            </Box>
          </Stack>
        ) : (
          <Typography color="text.secondary">No data available</Typography>
        )}
      </CardContent>
    </Card>
  );
}

interface TopActionsSummaryProps {
  actions?: Array<{ action: string; count: number }>;
  loading?: boolean;
}

export function TopActionsSummary({ actions, loading }: TopActionsSummaryProps) {
  const maxCount = actions ? Math.max(...actions.map((a) => a.count)) : 0;

  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Top Actions
        </Typography>

        {loading ? (
          <Stack spacing={1.5}>
            {[1, 2, 3, 4, 5].map((i) => (
              <Skeleton key={i} variant="rectangular" height={32} />
            ))}
          </Stack>
        ) : actions && actions.length > 0 ? (
          <Stack spacing={1.5}>
            {actions.slice(0, 5).map((action) => (
              <Box key={action.action}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                  <Typography variant="body2">{action.action.replace('_', ' ')}</Typography>
                  <Typography variant="body2" fontWeight={500}>
                    {action.count.toLocaleString()}
                  </Typography>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={(action.count / maxCount) * 100}
                  sx={{ height: 6, borderRadius: 3 }}
                />
              </Box>
            ))}
          </Stack>
        ) : (
          <Typography color="text.secondary">No data available</Typography>
        )}
      </CardContent>
    </Card>
  );
}

export default DailyAuditStats;
