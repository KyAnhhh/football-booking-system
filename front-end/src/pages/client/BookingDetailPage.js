import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import swal from "sweetalert2";
import {
  ArrowLeft,
  CalendarDays,
  Clock,
  CreditCard,
  MapPin,
  ReceiptText,
  RotateCcw,
  Star,
} from "lucide-react";
import { cancelBooking, getMyBookingDetail } from "../../services/bookingService";
import "../../assets/styles/BookingDetailPage.css";

const statusLabel = {
  PENDING: "Đang chờ",
  CONFIRMED: "Đã xác nhận",
  IN_PROGRESS: "Đang diễn ra",
  CANCELLED: "Đã hủy",
  COMPLETED: "Hoàn tất",
  EXPIRED: "Đã hết hạn",
};

const paymentStatusLabel = {
  UNPAID: "Chưa thanh toán",
  PENDING: "Chờ admin xác nhận",
  PAID: "Đã thanh toán",
  FAILED: "Thanh toán lỗi",
  REFUNDED: "Đã hoàn tiền",
};

const timelineSteps = ["PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED"];
const fallbackFieldImage =
  "https://placehold.co/900x520/f1f5f9/0f172a?text=Football+Field";

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const formatDate = (date) => new Date(date).toLocaleDateString("vi-VN");

const formatDateTime = (date) =>
  date ? new Date(date).toLocaleString("vi-VN") : "Chưa có";

const formatTime = (date) =>
  new Date(date).toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });

