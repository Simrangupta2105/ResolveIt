import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const result = await login(formData.username, formData.password);
    
    if (result.success) {
      // Check if user is admin and redirect accordingly
      const user = JSON.parse(localStorage.getItem('user'));
      const isAdmin = user?.roles?.some(role => role.authority === 'ROLE_ADMIN');
      
      if (isAdmin) {
        navigate('/admin/dashboard');
      } else {
        navigate('/');
      }
    } else {
      setError(result.error);
    }
    
    setLoading(false);
  };

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      <div style={{ maxWidth: '450px', margin: '0 auto' }}>
        <div className="card" style={{ background: 'var(--gradient-card)' }}>
          <div className="card-header" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🔐</div>
            <h2 className="card-title" style={{ fontSize: '2rem', marginBottom: '0.75rem' }}>Welcome Back</h2>
            <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              Sign in to your account to manage complaints and access the dashboard
            </p>
          </div>

          {error && (
            <div className="alert alert-error">
              ❌ {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">👤 Username or Email</label>
              <input
                type="text"
                name="username"
                className="form-input"
                placeholder="Enter your username or email address"
                value={formData.username}
                onChange={handleChange}
                required
                style={{ paddingLeft: '3rem' }}
              />
              <div style={{ 
                position: 'relative', 
                marginTop: '-2.5rem', 
                marginLeft: '1rem', 
                fontSize: '1.2rem',
                pointerEvents: 'none',
                color: 'var(--text-secondary)'
              }}>
                👤
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">🔒 Password</label>
              <input
                type="password"
                name="password"
                className="form-input"
                placeholder="Enter your password"
                value={formData.password}
                onChange={handleChange}
                required
                style={{ paddingLeft: '3rem' }}
              />
              <div style={{ 
                position: 'relative', 
                marginTop: '-2.5rem', 
                marginLeft: '1rem', 
                fontSize: '1.2rem',
                pointerEvents: 'none',
                color: 'var(--text-secondary)'
              }}>
                🔒
              </div>
            </div>

            <button 
              type="submit" 
              className="btn btn-primary"
              style={{ width: '100%', padding: '1rem', fontSize: '1.1rem', fontWeight: '600' }}
              disabled={loading}
            >
              {loading ? '⏳ Signing in...' : '🚀 Login'}
            </button>
          </form>

          <div style={{ 
            textAlign: 'center', 
            marginTop: '2rem', 
            borderTop: '2px solid var(--border-color)',
            background: 'var(--bg-secondary)',
            borderRadius: '12px',
            margin: '2rem -2rem -2rem -2rem',
            padding: '1.5rem 2rem'
          }}>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem', fontSize: '0.95rem' }}>
              💼 Need employee access?
            </p>
            <Link to="/admin/employee-requests" className="btn btn-secondary">
              📝 Request Access
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;