import type { Meta, StoryObj } from '@storybook/react';
import { Typography } from '@mui/material';
import { Accordion, AccordionGroup } from './Accordion';

const meta: Meta<typeof Accordion> = {
  title: 'Components/Accordion',
  component: Accordion,
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof Accordion>;

export const Default: StoryObj = {
  render: () => (
    <Accordion title="What is RapidAssist?">
      <Typography>
        RapidAssist is a comprehensive roadside assistance platform connecting users with nearby service providers,
        offering real-time tracking and automated dispatch.
      </Typography>
    </Accordion>
  ),
};

export const DefaultExpanded: StoryObj = {
  render: () => (
    <Accordion title="Default Expanded" defaultExpanded>
      <Typography>This accordion is expanded by default.</Typography>
    </Accordion>
  ),
};

export const WithActions: StoryObj = {
  render: () => (
    <Accordion
      title="Account Settings"
      defaultExpanded
      actions={
        <>
          <button style={{ padding: '6px 12px', marginRight: '8px', borderRadius: '4px', border: '1px solid #E5E7EB', background: 'white' }}>
            Cancel
          </button>
          <button style={{ padding: '6px 12px', borderRadius: '4px', border: 'none', background: '#0066CC', color: 'white' }}>
            Save
          </button>
        </>
      }
    >
      <Typography>Manage your account preferences and settings here.</Typography>
    </Accordion>
  ),
};

export const Group: StoryObj = {
  render: () => (
    <AccordionGroup
      items={[
        {
          id: 'item1',
          title: 'Getting Started',
          content: (
            <Typography>
              Learn how to set up your account and configure your profile settings.
            </Typography>
          ),
          defaultExpanded: true,
        },
        {
          id: 'item2',
          title: 'Features',
          content: (
            <Typography>
              Explore our comprehensive feature set including real-time tracking, automated dispatch, and multi-tenant support.
            </Typography>
          ),
        },
        {
          id: 'item3',
          title: 'Pricing',
          content: (
            <Typography>
              Choose from our flexible pricing plans designed for businesses of all sizes.
            </Typography>
          ),
        },
      ]}
    />
  ),
};

export const MultiExpand: StoryObj = {
  render: () => (
    <AccordionGroup
      allowMultiple
      items={[
        {
          id: 'multi1',
          title: 'Can I expand multiple accordions?',
          content: (
            <Typography>
              Yes! This accordion group allows multiple items to be expanded simultaneously.
            </Typography>
          ),
        },
        {
          id: 'multi2',
          title: 'Is there a limit?',
          content: (
            <Typography>
              No, you can expand as many accordions as you need.
            </Typography>
          ),
        },
        {
          id: 'multi3',
          title: 'What about mobile?',
          content: (
            <Typography>
              Accordion groups work seamlessly on mobile devices with touch-friendly interactions.
            </Typography>
          ),
        },
      ]}
    />
  ),
};

export const WithDisabledItem: StoryObj = {
  render: () => (
    <AccordionGroup
      items={[
        {
          id: 'enabled1',
          title: 'Active Accordion',
          content: <Typography>This accordion can be expanded.</Typography>,
        },
        {
          id: 'disabled1',
          title: 'Disabled Accordion',
          content: <Typography>This content is not accessible.</Typography>,
          disabled: true,
        },
        {
          id: 'enabled2',
          title: 'Another Active Accordion',
          content: <Typography>This accordion can also be expanded.</Typography>,
        },
      ]}
    />
  ),
};

export const FAQ: StoryObj = {
  render: () => (
    <div style={{ maxWidth: '800px' }}>
      <Typography variant="h4" gutterBottom style={{ marginBottom: '24px' }}>
        Frequently Asked Questions
      </Typography>
      <AccordionGroup
        items={[
          {
            id: 'faq1',
            title: 'How do I request roadside assistance?',
            content: (
              <Typography>
                Simply open the RapidAssist app, select your current location, describe your issue,
                and tap "Request Help." Our system will automatically dispatch the nearest available service provider.
              </Typography>
            ),
            defaultExpanded: true,
          },
          {
            id: 'faq2',
            title: 'What types of services are available?',
            content: (
              <Typography>
                We offer towing, tire changes, battery jumps, fuel delivery, lockout services,
                and minor on-site repairs. Premium members also have access to mechanical diagnostics.
              </Typography>
            ),
          },
          {
            id: 'faq3',
            title: 'How long does it take for help to arrive?',
            content: (
              <Typography>
                Average response time is under 30 minutes in urban areas. You can track your
                service provider in real-time through the app.
              </Typography>
            ),
          },
          {
            id: 'faq4',
            title: 'Is there a subscription required?',
            content: (
              <Typography>
                No! You can pay per service or subscribe to our premium plan for discounted rates
                and priority dispatch.
              </Typography>
            ),
          },
        ]}
      />
    </div>
  ),
};
