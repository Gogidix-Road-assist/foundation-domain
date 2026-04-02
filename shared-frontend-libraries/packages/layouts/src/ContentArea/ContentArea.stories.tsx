import type { Meta, StoryObj } from '@storybook/react';
import { ContentArea } from './ContentArea';

const meta: Meta<typeof ContentArea> = {
  title: 'Layouts/ContentArea',
  component: ContentArea,
  tags: ['autodocs'],
  argTypes: {
    scrollable: { control: 'boolean' },
    overflow: {
      control: 'select',
      options: ['auto', 'hidden', 'visible', 'scroll'],
    },
  },
};

export default meta;
type Story = StoryObj<typeof ContentArea>;

export const Default: Story = {
  args: {
    children: <div>Default content area</div>,
  },
};

export const NotScrollable: Story = {
  args: {
    scrollable: false,
    children: <div>Non-scrollable content</div>,
  },
};

export const HiddenOverflow: Story = {
  args: {
    overflow: 'hidden',
    children: <div>Content with hidden overflow</div>,
  },
};

export const WithCustomContent: StoryObj = {
  render: () => (
    <ContentArea>
      <div style={{ padding: 32 }}>
        <h2>Welcome to RapidAssist</h2>
        <p>This is the main content area component. It provides a flexible container for your application's primary content.</p>
        <div style={{ display: 'flex', gap: 16, marginTop: 24 }}>
          <div style={{ flex: 1, backgroundColor: '#F3F4F6', padding: 16, borderRadius: 8 }}>
            <h3>Content Block 1</h3>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
          </div>
          <div style={{ flex: 1, backgroundColor: '#E0E7FF', padding: 16, borderRadius: 8 }}>
            <h3>Content Block 2</h3>
            <p>Sed do eiusmod tempor incididunt ut labore et dolore.</p>
          </div>
        </div>
      </div>
    </ContentArea>
  ),
};
