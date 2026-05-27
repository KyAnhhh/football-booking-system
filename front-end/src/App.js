import { BrowserRouter, Routes, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Login from "./pages/Login";
import AdminLayout from "./layouts/AdminLayout";
import DashboardPage from "./pages/admin/DashboardPage";
import UsersPage from "./pages/admin/UserPage";
import ProductPage from "./pages/admin/ProductPage";
import ProtectedRoute from "./components/ProtectedRoute";
import ClientLayout from "./layouts/ClientLayout";
import Register from "./pages/Register";
import Product from "./pages/client/Product";
import Home from "./pages/client/Home";
import About from "./pages/client/About";
import Contact from "./pages/client/Contact";
import BrandPage from "./pages/admin/BrandPage";
import CategoryPage from "./pages/admin/CategoryPage";
import OrderPage from "./pages/admin/OrderPage";
import Profile from "./pages/client/Profile";
import SizePage from "./pages/admin/SizePage";
import Cart from "./pages/client/Cart";
import Checkout from "./pages/client/Checkout";
import ProductDetail from "./pages/client/ProductDetail";
import PaymentPage from "./pages/admin/PaymentPage";
import MyOrders from "./pages/client/MyOrders";
import Collection from "./pages/client/Collection";
import Service from "./pages/client/Service";
import ForgotPassword from "./pages/Forgot-Password";
import ResetPassword from "./pages/Reset-Password";
import ReviewSection from "./components/review/ReviewSection";
import Review from "./pages/client/Review";
import FootballFieldPage from "./pages/admin/FootballFieldPage";
import BookingPage from "./pages/admin/BookingPage";
import PaymentFootballField from "./pages/client/PaymentFootballField";
import FootballFieldDetail from "./pages/client/FootballFieldDetail";
import FootballField from "./pages/client/FootballField";
import MyBookings from "./pages/client/MyBookings";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />

        {/* Admin routes */}
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
        </Route>

        {/* Client routes */}
          <Route path="/" element={<ClientLayout><Home /></ClientLayout>} />
          <Route path="/products" element={<ClientLayout><Product /></ClientLayout>} />
          <Route path="/products/product-details/:id" element={<ClientLayout><ProductDetail /></ClientLayout>} />
          <Route path="/about" element={<ClientLayout><About /></ClientLayout>} />
          <Route path="/contact" element={<ClientLayout><Contact /></ClientLayout>} />
          <Route path="/profile" element={<ClientLayout><Profile /></ClientLayout>} />
          <Route path="/cart" element={<ClientLayout><Cart /></ClientLayout>} />
          <Route path="/collections" element={<ClientLayout><Collection /></ClientLayout>} />
          <Route path="/service" element={<ClientLayout><Service /></ClientLayout>} />
          <Route path="/my-orders" element={<ClientLayout><MyOrders /></ClientLayout>} />
          <Route path="/my-bookings" element={<ClientLayout><MyBookings /></ClientLayout>} />
          <Route path="/cart/checkout" element={<ClientLayout><Checkout /></ClientLayout>} />
          <Route path="/orders/:orderId/review/:productId" element={<ClientLayout><ReviewSection /></ClientLayout>} />
          <Route path="/review/:id" element={<ClientLayout><Review /></ClientLayout>} />
          <Route path="/football-fields" element={<ClientLayout><FootballField /></ClientLayout>} />
          <Route path="/football-fields/:id/detail" element={<ClientLayout><FootballFieldDetail /></ClientLayout>} />
          <Route path="/football-fields/:id/payment" element={<ClientLayout><PaymentFootballField /></ClientLayout>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
