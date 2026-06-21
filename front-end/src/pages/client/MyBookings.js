import { useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import swal from "sweetalert2";
import { cancelBooking, getMyBookings } from "../../services/bookingService";
import {
  createBookingReview,
  getMyReviews,
} from "../../services/bookingReviewService";
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

const REVIEW_RATINGS = [5, 4, 3, 2, 1];

export default function MyBookings() {
  const [bookings, setBookings] = useState([]);
  const [myReviews, setMyReviews] = useState([]);
  const [reviewModal, setReviewModal] = useState(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
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

  const closeReviewModal = () => {
    setReviewModal(null);
    setRating(5);
    setComment("");
  };

  const handleCreateReview = async () => {
    if (comment.trim().length < 10) {
      swal.fire("Thiếu nội dung", "Vui lòng nhập đánh giá tối thiểu 10 ký tự.", "warning");
      return;
    }

    try {
      await createBookingReview(reviewModal.id, { rating, comment: comment.trim() });
      await load();
      await loadMyReviews();
      swal.fire("Thành công", "Đánh giá sân bóng thành công.", "success");
      closeReviewModal();
    } catch (err) {
      console.error(err);
      swal.fire("Lỗi", "Không thể gửi đánh giá.", "error");
    }
  };

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
                    <button type="button" className="review" onClick={() => setReviewModal(booking)}>
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

      <Modal show={!!reviewModal} onHide={closeReviewModal} centered>
        <Modal.Header closeButton>
          <Modal.Title>Đánh giá sân bóng</Modal.Title>
        </Modal.Header>
        <Modal.Body className="review-modal-body">
          {reviewModal && (
            <div className="review-modal-field">
              <span>Sân đã đặt</span>
              <strong>{reviewModal.fieldName || "Sân bóng"}</strong>
              <small>
                {formatDate(reviewModal.startTime)} · {formatTime(reviewModal.startTime)} -{" "}
                {formatTime(reviewModal.endTime)}
              </small>
            </div>
          )}

          <Form.Group className="mb-3">
            <Form.Label>Số sao</Form.Label>
            <div className="review-rating-picker" role="radiogroup" aria-label="Chọn số sao">
              {REVIEW_RATINGS.map((value) => (
                <button
                  key={value}
                  type="button"
                  className={rating === value ? "active" : ""}
                  onClick={() => setRating(value)}
                  aria-pressed={rating === value}
                >
                  <Star size={17} fill="currentColor" />
                  {value}
                </button>
              ))}
            </div>
          </Form.Group>

          <Form.Group>
            <Form.Label>Nội dung đánh giá</Form.Label>
            <Form.Control
              as="textarea"
              rows={4}
              value={comment}
              onChange={(event) => setComment(event.target.value)}
              placeholder="Ví dụ: mặt sân, ánh sáng, thái độ phục vụ, dịch vụ thêm..."
            />
            <small className="review-modal-hint">{comment.trim().length}/10 ký tự tối thiểu</small>
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={closeReviewModal}>
            Đóng
          </Button>
          <Button variant="warning" onClick={handleCreateReview} disabled={comment.trim().length < 10}>
            Gửi đánh giá
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
