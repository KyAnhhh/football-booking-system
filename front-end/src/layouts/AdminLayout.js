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
  BsCalendarWeek,
  BsTicketPerforated,
  BsCashCoin,
  BsPlusSquare,
  BsList,
} from "react-icons/bs";

import "../assets/styles/AdminLayout.css";
import "../assets/styles/AdminManager.css";

const navItems = [
  { to: "/admin", label: "Tổng quan", icon: BsSpeedometer2, end: true },
  { to: "/admin/bookings", label: "Đặt sân", icon: BsReceipt },
  { to: "/admin/field-calendar", label: "Lịch sân", icon: BsCalendarWeek },
  { to: "/admin/football-fields", label: "Sân bóng", icon: BsHouseDoor },
  { to: "/admin/pricing-rules", label: "Bảng giá", icon: BsCashCoin },
  { to: "/admin/maintenance", label: "Bảo trì", icon: BsTools },
  { to: "/admin/vouchers", label: "Voucher", icon: BsTicketPerforated },
  { to: "/admin/extra-services", label: "Dịch vụ thêm", icon: BsPlusSquare },
  { to: "/admin/holidays", label: "Ngày lễ", icon: BsCalendarEvent },
  { to: "/admin/products", label: "Sản phẩm", icon: BsBoxSeam },
  { to: "/admin/brands", label: "Thương hiệu", icon: BsTags },
  { to: "/admin/categories", label: "Danh mục", icon: BsGrid },
  { to: "/admin/sizes", label: "Kích cỡ", icon: BsRulers },
  { to: "/admin/orders", label: "Đơn hàng", icon: BsReceipt },
  { to: "/admin/payments", label: "Thanh toán", icon: BsReceipt },
  { to: "/admin/users", label: "Người dùng", icon: BsPeople },
];

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const toggleSidebar = () => {
    setCollapsed((current) => !current);
  };

  const toggleUserMenu = () => {
    setUserMenuOpen((current) => !current);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    sessionStorage.removeItem("accessToken");
    sessionStorage.removeItem("refreshToken");
    window.location.href = "/login";
  };

  return (
    <div className={`admin-layout ${collapsed ? "collapsed" : ""}`}>
      <aside className="sidebar">
        <div className="sidebar-header">
          <button
            className="toggle-btn"
            type="button"
            aria-label="Thu gọn menu quản trị"
            onClick={toggleSidebar}
          >
            <BsList />
          </button>

          {!collapsed && <h2 className="logo">Football Admin</h2>}
        </div>

        <nav className="admin-nav" aria-label="Điều hướng quản trị">
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
            <button
              className="toggle-btn mobile"
              type="button"
              aria-label="Mở menu quản trị"
              onClick={toggleSidebar}
            >
              <BsList />
            </button>
            <h3>Bảng điều khiển</h3>
          </div>

          <div className={`admin-user-menu ${userMenuOpen ? "show" : ""}`}>
            <button
              className="admin-user-trigger"
              type="button"
              aria-expanded={userMenuOpen}
              onClick={toggleUserMenu}
            >
              <BsPersonCircle />
              <span>Admin</span>
            </button>

            <div className="admin-user-dropdown">
              <NavLink to="/">
                <BsHouseDoor />
                <span>Về trang khách</span>
              </NavLink>

              <button type="button" onClick={handleLogout}>
                <BsBoxArrowRight />
                <span>Đăng xuất</span>
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
