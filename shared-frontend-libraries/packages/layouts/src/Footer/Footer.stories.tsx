import type { Meta, StoryObj } from '@storybook/react';
import { Footer, FooterLink } from './Footer';

const meta: Meta<typeof Footer> = {
  title: 'Layouts/Footer',
  component: Footer,
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof Footer>;

const navigationLinks: FooterLink[] = [
  { label: 'Home', href: '/' },
  { label: 'Features', href: '/features' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'About', href: '/about' },
  { label: 'Contact', href: '/contact' },
];

export const Default: Story = {
  args: {},
};

export const WithNavigationLinks: StoryObj = {
  args: {
    links: navigationLinks,
  },
};

export const CustomCopyright: StoryObj = {
  args: {
    copyright: '© 2024 My Company. Custom copyright text.',
  },
};

export const MinimalFooter: StoryObj = {
  args: {
    links: [],
    socialLinks: [],
    copyright: 'Simple footer with minimal content',
  },
};
