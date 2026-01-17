import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useComplaintNotifications } from '../hooks/useWebSocket';
import axios from 'axios';

const EmployeeDashboard = () => {
  const { currentUser } = useAuth();
  const [assignedComplaints, setAssignedComplaints] = useState([]);
  const [allComplaints, setAllComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [realtimeNotification, setRealtimeNotification] = useState(null);
  const [stats, setStats] = useState({
    total: 0,
    new: 0,
    inProgress: 0,
    resolved: 0,
    assigned: 0,
    openComplaints: 0,
    resolvedComplaints: 0,
    averageResolutionTime: 0,
    totalComplaints: 0
  });
  const [activeTab, setActiveTab] = useState('assigned'); // 'assigned', 'all', 'statistics', 'notes'
  const [personalNotes, setPersonalNotes] = useState([]);
  const [unreadNotesCount, setUnreadNotesCount] = useState(0);

  // WebSocket connection for real-time updates
  const { connected } = useComplaintNotifications((notification) => {
    console.log('Employee Dashboard - Real-time notification:', notification);
    
    // Handle personal note notifications
    if (notification.type === 'PERSONAL_NOTE') {
      setRealtimeNotification({
        ...notification,
        message: `📝 New personal note from admin`
      });
      // Refresh personal notes
      fetchPersonalNotes();
    }
    // Show notification if it's relevant to this employee or general updates
    else if (notification.type === 'ASSIGNMENT' && 
        notification.complaint?.assignedTo?.id === currentUser?.id) {
      setRealtimeNotification(notification);
    } else if (['NEW_COMPLAINT', 'STATUS_CHANGE', 'ESCALATION'].includes(notification.type)) {
      setRealtimeNotification(notification);
    }
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
      setRealtimeNotification(null);
    }, 5000);
    
    // Refresh data for any complaint updates
    fetchDashboardData();
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [
        assignedResponse,
        allComplaintsResponse,
        statsResponse
      ] = await Promise.all([
        // Get assigned complaints
        axios.get('/api/admin/complaints?size=100'),
        // Get all complaints
        axios.get('/api/complaints?page=0&size=50'),
        // Get admin stats
        axios.get('/api/admin/dashboard/stats')
      ]);
      
      const allComplaintsData = assignedResponse.data.content || [];
      
      // Filter complaints assigned to current user
      const myComplaints = allComplaintsData.filter(complaint => 
        complaint.assignedTo?.id === currentUser?.id
      );
      
      setAssignedComplaints(myComplaints);
      setAllComplaints(allComplaintsResponse.data.content || []);
      
      // Calculate comprehensive stats
      const comprehensiveStats = {
        // Personal stats
        assigned: myComplaints.length,
        new: myComplaints.filter(c => c.status === 'NEW').length,
        inProgress: myComplaints.filter(c => c.status === 'IN_PROGRESS').length,
        resolved: myComplaints.filter(c => c.status === 'RESOLVED').length,
        // System-wide stats
        totalComplaints: statsResponse.data.totalComplaints || 0,
        openComplaints: statsResponse.data.openComplaints || 0,
        resolvedComplaints: statsResponse.data.resolvedComplaints || 0,
        averageResolutionTime: statsResponse.data.averageResolutionTime || 0
      };
      setStats(comprehensiveStats);
      
      // Fetch personal notes
      await fetchPersonalNotes();
      
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchPersonalNotes = async () => {
    try {
      console.log('Fetching personal notes for user:', currentUser?.id, currentUser?.username);
      const [notesResponse, unreadCountResponse] = await Promise.all([
        axios.get('/api/personal-notes/my-notes?page=0&size=20'),
        axios.get('/api/personal-notes/unread-count')
      ]);
      
      console.log('Personal notes response:', notesResponse.data);
      console.log('Unread count response:', unreadCountResponse.data);
      
      setPersonalNotes(notesResponse.data.content || []);
      setUnreadNotesCount(unreadCountResponse.data.unreadCount || 0);
    } catch (error) {
      console.error('Error fetching personal notes:', error);
      console.error('Error details:', error.response?.data);
      // Don't show error to user as this is a secondary feature
    }
  };

  const markNoteAsRead = async (noteId) => {
    try {
      await axios.put(`/api/personal-notes/${noteId}/read`);
      
      // Update local state
      setPersonalNotes(prevNotes => 
        prevNotes.map(note => 
          note.id === noteId ? { ...note, isRead: true, readAt: new Date().toISOString() } : note
        )
      );
      
      // Update unread count
      setUnreadNotesCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking note as read:', error);
      alert('❌ Failed to mark note as read');
    }
  };

  const handleStatusUpdate = async (complaintId, newStatus) => {
    try {
      await axios.put(`/api/complaints/${complaintId}/status`, {
        status: newStatus,
        comment: `Status updated to ${newStatus.replace('_', ' ')} by ${currentUser?.fullName}`
      });
      fetchDashboardData();
      alert('✅ Status updated successfully!');
    } catch (error) {
      console.error('Error updating status:', error);
      alert('❌ Failed to update status: ' + (error.response?.data?.message || error.message));
    }
  };

  const getStatusClass = (status) => {
    const statusClasses = {
      'NEW': 'status-new',
      'UNDER_REVIEW': 'status-under-review',
      'IN_PROGRESS': 'status-in-progress',
      'ESCALATED': 'status-escalated',
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
      <div className="page-header">
        <div>
          <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
            👨‍💼 Employee Dashboard
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
            Welcome back, {currentUser?.fullName || currentUser?.username}
          </p>
        </div>
        <Link to="/" className="btn btn-secondary">
          ← Back to Home
        </Link>
      </div>

      {/* Navigation Tabs */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem' }}>
          <button 
            className={`btn ${activeTab === 'assigned' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => setActiveTab('assigned')}
          >
            📋 My Assignments ({stats.assigned})
          </button>
          <button 
            className={`btn ${activeTab === 'all' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => setActiveTab('all')}
          >
            🌐 All Complaints ({allComplaints.length})
          </button>
          <button 
            className={`btn ${activeTab === 'statistics' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => setActiveTab('statistics')}
          >
            📊 Statistics & Reports
          </button>
          <button 
            className={`btn ${activeTab === 'notes' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => setActiveTab('notes')}
          >
            📝 Personal Notes {unreadNotesCount > 0 && <span className="badge">{unreadNotesCount}</span>}
          </button>
        </div>
      </div>

      {/* Statistics Overview */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', 
        gap: '1.5rem', 
        marginBottom: '2rem' 
      }}>
        <div className="card" style={{ textAlign: 'center', padding: '1.5rem' }}>
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>📋</div>
          <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--primary)', marginBottom: '0.25rem' }}>
            {stats.assigned}
          </div>
          <div style={{ color: 'var(--text-secondary)' }}>My Assignments</div>
        </div>

        <div className="card" style={{ textAlign: 'center', padding: '1.5rem' }}>
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>🆕</div>
          <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--info)', marginBottom: '0.25rem' }}>
            {stats.new}
          </div>
          <div style={{ color: 'var(--text-secondary)' }}>New</div>
        </div>

        <div className="card" style={{ textAlign: 'center', padding: '1.5rem' }}>
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>⚙️</div>
          <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--warning)', marginBottom: '0.25rem' }}>
            {stats.inProgress}
          </div>
          <div style={{ color: 'var(--text-secondary)' }}>In Progress</div>
        </div>

        <div className="card" style={{ textAlign: 'center', padding: '1.5rem' }}>
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>✅</div>
          <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--success)', marginBottom: '0.25rem' }}>
            {stats.resolved}
          </div>
          <div style={{ color: 'var(--text-secondary)' }}>Resolved</div>
        </div>
      </div>

      {/* Tab Content */}
      {activeTab === 'assigned' && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">📋 Your Assigned Complaints ({assignedComplaints.length})</h3>
            <button className="btn btn-primary" onClick={() => window.location.reload()}>
              🔄 Refresh
            </button>
          </div>

          {assignedComplaints.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📭</div>
              <p>No complaints assigned to you yet</p>
              <p>Check back later or contact your supervisor</p>
            </div>
          ) : (
            <div className="complaints-table">
              {assignedComplaints.map((complaint) => (
                <div key={complaint.id} className="complaint-row">
                  <div className="complaint-main">
                    <div className="complaint-info">
                      <h4 className="complaint-title">
                        <Link to={`/complaint/${complaint.complaintId}`}>
                          {complaint.subject}
                        </Link>
                      </h4>
                      <div className="complaint-meta">
                        <span className="complaint-id">ID: {complaint.complaintId}</span>
                        <span>•</span>
                        <span>Created: {formatDate(complaint.createdAt)}</span>
                        <span>•</span>
                        <span>Category: {complaint.category}</span>
                        {complaint.submissionType === 'ANONYMOUS' && (
                          <>
                            <span>•</span>
                            <span className="anonymous-badge">🕶️ Anonymous</span>
                          </>
                        )}
                      </div>
                      <div style={{ marginTop: '0.5rem', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                        📝 {complaint.description?.substring(0, 100)}
                        {complaint.description?.length > 100 && '...'}
                      </div>
                      
                      {/* File Attachments */}
                      {complaint.attachments && complaint.attachments.length > 0 && (
                        <div style={{ marginTop: '0.5rem', fontSize: '0.875rem' }}>
                          <span style={{ color: 'var(--text-secondary)' }}>📎 Attachments: </span>
                          {complaint.attachments.map((attachment, index) => (
                            <span key={attachment.id} style={{ marginRight: '0.5rem' }}>
                              {attachment.mimeType?.startsWith('video/') ? '🎥' : 
                               attachment.mimeType?.startsWith('image/') ? '🖼️' : '📄'}
                              <a 
                                href={`/api/complaints/${complaint.complaintId}/attachments/${attachment.id}/view`}
                                target="_blank"
                                rel="noopener noreferrer"
                                style={{ 
                                  color: 'var(--primary)', 
                                  textDecoration: 'none',
                                  marginLeft: '0.25rem'
                                }}
                                onMouseOver={(e) => e.target.style.textDecoration = 'underline'}
                                onMouseOut={(e) => e.target.style.textDecoration = 'none'}
                              >
                                {attachment.fileName}
                              </a>
                              {index < complaint.attachments.length - 1 && ', '}
                            </span>
                          ))}
                        </div>
                      )}
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

                  <div className="complaint-actions">
                    <div className="action-group">
                      <label style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                        Update Status:
                      </label>
                      <select
                        className="form-select"
                        style={{ fontSize: '0.875rem', padding: '0.5rem' }}
                        value={complaint.status}
                        onChange={(e) => handleStatusUpdate(complaint.complaintId, e.target.value)}
                      >
                        <option value="NEW">New</option>
                        <option value="UNDER_REVIEW">Under Review</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="RESOLVED">Resolved</option>
                      </select>
                    </div>

                    <div className="action-buttons">
                      <Link 
                        to={`/complaint/${complaint.complaintId}`} 
                        className="btn btn-primary btn-sm"
                      >
                        👁️ View Details
                      </Link>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'all' && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">🌐 All System Complaints ({allComplaints.length})</h3>
            <div style={{ display: 'flex', gap: '1rem' }}>
              <button className="btn btn-primary" onClick={() => window.location.reload()}>
                🔄 Refresh
              </button>
            </div>
          </div>

          {allComplaints.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📭</div>
              <p>No complaints in the system</p>
            </div>
          ) : (
            <div className="complaints-table">
              {allComplaints.slice(0, 20).map((complaint) => (
                <div key={complaint.id} className="complaint-row">
                  <div className="complaint-main">
                    <div className="complaint-info">
                      <h4 className="complaint-title">
                        <Link to={`/complaint/${complaint.complaintId}`}>
                          {complaint.subject}
                        </Link>
                      </h4>
                      <div className="complaint-meta">
                        <span className="complaint-id">ID: {complaint.complaintId}</span>
                        <span>•</span>
                        <span>Created: {formatDate(complaint.createdAt)}</span>
                        <span>•</span>
                        <span>Category: {complaint.category}</span>
                        {complaint.submissionType === 'ANONYMOUS' && (
                          <>
                            <span>•</span>
                            <span className="anonymous-badge">🕶️ Anonymous</span>
                          </>
                        )}
                        {complaint.assignedTo && (
                          <>
                            <span>•</span>
                            <span>Assigned to: {complaint.assignedTo.fullName}</span>
                          </>
                        )}
                      </div>
                      
                      {/* File Attachments */}
                      {complaint.attachments && complaint.attachments.length > 0 && (
                        <div style={{ marginTop: '0.5rem', fontSize: '0.875rem' }}>
                          <span style={{ color: 'var(--text-secondary)' }}>📎 Attachments: </span>
                          {complaint.attachments.map((attachment, index) => (
                            <span key={attachment.id} style={{ marginRight: '0.5rem' }}>
                              {attachment.mimeType?.startsWith('video/') ? '🎥' : 
                               attachment.mimeType?.startsWith('image/') ? '🖼️' : '📄'}
                              <a 
                                href={`/api/complaints/${complaint.complaintId}/attachments/${attachment.id}/view`}
                                target="_blank"
                                rel="noopener noreferrer"
                                style={{ 
                                  color: 'var(--primary)', 
                                  textDecoration: 'none',
                                  marginLeft: '0.25rem'
                                }}
                                onMouseOver={(e) => e.target.style.textDecoration = 'underline'}
                                onMouseOut={(e) => e.target.style.textDecoration = 'none'}
                              >
                                {attachment.fileName}
                              </a>
                              {index < complaint.attachments.length - 1 && ', '}
                            </span>
                          ))}
                        </div>
                      )}
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

                  <div className="complaint-actions">
                    <div className="action-buttons">
                      <Link 
                        to={`/complaint/${complaint.complaintId}`} 
                        className="btn btn-primary btn-sm"
                      >
                        👁️ View
                      </Link>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'statistics' && (
        <div>
          {/* System-wide Statistics */}
          <div className="card" style={{ marginBottom: '2rem' }}>
            <div className="card-header">
              <h3 className="card-title">📊 System-wide Statistics</h3>
            </div>
            <div style={{ 
              display: 'grid', 
              gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', 
              gap: '2rem', 
              padding: '1rem' 
            }}>
              <div style={{ textAlign: 'center', padding: '1.5rem', backgroundColor: 'var(--bg-secondary)', borderRadius: '8px' }}>
                <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>📈</div>
                <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--primary)', marginBottom: '0.25rem' }}>
                  {stats.totalComplaints}
                </div>
                <div style={{ color: 'var(--text-secondary)' }}>Total Complaints</div>
              </div>

              <div style={{ textAlign: 'center', padding: '1.5rem', backgroundColor: 'var(--bg-secondary)', borderRadius: '8px' }}>
                <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>🔓</div>
                <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--warning)', marginBottom: '0.25rem' }}>
                  {stats.openComplaints}
                </div>
                <div style={{ color: 'var(--text-secondary)' }}>Open Complaints</div>
              </div>

              <div style={{ textAlign: 'center', padding: '1.5rem', backgroundColor: 'var(--bg-secondary)', borderRadius: '8px' }}>
                <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>✅</div>
                <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--success)', marginBottom: '0.25rem' }}>
                  {stats.resolvedComplaints}
                </div>
                <div style={{ color: 'var(--text-secondary)' }}>Resolved Complaints</div>
              </div>

              <div style={{ textAlign: 'center', padding: '1.5rem', backgroundColor: 'var(--bg-secondary)', borderRadius: '8px' }}>
                <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>⏱️</div>
                <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--info)', marginBottom: '0.25rem' }}>
                  {stats.averageResolutionTime}
                </div>
                <div style={{ color: 'var(--text-secondary)' }}>Avg Resolution (days)</div>
              </div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'notes' && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">📝 Personal Notes from Admin ({personalNotes.length})</h3>
            <button className="btn btn-primary" onClick={fetchPersonalNotes}>
              🔄 Refresh
            </button>
          </div>

          {personalNotes.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📭</div>
              <p>No personal notes from admin yet</p>
              <p>Check back later for important messages</p>
            </div>
          ) : (
            <div className="notes-list">
              {personalNotes.map((note) => (
                <div 
                  key={note.id} 
                  className={`note-item ${!note.isRead ? 'unread' : ''}`}
                  style={{
                    padding: '1.5rem',
                    marginBottom: '1rem',
                    border: '1px solid var(--border-color)',
                    borderRadius: '8px',
                    backgroundColor: !note.isRead ? 'var(--info-light)' : 'var(--bg-primary)',
                    borderLeft: !note.isRead ? '4px solid var(--info)' : '4px solid var(--border-color)'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
                        <span style={{ fontSize: '1.2rem', fontWeight: '600', color: 'var(--text-primary)' }}>
                          👨‍💼 From: {note.fromAdmin?.fullName || 'Admin'}
                        </span>
                        {!note.isRead && (
                          <span className="badge" style={{ 
                            backgroundColor: 'var(--info)', 
                            color: 'white', 
                            padding: '0.25rem 0.5rem', 
                            borderRadius: '12px', 
                            fontSize: '0.75rem' 
                          }}>
                            NEW
                          </span>
                        )}
                      </div>
                      <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                        📅 {new Date(note.createdAt).toLocaleString()}
                        {note.readAt && (
                          <span style={{ marginLeft: '1rem' }}>
                            👁️ Read: {new Date(note.readAt).toLocaleString()}
                          </span>
                        )}
                      </div>
                    </div>
                    {!note.isRead && (
                      <button
                        onClick={() => markNoteAsRead(note.id)}
                        className="btn btn-outline btn-sm"
                        style={{ fontSize: '0.75rem' }}
                      >
                        ✓ Mark as Read
                      </button>
                    )}
                  </div>
                  
                  <div style={{ 
                    padding: '1rem', 
                    backgroundColor: 'var(--bg-secondary)', 
                    borderRadius: '6px',
                    border: '1px solid var(--border-color)'
                  }}>
                    <div style={{ 
                      fontSize: '1rem', 
                      lineHeight: '1.6', 
                      color: 'var(--text-primary)',
                      whiteSpace: 'pre-wrap'
                    }}>
                      {note.message}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default EmployeeDashboard;