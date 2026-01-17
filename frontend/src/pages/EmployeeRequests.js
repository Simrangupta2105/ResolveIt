import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

const EmployeeRequests = () => {
  const { currentUser } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    reason: ''
  });

  useEffect(() => {
    if (currentUser && isAdmin()) {
      fetchRequests();
    } else {
      setLoading(false);
    }
  }, [currentUser]);

  const isAdmin = () => {
    return currentUser?.roles?.some(role => role.authority === 'ROLE_ADMIN');
  };

  const fetchRequests = async () => {
    try {
      const response = await axios.get('/api/employee-requests?status=PENDING');
      setRequests(response.data.content);
    } catch (error) {
      setError('Failed to fetch employee requests');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitRequest = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      await axios.post('/api/employee-requests', formData);
      setSuccess('Employee access request submitted successfully! An admin will review your request.');
      setFormData({ email: '', reason: '' });
      setShowForm(false);
    } catch (error) {
      setError('Failed to submit request');
    }
  };

  const handleUpdateStatus = async (requestId, status) => {
    try {
      await axios.put(`/api/employee-requests/${requestId}/status`, { status });
      setSuccess(`Request ${status.toLowerCase()} successfully`);
      fetchRequests(); // Refresh the list
    } catch (error) {
      setError('Failed to update request status');
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div className="loading">
          <div className="spinner"></div>
        </div>
      </div>
    );
  }

  // Show request form for non-admin users
  if (!currentUser || !isAdmin()) {
    return (
      <div className="container" style={{ padding: '2rem 20px' }}>
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Request Employee Access</h2>
              <p style={{ color: '#666', marginTop: '0.5rem' }}>
                Submit a request to gain employee access to the complaint management system
              </p>
            </div>

            {success && (
              <div className="alert alert-success">
                {success}
              </div>
            )}

            {error && (
              <div className="alert alert-error">
                {error}
              </div>
            )}

            {!showForm ? (
              <div style={{ textAlign: 'center', padding: '2rem' }}>
                <p style={{ color: '#666', marginBottom: '1.5rem' }}>
                  Need access to manage complaints? Submit a request and an administrator will review it.
                </p>
                <button 
                  onClick={() => setShowForm(true)}
                  className="btn btn-primary"
                >
                  Submit Access Request
                </button>
              </div>
            ) : (
              <form onSubmit={handleSubmitRequest}>
                <div className="form-group">
                  <label className="form-label">Email Address *</label>
                  <input
                    type="email"
                    className="form-input"
                    placeholder="Enter your work email address"
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Reason for Access *</label>
                  <textarea
                    className="form-input form-textarea"
                    placeholder="Explain why you need employee access to the complaint system..."
                    value={formData.reason}
                    onChange={(e) => setFormData({...formData, reason: e.target.value})}
                    required
                    rows={4}
                  />
                </div>

                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => setShowForm(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary"
                  >
                    Submit Request
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      </div>
    );
  }

  // Admin view
  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Employee Access Requests</h2>
          <p style={{ color: '#666', marginTop: '0.5rem' }}>
            Review and manage pending employee access requests
          </p>
        </div>

        {success && (
          <div className="alert alert-success">
            {success}
          </div>
        )}

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        {requests.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem' }}>
            <p style={{ color: '#666' }}>
              No pending employee access requests
            </p>
          </div>
        ) : (
          <div style={{ display: 'grid', gap: '1rem' }}>
            {requests.map((request) => (
              <div key={request.id} style={{ 
                border: '1px solid #e1e5e9', 
                borderRadius: '8px', 
                padding: '1.5rem',
                backgroundColor: '#fafafa'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '1rem' }}>
                  <div>
                    <h3 style={{ margin: '0 0 0.5rem 0', color: '#333' }}>
                      {request.email}
                    </h3>
                    <p style={{ color: '#666', fontSize: '0.9rem' }}>
                      Requested: {formatDate(request.requestedAt)}
                    </p>
                  </div>
                  <span className={`status status-${request.status.toLowerCase()}`}>
                    {request.status}
                  </span>
                </div>
                
                <div style={{ marginBottom: '1.5rem' }}>
                  <h4 style={{ marginBottom: '0.5rem', color: '#555' }}>Reason:</h4>
                  <p style={{ color: '#555', lineHeight: '1.5' }}>
                    {request.reason}
                  </p>
                </div>
                
                {request.status === 'PENDING' && (
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button 
                      onClick={() => handleUpdateStatus(request.id, 'APPROVED')}
                      className="btn btn-success"
                      style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}
                    >
                      Approve
                    </button>
                    <button 
                      onClick={() => handleUpdateStatus(request.id, 'REJECTED')}
                      className="btn btn-danger"
                      style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}
                    >
                      Reject
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default EmployeeRequests;