import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

const ComplaintStatus = () => {
  const { complaintId } = useParams();
  const { currentUser } = useAuth();
  const [complaint, setComplaint] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updateForm, setUpdateForm] = useState({
    status: '',
    comment: ''
  });
  const [showUpdateForm, setShowUpdateForm] = useState(false);

  useEffect(() => {
    fetchComplaint();
  }, [complaintId]);

  const fetchComplaint = async () => {
    try {
      const response = await axios.get(`/api/complaints/${complaintId}`);
      setComplaint(response.data);
    } catch (error) {
      if (error.response?.status === 403 && error.response?.data?.error) {
        // Handle anonymous complaint access blocked
        setError(error.response.data.message || 'Anonymous complaints cannot be tracked for privacy protection');
      } else {
        setError('Complaint not found or access denied');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`/api/complaints/${complaintId}/status`, updateForm);
      setShowUpdateForm(false);
      setUpdateForm({ status: '', comment: '' });
      fetchComplaint(); // Refresh complaint data
    } catch (error) {
      setError('Failed to update complaint status');
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
      'ESCALATED': '🚨',
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

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  const canUpdateStatus = () => {
    return currentUser && (
      currentUser.roles?.some(role => 
        role.authority === 'ROLE_ADMIN' || 
        role.authority === 'ROLE_EMPLOYEE' ||
        role.authority === 'ROLE_MANAGER' ||
        role.authority === 'ROLE_SUPERVISOR'
      )
    );
  };

  const isAdmin = currentUser?.roles?.some(role => role.authority === 'ROLE_ADMIN');
  const isStaff = currentUser?.roles?.some(role => 
    ['ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_SUPERVISOR'].includes(role.authority)
  );

  if (loading) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div className="loading">
          <div className="spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading complaint details...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div className="card" style={{ textAlign: 'center', maxWidth: '600px', margin: '0 auto' }}>
          <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🕶️</div>
          <h2 style={{ marginBottom: '1rem', color: 'var(--text-primary)' }}>
            Privacy Protected
          </h2>
          <div className="alert alert-warning" style={{ textAlign: 'left', marginBottom: '2rem' }}>
            <p style={{ marginBottom: '1rem' }}>{error}</p>
            <p style={{ marginBottom: '0', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              💡 <strong>Tip:</strong> You can view general complaint updates and similar issues on the public dashboard without compromising privacy.
            </p>
          </div>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
            <Link to="/" className="btn btn-primary">
              🏠 Back to Home
            </Link>
            <Link to="/submit-complaint" className="btn btn-secondary">
              📝 Submit New Complaint
            </Link>
          </div>
        </div>
      </div>
    );
  }

  const isAnonymous = complaint.submissionType === 'ANONYMOUS';

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
        {/* Navigation */}
        <div style={{ marginBottom: '2rem' }}>
          <Link to="/" className="btn btn-secondary">
            ← Back to Dashboard
          </Link>
        </div>

        {/* Complaint Header */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
            <div style={{ flex: 1, minWidth: '300px' }}>
              <h1 style={{ fontSize: '2rem', marginBottom: '0.75rem', color: 'var(--text-primary)', fontWeight: '700' }}>
                📋 {complaint.subject}
              </h1>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', flexWrap: 'wrap' }}>
                {isAnonymous && !isAdmin ? (
                  <div className="complaint-id-hidden">
                    🕶️ Anonymous Complaint
                  </div>
                ) : (
                  <p style={{ color: 'var(--text-secondary)', fontSize: '1rem', margin: 0 }}>
                    🆔 Complaint ID: <strong style={{ color: 'var(--text-primary)' }}>{complaint.complaintId}</strong>
                  </p>
                )}
                {isAnonymous && (
                  <span className="anonymous-badge">
                    🕶️ Anonymous
                  </span>
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

          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', 
            gap: '1.5rem', 
            marginBottom: '2rem',
            padding: '1.5rem',
            backgroundColor: 'var(--bg-secondary)',
            borderRadius: '12px',
            border: '1px solid var(--border-color)'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontSize: '1.2rem' }}>📂</span>
              <div>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Category</div>
                <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>{complaint.category || 'Unknown'}</div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontSize: '1.2rem' }}>🔒</span>
              <div>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Submission Type</div>
                <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>{complaint.submissionType || 'Unknown'}</div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontSize: '1.2rem' }}>📅</span>
              <div>
                <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Created</div>
                <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>
                  {complaint.createdAt ? formatDate(complaint.createdAt) : 'Unknown'}
                </div>
              </div>
            </div>
            {complaint.resolvedAt && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <span style={{ fontSize: '1.2rem' }}>✅</span>
                <div>
                  <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Resolved</div>
                  <div style={{ fontWeight: '600', color: 'var(--success)' }}>{formatDate(complaint.resolvedAt)}</div>
                </div>
              </div>
            )}
          </div>

          <div>
            <h3 style={{ marginBottom: '1rem', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              📝 Description
            </h3>
            <div style={{ 
              padding: '1.5rem', 
              backgroundColor: 'var(--bg-secondary)', 
              borderRadius: '12px',
              border: '1px solid var(--border-color)',
              lineHeight: '1.7',
              color: 'var(--text-primary)'
            }}>
              {complaint.description || 'No description available'}
            </div>
          </div>

          {/* Attachments */}
          {complaint.attachments && complaint.attachments.length > 0 && (
            <div style={{ marginTop: '2rem' }}>
              <h3 style={{ marginBottom: '1rem', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                📎 Attachments ({complaint.attachments.length})
              </h3>
              <div style={{ display: 'grid', gap: '0.75rem' }}>
                {complaint.attachments.map((attachment) => (
                  <div key={attachment.id} style={{ 
                    padding: '1rem', 
                    border: '1px solid var(--border-color)', 
                    borderRadius: '8px',
                    backgroundColor: 'var(--bg-secondary)',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    transition: 'all 0.3s ease'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = 'var(--bg-tertiary)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = 'var(--bg-secondary)';
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <span style={{ fontSize: '1.5rem' }}>📄</span>
                      <div>
                        <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>{attachment.fileName}</div>
                        <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                          {(attachment.fileSize / 1024 / 1024).toFixed(2)} MB • {attachment.mimeType}
                        </div>
                      </div>
                    </div>
                    <span style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                      📅 {formatDate(attachment.uploadedAt)}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Admin Actions */}
          {canUpdateStatus() && (
            <div style={{ marginTop: '2rem', paddingTop: '2rem', borderTop: '2px solid var(--border-color)' }}>
              <h3 style={{ marginBottom: '1rem', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                ⚙️ Admin Actions
              </h3>
              {!showUpdateForm ? (
                <button 
                  onClick={() => setShowUpdateForm(true)}
                  className="btn btn-primary"
                >
                  📝 Update Status
                </button>
              ) : (
                <form onSubmit={handleStatusUpdate} style={{ 
                  padding: '1.5rem', 
                  backgroundColor: 'var(--bg-secondary)', 
                  borderRadius: '12px',
                  border: '1px solid var(--border-color)'
                }}>
                  <div style={{ display: 'grid', gridTemplateColumns: '200px 1fr', gap: '1rem', marginBottom: '1rem' }}>
                    <div className="form-group" style={{ marginBottom: 0 }}>
                      <label className="form-label">🔄 New Status</label>
                      <select
                        className="form-select"
                        value={updateForm.status}
                        onChange={(e) => setUpdateForm({...updateForm, status: e.target.value})}
                        required
                      >
                        <option value="">Select Status</option>
                        <option value="NEW">🆕 New</option>
                        <option value="UNDER_REVIEW">👀 Under Review</option>
                        <option value="IN_PROGRESS">⚙️ In Progress</option>
                        <option value="ESCALATED">🚨 Escalated</option>
                        <option value="RESOLVED">✅ Resolved</option>
                        <option value="CLOSED">🔒 Closed</option>
                      </select>
                    </div>
                    <div className="form-group" style={{ marginBottom: 0 }}>
                      <label className="form-label">💬 Comment</label>
                      <input
                        type="text"
                        className="form-input"
                        placeholder="Add a comment about this update..."
                        value={updateForm.comment}
                        onChange={(e) => setUpdateForm({...updateForm, comment: e.target.value})}
                      />
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: '0.75rem' }}>
                    <button type="submit" className="btn btn-success">
                      ✅ Update Status
                    </button>
                    <button 
                      type="button" 
                      onClick={() => setShowUpdateForm(false)}
                      className="btn btn-secondary"
                    >
                      ❌ Cancel
                    </button>
                  </div>
                </form>
              )}
            </div>
          )}
        </div>

        {/* Timeline */}
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">📈 Updates Timeline</h2>
            <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              Track all status changes and updates for this complaint
            </p>
          </div>

          {complaint.updates && complaint.updates.length > 0 ? (
            <div className="timeline">
              {complaint.updates
                .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
                .filter(update => {
                  // Show all updates to staff, only public updates to regular users
                  if (isStaff) return true;
                  return update.isPublic !== false;
                })
                .map((update, index) => (
                <div key={update.id} className="timeline-item">
                  <div className="timeline-content">
                    <div className="timeline-date">
                      📅 {formatDate(update.createdAt)}
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span className={getStatusClass(update.status)}>
                          {getStatusIcon(update.status)} {update.status ? update.status.replace('_', ' ') : 'Unknown'}
                        </span>
                        {isStaff && update.isPublic === false && (
                          <span style={{ 
                            fontSize: '0.75rem', 
                            padding: '0.25rem 0.5rem', 
                            backgroundColor: 'var(--warning)', 
                            color: 'white', 
                            borderRadius: '4px',
                            fontWeight: '600'
                          }}>
                            🔒 PRIVATE
                          </span>
                        )}
                      </div>
                      <span style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                        👤 by {update.updatedBy || 'System'}
                      </span>
                    </div>
                    {update.comment && (
                      <div style={{ 
                        padding: '1rem', 
                        backgroundColor: update.isPublic === false && isStaff ? 'var(--warning-light)' : 'var(--bg-secondary)', 
                        borderRadius: '8px',
                        border: `1px solid ${update.isPublic === false && isStaff ? 'var(--warning)' : 'var(--border-color)'}`,
                        color: 'var(--text-primary)', 
                        lineHeight: '1.6'
                      }}>
                        💬 {update.comment}
                        {isStaff && update.isPublic === false && (
                          <div style={{ 
                            fontSize: '0.75rem', 
                            color: 'var(--text-secondary)', 
                            marginTop: '0.5rem',
                            fontStyle: 'italic'
                          }}>
                            🔒 This is a private note visible only to staff members
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📭</div>
              <p style={{ fontSize: '1.1rem' }}>No updates available</p>
              <p>Updates will appear here as the complaint is processed</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ComplaintStatus;