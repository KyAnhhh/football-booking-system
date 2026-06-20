import { useState } from "react";
import swal from "sweetalert";
import { Link, useNavigate } from "react-router-dom";
import "../assets/styles/Register.css";
import { register } from "../services/authService";

export default function Register() {
  const [username, setUserName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async (event) => {
    event.preventDefault();

    if (username.trim().length < 4) {
      swal({ text: "Tên người dùng phải có ít nhất 4 ký tự.", icon: "warning" });
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      swal({ text: "Vui lòng nhập email hợp lệ.", icon: "warning" });
      return;
    }

    if (phone.trim().length !== 10) {
      swal({ text: "Số điện thoại phải có 10 số.", icon: "warning" });
      return;
    }

    if (password.length < 6) {
      swal({ text: "Mật khẩu phải có ít nhất 6 ký tự.", icon: "warning" });
      return;
    }

    if (password !== confirmPassword) {
      swal({ text: "Mật khẩu xác nhận không khớp.", icon: "warning" });
      return;
    }

    setLoading(true);
    try {
      const res = await register({ username, email, phone, password });
      const data = await res.json();

      if (res.ok) {
        swal({ text: "Đăng ký thành công. Đang chuyển hướng...", icon: "success" });
        setTimeout(() => navigate("/login"), 1000);
      } else if (data.message?.includes("Email")) {
        swal({ text: "Email đã tồn tại. Vui lòng sử dụng email khác.", icon: "error" });
      } else {
        swal({ text: data.message || "Đăng ký thất bại.", icon: "error" });
      }
    } catch (err) {
      console.error("Lỗi đăng ký:", err);
      swal({ text: "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại sau.", icon: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page register-page">
      <section className="auth-visual">
        <p>Tạo tài khoản</p>
        <h1>Bắt đầu mua sắm và đặt sân nhanh hơn</h1>
        <span>Lưu thông tin cá nhân, theo dõi đơn hàng và quản lý lịch đặt sân dễ dàng.</span>
      </section>

      <form className="auth-card" onSubmit={handleRegister}>
        <h2>Đăng ký tài khoản</h2>
        <p className="auth-muted">Điền thông tin bên dưới để tạo tài khoản mới.</p>

        <label>
          <span>Họ và tên</span>
          <input
            type="text"
            placeholder="Nhập họ và tên"
            value={username}
            onChange={(event) => setUserName(event.target.value)}
            required
          />
        </label>

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
          <span>Số điện thoại</span>
          <input
            type="tel"
            placeholder="Nhập số điện thoại"
            value={phone}
            onChange={(event) => setPhone(event.target.value)}
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

        <label>
          <span>Xác nhận mật khẩu</span>
          <input
            type="password"
            placeholder="Nhập lại mật khẩu"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
            required
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? "Đang đăng ký..." : "Đăng ký"}
        </button>

        <div className="auth-links">
          <span>
            Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
          </span>
        </div>
      </form>
    </div>
  );
}
