import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

const SubmitComplaint = () => {
  const [formData, setFormData] = useState({
    subject: '',
    description: '',
    category: 'SERVICE',
    priority: 'MEDIUM',
    submissionType: 'ANONYMOUS'
  });
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const { currentUser } = useAuth();
  const navigate = useNavigate();

  const categories = [
    { value: 'SERVICE', label: '🛎️ Service', description: 'Issues with service quality or delivery' },
    { value: 'BILLING', label: '💳 Billing', description: 'Payment, invoice, or billing concerns' },
    { value: 'TECHNICAL', label: '⚙️ Technical', description: 'Technical problems or system issues' },
    { value: 'STAFF', label: '👥 Staff', description: 'Staff behavior or service concerns' },
    { value: 'FACILITY', label: '🏢 Facility', description: 'Building, equipment, or facility issues' },
    { value: 'OTHER', label: '📝 Other', description: 'Any other type of complaint' }
  ];

  const priorities = [
    { value: 'LOW', label: '🟢 Low', description: 'Minor issue, can wait' },
    { value: 'MEDIUM', label: '🟡 Medium', description: 'Moderate issue, needs attention' },
    { value: 'HIGH', label: '🟠 High', description: 'Important issue, urgent attention needed' },
    { value: 'URGENT', label: '🔴 Urgent', description: 'Critical issue, immediate action required' }
  ];

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleFileChange = (e) => {
    setFiles(Array.from(e.target.files));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    // Validate public submission requires login
    if (formData.submissionType === 'PUBLIC' && !currentUser) {
      setError('Please login to submit a public complaint. You can still submit anonymously without logging in.');
      setLoading(false);
      return;
    }

    try {
      const submitData = new FormData();
      
      // Add form fields as a Blob with proper content type
      submitData.append('complaint', new Blob([JSON.stringify(formData)], {
        type: 'application/json'
      }));
      
      // Add files
      files.forEach((file) => {
        submitData.append('files', file);
      });

      console.log('Submitting complaint with', files.length, 'files');

      const response = await axios.post('/api/complaints', submitData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      console.log('Complaint submitted successfully:', response.data);

      if (formData.submissionType === 'ANONYMOUS') {
        setSuccess('🎉 Anonymous complaint submitted successfully! Your complaint has been received and will be processed. You can check the public dashboard for updates on similar complaints.');
      } else {
        setSuccess(`🎉 Complaint submitted successfully! Your complaint ID is: ${response.data.complaintId}`);
        
        // Redirect to complaint status page after 3 seconds for public complaints
        setTimeout(() => {
          navigate(`/complaint/${response.data.complaintId}`);
        }, 3000);
      }
      
      // Reset form
      setFormData({
        subject: '',
        description: '',
        category: 'SERVICE',
        priority: 'MEDIUM',
        submissionType: 'ANONYMOUS'
      });
      setFiles([]);

    } catch (error) {
      console.error('Error submitting complaint:', error);
      setError(error.response?.data?.message || error.message || 'Failed to submit complaint');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">📝 Submit Complaint</h2>
            <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              Fill out the form below to submit your complaint. You can choose to submit anonymously or publicly.
            </p>
          </div>

          {success && (
            <div className="alert alert-success">
              ✅ {success}
            </div>
          )}

          {error && (
            <div className="alert alert-error">
              ❌ {error}
            </div>
          )}

          {/* Warning for public submission without login */}
          {formData.submissionType === 'PUBLIC' && !currentUser && (
            <div className="alert alert-warning">
              ⚠️ Please login to submit a public complaint. 
              <a href="/login" style={{ marginLeft: '0.5rem', color: 'var(--primary)', textDecoration: 'underline' }}>
                Login here
              </a> or switch to anonymous submission.
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {/* Submission Type */}
            <div className="form-group">
              <label className="form-label">🔒 Submission Type</label>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginTop: '0.5rem' }}>
                <label style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '0.75rem',
                  padding: '1rem',
                  border: `2px solid ${formData.submissionType === 'ANONYMOUS' ? 'var(--primary)' : 'var(--border-color)'}`,
                  borderRadius: '12px',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  backgroundColor: formData.submissionType === 'ANONYMOUS' ? 'rgba(102, 126, 234, 0.1)' : 'var(--bg-primary)'
                }}>
                  <input
                    type="radio"
                    name="submissionType"
                    value="ANONYMOUS"
                    checked={formData.submissionType === 'ANONYMOUS'}
                    onChange={handleChange}
                    style={{ accentColor: 'var(--primary)' }}
                  />
                  <div>
                    <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>🕶️ Anonymous</div>
                    <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Your identity will be protected</div>
                  </div>
                </label>
                <label style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '0.75rem',
                  padding: '1rem',
                  border: `2px solid ${formData.submissionType === 'PUBLIC' ? 'var(--primary)' : 'var(--border-color)'}`,
                  borderRadius: '12px',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  backgroundColor: formData.submissionType === 'PUBLIC' ? 'rgba(102, 126, 234, 0.1)' : 'var(--bg-primary)'
                }}>
                  <input
                    type="radio"
                    name="submissionType"
                    value="PUBLIC"
                    checked={formData.submissionType === 'PUBLIC'}
                    onChange={handleChange}
                    style={{ accentColor: 'var(--primary)' }}
                  />
                  <div>
                    <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>👤 Public</div>
                    <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
                      {currentUser ? 'Linked to your account' : 'Login required for public submissions'}
                    </div>
                  </div>
                </label>
              </div>
            </div>

            {/* Subject */}
            <div className="form-group">
              <label className="form-label">📋 Subject *</label>
              <input
                type="text"
                name="subject"
                className="form-input"
                placeholder="Brief description of your complaint"
                value={formData.subject}
                onChange={handleChange}
                required
                maxLength={200}
              />
            </div>

            {/* Description */}
            <div className="form-group">
              <label className="form-label">📝 Description *</label>
              <textarea
                name="description"
                className="form-input form-textarea"
                placeholder="Provide detailed information about your complaint..."
                value={formData.description}
                onChange={handleChange}
                required
                rows={6}
              />
            </div>

            {/* Category and Priority */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">📂 Category *</label>
                <select
                  name="category"
                  className="form-select"
                  value={formData.category}
                  onChange={handleChange}
                  required
                >
                  {categories.map((category) => (
                    <option key={category.value} value={category.value} title={category.description}>
                      {category.label}
                    </option>
                  ))}
                </select>
                <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                  {categories.find(c => c.value === formData.category)?.description}
                </p>
              </div>

              <div className="form-group">
                <label className="form-label">⚡ Priority</label>
                <select
                  name="priority"
                  className="form-select"
                  value={formData.priority}
                  onChange={handleChange}
                >
                  {priorities.map((priority) => (
                    <option key={priority.value} value={priority.value} title={priority.description}>
                      {priority.label}
                    </option>
                  ))}
                </select>
                <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.25rem' }}>
                  {priorities.find(p => p.value === formData.priority)?.description}
                </p>
              </div>
            </div>

            {/* File Attachments */}
            <div className="form-group">
              <label className="form-label">📎 Attachments (Optional)</label>
              <input
                type="file"
                className="form-input"
                multiple
                onChange={handleFileChange}
                accept=".jpg,.jpeg,.png,.pdf,.doc,.docx,.txt,.mp4,.avi,.mov,.wmv,.flv,.webm,.mkv"
              />
              <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
                📁 Attach images, documents, or videos to support your complaint. Max 50MB per file.
                <br />
                🎥 Supported video formats: MP4, AVI, MOV, WMV, FLV, WebM, MKV
              </p>
              {files.length > 0 && (
                <div style={{ 
                  marginTop: '1rem', 
                  padding: '1rem', 
                  backgroundColor: 'var(--bg-secondary)', 
                  borderRadius: '8px',
                  border: '1px solid var(--border-color)'
                }}>
                  <p style={{ fontSize: '0.875rem', fontWeight: '600', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
                    📋 Selected files:
                  </p>
                  <ul style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', margin: 0, paddingLeft: '1.5rem' }}>
                    {files.map((file, index) => {
                      const isVideo = file.type.startsWith('video/') || 
                        ['.mp4', '.avi', '.mov', '.wmv', '.flv', '.webm', '.mkv'].some(ext => 
                          file.name.toLowerCase().endsWith(ext));
                      const isImage = file.type.startsWith('image/') || 
                        ['.jpg', '.jpeg', '.png', '.gif', '.bmp'].some(ext => 
                          file.name.toLowerCase().endsWith(ext));
                      const isPdf = file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf');
                      
                      let icon = '📄'; // Default document icon
                      if (isVideo) icon = '🎥';
                      else if (isImage) icon = '🖼️';
                      else if (isPdf) icon = '📋';
                      
                      return (
                        <li key={index} style={{ marginBottom: '0.25rem' }}>
                          {icon} {file.name} ({(file.size / 1024 / 1024).toFixed(2)} MB)
                        </li>
                      );
                    })}
                  </ul>
                </div>
              )}
            </div>

            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end', marginTop: '2rem' }}>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate('/')}
              >
                ❌ Cancel
              </button>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading || (formData.submissionType === 'PUBLIC' && !currentUser)}
                title={formData.submissionType === 'PUBLIC' && !currentUser ? 'Please login to submit public complaints' : ''}
              >
                {loading ? '⏳ Submitting...' : '✅ Submit Complaint'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SubmitComplaint;