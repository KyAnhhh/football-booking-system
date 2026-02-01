import { useState } from "react";
import { Outlet, NavLink } from "react-router-dom";
import {
  BsSpeedometer2,
  BsPeople,
  BsBoxSeam,
  BsTags,
  BsGrid,
  BsReceipt,
  BsRulers,
  BsHouseDoor
} from "react-icons/bs";

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

        <nav className="admin-nav">
          <ul>
            <li>
              <NavLink to="/admin" end>
                <BsSpeedometer2 />
                <span>Dashboard</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/users">
                <BsPeople />
                <span>Users</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/products">
                <BsBoxSeam />
                <span>Products</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/brands">
                <BsTags />
                <span>Brand</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/categories">
                <BsGrid />
                <span>Category</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/orders">
                <BsReceipt />
                <span>Orders</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/sizes">
                <BsRulers />
                <span>Sizes</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/payments">
                <BsReceipt />
                <span>Payments</span>
              </NavLink>
            </li>

            <li className="back-home">
              <NavLink to="/">
                <BsHouseDoor />
                <span>Quay về trang chủ</span>
              </NavLink>
            </li>
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
