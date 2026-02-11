
import { createTheme, type ThemeOptions } from '@mui/material/styles';

const palette = {
  primary: {
    main: '#0891B2',
    light: '#22D3EE',
    dark: '#0E7490',
    contrastText: '#FFFFFF',
  },
  secondary: {
    main: '#059669',
    light: '#34D399',
    dark: '#047857',
    contrastText: '#FFFFFF',
  },
  error: {
    main: '#DC2626',
    light: '#F87171',
    dark: '#B91C1C',
  },
  warning: {
    main: '#D97706',
    light: '#FBBF24',
    dark: '#B45309',
  },
  info: {
    main: '#0284C7',
    light: '#38BDF8',
    dark: '#0369A1',
  },
  success: {
    main: '#059669',
    light: '#34D399',
    dark: '#047857',
  },
  background: {
    default: '#F8FAFB',
    paper: '#FFFFFF',
  },
  text: {
    primary: '#164E63',
    secondary: '#475569',
    disabled: '#94A3B8',
  },
  divider: '#E2E8F0',
};

const themeOptions: ThemeOptions = {
  palette,
  typography: {
    fontFamily: '"Figtree", "Noto Sans", -apple-system, BlinkMacSystemFont, sans-serif',
    h1: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 700,
      fontSize: '2rem',
      lineHeight: 1.3,
      letterSpacing: '-0.01em',
    },
    h2: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 600,
      fontSize: '1.5rem',
      lineHeight: 1.35,
    },
    h3: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 600,
      fontSize: '1.25rem',
      lineHeight: 1.4,
    },
    h4: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 600,
      fontSize: '1.125rem',
    },
    h5: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 500,
      fontSize: '1rem',
    },
    h6: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 500,
      fontSize: '0.875rem',
    },
    body1: {
      fontFamily: '"Noto Sans", sans-serif',
      fontSize: '0.9375rem',
      lineHeight: 1.6,
    },
    body2: {
      fontFamily: '"Noto Sans", sans-serif',
      fontSize: '0.8125rem',
      lineHeight: 1.5,
    },
    subtitle1: {
      fontFamily: '"Noto Sans", sans-serif',
      fontWeight: 500,
      fontSize: '0.9375rem',
    },
    subtitle2: {
      fontFamily: '"Noto Sans", sans-serif',
      fontWeight: 500,
      fontSize: '0.8125rem',
    },
    button: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 600,
      textTransform: 'none',
      letterSpacing: '0.02em',
    },
    caption: {
      fontFamily: '"Noto Sans", sans-serif',
      fontSize: '0.75rem',
      color: '#475569',
    },
    overline: {
      fontFamily: '"Figtree", sans-serif',
      fontWeight: 600,
      fontSize: '0.6875rem',
      letterSpacing: '0.08em',
      textTransform: 'uppercase',
    },
  },
  shape: {
    borderRadius: 10,
  },
  shadows: [
    'none',
    '0 1px 2px 0 rgba(0,0,0,0.05)',
    '0 1px 3px 0 rgba(0,0,0,0.08), 0 1px 2px -1px rgba(0,0,0,0.04)',
    '0 4px 6px -1px rgba(0,0,0,0.08), 0 2px 4px -2px rgba(0,0,0,0.04)',
    '0 10px 15px -3px rgba(0,0,0,0.08), 0 4px 6px -4px rgba(0,0,0,0.04)',
    '0 20px 25px -5px rgba(0,0,0,0.08), 0 8px 10px -6px rgba(0,0,0,0.04)',

    ...Array(19).fill('0 20px 25px -5px rgba(0,0,0,0.08), 0 8px 10px -6px rgba(0,0,0,0.04)'),
  ] as ThemeOptions['shadows'],
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        '*': { boxSizing: 'border-box' },
        body: {
          backgroundColor: '#F8FAFB',
          color: '#164E63',
        },
        ':focus-visible': {
          outline: '3px solid #0891B2',
          outlineOffset: '2px',
          borderRadius: '4px',
        },
      },
    },
    MuiButton: {
      defaultProps: {
        disableElevation: true,
      },
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '8px 20px',
          minHeight: 40,
          transition: 'all 200ms ease',
          cursor: 'pointer',
        },
        contained: {
          '&:hover': {
            transform: 'translateY(-1px)',
            boxShadow: '0 4px 12px rgba(8, 145, 178, 0.3)',
          },
        },
        outlined: {
          borderWidth: 1.5,
          '&:hover': {
            borderWidth: 1.5,
            backgroundColor: 'rgba(8, 145, 178, 0.04)',
          },
        },
      },
    },
    MuiCard: {
      defaultProps: {
        elevation: 0,
      },
      styleOverrides: {
        root: {
          border: '1px solid #E2E8F0',
          borderRadius: 12,
          transition: 'box-shadow 200ms ease, border-color 200ms ease',
          '&:hover': {
            borderColor: '#CBD5E1',
            boxShadow: '0 4px 12px rgba(0,0,0,0.06)',
          },
        },
      },
    },
    MuiPaper: {
      defaultProps: {
        elevation: 0,
      },
      styleOverrides: {
        root: {
          borderRadius: 12,
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          fontWeight: 500,
          borderRadius: 8,
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        size: 'small',
        variant: 'outlined',
      },
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
            transition: 'border-color 200ms ease',
          },
        },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          '& .MuiTableCell-head': {
            fontFamily: '"Figtree", sans-serif',
            fontWeight: 600,
            fontSize: '0.8125rem',
            color: '#475569',
            backgroundColor: '#F1F5F9',
            textTransform: 'uppercase',
            letterSpacing: '0.05em',
          },
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          transition: 'background-color 150ms ease',
          '&:hover': {
            backgroundColor: '#F8FAFC',
            cursor: 'pointer',
          },
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 16,
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight: '1px solid #E2E8F0',
          backgroundColor: '#FFFFFF',
        },
      },
    },
    MuiAppBar: {
      defaultProps: {
        elevation: 0,
        color: 'inherit',
      },
      styleOverrides: {
        root: {
          backgroundColor: '#FFFFFF',
          borderBottom: '1px solid #E2E8F0',
        },
      },
    },
    MuiTooltip: {
      defaultProps: {
        arrow: true,
      },
      styleOverrides: {
        tooltip: {
          borderRadius: 6,
          fontSize: '0.75rem',
        },
      },
    },
    MuiAvatar: {
      styleOverrides: {
        root: {
          fontFamily: '"Figtree", sans-serif',
          fontWeight: 600,
        },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 500,
          minHeight: 44,
        },
      },
    },
    MuiAlert: {
      styleOverrides: {
        root: {
          borderRadius: 10,
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          borderRadius: 4,
          height: 6,
        },
      },
    },
    MuiSkeleton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
    },
    MuiBreadcrumbs: {
      styleOverrides: {
        root: {
          fontSize: '0.8125rem',
        },
      },
    },
  },
};

export const theme = createTheme(themeOptions);

export const statusColors = {

  ACTIVE: 'success' as const,
  INACTIVE: 'default' as const,
  DECEASED: 'error' as const,
  TRANSFERRED: 'warning' as const,
  DISCHARGED: 'info' as const,

  SCHEDULED: 'info' as const,
  CONFIRMED: 'primary' as const,
  CHECKED_IN: 'secondary' as const,
  IN_PROGRESS: 'warning' as const,
  COMPLETED: 'success' as const,
  CANCELLED: 'error' as const,
  NO_SHOW: 'error' as const,
  RESCHEDULED: 'warning' as const,

  DRAFT: 'default' as const,
  PENDING: 'warning' as const,
  PARTIALLY_PAID: 'info' as const,
  PAID: 'success' as const,
  OVERDUE: 'error' as const,
  REFUNDED: 'secondary' as const,

  SUBMITTED: 'info' as const,
  IN_REVIEW: 'warning' as const,
  APPROVED: 'success' as const,
  DENIED: 'error' as const,
  APPEALED: 'warning' as const,
} as const;
