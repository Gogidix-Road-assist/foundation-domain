import React, { useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from 'react-native';
import { useAuthStore } from '../store/authStore';

export default function LoginScreen({ navigation }) {
  const [email, setEmail] = useState('admin@foundation.local');
  const [password, setPassword] = useState('admin123');
  const [loading, setLoading] = useState(false);
  const login = useAuthStore((state) => state.login);

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Error', 'Please enter email and password');
      return;
    }

    setLoading(true);

    try {
      // Demo mode
      if (email === 'admin@foundation.local' && password === 'admin123') {
        const demoUser = {
          id: '1',
          email: 'admin@foundation.local',
          name: 'Foundation Admin',
          role: 'ADMIN',
        };
        const demoToken = 'demo-jwt-token-' + Date.now();
        await login(demoUser, demoToken);
        navigation.replace('MainApp');
      } else {
        Alert.alert('Error', 'Invalid credentials. Use demo credentials.');
      }
    } catch (error) {
      Alert.alert('Error', 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <View style={styles.header}>
          <Text style={styles.title}>Foundation Domain</Text>
          <Text style={styles.subtitle}>Mobile Dashboard</Text>
        </View>

        <View style={styles.form}>
          <Text style={styles.label}>Email</Text>
          <TextInput
            style={styles.input}
            value={email}
            onChangeText={setEmail}
            placeholder="admin@foundation.local"
            autoCapitalize="none"
            keyboardType="email-address"
          />

          <Text style={styles.label}>Password</Text>
          <TextInput
            style={styles.input}
            value={password}
            onChangeText={setPassword}
            placeholder="admin123"
            secureTextEntry
          />

          <TouchableOpacity
            style={[styles.button, loading && styles.buttonDisabled]}
            onPress={handleLogin}
            disabled={loading}
          >
            <Text style={styles.buttonText}>
              {loading ? 'Signing in...' : 'Sign In'}
            </Text>
          </TouchableOpacity>

          <View style={styles.demoBox}>
            <Text style={styles.demoTitle}>Demo Credentials:</Text>
            <Text style={styles.demoText}>Email: admin@foundation.local</Text>
            <Text style={styles.demoText}>Password: admin123</Text>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1a5490',
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    padding: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 40,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: 'rgba(255,255,255,0.8)',
  },
  form: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 24,
  },
  label: {
    fontSize: 14,
    fontWeight: '500',
    marginBottom: 8,
    marginTop: 16,
    color: '#1a1a1a',
  },
  input: {
    borderWidth: 1,
    borderColor: '#dee2e6',
    borderRadius: 8,
    padding: 12,
    fontSize: 14,
  },
  button: {
    backgroundColor: '#1a5490',
    borderRadius: 8,
    padding: 14,
    alignItems: 'center',
    marginTop: 24,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  demoBox: {
    backgroundColor: '#e3f2fd',
    borderRadius: 8,
    padding: 16,
    marginTop: 24,
    borderLeftWidth: 4,
    borderLeftColor: '#1a5490',
  },
  demoTitle: {
    fontWeight: 'bold',
    color: '#1a5490',
    marginBottom: 4,
  },
  demoText: {
    fontSize: 13,
    color: '#333',
    lineHeight: 20,
  },
});
