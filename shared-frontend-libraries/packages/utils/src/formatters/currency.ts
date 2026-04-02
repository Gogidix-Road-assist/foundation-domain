export const formatCurrency = (amount: number, currency: string = 'USD'): string => {
  return new Intl.NumberFormat(amount, {
    style: 'currency',
    currency,
  }).format(amount);
};

export const formatCurrencyCompact = (amount: number): string => {
  if (amount >= 1000000) {
    return `${(amount / 1000).toFixed(0)}K`;
  }
  return amount.toFixed(2);
};

export const parseCurrency = (value: string): number => {
  return parseFloat(value.replace(/[^0-9.-]/g, ''));
};

export const roundCurrency = (amount: number): number => {
  return Math.round(amount * 100) / 100;
};
