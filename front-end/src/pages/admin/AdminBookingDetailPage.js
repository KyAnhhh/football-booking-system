import { useCallback, useEffect, useMemo, useState } from "react";
import { Button, Modal } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  CalendarDays,
  CheckCircle2,
  Clock,
  CreditCard,
  MapPin,
  ReceiptText,
  UserRound,
} from "lucide-react";
import swal from "sweetalert2";
import {
  getBookingDetailForAdmin,
  updateBookingStatus,
} from "../../services/bookingService";
import "../../assets/styles/AdminBookingDetailPage.css";

const statusLabels = {
  PENDING: "Chờ xác nhận",
  CONFIRMED: "Đã xác nhận",
  CANCELLED: "Đã hủy",
  COMPLETED: "Hoàn tất",
  EXPIRED: "Hết hạn",
};

const paymentStatusLabels = {
  UNPAID: "Chưa thanh toán",
  PAID: "Đã thanh toán",
  FAILED: "Thanh toán lỗi",
  REFUNDED: "Đã hoàn tiền",
};

const statusActions = {
  PENDING: [
    { status: "CONFIRMED", label: "Xác nhận", variant: "success" },
    { status: "CANCELLED", label: "Hủy lịch", variant: "warning" },
    { status: "EXPIRED", label: "Đánh dấu hết hạn", variant: "secondary" },
  ],
  CONFIRMED: [
    { status: "COMPLETED", label: "Hoàn tất", variant: "primary" },
    { status: "CANCELLED", label: "Hủy lịch", variant: "warning" },
  ],
};

const timelineSteps = ["PENDING", "CONFIRMED", "COMPLETED"];
const fallbackFieldImage =
  "https://placehold.co/1000x620/f1f5f9/0f172a?text=Football+Field";

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const formatDate = (value) =>
  value ? new Date(value).toLocaleDateString("vi-VN") : "-";

const formatDateTime = (value) =>
  value ? new Date(value).toLocaleString("vi-VN") : "-";

const formatTime = (value) =>
  value
    ? new Date(value).toLocaleTimeString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
      })
    : "-";

