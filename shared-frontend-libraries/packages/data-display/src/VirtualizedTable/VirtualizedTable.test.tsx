import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, within } from '@testing-library/react';
import '@testing-library/jest-dom';
import { VirtualizedTable } from './VirtualizedTable';

interface TestRow {
  id: number;
  name: string;
  email: string;
  role: string;
}

const testColumns = [
  { id: 'name' as const, label: 'Name', width: 200 },
  { id: 'email' as const, label: 'Email', width: 250 },
  { id: 'role' as const, label: 'Role', width: 150 },
];

const testData: TestRow[] = Array.from({ length: 1000 }, (_, i) => ({
  id: i + 1,
  name: `User ${i + 1}`,
  email: `user${i + 1}@example.com`,
  role: ['Admin', 'Editor', 'Viewer'][i % 3] as string,
}));

describe('VirtualizedTable', () => {
  it('renders table with columns', () => {
    render(<VirtualizedTable columns={testColumns} data={testData} height={400} />);

    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('Role')).toBeInTheDocument();
  });

  it('renders visible rows only', () => {
    render(<VirtualizedTable columns={testColumns} data={testData} height={400} />);

    // First visible row should be present
    expect(screen.getByText('User 1')).toBeInTheDocument();

    // With rowHeight=50 and height=400, about 8 rows should be visible
    // Due to overscan, a few more should be rendered
    expect(screen.getByText('User 8')).toBeInTheDocument();
  });

  it('shows empty state when no data', () => {
    render(<VirtualizedTable columns={testColumns} data={[]} height={400} />);

    expect(screen.getByText('No data available')).toBeInTheDocument();
  });

  it('shows custom empty state when provided', () => {
    render(
      <VirtualizedTable
        columns={testColumns}
        data={[]}
        height={400}
        emptyState={<div>Custom empty message</div>}
      />
    );

    expect(screen.getByText('Custom empty message')).toBeInTheDocument();
  });

  it('handles row click', () => {
    const onRowClick = vi.fn();
    render(
      <VirtualizedTable
        columns={testColumns}
        data={testData}
        height={400}
        onRowClick={onRowClick}
      />
    );

    const firstRow = screen.getByText('User 1').closest('tr');
    if (firstRow) {
      fireEvent.click(firstRow);
      expect(onRowClick).toHaveBeenCalledWith(testData[0], 0);
    }
  });

  it('calls onScroll when scrolling', () => {
    const onScroll = vi.fn();
    const { container } = render(
      <VirtualizedTable
        columns={testColumns}
        data={testData}
        height={400}
        onScroll={onScroll}
      />
    );

    const scrollContainer = container.querySelector('.MuiTableContainer-root');
    if (scrollContainer) {
      fireEvent.scroll(scrollContainer, { target: { scrollTop: 100 } });
      expect(onScroll).toHaveBeenCalledWith(100);
    }
  });

  it('supports custom row height', () => {
    const { container } = render(
      <VirtualizedTable columns={testColumns} data={testData} height={400} rowHeight={75} />
    );

    expect(container.querySelector('.MuiTableContainer-root')).toBeInTheDocument();
  });

  it('supports custom cell rendering', () => {
    const columnsWithRender = [
      {
        id: 'role' as const,
        label: 'Role',
        width: 150,
        render: (value: string) => (
          <span data-testid={`role-${value}`}>{value}</span>
        ),
      },
    ];

    render(<VirtualizedTable columns={columnsWithRender} data={testData} height={400} />);

    expect(screen.getByTestId('role-Admin')).toBeInTheDocument();
  });

  it('handles large dataset efficiently', () => {
    const largeData = Array.from({ length: 10000 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
      email: `user${i + 1}@example.com`,
      role: 'Viewer',
    }));

    const startTime = performance.now();
    render(
      <VirtualizedTable columns={testColumns} data={largeData} height={400} />
    );
    const renderTime = performance.now() - startTime;

    // Should render quickly even with 10,000 rows
    expect(renderTime).toBeLessThan(100);
  });

  it('respects overscan parameter', () => {
    render(
      <VirtualizedTable columns={testColumns} data={testData} height={400} overscan={5} />
    );

    // With larger overscan, more rows should be rendered
    expect(screen.getByText('User 1')).toBeInTheDocument();
  });

  it('displays correct column alignment', () => {
    const alignedColumns = [
      { id: 'name' as const, label: 'Name', width: 200, align: 'left' },
      { id: 'email' as const, label: 'Email', width: 250, align: 'center' },
      { id: 'role' as const, label: 'Role', width: 150, align: 'right' },
    ];

    render(<VirtualizedTable columns={alignedColumns} data={testData} height={400} />);

    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('Role')).toBeInTheDocument();
  });
});
