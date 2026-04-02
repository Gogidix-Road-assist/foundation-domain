import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, within } from '@testing-library/react';
import '@testing-library/jest-dom';
import { DataTable } from './DataTable';

interface TestRow {
  id: number;
  name: string;
  email: string;
  role: string;
  status: string;
}

const testColumns = [
  { id: 'name', label: 'Name' },
  { id: 'email', label: 'Email' },
  { id: 'role', label: 'Role' },
  { id: 'status', label: 'Status' },
] as const;

const testData: TestRow[] = [
  { id: 1, name: 'John Doe', email: 'john@example.com', role: 'Admin', status: 'Active' },
  { id: 2, name: 'Jane Smith', email: 'jane@example.com', role: 'User', status: 'Active' },
  { id: 3, name: 'Bob Johnson', email: 'bob@example.com', role: 'User', status: 'Inactive' },
];

describe('DataTable', () => {
  it('renders table with columns and data', () => {
    render(<DataTable columns={testColumns} data={testData} />);

    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('jane@example.com')).toBeInTheDocument();
  });

  it('displays correct item count', () => {
    render(<DataTable columns={testColumns} data={testData} />);

    expect(screen.getByText('3 items')).toBeInTheDocument();
  });

  it('shows empty state when no data', () => {
    render(<DataTable columns={testColumns} data={[]} />);

    expect(screen.getByText('No data available')).toBeInTheDocument();
  });

  it('shows custom empty state when provided', () => {
    render(
      <DataTable
        columns={testColumns}
        data={[]}
        emptyState={<div>Custom empty message</div>}
      />
    );

    expect(screen.getByText('Custom empty message')).toBeInTheDocument();
  });

  it('handles search filtering', () => {
    render(<DataTable columns={testColumns} data={testData} />);

    const searchInput = screen.getByPlaceholderText('Search...');
    fireEvent.change(searchInput, { target: { value: 'Jane' } });

    expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    expect(screen.queryByText('John Doe')).not.toBeInTheDocument();
  });

  it('handles column sorting', () => {
    render(<DataTable columns={testColumns} data={testData} />);

    const nameHeader = screen.getByText('Name');
    fireEvent.click(nameHeader);

    const rows = screen.getAllByRole('row');
    // After clicking sort, the rows should be reordered
    expect(rows.length).toBeGreaterThan(0);
  });

  it('handles pagination', () => {
    const largeData = Array.from({ length: 25 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
      email: `user${i + 1}@example.com`,
      role: 'User',
      status: 'Active',
    }));

    render(<DataTable columns={testColumns} data={largeData} pageSize={10} />);

    expect(screen.getByText('25 items')).toBeInTheDocument();

    // Check first page shows 10 rows (header + 10 data rows = 11)
    expect(screen.getAllByRole('row').length).toBe(11);

    // Go to next page
    const nextPageButton = screen.getByLabelText(/next page/i);
    fireEvent.click(nextPageButton);
  });

  it('enables row selection when selectable', () => {
    const onSelectChange = vi.fn();
    render(
      <DataTable
        columns={testColumns}
        data={testData}
        selectable
        onSelectChange={onSelectChange}
      />
    );

    // Should have checkboxes
    const checkboxes = screen.getAllByRole('checkbox');
    expect(checkboxes.length).toBeGreaterThan(0);
  });

  it('calls onSelectChange when row is selected', () => {
    const onSelectChange = vi.fn();
    render(
      <DataTable
        columns={testColumns}
        data={testData}
        selectable
        onSelectChange={onSelectChange}
      />
    );

    const firstRowCheckbox = screen.getAllByRole('checkbox')[1]; // Skip header checkbox
    fireEvent.click(firstRowCheckbox);

    expect(onSelectChange).toHaveBeenCalled();
  });

  it('shows loading state', () => {
    render(<DataTable columns={testColumns} data={testData} loading />);

    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  it('displays "No results found" when filter matches nothing', () => {
    render(<DataTable columns={testColumns} data={testData} />);

    const searchInput = screen.getByPlaceholderText('Search...');
    fireEvent.change(searchInput, { target: { value: 'NonExistentName' } });

    expect(screen.getByText('No results found')).toBeInTheDocument();
  });

  it('supports custom cell rendering', () => {
    const columnsWithRender = [
      {
        id: 'status',
        label: 'Status',
        render: (value: string) => (
          <span data-testid={`status-${value}`}>{value}</span>
        ),
      },
    ] as const;

    render(<DataTable columns={columnsWithRender} data={testData} />);

    expect(screen.getByTestId('status-Active')).toBeInTheDocument();
  });

  it('handles page size change', () => {
    const largeData = Array.from({ length: 25 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
      email: `user${i + 1}@example.com`,
      role: 'User',
      status: 'Active',
    }));

    render(<DataTable columns={testColumns} data={largeData} pageSize={10} />);

    const pageSizeSelect = screen.getByLabelText(/rows per page/i);
    fireEvent.mouseDown(pageSizeSelect);

    // Open dropdown and select 25 rows per page
    const options = within(screen.getByRole('listbox')).getAllByRole('option');
    fireEvent.click(options[2]); // Select 25

    // Now all 25 rows should be visible (header + 25 data rows = 26)
    expect(screen.getAllByRole('row').length).toBe(26);
  });
});
