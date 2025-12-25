import { BrowserRouter, Routes, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Login from "./pages/Login";
import AdminLayout from "./layouts/AdminLayout";
import Dashboard from "./pages/admin/Dashboard";
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

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Admin routes */}
        <Route
          path="/admin/*"
          element={
            <ProtectedRoute role="admin">
              <AdminLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Dashboard />} />
          <Route path="users" element={<UsersPage />} />
          <Route path="products" element={<ProductPage />} />
          <Route path="brands" element={<BrandPage />} />
          <Route path="categories" element={<CategoryPage />} />
          <Route path="orders" element={<OrderPage />} />
        </Route>

        {/* Client routes */}
        <Route
          path="/"
          element={
            <ClientLayout>
              <Home />
            </ClientLayout>
          }
        />
        <Route
          path="/products"
          element={
            <ClientLayout>
              <Product />
            </ClientLayout>
          }
        />
        <Route
          path="/about"
          element={
            <ClientLayout>
              <About />
            </ClientLayout>
          }
        />
        <Route
          path="/contact"
          element={
            <ClientLayout>
              <Contact />
            </ClientLayout>
          }
        />
        <Route
          path="/profile"
          element={
            <ClientLayout>
              <Profile />
            </ClientLayout>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
