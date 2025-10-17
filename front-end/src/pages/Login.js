// src/pages/Login.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/styles/Login.css"; 
import { login } from "../services/authService";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const res = await login({ email, password });
      
      let data;
      const contentType = res.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        data = await res.json();
      } else {
        const text = await res.text();
        data = { message: text };
      }

      if (res.ok) {
        localStorage.setItem("token", data.accessToken);
        localStorage.setItem("role", data.role);

        const role = data.role?.toLowerCase().replace("role_", "");
        if (role === "admin") navigate("/admin");
        else navigate("/");
      } else {
        alert(data.message || "Đăng nhập thất bại");
      }
    } catch (err) {
      console.error("Lỗi đăng nhập:", err);
      alert("Error logging in");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form className="login-card" onSubmit={handleLogin}>
        <h2>Sign in</h2>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type="submit" disabled={loading}>
          {loading ? "Đang đăng nhập..." : "Đăng nhập"}
        </button>

        <p className="signup-text">
          Không có tài khoản? <a href="/register">Đăng ký</a>
        </p>
      </form>
    </div>
  );
}
