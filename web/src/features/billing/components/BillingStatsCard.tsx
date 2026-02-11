import { Card, CardContent, Typography, Grid, Box, Chip, Divider, Stack } from '@mui/material';
import {
  TrendingUp as RevenueIcon,
  AccountBalance as BalanceIcon,
  Receipt as InvoiceIcon,
  LocalHospital as InsuranceIcon,
  Warning as OverdueIcon,
} from '@mui/icons-material';
import { CurrencyDisplay } from './BillingChips';
import type { BillingStatisticsResponse } from '../types/billing.types';

interface BillingStatsCardProps {
  stats: BillingStatisticsResponse;
  isLoading?: boolean;
}

interface StatItemProps {
  icon: React.ReactNode;
  label: string;
  value: React.ReactNode;
  color: string;
}

function StatItem({ icon, label, value, color }: StatItemProps) {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
      <Box
        sx={{
          p: 1,
          borderRadius: 1,
          bgcolor: `${color}14`,
          color,
          display: 'flex',
        }}
      >
        {icon}
      </Box>
      <Box>
        <Typography variant="caption" color="text.secondary" display="block">
          {label}
        </Typography>
        {value}
      </Box>
    </Box>
  );
}

export function BillingStatsCard({ stats, isLoading: _isLoading }: BillingStatsCardProps) {
  return (
    <Card>
      <CardContent>
        <Typography variant="h4" sx={{ mb: 3 }}>
          Billing Overview
        </Typography>

        <Grid container spacing={3}>
          {}
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <StatItem
              icon={<RevenueIcon />}
              label="Total Revenue"
              value={<CurrencyDisplay amount={stats.totalRevenue} size="large" color="success" />}
              color="#059669"
            />
          </Grid>

          {}
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <StatItem
              icon={<BalanceIcon />}
              label="Outstanding Balance"
              value={
                <CurrencyDisplay amount={stats.outstandingBalance} size="large" color="primary" />
              }
              color="#0891B2"
            />
          </Grid>

          {}
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <StatItem
              icon={<OverdueIcon />}
              label="Overdue Invoices"
              value={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Typography variant="h4" fontWeight={700} color="error.main">
                    {stats.overdueInvoices}
                  </Typography>
                  <Chip label="Requires Action" color="error" size="small" />
                </Box>
              }
              color="#DC2626"
            />
          </Grid>
        </Grid>

        <Divider sx={{ my: 3 }} />

        {}
        <Grid container spacing={2}>
          <Grid size={{ xs: 6, sm: 3 }}>
            <Box sx={{ textAlign: 'center', p: 1.5, bgcolor: 'grey.50', borderRadius: 1 }}>
              <Typography variant="h4" fontWeight={600}>
                {stats.totalInvoices}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Total Invoices
              </Typography>
            </Box>
          </Grid>
          <Grid size={{ xs: 6, sm: 3 }}>
            <Box sx={{ textAlign: 'center', p: 1.5, bgcolor: 'success.lighter', borderRadius: 1 }}>
              <Typography variant="h4" fontWeight={600} color="success.main">
                {stats.paidInvoices}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Paid
              </Typography>
            </Box>
          </Grid>
          <Grid size={{ xs: 6, sm: 3 }}>
            <Box sx={{ textAlign: 'center', p: 1.5, bgcolor: 'warning.lighter', borderRadius: 1 }}>
              <Typography variant="h4" fontWeight={600} color="warning.main">
                {stats.pendingClaims}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Pending Claims
              </Typography>
            </Box>
          </Grid>
          <Grid size={{ xs: 6, sm: 3 }}>
            <Box sx={{ textAlign: 'center', p: 1.5, bgcolor: 'error.lighter', borderRadius: 1 }}>
              <Typography variant="h4" fontWeight={600} color="error.main">
                {stats.deniedClaims}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Denied Claims
              </Typography>
            </Box>
          </Grid>
        </Grid>

        <Divider sx={{ my: 3 }} />

        {}
        <Typography variant="subtitle2" sx={{ mb: 2 }}>
          Payment Sources
        </Typography>
        <Stack direction="row" spacing={3}>
          <StatItem
            icon={<InsuranceIcon />}
            label="Insurance Payments"
            value={<CurrencyDisplay amount={stats.insurancePayments} size="medium" />}
            color="#7C3AED"
          />
          <StatItem
            icon={<InvoiceIcon />}
            label="Patient Payments"
            value={<CurrencyDisplay amount={stats.patientPayments} size="medium" />}
            color="#0891B2"
          />
        </Stack>
      </CardContent>
    </Card>
  );
}
