import React from 'react';
import {
  PieChart as RechartsPieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { Box, Typography, useTheme, BoxProps } from '@mui/material';
import { motion } from 'framer-motion';

export interface PieChartDataPoint {
  name: string;
  value: number;
  [key: string]: string | number;
}

export interface PieChartProps {
  data: PieChartDataPoint[];
  dataKey?: string;
  nameKey?: string;
  title?: string;
  subtitle?: string;
  height?: number;
  width?: number | string;
  showLegend?: boolean;
  showTooltip?: boolean;
  showLabel?: boolean;
  colors?: string[];
  innerRadius?: number;
  outerRadius?: number;
  margin?: { top?: number; right?: number; bottom?: number; left?: number };
  label?: (entry: any) => string;
  cx?: string | number;
  cy?: string | number;
}

const DEFAULT_COLORS = [
  '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
  '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1',
];

export function PieChart({
  data,
  dataKey = 'value',
  nameKey = 'name',
  title,
  subtitle,
  height = 350,
  width = '100%',
  showLegend = true,
  showTooltip = true,
  showLabel = true,
  colors = DEFAULT_COLORS,
  innerRadius = 0,
  outerRadius = 80,
  margin,
  label,
  cx = '50%',
  cy = '50%',
}: PieChartProps) {
  const theme = useTheme();

  const renderLabel = label || ((entry: any) => {
    const percentage = ((entry.value / data.reduce((sum, d) => sum + d.value, 0)) * 100).toFixed(1);
    return `${entry.name} (${percentage}%)`;
  });

  const isDonut = innerRadius > 0;

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
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.3 }}
      >
        <ResponsiveContainer width="100%" height={height}>
          <RechartsPieChart margin={margin}>
            <Pie
              data={data}
              cx={cx}
              cy={cy}
              labelLine={false}
              label={showLabel ? renderLabel : false}
              outerRadius={outerRadius}
              innerRadius={innerRadius}
              fill="#8884d8"
              dataKey={dataKey}
              nameKey={nameKey}
              animationDuration={750}
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
      </motion.div>
    </Box>
  );
}

PieChart.displayName = 'PieChart';

// Donut Chart variant (exported for convenience)
export function DonutChart(props: Omit<PieChartProps, 'innerRadius'>) {
  return <PieChart {...props} innerRadius={60} />;
}

DonutChart.displayName = 'DonutChart';
