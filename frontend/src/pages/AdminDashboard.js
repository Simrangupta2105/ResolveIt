import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useComplaintNotifications } from '../hooks/useWebSocket';
import axios from 'axios';

const AdminDashboard = () => {
  const { currentUser } = useAuth();
  const [stats, setStats] = useState({
    openComplaints: 0,
    resolvedComplaints: 0,
    averageResolutionTime: 0,
    totalComplaints: 0
  });
  const [recentComplaints, setRecentComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [realtimeNotification, setRealtimeNotification] = useState(null);
  const [employees, setEmployees] = useState([]);
  const [personalNoteModal, setPersonalNoteModal] = useState({ show: false, employee: null });
  const [personalNoteForm, setPersonalNoteForm] = useState({ 
    message: '', 
    selectedEmployeeId: '' 
  });

  // WebSocket connection for real-time updates
  const { connected } = useComplaintNotifications((notification) => {
    console.log('Admin Dashboard - Real-time notification:', notification);
    
    // Show notification
    setRealtimeNotification(notification);
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
      setRealtimeNotification(null);
    }, 5000);
    
    // Refresh dashboard data
    fetchDashboardData();
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [statsResponse, complaintsResponse, employeesResponse] = await Promise.all([
        axios.get('/api/admin/dashboard/stats'),
        axios.get('/api/complaints?page=0&size=10'),
        axios.get('/api/admin/assignable-users')
      ]);

      setStats(statsResponse.data);
      setRecentComplaints(complaintsResponse.data.content);
      
      // Filter only employees (not admins)
      const employeeUsers = employeesResponse.data.filter(user => 
        ['EMPLOYEE', 'MANAGER', 'SUPERVISOR'].includes(user.role)
      );
      setEmployees(employeeUsers);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePersonalNoteClick = (employee) => {
    setPersonalNoteModal({ show: true, employee });
    setPersonalNoteForm({ message: '', selectedEmployeeId: employee.id.toString() });
  };

  const handlePersonalNoteSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/api/personal-notes/send', {
        toEmployeeId: personalNoteModal.employee.id,
        message: personalNoteForm.message
      });
      
      setPersonalNoteModal({ show: false, employee: null });
      setPersonalNoteForm({ message: '', selectedEmployeeId: '' });
      alert(`✅ Personal note sent to ${personalNoteModal.employee.fullName} successfully!`);
    } catch (error) {
      console.error('Error sending personal note:', error);
      alert('❌ Failed to send personal note: ' + (error.response?.data?.message || error.message));
    }
  };

  const getStatusClass = (status) => {
    const statusClasses = {
      'NEW': 'status-new',
      'UNDER_REVIEW': 'status-under-review',
      'IN_PROGRESS': 'status-in-progress',
      'RESOLVED': 'status-resolved',
      'CLOSED': 'status-closed'
    };
    return `status ${statusClasses[status] || 'status-unknown'}`;
  };

  const getPriorityClass = (priority) => {
    const priorityClasses = {
      'LOW': 'priority-low',
      'MEDIUM': 'priority-medium',
      'HIGH': 'priority-high',
      'URGENT': 'priority-urgent'
    };
    return `priority ${priorityClasses[priority] || 'priority-unknown'}`;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  if (loading) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div className="loading">
          <div className="spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
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

      {/* Header */}
      <div className="dashboard-header" style={{ marginBottom: '2.5rem' }}>
        <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
          Admin Dashboard
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
          Welcome back, {currentUser?.fullName || currentUser?.username}
        </p>
      </div>

      {/* Statistics Overview */}
      <div className="stats-grid" style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', 
        gap: '2rem', 
        marginBottom: '4rem' 
      }}>
        <div className="stat-card">
          <div className="stat-content">
            <h3 className="stat-number">{stats.openComplaints}</h3>
            <p className="stat-label">Open Complaints</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-content">
            <h3 className="stat-number">{stats.resolvedComplaints}</h3>
            <p className="stat-label">Resolved Complaints</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-content">
            <h3 className="stat-number">{stats.averageResolutionTime} days</h3>
            <p className="stat-label">Average Resolution Time</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-content">
            <h3 className="stat-number">{stats.totalComplaints}</h3>
            <p className="stat-label">Total Complaints</p>
          </div>
        </div>
      </div>

      {/* All Complaints */}
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">All Complaints</h2>
          <Link to="/admin/complaints" className="btn btn-outline">Manage All</Link>
        </div>

        {recentComplaints.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
            <p>No recent complaints</p>
          </div>
        ) : (
          <div className="complaints-list" style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
            {recentComplaints.map((complaint) => (
              <div key={complaint.id} className="complaint-item" style={{ 
                padding: '1.5rem', 
                border: '1px solid var(--border-color)', 
                borderRadius: '12px', 
                backgroundColor: 'var(--bg-card)',
                boxShadow: 'var(--shadow-sm)'
              }}>
                <div className="complaint-header" style={{ marginBottom: '1rem' }}>
                  <div className="complaint-info">
                    <h4 className="complaint-title">{complaint.subject}</h4>
                    <div className="complaint-meta">
                      <span>ID: {complaint.complaintId}</span>
                      <span>•</span>
                      <span>Created: {formatDate(complaint.createdAt)}</span>
                      <span>•</span>
                      <span>Category: {complaint.category}</span>
                    </div>
                  </div>
                  <div className="complaint-badges">
                    <span className={getStatusClass(complaint.status)}>
                      {complaint.status?.replace('_', ' ')}
                    </span>
                    <span className={getPriorityClass(complaint.priority)}>
                      {complaint.priority}
                    </span>
                  </div>
                </div>
                
                <p className="complaint-description" style={{ 
                  margin: '1rem 0', 
                  color: 'var(--text-secondary)', 
                  lineHeight: '1.6' 
                }}>
                  {complaint.description?.length > 150 
                    ? `${complaint.description.substring(0, 150)}...` 
                    : complaint.description}
                </p>

                <div className="complaint-actions" style={{ 
                  display: 'flex', 
                  gap: '1rem', 
                  marginTop: '1.5rem' 
                }}>
                  <Link 
                    to={`/admin/complaints/${complaint.complaintId}`} 
                    className="btn btn-primary btn-sm"
                  >
                    View Details
                  </Link>
                  <Link 
                    to={`/admin/complaints/${complaint.complaintId}/assign`} 
                    className="btn btn-secondary btn-sm"
                  >
                    Assign
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Personal Notes Section */}
      <div className="card" style={{ marginTop: '2rem' }}>
        <div className="card-header">
          <h3 className="card-title">💌 Send Personal Note to Employee</h3>
        </div>
        <div style={{ padding: '1.5rem' }}>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
            Send private messages and guidance to your team members
          </p>
          
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: '1fr auto', 
            gap: '1rem', 
            alignItems: 'end',
            marginBottom: '1rem'
          }}>
            <div className="form-group" style={{ margin: 0 }}>
              <label htmlFor="employeeSelect" style={{ marginBottom: '0.5rem', display: 'block' }}>
                Select Employee:
              </label>
              <select
                id="employeeSelect"
                className="form-select"
                value={personalNoteForm.selectedEmployeeId || ''}
                onChange={(e) => setPersonalNoteForm({ 
                  ...personalNoteForm, 
                  selectedEmployeeId: e.target.value 
                })}
                style={{ width: '100%' }}
              >
                <option value="">Choose an employee...</option>
                {employees.map(employee => (
                  <option key={employee.id} value={employee.id}>
                    {employee.fullName} ({employee.role}) - {employee.email}
                  </option>
                ))}
              </select>
            </div>
            
            <button
              onClick={() => {
                const selectedEmployee = employees.find(emp => emp.id.toString() === personalNoteForm.selectedEmployeeId);
                if (selectedEmployee) {
                  handlePersonalNoteClick(selectedEmployee);
                } else {
                  alert('Please select an employee first');
                }
              }}
              className="btn btn-primary"
              disabled={!personalNoteForm.selectedEmployeeId}
            >
              💌 Send Note
            </button>
          </div>
          
          {personalNoteForm.selectedEmployeeId && (
            <div style={{ 
              padding: '1rem', 
              backgroundColor: 'var(--info-light)', 
              borderRadius: '6px',
              border: '1px solid var(--info)',
              marginTop: '1rem'
            }}>
              {(() => {
                const selectedEmployee = employees.find(emp => emp.id.toString() === personalNoteForm.selectedEmployeeId);
                return selectedEmployee ? (
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                      <span style={{ fontSize: '1.2rem' }}>👤</span>
                      <strong>Selected: {selectedEmployee.fullName}</strong>
                    </div>
                    <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                      📧 {selectedEmployee.email} • 🏷️ {selectedEmployee.role}
                    </div>
                  </div>
                ) : null;
              })()}
            </div>
          )}
        </div>
      </div>

      {/* Personal Note Modal */}
      {personalNoteModal.show && (
        <div className="modal-overlay" onClick={() => setPersonalNoteModal({ show: false, employee: null })}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>💌 Send Personal Note</h3>
              <button 
                className="modal-close"
                onClick={() => setPersonalNoteModal({ show: false, employee: null })}
              >
                ✕
              </button>
            </div>
            
            <form onSubmit={handlePersonalNoteSubmit}>
              <div className="modal-body">
                <div style={{ 
                  padding: '1rem', 
                  backgroundColor: 'var(--info-light)', 
                  borderRadius: '6px', 
                  marginBottom: '1rem',
                  border: '1px solid var(--info)'
                }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                    <span style={{ fontSize: '1.2rem' }}>👤</span>
                    <strong>Sending to: {personalNoteModal.employee?.fullName}</strong>
                  </div>
                  <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                    📧 {personalNoteModal.employee?.email} • 🏷️ {personalNoteModal.employee?.role}
                  </div>
                  <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
                    💡 This personal note will be private and only visible to this employee. They will also receive an email notification.
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="personalMessage">Personal Message:</label>
                  <textarea
                    id="personalMessage"
                    name="message"
                    className="form-input form-textarea"
                    placeholder="Enter your personal message to the employee..."
                    value={personalNoteForm.message}
                    onChange={(e) => setPersonalNoteForm({ ...personalNoteForm, message: e.target.value })}
                    required
                    rows={6}
                    style={{ minHeight: '120px' }}
                  />
                  <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
                    💬 Use this to provide guidance, feedback, or important information to the employee.
                  </p>
                </div>
              </div>

              <div className="modal-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setPersonalNoteModal({ show: false, employee: null })}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={!personalNoteForm.message.trim()}
                >
                  💌 Send Personal Note
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;