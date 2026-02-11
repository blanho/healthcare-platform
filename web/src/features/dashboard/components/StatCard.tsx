import { Box, Card, CardContent, Typography, Skeleton } from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
} from '@mui/icons-material';

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle: string;
  icon: React.ReactNode;
  color: string;
  trend?: number;
  isLoading?: boolean;
  onClick?: () => void;
}

export function StatCard({
  title,
  value,
  subtitle,
  icon,
  color,
  trend,
  isLoading = false,
  onClick,
}: StatCardProps) {
  const trendIsPositive = trend && trend > 0;
  const trendColor = trendIsPositive ? 'success.main' : 'error.main';

  return (
    <Card
      onClick={onClick}
      sx={{
        cursor: onClick ? 'pointer' : 'default',
        transition: 'transform 200ms ease, box-shadow 200ms ease',
        height: '100%',
        '&:hover': onClick ? { transform: 'translateY(-2px)', boxShadow: 3 } : undefined,
      }}
    >
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <Box sx={{ flex: 1 }}>
            <Typography variant="overline" color="text.secondary" sx={{ letterSpacing: 1 }}>
              {title}
            </Typography>
            {isLoading ? (
              <Skeleton variant="text" width={80} height={48} />
            ) : (
              <Typography variant="h1" sx={{ my: 0.5, fontSize: '2rem', fontWeight: 600 }}>
                {value}
              </Typography>
            )}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              {isLoading ? (
                <Skeleton variant="text" width={100} height={20} />
              ) : (
                <>
                  {trend !== undefined && (
                    <Box
                      sx={{
                        display: 'flex',
                        alignItems: 'center',
                        color: trendColor,
                      }}
                    >
                      {trendIsPositive ? (
                        <TrendingUpIcon sx={{ fontSize: 16 }} />
                      ) : (
                        <TrendingDownIcon sx={{ fontSize: 16 }} />
                      )}
                      <Typography variant="caption" sx={{ fontWeight: 600, ml: 0.25 }}>
                        {Math.abs(trend)}%
                      </Typography>
                    </Box>
                  )}
                  <Typography variant="caption" color="text.secondary">
                    {subtitle}
                  </Typography>
                </>
              )}
            </Box>
          </Box>
          <Box
            sx={{
              p: 1.5,
              borderRadius: 2,
              backgroundColor: `${color}14`,
              color,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
}
