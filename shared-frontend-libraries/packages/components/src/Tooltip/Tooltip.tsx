import React from 'react';
import { Tooltip as MuiTooltip, TooltipProps as MuiTooltipProps } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type TooltipPlacement =
  | 'top'
  | 'top-start'
  | 'top-end'
  | 'right'
  | 'right-start'
  | 'right-end'
  | 'bottom'
  | 'bottom-start'
  | 'bottom-end'
  | 'left'
  | 'left-start'
  | 'left-end';

export type TooltipColor = 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info' | 'default';

export interface TooltipProps extends Omit<MuiTooltipProps, 'placement'> {
  title: React.ReactNode;
  placement?: TooltipPlacement;
  color?: TooltipColor;
  arrow?: boolean;
  delay?: number;
}

const colorMap: Record<TooltipColor, { bg: string; text: string }> = {
  primary: { bg: MANAGEMENT_COLORS.primary, text: '#FFFFFF' },
  secondary: { bg: MANAGEMENT_COLORS.secondary, text: '#FFFFFF' },
  success: { bg: MANAGEMENT_COLORS.success, text: '#FFFFFF' },
  warning: { bg: MANAGEMENT_COLORS.warning, text: '#000000' },
  error: { bg: MANAGEMENT_COLORS.error, text: '#FFFFFF' },
  info: { bg: MANAGEMENT_COLORS.info, text: '#FFFFFF' },
  default: { bg: '#1F2937', text: '#FFFFFF' },
};

/**
 * Tooltip Component
 * Enterprise-grade tooltip with color variants and arrow
 */
export const Tooltip = React.forwardRef<HTMLDivElement, TooltipProps>(
  (
    {
      title,
      placement = 'bottom',
      color = 'default',
      arrow = true,
      delay = 100,
      children,
      className,
      ...props
    },
    ref
  ) => {
    const config = colorMap[color];

    return (
      <MuiTooltip
        ref={ref}
        title={title}
        placement={placement}
        arrow={arrow}
        enterDelay={delay / 1000}
        leaveDelay={0}
        className={className}
        componentsProps={{
          tooltip: {
            sx: {
              backgroundColor: config.bg,
              color: config.text,
              fontSize: '0.875rem',
              fontWeight: 500,
              padding: '8px 12px',
              borderRadius: '6px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
            },
          },
          arrow: {
            sx: {
              color: config.bg,
            },
          },
        }}
        {...props}
      >
        {children}
      </MuiTooltip>
    );
  }
);

Tooltip.displayName = 'Tooltip';
