import React, { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../../../../auth/state/Auth/Action";

const Navbar = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { isAuthenticated, isAdmin, user } = useSelector((state) => state.auth);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    dispatch(logout());
    navigate("/login");
  };

  const navLinkClass = ({ isActive }) =>
    `px-3 py-2 rounded-lg text-sm font-medium transition ${
      isActive
        ? "bg-teal-50 text-teal-700 font-semibold"
        : "text-slate-600 hover:text-slate-900 hover:bg-slate-100"
    }`;

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <Link to="/" className="flex items-center gap-2.5 no-underline">
            <div className="w-9 h-9 rounded-xl bg-teal-600 text-white flex items-center justify-center font-extrabold text-lg shadow-xs">
              R
            </div>
            <div>
              <span className="text-lg font-extrabold text-slate-900 tracking-tight">
                Ride<span className="text-teal-600">Tap</span>
              </span>
              <span className="hidden sm:inline-block ml-2 text-[11px] font-semibold uppercase tracking-wider bg-slate-100 text-slate-600 px-2 py-0.5 rounded-full">
                Smart Metro
              </span>
            </div>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center gap-1">
            <NavLink to="/" end className={navLinkClass}>
              Home
            </NavLink>
            <NavLink to="/stations" className={navLinkClass}>
              Stations &amp; Fare
            </NavLink>
            {isAuthenticated && (
              <>
                <NavLink to="/book-ride" className={navLinkClass}>
                  Book Ride
                </NavLink>
                <NavLink to="/bookings" className={navLinkClass}>
                  My Tickets
                </NavLink>
                <NavLink to="/wallet" className={navLinkClass}>
                  Wallet
                </NavLink>
                <NavLink to="/ai-assistant" className={navLinkClass}>
                  AI Concierge
                </NavLink>
                <NavLink to="/profile" className={navLinkClass}>
                  Profile
                </NavLink>
              </>
            )}
            {isAdmin && (
              <NavLink to="/admin" className={navLinkClass}>
                Admin
              </NavLink>
            )}
          </nav>

          {/* Right Auth Controls */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <span className="text-xs text-slate-500 max-w-[160px] truncate">
                  {user?.email}
                </span>
                <button
                  type="button"
                  onClick={handleLogout}
                  className="px-3.5 py-2 rounded-xl text-xs font-semibold bg-slate-900 hover:bg-slate-800 text-white transition cursor-pointer border-0"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="px-3.5 py-2 rounded-xl text-xs font-semibold text-slate-700 hover:bg-slate-100 transition no-underline"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 rounded-xl text-xs font-semibold bg-teal-600 hover:bg-teal-700 text-white shadow-xs transition no-underline"
                >
                  Get Started
                </Link>
              </>
            )}
          </div>

          {/* Mobile Hamburger Button */}
          <button
            type="button"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 rounded-lg text-slate-600 hover:bg-slate-100 bg-transparent border-0 cursor-pointer"
            aria-label="Toggle Menu"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {mobileMenuOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>

        {/* Mobile Dropdown */}
        {mobileMenuOpen && (
          <div className="md:hidden py-3 border-t border-slate-100 flex flex-col gap-1">
            <NavLink to="/" end className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
              Home
            </NavLink>
            <NavLink to="/stations" className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
              Stations &amp; Fare
            </NavLink>
            {isAuthenticated && (
              <>
                <NavLink to="/book-ride" className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
                  Book Ride
                </NavLink>
                <NavLink to="/bookings" className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
                  My Tickets
                </NavLink>
                <NavLink to="/wallet" className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
                  Wallet
                </NavLink>
                <NavLink to="/ai-assistant" className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
                  AI Concierge
                </NavLink>
                <NavLink to="/profile" className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
                  Profile
                </NavLink>
              </>
            )}
            {isAdmin && (
              <NavLink to="/admin" className={navLinkClass} onClick={() => setMobileMenuOpen(false)}>
                Admin
              </NavLink>
            )}
            <div className="pt-2 mt-2 border-t border-slate-100 flex items-center justify-between px-3">
              {isAuthenticated ? (
                <>
                  <span className="text-xs text-slate-500 truncate">{user?.email}</span>
                  <button
                    type="button"
                    onClick={handleLogout}
                    className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-slate-900 text-white border-0 cursor-pointer"
                  >
                    Logout
                  </button>
                </>
              ) : (
                <div className="flex gap-2 w-full">
                  <Link
                    to="/login"
                    onClick={() => setMobileMenuOpen(false)}
                    className="flex-1 text-center py-2 rounded-lg text-xs font-semibold border border-slate-300 text-slate-700 no-underline"
                  >
                    Sign In
                  </Link>
                  <Link
                    to="/register"
                    onClick={() => setMobileMenuOpen(false)}
                    className="flex-1 text-center py-2 rounded-lg text-xs font-semibold bg-teal-600 text-white no-underline"
                  >
                    Register
                  </Link>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </header>
  );
};

export default Navbar;
