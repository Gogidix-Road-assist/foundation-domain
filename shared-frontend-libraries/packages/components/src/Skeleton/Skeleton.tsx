import React from 'react';
import { Skeleton as MuiSkeleton, SkeletonProps as MuiSkeletonProps, Box, Stack } from '@mui/material';

export type SkeletonVariant = 'text' | 'circular' | 'rectangular' | 'rounded';
export type SkeletonAnimation = 'pulse' | 'wave' | 'none';

export interface SkeletonProps extends Omit<MuiSkeletonProps, 'variant' | 'animation'> {
  variant?: SkeletonVariant;
  animation?: SkeletonAnimation;
  width?: number | string;
  height?: number | string;
  count?: number;
}

/**
 * Skeleton Component
 * Enterprise-grade loading skeleton placeholder
 */
export const Skeleton = React.forwardRef<HTMLDivElement, SkeletonProps>(
  (
    {
      variant = 'text',
      animation = 'pulse',
      width,
      height,
      count = 1,
      className,
      ...props
    },
    ref
  ) => {
    const skeletons = Array.from({ length: count }).map((_, index) => (
      <MuiSkeleton
        key={index}
        ref={count === 1 ? ref : undefined}
        variant={variant}
        animation={animation === 'none' ? false : animation}
        width={width}
        height={height}
        className={className}
        sx={{
          backgroundColor: '#E5E7EB',
          '&::before': {
            background: 'linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent)',
          },
        }}
        {...props}
      />
    ));

    if (count > 1) {
      return (
        <Stack spacing={1} ref={ref}>
          {skeletons}
        </Stack>
      );
    }

    return <>{skeletons}</>;
  }
);

Skeleton.displayName = 'Skeleton';

/**
 * SkeletonCard Component
 * Pre-built card skeleton
 */
export interface SkeletonCardProps {
  showAvatar?: boolean;
  lines?: number;
  showAction?: boolean;
}

export const SkeletonCard: React.FC<SkeletonCardProps> = ({
  showAvatar = true,
  lines = 3,
  showAction = true,
}) => {
  return (
    <Box sx={{ p: 2, border: '1px solid #E5E7EB', borderRadius: '8px' }}>
      {showAvatar && (
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Skeleton variant="circular" width={40} height={40} />
          <Box sx={{ ml: 2, flex: 1 }}>
            <Skeleton variant="text" width="60%" />
          </Box>
        </Box>
      )}
      <Stack spacing={1}>
        {Array.from({ length: lines }).map((_, i) => (
          <Skeleton key={i} variant="text" width={i === lines - 1 ? '70%' : '100%'} />
        ))}
      </Stack>
      {showAction && (
        <Box sx={{ mt: 2 }}>
          <Skeleton variant="rectangular" height={32} width={100} />
        </Box>
      )}
    </Box>
  );
};

/**
 * SkeletonList Component
 * Pre-built list skeleton
 */
export interface SkeletonListProps {
  items?: number;
  showAvatar?: boolean;
}

export const SkeletonList: React.FC<SkeletonListProps> = ({ items = 5, showAvatar = true }) => {
  return (
    <Stack spacing={2}>
      {Array.from({ length: items }).map((_, index) => (
        <Box key={index} sx={{ display: 'flex', alignItems: 'center' }}>
          {showAvatar && (
            <Skeleton variant="circular" width={40} height={40} sx={{ mr: 2 }} />
          )}
          <Stack spacing={1} sx={{ flex: 1 }}>
            <Skeleton variant="text" width="80%" />
            <Skeleton variant="text" width="60%" />
          </Stack>
        </Box>
      ))}
    </Stack>
  );
};

/**
 * SkeletonTable Component
 * Pre-built table skeleton
 */
export interface SkeletonTableProps {
  rows?: number;
  columns?: number;
  showHeader?: boolean;
}

export const SkeletonTable: React.FC<SkeletonTableProps> = ({
  rows = 5,
  columns = 4,
  showHeader = true,
}) => {
  return (
    <Box>
      {showHeader && (
        <Box sx={{ display: 'flex', mb: 2, borderBottom: '1px solid #E5E7EB', pb: 1 }}>
          {Array.from({ length: columns }).map((_, i) => (
            <Skeleton
              key={`header-${i}`}
              variant="text"
              width="100%"
              sx={{ mr: i < columns - 1 ? 4 : 0, flex: 1 }}
            />
          ))}
        </Box>
      )}
      {Array.from({ length: rows }).map((_, rowIndex) => (
        <Box
          key={`row-${rowIndex}`}
          sx={{ display: 'flex', py: 2, borderBottom: '1px solid #F3F4F6' }}
        >
          {Array.from({ length: columns }).map((_, colIndex) => (
            <Skeleton
              key={`cell-${rowIndex}-${colIndex}`}
              variant="text"
              width={colIndex === 0 ? '60%' : '80%'}
              sx={{ mr: colIndex < columns - 1 ? 4 : 0, flex: 1 }}
            />
          ))}
        </Box>
      ))}
    </Box>
  );
};
