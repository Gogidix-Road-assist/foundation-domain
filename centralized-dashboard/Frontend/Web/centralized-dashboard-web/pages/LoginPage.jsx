import React, { useState } from 'react';
import { useAuthStore } from '../store/authStore';
import './LoginPage.css';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const login = useAuthStore((state) => state.login);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Simulate API call - replace with actual authentication
      await new Promise((resolve) => setTimeout(resolve, 1000));

      // Demo credentials
      if (email === 'admin@foundation.local' && password === 'admin123') {
        const token = 'demo-jwt-token-' + Date.now();
        const user = {
          id: '1',
          email,
          name: 'Foundation Admin',
          role: 'ADMIN',
        };
        login(token, user);
      } else {
        setError('Invalid credentials. Use admin@foundation.local / admin123');
      }
    } catch (err) {
      setError('Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-header">
          <h1>Foundation Domain</h1>
          <p>Centralized Management Dashboard</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@foundation.local"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="login-footer">
          <p>Demo credentials: admin@foundation.local / admin123</p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;