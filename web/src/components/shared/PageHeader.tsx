import { Box, Typography, Breadcrumbs, Link as MuiLink, type SxProps } from '@mui/material';
import { Link } from 'react-router-dom';
import { NavigateNext as NavigateNextIcon } from '@mui/icons-material';

interface BreadcrumbItem {
  label: string;
  href?: string;
}

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  breadcrumbs?: BreadcrumbItem[];
  action?: React.ReactNode;
  sx?: SxProps;
}

export function PageHeader({ title, subtitle, breadcrumbs, action, sx }: PageHeaderProps) {
  return (
    <Box sx={{ mb: 3, ...sx }}>
      {breadcrumbs && breadcrumbs.length > 0 && (
        <Breadcrumbs
          separator={<NavigateNextIcon fontSize="small" />}
          sx={{ mb: 1 }}
          aria-label="breadcrumb"
        >
          {breadcrumbs.map((crumb, index) =>
            crumb.href ? (
              <MuiLink
                key={index}
                component={Link}
                to={crumb.href}
                underline="hover"
                color="text.secondary"
                sx={{ cursor: 'pointer' }}
              >
                {crumb.label}
              </MuiLink>
            ) : (
              <Typography key={index} color="text.primary" variant="body2">
                {crumb.label}
              </Typography>
            ),
          )}
        </Breadcrumbs>
      )}

      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box>
          <Typography variant="h1" component="h1">
            {title}
          </Typography>
          {subtitle && (
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              {subtitle}
            </Typography>
          )}
        </Box>
        {action && <Box sx={{ ml: 2, flexShrink: 0 }}>{action}</Box>}
      </Box>
    </Box>
  );
}
