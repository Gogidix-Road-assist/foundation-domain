import { create } from 'zustand';
import { persist } from 'zustand/middleware';

const useAuthStore = create(
  persist(
    (set) => ({
      isAuthenticated: false,
      token: null,
      user: null,

      login: (token, user) => set({ isAuthenticated: true, token, user }),

      logout: () => set({ isAuthenticated: false, token: null, user: null }),

      checkAuth: () => {
        const token = localStorage.getItem('auth_token');
        const user = localStorage.getItem('auth_user');
        if (token && user) {
          set({ isAuthenticated: true, token, user: JSON.parse(user) });
        } else {
          set({ isAuthenticated: false, token: null, user: null });
        }
      },
    }),
    {
      name: 'auth-storage',
      onRehydrateStorage: () => (state) => {
        // Custom rehydration logic if needed
        state.checkAuth?.();
      },
    }
  )
);

export default useAuthStore;
