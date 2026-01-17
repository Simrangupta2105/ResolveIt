import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';

const Header = () => {
  const { currentUser, logout } = useAuth();
  const { isDarkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isAdmin = currentUser?.roles?.some(role => role.authority === 'ROLE_ADMIN');
  const isEmployee = currentUser?.roles?.some(role => 
    ['ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_SUPERVISOR'].includes(role.authority)
  );

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <Link to="/" className="logo">
            <span className="logo-icon">🛡️</span>
            Resolve
          </Link>
          
          <nav className="nav">
            {/* Show Dashboard for non-admin users (both logged in and not logged in) */}
            {!isAdmin && !isEmployee && (
              <Link to="/" className="nav-link">
                <span className="nav-icon">📊</span>
                Dashboard
              </Link>
            )}

            {/* Admin Navigation */}
            {isAdmin ? (
              <>
                <Link to="/admin/dashboard" className="nav-link">
                  <span className="nav-icon">👨‍💼</span>
                  Admin Panel
                </Link>
                <Link to="/admin/complaints" className="nav-link">
                  <span className="nav-icon">📋</span>
                  Manage Complaints
                </Link>
                <Link to="/admin/employee-requests" className="nav-link">
                  <span className="nav-icon">👥</span>
                  Employee Requests
                </Link>
              </>
            ) : isEmployee ? (
              /* Employee Navigation */
              <>
                <Link to="/employee/dashboard" className="nav-link">
                  <span className="nav-icon">👨‍💼</span>
                  My Dashboard
                </Link>
              </>
            ) : (
              /* Regular User Navigation */
              <>
                <Link to="/submit-complaint" className="nav-link">
                  <span className="nav-icon">📝</span>
                  Submit Complaint
                </Link>
                {currentUser && (
                  <Link to="/my-complaints" className="nav-link">
                    <span className="nav-icon">📋</span>
                    My Complaints
                  </Link>
                )}
              </>
            )}

            {/* Theme Toggle */}
            <button 
              onClick={toggleTheme} 
              className="theme-toggle"
              title={isDarkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
            >
              {isDarkMode ? '☀️' : '🌙'}
            </button>
            
            {currentUser ? (
              <>
                <span className="nav-link user-info">
                  <span className="nav-icon">👤</span>
                  <span>{currentUser.username}</span>
                </span>
                <button onClick={handleLogout} className="btn btn-outline">
                  <span className="nav-icon">🚪</span>
                  Logout
                </button>
              </>
            ) : (
              <Link to="/login" className="btn btn-primary">
                <span className="nav-icon">🔑</span>
                Login
              </Link>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;