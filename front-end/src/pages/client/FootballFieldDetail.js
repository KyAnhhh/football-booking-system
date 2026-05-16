import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getFieldById,
  getAvailableSlots
} from "../../services/fieldService";
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

  // load field detail
  useEffect(() => {
    const loadField = async () => {
      try {
        const data = await getFieldById(id);
        setField(data);
      } catch (error) {
        console.error("Load field error:", error);
      }
    };

    loadField();
  }, [id]);

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
        swal(
          "Vui lòng chọn phương thức thanh toán",
          "",
          "warning"
        );
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

        <div className="detail-layout">

          {/* LEFT */}
          <div className="left-section">

            <div className="detail-info">
              <h2>{field?.name}</h2>

              <p className="address">
                📍 {field?.address}
              </p>

              <img
                src={field?.imageUrl}
                alt={field?.name}
                className="detail-image"
              />

              <p className="price">
                Giá: {field?.pricePerHour} VND / Giờ
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
                value={date}
                min={new Date().toISOString().split("T")[0]}
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
                  {slots.map((slot) => (
                    <button
                      key={`${slot.startTime}-${slot.endTime}`}
                      disabled={!slot.available}
                      className={`slot ${
                        selectedSlot?.startTime === slot.startTime &&
                        selectedSlot?.endTime === slot.endTime
                          ? "active"
                          : ""
                      }`}
                      onClick={() => setSelectedSlot(slot)}
                    >
                      {slot.startTime} - {slot.endTime}
                    </button>
                  ))}
                </div>
              )}
            </div>

              {/* PAYMENT METHOD */}
            <div className="payment-method-section">
              <h3>Phương thức thanh toán</h3>

              <div className="payment-methods">

                <button
                  type="button"
                  className={`payment-method ${
                    paymentMethod === "VNPAY" ? "active" : ""
                  }`}
                  onClick={() => setPaymentMethod("VNPAY")}
                >
                  VNPay
                </button>

                <button
                  type="button"
                  className={`payment-method ${
                    paymentMethod === "VIETQR" ? "active" : ""
                  }`}
                  onClick={() => setPaymentMethod("VIETQR")}
                >
                  VietQR
                </button>

                <button
                  type="button"
                  className={`payment-method ${
                    paymentMethod === "CASH" ? "active" : ""
                  }`}
                  onClick={() => setPaymentMethod("CASH")}
                >
                  Tiền mặt
                </button>

              </div>
            </div>

            {/* BUTTON */}
            <Button
              className="book-btn"
              disabled={!selectedSlot || !date || !paymentMethod}
              onClick={handleBooking}
            >
                {bookingLoading ? "Đang xử lý..." : "Đặt sân"}
            </Button>

          </div>

        </div>
      </div>
    </div>
  );
}