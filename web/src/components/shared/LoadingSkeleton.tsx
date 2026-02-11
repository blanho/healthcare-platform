import { Box, Skeleton, Card, CardContent, Stack } from '@mui/material';

interface LoadingSkeletonProps {
  variant?: 'list' | 'detail' | 'form' | 'cards';
  count?: number;
}

function ListSkeleton({ count = 5 }: { count: number }) {
  return (
    <Stack spacing={1}>
      <Skeleton variant="rectangular" height={52} sx={{ borderRadius: 1 }} />
      {Array.from({ length: count }).map((_, i) => (
        <Skeleton key={i} variant="rectangular" height={48} sx={{ borderRadius: 1 }} />
      ))}
    </Stack>
  );
}

function DetailSkeleton() {
  return (
    <Stack spacing={3}>
      <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
        <Skeleton variant="circular" width={64} height={64} />
        <Box sx={{ flex: 1 }}>
          <Skeleton width="40%" height={32} />
          <Skeleton width="25%" height={20} />
        </Box>
      </Box>
      <Card>
        <CardContent>
          <Stack spacing={2}>
            {Array.from({ length: 6 }).map((_, i) => (
              <Box key={i} sx={{ display: 'flex', gap: 4 }}>
                <Skeleton width={120} height={20} />
                <Skeleton width="50%" height={20} />
              </Box>
            ))}
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
}

function FormSkeleton() {
  return (
    <Card>
      <CardContent>
        <Stack spacing={3}>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Skeleton variant="rectangular" height={40} sx={{ flex: 1, borderRadius: 1 }} />
            <Skeleton variant="rectangular" height={40} sx={{ flex: 1, borderRadius: 1 }} />
          </Box>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Skeleton variant="rectangular" height={40} sx={{ flex: 1, borderRadius: 1 }} />
            <Skeleton variant="rectangular" height={40} sx={{ flex: 1, borderRadius: 1 }} />
          </Box>
          <Skeleton variant="rectangular" height={40} sx={{ borderRadius: 1 }} />
          <Skeleton variant="rectangular" height={80} sx={{ borderRadius: 1 }} />
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
            <Skeleton variant="rectangular" width={100} height={40} sx={{ borderRadius: 1 }} />
            <Skeleton variant="rectangular" width={100} height={40} sx={{ borderRadius: 1 }} />
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}

function CardsSkeleton({ count = 4 }: { count: number }) {
  return (
    <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 2 }}>
      {Array.from({ length: count }).map((_, i) => (
        <Card key={i}>
          <CardContent>
            <Skeleton width="60%" height={24} sx={{ mb: 1 }} />
            <Skeleton width="80%" height={16} />
            <Skeleton width="40%" height={16} />
            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between' }}>
              <Skeleton width={80} height={28} sx={{ borderRadius: 1 }} />
              <Skeleton width={60} height={28} sx={{ borderRadius: 1 }} />
            </Box>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
}

export function LoadingSkeleton({ variant = 'list', count = 5 }: LoadingSkeletonProps) {
  switch (variant) {
    case 'detail':
      return <DetailSkeleton />;
    case 'form':
      return <FormSkeleton />;
    case 'cards':
      return <CardsSkeleton count={count} />;
    default:
      return <ListSkeleton count={count} />;
  }
}
