import React from "react";
import { Routes, Route } from "react-router-dom";
import AiAssistantPage from "../customer/pages/AiAssistantPage/AiAssistantPage";
import { ProtectedRoute } from "../../auth/Routers/AuthRoutes";

const AiRoutes = () => {
  return (
    <Routes>
      <Route
        path="/ai-assistant"
        element={
          <ProtectedRoute>
            <AiAssistantPage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};

export default AiRoutes;
