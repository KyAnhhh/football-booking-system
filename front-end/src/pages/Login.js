import { useState } from "react";
import swal from "sweetalert";
import { Link, useLocation, useNavigate } from "react-router-dom";
import "../assets/styles/Login.css";
import { login } from "../services/authService";
import { jwtDecode } from "jwt-decode";

const normalizeRole = (value) =>
  String(value || "")
    .replace("ROLE_", "")
    .toUpperCase();

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogin = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const res = await login({ email, password });
      const contentType = res.headers.get("content-type");
      const data =
        contentType && contentType.includes("application/json")
          ? await res.json()
          : { message: await res.text() };

      if (res.ok) {
        sessionStorage.setItem("accessToken", data.accessToken);
        sessionStorage.setItem("refreshToken", data.refreshToken);
        const decoded = jwtDecode(data.accessToken);
        const role = normalizeRole(decoded.role);

        setTimeout(() => {
          const requestedPath = location.state?.from?.pathname;
          navigate(
            requestedPath ||
              (["ADMIN", "MANAGER", "STAFF"].includes(role) ? "/admin" : "/"),
            { replace: true }
          );
        }, 800);
      } else {
        swal({ text: data.message || "Email hoặc mật khẩu không đúng.", icon: "error" });
      }
    } catch (err) {
      console.error("Lỗi đăng nhập:", err);
      swal({ text: "Không thể đăng nhập. Vui lòng thử lại.", icon: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <section className="auth-visual">
        <p>Shop Bóng Đá</p>
        <h1>Đăng nhập để tiếp tục trận đấu</h1>
        <span>Quản lý đơn hàng, lịch đặt sân, voucher và đánh giá trong tài khoản của bạn.</span>
      </section>

      <form className="auth-card" onSubmit={handleLogin}>
        <h2>Đăng nhập</h2>
        <p className="auth-muted">Nhập email và mật khẩu để truy cập hệ thống.</p>

        <label>
          <span>Email</span>
          <input
            type="email"
            placeholder="Nhập email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </label>

        <label>
          <span>Mật khẩu</span>
          <input
            type="password"
            placeholder="Nhập mật khẩu"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? "Đang đăng nhập..." : "Đăng nhập"}
        </button>

        <div className="auth-links">
          <Link to="/forgot-password">Quên mật khẩu?</Link>
          <span>
            Chưa có tài khoản? <Link to="/register">Đăng ký</Link>
          </span>
        </div>
      </form>
    </div>
  );
}
