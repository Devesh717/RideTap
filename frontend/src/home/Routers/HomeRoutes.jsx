import React from "react";
import { Routes, Route } from "react-router-dom";
import HomePage from "../customer/pages/HomePage/HomePage";

const HomeRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
    </Routes>
  );
};

export default HomeRoutes;
