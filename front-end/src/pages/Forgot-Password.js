import { useState } from "react";
import Swal from "sweetalert2";
import "../assets/styles/Forgot-Password.css"; 
import { forgotPassword } from "../services/authService";
import { Navigate } from "react-router-dom";

export default function ForgotPassword() {
    const[email, setEmail] = useState("");
    const[loading, setLoading] = useState(false);
    // const navigate = Navigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const res = await forgotPassword(email);
            if(res.ok){
                Swal.fire({
                    title: "Thành công",
                    text: "Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư đến.",
                    icon: "success",
                }).then(() => {
                    // navigate("/login"); 
                });
            } else {
                const data = await res.json();
                Swal.fire({
                    title: "Lỗi",
                    text: data.message || "Gửi yêu cầu thất bại. Vui lòng thử lại.",
                    icon: "error",
                    confirmButtonText: "OK"
                })
            }
        }
        catch (err) {
            console.error("Lỗi gửi yêu cầu đặt lại mật khẩu:", err);
            Swal.fire({
                title: "Lỗi",
                text: "Đã xảy ra lỗi khi gửi yêu cầu đặt lại mật khẩu. Vui lòng thử lại sau.",
                icon: "error",
                confirmButtonText: "OK"
            });
        }
        finally {
            setLoading(false);
        }
    }
    return(
        <>
            <div className="forgot-password">
                <h2>Quên mật khẩu</h2>
                <form className="forgot-card" onSubmit={handleSubmit}>
                    <input
                        type="email"
                        placeholder="Nhập email của bạn"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <button type="submit" disabled={loading}>
                        {loading ? "Đang gửi..." : "Gửi yêu cầu"}
                    </button>
                </form>
            </div>
        </>
    );
}