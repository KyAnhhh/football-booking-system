import { useState } from "react";
import swal from 'sweetalert';
import { useNavigate } from "react-router-dom";
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

  const handleRegister = async (e) => {
    e.preventDefault();

    if (username.length < 4) {
      swal({ text: "Tên người dùng phải có ít nhất 4 ký tự", icon: "warning" });
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      swal({ text: "Vui lòng nhập email hợp lệ", icon: "warning" });
      return;
    }
    if(phone.length < 10 && phone.length > 10){
      swal({ text: "Phone phải là 10 số", icon: "warning" });
      return;
    }

    if (password.length < 6) {
      swal({ text: "Mật khẩu phải có ít nhất 6 ký tự", icon: "warning" });
      return;
    }

    if (password !== confirmPassword) {
      swal({ text: "Mật khẩu không khớp! Vui lòng kiểm tra lại.", icon: "warning" });
      return;
    }

    setLoading(true);
    try {
      const res = await register({ username, email, phone, password });
      const data = await res.json();

      if (res.ok) {
        swal({ text: "Đăng ký thành công! Đang chuyển hướng...", icon: "success" });

        setTimeout(() => navigate("/login"), 1000);
      } else {
        if (data.message?.includes("Email")) {
          swal({ text: "Email đã tồn tại. Vui lòng sử dụng email khác.", icon: "error" });
        } else {
          swal({ text: data.message || "Đăng ký thất bại.", icon: "error" });
        }
      }
    } catch (err) {
      console.error("Lỗi đăng ký:", err);
      swal({ text: "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại sau.", icon: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <form className="register-card" onSubmit={handleRegister}>
        <h2>Đăng ký tài khoản</h2>

        <input
          type="text"
          placeholder="Họ và tên"
          value={username}
          onChange={(e) => setUserName(e.target.value)}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Phone"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Mật khẩu"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Xác nhận mật khẩu"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />

        <button type="submit" disabled={loading}>
          {loading ? "Đang đăng ký..." : "Đăng ký"}
        </button>

        <p className="register-text">
          Đã có tài khoản? <a href="/login">Đăng nhập</a>
        </p>
      </form>
    </div>
  );
}
