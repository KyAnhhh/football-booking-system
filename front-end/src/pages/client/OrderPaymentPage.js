import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { CheckCircle2, Package, QrCode } from "lucide-react";
import swal from "sweetalert";

import {
  getOrderById,
  markOrderPaymentSubmitted,
} from "../../services/orderService";
import { getQrCode } from "../../services/qrCodeService";
import "../../assets/styles/PaymentFootballField.css";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

export default function OrderPaymentPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [qrCodeUrl, setQrCodeUrl] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const loadOrder = async () => {
      try {
        const data = await getOrderById(id);
        setOrder(data);

        if (data.orderStatus === "PENDING"
          && data.paymentMethod === "VIETQR"
          && data.paymentStatus !== "PAID") {
          setQrCodeUrl(await getQrCode(id));
        }
      } catch (error) {
        await swal(
          "Không thể tải thanh toán",
          error.message || "Đơn hàng không tồn tại hoặc không thể thanh toán.",
          "error"
        );
        navigate("/my-orders");
      } finally {
        setLoading(false);
      }
    };

    loadOrder();
  }, [id, navigate]);

  const canSubmit =
    order?.orderStatus === "PENDING"
    && order?.paymentMethod === "VIETQR"
    && order?.paymentStatus === "UNPAID";

  const submitted =
    order?.orderStatus === "PENDING"
    && order?.paymentMethod === "VIETQR"
    && order?.paymentStatus === "PENDING";

  const handlePaymentSubmitted = async () => {
    try {
      setSubmitting(true);
      const updated = await markOrderPaymentSubmitted(order.id);
      setOrder(updated);
      await swal(
        "Đã gửi yêu cầu",
        "Admin sẽ kiểm tra giao dịch VietQR của bạn.",
        "success"
      );
      navigate("/my-orders");
    } catch (error) {
      swal(
        "Không thể ghi nhận",
        error.message || "Vui lòng thử lại.",
        "error"
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="booking-payment-state">Đang tải đơn hàng...</div>;
  }

  if (!order) return null;

  return (
    <div className="booking-payment-page">
      <section className="booking-payment-shell">
        <div className="booking-payment-hero">
          <p>Thanh toán đơn hàng</p>
          <h1>Đơn hàng #{order.id}</h1>
          <span>Quét đúng mã QR và gửi yêu cầu để admin xác nhận.</span>
        </div>

        <div className="booking-payment-grid">
          <article className="booking-payment-card">
            <div className="booking-payment-card-header">
              <div>
                <span>Đơn hàng</span>
                <h2>#{order.id}</h2>
              </div>
              <Package size={24} />
            </div>

            <div className="booking-payment-total">
              <span>Tổng thanh toán</span>
              <strong>{formatCurrency(order.totalAmount)}</strong>
            </div>

            {canSubmit && (
              <button
                type="button"
                className="booking-payment-confirm"
                onClick={handlePaymentSubmitted}
                disabled={submitting}
              >
                <CheckCircle2 size={19} />
                {submitting ? "Đang gửi..." : "Tôi đã thanh toán"}
              </button>
            )}

            {submitted && (
              <div className="booking-payment-state">
                Đã gửi yêu cầu. Vui lòng chờ admin xác nhận.
              </div>
            )}

            {!canSubmit && !submitted && (
              <div className="booking-payment-state">
                Đơn hàng này hiện không thể thanh toán bằng VietQR.
              </div>
            )}
          </article>

          <aside className="booking-payment-qr-card">
            <div className="booking-payment-qr-title">
              <QrCode size={20} />
              <div>
                <h2>Mã QR</h2>
                <p>Chuyển khoản đúng nội dung và số tiền.</p>
              </div>
            </div>

            {qrCodeUrl ? (
              <img src={qrCodeUrl} alt={`QR thanh toán đơn hàng ${order.id}`} />
            ) : (
              <div className="booking-payment-qr-loading">
                Không có mã QR cho đơn hàng này.
              </div>
            )}
          </aside>
        </div>
      </section>
    </div>
  );
}
