import { useState } from "react";
import { Outlet, Link } from "react-router-dom";
import "../assets/styles/AdminLayout.css";

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <div className={`admin-layout ${collapsed ? "collapsed" : ""}`}>
      
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-header">
          <button className="toggle-btn" onClick={() => setCollapsed(!collapsed)}>
            ☰
          </button>
          {!collapsed && <h2 className="logo">⚽ Shop Bóng Đá</h2>}
        </div>

        <nav>
          <ul>
            <li><Link to="/admin">Dashboard</Link></li>
            <li><Link to="/admin/users">Users</Link></li>
            <li><Link to="/admin/products">Products</Link></li>
            <li><Link to="/admin/brands">Brand</Link></li>
            <li><Link to="/admin/categories">Category</Link></li>
            <li><Link to="/admin/orders">Orders</Link></li>
            <li><Link to="/">Quay về trang chủ</Link></li>
          </ul>
        </nav>
      </aside>

      {/* Main Content */}
      <div className="main">
        <header className="navbar">
          <button className="toggle-btn mobile" onClick={() => setCollapsed(!collapsed)}>
            ☰
          </button>
          <h3>Admin Panel</h3>
        </header>

        <div className="content">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
