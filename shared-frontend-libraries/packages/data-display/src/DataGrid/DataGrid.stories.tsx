import type { Meta, StoryObj } from '@storybook/react';
import { DataGrid } from './DataGrid';

const meta = {
  title: 'Data Display/DataGrid',
  component: DataGrid,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof DataGrid>;

export default meta;
type Story = StoryObj<typeof DataGrid>;

const sampleColumns = [
  { field: 'id', headerName: 'ID', width: 70, type: 'number' },
  { field: 'name', headerName: 'Name', width: 180 },
  { field: 'email', headerName: 'Email', width: 230 },
  {
    field: 'role',
    headerName: 'Role',
    width: 120,
    type: 'singleSelect',
    valueOptions: ['Admin', 'Editor', 'Viewer'],
  },
  {
    field: 'status',
    headerName: 'Status',
    width: 120,
    renderCell: (params: any) => (
      <span
        style={{
          padding: '4px 12px',
          borderRadius: '12px',
          fontSize: '12px',
          fontWeight: 500,
          backgroundColor: params.value === 'Active' ? '#d1fae5' : '#f3f4f6',
          color: params.value === 'Active' ? '#065f46' : '#374151',
        }}
      >
        {params.value}
      </span>
    ),
  },
  { field: 'lastLogin', headerName: 'Last Login', width: 160 },
];

const sampleRows = [
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
];

export const Default: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
  },
};

export const WithCheckboxSelection: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    checkboxSelection: true,
    disableRowSelectionOnClick: true,
  },
};

export const Loading: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    loading: true,
  },
};

export const CompactDensity: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    density: 'compact',
  },
};

export const ComfortableDensity: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    density: 'comfortable',
  },
};

export const CustomHeight: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    height: 300,
  },
};

export const AutoHeight: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    autoHeight: true,
  },
};

export const LargeDataset: Story = {
  args: {
    columns: sampleColumns,
    rows: Array.from({ length: 100 }, (_, i) => ({
      id: i + 1,
      name: `User ${i + 1}`,
      email: `user${i + 1}@example.com`,
      role: ['Admin', 'Editor', 'Viewer'][i % 3] as string,
      status: i % 3 === 0 ? 'Inactive' : 'Active',
      lastLogin: `2024-03-${(10 + (i % 30)).toString().padStart(2, '0')}`,
    })),
    pageSize: 25,
    pageSizeOptions: [25, 50, 100],
  },
};

export const DisabledColumnFilter: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    disableColumnFilter: true,
  },
};

export const DisabledColumnSorting: Story = {
  args: {
    columns: sampleColumns,
    rows: sampleRows,
    disableColumnSorting: true,
  },
};
