import React from 'react';
import { Card, CardContent, Typography, Box, Grid, CardProps, LinearProgress } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface KPIMetric {
  label: string;
  value: number;
  target: number;
  change?: number;
}

export interface KPICardProps extends Omit<CardProps, 'children'> {
  title: string;
  metrics: KPIMetric[];
}

/**
 * KPICard Component
 * Enterprise-grade KPI card widget
 */
export const KPICard: React.FC<KPICardProps> = ({ title, metrics, ...props }) => {
  const getTrendColor = (change?: number) => {
    if (change === undefined) return '#9CA3AF';
    return change > 0 ? MANAGEMENT_COLORS.success : MANAGEMENT_COLORS.error;
  };

  const getProgress = (value: number, target: number) => Math.min(100, Math.round((value / target) * 100));

  return (
    <Card {...props}>
      <CardContent>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 600, color: '#111827' }}>
          {title}
        </Typography>
        <Grid container spacing={2}>
          {metrics.map((metric, index) => (
            <Grid item xs={6} key={index}>
              <Box
                sx={{
                  border: '1px solid #E5E7EB',
                  borderRadius: 2,
                  p: 2,
                  backgroundColor: '#F9FAFB',
                }}
              >
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ fontWeight: 500, color: '#374151' }}>
                    {metric.label}
                  </Typography>
                  <Box sx={{ textAlign: 'right' }}>
                    <Typography variant="h5" sx={{ fontWeight: 600, color: '#111827' }}>
                      {metric.value}
                    </Typography>
                  </Box>
                </Box>
                <LinearProgress
                  variant="determinate"
                  value={getProgress(metric.value, metric.target || 100)}
                  sx={{
                    height: 8,
                    borderRadius: 4,
                    backgroundColor: getTrendColor(metric.change),
                  }}
                />
              </Box>
            </Grid>
          ))}
        </Grid>
      </CardContent>
    </Card>
  );
};
