import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

const Reports = () => {
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    category: '',
    status: ''
  });
  const [loading, setLoading] = useState(false);
  const [exportFormat, setExportFormat] = useState('csv');

  const categories = [
    { value: '', label: 'All Categories' },
    { value: 'SERVICE', label: 'Service' },
    { value: 'BILLING', label: 'Billing' },
    { value: 'TECHNICAL', label: 'Technical' },
    { value: 'STAFF', label: 'Staff' },
    { value: 'FACILITY', label: 'Facility' },
    { value: 'OTHER', label: 'Other' }
  ];

  const statuses = [
    { value: '', label: 'All Statuses' },
    { value: 'NEW', label: 'New' },
    { value: 'UNDER_REVIEW', label: 'Under Review' },
    { value: 'IN_PROGRESS', label: 'In Progress' },
    { value: 'RESOLVED', label: 'Resolved' },
    { value: 'CLOSED', label: 'Closed' }
  ];

  const handleFilterChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value
    });
  };

  const handleExport = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.startDate) params.append('startDate', filters.startDate);
      if (filters.endDate) params.append('endDate', filters.endDate);
      if (filters.category) params.append('category', filters.category);
      if (filters.status) params.append('status', filters.status);

      const endpoint = exportFormat === 'csv' ? 
        `/api/admin/reports/export/csv?${params}` : 
        `/api/admin/reports/export/pdf?${params}`;

      const response = await axios.get(endpoint, {
        responseType: 'blob'
      });

      // Create download link
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      const filename = `complaints_report_${timestamp}.${exportFormat}`;
      link.setAttribute('download', filename);
      
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error('Export failed:', error);
      alert('❌ Export failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem 20px' }}>
      {/* Header */}
      <div className="page-header">
        <div>
          <h1 style={{ fontSize: '2.5rem', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
            📊 Reports & Exports
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
            Generate and export complaint reports in CSV or PDF format
          </p>
        </div>
        <Link to="/admin/dashboard" className="btn btn-secondary">
          ← Back to Dashboard
        </Link>
      </div>

      <div className="card">
        <div className="card-header">
          <h3 className="card-title">📋 Report Parameters</h3>
          <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
            Configure the filters and export options for your complaint report
          </p>
        </div>

        <div className="card-body">
          {/* Date Range */}
          <div className="form-group">
            <label className="form-label">📅 Date Range</label>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <label style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginBottom: '0.5rem', display: 'block' }}>
                  Start Date
                </label>
                <input
                  type="date"
                  name="startDate"
                  className="form-input"
                  value={filters.startDate}
                  onChange={handleFilterChange}
                />
              </div>
              <div>
                <label style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginBottom: '0.5rem', display: 'block' }}>
                  End Date
                </label>
                <input
                  type="date"
                  name="endDate"
                  className="form-input"
                  value={filters.endDate}
                  onChange={handleFilterChange}
                />
              </div>
            </div>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              Leave empty to include all dates (defaults to last 30 days)
            </p>
          </div>

          {/* Filters */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
            <div className="form-group">
              <label className="form-label">📂 Complaint Categories</label>
              <select
                name="category"
                className="form-select"
                value={filters.category}
                onChange={handleFilterChange}
              >
                {categories.map((category) => (
                  <option key={category.value} value={category.value}>
                    {category.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">📊 Status Filter</label>
              <select
                name="status"
                className="form-select"
                value={filters.status}
                onChange={handleFilterChange}
              >
                {statuses.map((status) => (
                  <option key={status.value} value={status.value}>
                    {status.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Export Options */}
          <div className="form-group">
            <label className="form-label">📤 Export Options</label>
            <div style={{ display: 'flex', gap: '2rem', marginTop: '1rem' }}>
              <label style={{ 
                display: 'flex', 
                alignItems: 'center', 
                gap: '0.75rem',
                padding: '1rem 1.5rem',
                border: `2px solid ${exportFormat === 'csv' ? 'var(--primary)' : 'var(--border-color)'}`,
                borderRadius: '12px',
                cursor: 'pointer',
                backgroundColor: exportFormat === 'csv' ? 'rgba(229, 9, 20, 0.1)' : 'var(--bg-primary)',
                minWidth: '150px'
              }}>
                <input
                  type="radio"
                  name="exportFormat"
                  value="csv"
                  checked={exportFormat === 'csv'}
                  onChange={(e) => setExportFormat(e.target.value)}
                  style={{ accentColor: 'var(--primary)' }}
                />
                <div>
                  <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>📊 CSV</div>
                  <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Spreadsheet format</div>
                </div>
              </label>

              <label style={{ 
                display: 'flex', 
                alignItems: 'center', 
                gap: '0.75rem',
                padding: '1rem 1.5rem',
                border: `2px solid ${exportFormat === 'pdf' ? 'var(--primary)' : 'var(--border-color)'}`,
                borderRadius: '12px',
                cursor: 'pointer',
                backgroundColor: exportFormat === 'pdf' ? 'rgba(229, 9, 20, 0.1)' : 'var(--bg-primary)',
                minWidth: '150px'
              }}>
                <input
                  type="radio"
                  name="exportFormat"
                  value="pdf"
                  checked={exportFormat === 'pdf'}
                  onChange={(e) => setExportFormat(e.target.value)}
                  style={{ accentColor: 'var(--primary)' }}
                />
                <div>
                  <div style={{ fontWeight: '600', color: 'var(--text-primary)' }}>📄 PDF</div>
                  <div style={{ fontSize: '0.875rem', color: 'var(--text-secondary)' }}>Document format</div>
                </div>
              </label>
            </div>
          </div>

          {/* Export Button */}
          <div style={{ display: 'flex', justifyContent: 'center', marginTop: '2rem' }}>
            <button
              onClick={handleExport}
              disabled={loading}
              className="btn btn-primary btn-lg"
              style={{ minWidth: '200px' }}
            >
              {loading ? '⏳ Generating...' : `📤 Generate ${exportFormat.toUpperCase()} Report`}
            </button>
          </div>
        </div>
      </div>

      {/* Info Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.5rem', marginTop: '2rem' }}>
        <div className="card">
          <div className="card-body">
            <h4 style={{ color: 'var(--text-primary)', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              📊 CSV Export Features
            </h4>
            <ul style={{ color: 'var(--text-secondary)', lineHeight: '1.8', paddingLeft: '1.5rem' }}>
              <li>Complete complaint data in spreadsheet format</li>
              <li>Easy to analyze with Excel or Google Sheets</li>
              <li>Includes all complaint fields and metadata</li>
              <li>Perfect for data analysis and reporting</li>
            </ul>
          </div>
        </div>

        <div className="card">
          <div className="card-body">
            <h4 style={{ color: 'var(--text-primary)', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              📄 PDF Export Features
            </h4>
            <ul style={{ color: 'var(--text-secondary)', lineHeight: '1.8', paddingLeft: '1.5rem' }}>
              <li>Professional formatted report document</li>
              <li>Includes summary statistics and breakdowns</li>
              <li>Ready for presentations and official reports</li>
              <li>Contains visual data summaries</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Reports;