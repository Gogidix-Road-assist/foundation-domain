/**
 * Stats Widget Component
 * Displays a key metric with title and subtitle
 */

import { FC } from 'react';

type Color = 'blue' | 'green' | 'amber' | 'purple' | 'red';

interface StatsWidgetProps {
  title: string;
  value: number | string;
  subtitle?: string;
  color?: Color;
  loading?: boolean;
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

const colorClasses: Record<Color, { bg: string; text: string; icon: string }> = {
  blue: { bg: 'bg-blue-50', text: 'text-blue-600', icon: 'text-blue-500' },
  green: { bg: 'bg-green-50', text: 'text-green-600', icon: 'text-green-500' },
  amber: { bg: 'bg-amber-50', text: 'text-amber-600', icon: 'text-amber-500' },
  purple: { bg: 'bg-purple-50', text: 'text-purple-600', icon: 'text-purple-500' },
  red: { bg: 'bg-red-50', text: 'text-red-600', icon: 'text-red-500' }
};

const StatsWidget: FC<StatsWidgetProps> = ({
  title,
  value,
  subtitle,
  color = 'blue',
  loading = false,
  trend
}) => {
  const classes = colorClasses[color];

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-24 mb-2"></div>
          <div className="h-8 bg-gray-200 rounded w-16 mb-1"></div>
          <div className="h-3 bg-gray-200 rounded w-32"></div>
        </div>
      </div>
    );
  }

  return (
    <div className={`bg-white rounded-lg shadow-sm border border-gray-200 p-4 ${classes.bg}`}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className={`text-2xl font-bold ${classes.text} mt-1`}>
            {typeof value === 'number' ? value.toLocaleString() : value}
          </p>
          {subtitle && (
            <p className="text-xs text-gray-500 mt-1">{subtitle}</p>
          )}
        </div>
        {trend && (
          <div className={`flex items-center gap-1 text-sm ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
            <span>
              {trend.isPositive ? '↑' : '↓'} {Math.abs(trend.value)}%
            </span>
          </div>
        )}
      </div>
    </div>
  );
};

export default StatsWidget;
