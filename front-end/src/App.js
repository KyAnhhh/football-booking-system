import { BrowserRouter, Routes, Route } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import AdminLayout from "./layouts/AdminLayout";
import Dashboard from "./pages/admin/Dashboard";
import UsersPage from "./pages/admin/UserPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="users" element={<UsersPage />} />
          {/* <Route path="products" element={<ProductPage />} />
          <Route path="orders" element={<OrderPage />} /> */}
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
