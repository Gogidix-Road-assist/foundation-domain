import React from 'react';
import { Box, Typography, Stack, Chip } from '@mui/material';
import { CheckCircle, Pending, Error, Schedule, Circle } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type TimelineStatus = 'completed' | 'pending' | 'error' | 'in-progress';

export interface TimelineItem {
  id: string;
  title: string;
  description?: string;
  timestamp?: string;
  status: TimelineStatus;
  icon?: React.ReactNode;
}

export interface TimelineProps {
  items: TimelineItem[];
  showStatus?: boolean;
}

const statusConfig = {
  completed: { color: MANAGEMENT_COLORS.success, icon: <CheckCircle /> },
  pending: { color: '#F59E0B', icon: <Pending /> },
  error: { color: MANAGEMENT_COLORS.error, icon: <Error /> },
  'in-progress': { color: MANAGEMENT_COLORS.primary, icon: <Schedule /> },
};

/**
 * Timeline Component
 * Enterprise-grade timeline for activity tracking
 */
export const Timeline: React.FC<TimelineProps> = ({ items, showStatus = true }) => {
  return (
    <Stack spacing={3}>
      {items.map((item, index) => (
        <Box key={item.id} sx={{ display: 'flex', gap: 2 }}>
          <Box
            sx={{
              position: 'relative',
              flex: '0 0 auto',
            }}
          >
            {item.icon || (
              <Box
                sx={{
                  width: 40,
                  height: 40,
                  borderRadius: '50%',
                  backgroundColor: '#F3F4F6',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  zIndex: 1,
                }}
              >
                {item.icon}
              </Box>
            )}
            <Box
              sx={{
                flex: 1,
                ml: 2,
              }}
            >
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                <Box>
                  <Typography variant="body2" sx={{ fontWeight: 500, color: '#111827' }}>
                    {item.title}
                  </Typography>
                  {item.description && (
                    <Typography variant="body2" color="text.secondary" sx={{ fontSize: '0.875rem' }}>
                      {item.description}
                    </Typography>
                  )}
                </Box>
                {showStatus && (
                  <Chip
                    size="small"
                    icon={statusConfig[item.status].icon}
                    label={item.status}
                    sx={{
                      backgroundColor: `${statusConfig[item.status].color}15`,
                      color: statusConfig[item.status].color,
                      fontSize: '0.75rem',
                      fontWeight: 500,
                    }}
                  />
                )}
              </Box>
            </Box>
            {item.timestamp && (
              <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.75rem' }}>
                {item.timestamp}
              </Typography>
            )}
          </Box>
          {index < items.length - 1 && (
            <Box
              sx={{
                position: 'absolute',
                left: 20,
                top: 0,
                bottom: 0,
                width: 2,
                height: '100%',
                backgroundColor: '#E5E7EB',
              }}
            />
          )}
        </Box>
      ))}
    </Stack>
  );
};
