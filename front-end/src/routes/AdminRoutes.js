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
import AdminFieldCalendarPage from "../pages/admin/AdminFieldCalendarPage";
import BookingPage from "../pages/admin/BookingPage";
import AdminBookingDetailPage from "../pages/admin/AdminBookingDetailPage";
import MaintenanceManagementPage from "../pages/admin/MaintenanceManagementPage";
import PricingRulePage from "../pages/admin/PricingRulePage";
import HolidayPricingRulePage from "../pages/admin/HolidayPricingRulePage";
import VoucherPage from "../pages/admin/VoucherPage";
import ExtraServicePage from "../pages/admin/ExtraServicePage";
import HolidayPage from "../pages/admin/HolidayPage";

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
    <Route path="field-calendar" element={<AdminFieldCalendarPage />} />
    <Route path="bookings" element={<BookingPage />} />
    <Route path="bookings/:bookingId" element={<AdminBookingDetailPage />} />
    <Route path="maintenance" element={<MaintenanceManagementPage />} />
    <Route path="pricing-rules" element={<PricingRulePage />} />
    <Route path="holiday-pricing-rules" element={<HolidayPricingRulePage />} />
    <Route path="vouchers" element={<VoucherPage />} />
    <Route path="extra-services" element={<ExtraServicePage />} />
    <Route path="holidays" element={<HolidayPage />} />
  </Route>
);

export default AdminRoutes;
