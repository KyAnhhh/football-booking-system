import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getFieldById,
  getAvailableSlots,
} from "../../services/fieldService";
import {
  getFieldReviews
} from "../../services/bookingReviewService";
import { createBooking } from "../../services/bookingService";
import { Button, Form } from "react-bootstrap";
import "../../assets/styles/FootballFieldDetail.css";
import swal from "sweetalert";

export default function FootballFieldDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [field, setField] = useState(null);
  const [date, setDate] = useState("");
  const [slots, setSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [loading, setLoading] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [bookingLoading, setBookingLoading] = useState(false);
  const [reviews, setReviews] = useState([]);
  const [maintenanceInfo, setMaintenanceInfo] = useState(null);

  // load available slots
  const loadSlots = async (selectedDate) => {
    try {
      setLoading(true);
      const data = await getAvailableSlots(id, selectedDate);
      setSlots(data || []);
      setSelectedSlot(null);
    } catch (error) {
      console.error("Load slots error:", error);
    } finally {
      setLoading(false);
    }
  };

  const loadReviews = async () => {
    try {
      const data = await getFieldReviews(id);
      setReviews(data || []);
    } catch (error) {
      console.error("Load reviews error:", error);
    }
  };

  // load field detail
  useEffect(() => {
    const loadField = async () => {
      try {
        const data = await getFieldById(id);
        setField(data);
        
        // Use maintenance info from field response (already included in getFieldById)
        if (data) {
          setMaintenanceInfo({
            isUnderMaintenance: data.isUnderMaintenance || false,
            maintenanceStartTime: data.maintenanceStartTime,
            maintenanceEndTime: data.maintenanceEndTime,
            maintenanceReason: data.maintenanceReason
          });
        }
      } catch (error) {
        console.error("Load field error:", error);
      }
    };

    loadField();
    loadReviews();
  }, [id]);

  // booking
  const handleBooking = async () => {
    if (!selectedSlot || !date) return;
    setBookingLoading(true);
    try {
      const token = sessionStorage.getItem("accessToken");
      if (!token) {
        swal("Vui lòng đăng nhập để đặt sân.", "", "warning");
        navigate("/login");
        return;
      }

      if (!paymentMethod) {
        swal("Vui lòng chọn phương thức thanh toán", "", "warning");
        return;
      }

      const bookingData = {
        fieldId: Number(id),
        startTime: `${date}T${selectedSlot.startTime}`,
        endTime: `${date}T${selectedSlot.endTime}`,
        paymentMethod
      };

      const booking = await createBooking(bookingData);

      switch (paymentMethod) {
        case "VNPAY":
          window.location.href = booking.paymentUrl;
          break;

        case "VIETQR":
          navigate(`/football-fields/${booking.id}/payment`);
          break;

        case "CASH":
          await swal(
            "Đặt sân thành công!",
            "Vui lòng thanh toán tại sân.",
            "success"
          );
          navigate("/my-bookings");
          break;

        default:
          break;
      }
    } catch (error) {
      console.error("Booking error:", error);
      swal("Đặt sân thất bại!", error?.response?.data?.message || "Vui lòng thử lại sau.", "error");
    } finally {
      setBookingLoading(false);
    }
  };

  return (
    <div className="detail-page">
      <div className="detail-container">

        {/* Maintenance Banner */}
        {maintenanceInfo?.isUnderMaintenance && (
          <div className="maintenance-banner">
            <h3>
              <span className="warning-icon">⚠️</span>
              Sân đang bảo trì
            </h3>
            <p>
              <strong>Từ:</strong>{' '}
              {new Date(maintenanceInfo.maintenanceStartTime).toLocaleString('vi-VN')}
              {' '}<strong>đến:</strong>{' '}
              {new Date(maintenanceInfo.maintenanceEndTime).toLocaleString('vi-VN')}
            </p>
            {maintenanceInfo.maintenanceReason && (
              <p>
                <strong>Lý do:</strong> {maintenanceInfo.maintenanceReason}
              </p>
            )}
          </div>
        )}

        <div className="detail-layout">

          {/* LEFT */}
          <div className="left-section">
            <div className="detail-info">
              <h2>{field?.name}</h2>
              <p className="address">📍 {field?.address}</p>
              <img
                src={field?.imageUrl}
                alt={field?.name}
                className="detail-image"
              />
              <p className="price">
                Giá: {Number(field?.pricePerHour).toLocaleString("vi-VN")} ₫ / giờ
              </p>
              <p className="description">
                Chú ý: {field?.description}
              </p>
            </div>
          </div>

          {/* RIGHT */}
          <div className="right-section">

            {/* DATE */}
            <div className="booking-section">
              <h3>Chọn ngày</h3>
              <Form.Control
                type="date"
                min={new Date().toISOString().split("T")[0]}
                value={date}
                onChange={(e) => {
                  const selectedDate = e.target.value;
                  setDate(selectedDate);
                  if (selectedDate) {
                    loadSlots(selectedDate);
                  }
                }}
              />
            </div>

            {/* SLOT */}
            <div className="slot-section">
              <h3>Các khung giờ có sẵn</h3>

              {loading ? (
                <p>Đang tải các khung giờ...</p>
              ) : slots.length === 0 ? (
                <p>Không có khung giờ nào có sẵn</p>
              ) : (
                <div className="slot-grid">
                  {slots.map((slot) => {
                    const isUnderMaintenance = maintenanceInfo?.isUnderMaintenance;
                    const maintenanceStart = maintenanceInfo?.maintenanceStartTime;
                    const maintenanceEnd = maintenanceInfo?.maintenanceEndTime;
                    
                    // Check if this specific slot falls within maintenance window
                    const slotStart = new Date(`${date}T${slot.startTime}`);
                    const slotEnd = new Date(`${date}T${slot.endTime}`);
                    const isSlotUnderMaintenance = isUnderMaintenance && maintenanceStart && maintenanceEnd &&
                      slotStart < new Date(maintenanceEnd) && slotEnd > new Date(maintenanceStart);

                    return (
                      <button
                        key={`${slot.startTime}-${slot.endTime}`}
                        disabled={!slot.available || isSlotUnderMaintenance}
                        className={`slot ${
                          selectedSlot?.startTime === slot.startTime &&
                          selectedSlot?.endTime === slot.endTime
                            ? "active"
                            : ""
                        } ${isSlotUnderMaintenance ? "maintenance" : ""}`}
                        title={isSlotUnderMaintenance ? `Trong giờ bảo trì (${new Date(maintenanceStart).toLocaleString('vi-VN')} - ${new Date(maintenanceEnd).toLocaleString('vi-VN')})` : ""}
                        onClick={() => !isSlotUnderMaintenance && setSelectedSlot(slot)}
                      >
                        {slot.startTime} - {slot.endTime}
                        {isSlotUnderMaintenance && " ⚠️"}
                      </button>
                    );
                  })}
                </div>
              )}
            </div> {/* Gốc của slot-section đóng tại đây */}

            {/* PAYMENT METHOD */}
            <div className="payment-method-section">
              <h3>Phương thức thanh toán</h3>
              <div className="payment-methods">
                <button
                  type="button"
                  className={`payment-method ${paymentMethod === "VNPAY" ? "active" : ""}`}
                  onClick={() => setPaymentMethod("VNPAY")}
                >
                  VNPay
                </button>

                <button
                  type="button"
                  className={`payment-method ${paymentMethod === "VIETQR" ? "active" : ""}`}
                  onClick={() => setPaymentMethod("VIETQR")}
                >
                  VietQR
                </button>

                <button
                  type="button"
                  className={`payment-method ${paymentMethod === "CASH" ? "active" : ""}`}
                  onClick={() => setPaymentMethod("CASH")}
                >
                  Tiền mặt
                </button>
              </div>
            </div>

            {/* BUTTON */}
            <Button
              className="book-btn"
              disabled={!selectedSlot || !date || !paymentMethod || maintenanceInfo?.isUnderMaintenance}
              onClick={handleBooking}
              title={maintenanceInfo?.isUnderMaintenance ? "Không thể đặt sân trong thời gian bảo trì" : ""}
            >
              {bookingLoading ? "Đang xử lý..." : maintenanceInfo?.isUnderMaintenance ? "Đang bảo trì" : "Đặt sân"}
            </Button>

          </div> {/* Đóng right-section */}
        </div> {/* Đóng detail-layout */}
      </div> {/* Đóng detail-container */}

      {/* REVIEWS */}
      <div className="reviews-section">
        <h3>Reviews ({reviews.length})</h3>
        {reviews.length === 0 ? (
          <p className="no-review">Chưa có đánh giá nào</p>
        ) : (
          reviews.map((review) => (
            <div key={review.id} className="review-card">
              <div className="review-top">
                <strong>{review.user?.username}</strong>
                <span className="review-rating">{"⭐".repeat(review.rating)}</span>
              </div>
              <p className="review-comment">{review.comment}</p>
              <small className="review-date">
                {new Date(review.createdAt).toLocaleDateString("vi-VN")}
              </small>
            </div>
          ))
        )}
      </div>
    </div>
  );
}