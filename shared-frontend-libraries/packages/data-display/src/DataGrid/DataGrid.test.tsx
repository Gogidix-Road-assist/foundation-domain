import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { DataGrid } from './DataGrid';

const sampleColumns = [
  { field: 'id', headerName: 'ID', width: 70 },
  { field: 'name', headerName: 'Name', width: 200 },
  { field: 'email', headerName: 'Email', width: 250 },
  { field: 'role', headerName: 'Role', width: 150 },
];

const sampleRows = [
  { id: 1, name: 'John Doe', email: 'john@example.com', role: 'Admin' },
  { id: 2, name: 'Jane Smith', email: 'jane@example.com', role: 'Editor' },
  { id: 3, name: 'Bob Johnson', email: 'bob@example.com', role: 'Viewer' },
];

describe('DataGrid', () => {
  it('renders with columns and rows', () => {
    render(<DataGrid columns={sampleColumns} rows={sampleRows} />);

    expect(screen.getByText('ID')).toBeInTheDocument();
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('jane@example.com')).toBeInTheDocument();
  });

  it('displays loading state', () => {
    render(<DataGrid columns={sampleColumns} rows={sampleRows} loading={true} />);

    // MUI DataGrid shows a loading overlay
    const loadingOverlay = document.querySelector('.MuiDataGrid-overlay');
    expect(loadingOverlay).toBeInTheDocument();
  });

  it('enables checkbox selection when checkboxSelection is true', () => {
    render(<DataGrid columns={sampleColumns} rows={sampleRows} checkboxSelection={true} />);

    // Checkboxes should be present
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    expect(checkboxes.length).toBeGreaterThan(0);
  });

  it('calls onSelectionChange when rows are selected', async () => {
    const onSelectionChange = vi.fn();
    render(
      <DataGrid
        columns={sampleColumns}
        rows={sampleRows}
        checkboxSelection={true}
        onSelectionChange={onSelectionChange}
      />
    );

    // Find the first row checkbox and click it
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    const firstRowCheckbox = checkboxes[1]; // Skip the header checkbox
    fireEvent.click(firstRowCheckbox);

    await waitFor(() => {
      expect(onSelectionChange).toHaveBeenCalled();
    });
  });

  it('renders with custom height', () => {
    const { container } = render(
      <DataGrid columns={sampleColumns} rows={sampleRows} height={300} />
    );

    const dataGrid = container.querySelector('.MuiDataGrid-root');
    expect(dataGrid).toHaveStyle({ height: '300px' });
  });

  it('renders with autoHeight', () => {
    const { container } = render(
      <DataGrid columns={sampleColumns} rows={sampleRows} autoHeight={true} />
    );

    const dataGrid = container.querySelector('.MuiDataGrid-root');
    expect(dataGrid).not.toHaveStyle({ height: '500px' });
  });

  it('applies custom density', () => {
    render(<DataGrid columns={sampleColumns} rows={sampleRows} density="compact" />);

    const dataGrid = document.querySelector('.MuiDataGrid-root');
    expect(dataGrid).toBeInTheDocument();
  });

  it('calls onRowClick when a row is clicked', () => {
    const onRowClick = vi.fn();
    render(
      <DataGrid columns={sampleColumns} rows={sampleRows} onRowClick={onRowClick} />
    );

    const firstRow = screen.getByText('John Doe');
    fireEvent.click(firstRow);

    expect(onRowClick).toHaveBeenCalledWith(sampleRows[0]);
  });

  it('renders empty state when no rows', () => {
    render(<DataGrid columns={sampleColumns} rows={[]} />);

    expect(screen.getByText('No rows')).toBeInTheDocument();
  });

  it('uses custom getRowId', () => {
    const customRows = [
      { customId: 'A', name: 'Test 1' },
      { customId: 'B', name: 'Test 2' },
    ];

    const customColumns = [
      { field: 'customId', headerName: 'ID' },
      { field: 'name', headerName: 'Name' },
    ];

    render(
      <DataGrid
        columns={customColumns}
        rows={customRows}
        getRowId={(row) => row.customId}
      />
    );

    expect(screen.getByText('Test 1')).toBeInTheDocument();
  });

  it('handles pagination', async () => {
    const largeRows = Array.from({ length: 25 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
    }));

    render(<DataGrid columns={sampleColumns} rows={largeRows} pageSize={10} />);

    // Should show pagination controls
    const pageSizeSelect = document.querySelector('[aria-label*="Rows per page"]');
    expect(pageSizeSelect).toBeInTheDocument();
  });
});
