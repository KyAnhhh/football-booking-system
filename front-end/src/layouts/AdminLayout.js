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
  BsBoxArrowRight,
  BsTools,
  BsCalendarEvent,
  BsTicketPerforated,
  BsCashCoin,
  BsPlusSquare,
} from "react-icons/bs";

import "../assets/styles/AdminLayout.css";
import "../assets/styles/AdminManager.css";

const navItems = [
  { to: "/admin", label: "Dashboard", icon: BsSpeedometer2, end: true },
  { to: "/admin/users", label: "Users", icon: BsPeople },
  { to: "/admin/products", label: "Products", icon: BsBoxSeam },
  { to: "/admin/brands", label: "Brand", icon: BsTags },
  { to: "/admin/categories", label: "Category", icon: BsGrid },
  { to: "/admin/orders", label: "Orders", icon: BsReceipt },
  { to: "/admin/sizes", label: "Sizes", icon: BsRulers },
  { to: "/admin/payments", label: "Payments", icon: BsReceipt },
  { to: "/admin/football-fields", label: "Football Fields", icon: BsReceipt },
  { to: "/admin/maintenance", label: "Maintenance", icon: BsTools },
  { to: "/admin/pricing-rules", label: "Pricing Rules", icon: BsCashCoin },
  { to: "/admin/vouchers", label: "Vouchers", icon: BsTicketPerforated },
  { to: "/admin/extra-services", label: "Extra Services", icon: BsPlusSquare },
  { to: "/admin/holidays", label: "Holidays", icon: BsCalendarEvent },
  { to: "/admin/bookings", label: "Bookings", icon: BsReceipt },
];

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  return (
    <div className={`admin-layout ${collapsed ? "collapsed" : ""}`}>
      <aside className="sidebar">
        <div className="sidebar-header">
          <button className="toggle-btn" onClick={() => setCollapsed(!collapsed)}>
            ☰
          </button>

          {!collapsed && <h2 className="logo">Football Admin</h2>}
        </div>

        <nav className="admin-nav">
          <ul>
            {navItems.map((item) => {
              const Icon = item.icon;
              return (
                <li key={item.to}>
                  <NavLink to={item.to} end={item.end}>
                    <Icon />
                    <span>{item.label}</span>
                  </NavLink>
                </li>
              );
            })}
          </ul>
        </nav>
      </aside>

      <div className="main">
        <header className="navbar">
          <div className="navbar-left">
            <button className="toggle-btn mobile" onClick={() => setCollapsed(!collapsed)}>
              ☰
            </button>
            <h3>Admin Panel</h3>
          </div>

          <div className={`admin-user-menu ${userMenuOpen ? "show" : ""}`}>
            <div className="admin-user-trigger" onClick={() => setUserMenuOpen(!userMenuOpen)}>
              <BsPersonCircle />
              <span>Admin</span>
            </div>

            <div className="admin-user-dropdown">
              <NavLink to="/">
                <BsHouseDoor />
                <span>Back to site</span>
              </NavLink>

              <button onClick={handleLogout}>
                <BsBoxArrowRight />
                <span>Logout</span>
              </button>
            </div>
          </div>
        </header>

        <div className="content">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
