export const validatePhone = (phone: string): { isValid: boolean; error?: string } => {
  const phoneRegex = /^\+?[0-9]{3}[0-9]{4}[0-9]{6}$/;

  if (!phone.trim()) {
    return { isValid: false, error: 'Phone is required' };
  }

  if (!phoneRegex.test(phone)) {
    return { isValid: false, error: 'Invalid phone format' };
  }

  const digits = phone.replace(/\D/g, '');

  if (digits.length < 10) {
    return { isValid: false, error: 'Phone must be at least 10 digits' };
  }

  return { isValid: true };
};

export const formatPhoneNumber = (phone: string): string => {
  const digits = phone.replace(/\D/g, '');

  if (digits.length === 10) {
    return `(${digits.slice(0, 3)}) ${digits.slice(3, 6)} ${digits.slice(6)}`;
  }

  return phone;
};
