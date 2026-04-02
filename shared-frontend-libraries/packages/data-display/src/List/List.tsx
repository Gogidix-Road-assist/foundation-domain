import React from 'react';
import { List, ListItem, ListItemText, ListItemButton, ListItemAvatar, Avatar, IconButton, Divider, Box, Typography } from '@mui/material';
import { MoreVert } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type ListSize = 'small' | 'medium' | 'large';

export interface ListItemData {
  id: string;
  primary: string;
  secondary?: string;
  avatar?: string;
  avatarVariant?: 'circle' | 'square';
  action?: {
    label: string;
    onClick: () => void;
  };
}

export interface ListProps {
  items: ListItemData[];
  size?: ListSize;
  onItemClick?: (item: ListItemData) => void;
  divider?: boolean;
  showAvatar?: boolean;
  twoLine?: boolean;
}

/**
 * List Component
 * Enterprise-grade list with avatars and actions
 */
export const List: React.FC<ListProps> = ({
  items,
  size = 'medium',
  onItemClick,
  divider = true,
  showAvatar = true,
  twoLine = false,
}) => {
  const sizeStyles = {
    small: { padding: '8px' },
    medium: { padding: '12px' },
    large: { padding: '16px' },
  };

  return (
    <List sx={{ width: '100%', backgroundColor: '#FFFFFF', borderRadius: 1 }}>
      {items.map((item, index) => (
        <React.Fragment key={item.id}>
          <ListItem
            secondaryAction={
              item.action && (
                <IconButton edge="end" onClick={item.action.onClick}>
                  <MoreVert />
                </IconButton>
              )
            }
            sx={{
              py: sizeStyles[size].padding,
              borderBottom: divider ? '1px solid #E5E7EB' : 'none',
            }}
          >
            {showAvatar && item.avatar && (
              <ListItemAvatar
                sx={{
                  backgroundColor: MANAGEMENT_COLORS.primary,
                  color: '#FFFFFF',
                  width: size === 'small' ? 32 : size === 'medium' ? 40 : 48,
                  height: size === 'small' ? 32 : size === 'medium' ? 40 : 48,
                fontSize: size === 'small' ? '0.875rem' : size === 'medium' ? '1.125rem' : '1.5rem',
                mr: 2,
                }}
              >
                {item.avatar.charAt(0).toUpperCase()}
              </ListItemAvatar>
            )}
            <ListItemText
              primary={item.primary}
              secondary={item.secondary}
              sx={{
                '& .MuiListItemText-primary': {
                  fontWeight: 500,
                  color: '#111827',
                },
                '& .MuiListItemText-secondary': {
                  fontSize: '0.875rem',
                  color: '#6B7280',
                },
              }}
            />
          </ListItem>
          {index < items.length - 1 && <Divider variant="inset" component="li" />}
        </React.Fragment>
      ))}
    </List>
  );
};

/**
 * AvatarItem Component
 * List item with avatar
 */
export const AvatarItem: React.FC<{ children: React.ReactNode }> = ({ children }) => <>{children}</>;
