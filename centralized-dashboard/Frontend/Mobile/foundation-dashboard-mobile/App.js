import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import { useAuthStore } from './store/authStore';
import { useDashboardStore } from './store/dashboardStore';
import { setAuthToken } from './services/api';

// Screens
import LoginScreen from './screens/LoginScreen';
import DashboardScreen from './screens/DashboardScreen';
import ServicesScreen from './screens/ServicesScreen';
import AnalyticsScreen from './screens/AnalyticsScreen';
import SettingsScreen from './screens/SettingsScreen';

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        tabBarActiveTintColor: '#1a5490',
        tabBarInactiveTintColor: '#6c757d',
        headerShown: false,
      }}
    >
      <Tab.Screen
        name="Dashboard"
        component={DashboardScreen}
        options={{
          tabBarLabel: 'Dashboard',
        }}
      />
      <Tab.Screen
        name="Services"
        component={ServicesScreen}
        options={{
          tabBarLabel: 'Services',
        }}
      />
      <Tab.Screen
        name="Analytics"
        component={AnalyticsScreen}
        options={{
          tabBarLabel: 'Analytics',
        }}
      />
      <Tab.Screen
        name="Settings"
        component={SettingsScreen}
        options={{
          tabBarLabel: 'Settings',
        }}
      />
    </Tab.Navigator>
  );
}

export default function App() {
  const { isAuthenticated, checkAuth, token } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, []);

  useEffect(() => {
    if (token) {
      setAuthToken(token);
    }
  }, [token]);

  if (!isAuthenticated) {
    return (
      <NavigationContainer>
        <Stack.Navigator screenOptions={{ headerShown: false }}>
          <Stack.Screen name="Login" component={LoginScreen} />
        </Stack.Navigator>
      </NavigationContainer>
    );
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        <Stack.Screen name="MainApp" component={MainTabs} />
        <Stack.Screen name="Login" component={LoginScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
