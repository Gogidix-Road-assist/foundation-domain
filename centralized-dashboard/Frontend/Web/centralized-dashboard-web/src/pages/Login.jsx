import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { useStore } from '../store/useStore';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const login = useStore((state) => state.login);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Demo mode - bypass API for demo credentials
      if (email === 'admin@foundation.local' && password === 'admin123') {
        const demoUser = {
          id: '1',
          email: 'admin@foundation.local',
          name: 'Foundation Admin',
          role: 'ADMIN',
        };
        const demoToken = 'demo-jwt-token-' + Date.now();

        login(demoUser, demoToken);
        localStorage.setItem('auth_token', demoToken);
        navigate('/dashboard');
        return;
      }

      // Try real API if not demo credentials
      const response = await authAPI.login(email, password);
      const { user, token } = response.data;

      login(user, token);
      localStorage.setItem('auth_token', token);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials. Use admin@foundation.local / admin123 for demo mode.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-header">
          <h1>Rapid Assist</h1>
          <h2>Centralized Dashboard</h2>
          <p>Sign in to access your dashboards</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="admin@foundation.local"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="admin123"
            />
          </div>

          <button type="submit" className="btn btn-primary btn-lg" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>

          <div className="login-links">
            <a href="#forgot-password">Forgot password?</a>
          </div>

          <div className="demo-credentials">
            <strong>Demo Credentials:</strong><br />
            Email: admin@foundation.local<br />
            Password: admin123
          </div>
        </form>

        <div className="login-footer">
          <p>&copy; 2024 Rapid Assist. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
}

export default Login;
