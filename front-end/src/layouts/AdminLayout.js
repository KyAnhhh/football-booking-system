import { useMemo, useState } from "react";
import { Outlet, NavLink } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
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
  BsCalendar2Plus,
  BsUpload,
} from "react-icons/bs";

import "../assets/styles/AdminLayout.css";
import "../assets/styles/AdminManager.css";
import NotificationBell from "../components/notification/NotificationBell";

const normalizeRole = (value) =>
  String(value || "")
    .replace("ROLE_", "")
    .toUpperCase();

const navItems = [
  { to: "/admin/imports", label: "Import dữ liệu", icon: BsUpload, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/inventory", label: "Kho", icon: BsBoxSeam, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin", label: "Tổng quan", icon: BsSpeedometer2, end: true, roles: ["ADMIN", "MANAGER", "STAFF"] },
  { to: "/admin/bookings", label: "Đặt sân", icon: BsReceipt, roles: ["ADMIN", "MANAGER", "STAFF"] },
  { to: "/admin/field-calendar", label: "Lịch sân", icon: BsCalendarWeek, roles: ["ADMIN", "MANAGER", "STAFF"] },
  { to: "/admin/football-fields", label: "Sân bóng", icon: BsHouseDoor, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/pricing-rules", label: "Bảng giá", icon: BsCashCoin, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/holiday-pricing-rules", label: "Giá ngày lễ", icon: BsCalendar2Plus, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/maintenance", label: "Bảo trì", icon: BsTools, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/vouchers", label: "Voucher", icon: BsTicketPerforated, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/extra-services", label: "Dịch vụ thêm", icon: BsPlusSquare, roles: ["ADMIN", "MANAGER", "STAFF"] },
  { to: "/admin/holidays", label: "Ngày lễ", icon: BsCalendarEvent, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/products", label: "Sản phẩm", icon: BsBoxSeam, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/brands", label: "Thương hiệu", icon: BsTags, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/categories", label: "Danh mục", icon: BsGrid, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/sizes", label: "Kích cỡ", icon: BsRulers, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/orders", label: "Đơn hàng", icon: BsReceipt, roles: ["ADMIN", "MANAGER", "STAFF"] },
  { to: "/admin/payments", label: "Thanh toán", icon: BsReceipt, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/users", label: "Người dùng", icon: BsPeople, roles: ["ADMIN", "MANAGER"] },
  { to: "/admin/membership-tiers", label: "Hạng thành viên", icon: BsPeople, roles: ["ADMIN"] },
];

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const currentRole = useMemo(() => {
    const token = sessionStorage.getItem("accessToken");
    if (!token) return "";

    try {
      return normalizeRole(jwtDecode(token).role);
    } catch {
      return "";
    }
  }, []);

  const visibleNavItems = useMemo(
    () => navItems.filter((item) => item.roles.includes(currentRole)),
    [currentRole]
  );

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
            onClick={() => setCollapsed((current) => !current)}
          >
            <BsList />
          </button>

          {!collapsed && <h2 className="logo">Football Admin</h2>}
        </div>

        <nav className="admin-nav" aria-label="Điều hướng quản trị">
          <ul>
            {visibleNavItems.map((item) => {
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
              onClick={() => setCollapsed((current) => !current)}
            >
              <BsList />
            </button>
            <h3>Bảng điều khiển</h3>
          </div>

          <div className="admin-navbar-actions">
            <NotificationBell context="admin" />

            <div className={`admin-user-menu ${userMenuOpen ? "show" : ""}`}>
              <button
                className="admin-user-trigger"
                type="button"
                aria-expanded={userMenuOpen}
                onClick={() => setUserMenuOpen((current) => !current)}
              >
                <BsPersonCircle />
                <span>{currentRole || "Admin"}</span>
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
          </div>
        </header>

        <div className="content">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
