import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button } from '@mui/material';
import { Block as BlockIcon } from '@mui/icons-material';

export function UnauthorizedPage() {
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
      <BlockIcon sx={{ fontSize: 80, color: 'error.main', mb: 2 }} />
      <Typography variant="h1" sx={{ mb: 1 }}>
        403
      </Typography>
      <Typography variant="h3" sx={{ mb: 1 }}>
        Access Denied
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 400 }}>
        You don't have permission to access this resource. Contact your administrator if you believe this is an error.
      </Typography>
      <Button variant="contained" onClick={() => navigate('/')} sx={{ cursor: 'pointer' }}>
        Back to Dashboard
      </Button>
    </Box>
  );
}
