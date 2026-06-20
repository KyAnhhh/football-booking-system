import { useState } from "react";
import Swal from "sweetalert2";
import "../assets/styles/Forgot-Password.css";
import { forgotPassword } from "../services/authService";
import { Link } from "react-router-dom";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const res = await forgotPassword(email);
      if (res.ok) {
        Swal.fire({
          title: "Thành công",
          text: "Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn.",
          icon: "success",
        });
      } else {
        const data = await res.json();
        Swal.fire({
          title: "Lỗi",
          text: data.message || "Gửi yêu cầu thất bại. Vui lòng thử lại.",
          icon: "error",
          confirmButtonText: "OK",
        });
      }
    } catch (err) {
      console.error("Lỗi gửi yêu cầu đặt lại mật khẩu:", err);
      Swal.fire({
        title: "Lỗi",
        text: "Đã xảy ra lỗi khi gửi yêu cầu đặt lại mật khẩu.",
        icon: "error",
        confirmButtonText: "OK",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page forgot-page">
      <section className="auth-visual">
        <p>Khôi phục tài khoản</p>
        <h1>Lấy lại quyền truy cập</h1>
        <span>Nhập email đã đăng ký để nhận hướng dẫn đặt lại mật khẩu.</span>
      </section>

      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Quên mật khẩu</h2>
        <p className="auth-muted">Chúng tôi sẽ gửi liên kết đặt lại mật khẩu đến email của bạn.</p>

        <label>
          <span>Email</span>
          <input
            type="email"
            placeholder="Nhập email của bạn"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? "Đang gửi..." : "Gửi yêu cầu"}
        </button>

        <div className="auth-links">
          <Link to="/login">Quay lại đăng nhập</Link>
        </div>
      </form>
    </div>
  );
}
