import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Skeleton, SkeletonCard, SkeletonList } from './Skeleton';

describe('Skeleton Component', () => {
  it('renders skeleton element', () => {
    render(<Skeleton />);
    const skeleton = document.querySelector('.MuiSkeleton-root');
    expect(skeleton).toBeInTheDocument();
  });

  it('renders multiple skeletons with count prop', () => {
    render(<Skeleton count={3} />);
    const skeletons = document.querySelectorAll('.MuiSkeleton-root');
    expect(skeletons).toHaveLength(3);
  });

  it('applies variant styles', () => {
    const { container } = render(<Skeleton variant="circular" width={40} height={40} />);
    const skeleton = container.querySelector('.MuiSkeleton-circular');
    expect(skeleton).toBeInTheDocument();
  });

  it('applies custom width and height', () => {
    render(<Skeleton variant="rectangular" width={200} height={100} />);
    const skeleton = document.querySelector('.MuiSkeleton-root');
    expect(skeleton).toHaveStyle({ width: '200px', height: '100px' });
  });
});

describe('SkeletonCard Component', () => {
  it('renders card skeleton with avatar', () => {
    render(<SkeletonCard showAvatar />);
    const avatar = document.querySelector('.MuiSkeleton-circular');
    expect(avatar).toBeInTheDocument();
  });

  it('renders card skeleton with specified lines', () => {
    render(<SkeletonCard lines={5} />);
    const textSkeletons = document.querySelectorAll('.MuiSkeleton-text');
    expect(textSkeletons).toHaveLength(6); // 1 from avatar label + 5 from lines
  });
});

describe('SkeletonList Component', () => {
  it('renders list with specified items', () => {
    render(<SkeletonList items={3} />);
    const avatarSkeletons = document.querySelectorAll('.MuiSkeleton-circular');
    expect(avatarSkeletons).toHaveLength(3);
  });

  it('renders list without avatar when showAvatar is false', () => {
    render(<SkeletonList items={2} showAvatar={false} />);
    const avatarSkeletons = document.querySelectorAll('.MuiSkeleton-circular');
    expect(avatarSkeletons).toHaveLength(0);
  });
});
