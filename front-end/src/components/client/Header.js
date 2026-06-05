import { useState, useEffect, useRef } from "react";
import "../assets/styles/Header.css";
import { useLocation, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { Link } from "react-router-dom";
import { logout } from "../../services/authService";

export default function Header() {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState(null);

  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const userMenuRef = useRef(null);


  const location = useLocation();

  useEffect(() => {
    const token = sessionStorage.getItem("accessToken");

    if (token) {
      try {
        const decode = jwtDecode(token);

        if (decode.exp * 1000 < Date.now()) {
          sessionStorage.removeItem("accessToken");
          sessionStorage.removeItem("refreshToken");
          setUser(null);
          return;
        }

        setUser({
          id: decode.id,
          username: decode.sub,
          role: decode.role.replace("ROLE_", "")
        });
      } catch (e) {
        setUser(null);
      }
    } else {
      setUser(null);
    }
  }, [location]);

  useEffect(() => {
  function handleClickOutside(e) {
    if (
      userMenuRef.current &&
      !userMenuRef.current.contains(e.target)
    ) {
      setUserMenuOpen(false);
    }
  }

  document.addEventListener("mousedown", handleClickOutside);
  return () => document.removeEventListener("mousedown", handleClickOutside);
}, []);


  useEffect(() => {
    function onResize() {
      if (window.innerWidth > 768) setOpen(false);
    }
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, []);

  const handleLogout = async () => {
    
    try {
      await logout();
    } catch (error) {
      console.error("Logout failed:", error);
    } finally {
      sessionStorage.removeItem("accessToken");
      sessionStorage.removeItem("refreshToken");
      setUser(null);
      navigate("/login");
      setUserMenuOpen(false);
      setOpen(false);
    }
  };

  if (location.pathname === "/admin" || location.pathname.startsWith("/admin/")) {
    return null;
  }

  return (
    <header className="fs-header">
      <div className="fs-header-inner">
        <Link to="/" className="fs-logo" aria-label="FootballShop Home">
          <span className="fs-logo-ball">⚽</span>
          <span className="fs-logo-text">Football Shop</span>
        </Link>

        <nav className={`fs-nav ${open ? "open" : ""}`}>
          <Link className="fs-nav-link" to="/">Trang chủ</Link>
          <Link className="fs-nav-link" to="/about">Giới thiệu</Link>
          <Link className="fs-nav-link" to="/products">Sản phẩm</Link>
          <Link className="fs-nav-link" to="/football-fields">Sân bóng</Link>
          <Link className="fs-nav-link" to="/contact">Liên hệ</Link>
          <Link className="fs-nav-link" to="/service">Dịch vụ</Link>
        </nav>

        {/* Actions */}
        <div className="fs-actions">
          <Link className="fs-icon-btn fs-cart-btn" to="/cart" aria-label="Cart">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden>
              <path d="M6 6h15l-1.5 9h-11L6 6z" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
              <circle cx="10" cy="20" r="1" fill="currentColor"/>
              <circle cx="18" cy="20" r="1" fill="currentColor"/>
            </svg>
          </Link>

          {user ? (
            <>
              <div className="fs-user-menu" ref={userMenuRef}>
                <button
                  className="fs-icon-btn"
                  onClick={() => setUserMenuOpen(!userMenuOpen)}
                  aria-label="User menu"
                >
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                    <path
                      d="M20 21v-1a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v1"
                      stroke="currentColor"
                      strokeWidth="1.6"
                    />
                    <circle
                      cx="12"
                      cy="7"
                      r="4"
                      stroke="currentColor"
                      strokeWidth="1.6"
                    />
                  </svg>
                </button>

                <div className={`fs-user-dropdown ${userMenuOpen ? "show" : ""}`}>
                  <Link to="/profile">Thông tin cá nhân</Link>
                  <Link to="/my-orders">Đơn hàng của tôi</Link>
                  <Link to="/my-bookings">Đặt sân của tôi</Link>

                  {user?.role === "ADMIN" && (
                    <Link to="/admin" className="admin-link">
                      🔧 Quản trị hệ thống
                    </Link>
                  )}

                  <button onClick={handleLogout}>Đăng xuất</button>
                </div>
              </div>


            </>
          ) : (
            <Link className="fs-login-btn" to="/login">Đăng nhập</Link>
          )}

          {/* Hamburger button */}
          <button
            className="fs-menu-toggle"
            aria-label={open ? "Close menu" : "Open menu"}
            aria-expanded={open}
            onClick={() => setOpen(!open)}
          >
            {open ? (
              <svg width="24" height="24" viewBox="0 0 24 24" aria-hidden>
                <path d="M6 6l12 12M6 18L18 6" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            ) : (
              <svg width="24" height="24" viewBox="0 0 24 24" aria-hidden>
                <path d="M3 6h18M3 12h18M3 18h18" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            )}
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      <div className={`fs-mobile-menu ${open ? "show" : ""}`}>
        <Link to="/" onClick={() => setOpen(false)}>Trang chủ</Link>
        <Link to="/products" onClick={() => setOpen(false)}>Sản phẩm</Link>
        <Link to="/football-fields" onClick={() => setOpen(false)}>Sân bóng</Link>
        <Link to="/about" onClick={() => setOpen(false)}>Giới thiệu</Link>
        <Link to="/contact" onClick={() => setOpen(false)}>Liên hệ</Link>
        <Link to="/service" onClick={() => setOpen(false)}>Dịch vụ</Link>

        {!user ? (
          <Link to="/login" onClick={() => setOpen(false)}>Đăng nhập</Link>
        ) : (
          <button onClick={async () => { await handleLogout(); setOpen(false); }}>
            Đăng xuất
          </button>
        )}
      </div>
    </header>
  );
}
