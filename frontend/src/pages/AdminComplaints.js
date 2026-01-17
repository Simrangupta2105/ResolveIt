import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useComplaintNotifications } from '../hooks/useWebSocket';
import axios from 'axios';

const AdminComplaints = () => {
  const { currentUser } = useAuth();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Check if current user is admin
  const isAdmin = currentUser?.roles?.some(role => role.authority === 'ROLE_ADMIN');
  
  // WebSocket connection for real-time updates
  const { connected } = useComplaintNotifications((notification) => {
    console.log('AdminComplaints - Real-time notification:', notification);
    
    // Refresh complaints list for any relevant updates
    if (['NEW_COMPLAINT', 'STATUS_CHANGE', 'ASSIGNMENT', 'ESCALATION'].includes(notification.type)) {
      fetchComplaints();
    }
  });
  const [filters, setFilters] = useState({
    status: '',
    category: '',
    priority: '',
    assignedTo: '',
    search: ''
  });
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [users, setUsers] = useState([]);
  const [escalateModal, setEscalateModal] = useState({ show: false, complaint: null });
  const [escalateForm, setEscalateForm] = useState({
    higherAuthorityId: '',
    reason: '',
    notifyAllParties: true
  });
  const [noteModal, setNoteModal] = useState({ show: false, complaint: null });
  const [noteForm, setNoteForm] = useState({
    note: '',
    isPublic: true
  });
  const [personalNoteModal, setPersonalNoteModal] = useState({ show: false, employee: null });
  const [personalNoteForm, setPersonalNoteForm] = useState({
    message: '',
    selectedEmployeeId: ''
  });

  const fetchComplaints = async () => {
    try {
      console.log('Fetching complaints with filters:', filters);
      const params = new URLSearchParams({
        page: currentPage,
        size: 10,
        ...Object.fromEntries(Object.entries(filters).filter(([_, v]) => v))
      });

      const response = await axios.get(`/api/admin/complaints?${params}`);
      console.log('Complaints fetched successfully:', response.data);
      setComplaints(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Error fetching complaints:', error);
      alert('❌ Failed to fetch complaints: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await axios.get('/api/admin/assignable-users');
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching assignable users:', error);
    }
  };

  useEffect(() => {
    fetchComplaints();
    if (isAdmin) {
      fetchUsers();
    }
  }, [currentPage, filters, isAdmin]);

  const handleFilterChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value
    });
    setCurrentPage(0);
  };

  const handleAssignComplaint = async (complaintId, userId) => {
    try {
      console.log('Assigning complaint:', complaintId, 'to user:', userId);
      
      // Convert empty string to null for unassignment
      const userIdToSend = userId === '' ? null : userId;
      
      const response = await axios.put(`/api/admin/complaints/${complaintId}/assign`, { userId: userIdToSend });
      console.log('Assignment successful:', response.data);
      
      // Immediately update the local state to reflect the change
      setComplaints(prevComplaints => 
        prevComplaints.map(complaint => 
          complaint.complaintId === complaintId 
            ? { 
                ...complaint, 
                assignedTo: userIdToSend ? users.find(u => u.id.toString() === userId) : null 
              }
            : complaint
        )
      );
      
      // Also fetch fresh data to ensure consistency
      fetchComplaints();
      
      const assignedUser = userIdToSend ? users.find(u => u.id.toString() === userId) : null;
      const message = assignedUser 
        ? `✅ Complaint assigned to ${assignedUser.fullName} successfully!`
        : '✅ Complaint unassigned successfully!';
      alert(message);
    } catch (error) {
      console.error('Error assigning complaint:', error);
      alert('❌ Failed to assign complaint: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleStatusUpdate = async (complaintId, newStatus) => {
    try {
      console.log('Updating status for complaint:', complaintId, 'to:', newStatus);
      await axios.put(`/api/complaints/${complaintId}/status`, { 
        status: newStatus,
        comment: `Status updated to ${newStatus.replace('_', ' ')}`
      });
      console.log('Status update successful');
      fetchComplaints();
    } catch (error) {
      console.error('Error updating status:', error);
      alert('❌ Failed to update status: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleEscalateClick = (complaint) => {
    // Check if escalation is eligible (7-day rule)
    const createdDate = new Date(complaint.createdAt);
    const eligibleDate = new Date(createdDate.getTime() + (7 * 24 * 60 * 60 * 1000));
    const now = new Date();
    
    if (now < eligibleDate) {
      const daysRemaining = Math.ceil((eligibleDate - now) / (24 * 60 * 60 * 1000));
      alert(`⏰ Escalation not allowed yet. Please wait ${daysRemaining} more day(s). Escalation will be available on ${eligibleDate.toLocaleDateString()}`);
      return;
    }
    
    setEscalateModal({ show: true, complaint });
    setEscalateForm({
      higherAuthorityId: '',
      reason: '',
      notifyAllParties: true
    });
  };

  const handleEscalateSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`/api/admin/complaints/${escalateModal.complaint.complaintId}/escalate`, {
        higherAuthorityId: escalateForm.higherAuthorityId || null,
        reason: escalateForm.reason,
        notifyAllParties: escalateForm.notifyAllParties
      });
      
      setEscalateModal({ show: false, complaint: null });
      fetchComplaints();
      alert('✅ Complaint escalated successfully!');
    } catch (error) {
      console.error('Error escalating complaint:', error);
      alert('❌ Failed to escalate complaint: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleNoteClick = (complaint) => {
    setNoteModal({ show: true, complaint });
    setNoteForm({
      note: '',
      isPublic: true
    });
  };

  const handlePersonalNoteClick = (employee) => {
    setPersonalNoteModal({ show: true, employee });
    setPersonalNoteForm({ message: '' });
  };

  const handlePersonalNoteSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/api/personal-notes/send', {
        toEmployeeId: personalNoteModal.employee.id,
        message: personalNoteForm.message
      });
      
      setPersonalNoteModal({ show: false, employee: null });
      setPersonalNoteForm({ message: '' });
      alert(`✅ Personal note sent to ${personalNoteModal.employee.fullName} successfully!`);
    } catch (error) {
      console.error('Error sending personal note:', error);
      alert('❌ Failed to send personal note: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleNoteSubmit = async (e) => {
    e.preventDefault();
    try {
      const endpoint = noteForm.isPublic ? 'notes' : 'private-notes';
      await axios.post(`/api/admin/complaints/${noteModal.complaint.complaintId}/${endpoint}`, {
        note: noteForm.note
      });
      
      setNoteModal({ show: false, complaint: null });
      fetchComplaints();
      alert('✅ Note added successfully!');
    } catch (error) {
      console.error('Error adding note:', error);
      alert('❌ Failed to add note: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleEscalateFormChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEscalateForm({
      ...escalateForm,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleNoteFormChange = (e) => {
    const { name, value, type, checked } = e.target;
    setNoteForm({
      ...noteForm,
      [name]: type === 'checkbox' ? checked : value
    });
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

  const isEscalationEligible = (createdAt) => {
    const createdDate = new Date(createdAt);
    const eligibleDate = new Date(createdDate.getTime() + (7 * 24 * 60 * 60 * 1000));
    const now = new Date();
    return now >= eligibleDate;
  };

  const getEscalationEligibilityText = (createdAt) => {
    const createdDate = new Date(createdAt);
    const eligibleDate = new Date(createdDate.getTime() + (7 * 24 * 60 * 60 * 1000));
    const now = new Date();
    
    if (now >= eligibleDate) {
      return "✅ Eligible for escalation";
    } else {
      const daysRemaining = Math.ceil((eligibleDate - now) / (24 * 60 * 60 * 1000));
      return `⏰ Eligible in ${daysRemaining} day(s) (${eligibleDate.toLocaleDateString()})`;
    }
  };

  if (loading) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div className="loading">
          <div className="spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading complaints...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      {/* Header */}
      <div className="page-header">
        <div>
          <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
            📋 Complaint Management
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
            Manage and track all complaints in the system
          </p>
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
        <Link to="/admin/dashboard" className="btn btn-secondary">
          ← Back to Dashboard
        </Link>
      </div>

      {/* Filters */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <div className="card-header">
          <h3 className="card-title">🔍 Filters</h3>
        </div>
        
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
          <div className="form-group">
            <label className="form-label">Search</label>
            <input
              type="text"
              name="search"
              className="form-input"
              placeholder="Search complaints..."
              value={filters.search}
              onChange={handleFilterChange}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Status</label>
            <select
              name="status"
              className="form-select"
              value={filters.status}
              onChange={handleFilterChange}
            >
              <option value="">All Statuses</option>
              <option value="NEW">New</option>
              <option value="UNDER_REVIEW">Under Review</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="ESCALATED">Escalated</option>
              <option value="RESOLVED">Resolved</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Category</label>
            <select
              name="category"
              className="form-select"
              value={filters.category}
              onChange={handleFilterChange}
            >
              <option value="">All Categories</option>
              <option value="SERVICE">Service</option>
              <option value="BILLING">Billing</option>
              <option value="TECHNICAL">Technical</option>
              <option value="STAFF">Staff</option>
              <option value="FACILITY">Facility</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Priority</label>
            <select
              name="priority"
              className="form-select"
              value={filters.priority}
              onChange={handleFilterChange}
            >
              <option value="">All Priorities</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="URGENT">Urgent</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Assigned To</label>
            <select
              name="assignedTo"
              className="form-select"
              value={filters.assignedTo}
              onChange={handleFilterChange}
            >
              <option value="">All Assignments</option>
              <option value="unassigned">Unassigned</option>
              {users.map(user => (
                <option key={user.id} value={user.id}>{user.fullName}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Complaints List */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">📋 Complaints ({complaints.length})</h3>
          <div style={{ display: 'flex', gap: '1rem' }}>
            {isAdmin && (
              <button 
                onClick={() => setPersonalNoteModal({ show: true, employee: null })}
                className="btn btn-outline"
              >
                💌 Send Personal Note
              </button>
            )}
            <Link to="/admin/reports" className="btn btn-outline">
              📊 Generate Report
            </Link>
            <button className="btn btn-primary" onClick={() => window.location.reload()}>
              🔄 Refresh
            </button>
          </div>
        </div>

        {complaints.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📭</div>
            <p>No complaints found matching your filters</p>
          </div>
        ) : (
          <>
            <div className="complaints-table">
              {complaints.map((complaint) => (
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
                        <br />
                        <span style={{ 
                          fontSize: '0.875rem', 
                          color: isEscalationEligible(complaint.createdAt) ? 'var(--success)' : 'var(--warning)' 
                        }}>
                          {getEscalationEligibilityText(complaint.createdAt)}
                        </span>
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
                                href={`/api/complaints/${complaint.complaintId}/file/${attachment.id}`}
                                target="_blank"
                                rel="noopener noreferrer"
                                download={attachment.fileName}
                                style={{ 
                                  color: 'var(--primary)', 
                                  textDecoration: 'none',
                                  marginLeft: '0.25rem',
                                  cursor: 'pointer'
                                }}
                                onMouseOver={(e) => e.target.style.textDecoration = 'underline'}
                                onMouseOut={(e) => e.target.style.textDecoration = 'none'}
                                title={`Click to open ${attachment.fileName}`}
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
                        Quick Status Update:
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
                        <option value="ESCALATED">Escalated</option>
                        <option value="RESOLVED">Resolved</option>
                        <option value="CLOSED">Closed</option>
                      </select>
                    </div>

                    {isAdmin && (
                      <div className="action-group">
                        <label style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                          Assign To:
                        </label>
                        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                          <select
                            className="form-select"
                            style={{ fontSize: '0.875rem', padding: '0.5rem', flex: 1 }}
                            value={complaint.assignedTo?.id || ''}
                            onChange={(e) => handleAssignComplaint(complaint.complaintId, e.target.value)}
                          >
                            <option value="">Unassigned</option>
                            {users.map(user => (
                              <option key={user.id} value={user.id}>{user.fullName}</option>
                            ))}
                          </select>
                          {complaint.assignedTo && (
                            <button
                              onClick={() => handlePersonalNoteClick(complaint.assignedTo)}
                              className="btn btn-outline btn-sm"
                              style={{ fontSize: '0.75rem', padding: '0.25rem 0.5rem' }}
                              title={`Send personal note to ${complaint.assignedTo.fullName}`}
                            >
                              💌 Note
                            </button>
                          )}
                        </div>
                      </div>
                    )}

                    <div className="action-buttons">
                      <Link 
                        to={`/complaint/${complaint.complaintId}`} 
                        className="btn btn-primary btn-sm"
                      >
                        👁️ View
                      </Link>
                      {isAdmin && (
                        <button 
                          onClick={() => handleNoteClick(complaint)}
                          className="btn btn-outline btn-sm"
                        >
                          📝 Add Note
                        </button>
                      )}
                      <button 
                        onClick={() => handleEscalateClick(complaint)}
                        className={`btn btn-sm ${isEscalationEligible(complaint.createdAt) ? 'btn-secondary' : 'btn-disabled'}`}
                        disabled={!isEscalationEligible(complaint.createdAt)}
                      >
                        🚨 Escalate
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="pagination">
                <button
                  onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                  disabled={currentPage === 0}
                  className="btn btn-secondary"
                >
                  ← Previous
                </button>
                
                <span className="pagination-info">
                  Page {currentPage + 1} of {totalPages}
                </span>
                
                <button
                  onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                  disabled={currentPage === totalPages - 1}
                  className="btn btn-secondary"
                >
                  Next →
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Escalation Modal */}
      {escalateModal.show && (
        <div className="modal-overlay" onClick={() => setEscalateModal({ show: false, complaint: null })}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>🚨 Escalate Complaint</h3>
              <button 
                className="modal-close"
                onClick={() => setEscalateModal({ show: false, complaint: null })}
              >
                ✕
              </button>
            </div>

            <div className="modal-body">
              <div style={{ marginBottom: '1.5rem', padding: '1rem', backgroundColor: 'var(--bg-secondary)', borderRadius: '8px' }}>
                <h4 style={{ marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
                  {escalateModal.complaint?.subject}
                </h4>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                  ID: {escalateModal.complaint?.complaintId} • 
                  Status: {escalateModal.complaint?.status?.replace('_', ' ')} • 
                  Priority: {escalateModal.complaint?.priority}
                </p>
              </div>

              <form onSubmit={handleEscalateSubmit}>
                <div className="form-group">
                  <label className="form-label">👤 Escalate To (Higher Authority)</label>
                  <select
                    name="higherAuthorityId"
                    className="form-select"
                    value={escalateForm.higherAuthorityId}
                    onChange={handleEscalateFormChange}
                  >
                    <option value="">Select Higher Authority (Optional)</option>
                    {users.filter(user => ['ADMIN', 'MANAGER', 'SUPERVISOR'].includes(user.role)).map(user => (
                      <option key={user.id} value={user.id}>
                        {user.fullName} ({user.role})
                      </option>
                    ))}
                  </select>
                  <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                    Leave empty to escalate without specific assignment
                  </p>
                </div>

                <div className="form-group">
                  <label className="form-label">📝 Escalation Reason *</label>
                  <textarea
                    name="reason"
                    className="form-input form-textarea"
                    placeholder="Explain why this complaint needs to be escalated..."
                    value={escalateForm.reason}
                    onChange={handleEscalateFormChange}
                    required
                    rows={4}
                  />
                </div>

                <div className="form-group">
                  <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
                    <input
                      type="checkbox"
                      name="notifyAllParties"
                      checked={escalateForm.notifyAllParties}
                      onChange={handleEscalateFormChange}
                      style={{ accentColor: 'var(--primary)' }}
                    />
                    <span>📧 Notify all parties about escalation</span>
                  </label>
                </div>

                <div className="modal-actions">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => setEscalateModal({ show: false, complaint: null })}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary"
                  >
                    🚨 Escalate Complaint
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Note Modal */}
      {noteModal.show && (
        <div className="modal-overlay" onClick={() => setNoteModal({ show: false, complaint: null })}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>📝 Add Note</h3>
              <button 
                className="modal-close"
                onClick={() => setNoteModal({ show: false, complaint: null })}
              >
                ✕
              </button>
            </div>

            <div className="modal-body">
              <div style={{ marginBottom: '1.5rem', padding: '1rem', backgroundColor: 'var(--bg-secondary)', borderRadius: '8px' }}>
                <h4 style={{ marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
                  {noteModal.complaint?.subject}
                </h4>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                  ID: {noteModal.complaint?.complaintId} • 
                  Status: {noteModal.complaint?.status?.replace('_', ' ')} • 
                  Priority: {noteModal.complaint?.priority}
                </p>
              </div>

              <form onSubmit={handleNoteSubmit}>
                <div className="form-group">
                  <label className="form-label">📝 Note Content *</label>
                  <textarea
                    name="note"
                    className="form-input form-textarea"
                    placeholder="Enter your note here..."
                    value={noteForm.note}
                    onChange={handleNoteFormChange}
                    required
                    rows={4}
                  />
                </div>

                <div className="form-group">
                  <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
                    <input
                      type="checkbox"
                      name="isPublic"
                      checked={noteForm.isPublic}
                      onChange={handleNoteFormChange}
                      style={{ accentColor: 'var(--primary)' }}
                    />
                    <span>👁️ Make this note visible to the complainant</span>
                  </label>
                  <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                    {noteForm.isPublic ? 
                      "This note will be visible to the complainant and all staff members." : 
                      "This note will only be visible to staff members (private note)."}
                  </p>
                </div>

                <div className="modal-actions">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => setNoteModal({ show: false, complaint: null })}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary"
                  >
                    📝 Add Note
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

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
                {!personalNoteModal.employee ? (
                  // Employee selection when opened from general button
                  <div className="form-group" style={{ marginBottom: '1.5rem' }}>
                    <label htmlFor="employeeSelect">Select Employee:</label>
                    <select
                      id="employeeSelect"
                      className="form-select"
                      value={personalNoteForm.selectedEmployeeId || ''}
                      onChange={(e) => {
                        const selectedEmployee = users.find(user => user.id.toString() === e.target.value);
                        setPersonalNoteForm({ 
                          ...personalNoteForm, 
                          selectedEmployeeId: e.target.value 
                        });
                        if (selectedEmployee) {
                          setPersonalNoteModal({ show: true, employee: selectedEmployee });
                        }
                      }}
                      required
                    >
                      <option value="">Choose an employee...</option>
                      {users.filter(user => ['EMPLOYEE', 'MANAGER', 'SUPERVISOR'].includes(user.role)).map(user => (
                        <option key={user.id} value={user.id}>
                          {user.fullName} ({user.role}) - {user.email}
                        </option>
                      ))}
                    </select>
                  </div>
                ) : (
                  // Show selected employee info
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
                      📧 {personalNoteModal.employee?.email}
                    </div>
                    <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
                      💡 This personal note will be private and only visible to this employee. They will also receive an email notification.
                    </div>
                  </div>
                )}

                {personalNoteModal.employee && (
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
                )}
              </div>

              <div className="modal-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setPersonalNoteModal({ show: false, employee: null })}
                >
                  Cancel
                </button>
                {personalNoteModal.employee && (
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={!personalNoteForm.message.trim()}
                  >
                    💌 Send Personal Note
                  </button>
                )}
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminComplaints;