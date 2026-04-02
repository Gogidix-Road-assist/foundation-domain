import React, { useState, useMemo, useCallback } from 'react';
import { Box, Table as MuiTable, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination, Checkbox, TableSortLabel, Toolbar, Typography, IconButton, TextField, InputAdornment } from '@mui/material';
import { visuallyHidden } from '@mui/utils';
import { motion, AnimatePresence } from 'framer-motion';
import SearchIcon from '@mui/icons-material/Search';

// Types
export interface DataTableColumn<T = any> {
  id: keyof T | string;
  label: string;
  width?: number | string;
  align?: 'left' | 'center' | 'right';
  sortable?: boolean;
  filterable?: boolean;
  render?: (value: any, row: T) => React.ReactNode;
}

export interface DataTableProps<T = any> {
  columns: DataTableColumn<T>[];
  data: T[];
  loading?: boolean;
  selectable?: boolean;
  onSelectChange?: (selected: T[]) => void;
  defaultSortColumn?: string;
  defaultSortDirection?: 'asc' | 'desc';
  pageSize?: number;
  pageSizeOptions?: number[];
  emptyState?: React.ReactNode;
}

type Order = 'asc' | 'desc';

// Helper functions
function descendingComparator<T>(a: T, b: T, orderBy: keyof T) {
  if (b[orderBy] < a[orderBy]) return -1;
  if (b[orderBy] > a[orderBy]) return 1;
  return 0;
}

function getComparator<T>(order: Order, orderBy: keyof T): (a: T, b: T) => number {
  return order === 'desc'
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

function stableSort<T>(array: T[], comparator: (a: T, b: T) => number) {
  const stabilizedThis = array.map((el, index) => [el, index] as [T, number]);
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) return order;
    return a[1] - b[1];
  });
  return stabilizedThis.map((el) => el[0]);
}

// Enhanced Table Head with Sorting
interface EnhancedTableHeadProps<T> {
  columns: DataTableColumn<T>[];
  order: Order;
  orderBy: string;
  onRequestSort: (event: React.MouseEvent<unknown>, property: string) => void;
  selectable?: boolean;
}

