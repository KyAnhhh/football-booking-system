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
import ImportPage from "../pages/admin/ImportPage";
import InventoryPage from "../pages/admin/InventoryPage";
import MembershipTierPage from "../pages/admin/MembershipTierPage";
import ReviewManagementPage from "../pages/admin/ReviewManagementPage";

import ProtectedRoute from "./ProtectedRoute";
import AdminLayout from "../layouts/AdminLayout";

const ADMIN_MANAGER = ["ADMIN", "MANAGER"];
const ADMIN_MANAGER_STAFF = ["ADMIN", "MANAGER", "STAFF"];

const allow = (element, roles) => (
  <ProtectedRoute roles={roles}>
    {element}
  </ProtectedRoute>
);

const AdminRoutes = (
  <Route
    path="/admin/*"
    element={
      <ProtectedRoute roles={["ADMIN", "MANAGER", "STAFF"]}>
        <AdminLayout />
      </ProtectedRoute>
    }
  >
    <Route index element={allow(<DashboardPage />, ADMIN_MANAGER_STAFF)} />
    <Route path="users" element={allow(<UsersPage />, ADMIN_MANAGER)} />
    <Route path="membership-tiers" element={allow(<MembershipTierPage />, ["ADMIN"])} />
    <Route path="products" element={allow(<ProductPage />, ADMIN_MANAGER)} />
    <Route path="brands" element={allow(<BrandPage />, ADMIN_MANAGER)} />
    <Route path="categories" element={allow(<CategoryPage />, ADMIN_MANAGER)} />
    <Route path="orders" element={allow(<OrderPage />, ADMIN_MANAGER_STAFF)} />
    <Route path="sizes" element={allow(<SizePage />, ADMIN_MANAGER)} />
    <Route path="payments" element={allow(<PaymentPage />, ADMIN_MANAGER)} />
    <Route path="football-fields" element={allow(<FootballFieldPage />, ADMIN_MANAGER)} />
    <Route path="field-calendar" element={allow(<AdminFieldCalendarPage />, ADMIN_MANAGER_STAFF)} />
    <Route path="bookings" element={allow(<BookingPage />, ADMIN_MANAGER_STAFF)} />
    <Route path="bookings/:bookingId" element={allow(<AdminBookingDetailPage />, ADMIN_MANAGER_STAFF)} />
    <Route path="maintenance" element={allow(<MaintenanceManagementPage />, ADMIN_MANAGER)} />
    <Route path="pricing-rules" element={allow(<PricingRulePage />, ADMIN_MANAGER)} />
    <Route path="holiday-pricing-rules" element={allow(<HolidayPricingRulePage />, ADMIN_MANAGER)} />
    <Route path="vouchers" element={allow(<VoucherPage />, ADMIN_MANAGER)} />
    <Route path="extra-services" element={allow(<ExtraServicePage />, ADMIN_MANAGER_STAFF)} />
    <Route path="holidays" element={allow(<HolidayPage />, ADMIN_MANAGER)} />
    <Route path="inventory" element={allow(<InventoryPage />, ADMIN_MANAGER)} />
    <Route path="imports" element={allow(<ImportPage />, ADMIN_MANAGER)} />
    <Route path="reviews" element={allow(<ReviewManagementPage />, ADMIN_MANAGER)} />
  </Route>
);

export default AdminRoutes;
