import type { Meta, StoryObj } from '@storybook/react';
import { Button } from '@shared-frontend-libraries/components';
import { Header } from './Header';

const meta: Meta<typeof Header> = {
  title: 'Layouts/Header',
  component: Header,
  tags: ['autodocs'],
  argTypes: {
    elevation: { control: 'number', min: 0, max: 4 },
  },
};

export default meta;
type Story = StoryObj<typeof Header>;

export const Default: Story = {
  args: {
    title: 'RapidAssist',
  },
};

export const WithActions: StoryObj = {
  render: () => (
    <Header
      title="RapidAssist"
      actions={
        <>
          <Button variant="ghost">Help</Button>
          <Button variant="primary">Sign Out</Button>
        </>
      }
    />
  ),
};

export const WithLogo: StoryObj = {
  render: () => (
    <Header
      title="My App"
      logo={<div style={{ width: 32, height: 32, backgroundColor: '#0066CC', borderRadius: 4 }} />}
      actions={<Button variant="primary">Action</Button>}
    />
  ),
};