function EnhancedTableHead<T>(props: EnhancedTableHeadProps<T>) {
  const { columns, order, orderBy, onRequestSort, selectable } = props;

  return (
    <TableHead>
      <TableRow>
        {selectable && (
          <TableCell padding="checkbox">
            <Checkbox
              color="primary"
              disabled
            />
          </TableCell>
        )}
        {columns.map((column) => (
          <TableCell
            key={String(column.id)}
            align={column.align || 'left'}
            style={{ width: column.width }}
            sortDirection={orderBy === String(column.id) ? order : false}
          >
            {column.sortable !== false ? (
              <TableSortLabel
                active={orderBy === String(column.id)}
                direction={orderBy === String(column.id) ? order : 'asc'}
                onClick={(event) => onRequestSort(event, String(column.id))}
              >
                {column.label}
                {orderBy === String(column.id) ? (
                  <Box component="span" sx={visuallyHidden}>
                    {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                  </Box>
                ) : null}
              </TableSortLabel>
            ) : (
              column.label
            )}
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

// DataTable Component
export function DataTable<T extends Record<string, any>>({
  columns,
  data,
  loading = false,
  selectable = false,
  onSelectChange,
  defaultSortColumn,
  defaultSortDirection = 'asc',
  pageSize = 10,
  pageSizeOptions = [5, 10, 25, 50],
  emptyState,
}: DataTableProps<T>) {
  const [order, setOrder] = useState<Order>(defaultSortDirection);
  const [orderBy, setOrderBy] = useState(defaultSortColumn || columns[0]?.id.toString() || '');
  const [selected, setSelected] = useState<readonly string[]>([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(pageSize);
  const [filter, setFilter] = useState('');

  // Handle sort request
  const handleRequestSort = useCallback((event: React.MouseEvent<unknown>, property: string) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  }, [order, orderBy]);

  // Handle select all
  const handleSelectAllClick = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      const newSelected = filteredRows.map((n) => JSON.stringify(n));
      setSelected(newSelected);
      onSelectChange?.(filteredRows);
      return;
    }
    setSelected([]);
    onSelectChange?.([]);
  }, [filteredRows, onSelectChange]);

  // Handle click
  const handleClick = useCallback((event: React.MouseEvent<unknown>, row: T) => {
    if (!selectable) return;
    const id = JSON.stringify(row);
    const selectedIndex = selected.indexOf(id);
    let newSelected: readonly string[] = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, id);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.slice(1);
    } else if (selectedIndex === selected.length - 1) {
      newSelected = newSelected.slice(0, -1);
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selected.slice(0, selectedIndex),
        selected.slice(selectedIndex + 1),
      );
    }

    setSelected(newSelected);
    onSelectChange?.(data.filter((row) => newSelected.includes(JSON.stringify(row))));
  }, [data, selected, selectable, onSelectChange]);

  // Handle page change
  const handleChangePage = useCallback((event: unknown, newPage: number) => {
    setPage(newPage);
  }, []);

  // Handle rows per page change
  const handleChangeRowsPerPage = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  }, []);

  // Filter rows
  const filteredRows = useMemo(() => {
    if (!filter) return data;
    return data.filter((row) => {
      return columns.some((column) => {
        const value = row[column.id as keyof T];
        if (value === null || value === undefined) return false;
        return String(value).toLowerCase().includes(filter.toLowerCase());
      });
    });
  }, [data, filter, columns]);

  // Sort rows
  const sortedRows = useMemo(
    () => stableSort(filteredRows, getComparator(order, orderBy as keyof T)),
    [filteredRows, order, orderBy]
  );

  // Paginate rows
  const paginatedRows = useMemo(() => {
    return sortedRows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
  }, [sortedRows, page, rowsPerPage]);

  const isSelected = (row: T) => selected.includes(JSON.stringify(row));

  // Empty state
  if (data.length === 0 && !loading) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        {emptyState || <Typography color="text.secondary">No data available</Typography>}
      </Box>
    );
  }

  return (
    <Box sx={{ width: '100%' }}>
      <Paper sx={{ width: '100%', mb: 2 }}>
        {/* Toolbar with search */}
        <Toolbar
          sx={{
            pl: { sm: 2 },
            pr: { xs: 1, sm: 1 },
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <Typography variant="h6" id="tableTitle" component="div">
            {data.length} {data.length === 1 ? 'item' : 'items'}
          </Typography>
          {columns.some((col) => col.filterable !== false) && (
            <TextField
              size="small"
              placeholder="Search..."
              value={filter}
              onChange={(e) => setFilter(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
              sx={{ width: 250 }}
            />
          )}
        </Toolbar>

        <TableContainer>
          <MuiTable
            aria-labelledby="tableTitle"
            size="medium"
            sx={{ minWidth: 750 }}
          >
            <EnhancedTableHead
              columns={columns}
              order={order}
              orderBy={orderBy}
              onRequestSort={handleRequestSort}
              selectable={selectable}
            />
            <TableBody>
              <AnimatePresence>
                {loading ? (
                  <TableRow>
                    <TableCell colSpan={columns.length + (selectable ? 1 : 0)} align="center">
                      <Typography color="text.secondary">Loading...</Typography>
                    </TableCell>
                  </TableRow>
                ) : paginatedRows.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={columns.length + (selectable ? 1 : 0)} align="center">
                      <Typography color="text.secondary">No results found</Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  paginatedRows.map((row, index) => {
                    const isItemSelected = isSelected(row);
                    const labelId = `enhanced-table-checkbox-${index}`;

                    return (
                      <TableRow
                        hover
                        onClick={(event) => handleClick(event, row)}
                        role="checkbox"
                        aria-checked={isItemSelected}
                        tabIndex={-1}
                        key={index}
                        selected={isItemSelected}
                        component={motion.tr}
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0 }}
                        transition={{ delay: index * 0.02 }}
                      >
                        {selectable && (
                          <TableCell padding="checkbox">
                            <Checkbox
                              color="primary"
                              checked={isItemSelected}
                              inputProps={{ 'aria-labelledby': labelId }}
                            />
                          </TableCell>
                        )}
                        {columns.map((column) => (
                          <TableCell
                            key={String(column.id)}
                            align={column.align || 'left'}
                          >
                            {column.render ? (
                              column.render(row[column.id as keyof T], row)
                            ) : (
                              String(row[column.id as keyof T] ?? '')
                            )}
                          </TableCell>
                        ))}
                      </TableRow>
                    );
                  })
                )}
              </AnimatePresence>
            </TableBody>
          </MuiTable>
        </TableContainer>

        {/* Pagination */}
        <TablePagination
          rowsPerPageOptions={pageSizeOptions}
          component="div"
          count={filteredRows.length}
          rowsPerPage={rowsPerPage}
          page={Math.min(page, Math.ceil(filteredRows.length / rowsPerPage) - 1)}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </Paper>
    </Box>
  );
}

DataTable.displayName = 'DataTable';
