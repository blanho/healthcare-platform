import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button } from '@mui/material';
import { SearchOff as NotFoundIcon } from '@mui/icons-material';

export function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        px: 3,
      }}
    >
      <NotFoundIcon sx={{ fontSize: 80, color: 'text.disabled', mb: 2 }} />
      <Typography variant="h1" sx={{ mb: 1 }}>
        404
      </Typography>
      <Typography variant="h3" sx={{ mb: 1 }}>
        Page Not Found
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 400 }}>
        The page you're looking for doesn't exist or has been moved.
      </Typography>
      <Button variant="contained" onClick={() => navigate('/')} sx={{ cursor: 'pointer' }}>
        Back to Dashboard
      </Button>
    </Box>
  );
}
