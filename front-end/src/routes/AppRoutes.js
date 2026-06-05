import { Routes, Route } from "react-router-dom";

import Login from "../pages/Login";
import Register from "../pages/Register";
import ForgotPassword from "../pages/Forgot-Password";
import ResetPassword from "../pages/Reset-Password";

import AdminRoutes from "./AdminRoutes";
import ClientRoutes from "./ClientRoutes";

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />

      {AdminRoutes}

      {ClientRoutes}
    </Routes>
  );
}

export default AppRoutes;