export default function BookingDetailPage() {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadBooking = useCallback(async () => {
    try {
      setLoading(true);
      const data = await getMyBookingDetail(bookingId);
      setBooking(data);
    } catch (error) {
      console.error(error);
      swal.fire("Lỗi", error.message || "Không thể tải chi tiết đặt sân.", "error");
    } finally {
      setLoading(false);
    }
  }, [bookingId]);

  useEffect(() => {
    loadBooking();
  }, [loadBooking]);

  const bookingTime = useMemo(() => {
    if (!booking?.startTime || !booking?.endTime) return "";
    return `${formatTime(booking.startTime)} - ${formatTime(booking.endTime)}`;
  }, [booking]);

  const canCancel = useMemo(() => {
    if (booking?.status !== "PENDING") return false;
    const diffHours = (new Date(booking.startTime) - new Date()) / (1000 * 60 * 60);
    return diffHours >= 2;
  }, [booking]);

  const canPayAgain =
    booking?.status === "PENDING"
    && booking?.paymentMethod === "VIETQR"
    && booking?.paymentStatus === "UNPAID";

  const handleCancel = async () => {
    const result = await swal.fire({
      title: "Hủy lịch đặt sân?",
      text: "Lịch đặt sân sẽ chuyển sang trạng thái đã hủy. Dịch vụ thêm có tồn kho sẽ được hoàn lại.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Hủy lịch",
      cancelButtonText: "Giữ lại",
    });

    if (!result.isConfirmed) return;

    try {
      await cancelBooking(booking.id);
      await loadBooking();
      swal.fire("Đã hủy", "Lịch đặt sân đã được hủy.", "success");
    } catch (error) {
      swal.fire("Lỗi", error.message || "Không thể hủy lịch đặt sân.", "error");
    }
  };

  if (loading) {
    return <div className="booking-detail-state">Đang tải chi tiết đặt sân...</div>;
  }

  if (!booking) {
    return <div className="booking-detail-state">Không tìm thấy lịch đặt sân.</div>;
  }

  const terminalStatus = ["CANCELLED", "EXPIRED"].includes(booking.status);

  return (
    <div className="booking-detail-page">
      <div className="booking-detail-shell">
        <button type="button" className="booking-detail-back" onClick={() => navigate("/my-bookings")}>
          <ArrowLeft size={18} />
          Quay lại
        </button>

        <section className="booking-detail-hero">
          <div className="booking-detail-media">
            <img src={booking.fieldImage || fallbackFieldImage} alt={booking.fieldName || "Sân bóng"} />
            <span className={`booking-detail-status ${booking.status?.toLowerCase()}`}>
              {statusLabel[booking.status] || booking.status}
            </span>
          </div>

          <div className="booking-detail-title">
            <span>Lịch đặt sân #{booking.id}</span>
            <h1>{booking.fieldName}</h1>
            <p>
              <MapPin size={18} />
              {booking.fieldAddress || "Chưa cập nhật địa chỉ"}
            </p>
          </div>
        </section>

        <div className="booking-detail-layout">
          <main className="booking-detail-main">
            <section className="booking-detail-panel">
              <div className="booking-detail-panel-title">
                <CalendarDays size={20} />
                <h2>Thông tin lịch</h2>
              </div>

              <div className="booking-detail-facts">
                <div>
                  <span>Ngày đá</span>
                  <strong>{formatDate(booking.startTime)}</strong>
                </div>
                <div>
                  <span>Khung giờ</span>
                  <strong>{bookingTime}</strong>
                </div>
                <div>
                  <span>Tạo lúc</span>
                  <strong>{formatDateTime(booking.createdAt)}</strong>
                </div>
                <div>
                  <span>Điện thoại</span>
                  <strong>{booking.contactPhone || "Chưa có"}</strong>
                </div>
              </div>

              {booking.note && <p className="booking-detail-note">Ghi chú: {booking.note}</p>}
            </section>

            <section className="booking-detail-panel">
              <div className="booking-detail-panel-title">
                <Clock size={20} />
                <h2>Tiến trình</h2>
              </div>

              <div className={`booking-timeline ${terminalStatus ? "terminal" : ""}`}>
                {terminalStatus ? (
                  <div className="booking-timeline-terminal">
                    {statusLabel[booking.status] || booking.status}
                  </div>
                ) : (
                  timelineSteps.map((step) => {
                    const currentIndex = timelineSteps.indexOf(booking.status);
                    const stepIndex = timelineSteps.indexOf(step);
                    const active = stepIndex <= currentIndex;

                    return (
                      <div key={step} className={`booking-timeline-step ${active ? "active" : ""}`}>
                        <i />
                        <span>{statusLabel[step]}</span>
                      </div>
                    );
                  })
                )}
              </div>
            </section>

            <section className="booking-detail-panel">
              <div className="booking-detail-panel-title">
                <ReceiptText size={20} />
                <h2>Dịch vụ thêm</h2>
              </div>

              {booking.extraServices?.length > 0 ? (
                <div className="booking-extra-detail-list">
                  {booking.extraServices.map((service) => (
                    <div key={service.serviceId}>
                      <span>{service.serviceName}</span>
                      <strong>
                        x{service.quantity} · {formatMoney(service.totalPrice)}
                      </strong>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="booking-detail-muted">Không sử dụng dịch vụ thêm.</p>
              )}
            </section>
          </main>

          <aside className="booking-detail-side">
            <section className="booking-detail-panel sticky">
              <div className="booking-detail-panel-title">
                <CreditCard size={20} />
                <h2>Thanh toán</h2>
              </div>

              <div className="booking-payment-box">
                <span>{paymentStatusLabel[booking.paymentStatus] || booking.paymentStatus || "Chưa rõ"}</span>
                <strong>{formatMoney(booking.finalPrice || booking.totalPrice)}</strong>
                <small>{booking.paymentMethod || "Chưa chọn phương thức"}</small>
              </div>

              <div className="booking-detail-breakdown">
                <div>
                  <span>Giá gốc</span>
                  <strong>{formatMoney(booking.originalPrice || booking.totalPrice)}</strong>
                </div>
                <div>
                  <span>Dịch vụ thêm</span>
                  <strong>{formatMoney(booking.extraServicePrice)}</strong>
                </div>
                <div>
                  <span>Giảm membership</span>
                  <strong>-{formatMoney(booking.membershipDiscount)}</strong>
                </div>
                <div>
                  <span>Giảm voucher {booking.voucherCode ? `(${booking.voucherCode})` : ""}</span>
                  <strong>-{formatMoney(booking.voucherDiscount)}</strong>
                </div>
                <div className="total">
                  <span>Thanh toán</span>
                  <strong>{formatMoney(booking.finalPrice || booking.totalPrice)}</strong>
                </div>
              </div>

              <div className="booking-detail-actions">
                {canPayAgain && (
                  <button type="button" onClick={() => navigate(`/football-fields/${booking.id}/payment`)}>
                    <CreditCard size={17} />
                    Thanh toán lại
                  </button>
                )}

                {canCancel && (
                  <button type="button" className="danger" onClick={handleCancel}>
                    <RotateCcw size={17} />
                    Hủy lịch
                  </button>
                )}

                {booking.status === "COMPLETED" && (
                  <button type="button" onClick={() => navigate("/my-bookings")}>
                    <Star size={17} />
                    Đánh giá sân
                  </button>
                )}
              </div>
            </section>
          </aside>
        </div>
      </div>
    </div>
  );
}
