import { Box, Typography, Button, type SxProps } from '@mui/material';
import { SearchOff as EmptyIcon } from '@mui/icons-material';

interface EmptyStateProps {
  title?: string;
  message?: string;
  icon?: React.ReactNode;
  actionLabel?: string;
  onAction?: () => void;
  sx?: SxProps;
}

export function EmptyState({
  title = 'No results found',
  message = 'Try adjusting your search or filters.',
  icon,
  actionLabel,
  onAction,
  sx,
}: EmptyStateProps) {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        py: 8,
        px: 3,
        textAlign: 'center',
        ...sx,
      }}
    >
      <Box sx={{ color: 'text.disabled', mb: 2 }}>
        {icon ?? <EmptyIcon sx={{ fontSize: 64 }} />}
      </Box>
      <Typography variant="h3" sx={{ mb: 1 }}>
        {title}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ maxWidth: 360 }}>
        {message}
      </Typography>
      {actionLabel && onAction && (
        <Button
          variant="contained"
          onClick={onAction}
          sx={{ mt: 3, cursor: 'pointer' }}
        >
          {actionLabel}
        </Button>
      )}
    </Box>
  );
}
