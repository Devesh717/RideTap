import React from "react";
import { Routes, Route } from "react-router-dom";
import StationsPage from "../customer/pages/StationsPage/StationsPage";

const StationRoutes = () => {
  return (
    <Routes>
      <Route path="/stations" element={<StationsPage />} />
    </Routes>
  );
};

export default StationRoutes;
