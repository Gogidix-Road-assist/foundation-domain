import React from 'react';
import {
  BarChart as RechartsBarChart,
  Bar as RechartsBar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { Box, Typography, useTheme } from '@mui/material';
import { motion } from 'framer-motion';

export interface BarChartDataPoint {
  name: string;
  [key: string]: string | number;
}

export interface BarChartSeries {
  dataKey: string;
  name: string;
  color?: string;
}

export interface BarChartProps {
  data: BarChartDataPoint[];
  series: BarChartSeries[];
  title?: string;
  subtitle?: string;
  height?: number;
  width?: number | string;
  showLegend?: boolean;
  showGrid?: boolean;
  showTooltip?: boolean;
  xAxisLabel?: string;
  yAxisLabel?: string;
  colors?: string[];
  layout?: 'vertical' | 'horizontal';
  margin?: { top?: number; right?: number; bottom?: number; left?: number };
}

const DEFAULT_COLORS = [
  '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
  '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1',
];

export function BarChart({
  data,
  series,
  title,
  subtitle,
  height = 350,
  width = '100%',
  showLegend = true,
  showGrid = true,
  showTooltip = true,
  xAxisLabel,
  yAxisLabel,
  colors = DEFAULT_COLORS,
  layout = 'vertical',
  margin,
}: BarChartProps) {
  const theme = useTheme();

  const seriesColors = series.map((s, index) => s.color || colors[index % colors.length]);

  return (
    <Box sx={{ width }}>
      {title && (
        <Box sx={{ mb: 2 }}>
          <Typography variant="h6">{title}</Typography>
          {subtitle && (
            <Typography variant="body2" color="text.secondary">
              {subtitle}
            </Typography>
          )}
        </Box>
      )}

      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <ResponsiveContainer width="100%" height={height}>
          <RechartsBarChart
            data={data}
            layout={layout === 'horizontal' ? 'vertical' : 'horizontal'}
            margin={margin}
          >
            {showGrid && <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />}
            {layout === 'vertical' ? (
              <>
                <XAxis
                  dataKey="name"
                  stroke={theme.palette.text.secondary}
                  label={xAxisLabel}
                />
                <YAxis stroke={theme.palette.text.secondary} label={yAxisLabel} />
              </>
            ) : (
              <>
                <XAxis type="number" stroke={theme.palette.text.secondary} label={xAxisLabel} />
                <YAxis
                  dataKey="name"
                  type="category"
                  stroke={theme.palette.text.secondary}
                  label={yAxisLabel}
                />
              </>
            )}
            {showTooltip && (
              <Tooltip
                contentStyle={{
                  backgroundColor: theme.palette.background.paper,
                  border: `1px solid ${theme.palette.divider}`,
                  borderRadius: '8px',
                }}
              />
            )}
            {showLegend && <Legend />}
            {series.map((s, index) => (
              <RechartsBar
                key={s.dataKey}
                dataKey={s.dataKey}
                name={s.name}
                fill={seriesColors[index]}
                animationDuration={750}
                radius={[4, 4, 0, 0]}
              />
            ))}
          </RechartsBarChart>
        </ResponsiveContainer>
      </motion.div>
    </Box>
  );
}

BarChart.displayName = 'BarChart';