export default function AdminBookingDetailPage() {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [pendingAction, setPendingAction] = useState(null);
  const [saving, setSaving] = useState(false);

  const loadBooking = useCallback(async () => {
    try {
      setLoading(true);
      const data = await getBookingDetailForAdmin(bookingId);
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
    if (!booking?.startTime || !booking?.endTime) return "-";
    return `${formatTime(booking.startTime)} - ${formatTime(booking.endTime)}`;
  }, [booking]);

  const availableActions = statusActions[booking?.status] || [];
  const terminalStatus = ["CANCELLED", "EXPIRED"].includes(booking?.status);

  const handleConfirmStatus = async () => {
    if (!pendingAction || !booking) return;

    try {
      setSaving(true);
      await updateBookingStatus(booking.id, pendingAction.status);
      setPendingAction(null);
      await loadBooking();
      swal.fire("Đã cập nhật", "Trạng thái đặt sân đã được cập nhật.", "success");
    } catch (error) {
      console.error(error);
      swal.fire("Lỗi", error.message || "Không thể cập nhật trạng thái.", "error");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="admin-booking-detail-state">Đang tải chi tiết đặt sân...</div>;
  }

  if (!booking) {
    return <div className="admin-booking-detail-state">Không tìm thấy đặt sân.</div>;
  }

  return (
    <div className="admin-booking-detail-page">
      <button type="button" className="admin-booking-back" onClick={() => navigate("/admin/bookings")}>
        <ArrowLeft size={18} />
        Quay lại danh sách
      </button>

      <section className="admin-booking-hero">
        <div className="admin-booking-media">
          <img src={booking.fieldImage || fallbackFieldImage} alt={booking.fieldName || "Sân bóng"} />
          <span className={`admin-booking-status ${booking.status?.toLowerCase()}`}>
            {statusLabels[booking.status] || booking.status}
          </span>
        </div>

        <div className="admin-booking-heading">
          <span>Booking #{booking.id}</span>
          <h1>{booking.fieldName}</h1>
          <p>
            <MapPin size={18} />
            {booking.fieldAddress || "Chưa cập nhật địa chỉ"}
          </p>
        </div>
      </section>

      <div className="admin-booking-layout">
        <main className="admin-booking-main">
          <section className="admin-booking-panel">
            <div className="admin-booking-panel-title">
              <CalendarDays size={20} />
              <h2>Thông tin lịch</h2>
            </div>

            <div className="admin-booking-facts">
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
                <span>Điện thoại liên hệ</span>
                <strong>{booking.contactPhone || "Chưa có"}</strong>
              </div>
            </div>

            {booking.note && <p className="admin-booking-note">Ghi chú: {booking.note}</p>}
          </section>

          <section className="admin-booking-panel">
            <div className="admin-booking-panel-title">
              <UserRound size={20} />
              <h2>Người đặt</h2>
            </div>

            <div className="admin-booking-customer">
              <strong>{booking.userName || "Khách hàng"}</strong>
              <span>Mã người dùng: {booking.userId || "-"}</span>
            </div>
          </section>

          <section className="admin-booking-panel">
            <div className="admin-booking-panel-title">
              <Clock size={20} />
              <h2>Timeline trạng thái</h2>
            </div>

            <div className={`admin-booking-timeline ${terminalStatus ? "terminal" : ""}`}>
              {terminalStatus ? (
                <div className="admin-booking-terminal">
                  {statusLabels[booking.status] || booking.status}
                </div>
              ) : (
                timelineSteps.map((step) => {
                  const currentIndex = timelineSteps.indexOf(booking.status);
                  const stepIndex = timelineSteps.indexOf(step);
                  const active = stepIndex <= currentIndex;

                  return (
                    <div key={step} className={`admin-booking-timeline-step ${active ? "active" : ""}`}>
                      <CheckCircle2 size={18} />
                      <span>{statusLabels[step]}</span>
                    </div>
                  );
                })
              )}
            </div>
          </section>

          <section className="admin-booking-panel">
            <div className="admin-booking-panel-title">
              <ReceiptText size={20} />
              <h2>Dịch vụ thêm</h2>
            </div>

            {booking.extraServices?.length > 0 ? (
              <div className="admin-booking-extra-list">
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
              <p className="admin-booking-muted">Booking này không dùng dịch vụ thêm.</p>
            )}
          </section>
        </main>

        <aside className="admin-booking-side">
          <section className="admin-booking-panel sticky">
            <div className="admin-booking-panel-title">
              <CreditCard size={20} />
              <h2>Thanh toán</h2>
            </div>

            <div className="admin-booking-payment-box">
              <span>{paymentStatusLabels[booking.paymentStatus] || booking.paymentStatus || "Chưa rõ"}</span>
              <strong>{formatMoney(booking.finalPrice || booking.totalPrice)}</strong>
              <small>{booking.paymentMethod || "Chưa chọn phương thức"}</small>
            </div>

            <div className="admin-booking-breakdown">
              <div>
                <span>Giá sân</span>
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
                <span>Khách cần thanh toán</span>
                <strong>{formatMoney(booking.finalPrice || booking.totalPrice)}</strong>
              </div>
            </div>

            {booking.transactionCode && (
              <div className="admin-booking-transaction">
                <span>Mã giao dịch</span>
                <strong>{booking.transactionCode}</strong>
                <small>{formatDateTime(booking.paymentDate)}</small>
              </div>
            )}

            <div className="admin-booking-actions">
              {availableActions.length > 0 ? (
                availableActions.map((action) => (
                  <Button
                    key={action.status}
                    variant={action.variant}
                    onClick={() => setPendingAction(action)}
                  >
                    {action.label}
                  </Button>
                ))
              ) : (
                <p>Không còn thao tác trạng thái cho booking này.</p>
              )}
            </div>
          </section>
        </aside>
      </div>

      <Modal show={!!pendingAction} onHide={() => setPendingAction(null)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận đổi trạng thái</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Chuyển booking <strong>#{booking.id}</strong> từ{" "}
          <strong>{statusLabels[booking.status] || booking.status}</strong> sang{" "}
          <strong>{statusLabels[pendingAction?.status] || pendingAction?.status}</strong>?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setPendingAction(null)} disabled={saving}>
            Giữ lại
          </Button>
          <Button variant={pendingAction?.variant || "primary"} onClick={handleConfirmStatus} disabled={saving}>
            {saving ? "Đang cập nhật..." : "Xác nhận"}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
