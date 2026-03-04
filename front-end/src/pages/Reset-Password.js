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

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!token) {
            Swal.fire("Lỗi", "Token không hợp lệ hoặc đã hết hạn", "error");
            return;
        }

        if (newPassword.length < 6) {
            Swal.fire("Lỗi", "Mật khẩu phải ít nhất 6 ký tự", "warning");
            return;
        }

        if (newPassword !== confirmPassword) {
            Swal.fire("Lỗi", "Mật khẩu xác nhận không khớp", "warning");
            return;
        }

        setLoading(true);

        try {
            const res = await resetPassword({
                token: token,
                newPassword: newPassword
            });

            if (res.ok) {
                Swal.fire({
                    title: "Thành công",
                    text: "Mật khẩu đã được cập nhật",
                    icon: "success"
                }).then(() => {
                    navigate("/login");
                });
            } else {
                const data = await res.json();
                Swal.fire("Lỗi", data.message || "Reset thất bại", "error");
            }

        } catch (err) {
            Swal.fire("Lỗi", "Có lỗi xảy ra", "error");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="reset-wrapper">
            <div className="reset-card">
                <h2>Đặt lại mật khẩu</h2>

                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <input
                            type="password"
                            placeholder="Mật khẩu mới"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                        />
                    </div>

                    <div className="input-group">
                        <input
                            type="password"
                            placeholder="Xác nhận mật khẩu"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" disabled={loading}>
                        {loading ? "Đang xử lý..." : "Đặt lại mật khẩu"}
                    </button>
                </form>
            </div>
        </div>
    );
}