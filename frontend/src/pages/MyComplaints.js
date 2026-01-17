import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

const MyComplaints = () => {
  const { currentUser } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    if (currentUser) {
      fetchMyComplaints();
    }
  }, [currentUser, currentPage]);

  const fetchMyComplaints = async () => {
    try {
      const response = await axios.get(`/api/complaints/my?page=${currentPage}&size=10`);
      setComplaints(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      setError('Failed to fetch complaints');
    } finally {
      setLoading(false);
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

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  if (!currentUser) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div className="card" style={{ textAlign: 'center' }}>
          <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🔐</div>
          <div className="alert alert-info">
            🔑 Please log in to view your complaints.
          </div>
          <Link to="/login" className="btn btn-primary" style={{ marginTop: '1rem' }}>
            🚀 Login Now
          </Link>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div className="loading">
          <div className="spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading your complaints...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">📋 My Complaints</h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
            View and track all your submitted complaints with real-time status updates
          </p>
        </div>

        {error && (
          <div className="alert alert-error">
            ❌ {error}
          </div>
        )}

        {complaints.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4rem 2rem' }}>
            <div style={{ fontSize: '4rem', marginBottom: '1.5rem' }}>📭</div>
            <h3 style={{ color: 'var(--text-primary)', marginBottom: '1rem', fontSize: '1.5rem' }}>
              No Complaints Yet
            </h3>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem', fontSize: '1.1rem' }}>
              You haven't submitted any complaints yet. Start by submitting your first complaint.
            </p>
            <Link to="/submit-complaint" className="btn btn-primary">
              📝 Submit Your First Complaint
            </Link>
          </div>
        ) : (
          <>
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
                    <div style={{ flex: 1, minWidth: '250px' }}>
                      <h3 style={{ margin: '0 0 0.75rem 0', color: 'var(--text-primary)', fontSize: '1.25rem', fontWeight: '600' }}>
                        {complaint.subject}
                      </h3>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', flexWrap: 'wrap', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          🆔 {complaint.complaintId}
                        </span>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          📅 {formatDate(complaint.createdAt)}
                        </span>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          🔒 {complaint.submissionType}
                        </span>
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
                  
                  <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem', lineHeight: '1.6' }}>
                    {complaint.description && complaint.description.length > 200 
                      ? `${complaint.description.substring(0, 200)}...` 
                      : (complaint.description || 'No description available')}
                  </p>
                  
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
                    <div style={{ display: 'flex', gap: '1.5rem', fontSize: '0.9rem', color: 'var(--text-secondary)', flexWrap: 'wrap' }}>
                      <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                        📂 {complaint.category || 'Unknown'}
                      </span>
                      {complaint.attachments && complaint.attachments.length > 0 && (
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          📎 {complaint.attachments.length} attachment{complaint.attachments.length !== 1 ? 's' : ''}
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

                  {/* Latest Update */}
                  {complaint.updates && complaint.updates.length > 0 && (
                    <div style={{ 
                      marginTop: '1.5rem', 
                      paddingTop: '1rem', 
                      borderTop: '1px solid var(--border-color)',
                      fontSize: '0.9rem',
                      backgroundColor: 'var(--bg-secondary)',
                      padding: '1rem',
                      borderRadius: '8px',
                      marginLeft: '-1.5rem',
                      marginRight: '-1.5rem',
                      marginBottom: '-1.5rem'
                    }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                        <span style={{ fontWeight: '600', color: 'var(--text-primary)' }}>🔄 Latest Update:</span>
                        <span style={{ color: 'var(--text-secondary)' }}>
                          {formatDate(complaint.updates[complaint.updates.length - 1].createdAt)}
                        </span>
                      </div>
                      <p style={{ color: 'var(--text-secondary)', margin: 0, fontStyle: 'italic' }}>
                        "{complaint.updates[complaint.updates.length - 1].comment || 'Status updated'}"
                      </p>
                    </div>
                  )}
                </div>
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '1rem', marginTop: '2rem', padding: '1rem' }}>
                <button
                  onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                  disabled={currentPage === 0}
                  className="btn btn-secondary"
                  style={{ padding: '0.75rem 1.5rem' }}
                >
                  ← Previous
                </button>
                
                <div style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '0.5rem',
                  padding: '0.75rem 1.5rem',
                  backgroundColor: 'var(--bg-secondary)',
                  borderRadius: '8px',
                  border: '1px solid var(--border-color)',
                  color: 'var(--text-primary)',
                  fontWeight: '500'
                }}>
                  📄 Page {currentPage + 1} of {totalPages}
                </div>
                
                <button
                  onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                  disabled={currentPage === totalPages - 1}
                  className="btn btn-secondary"
                  style={{ padding: '0.75rem 1.5rem' }}
                >
                  Next →
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default MyComplaints;