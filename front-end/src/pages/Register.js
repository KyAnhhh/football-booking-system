import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../assets/styles/Register.css"; 

export default function Register() {
  const [username, setUserName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      alert("Mật khẩu không khớp!");
      return;
    }

    setLoading(true);
    try {
      const res = await fetch("http://localhost:8080/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          username, 
          email, 
          password
        }),
      });

      const data = await res.json();

      if (res.ok) {
        alert("Đăng ký thành công!");
        navigate("/login");
      } else {
        alert(data.message || "Đăng ký thất bại");
      }
    } catch (err) {
      console.error("Register error:", err);
      alert("Có lỗi xảy ra khi đăng ký");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <form className="register-card" onSubmit={handleRegister}>
        <h2>Register</h2>
        <input
          type="text"
          placeholder="Full Name"
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
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />

        <button type="submit" disabled={loading}>
          {loading ? "Đang đăng ký..." : "Đăng ký"}
        </button>

        <p className="register-text">
          Already have an account? <a href="/login">Login</a>
        </p>
      </form>
    </div>
  );
}
