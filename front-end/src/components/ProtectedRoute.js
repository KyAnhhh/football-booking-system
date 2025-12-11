// src/components/ProtectedRoute.jsx
import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

export default function ProtectedRoute({ role, children }) {
  const token = localStorage.getItem("token");

  if (!token) {
    // Nếu chưa login → redirect login
    return <Navigate to="/login" />;
  }

  // Decode token để lấy role
  let userRole = "";
  try {
    const decoded = jwtDecode(token);
    userRole = decoded.role || "";
    // console.log("ProtectedRoute - Decoded role:", userRole);
  } catch (err) {
    console.error("Invalid token:", err);
    localStorage.removeItem("token");
    return <Navigate to="/login" />;
  }

  if (role && !userRole.toUpperCase().includes(role.toUpperCase())) {
    // Nếu role không match → redirect trang client
    console.log(`Access denied. Required: ${role}, Got: ${userRole}`);
    return <Navigate to="/" />;
  }

  return children;
}
