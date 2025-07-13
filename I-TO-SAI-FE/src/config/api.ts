// API configuration for the frontend
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Helper function to make API calls
export const apiCall = async (endpoint: string, options: RequestInit = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetch(url, {
    ...options,
    credentials: 'include', // Important for session cookies
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });
  
  if (!response.ok) {
    throw new Error(`API call failed: ${response.status} ${response.statusText}`);
  }
  
  return response;
};

// Specific API functions
export const api = {
  // User endpoints
  getUserDetails: () => apiCall('/api/getUserDetails'),
  
  // Daily response endpoints
  submitDailyResponse: (data: any) => apiCall('/api/submitDailyResponse', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  viewResponsesForUser: () => apiCall('/api/viewResponsesForUser'),
}; 