import React from 'react';
import {
  Card,
  CardContent,
  Box,
  Typography,
  useTheme,
  alpha,
} from '@mui/material';
import { motion } from 'framer-motion';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import TrendingFlatIcon from '@mui/icons-material/TrendingFlat';

export interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  trend?: {
    value: number;
    label?: string;
    positive?: boolean; // true = good increase, false = good decrease, undefined = automatic
  };
  icon?: React.ReactNode;
  color?: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';
  size?: 'small' | 'medium' | 'large';
  loading?: boolean;
  onClick?: () => void;
  className?: string;
  showTrendIcon?: boolean;
}

const COLOR_MAP = {
  primary: '#3B82F6',
  secondary: '#6B7280',
  success: '#10B981',
  warning: '#F59E0B',
  error: '#EF4444',
  info: '#06B6D4',
};

export function StatCard({
  title,
  value,
  subtitle,
  trend,
  icon,
  color = 'primary',
  size = 'medium',
  loading = false,
  onClick,
  className,
  showTrendIcon = true,
}: StatCardProps) {
  const theme = useTheme();
  const cardColor = COLOR_MAP[color];
  const isClickable = !!onClick;

  const sizeConfig = {
    small: {
      padding: 1.5,
      titleVariant: 'body2' as const,
      valueVariant: 'h5' as const,
      iconSize: 24,
    },
    medium: {
      padding: 2,
      titleVariant: 'body1' as const,
      valueVariant: 'h4' as const,
      iconSize: 32,
    },
    large: {
      padding: 2.5,
      titleVariant: 'h6' as const,
      valueVariant: 'h3' as const,
      iconSize: 40,
    },
  };

  const config = sizeConfig[size];

  // Determine trend direction and color
  const getTrendColor = () => {
    if (!trend) return undefined;
    if (trend.positive === undefined) {
      // Automatic: positive trend = green, negative = red
      return trend.value >= 0 ? 'success.main' : 'error.main';
    }
    // Manual: positive=true means increase is good
    return (trend.value >= 0) === trend.positive
      ? 'success.main'
      : 'error.main';
  };

  const getTrendIcon = () => {
    if (!trend || !showTrendIcon) return null;

    if (trend.value > 0) return <TrendingUpIcon />;
    if (trend.value < 0) return <TrendingDownIcon />;
    return <TrendingFlatIcon />;
  };

  const trendColor = getTrendColor();
  const trendIcon = getTrendIcon();

  if (loading) {
    return (
      <Card
        className={className}
        sx={{
          height: '100%',
          borderRadius: 2,
        }}
      >
        <CardContent sx={{ p: config.padding }}>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <Box sx={{ flex: 1 }}>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                {title}
              </Typography>
              <Box
                sx={{
                  height: size === 'small' ? 24 : size === 'medium' ? 32 : 40,
                  width: size === 'small' ? 60 : size === 'medium' ? 100 : 150,
                  bgcolor: alpha(cardColor, 0.1),
                  borderRadius: 1,
                }}
              />
            </Box>
            {icon && (
              <Box sx={{ opacity: 0.3 }}>
                {React.cloneElement(icon as React.ReactElement, {
                  style: { fontSize: config.iconSize },
                })}
              </Box>
            )}
          </Box>
        </CardContent>
      </Card>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      whileHover={isClickable ? { scale: 1.02 } : {}}
      whileTap={isClickable ? { scale: 0.98 } : {}}
    >
      <Card
        className={className}
        onClick={onClick}
        sx={{
          height: '100%',
          borderRadius: 2,
          cursor: isClickable ? 'pointer' : 'default',
          transition: 'box-shadow 0.2s ease-in-out',
          '&:hover': isClickable
            ? {
                boxShadow: theme.shadows[4],
              }
            : {
                boxShadow: theme.shadows[2],
              },
        }}
      >
        <CardContent sx={{ p: config.padding }}>
          <Box
            sx={{
              display: 'flex',
              alignItems: 'flex-start',
              justifyContent: 'space-between',
            }}
          >
            {/* Left side: title, value, trend */}
            <Box sx={{ flex: 1, minWidth: 0 }}>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                {title}
              </Typography>
              <Typography
                variant={config.valueVariant}
                sx={{
                  fontWeight: 700,
                  color: cardColor,
                  mb: subtitle || trend ? 0.5 : 0,
                }}
              >
                {typeof value === 'number'
                  ? value.toLocaleString()
                  : value}
              </Typography>

              {/* Subtitle */}
              {subtitle && (
                <Typography variant="caption" color="text.secondary">
                  {subtitle}
                </Typography>
              )}

              {/* Trend indicator */}
              {trend && (
                <Box
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    mt: 0.5,
                    gap: 0.5,
                  }}
                >
                  {trendIcon && (
                    <Box sx={{ color: trendColor, fontSize: 16 }}>
                      {trendIcon}
                    </Box>
                  )}
                  <Typography
                    variant="caption"
                    sx={{
                      color: trendColor,
                      fontWeight: 600,
                    }}
                  >
                    {Math.abs(trend.value)}%{trend.label ? ` ${trend.label}` : ''}
                  </Typography>
                </Box>
              )}
            </Box>

            {/* Right side: icon */}
            {icon && (
              <Box
                sx={{
                  p: 1,
                  borderRadius: 2,
                  bgcolor: alpha(cardColor, 0.1),
                  color: cardColor,
                }}
              >
                {React.cloneElement(icon as React.ReactElement, {
                  style: { fontSize: config.iconSize },
                })}
              </Box>
            )}
          </Box>
        </CardContent>
      </Card>
    </motion.div>
  );
}

StatCard.displayName = 'StatCard';
