import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './PersonalNotes.css';

const PersonalNotes = () => {
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPersonalNotes();
  }, []);

  const fetchPersonalNotes = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('/api/personal-notes', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setNotes(response.data);
    } catch (error) {
      console.error('Error fetching personal notes:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="personal-notes-container">
      <h2>Personal Notes</h2>
      {notes.length === 0 ? (
        <p>No personal notes yet.</p>
      ) : (
        <div className="notes-list">
          {notes.map(note => (
            <div key={note.id} className="note-card">
              <h4>{note.fromUser}</h4>
              <p>{note.message}</p>
              <small>{new Date(note.createdAt).toLocaleString()}</small>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default PersonalNotes;
