import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import Header from './components/Header';
import Home from './pages/Home';
import Login from './pages/Login';
import SubmitComplaint from './pages/SubmitComplaint';
import ComplaintStatus from './pages/ComplaintStatus';
import MyComplaints from './pages/MyComplaints';
import EmployeeRequests from './pages/EmployeeRequests';
import AdminDashboard from './pages/AdminDashboard';
import AdminComplaints from './pages/AdminComplaints';
import Reports from './pages/Reports';
import EmployeeDashboard from './pages/EmployeeDashboard';
import './config/axios'; // Configure axios
import './index.css';

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Router>
          <div className="App">
            <Header />
            <main>
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/submit-complaint" element={<SubmitComplaint />} />
                <Route path="/complaint/:complaintId" element={<ComplaintStatus />} />
                <Route path="/my-complaints" element={<MyComplaints />} />
                <Route path="/admin/employee-requests" element={<EmployeeRequests />} />
                <Route path="/admin/dashboard" element={<AdminDashboard />} />
                <Route path="/admin/complaints" element={<AdminComplaints />} />
                <Route path="/admin/reports" element={<Reports />} />
                <Route path="/employee/dashboard" element={<EmployeeDashboard />} />
              </Routes>
            </main>
          </div>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;