import React from 'react';
import { Box, Typography, Link, IconButton } from '@mui/material';
import {
  Facebook,
  Twitter,
  LinkedIn,
  Instagram,
  GitHub,
} from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface FooterLink {
  label: string;
  href?: string;
  icon?: React.ReactNode;
}

export interface FooterProps {
  links?: FooterLink[];
  copyright?: string;
  socialLinks?: FooterLink[];
}

const defaultSocialLinks: FooterLink[] = [
  { label: 'Twitter', href: 'https://twitter.com', icon: <Twitter /> },
  { label: 'Facebook', href: 'https://facebook.com', icon: <Facebook /> },
  { label: 'LinkedIn', href: 'https://linkedin.com', icon: <LinkedIn /> },
  { label: 'GitHub', href: 'https://github.com', icon: <GitHub /> },
];

/**
 * Footer Component
 * Enterprise-grade application footer
 */
export const Footer: React.FC<FooterProps> = ({
  links = [],
  copyright = `© ${new Date().getFullYear()} RapidAssist. All rights reserved.`,
  socialLinks = defaultSocialLinks,
}) => {
  return (
    <Box
      component="footer"
      sx={{
        backgroundColor: '#FFFFFF',
        borderTop: '1px solid #E5E7EB',
        padding: '24px 0',
        mt: 'auto',
      }}
    >
      <Box
        sx={{
          maxWidth: 1200,
          margin: '0 auto',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: 3,
        }}
      >
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
          {links.map((link, index) => (
            <Link
              key={index}
              href={link.href}
              sx={{
                color: '#6B7280',
                textDecoration: 'none',
                fontSize: '0.875rem',
                fontWeight: 500,
                '&:hover': {
                  color: MANAGEMENT_COLORS.primary,
                },
              }}
            >
              {link.icon && (
                <span style={{ marginRight: 6, verticalAlign: 'middle' }}>
                  {link.icon}
                </span>
              )}
              {link.label}
            </Link>
          ))}
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {socialLinks.map((link, index) => (
            <IconButton
              key={index}
              href={link.href}
              size="small"
              sx={{
                color: '#9CA3AF',
                '&:hover': {
                  color: MANAGEMENT_COLORS.primary,
                  backgroundColor: '#F3F4F6',
                },
              }}
            >
              {link.icon}
            </IconButton>
          ))}
        </Box>
      </Box>

      <Box
        sx={{
          maxWidth: 1200,
          margin: '16px auto 0',
          textAlign: 'center',
        }}
      >
        <Typography
          variant="body2"
          sx={{
            color: '#9CA3AF',
            fontSize: '0.875rem',
          }}
        >
          {copyright}
        </Typography>
      </Box>
    </Box>
  );
};
