import React from 'react';
import {
  Menu as MuiMenu,
  MenuItem as MuiMenuItem,
  MenuList as MuiMenuList,
  MenuProps as MuiMenuProps,
  MenuItemProps as MuiMenuItemProps,
  Divider,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface MenuItemOption {
  label: string;
  value: string;
  icon?: React.ReactNode;
  disabled?: boolean;
  danger?: boolean;
}

export interface MenuProps extends Omit<MuiMenuProps, 'open'> {
  anchorEl: HTMLElement | null;
  onClose: () => void;
  items?: MenuItemOption[];
  onSelect?: (value: string) => void;
}

export interface CustomMenuItemProps extends Omit<MuiMenuItemProps, 'value'> {
  value?: string;
  danger?: boolean;
}

/**
 * MenuItem Component
 * Individual menu item with optional danger state
 */
export const MenuItem = React.forwardRef<HTMLLIElement, CustomMenuItemProps>(
  ({ value, danger = false, children, onClick, disabled, className, ...props }, ref) => {
    return (
      <MuiMenuItem
        ref={ref}
        value={value}
        onClick={(e) => {
          onClick?.(e);
        }}
        disabled={disabled}
        className={className}
        sx={{
          fontSize: '0.875rem',
          color: danger ? MANAGEMENT_COLORS.error : 'inherit',
          '&:hover': {
            backgroundColor: danger ? '#FEE2E2' : '#F3F4F6',
          },
          '&.Mui-selected': {
            backgroundColor: danger ? '#FEE2E2' : `${MANAGEMENT_COLORS.primary}15`,
            color: danger ? MANAGEMENT_COLORS.error : MANAGEMENT_COLORS.primary,
          },
        }}
        {...props}
      >
        {children}
      </MuiMenuItem>
    );
  }
);

MenuItem.displayName = 'MenuItem';

/**
 * Menu Component
 * Enterprise-grade dropdown menu with animations
 */
export const Menu = React.forwardRef<HTMLDivElement, MenuProps>(
  ({ anchorEl, onClose, items, onSelect, className, ...props }, ref) => {
    const handleItemClick = (item: MenuItemOption) => () => {
      onSelect?.(item.value);
      onClose();
    };

    return (
      <MuiMenu
        ref={ref}
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={onClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'left',
        }}
        className={className}
        PaperProps={{
          sx: {
            borderRadius: '8px',
            boxShadow: '0 10px 25px rgba(0, 0, 0, 0.15)',
            border: '1px solid #E5E7EB',
            minWidth: '180px',
          },
        }}
        MenuListProps={{
          sx: {
            padding: '4px',
          },
        }}
        {...props}
      >
        {items ? (
          items.map((item, index) => (
            <React.Fragment key={item.value}>
              <MenuItem
                value={item.value}
                onClick={handleItemClick(item)}
                disabled={item.disabled}
                danger={item.danger}
              >
                {item.icon && <span style={{ marginRight: '12px', display: 'flex', alignItems: 'center' }}>{item.icon}</span>}
                {item.label}
              </MenuItem>
              {index < items.length - 1 && items[index + 1].divider && (
                <Divider sx={{ my: 0.5 }} />
              )}
            </React.Fragment>
          ))
        ) : (
          props.children
        )}
      </MuiMenu>
    );
  }
);

Menu.displayName = 'Menu';

/**
 * MenuList Component
 * List component for menu items
 */
export const MenuList = MuiMenuList;
