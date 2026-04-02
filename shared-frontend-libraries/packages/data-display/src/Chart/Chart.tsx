import React, { useMemo } from 'react';
import {
  LineChart as RechartsLineChart,
  Line as RechartsLine,
  BarChart as RechartsBarChart,
  Bar as RechartsBar,
  PieChart as RechartsPieChart,
  Pie as RechartsPie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { Box, Typography, useTheme } from '@mui/material';
import { motion } from 'framer-motion';

// Types
export type ChartType = 'line' | 'bar' | 'pie';

export interface ChartDataPoint {
  name: string;
  [key: string]: string | number;
}

export interface ChartSeries {
  dataKey: string;
  name: string;
  color?: string;
  type?: 'monotone' | 'linear' | 'step' | 'stepBefore' | 'stepAfter';
}

export interface ChartProps {
  type: ChartType;
  data: ChartDataPoint[];
  series: ChartSeries[];
  title?: string;
  height?: number;
  width?: number | string;
  showLegend?: boolean;
  showGrid?: boolean;
  showTooltip?: boolean;
  xAxisLabel?: string;
  yAxisLabel?: string;
  colors?: string[];
  animation?: boolean;
  margin?: { top?: number; right?: number; bottom?: number; left?: number };
}

const DEFAULT_COLORS = [
  '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
  '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1',
];

export function Chart({
  type,
  data,
  series,
  title,
  height = 300,
  width = '100%',
  showLegend = true,
  showGrid = true,
  showTooltip = true,
  xAxisLabel,
  yAxisLabel,
  colors = DEFAULT_COLORS,
  animation = true,
  margin,
}: ChartProps) {
  const theme = useTheme();

  // Generate chart colors
  const seriesColors = useMemo(() => {
    return series.map((s, index) => s.color || colors[index % colors.length]);
  }, [series, colors]);

  // Render Line Chart
  if (type === 'line') {
    return (
      <Box sx={{ width }}>
        {title && (
          <Typography variant="h6" sx={{ mb: 2 }}>
            {title}
          </Typography>
        )}
        <ResponsiveContainer width="100%" height={height}>
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
                type={s.type || 'monotone'}
                dataKey={s.dataKey}
                name={s.name}
                stroke={seriesColors[index]}
                strokeWidth={2}
                dot={{ r: 4 }}
                activeDot={{ r: 6 }}
                animationDuration={animation ? 750 : 0}
              />
            ))}
          </RechartsLineChart>
        </ResponsiveContainer>
      </Box>
    );
  }

  // Render Bar Chart
  if (type === 'bar') {
    return (
      <Box sx={{ width }}>
        {title && (
          <Typography variant="h6" sx={{ mb: 2 }}>
            {title}
          </Typography>
        )}
        <ResponsiveContainer width="100%" height={height}>
          <RechartsBarChart data={data} margin={margin}>
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
              <RechartsBar
                key={s.dataKey}
                dataKey={s.dataKey}
                name={s.name}
                fill={seriesColors[index]}
                animationDuration={animation ? 750 : 0}
              />
            ))}
          </RechartsBarChart>
        </ResponsiveContainer>
      </Box>
    );
  }

  // Render Pie Chart
  if (type === 'pie') {
    return (
      <Box sx={{ width }}>
        {title && (
          <Typography variant="h6" sx={{ mb: 2 }}>
            {title}
          </Typography>
        )}
        <ResponsiveContainer width="100%" height={height}>
          <RechartsPieChart margin={margin}>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              labelLine={false}
              label={({ name, percent }) => `${name} (${(percent * 100).toFixed(0)}%)`}
              outerRadius={80}
              fill="#8884d8"
              dataKey={series[0]?.dataKey}
            >
              {data.map((entry, index) => (
                <Cell
                  key={`cell-${index}`}
                  fill={colors[index % colors.length]}
                />
              ))}
            </Pie>
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
          </RechartsPieChart>
        </ResponsiveContainer>
      </Box>
    );
  }

  return null;
}

Chart.displayName = 'Chart';

// Motion wrapper for animations
export function AnimatedChart(props: ChartProps) {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.3 }}
    >
      <Chart {...props} animation={false} />
    </motion.div>
  );
}

AnimatedChart.displayName = 'AnimatedChart';
