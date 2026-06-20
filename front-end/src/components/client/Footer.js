import { CalendarDays, Mail, MapPin, Phone, ShieldCheck, ShoppingBag } from "lucide-react";
import { Link } from "react-router-dom";
import "../../assets/styles/Footer.css";

const shopLinks = [
  { label: "Sản phẩm", to: "/products" },
  { label: "Sân bóng", to: "/football-fields" },
  { label: "Bộ sưu tập", to: "/collections" },
  { label: "Dịch vụ", to: "/service" },
];

const accountLinks = [
  { label: "Tài khoản", to: "/profile" },
  { label: "Giỏ hàng", to: "/cart" },
  { label: "Đơn hàng của tôi", to: "/my-orders" },
  { label: "Đặt sân của tôi", to: "/my-bookings" },
];

export default function Footer() {
  return (
    <footer className="fs-footer">
      <div className="fs-footer-topline" />

      <div className="fs-footer-inner">
        <section className="fs-footer-brand">
          <Link to="/" className="fs-footer-logo" aria-label="Về trang chủ Football Shop">
            <span className="fs-footer-mark">FS</span>
            <span>
              <span className="fs-footer-title">Football Shop</span>
              <span className="fs-footer-kicker">Gear & sân bóng</span>
            </span>
          </Link>
          <p className="fs-footer-desc">
            Điểm đến cho đồ bóng đá, phụ kiện thi đấu và đặt sân nhanh gọn.
            Chọn sản phẩm, đặt lịch, thanh toán rõ ràng trong cùng một trải nghiệm.
          </p>
          <div className="fs-footer-badges">
            <span>
              <ShieldCheck size={16} />
              Giá minh bạch
            </span>
            <span>
              <CalendarDays size={16} />
              Đặt sân theo slot
            </span>
          </div>
        </section>

        <section className="fs-footer-col">
          <h4>Mua sắm</h4>
          <ul>
            {shopLinks.map((link) => (
              <li key={link.to}>
                <Link to={link.to}>{link.label}</Link>
              </li>
            ))}
          </ul>
        </section>

        <section className="fs-footer-col">
          <h4>Tài khoản</h4>
          <ul>
            {accountLinks.map((link) => (
              <li key={link.to}>
                <Link to={link.to}>{link.label}</Link>
              </li>
            ))}
          </ul>
        </section>

        <section className="fs-footer-col fs-footer-contact">
          <h4>Liên hệ</h4>
          <ul>
            <li>
              <MapPin size={17} />
              TP. Hồ Chí Minh, Việt Nam
            </li>
            <li>
              <Phone size={17} />
              0900 123 456
            </li>
            <li>
              <Mail size={17} />
              support@footballshop.vn
            </li>
          </ul>
          <Link className="fs-footer-cta" to="/football-fields">
            <ShoppingBag size={18} />
            Đặt sân ngay
          </Link>
        </section>
      </div>

      <div className="fs-footer-bottom">
        <span>© {new Date().getFullYear()} Football Shop</span>
        <span>Thiết kế cho trải nghiệm mua sắm và đặt sân bóng.</span>
      </div>
    </footer>
  );
}
