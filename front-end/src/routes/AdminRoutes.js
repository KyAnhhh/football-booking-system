import { Route } from "react-router-dom";

import DashboardPage from "../pages/admin/DashboardPage";
import UsersPage from "../pages/admin/UserPage";
import ProductPage from "../pages/admin/ProductPage";
import BrandPage from "../pages/admin/BrandPage";
import CategoryPage from "../pages/admin/CategoryPage";
import OrderPage from "../pages/admin/OrderPage";
import SizePage from "../pages/admin/SizePage";
import PaymentPage from "../pages/admin/PaymentPage";
import FootballFieldPage from "../pages/admin/FootballFieldPage";
import BookingPage from "../pages/admin/BookingPage";
import MaintenanceManagementPage from "../pages/admin/MaintenanceManagementPage";

import ProtectedRoute from "./ProtectedRoute";
import AdminLayout from "../layouts/AdminLayout";

const AdminRoutes = (
  <Route
    path="/admin/*"
    element={
      <ProtectedRoute role="admin">
        <AdminLayout />
      </ProtectedRoute>
    }
  >
    <Route index element={<DashboardPage />} />
    <Route path="users" element={<UsersPage />} />
    <Route path="products" element={<ProductPage />} />
    <Route path="brands" element={<BrandPage />} />
    <Route path="categories" element={<CategoryPage />} />
    <Route path="orders" element={<OrderPage />} />
    <Route path="sizes" element={<SizePage />} />
    <Route path="payments" element={<PaymentPage />} />
    <Route path="football-fields" element={<FootballFieldPage />} />
    <Route path="bookings" element={<BookingPage />} />
    <Route path="maintenance" element={<MaintenanceManagementPage />} />
  </Route>
);

export default AdminRoutes;