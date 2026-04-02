import React from 'react';
import {
  LineChart as RechartsLineChart,
  Line as RechartsLine,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Area,
  AreaChart,
} from 'recharts';
import { Box, Typography, useTheme } from '@mui/material';
import { motion } from 'framer-motion';

export interface LineChartDataPoint {
  name: string;
  [key: string]: string | number;
}

export interface LineChartSeries {
  dataKey: string;
  name: string;
  color?: string;
  strokeWidth?: number;
  type?: 'monotone' | 'linear' | 'step' | 'stepBefore' | 'stepAfter';
  showArea?: boolean;
  areaColor?: string;
  areaOpacity?: number;
  dot?: boolean | { r?: number };
}

export interface LineChartProps {
  data: LineChartDataPoint[];
  series: LineChartSeries[];
  title?: string;
  subtitle?: string;
  height?: number;
  width?: number | string;
  showLegend?: boolean;
  showGrid?: boolean;
  showTooltip?: boolean;
  showDots?: boolean;
  showArea?: boolean;
  xAxisLabel?: string;
  yAxisLabel?: string;
  colors?: string[];
  smooth?: boolean;
  curveType?: 'monotone' | 'linear' | 'step' | 'stepBefore' | 'stepAfter';
  margin?: { top?: number; right?: number; bottom?: number; left?: number };
}

const DEFAULT_COLORS = [
  '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
  '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1',
];

export function LineChart({
  data,
  series,
  title,
  subtitle,
  height = 350,
  width = '100%',
  showLegend = true,
  showGrid = true,
  showTooltip = true,
  showDots = true,
  showArea = false,
  xAxisLabel,
  yAxisLabel,
  colors = DEFAULT_COLORS,
  smooth = true,
  curveType = 'monotone',
  margin,
}: LineChartProps) {
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
          {showArea ? (
            <AreaChart data={data} margin={margin}>
              {showGrid && <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />}
              <XAxis
                dataKey="name"
                stroke={theme.palette.text.secondary}
                label={xAxisLabel}
              />
              <YAxis stroke={theme.palette.text.secondary} label={yAxisLabel} />
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
                <Area
                  key={s.dataKey}
                  type={curveType}
                  dataKey={s.dataKey}
                  name={s.name}
                  stroke={seriesColors[index]}
                  strokeWidth={s.strokeWidth || 2}
                  fill={s.areaColor || seriesColors[index]}
                  fillOpacity={s.areaOpacity || 0.1}
                />
              ))}
            </AreaChart>
          ) : (
            <RechartsLineChart data={data} margin={margin}>
              {showGrid && <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />}
              <XAxis
                dataKey="name"
                stroke={theme.palette.text.secondary}
                label={xAxisLabel}
              />
              <YAxis stroke={theme.palette.text.secondary} label={yAxisLabel} />
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
                <RechartsLine
                  key={s.dataKey}
                  type={s.type || curveType}
                  dataKey={s.dataKey}
                  name={s.name}
                  stroke={seriesColors[index]}
                  strokeWidth={s.strokeWidth || 2}
                  dot={showDots ? (s.dot || { r: 4 }) : false}
                  activeDot={{ r: 6 }}
                  isAnimationActive={true}
                  animationDuration={750}
                />
              ))}
            </RechartsLineChart>
          )}
        </ResponsiveContainer>
      </motion.div>
    </Box>
  );
}

LineChart.displayName = 'LineChart';
