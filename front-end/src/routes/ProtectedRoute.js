import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const normalizeRole = (value) =>
  String(value || "")
    .replace("ROLE_", "")
    .toUpperCase();

export default function ProtectedRoute({ role, roles, children }) {
  const token = sessionStorage.getItem("accessToken");

  if (!token) {
    return <Navigate to="/login" />;
  }

  let userRole = "";

  try {
    const decoded = jwtDecode(token);
    userRole = decoded.role || "";
  } catch (err) {
    console.error("Invalid token:", err);
    sessionStorage.removeItem("accessToken");
    sessionStorage.removeItem("refreshToken");
    return <Navigate to="/login" />;
  }

  const allowedRoles = roles || (role ? [role] : []);
  const normalizedUserRole = normalizeRole(userRole);
  const canAccess =
    allowedRoles.length === 0 ||
    allowedRoles.some((allowedRole) => normalizedUserRole === normalizeRole(allowedRole));

  if (!canAccess) {
    console.log(`Access denied. Required: ${allowedRoles.join(", ")}, Got: ${userRole}`);
    return <Navigate to="/" />;
  }

  return children;
}
