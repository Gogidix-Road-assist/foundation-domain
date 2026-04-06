export const get = async <T>(url: string, options?: RequestInit): Promise<T> => {
  const response = await fetch(url, {
    method: options?.method || 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
    ...(options?.body ? { body: JSON.stringify(options.body) } : {}),
  });

  if (!response.ok) {
    throw new Error(`API request failed: ${response.status}`);
  }

  return response.json();
};

export const post = async <T>(url: string, data: any, options?: RequestInit): Promise<T> => {
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
    body: JSON.stringify(data),
    ...(options?.body ? { body: JSON.stringify(options.body) } : {}),
  });

  if (!response.ok) {
    throw new Error(`API request failed: ${response.status}`);
  }

  return response.json();
};

export const put = async <T>(url: string, data: any, options?: RequestInit): Promise<T> => {
  const response = await fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...(options?.headers || {}),
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error(`API request failed: ${response.status}`);
  }

  return response.json();
};

export const del = async <T>(url: string, options?: RequestInit): Promise<T> => {
  const response = await fetch(url, {
    method: 'DELETE',
    headers: {
      ...(options?.headers || {}),
    },
  });

  if (!response.ok) {
    throw new Error(`API request failed: ${response.status}`);
  }

  return response.json();
};
