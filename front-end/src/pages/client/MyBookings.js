import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import swal from "sweetalert2";
import { cancelBooking, getMyBookings } from "../../services/bookingService";
import { getMyReviews } from "../../services/bookingReviewService";
import "../../assets/styles/MyBookings.css";
import { CalendarDays, Clock, MapPin, Star } from "lucide-react";

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const statusLabel = {
  PENDING: "Đang chờ",
  CONFIRMED: "Đã xác nhận",
  CANCELLED: "Đã hủy",
  COMPLETED: "Hoàn tất",
  EXPIRED: "Đã hết hạn",
};

export default function MyBookings() {
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);
  const [myReviews, setMyReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      setLoading(true);
      const data = await getMyBookings();
      setBookings(data || []);
    } catch (err) {
      console.error(err);
      swal.fire("Lỗi", "Không thể tải danh sách đặt sân.", "error");
    } finally {
      setLoading(false);
    }
  };

  const loadMyReviews = async () => {
    try {
      const data = await getMyReviews();
      setMyReviews(data || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    load();
    loadMyReviews();
  }, []);

  const hasReviewed = (bookingId) =>
    myReviews.some(
      (review) => review.bookingId === bookingId || review.booking?.id === bookingId
    );

  const handleCancel = async (id) => {
    const result = await swal.fire({
      title: "Hủy lịch đặt sân?",
      text: "Lịch đặt sân sẽ được chuyển sang trạng thái đã hủy.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Hủy lịch",
      cancelButtonText: "Giữ lại",
    });

    if (!result.isConfirmed) return;

    try {
      await cancelBooking(id);
      await load();
    } catch (err) {
      console.error(err);
      swal.fire("Lỗi", "Không thể hủy lịch đặt sân.", "error");
    }
  };

  const formatDate = (date) => new Date(date).toLocaleDateString("vi-VN");
  const formatTime = (date) =>
    new Date(date).toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });

  const canCancelBooking = (booking) => {
    if (booking.status !== "PENDING") return false;
    const diffHours = (new Date(booking.startTime) - new Date()) / (1000 * 60 * 60);
    return diffHours >= 2;
  };

  return (
    <div className="my-bookings-page">
      <section className="my-bookings-hero">
        <div>
          <p>Lịch đặt sân</p>
          <h1>Đặt sân của tôi</h1>
          <span>Theo dõi lịch chơi, giá thanh toán, dịch vụ thêm và đánh giá sau trận.</span>
        </div>
      </section>

      {loading ? (
        <div className="my-bookings-state">Đang tải lịch đặt sân...</div>
      ) : bookings.length === 0 ? (
        <div className="my-bookings-state">Bạn chưa có lịch đặt sân nào.</div>
      ) : (
        <section className="my-bookings-grid">
          {bookings.map((booking) => (
            <article key={booking.id} className="my-booking-card">
              <div className="my-booking-media">
                <img
                  src={
                    booking.fieldImage ||
                    "https://placehold.co/900x520/f1f5f9/0f172a?text=Football+Field"
                  }
                  alt={booking.fieldName || "Sân bóng"}
                />
                <span className={`my-booking-status ${booking.status?.toLowerCase()}`}>
                  {statusLabel[booking.status] || booking.status}
                </span>
              </div>

              <div className="my-booking-content">
                <h2>{booking.fieldName}</h2>
                <p className="my-booking-address">
                  <MapPin size={16} />
                  {booking.fieldAddress || "Chưa cập nhật địa chỉ"}
                </p>

                <div className="my-booking-facts">
                  <div>
                    <CalendarDays size={17} />
                    <span>Ngày</span>
                    <strong>{formatDate(booking.startTime)}</strong>
                  </div>
                  <div>
                    <Clock size={17} />
                    <span>Khung giờ</span>
                    <strong>
                      {formatTime(booking.startTime)} - {formatTime(booking.endTime)}
                    </strong>
                  </div>
                </div>

                <div className="my-booking-breakdown">
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
                    <span>
                      Giảm voucher {booking.voucherCode ? `(${booking.voucherCode})` : ""}
                    </span>
                    <strong>-{formatMoney(booking.voucherDiscount)}</strong>
                  </div>
                  <div className="total">
                    <span>Thanh toán</span>
                    <strong>{formatMoney(booking.finalPrice || booking.totalPrice)}</strong>
                  </div>
                </div>

                {booking.extraServices?.length > 0 && (
                  <div className="my-booking-extra-list">
                    {booking.extraServices.map((service) => (
                      <span key={service.serviceId}>
                        {service.serviceName} x{service.quantity}
                      </span>
                    ))}
                  </div>
                )}

                <div className="my-booking-actions">
                  <button type="button" className="detail" onClick={() => navigate(`/my-bookings/${booking.id}`)}>
                    Chi tiết
                  </button>

                  {canCancelBooking(booking) ? (
                    <button type="button" className="danger" onClick={() => handleCancel(booking.id)}>
                      Hủy lịch
                    </button>
                  ) : booking.status === "PENDING" ? (
                    <button type="button" disabled>
                      Không thể hủy
                    </button>
                  ) : null}

                  {booking.status === "CONFIRMED" && (
                    <button type="button" className="success" disabled>
                      Đã xác nhận
                    </button>
                  )}

                  {booking.status === "COMPLETED" && !hasReviewed(booking.id) && (
                    <button type="button" className="review" onClick={() => navigate(`/my-bookings/${booking.id}/review`)}>
                      <Star size={16} />
                      Đánh giá
                    </button>
                  )}

                  {booking.status === "COMPLETED" && hasReviewed(booking.id) && (
                    <button type="button" disabled>
                      Đã đánh giá
                    </button>
                  )}
                </div>
              </div>
            </article>
          ))}
        </section>
      )}

    </div>
  );
}
