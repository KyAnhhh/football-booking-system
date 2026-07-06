import { useEffect, useRef, useState } from "react";
import {
  CalendarDays,
  ChevronDown,
  Heart,
  LayoutDashboard,
  LogIn,
  LogOut,
  Menu,
  PackageCheck,
  ShoppingBag,
  ShoppingCart,
  UserRound,
  X,
} from "lucide-react";
import { jwtDecode } from "jwt-decode";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";

import "../../assets/styles/Header.css";
import { logout } from "../../services/authService";
import NotificationBell from "../notification/NotificationBell";
import { useCart } from "../../contexts/CartContext";

const NAV_ITEMS = [
  { label: "Trang chủ", to: "/" },
  { label: "Sản phẩm", to: "/products" },
  { label: "Sân bóng", to: "/football-fields" },
  { label: "Bộ sưu tập", to: "/collections" },
  { label: "Dịch vụ", to: "/service" },
  { label: "Liên hệ", to: "/contact" },
];

const ADMIN_ROLES = ["ADMIN", "MANAGER", "STAFF"];

export default function Header() {
  const navigate = useNavigate();
  const location = useLocation();
  const userMenuRef = useRef(null);
  const { cartCount, refreshCartCount, clearCartCount } = useCart();

  const [open, setOpen] = useState(false);
  const [user, setUser] = useState(null);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  useEffect(() => {
    const token = sessionStorage.getItem("accessToken");

    if (!token) {
      setUser(null);
      clearCartCount();
      return;
    }

    try {
      const decoded = jwtDecode(token);

      if (decoded.exp * 1000 < Date.now()) {
        sessionStorage.removeItem("accessToken");
        sessionStorage.removeItem("refreshToken");
        setUser(null);
        clearCartCount();
        return;
      }

      setUser({
        id: decoded.id,
        username: decoded.sub,
        role: decoded.role?.replace("ROLE_", ""),
      });
    } catch (error) {
      setUser(null);
      clearCartCount();
    }
  }, [location.pathname, clearCartCount]);

  useEffect(() => {
    if (user) {
      refreshCartCount();
      return;
    }

    clearCartCount();
  }, [user, location.pathname, refreshCartCount, clearCartCount]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setUserMenuOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    function onResize() {
      if (window.innerWidth > 900) {
        setOpen(false);
      }
    }

    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, []);

  useEffect(() => {
    setOpen(false);
    setUserMenuOpen(false);
  }, [location.pathname]);

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      sessionStorage.removeItem("accessToken");
      sessionStorage.removeItem("refreshToken");
      clearCartCount();
      setUser(null);
      setUserMenuOpen(false);
      setOpen(false);
      navigate("/login");
    }
  };

  if (location.pathname === "/admin" || location.pathname.startsWith("/admin/")) {
    return null;
  }

  const canAccessAdmin = ADMIN_ROLES.includes(user?.role);
  const displayCartCount = cartCount > 99 ? "99+" : cartCount;

  return (
    <header className="fs-header">
      <div className="fs-header-inner">
        <Link to="/" className="fs-logo" aria-label="Về trang chủ Football Shop">
          <span className="fs-logo-mark">FS</span>
          <span className="fs-logo-copy">
            <span className="fs-logo-text">Football Shop</span>
            <span className="fs-logo-subtext">Gear & sân bóng</span>
          </span>
        </Link>

        <nav className="fs-nav" aria-label="Điều hướng chính">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              className={({ isActive }) =>
                `fs-nav-link ${isActive ? "is-active" : ""}`
              }
              to={item.to}
              end={item.to === "/"}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="fs-actions">
          <Link className="fs-icon-btn fs-cart-btn" to="/cart" aria-label="Giỏ hàng">
            <ShoppingCart size={20} strokeWidth={2} />
            {cartCount > 0 && (
              <span className="fs-cart-badge" aria-label={`${cartCount} sản phẩm trong giỏ`}>
                {displayCartCount}
              </span>
            )}
          </Link>

          {user ? (
            <>
              <NotificationBell context={canAccessAdmin ? "admin" : "client"} />
              <div className="fs-user-menu" ref={userMenuRef}>
                <button
                  className="fs-user-trigger"
                  type="button"
                  onClick={() => setUserMenuOpen((current) => !current)}
                  aria-label="Mở menu tài khoản"
                  aria-expanded={userMenuOpen}
                >
                  <span className="fs-user-avatar">
                    <UserRound size={18} strokeWidth={2} />
                  </span>
                  <span className="fs-user-name">{user.username}</span>
                  <ChevronDown size={16} strokeWidth={2} />
                </button>

                <div className={`fs-user-dropdown ${userMenuOpen ? "show" : ""}`}>
                  <Link to="/profile">
                    <UserRound size={17} />
                    Thông tin cá nhân
                  </Link>
                  <Link to="/my-orders">
                    <PackageCheck size={17} />
                    Đơn hàng của tôi
                  </Link>
                  <Link to="/my-bookings">
                    <CalendarDays size={17} />
                    Đặt sân của tôi
                  </Link>
                  <Link to="/wishlist">
                    <Heart size={17} />
                    Sản phẩm yêu thích
                  </Link>

                  {canAccessAdmin && (
                    <Link to="/admin" className="admin-link">
                      <LayoutDashboard size={17} />
                      Quản trị hệ thống
                    </Link>
                  )}

                  <button type="button" onClick={handleLogout}>
                    <LogOut size={17} />
                    Đăng xuất
                  </button>
                </div>
              </div>
            </>
          ) : (
            <Link className="fs-login-btn" to="/login">
              <LogIn size={18} />
              Đăng nhập
            </Link>
          )}

          <button
            className="fs-menu-toggle"
            type="button"
            aria-label={open ? "Đóng menu" : "Mở menu"}
            aria-expanded={open}
            onClick={() => setOpen((current) => !current)}
          >
            {open ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      <div className={`fs-mobile-menu ${open ? "show" : ""}`}>
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === "/"}
            className={({ isActive }) =>
              `fs-mobile-link ${isActive ? "is-active" : ""}`
            }
          >
            {item.label}
          </NavLink>
        ))}

        <div className="fs-mobile-divider" />

        <Link className="fs-mobile-link" to="/cart">
          <ShoppingBag size={18} />
          Giỏ hàng
          {cartCount > 0 && (
            <span className="fs-mobile-cart-count">{displayCartCount}</span>
          )}
        </Link>

        {user ? (
          <>
            <Link className="fs-mobile-link" to="/profile">
              <UserRound size={18} />
              Tài khoản
            </Link>
            <Link className="fs-mobile-link" to="/my-bookings">
              <CalendarDays size={18} />
              Đặt sân của tôi
            </Link>
            <Link className="fs-mobile-link" to="/wishlist">
              <Heart size={18} />
              Sản phẩm yêu thích
            </Link>
            {canAccessAdmin && (
              <Link className="fs-mobile-link" to="/admin">
                <LayoutDashboard size={18} />
                Quản trị hệ thống
              </Link>
            )}
            <button
              className="fs-mobile-link fs-mobile-logout"
              type="button"
              onClick={handleLogout}
            >
              <LogOut size={18} />
              Đăng xuất
            </button>
          </>
        ) : (
          <Link className="fs-mobile-link fs-mobile-login" to="/login">
            <LogIn size={18} />
            Đăng nhập
          </Link>
        )}
      </div>
    </header>
  );
}
