// src/components/ProtectedRoute.jsx
import React from "react";
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ role, children }) {
  const token = localStorage.getItem("token");
  const userRole = localStorage.getItem("role")?.toLowerCase().replace("role_", "");

  if (!token) {
    // Nếu chưa login → redirect login
    return <Navigate to="/login" />;
  }

  if (role && role.toLowerCase() !== userRole) {
    // Nếu role không match → redirect trang client
    return <Navigate to="/" />;
  }

  return children;
}
