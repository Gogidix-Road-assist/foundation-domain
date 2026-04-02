import { create } from 'zustand';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as SecureStore from 'expo-secure-store';

const useAuthStore = create((set, get) => ({
  isAuthenticated: false,
  token: null,
  user: null,

  login: async (user, token) => {
    try {
      await SecureStore.setItemAsync('auth_token', token);
      await AsyncStorage.setItem('auth_user', JSON.stringify(user));
      set({ isAuthenticated: true, token, user });
    } catch (error) {
      console.error('Login error:', error);
    }
  },

  logout: async () => {
    try {
      await SecureStore.deleteItemAsync('auth_token');
      await AsyncStorage.removeItem('auth_user');
      set({ isAuthenticated: false, token: null, user: null });
    } catch (error) {
      console.error('Logout error:', error);
    }
  },

  checkAuth: async () => {
    try {
      const token = await SecureStore.getItemAsync('auth_token');
      const userStr = await AsyncStorage.getItem('auth_user');
      if (token && userStr) {
        set({ isAuthenticated: true, token, user: JSON.parse(userStr) });
      } else {
        set({ isAuthenticated: false, token: null, user: null });
      }
    } catch (error) {
      console.error('Check auth error:', error);
      set({ isAuthenticated: false, token: null, user: null });
    }
  },
}));

export default useAuthStore;
