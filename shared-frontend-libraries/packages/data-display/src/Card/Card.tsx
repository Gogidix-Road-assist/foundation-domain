import React from 'react';
import { Card as MuiCard, CardProps as MuiCardProps, CardContent, CardActions, Typography, Chip, Box } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface CardItemData {
  id: string;
  title: string;
  description?: string;
  image?: string;
  tags?: string[];
  stats?: Array<{ label: string; value: string }>;
  actions?: Array<{ label: string; onClick: () => void }>;
}

export interface DataCardProps extends Omit<MuiCardProps, 'children'> {
  item: CardItemData;
  size?: 'small' | 'medium' | 'large';
  showActions?: boolean;
}

/**
 * DataCard Component
 * Enterprise-grade card with image, tags, stats, and actions
 */
export const DataCard: React.FC<DataCardProps> = ({ item, size = 'medium', showActions = true, ...props }) => {
  const sizeStyles = {
    small: { maxWidth: 280 },
    medium: { maxWidth: 340 },
    large: { maxWidth: 400 },
  };

  return (
    <MuiCard
      {...props}
      sx={{
        maxWidth: sizeStyles[size].maxWidth,
        height: '100%',
        boxShadow: 0,
        border: '1px solid #E5E7EB',
        borderRadius: 2,
        overflow: 'hidden',
      }}
    >
      {item.image && (
        <Box
          sx={{
            height: size === 'small' ? 120 : size === 'medium' ? 160 : 200,
            backgroundImage: `url(${item.image})`,
            backgroundSize: 'cover',
            backgroundPosition: 'center',
          }}
        />
      )}
      <CardContent>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 600, color: '#111827' }}>
          {item.title}
        </Typography>
        {item.description && (
          <Typography variant="body2" color="text.secondary" sx={{ fontSize: '0.875rem', mb: 1 }}>
            {item.description}
          </Typography>
        )}
        {item.tags && (
          <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 2 }}>
            {item.tags.map((tag) => (
              <Chip
                key={tag}
                label={tag}
                size="small"
                sx={{
                  backgroundColor: `${MANAGEMENT_COLORS.primary}15`,
                  color: MANAGEMENT_COLORS.primary,
                  fontSize: '0.75rem',
                }}
              />
            ))}
          </Box>
        )}
        {item.stats && (
          <Box sx={{ display: 'flex', gap: 3, mb: 2 }}>
            {item.stats.map((stat, index) => (
              <Box key={index} sx={{ flex: 1, textAlign: 'center' }}>
                <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.75rem' }}>
                  {stat.label}
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 600, color: '#111827' }}>
                  {stat.value}
                </Typography>
              </Box>
            ))}
          </Box>
        )}
      </CardContent>
      {showActions && item.actions && (
        <CardActions sx={{ justifyContent: 'flex-end', borderTop: '1px solid #F3F4F6', pt: 2 }}>
          {item.actions.map((action, index) => (
            <Typography
              key={index}
              variant="button"
              onClick={action.onClick}
              sx={{
                color: MANAGEMENT_COLORS.primary,
                fontWeight: 500,
                cursor: 'pointer',
                mr: index < item.actions.length - 1 ? 2 : 0,
                '&:hover': {
                  textDecoration: 'underline',
                },
              }}
            >
              {action.label}
            </Typography>
          ))}
        </CardActions>
      )}
    </MuiCard>
  );
};
