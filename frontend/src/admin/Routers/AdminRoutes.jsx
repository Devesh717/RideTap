import React from "react";
import { Routes, Route } from "react-router-dom";
import AdminDashboardPage from "../pages/AdminDashboardPage/AdminDashboardPage";
import AdminRoute  from "../Routers/AdminRoutes";

const AdminRoutes = () => {
  return (
    <Routes>
      <Route
        path="/admin"
        element={
          <AdminRoute>
            <AdminDashboardPage />
          </AdminRoute>
        }
      />
    </Routes>
  );
};

export default AdminRoutes;
