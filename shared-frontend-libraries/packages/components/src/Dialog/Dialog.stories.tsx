import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import { Typography, Button as MuiButton } from '@mui/material';
import { Dialog, DialogConfirm } from './Dialog';

const meta: Meta<typeof Dialog> = {
  title: 'Components/Dialog',
  component: Dialog,
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['xs', 'sm', 'md', 'lg', 'xl', 'full'],
    },
    open: { control: 'boolean' },
    showCloseButton: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Dialog>;

export const Default: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Open Dialog</MuiButton>
        <Dialog open={open} onClose={() => setOpen(false)} title="Dialog Title">
          <Typography>
            This is a simple dialog with some content. Dialogs are used to focus attention
            on important information or to request user input.
          </Typography>
        </Dialog>
      </>
    );
  },
};

export const Small: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Small Dialog</MuiButton>
        <Dialog open={open} onClose={() => setOpen(false)} title="Small" size="xs">
          <Typography>This is a small dialog.</Typography>
        </Dialog>
      </>
    );
  },
};

export const Large: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Large Dialog</MuiButton>
        <Dialog open={open} onClose={() => setOpen(false)} title="Large Dialog" size="lg">
          <Typography>
            This is a large dialog with more space for content. You can put forms, tables,
            or any other component inside.
          </Typography>
          <Typography paragraph>
            Large dialogs are useful for complex interactions that require more screen real estate.
          </Typography>
        </Dialog>
      </>
    );
  },
};

export const WithActions: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Dialog with Actions</MuiButton>
        <Dialog
          open={open}
          onClose={() => setOpen(false)}
          title="Confirm Action"
          actions={
            <>
              <MuiButton onClick={() => setOpen(false)}>Cancel</MuiButton>
              <MuiButton variant="contained" onClick={() => setOpen(false)}>
                Confirm
              </MuiButton>
            </>
          }
        >
          <Typography>
            Are you sure you want to proceed with this action? This cannot be undone.
          </Typography>
        </Dialog>
      </>
    );
  },
};

export const NoCloseButton: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>No Close Button</MuiButton>
        <Dialog
          open={open}
          onClose={() => setOpen(false)}
          title="Must Choose"
          showCloseButton={false}
          actions={
            <>
              <MuiButton onClick={() => setOpen(false)}>Option A</MuiButton>
              <MuiButton variant="contained" onClick={() => setOpen(false)}>
                Option B
              </MuiButton>
            </>
          }
        >
          <Typography>You must select one of the options below to continue.</Typography>
        </Dialog>
      </>
    );
  },
};

export const ConfirmDialog: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Delete Item</MuiButton>
        <DialogConfirm
          open={open}
          onClose={() => setOpen(false)}
          onConfirm={() => console.log('Confirmed')}
          title="Delete Item"
          message="Are you sure you want to delete this item? This action cannot be undone."
          confirmText="Delete"
          cancelText="Cancel"
          severity="error"
        />
      </>
    );
  },
};

export const WarningConfirm: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Proceed</MuiButton>
        <DialogConfirm
          open={open}
          onClose={() => setOpen(false)}
          onConfirm={() => console.log('Confirmed')}
          title="Warning"
          message="This action will affect multiple items. Are you sure you want to proceed?"
          confirmText="Proceed"
          cancelText="Go Back"
          severity="warning"
        />
      </>
    );
  },
};

export const InfoConfirm: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Learn More</MuiButton>
        <DialogConfirm
          open={open}
          onClose={() => setOpen(false)}
          onConfirm={() => console.log('Confirmed')}
          title="Information"
          message="This feature is available with the premium subscription. Upgrade now to unlock all features."
          confirmText="Upgrade"
          cancelText="Maybe Later"
          severity="info"
        />
      </>
    );
  },
};

export const FullScreen: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <MuiButton onClick={() => setOpen(true)}>Full Screen Dialog</MuiButton>
        <Dialog open={open} onClose={() => setOpen(false)} title="Full Screen" size="full">
          <Typography>This dialog takes up the full screen.</Typography>
          <Typography paragraph>
            Useful for mobile experiences or complex workflows.
          </Typography>
          <MuiButton onClick={() => setOpen(false)}>Close</MuiButton>
        </Dialog>
      </>
    );
  },
};
