import React from 'react';
import { Box, Grid, Paper, Typography, Card, CardContent, LinearProgress } from '@mui/material';
import {
  BarChart,
  TrendingUp,
  CheckCircle,
  TrendingDown,
} from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface DashboardStat {
  label: string;
  value: number | string;
  change?: number;
  trend?: 'up' | 'down';
  icon?: React.ReactNode;
}

export interface GlobalHQDashboardProps {
  refreshInterval?: number;
}

/**
 * GlobalHQDashboard Component
 * Enterprise-grade global headquarters dashboard
 */
export const GlobalHQDashboard: React.FC<GlobalHQDashboardProps> = ({ refreshInterval = 30000 }) => {
  const stats = [
    { label: 'Total Requests', value: '24,532', trend: 'up', icon: <TrendingUp /> },
    { label: 'Active Users', value: '1,847', trend: 'up', icon: <CheckCircle /> },
    { label: 'Avg Response Time', value: '45s', trend: 'down', icon: <BarChart /> },
    { label: 'Service Health', value: '98.5%', trend: 'up', icon: <CheckCircle /> },
  ];

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 600, color: '#111827' }}>
        Global Headquarters Dashboard
      </Typography>
      <Grid container spacing={3}>
        {stats.map((stat, index) => (
          <Grid item xs={12} sm={6} key={index}>
            <Paper sx={{ height: '100%', p: 2, borderRadius: 2 }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    {stat.icon}
                    <Typography variant="h6" sx={{ color: '#111827', fontWeight: 600 }}>
                      {stat.value}
                    </Typography>
                  </Box>
                  <Box sx={{ flex: 1, textAlign: 'right' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      {stat.trend === 'up' && (
                        <TrendingUp sx={{ fontSize: '1rem', color: MANAGEMENT_COLORS.success }} />
                      )}
                      {stat.trend === 'down' && (
                        <TrendingDown sx={{ fontSize: '1rem', color: MANAGEMENT_COLORS.error }} />
                      )}
                      <Typography variant="caption" color="text.secondary">
                        {typeof stat.change === 'number' ? `${stat.change > 0 ? '+' : ''}${Math.abs(stat.change)}%` : stat.change}
                      </Typography>
                    </Box>
                  </Box>
                </Box>
                <Typography variant="h6" sx={{ color: '#374151', fontWeight: 500 }}>
                  {stat.label}
                </Typography>
              </CardContent>
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};
