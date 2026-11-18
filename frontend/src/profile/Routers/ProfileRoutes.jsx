import React from "react";
import { Routes, Route } from "react-router-dom";
import ProfilePage from "../customer/pages/ProfilePage/ProfilePage";
import { ProtectedRoute } from "../../auth/Routers/AuthRoutes";

const ProfileRoutes = () => {
  return (
    <Routes>
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};

export default ProfileRoutes;
