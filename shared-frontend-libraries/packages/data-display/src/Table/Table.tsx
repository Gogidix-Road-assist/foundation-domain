import React from 'react';
import {
  Table as MuiTable,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  Box,
  Checkbox,
  Chip,
  Avatar,
  IconButton,
} from '@mui/material';
import { ArrowUpward, ArrowDownward } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type SortDirection = 'asc' | 'desc';
export type SortableColumn<T> = {
  key: string;
  label: string;
  sortable?: boolean;
  width?: number | string;
  render?: (value: T, row: any) => React.ReactNode;
}

export interface TableProps {
  columns: SortableColumn<any>[];
  data: any[];
  onSort?: (column: string, direction: SortDirection) => void;
  sortColumn?: string;
  sortDirection?: SortDirection;
  selectable?: boolean;
  onSelectionChange?: (selected: any[]) => void;
  selected?: any[];
  pageSize?: number;
  currentPage?: number;
  onPageChange?: (page: number) => void;
  loading?: boolean;
  emptyState?: React.ReactNode;
}

/**
 * Table Component
 * Enterprise-grade data table with sorting and pagination
 */
export const Table: React.FC<TableProps> = ({
  columns,
  data,
  onSort,
  sortColumn,
  sortDirection = 'desc',
  selectable = false,
  onSelectionChange,
  selected = [],
  pageSize = 10,
  currentPage = 1,
  onPageChange,
  loading = false,
  emptyState,
}) => {
  const handleSort = (column: string) => {
    const newDirection: SortDirection =
      sortColumn === column && sortDirection === 'asc' ? 'desc' : 'asc';
    onSort?.(column, newDirection);
  };

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      onSelectionChange?.([...data]);
    } else {
      onSelectionChange?.([]);
    }
  };

  const handleSelectRow = (row: any, checked: boolean) => {
    if (checked) {
      if (!selected.includes(row)) {
        onSelectionChange?.([...selected, row]);
      }
    } else {
      onSelectionChange?.(selected.filter((r) => r !== row));
    }
  };

  const isSelected = (row: any) => selected.includes(row);
  const isAllSelected = data.length > 0 && selected.length === data.length;

  const paginatedData = data.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  return (
    <TableContainer component={Box} sx={{ width: '100%', overflow: 'auto' }}>
      <MuiTable stickyHeader>
        <TableHead>
          <TableRow>
            {selectable && (
              <TableCell padding="checkbox" sx={{ width: 50 }}>
                <Checkbox
                  checked={isAllSelected}
                  onChange={(_, checked) => handleSelectAll(checked)}
                  sx={{ width: 20 }}
                />
              </TableCell>
            )}
            {columns.map((column) => (
              <TableCell
                key={column.key}
                sortDirection={column.sortable && sortColumn === column.key ? sortDirection : false}
                onClick={column.sortable ? () => handleSort(column.key) : undefined}
                sx={{
                  fontWeight: 600,
                  color: '#374151',
                  cursor: column.sortable ? 'pointer' : 'default',
                  minWidth: column.width,
                }}
              >
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  {column.label}
                  {column.sortable && sortColumn === column.key && (
                    <IconButton size="small" sx={{ width: 20, height: 20 }}>
                      {sortDirection === 'asc' ? <ArrowDownward /> : <ArrowUpward />}
                    </IconButton>
                  )}
                </Box>
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
      </MuiTable>
      <TableBody>
        {loading ? (
          <TableRow>
            <TableCell colSpan={columns.length + (selectable ? 1 : 0)} align="center">
              Loading...
            </TableCell>
          </TableRow>
        ) : paginatedData.length === 0 && emptyState ? (
          <TableRow>
            <TableCell colSpan={columns.length + (selectable ? 1 : 0)} align="center">
              {emptyState}
            </TableCell>
          </TableRow>
        ) : paginatedData.map((row, index) => (
          <TableRow
            key={index}
            selected={isSelected(row)}
            sx={{
              '&.Mui-selected': {
                backgroundColor: `${MANAGEMENT_COLORS.primary}10`,
              },
            }}
          >
            {selectable && (
              <TableCell padding="checkbox" sx={{ width: 50 }}>
                <Checkbox
                  checked={isSelected(row)}
                  onChange={(_, checked) => handleSelectRow(row, checked)}
                  sx={{ width: 20 }}
                />
              </TableCell>
            )}
            {columns.map((column) => (
              <TableCell key={column.key} sx={{ minWidth: column.width }}>
                {column.render ? column.render(row[column.key], row) : row[column.key]}
              </TableCell>
            ))}
          </TableRow>
        ))}
      </TableBody>
      {data.length > pageSize && (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 2, borderTop: '1px solid #E5E7EB' }}>
          <IconButton
            onClick={() => onPageChange?.(currentPage - 1)}
            disabled={currentPage === 1}
            size="small"
          >
            <ArrowUpward />
          </IconButton>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            {Array.from({ length: Math.ceil(data.length / pageSize) }).map((_, i) => (
              <Box
                key={i}
                onClick={() => onPageChange?.(i + 1)}
                sx={{
                  width: 8,
                  height: 8,
                  borderRadius: '50%',
                  backgroundColor: i + 1 === currentPage ? MANAGEMENT_COLORS.primary : '#E5E7EB',
                  color: i + 1 === currentPage ? '#FFFFFF' : '#6B7280',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '0.75rem',
                }}
              >
                {i + 1}
              </Box>
            ))}
          </Box>
          )}
          <IconButton
            onClick={() => onPageChange?.(currentPage + 1)}
            disabled={currentPage === Math.ceil(data.length / pageSize)}
            size="small"
          >
            <ArrowDownward />
          </IconButton>
        </Box>
      )}
    </TableContainer>
  );
};
