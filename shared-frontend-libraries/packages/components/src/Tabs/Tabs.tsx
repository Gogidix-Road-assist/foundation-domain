import React from 'react';
import {
  Tabs as MuiTabs,
  Tab as MuiTab,
  TabsProps as MuiTabsProps,
  TabProps as MuiTabProps,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type TabSize = 'small' | 'medium';
export type TabVariant = 'standard' | 'scrollable' | 'fullWidth';

export interface TabItem {
  label: string;
  value: string;
  icon?: React.ReactNode;
  disabled?: boolean;
}

export interface TabsProps extends Omit<MuiTabsProps, 'variant' | 'size'> {
  value: string;
  onChange: (event: React.SyntheticEvent, value: string) => void;
  tabs: TabItem[];
  size?: TabSize;
  variant?: TabVariant;
}

export interface TabProps extends Omit<MuiTabProps, 'size'> {
  size?: TabSize;
}

/**
 * Tab Component
 * Individual tab for Tabs component
 */
export const Tab = React.forwardRef<HTMLDivElement, TabProps>(
  ({ size = 'medium', className, children, ...props }, ref) => {
    return (
      <MuiTab
        ref={ref}
        className={className}
        sx={{
          minHeight: size === 'small' ? '36px' : '48px',
          fontSize: size === 'small' ? '0.875rem' : '1rem',
          fontWeight: 500,
          color: '#6B7280',
          '&.Mui-selected': {
            color: MANAGEMENT_COLORS.primary,
            fontWeight: 600,
          },
          '&:hover': {
            color: MANAGEMENT_COLORS.primary,
          },
        }}
        {...props}
      >
        {children}
      </MuiTab>
    );
  }
);

Tab.displayName = 'Tab';

/**
 * Tabs Component
 * Enterprise-grade tabs for navigation and content organization
 */
export const Tabs = React.forwardRef<HTMLDivElement, TabsProps>(
  (
    {
      value,
      onChange,
      tabs,
      size = 'medium',
      variant = 'standard',
      className,
      orientation = 'horizontal',
      ...props
    },
    ref
  ) => {
    return (
      <MuiTabs
        ref={ref}
        value={value}
        onChange={onChange}
        orientation={orientation}
        variant={variant === 'scrollable' ? 'scrollable' : variant === 'fullWidth' ? 'fullWidth' : undefined}
        scrollButtons="auto"
        className={className}
        sx={{
          minHeight: size === 'small' ? '36px' : '48px',
          '& .MuiTabs-indicator': {
            backgroundColor: MANAGEMENT_COLORS.primary,
            height: 3,
            borderRadius: '3px 3px 0 0',
          },
        }}
        {...props}
      >
        {tabs.map((tab) => (
          <Tab
            key={tab.value}
            value={tab.value}
            label={tab.label}
            icon={tab.icon}
            disabled={tab.disabled}
            size={size}
          />
        ))}
      </MuiTabs>
    );
  }
);

Tabs.displayName = 'Tabs';
