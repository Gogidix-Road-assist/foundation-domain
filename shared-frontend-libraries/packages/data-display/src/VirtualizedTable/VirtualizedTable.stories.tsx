import type { Meta, StoryObj } from '@storybook/react';
import { VirtualizedTable } from './VirtualizedTable';

const meta = {
  title: 'Data Display/VirtualizedTable',
  component: VirtualizedTable,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof VirtualizedTable>;

export default meta;
type Story = StoryObj<typeof VirtualizedTable>;

interface UserData {
  id: number;
  name: string;
  email: string;
  role: string;
  status: string;
}

const sampleColumns = [
  { id: 'name' as const, label: 'Name', width: 200 },
  { id: 'email' as const, label: 'Email', width: 250 },
  { id: 'role' as const, label: 'Role', width: 150 },
  {
    id: 'status' as const,
    label: 'Status',
    width: 120,
    render: (value: string) => (
      <span
        style={{
          padding: '4px 12px',
          borderRadius: '12px',
          fontSize: '12px',
          fontWeight: 500,
          backgroundColor: value === 'Active' ? '#d1fae5' : '#f3f4f6',
          color: value === 'Active' ? '#065f46' : '#374151',
        }}
      >
        {value}
      </span>
    ),
  },
];

const sampleData: UserData[] = Array.from({ length: 1000 }, (_, i) => ({
  id: i + 1,
  name: `User ${i + 1}`,
  email: `user${i + 1}@example.com`,
  role: ['Admin', 'Editor', 'Viewer'][i % 3] as string,
  status: i % 3 === 0 ? 'Inactive' : 'Active',
}));

export const Default: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
    height: 400,
  },
};

export const SmallDataset: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData.slice(0, 10),
    height: 400,
  },
};

export const CustomRowHeight: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
    height: 500,
    rowHeight: 75,
  },
};

export const WithEmptyState: Story = {
  args: {
    columns: sampleColumns,
    data: [],
    height: 400,
    emptyState: <div style={{ padding: '40px', textAlign: 'center' }}>
      <div style={{ fontSize: '48px', marginBottom: '16px' }}>📋</div>
      <div style={{ fontSize: '16px', color: '#6b7280' }}>No data found</div>
    </div>,
  },
};

export const WithRowClick: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
    height: 400,
    onRowClick: (row) => console.log('Row clicked:', row),
  },
};

export const LargeOverscan: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
    height: 400,
    overscan: 10,
  },
};

export const ColumnAlignments: Story = {
  args: {
    columns: [
      { id: 'name' as const, label: 'Name', width: 200, align: 'left' },
      { id: 'email' as const, label: 'Email', width: 250, align: 'center' },
      { id: 'role' as const, label: 'Role', width: 150, align: 'right' },
    ],
    data: sampleData,
    height: 400,
  },
};

export const VeryLargeDataset: Story = {
  args: {
    columns: sampleColumns,
    data: Array.from({ length: 10000 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
      email: `user${i + 1}@example.com`,
      role: ['Admin', 'Editor', 'Viewer'][i % 3] as string,
      status: i % 3 === 0 ? 'Inactive' : 'Active',
    })),
    height: 400,
  },
};
