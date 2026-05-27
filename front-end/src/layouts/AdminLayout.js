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
  BsHouseDoor,
  BsPersonCircle,
  BsBoxArrowRight
} from "react-icons/bs";

import "../assets/styles/AdminLayout.css";

export default function AdminLayout() {

  const [collapsed, setCollapsed] = useState(false);

  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <div className={`admin-layout ${collapsed ? "collapsed" : ""}`}>

      {/* Sidebar */}
      <aside className="sidebar">

        <div className="sidebar-header">
          <button
            className="toggle-btn"
            onClick={() => setCollapsed(!collapsed)}
          >
            ☰
          </button>

          {!collapsed && (
            <h2 className="logo">
              ⚽ Shop Bóng Đá
            </h2>
          )}
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

            <li>
              <NavLink to="/admin/football-fields">
                <BsReceipt />
                <span>Football Fields</span>
              </NavLink>
            </li>

            <li>
              <NavLink to="/admin/bookings">
                <BsReceipt />
                <span>Bookings</span>
              </NavLink>
            </li>

          </ul>
        </nav>

      </aside>

      {/* Main */}
      <div className="main">

        {/* Navbar */}
        <header className="navbar">

          <div className="navbar-left">

            <button
              className="toggle-btn mobile"
              onClick={() => setCollapsed(!collapsed)}
            >
              ☰
            </button>

            <h3>Admin Panel</h3>

          </div>

          {/* User Dropdown */}
          <div
            className={`admin-user-menu ${
              userMenuOpen ? "show" : ""
            }`}
          >

            <div
              className="admin-user-trigger"
              onClick={() =>
                setUserMenuOpen(!userMenuOpen)
              }
            >
              <BsPersonCircle />
              <span>Admin</span>
            </div>

            <div className="admin-user-dropdown">

              <NavLink to="/">
                <BsHouseDoor />
                <span>Quay về trang chủ</span>
              </NavLink>

              <button onClick={handleLogout}>
                <BsBoxArrowRight />
                <span>Đăng xuất</span>
              </button>

            </div>

          </div>

        </header>

        {/* Content */}
        <div className="content">
          <Outlet />
        </div>

      </div>

    </div>
  );
}