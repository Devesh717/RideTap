import React from "react";
import { Routes, Route } from "react-router-dom";
import BookRidePage from "../customer/pages/BookRidePage/BookRidePage";
import MyBookingsPage from "../customer/pages/MyBookingsPage/MyBookingsPage";
import BookingDetailsPage from "../customer/pages/BookingDetailsPage/BookingDetailsPage";
import { ProtectedRoute } from "../../auth/Routers/AuthRoutes";

const BookingRoutes = () => {
  return (
    <Routes>
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
    </Routes>
  );
};

export default BookingRoutes;
