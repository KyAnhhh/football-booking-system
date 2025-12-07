import { useState } from "react";
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
      alert("Tên người dùng phải có ít nhất 4 ký tự");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      alert("Vui lòng nhập email hợp lệ");
      return;
    }
    if(phone.length < 10 && phone.length > 10){
      alert("Phone phải là 10 số");
      return;
    }

    if (password.length < 6) {
      alert("Mật khẩu phải có ít nhất 6 ký tự");
      return;
    }

    if (password !== confirmPassword) {
      alert("Mật khẩu không khớp! Vui lòng kiểm tra lại.");
      return;
    }

    setLoading(true);
    try {
      const res = await register({ username, email, phone, password });
      const data = await res.json();

      if (res.ok) {
        alert("Đăng ký thành công! Đang chuyển hướng...");

        setTimeout(() => navigate("/login"), 1000);
      } else {
        if (data.message?.includes("Email")) {
          alert("Email đã tồn tại. Vui lòng sử dụng email khác.");
        } else {
          alert(data.message || "Đăng ký thất bại.");
        }
        alert("error");
      }
    } catch (err) {
      console.error("Lỗi đăng ký:", err);
      alert("Có lỗi xảy ra khi đăng ký. Vui lòng thử lại sau.");
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
