import { Outlet, Link } from "react-router-dom";
import "../assets/styles/AdminLayout.css";

export default function AdminLayout() {
  return (
    <div className="admin-layout">
      {/* Sidebar */}
      <aside className="sidebar">
        <h2 className="logo">⚽ Shop Bóng Đá</h2>
        <nav>
          <ul>
            <li><Link to="/admin">Dashboard</Link></li>
            <li><Link to="/admin/users">Users</Link></li>
            <li><Link to="/admin/products">Products</Link></li>
            <li><Link to="/admin/orders">Orders</Link></li>
          </ul>
        </nav>
      </aside>

      {/* Main Content */}
      <div className="main">
        {/* Navbar */}
        <header className="navbar">
          <h3>Admin Panel</h3>
        </header>

        {/* Render Page Con */}
        <div className="content">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
