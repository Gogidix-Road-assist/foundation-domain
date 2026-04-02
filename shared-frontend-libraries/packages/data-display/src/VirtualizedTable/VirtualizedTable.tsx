import React, { useRef, useState, useCallback, useMemo } from 'react';
import {
  TableContainer,
  Table as MuiTable,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Paper,
  Box,
  Typography,
} from '@mui/material';
import { motion } from 'framer-motion';

export interface VirtualizedTableColumn<T = any> {
  id: keyof T | string;
  label: string;
  width: number;
  align?: 'left' | 'center' | 'right';
  render?: (value: any, row: T, index: number) => React.ReactNode;
}

export interface VirtualizedTableProps<T = any> {
  columns: VirtualizedTableColumn<T>[];
  data: T[];
  rowHeight?: number;
  height?: number;
  estimatedRowHeight?: number;
  onRowClick?: (row: T, index: number) => void;
  onScroll?: (scrollTop: number) => void;
  emptyState?: React.ReactNode;
  overscan?: number;
}

export function VirtualizedTable<T extends Record<string, any>>({
  columns,
  data,
  rowHeight = 50,
  height = 400,
  estimatedRowHeight = 50,
  onRowClick,
  onScroll,
  emptyState,
  overscan = 3,
}: VirtualizedTableProps<T>) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [scrollTop, setScrollTop] = useState(0);

  // Calculate total height
  const totalHeight = useMemo(() => data.length * rowHeight, [data.length, rowHeight]);

  // Calculate visible range
  const visibleRange = useMemo(() => {
    const startIndex = Math.max(0, Math.floor(scrollTop / rowHeight) - overscan);
    const endIndex = Math.min(
      data.length,
      Math.ceil((scrollTop + height) / rowHeight) + overscan
    );
    return { startIndex, endIndex };
  }, [scrollTop, rowHeight, height, data.length, overscan]);

  // Handle scroll
  const handleScroll = useCallback(
    (event: React.UIEvent<HTMLDivElement>) => {
      const newScrollTop = event.currentTarget.scrollTop;
      setScrollTop(newScrollTop);
      onScroll?.(newScrollTop);
    },
    [onScroll]
  );

  // Handle row click
  const handleRowClick = useCallback(
    (row: T, index: number) => {
      onRowClick?.(row, index);
    },
    [onRowClick]
  );

  // Empty state
  if (data.length === 0) {
    return (
      <Box sx={{ height, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        {emptyState || <Typography color="text.secondary">No data available</Typography>}
      </Box>
    );
  }

  return (
    <Paper elevation={1} sx={{ overflow: 'hidden' }}>
      <TableContainer
        ref={containerRef}
        onScroll={handleScroll}
        sx={{
          height,
          overflow: 'auto',
          position: 'relative',
        }}
      >
        <MuiTable stickyHeader sx={{ minWidth: '100%' }}>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell
                  key={String(column.id)}
                  align={column.align || 'left'}
                  sx={{
                    width: column.width,
                    minWidth: column.width,
                    fontWeight: 600,
                    backgroundColor: '#f9fafb',
                    borderBottom: '2px solid #e5e7eb',
                    position: 'sticky',
                    top: 0,
                    zIndex: 10,
                  }}
                >
                  {column.label}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {/* Spacer for scrolling */}
            <TableRow sx={{ height: visibleRange.startIndex * rowHeight }}>
              <TableCell colSpan={columns.length} sx={{ padding: 0, border: 'none' }} />
            </TableRow>

            {/* Visible rows */}
            {data.slice(visibleRange.startIndex, visibleRange.endIndex).map((row, i) => {
              const actualIndex = visibleRange.startIndex + i;
              const rowIndex = actualIndex;

              return (
                <motion.tr
                  key={rowIndex}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ duration: 0.15 }}
                  style={{
                    height: rowHeight,
                    cursor: onRowClick ? 'pointer' : 'default',
                  }}
                  onClick={() => handleRowClick(row, rowIndex)}
                >
                  {columns.map((column) => (
                    <TableCell
                      key={String(column.id)}
                      align={column.align || 'left'}
                      sx={{
                        borderBottom: '1px solid #f3f4f6',
                        '&:hover': {
                          backgroundColor: '#f9fafb',
                        },
                      }}
                    >
                      {column.render
                        ? column.render(row[column.id as keyof T], row, rowIndex)
                        : String(row[column.id as keyof T] ?? '')}
                    </TableCell>
                  ))}
                </motion.tr>
              );
            })}

            {/* Spacer for scrolling */}
            <TableRow
              sx={{
                height: Math.max(0, (data.length - visibleRange.endIndex) * rowHeight),
              }}
            >
              <TableCell colSpan={columns.length} sx={{ padding: 0, border: 'none' }} />
            </TableRow>
          </TableBody>
        </MuiTable>
      </TableContainer>
    </Paper>
  );
}

VirtualizedTable.displayName = 'VirtualizedTable';
