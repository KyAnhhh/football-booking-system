import { useState } from "react";
import swal from 'sweetalert';
import { useNavigate } from "react-router-dom";
import "../assets/styles/Login.css"; 
import { login } from "../services/authService";
import { jwtDecode } from "jwt-decode";

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
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);
        const decode = jwtDecode(data.accessToken);
        
        if (decode.role && decode.role.includes("ADMIN")){
          setTimeout(() => {
            navigate("/admin");
          }, 2000);
        } else{
          setTimeout(() => {
            navigate("/");
          }, 2000);
        }
      } else {
        swal({ text: "Email hoặc mật khẩu sai. Hãy nhập lại!" || "Đăng nhập thất bại", icon: "error" });
      }
    } catch (err) {
      console.error("Lỗi đăng nhập:", err);
      swal({ text: "Lỗi đăng nhập", icon: "error" });
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
