import React from "react";
import { Routes, Route } from "react-router-dom";
import WalletPage from "../customer/pages/WalletPage/WalletPage";
import { ProtectedRoute } from "../../auth/Routers/AuthRoutes";

const WalletRoutes = () => {
  return (
    <Routes>
      <Route
        path="/wallet"
        element={
          <ProtectedRoute>
            <WalletPage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};

export default WalletRoutes;
