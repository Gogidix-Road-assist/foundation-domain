import React, { useState, useCallback, useMemo } from 'react';
import { Box } from '@mui/material';
import { DataGrid as MuiDataGrid, GridColDef, GridRowSelectionModel } from '@mui/x-data-grid';
import { motion } from 'framer-motion';

export interface DataGridProps {
  columns: GridColDef[];
  rows: any[];
  loading?: boolean;
  pageSize?: number;
  pageSizeOptions?: number[];
  rowHeight?: number;
  checkboxSelection?: boolean;
  disableRowSelectionOnClick?: boolean;
  onSelectionChange?: (selectionModel: GridRowSelectionModel) => void;
  onRowClick?: (row: any) => void;
  height?: number | string;
  autoHeight?: boolean;
  getRowId?: (row: any) => string | number;
  density?: 'comfortable' | 'compact' | 'standard';
  disableColumnFilter?: boolean;
  disableColumnSorting?: boolean;
  disableColumnMenu?: boolean;
}

export function DataGrid({
  columns,
  rows,
  loading = false,
  pageSize = 10,
  pageSizeOptions = [10, 25, 50, 100],
  rowHeight = 52,
  checkboxSelection = false,
  disableRowSelectionOnClick = false,
  onSelectionChange,
  onRowClick,
  height = 500,
  autoHeight = false,
  getRowId,
  density = 'standard',
  disableColumnFilter = false,
  disableColumnSorting = false,
  disableColumnMenu = false,
}: DataGridProps) {
  const [selectionModel, setSelectionModel] = useState<GridRowSelectionModel>([]);

  const handleSelectionChange = useCallback(
    (newSelectionModel: GridRowSelectionModel) => {
      setSelectionModel(newSelectionModel);
      onSelectionChange?.(newSelectionModel);
    },
    [onSelectionChange]
  );

  const handleRowClick = useCallback(
    (params: any) => {
      onRowClick?.(params.row);
    },
    [onRowClick]
  );

  // Memoize enhanced columns with styling
  const enhancedColumns = useMemo(
    () =>
      columns.map((col) => ({
        ...col,
        headerClassName: 'data-grid-header',
        cellClassName: 'data-grid-cell',
      })),
    [columns]
  );

  return (
    <Box
      sx={{
        width: '100%',
        '& .data-grid-header': {
          backgroundColor: '#f9fafb',
          fontWeight: 600,
          color: '#374151',
          borderBottom: '2px solid #e5e7eb',
        },
        '& .data-grid-cell': {
          borderBottom: '1px solid #f3f4f6',
          '&:focus': {
            outline: 'none',
          },
        },
        '& .MuiDataGrid-row': {
          '&:hover': {
            backgroundColor: '#f9fafb',
          },
          '&.Mui-selected': {
            backgroundColor: '#eff6ff',
          },
          '&.Mui-selected:hover': {
            backgroundColor: '#dbeafe',
          },
        },
        '& .MuiDataGrid-columnSeparator': {
          color: '#e5e7eb',
        },
        '& .MuiDataGrid-footerContainer': {
          borderTop: '1px solid #e5e7eb',
          backgroundColor: '#f9fafb',
        },
      }}
    >
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <MuiDataGrid
          rows={rows}
          columns={enhancedColumns}
          loading={loading}
          pageSizeOptions={pageSizeOptions}
          initialState={{
            pagination: {
              paginationModel: { pageSize },
            },
          }}
          rowHeight={rowHeight}
          checkboxSelection={checkboxSelection}
          disableRowSelectionOnClick={disableRowSelectionOnClick}
          onRowSelectionModelChange={handleSelectionChange}
          onRowClick={handleRowClick}
          getRowId={getRowId}
          density={density}
          disableColumnFilter={disableColumnFilter}
          disableColumnSorting={disableColumnSorting}
          disableColumnMenu={disableColumnMenu}
          autoHeight={autoHeight}
          sx={{
            height: autoHeight ? 'auto' : height,
            border: '1px solid #e5e7eb',
            borderRadius: '8px',
            '& .MuiDataGrid-virtualScroller': {
              scrollbarWidth: 'thin',
              scrollbarColor: '#cbd5e1 #f1f5f9',
              '&::-webkit-scrollbar': {
                width: '8px',
                height: '8px',
              },
              '&::-webkit-scrollbar-track': {
                background: '#f1f5f9',
              },
              '&::-webkit-scrollbar-thumb': {
                background: '#cbd5e1',
                borderRadius: '4px',
                '&:hover': {
                  background: '#94a3b8',
                },
              },
            },
          }}
        />
      </motion.div>
    </Box>
  );
}

DataGrid.displayName = 'DataGrid';

// Export types for convenience
export type { GridColDef, GridRowSelectionModel };
