export const validateEmail = (email: string): { isValid: boolean; error?: string } => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+\.[^\s@]+$/;

  if (!email.trim()) {
    return { isValid: false, error: 'Email is required' };
  }

  if (!emailRegex.test(email)) {
    return { isValid: false, error: 'Invalid email format' };
  }

  const [localPart, domain] = email.trim().split('@');

  if (localPart.length < 1 || domain.length < 1) {
    return { isValid: false, error: 'Email must have a domain' };
  }

  if (domain.length < 2) {
    return { isValid: false, error: 'Domain must be at least 2 characters' };
  }

  if (!/^[a-zA-Z0-9]+$/.test(localPart)) {
    return { isValid: false, error: 'Local part must only contain letters' };
  }

  return { isValid: true };
};

export const validateEmailLength = (email: string): { isValid: boolean; error?: string } => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!emailRegex.test(email)) {
    return { isValid: false, error: 'Invalid email format' };
  }

  if (email.trim().length > 320) {
    return { isValid: false, error: 'Email is too long' };
  }

  return { isValid: true };
};
