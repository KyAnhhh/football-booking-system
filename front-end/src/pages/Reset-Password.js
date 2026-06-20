import { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { resetPassword } from "../services/authService";
import "../assets/styles/Reset-Password.css";

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get("token");

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!token) {
      Swal.fire("Lỗi", "Token không hợp lệ hoặc đã hết hạn.", "error");
      return;
    }

    if (newPassword.length < 6) {
      Swal.fire("Lỗi", "Mật khẩu phải có ít nhất 6 ký tự.", "warning");
      return;
    }

    if (newPassword !== confirmPassword) {
      Swal.fire("Lỗi", "Mật khẩu xác nhận không khớp.", "warning");
      return;
    }

    setLoading(true);

    try {
      const res = await resetPassword({
        token,
        newPassword,
      });

      if (res.ok) {
        Swal.fire({
          title: "Thành công",
          text: "Mật khẩu đã được cập nhật.",
          icon: "success",
        }).then(() => navigate("/login"));
      } else {
        const data = await res.json();
        Swal.fire("Lỗi", data.message || "Đặt lại mật khẩu thất bại.", "error");
      }
    } catch (err) {
      Swal.fire("Lỗi", "Có lỗi xảy ra. Vui lòng thử lại.", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page reset-page">
      <section className="auth-visual">
        <p>Bảo mật tài khoản</p>
        <h1>Tạo mật khẩu mới</h1>
        <span>Chọn mật khẩu đủ mạnh để bảo vệ đơn hàng và lịch đặt sân của bạn.</span>
      </section>

      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Đặt lại mật khẩu</h2>
        <p className="auth-muted">Mật khẩu mới cần có ít nhất 6 ký tự.</p>

        <label>
          <span>Mật khẩu mới</span>
          <input
            type="password"
            placeholder="Nhập mật khẩu mới"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
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
          {loading ? "Đang xử lý..." : "Đặt lại mật khẩu"}
        </button>
      </form>
    </div>
  );
}
