

import {
  Card,
  CardContent,
  Box,
  Typography,
  Stack,
  Divider,
  Collapse,
  IconButton,
  Tooltip,
  Link,
} from '@mui/material';
import { ExpandMore, ExpandLess, OpenInNew, ContentCopy } from '@mui/icons-material';
import { useState } from 'react';
import {
  AuditActionChip,
  AuditOutcomeChip,
  AuditSeverityChip,
  ResourceCategoryChip,
  TimestampDisplay,
} from './AuditChips';
import type { AuditEventSummary } from '../types/audit.types';

interface AuditEventCardProps {
  readonly event: AuditEventSummary;
  readonly onViewResource?: (category: string, resourceId: string) => void;
  readonly onViewUser?: (userId: string) => void;
  readonly onViewPatient?: (patientId: string) => void;
}

function getSeverityBorderColor(severity: string): string {
  switch (severity) {
    case 'CRITICAL':
      return 'error.main';
    case 'ERROR':
      return 'error.light';
    case 'WARNING':
      return 'warning.main';
    default:
      return 'divider';
  }
}

export function AuditEventCard({
  event,
  onViewResource,
  onViewUser,
  onViewPatient,
}: AuditEventCardProps) {
  const [expanded, setExpanded] = useState(false);

  const handleCopyId = () => {
    navigator.clipboard.writeText(event.id);
  };

  const handleCopyCorrelationId = () => {
    if (event.correlationId) {
      navigator.clipboard.writeText(event.correlationId);
    }
  };

  return (
    <Card
      variant="outlined"
      sx={{
        mb: 1,
        borderLeft: 4,
        borderLeftColor: getSeverityBorderColor(event.severity),
        '&:hover': {
          bgcolor: 'action.hover',
        },
      }}
    >
      <CardContent sx={{ py: 1.5, '&:last-child': { pb: 1.5 } }}>
        {}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
          {}
          <Typography variant="body2" color="text.secondary" sx={{ minWidth: 100 }}>
            <TimestampDisplay timestamp={event.timestamp} />
          </Typography>

          {}
          <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap>
            <AuditActionChip action={event.action} />
            <AuditOutcomeChip outcome={event.outcome} />
            <AuditSeverityChip severity={event.severity} />
            <ResourceCategoryChip category={event.resourceCategory} />
          </Stack>

          {}
          {event.username && (
            <Box sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <Typography variant="body2" color="text.secondary">
                by
              </Typography>
              {event.userId && onViewUser ? (
                <Link
                  component="button"
                  variant="body2"
                  onClick={() => onViewUser(event.userId!)}
                  sx={{ cursor: 'pointer' }}
                >
                  {event.username}
                </Link>
              ) : (
                <Typography variant="body2" fontWeight={500}>
                  {event.username}
                </Typography>
              )}
              {event.userRole && (
                <Typography variant="caption" color="text.secondary">
                  ({event.userRole})
                </Typography>
              )}
            </Box>
          )}

          {}
          <IconButton size="small" onClick={() => setExpanded(!expanded)}>
            {expanded ? <ExpandLess /> : <ExpandMore />}
          </IconButton>
        </Box>

        {}
        {event.description && (
          <Typography variant="body2" sx={{ mt: 1 }}>
            {event.description}
          </Typography>
        )}

        {}
        {event.resourceId && onViewResource && (
          <Box sx={{ mt: 0.5 }}>
            <Link
              component="button"
              variant="body2"
              onClick={() => onViewResource(event.resourceCategory, event.resourceId!)}
              sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
            >
              View {event.resourceCategory.toLowerCase().replace('_', ' ')}
              <OpenInNew fontSize="inherit" />
            </Link>
          </Box>
        )}

        {}
        {event.patientId && onViewPatient && (
          <Box sx={{ mt: 0.5 }}>
            <Typography variant="caption" color="text.secondary">
              Patient:{' '}
            </Typography>
            <Link
              component="button"
              variant="body2"
              onClick={() => onViewPatient(event.patientId!)}
            >
              View Patient Record
            </Link>
          </Box>
        )}

        {}
        <Collapse in={expanded}>
          <Divider sx={{ my: 1.5 }} />
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
              gap: 2,
            }}
          >
            {}
            <Box>
              <Typography variant="caption" color="text.secondary">
                Event ID
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <Typography variant="body2" fontFamily="monospace" fontSize={12}>
                  {event.id}
                </Typography>
                <Tooltip title="Copy ID">
                  <IconButton size="small" onClick={handleCopyId}>
                    <ContentCopy fontSize="inherit" />
                  </IconButton>
                </Tooltip>
              </Box>
            </Box>

            {}
            {event.correlationId && (
              <Box>
                <Typography variant="caption" color="text.secondary">
                  Correlation ID
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                  <Typography variant="body2" fontFamily="monospace" fontSize={12}>
                    {event.correlationId}
                  </Typography>
                  <Tooltip title="Copy Correlation ID">
                    <IconButton size="small" onClick={handleCopyCorrelationId}>
                      <ContentCopy fontSize="inherit" />
                    </IconButton>
                  </Tooltip>
                </Box>
              </Box>
            )}

            {}
            {event.sessionId && (
              <Box>
                <Typography variant="caption" color="text.secondary">
                  Session ID
                </Typography>
                <Typography variant="body2" fontFamily="monospace" fontSize={12}>
                  {event.sessionId}
                </Typography>
              </Box>
            )}

            {}
            {event.ipAddressHash && (
              <Box>
                <Typography variant="caption" color="text.secondary">
                  IP Hash
                </Typography>
                <Typography variant="body2" fontFamily="monospace" fontSize={12}>
                  {event.ipAddressHash}
                </Typography>
              </Box>
            )}

            {}
            {event.userAgent && (
              <Box sx={{ gridColumn: 'span 2' }}>
                <Typography variant="caption" color="text.secondary">
                  User Agent
                </Typography>
                <Typography variant="body2" fontSize={12} noWrap>
                  {event.userAgent}
                </Typography>
              </Box>
            )}

            {}
            {event.details && Object.keys(event.details).length > 0 && (
              <Box sx={{ gridColumn: '1 / -1' }}>
                <Typography variant="caption" color="text.secondary">
                  Additional Details
                </Typography>
                <Box
                  component="pre"
                  sx={{
                    bgcolor: 'grey.100',
                    p: 1,
                    borderRadius: 1,
                    fontSize: 12,
                    fontFamily: 'monospace',
                    overflow: 'auto',
                    maxHeight: 200,
                    m: 0,
                  }}
                >
                  {JSON.stringify(event.details, null, 2)}
                </Box>
              </Box>
            )}
          </Box>
        </Collapse>
      </CardContent>
    </Card>
  );
}

export default AuditEventCard;
