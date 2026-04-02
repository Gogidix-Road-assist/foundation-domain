import React from 'react';
import { motion } from 'framer-motion';
import {
  Card as MuiCard,
  CardProps as MuiCardProps,
} from '@mui/material';
import { SHADOWS } from '@shared-frontend-libraries/design-system';

export type CardElevation = 0 | 1 | 2 | 3 | 4;

export interface CardProps extends MuiCardProps {
  elevation?: CardElevation;
  hoverable?: boolean;
}

const elevationStyles: Record<CardElevation, string> = {
  0: SHADOWS.none,
  1: SHADOWS.xs,
  2: SHADOWS.sm,
  3: SHADOWS.md,
  4: SHADOWS.lg,
};

/**
 * Card Component
 * Enterprise-grade card with elevation variants and hover states
 */
export const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({ children, elevation = 1, hoverable = false, className, ...props }, ref) => {
    const CardComponent = hoverable ? motion.div : 'div';
    const cardProps = hoverable ? {
      whileHover: { y: -4 },
      transition: { duration: 0.2 },
    } : {};

    return (
      <CardComponent
        ref={ref as any}
        className={className}
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '12px',
          boxShadow: elevationStyles[elevation],
          padding: '24px',
          ...cardProps,
        }}
        {...props}
      >
        <MuiCard
          elevation={0}
          sx={{
            boxShadow: 'none',
            backgroundColor: 'transparent',
            padding: 0,
            '&:hover': hoverable ? {
              boxShadow: SHADOWS.lg,
            } : undefined,
          }}
          {...props}
        >
          {children}
        </MuiCard>
      </CardComponent>
    );
  }
);

Card.displayName = 'Card';
