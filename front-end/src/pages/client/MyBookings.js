import { useEffect, useState } from "react";

import {
  getMyBookings,
  cancelBooking
} from "../../services/bookingService";

import {
  createBookingReview,
  getMyReviews
} from "../../services/bookingReviewService";

import {
  Modal,
  Button,
  Form
} from "react-bootstrap";

import "../../assets/styles/MyBookings.css";

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

    return myReviews.some(
      (r) =>
        r.bookingId === bookingId ||
        r.booking?.id === bookingId
    );
  };

  const closeReviewModal = () => {

    setReviewModal(null);

    setRating(5);

    setComment("");
  };

  const handleCreateReview = async () => {

    try {

      await createBookingReview(
        reviewModal.id,
        {
          rating,
          comment,
        }
      );

      await load();
      await loadMyReviews();

      closeReviewModal();

    } catch (err) {

      console.error(err);

      alert(err.message);
    }
  };

  const handleCancel = async (id) => {

    try {

      await cancelBooking(id);

      load();

    } catch (err) {

      console.error(err);
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

      default:
        return "status";
    }
  };

  const formatDate = (date) => {

    return new Date(date)
      .toLocaleDateString("vi-VN");
  };

  const formatTime = (date) => {

    return new Date(date)
      .toLocaleTimeString(
        "vi-VN",
        {
          hour: "2-digit",
          minute: "2-digit"
        }
      );
  };

  return (

    <div className="my-bookings-page">

      <div className="my-booking-header">
        <h2>My Football Bookings</h2>
      </div>

      <div className="booking-grid">

        {bookings.length === 0 ? (

          <div className="empty-booking">
            No bookings found
          </div>

        ) : (

          bookings.map((b) => (

            <div
              key={b.id}
              className="booking-card"
            >

              {/* IMAGE */}
              <div className="booking-image-wrapper">

                <img
                  src={
                    b.fieldImage ||
                    "/no-image.png"
                  }
                  alt={
                    b.fieldName ||
                    "Football Field"
                  }
                  className="booking-image"
                />

                <span
                  className={getStatusClass(
                    b.status
                  )}
                >
                  {b.status}
                </span>

              </div>

              <div className="booking-content">

                <h3>{b.fieldName}</h3>

                <div className="booking-location">
                  📍 {b.fieldAddress}
                </div>

                <div className="booking-info">

                  <div className="info-item">
                    <span>📅</span>

                    <span>
                      {formatDate(b.startTime)}
                    </span>
                  </div>

                  <div className="info-item">

                    <span>⏰</span>

                    <span>
                      {formatTime(b.startTime)}
                      {" - "}
                      {formatTime(b.endTime)}
                    </span>
                  </div>

                  <div className="info-item price">
                    <span>💰</span>
                    <span>
                      {Number(
                        b.totalPrice
                      ).toLocaleString(
                        "vi-VN"
                      )} ₫
                    </span>
                  </div>
                </div>

                <div className="actions">
                  {b.status === "PENDING" && (
                    <button
                      onClick={() =>
                        handleCancel(b.id)
                      }
                      className="btn-cancel"
                    >
                      Cancel Booking
                    </button>
                  )}

                  {b.status === "CONFIRMED" && (

                    <button className="btn-ok">
                      Booking Confirmed
                    </button>
                  )}

                  {b.status === "COMPLETED" &&
                    !hasReviewed(b.id) && (
                    <button
                      className="btn-review"
                      onClick={() =>
                        setReviewModal(b)
                      }
                    >
                      Đánh giá
                    </button>
                  )}

                  {b.status === "COMPLETED" &&
                    hasReviewed(b.id) && (

                    <button
                      className="btn-reviewed"
                      disabled
                    >
                      Đã đánh giá
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <Modal
        show={!!reviewModal}
        onHide={closeReviewModal}
      >

        <Modal.Header closeButton>

          <Modal.Title>
            Đánh giá sân bóng
          </Modal.Title>

        </Modal.Header>

        <Modal.Body>

          <Form.Group className="mb-3">

            <Form.Label>
              Số sao
            </Form.Label>

            <Form.Select
              value={rating}
              onChange={(e) =>
                setRating(
                  Number(e.target.value)
                )
              }
            >

              <option value={5}>5 ⭐</option>
              <option value={4}>4 ⭐</option>
              <option value={3}>3 ⭐</option>
              <option value={2}>2 ⭐</option>
              <option value={1}>1 ⭐</option>
            </Form.Select>
          </Form.Group>

          <Form.Group>

            <Form.Label>
              Nội dung đánh giá
            </Form.Label>

            <Form.Control
              as="textarea"
              rows={4}
              value={comment}
              onChange={(e) =>
                setComment(e.target.value)
              }
            />

          </Form.Group>

        </Modal.Body>
        <Modal.Footer>

          <Button
            variant="secondary"
            onClick={closeReviewModal}
          >
            Đóng
          </Button>

          <Button
            variant="warning"
            onClick={handleCreateReview}
            disabled={!comment.trim()}
          >
            Gửi đánh giá
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}