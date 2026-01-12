import { useState, useEffect } from "react";
import "../assets/styles/Header.css";
import { useLocation } from "react-router-dom";

export default function Header() {
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState(null);
  const location = useLocation();

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if(token){
      setUser({ token: "user" });
    } else{
      setUser(null);
    }
  }, []);

  useEffect(() => {
    function onResize() {
      if (window.innerWidth > 768) setOpen(false);
    }
    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, []);

  const handleLogout = () => {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      setUser(null);
    };

    if(location.pathname.startsWith("/admin")){
      return null;
    }

  return (
    <header className="fs-header">
      <div className="fs-header-inner">
        <a href="/" className="fs-logo" aria-label="FootballShop Home">
          <span className="fs-logo-ball" aria-hidden>⚽</span>
          <span className="fs-logo-text">Football Shop</span>
        </a>

        <nav className={`fs-nav ${open ? "open" : ""}`} aria-label="Primary">
          <a className="fs-nav-link" href="/">Trang chủ</a>
          <a className="fs-nav-link" href="/products">Sản phẩm</a>
          <a className="fs-nav-link" href="/collections">Bộ sưu tập</a>
          <a className="fs-nav-link" href="/about">Giới thiệu</a>
          <a className="fs-nav-link" href="/contact">Liên hệ</a>
          <a className="fs-nav-link" href="/service">Dịch vụ</a>
        </nav>

        {/* Actions */}
        <div className="fs-actions">
          <a className="fs-icon-btn" href="/cart" aria-label="Cart">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden>
              <path d="M6 6h15l-1.5 9h-11L6 6z" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
              <circle cx="10" cy="20" r="1" fill="currentColor"/>
              <circle cx="18" cy="20" r="1" fill="currentColor"/>
            </svg>
          </a>

          {user ? (
            <>
              <a className="fs-icon-btn" href="/profile" aria-label="Profile">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden>
                  <path d="M20 21v-1a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v1" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </a>
              <button className="fs-login-btn logout" onClick={handleLogout}>Đăng xuất</button>
            </>
          ) : (
            <a className="fs-login-btn" href="/login">Đăng nhập</a>
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
      <div className={`fs-mobile-menu ${open ? "show" : ""}`} aria-hidden={!open}>
        <a className="fs-mobile-link" href="/" onClick={() => setOpen(false)}>Trang chủ</a>
        <a className="fs-mobile-link" href="/products" onClick={() => setOpen(false)}>Sản phẩm</a>
        <a className="fs-mobile-link" href="/collections" onClick={() => setOpen(false)}>Bộ sưu tập</a>
        <a className="fs-mobile-link" href="/about" onClick={() => setOpen(false)}>Giới thiệu</a>
        <a className="fs-mobile-link" href="/contact" onClick={() => setOpen(false)}>Liên hệ</a>
        <a className="fs-mobile-link" href="/service" onClick={() => setOpen(false)}>Dịch vụ</a>
        {!user ? (
          <a className="fs-mobile-link" href="/login" onClick={() => setOpen(false)}>Đăng nhập</a>
        ) : (
          <button className="fs-mobile-link logout" onClick={() => { handleLogout(); setOpen(false); }}>Đăng xuất</button>
        )}
      </div>
    </header>
  );
}
