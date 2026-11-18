import React from "react";
import { Routes, Route, Navigate, useLocation } from "react-router-dom";

// Pages & Route Guards
import LoginPage from "./auth/customer/auth/LoginForm";
import RegisterPage from "./auth/customer/auth/RegisterForm";
import { ProtectedRoute } from "./auth/Routers/AuthRoutes";

import HomePage from "./home/customer/pages/HomePage/HomePage";
import StationsPage from "./station/customer/pages/StationsPage/StationsPage";
import WalletPage from "./wallet/customer/pages/WalletPage/WalletPage";
import BookRidePage from "./booking/customer/pages/BookRidePage/BookRidePage";
import MyBookingsPage from "./booking/customer/pages/MyBookingsPage/MyBookingsPage";
import BookingDetailsPage from "./booking/customer/pages/BookingDetailsPage/BookingDetailsPage";
import AiAssistantPage from "./ai/customer/pages/AiAssistantPage/AiAssistantPage";
import ProfilePage from "./profile/customer/pages/ProfilePage/ProfilePage";
import AdminDashboardPage from "./admin/pages/AdminDashboardPage/AdminDashboardPage";
import AdminRoute  from "./admin/Routers/AdminRoutes";
import MetroCardRechargePage from "./wallet/customer/pages/MetroCardRechargePage/MetroCardRechargePage";

import FloatingAiWidget from "./ai/customer/components/AiAssistant/FloatingAiWidget";
import Navbar from "./home/customer/components/Navigation/Navbar";
import Footer from "./home/customer/components/Footer/Footer";

function App() {
  const location = useLocation();
  const isAuthPage =
    location.pathname === "/login" || location.pathname === "/register";

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-900 antialiased">
      {!isAuthPage && <Navbar />}

      <main className="flex-1">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/stations" element={<StationsPage />} />

          {/* Protected Commuter Routes */}
          <Route
            path="/wallet"
            element={
              <ProtectedRoute>
                <WalletPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/book-ride"
            element={
              <ProtectedRoute>
                <BookRidePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bookings"
            element={
              <ProtectedRoute>
                <MyBookingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/bookings/:id"
            element={
              <ProtectedRoute>
                <BookingDetailsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/ai-assistant"
            element={
              <ProtectedRoute>
                <AiAssistantPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            }
          />

          {/* Admin Console Route */}
          <Route
            path="/admin"
            element={
              <AdminRoute>
                <AdminDashboardPage />
              </AdminRoute>
            }
          />

          <Route path="/recharge" element={<MetroCardRechargePage />} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>

      {!isAuthPage && <FloatingAiWidget />}
      {!isAuthPage && <Footer />}
    </div>
  );
}

export default App;