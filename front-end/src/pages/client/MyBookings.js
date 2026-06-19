import { useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import swal from "sweetalert2";
import { cancelBooking, getMyBookings } from "../../services/bookingService";
import { createBookingReview, getMyReviews } from "../../services/bookingReviewService";
import "../../assets/styles/MyBookings.css";

const formatMoney = (value) => `${Number(value || 0).toLocaleString("vi-VN")} VND`;

export default function MyBookings() {
  const [bookings, setBookings] = useState([]);
  const [myReviews, setMyReviews] = useState([]);
  const [reviewModal, setReviewModal] = useState(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");

  const load = async () => {
    try {
      const data = await getMyBookings();
      setBookings(data || []);
    } catch (err) {
      console.error(err);
      swal.fire("Error", "Failed to load bookings", "error");
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

  const hasReviewed = (bookingId) => {
    return myReviews.some((review) => review.bookingId === bookingId || review.booking?.id === bookingId);
  };

  const closeReviewModal = () => {
    setReviewModal(null);
    setRating(5);
    setComment("");
  };

  const handleCreateReview = async () => {
    try {
      await createBookingReview(reviewModal.id, { rating, comment });
      await load();
      await loadMyReviews();
      swal.fire("Success", "Danh gia thanh cong", "success");
      closeReviewModal();
    } catch (err) {
      console.error(err);
      swal.fire("Error", "Failed to create review", "error");
    }
  };

  const handleCancel = async (id) => {
    try {
      await cancelBooking(id);
      await load();
    } catch (err) {
      console.error(err);
      swal.fire("Error", "Failed to cancel booking", "error");
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "CONFIRMED":
        return "status confirmed";
      case "PENDING":
        return "status pending";
      case "CANCELLED":
        return "status cancelled";
      case "COMPLETED":
        return "status completed";
      case "EXPIRED":
        return "status expired";
      default:
        return "status";
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
      <div className="my-booking-header">
        <h2>My Football Bookings</h2>
      </div>

      <div className="booking-grid">
        {bookings.length === 0 ? (
          <div className="empty-booking">No bookings found</div>
        ) : (
          bookings.map((booking) => (
            <div key={booking.id} className="booking-card">
              <div className="booking-image-wrapper">
                <img
                  src={booking.fieldImage || "/no-image.png"}
                  alt={booking.fieldName || "Football Field"}
                  className="booking-image"
                />
                <span className={getStatusClass(booking.status)}>{booking.status}</span>
              </div>

              <div className="booking-content">
                <h3>{booking.fieldName}</h3>
                <div className="booking-location">{booking.fieldAddress}</div>

                <div className="booking-info">
                  <div className="info-item">
                    <span>Date</span>
                    <span>{formatDate(booking.startTime)}</span>
                  </div>

                  <div className="info-item">
                    <span>Time</span>
                    <span>
                      {formatTime(booking.startTime)} - {formatTime(booking.endTime)}
                    </span>
                  </div>

                  <div className="info-item price">
                    <span>Total</span>
                    <span>{formatMoney(booking.finalPrice || booking.totalPrice)}</span>
                  </div>
                </div>

                <div className="booking-breakdown">
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
                  <div className="booking-breakdown-total">
                    <span>Thanh toán</span>
                    <strong>{formatMoney(booking.finalPrice || booking.totalPrice)}</strong>
                  </div>
                </div>

                {booking.extraServices?.length > 0 && (
                  <div className="booking-extra-list">
                    {booking.extraServices.map((service) => (
                      <span key={service.serviceId}>
                        {service.serviceName} x{service.quantity}
                      </span>
                    ))}
                  </div>
                )}

                <div className="actions">
                  {canCancelBooking(booking) ? (
                    <button onClick={() => handleCancel(booking.id)} className="btn-cancel">
                      Cancel Booking
                    </button>
                  ) : booking.status === "PENDING" ? (
                    <button className="btn-cancel-disabled" disabled>
                      Không thể hủy
                    </button>
                  ) : null}

                  {booking.status === "CONFIRMED" && <button className="btn-ok">Booking Confirmed</button>}

                  {booking.status === "COMPLETED" && !hasReviewed(booking.id) && (
                    <button className="btn-review" onClick={() => setReviewModal(booking)}>
                      Đánh giá
                    </button>
                  )}

                  {booking.status === "COMPLETED" && hasReviewed(booking.id) && (
                    <button className="btn-reviewed" disabled>
                      Đã đánh giá
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <Modal show={!!reviewModal} onHide={closeReviewModal}>
        <Modal.Header closeButton>
          <Modal.Title>Đánh giá sân bóng</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label>Số sao</Form.Label>
            <Form.Select value={rating} onChange={(event) => setRating(Number(event.target.value))}>
              <option value={5}>5 sao</option>
              <option value={4}>4 sao</option>
              <option value={3}>3 sao</option>
              <option value={2}>2 sao</option>
              <option value={1}>1 sao</option>
            </Form.Select>
          </Form.Group>

          <Form.Group>
            <Form.Label>Nội dung đánh giá</Form.Label>
            <Form.Control as="textarea" rows={4} value={comment} onChange={(event) => setComment(event.target.value)} />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={closeReviewModal}>
            Đóng
          </Button>
          <Button variant="warning" onClick={handleCreateReview} disabled={!comment.trim()}>
            Gửi đánh giá
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
