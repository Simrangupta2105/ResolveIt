import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useComplaintNotifications } from '../hooks/useWebSocket';
import axios from 'axios';

const Home = () => {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchId, setSearchId] = useState('');
  const [realtimeNotification, setRealtimeNotification] = useState(null);
  const { currentUser } = useAuth();

  // WebSocket connection for real-time updates
  const { connected } = useComplaintNotifications((notification) => {
    console.log('Real-time notification received:', notification);
    
    // Show notification to user
    setRealtimeNotification(notification);
    
    // Auto-hide notification after 5 seconds
    setTimeout(() => {
      setRealtimeNotification(null);
    }, 5000);
    
    // Refresh data when new complaint is submitted or updated
    if (notification.type === 'NEW_COMPLAINT' || 
        notification.type === 'STATUS_CHANGE' || 
        notification.type === 'ASSIGNMENT' || 
        notification.type === 'ESCALATION') {
      fetchRecentComplaints();
    }
  });

  useEffect(() => {
    fetchRecentComplaints();
  }, []);

  const fetchRecentComplaints = async () => {
    try {
      const response = await axios.get('/api/complaints?page=0&size=6');
      setComplaints(response.data.content);
    } catch (error) {
      console.error('Error fetching complaints:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchId.trim()) {
      window.location.href = `/complaint/${searchId.trim()}`;
    }
  };

  const getStatusClass = (status) => {
    if (!status) return 'status status-unknown';
    return `status status-${status.toLowerCase().replace('_', '-')}`;
  };

  const getPriorityClass = (priority) => {
    if (!priority) return 'priority priority-unknown';
    return `priority priority-${priority.toLowerCase()}`;
  };

  const getStatusIcon = (status) => {
    const icons = {
      'NEW': '🆕',
      'UNDER_REVIEW': '👀',
      'IN_PROGRESS': '⚙️',
      'RESOLVED': '✅',
      'CLOSED': '🔒'
    };
    return icons[status] || '❓';
  };

  const getPriorityIcon = (priority) => {
    const icons = {
      'LOW': '🟢',
      'MEDIUM': '🟡',
      'HIGH': '🟠',
      'URGENT': '🔴'
    };
    return icons[priority] || '⚪';
  };

  const isAdmin = currentUser?.roles?.some(role => role.authority === 'ROLE_ADMIN');
  const isEmployee = currentUser?.roles?.some(role => 
    ['ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_SUPERVISOR'].includes(role.authority)
  );

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      {/* Hero Section */}
      <div className="card" style={{ textAlign: 'center', marginBottom: '3rem', background: 'var(--gradient-card)' }}>
        <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🛡️</div>
        <h1 style={{ fontSize: '2.75rem', marginBottom: '1rem', color: 'var(--text-primary)', fontWeight: '700' }}>
          Resolve Complaint Portal
        </h1>
        <p style={{ fontSize: '1.25rem', color: 'var(--text-secondary)', marginBottom: '2rem', maxWidth: '600px', margin: '0 auto 2rem' }}>
          Submit complaints anonymously or publicly, track status updates in real-time, and ensure your voice is heard
        </p>
        
        <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
          {isEmployee ? (
            <Link to="/employee/dashboard" className="btn btn-primary">
              <span className="nav-icon">👨‍💼</span>
              Go to Employee Dashboard
            </Link>
          ) : !isAdmin ? (
            <>
              <Link to="/submit-complaint" className="btn btn-primary">
                <span className="nav-icon">📝</span>
                Submit Complaint
              </Link>
              {!currentUser && (
                <Link to="/login" className="btn btn-secondary">
                  <span className="nav-icon">🔑</span>
                  Employee Login
                </Link>
              )}
              {currentUser && (
                <Link to="/my-complaints" className="btn btn-secondary">
                  <span className="nav-icon">📋</span>
                  My Complaints
                </Link>
              )}
            </>
          ) : (
            <Link to="/admin/dashboard" className="btn btn-primary">
              <span className="nav-icon">👥</span>
              Admin Dashboard
            </Link>
          )}
        </div>
      </div>

      {/* Real-time Notification */}
      {realtimeNotification && (
        <div className="card" style={{ 
          marginBottom: '2rem', 
          backgroundColor: 'var(--success-light)', 
          border: '2px solid var(--success)',
          animation: 'slideIn 0.3s ease-out'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <span style={{ fontSize: '1.5rem' }}>
                {realtimeNotification.type === 'NEW_COMPLAINT' && '🆕'}
                {realtimeNotification.type === 'STATUS_CHANGE' && '🔄'}
                {realtimeNotification.type === 'ASSIGNMENT' && '👤'}
                {realtimeNotification.type === 'ESCALATION' && '🚨'}
              </span>
              <div>
                <div style={{ fontWeight: '600', color: 'var(--success)', marginBottom: '0.25rem' }}>
                  Real-time Update
                </div>
                <div style={{ color: 'var(--text-primary)' }}>
                  {realtimeNotification.message}
                </div>
              </div>
            </div>
            <button 
              onClick={() => setRealtimeNotification(null)}
              style={{ 
                background: 'none', 
                border: 'none', 
                fontSize: '1.2rem', 
                cursor: 'pointer',
                color: 'var(--text-secondary)'
              }}
            >
              ✕
            </button>
          </div>
          <div style={{ 
            fontSize: '0.875rem', 
            color: 'var(--text-secondary)', 
            marginTop: '0.5rem',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }}>
            <span style={{ 
              width: '8px', 
              height: '8px', 
              borderRadius: '50%', 
              backgroundColor: connected ? 'var(--success)' : 'var(--error)',
              display: 'inline-block'
            }}></span>
            {connected ? 'Live updates active' : 'Connecting...'}
          </div>
        </div>
      )}

      {/* Search Section - Only show for regular users (not admin or employee) */}
      {!isAdmin && !isEmployee && (
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">🔍 Track Your Complaint</h2>
            <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              Enter your complaint ID to check the current status and updates
            </p>
          </div>
          
          <div className="alert alert-info" style={{ marginBottom: '1.5rem' }}>
            <p style={{ margin: '0 0 0.5rem 0', fontWeight: '600' }}>🕶️ Privacy Notice:</p>
            <p style={{ margin: 0, fontSize: '0.9rem' }}>
              Anonymous complaints cannot be tracked by ID to protect submitter privacy. 
              Only public complaints can be tracked individually.
            </p>
          </div>
          
          <form onSubmit={handleSearch}>
            <div className="form-group">
              <label className="form-label">📋 Public Complaint ID</label>
              <div style={{ display: 'flex', gap: '1rem' }}>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g., C2024001 (Public complaints only)"
                  value={searchId}
                  onChange={(e) => setSearchId(e.target.value)}
                  style={{ flex: 1 }}
                />
                <button type="submit" className="btn btn-primary">
                  <span className="nav-icon">🔍</span>
                  Track Status
                </button>
              </div>
            </div>
          </form>
        </div>
      )}

      {/* Recent Complaints - Only show for regular users (not admin or employee) */}
      {!isAdmin && !isEmployee && (
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">📊 Recent Public Complaints</h2>
            <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              View the latest complaints and their current status
            </p>
          </div>
          
          {loading ? (
            <div className="loading">
              <div className="spinner"></div>
              <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading complaints...</p>
            </div>
          ) : (
            <div>
              {complaints.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                  <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📭</div>
                  <p style={{ fontSize: '1.1rem' }}>No complaints found</p>
                  <p>Be the first to submit a complaint!</p>
                </div>
              ) : (
                <div style={{ display: 'grid', gap: '1.5rem' }}>
                  {complaints.map((complaint) => (
                    <div key={complaint.id} style={{ 
                      border: '1px solid var(--border-color)', 
                      borderRadius: '12px', 
                      padding: '1.5rem',
                      background: 'var(--bg-primary)',
                      transition: 'all 0.3s ease',
                      cursor: 'pointer'
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.transform = 'translateY(-2px)';
                      e.currentTarget.style.boxShadow = '0 8px 25px var(--shadow-medium)';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.transform = 'translateY(0)';
                      e.currentTarget.style.boxShadow = 'none';
                    }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '1rem', flexWrap: 'wrap', gap: '1rem' }}>
                        <div style={{ flex: 1, minWidth: '200px' }}>
                          <h3 style={{ margin: '0 0 0.5rem 0', color: 'var(--text-primary)', fontSize: '1.25rem', fontWeight: '600' }}>
                            {complaint.subject}
                          </h3>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                            {complaint.submissionType === 'ANONYMOUS' ? (
                              <div className="complaint-id-hidden">
                                🕶️ Anonymous Complaint
                              </div>
                            ) : (
                              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', margin: 0 }}>
                                📋 ID: {complaint.complaintId}
                              </p>
                            )}
                          </div>
                        </div>
                        <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center', flexWrap: 'wrap' }}>
                          <span className={getStatusClass(complaint.status)}>
                            {getStatusIcon(complaint.status)} {complaint.status ? complaint.status.replace('_', ' ') : 'Unknown'}
                          </span>
                          <span className={getPriorityClass(complaint.priority)}>
                            {getPriorityIcon(complaint.priority)} {complaint.priority || 'Unknown'}
                          </span>
                        </div>
                      </div>
                      
                      <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem', lineHeight: '1.6' }}>
                        {complaint.description && complaint.description.length > 200 
                          ? `${complaint.description.substring(0, 200)}...` 
                          : (complaint.description || 'No description available')}
                      </p>
                      
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
                        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
                          <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                            📂 {complaint.category || 'Unknown'}
                          </span>
                          <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                            📅 {new Date(complaint.createdAt).toLocaleDateString()}
                          </span>
                          {complaint.submissionType === 'ANONYMOUS' && (
                            <span className="anonymous-badge">
                              🕶️ Anonymous
                            </span>
                          )}
                        </div>
                        <Link 
                          to={`/complaint/${complaint.complaintId}`}
                          className="btn btn-outline"
                          style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}
                        >
                          👁️ View Details
                        </Link>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {/* Features Section - Only show for regular users (not admin or employee) */}
      {!isAdmin && !isEmployee && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '2rem', marginTop: '2rem' }}>
          <div className="card" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🕶️</div>
            <h3 style={{ color: 'var(--primary)', marginBottom: '1rem', fontSize: '1.5rem', fontWeight: '600' }}>Anonymous Submission</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>
              Submit complaints anonymously to maintain complete privacy. Anonymous complaints cannot be tracked individually to protect your identity.
            </p>
          </div>
          
          <div className="card" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📊</div>
            <h3 style={{ color: 'var(--primary)', marginBottom: '1rem', fontSize: '1.5rem', fontWeight: '600' }}>Real-time Tracking</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>
              Monitor your public complaint progress through our transparent status system with instant updates and detailed timeline tracking.
            </p>
          </div>
          
          <div className="card" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📎</div>
            <h3 style={{ color: 'var(--primary)', marginBottom: '1rem', fontSize: '1.5rem', fontWeight: '600' }}>File Attachments</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.6' }}>
              Upload supporting documents, images, and evidence to strengthen your complaint and provide comprehensive context.
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;