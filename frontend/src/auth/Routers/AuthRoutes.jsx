import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useSelector } from "react-redux";
import LoginForm from "../customer/auth/LoginForm";
import RegisterForm from "../customer/auth/RegisterForm";

// Reusable Route Guard for authenticated users (and optional admin-only check)
export const ProtectedRoute = ({ children, requireAdmin = false }) => {
  const { isAuthenticated, isAdmin } = useSelector((state) => state.auth);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin && !isAdmin) {
    return <Navigate to="/" replace />;
  }

  return children;
};

const AuthPageWrapper = ({ children }) => (
  <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-900 via-slate-800 to-teal-800 p-6">
    <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-8 sm:p-9">
      {children}
    </div>
  </div>
);

const AuthRoutes = () => {
  return (
    <Routes>
      <Route
        path="/login"
        element={
          <AuthPageWrapper>
            <LoginForm />
          </AuthPageWrapper>
        }
      />
      <Route
        path="/register"
        element={
          <AuthPageWrapper>
            <RegisterForm />
          </AuthPageWrapper>
        }
      />
    </Routes>
  );
};

export default AuthRoutes;
