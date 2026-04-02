import type { Meta, StoryObj } from '@storybook/react';
import { DataTable, DataTableColumn } from './DataTable';

const meta = {
  title: 'Data Display/DataTable',
  component: DataTable,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof DataTable>;

export default meta;
type Story = StoryObj<typeof DataTable>;

interface UserData {
  id: number;
  name: string;
  email: string;
  role: string;
  status: string;
  lastLogin: string;
}

const sampleColumns: DataTableColumn<UserData>[] = [
  { id: 'name', label: 'Name', width: 200, sortable: true },
  { id: 'email', label: 'Email', width: 250, sortable: true },
  { id: 'role', label: 'Role', width: 150, sortable: true },
  {
    id: 'status',
    label: 'Status',
    width: 120,
    sortable: true,
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
  { id: 'lastLogin', label: 'Last Login', width: 180, sortable: true },
];

const sampleData: UserData[] = [
  { id: 1, name: 'John Doe', email: 'john.doe@example.com', role: 'Admin', status: 'Active', lastLogin: '2024-03-15' },
  { id: 2, name: 'Jane Smith', email: 'jane.smith@example.com', role: 'Editor', status: 'Active', lastLogin: '2024-03-14' },
  { id: 3, name: 'Bob Johnson', email: 'bob.johnson@example.com', role: 'Viewer', status: 'Inactive', lastLogin: '2024-02-20' },
  { id: 4, name: 'Alice Williams', email: 'alice.williams@example.com', role: 'Editor', status: 'Active', lastLogin: '2024-03-13' },
  { id: 5, name: 'Charlie Brown', email: 'charlie.brown@example.com', role: 'Viewer', status: 'Inactive', lastLogin: '2024-01-15' },
  { id: 6, name: 'Diana Prince', email: 'diana.prince@example.com', role: 'Admin', status: 'Active', lastLogin: '2024-03-15' },
  { id: 7, name: 'Edward Norton', email: 'edward.norton@example.com', role: 'Editor', status: 'Active', lastLogin: '2024-03-12' },
  { id: 8, name: 'Fiona Gallagher', email: 'fiona.gallagher@example.com', role: 'Viewer', status: 'Inactive', lastLogin: '2024-02-28' },
  { id: 9, name: 'George Miller', email: 'george.miller@example.com', role: 'Viewer', status: 'Active', lastLogin: '2024-03-11' },
  { id: 10, name: 'Hannah Montana', email: 'hannah.montana@example.com', role: 'Editor', status: 'Active', lastLogin: '2024-03-10' },
  { id: 11, name: 'Ian Wright', email: 'ian.wright@example.com', role: 'Viewer', status: 'Inactive', lastLogin: '2024-01-20' },
  { id: 12, name: 'Julia Roberts', email: 'julia.roberts@example.com', role: 'Admin', status: 'Active', lastLogin: '2024-03-09' },
];

export const Default: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
  },
};

export const WithSelectable: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
    selectable: true,
  },
};

export const WithCustomPageSize: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
    pageSize: 5,
    pageSizeOptions: [5, 10, 20],
  },
};

export const WithEmptyState: Story = {
  args: {
    columns: sampleColumns,
    data: [],
    emptyState: <div style={{ padding: '40px', textAlign: 'center' }}>
      <div style={{ fontSize: '48px', marginBottom: '16px' }}>📋</div>
      <div style={{ fontSize: '16px', color: '#6b7280' }}>No users found</div>
    </div>,
  },
};

export const Loading: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
    loading: true,
  },
};

export const WithCustomRender: Story = {
  args: {
    columns: sampleColumns,
    data: sampleData,
  },
};

export const LargeDataset: Story = {
  args: {
    columns: sampleColumns,
    data: Array.from({ length: 100 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
      email: `user${i + 1}@example.com`,
      role: ['Admin', 'Editor', 'Viewer'][i % 3] as string,
      status: i % 3 === 0 ? 'Inactive' : 'Active',
      lastLogin: `2024-03-${(10 + (i % 30)).toString().padStart(2, '0')}`,
    })),
    pageSize: 20,
  },
};

export const Alignments: Story = {
  args: {
    columns: [
      { id: 'name', label: 'Name', align: 'left' },
      { id: 'email', label: 'Email', align: 'left' },
      { id: 'role', label: 'Role', align: 'center' },
      { id: 'status', label: 'Status', align: 'center' },
      { id: 'id', label: 'ID', align: 'right' },
    ] as DataTableColumn<UserData>[],
    data: sampleData,
  },
};
