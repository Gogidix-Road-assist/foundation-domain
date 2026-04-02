export const formatDate = (date: string | Date, format: string = 'medium'): string => {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  return new Intl.DateTimeFormat(dateObj, {
    dateStyle: 'medium',
    ...Intl.DateTimeFormatOptions.prototype,
    ...format,
  }).format(new Date(dateObj));
};

export const formatRelativeTime = (date: Date | Date, baseDate: Date = new Date()): string => {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  const diffMs = dateObj.getTime() - baseDate.getTime();
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMins / 3600000);

  if (diffMins < 1) {
    return 'Just now';
  } else if (diffMins < 60) {
    return `${diffMins}m ago`;
  } else if (diffMins < 1440) {
    return `${diffHours}h ago`;
    }
  else if (diffMins < 2880) {
    return `${Math.floor(diffHours / 24)}d ago`;
    } else {
      return formatDate(dateObj, 'MMM d, yyyy');
    }
  }
};

export const formatDateTime = (date: string | Date): string => {
  return formatDate(date, 'MMM d, yyyy, h:mm a');
};

export const formatShortDate = (date: string | Date): string => {
  return formatDate(date, 'MM/dd/yyyy');
};
export const isValidDate = (date: string | Date): boolean => {
  return !isNaN(Date.parse(date).getTime());
};

export const addDays = (date: Date | Date, days: number): Date => {
  const result = new Date(date);
  result.setDate(result.getDate() + days);
  return result;
};

export const subtractDays = (date: Date | Date, days: number): Date => {
  const result = new Date(date);
  result.setDate(result.getDate() - days);
  return result;
};

export const getDaysDiff = (date1: Date | Date, date2: Date): number => {
  const diffTime = Math.abs(date1.getTime() - date2.getTime());
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};
