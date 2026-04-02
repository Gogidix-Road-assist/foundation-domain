import React from 'react';
import {
  Accordion as MuiAccordion,
  AccordionSummary as MuiAccordionSummary,
  AccordionDetails as MuiAccordionDetails,
  AccordionActions as MuiAccordionActions,
  AccordionProps as MuiAccordionProps,
} from '@mui/material';
import { ExpandMore } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';
import { motion, AnimatePresence } from 'framer-motion';

export interface AccordionProps extends MuiAccordionProps {
  title: React.ReactNode;
  expanded?: boolean;
  onChange?: (event: React.SyntheticEvent, expanded: boolean) => void;
  defaultExpanded?: boolean;
  disabled?: boolean;
  actions?: React.ReactNode;
}

export interface AccordionItem {
  id: string;
  title: React.ReactNode;
  content: React.ReactNode;
  disabled?: boolean;
  defaultExpanded?: boolean;
  actions?: React.ReactNode;
}

export interface AccordionGroupProps {
  items: AccordionItem[];
  allowMultiple?: boolean;
}

/**
 * AccordionSummary Component
 * Header of the accordion that expands/collapses content
 */
const AccordionSummary = ({ children, expanded, disabled }: { children: React.ReactNode; expanded?: boolean; disabled?: boolean }) => (
  <MuiAccordionSummary
    expandIcon={<ExpandMore />}
    sx={{
      minHeight: '48px',
      '& .MuiAccordionSummary-content': {
        margin: '12px 0',
        fontWeight: 500,
        color: '#374151',
      },
      '& .MuiAccordionSummary-expandIconWrapper': {
        color: expanded ? MANAGEMENT_COLORS.primary : '#9CA3AF',
        transition: 'transform 0.2s ease',
      },
      '&.Mui-expanded': {
        minHeight: '48px',
        '& .MuiAccordionSummary-content': {
          margin: '12px 0',
          color: MANAGEMENT_COLORS.primary,
          fontWeight: 600,
        },
      },
      '&.Mui-disabled': {
        opacity: 0.6,
      },
    }}
  >
    {children}
  </MuiAccordionSummary>
);

/**
 * Accordion Component
 * Enterprise-grade collapsible content panel with animations
 */
export const Accordion = React.forwardRef<HTMLDivElement, AccordionProps>(
  ({ title, expanded, onChange, defaultExpanded = false, disabled = false, actions, children, ...props }, ref) => {
    const [internalExpanded, setInternalExpanded] = React.useState(defaultExpanded);
    const isControlled = expanded !== undefined;
    const currentExpanded = isControlled ? expanded : internalExpanded;

    const handleChange = (event: React.SyntheticEvent, isExpanded: boolean) => {
      if (isControlled) {
        onChange?.(event, isExpanded);
      } else {
        setInternalExpanded(isExpanded);
        onChange?.(event, isExpanded);
      }
    };

    return (
      <MuiAccordion
        ref={ref}
        expanded={currentExpanded}
        onChange={handleChange}
        disabled={disabled}
        elevation={currentExpanded ? 2 : 0}
        sx={{
          backgroundColor: '#FFFFFF',
          border: '1px solid #E5E7EB',
          borderRadius: '8px',
          '&:before': {
            display: 'none',
          },
          '&.Mui-expanded': {
            margin: '0 0 8px 0',
            borderColor: MANAGEMENT_COLORS.primary,
          },
        }}
        {...props}
      >
        <AccordionSummary expanded={currentExpanded} disabled={disabled}>
          {title}
        </AccordionSummary>
        <AnimatePresence>
          {currentExpanded && (
            <motion.div
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              transition={{ duration: 0.2 }}
            >
              <MuiAccordionDetails
                sx={{
                  padding: '16px 24px',
                  color: '#6B7280',
                  borderTop: '1px solid #F3F4F6',
                }}
              >
                {children}
              </MuiAccordionDetails>
              {actions && (
                <MuiAccordionActions
                  sx={{
                    padding: '8px 24px 16px',
                    borderTop: '1px solid #F3F4F6',
                  }}
                >
                  {actions}
                </MuiAccordionActions>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </MuiAccordion>
    );
  }
);

Accordion.displayName = 'Accordion';

/**
 * AccordionGroup Component
 * Group of accordions with optional multi-expand
 */
export const AccordionGroup: React.FC<AccordionGroupProps> = ({ items, allowMultiple = false }) => {
  const [expandedItems, setExpandedItems] = React.useState<Set<string>>(new Set());

  const handleAccordionChange = (itemId: string) => (event: React.SyntheticEvent, isExpanded: boolean) => {
    setExpandedItems((prev) => {
      const next = new Set(prev);
      if (isExpanded) {
        if (allowMultiple) {
          next.add(itemId);
        } else {
          next.clear();
          next.add(itemId);
        }
      } else {
        next.delete(itemId);
      }
      return next;
    });
  };

  return (
    <div>
      {items.map((item) => (
        <Accordion
          key={item.id}
          title={item.title}
          expanded={expandedItems.has(item.id)}
          onChange={handleAccordionChange(item.id)}
          defaultExpanded={item.defaultExpanded}
          disabled={item.disabled}
          actions={item.actions}
        >
          {item.content}
        </Accordion>
      ))}
    </div>
  );
};
