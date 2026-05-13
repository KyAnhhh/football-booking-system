import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getFieldById,
  getAvailableSlots
} from "../../services/fieldService";
import { createBooking } from "../../services/bookingService";
import { Button, Form } from "react-bootstrap";
import "../../assets/styles/FootballFieldDetail.css";

export default function FootballFieldDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [field, setField] = useState(null);
  const [date, setDate] = useState("");
  const [slots, setSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [loading, setLoading] = useState(false);

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

    try {
      const bookingData = {
        fieldId: Number(id),
        startTime: `${date}T${selectedSlot.startTime}`,
        endTime: `${date}T${selectedSlot.endTime}`
      };

      const booking = await createBooking(bookingData);

      navigate(`/payment/${booking.id}`);
    } catch (error) {
      console.error("Booking error:", error);

      alert(
        error?.response?.data?.message ||
        "Booking failed"
      );
    }
  };

  return (
    <div className="detail-page">
      <div className="detail-container">

        

        {/* INFO */}
        <div className="detail-info">
          <h2>{field?.name}</h2>

          <p className="address">
            📍 {field?.address}
          </p>

          {/* IMAGE */}
        <img
          src={field?.imageUrl}
          alt={field?.name}
          className="detail-image"
        />

          <p className="price">
            Giá: {field?.pricePerHour} VND / hour
          </p>

          <p className="description">
            Chú ý: {field?.description}
          </p>
        </div>

        {/* DATE */}
        <div className="booking-section">
          <h3>Select Date</h3>

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
          <h3>Available Slots</h3>

          {loading ? (
            <p>Loading slots...</p>
          ) : slots.length === 0 ? (
            <p>No slots available</p>
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
                  } ${
                    !slot.available ? "disabled" : ""
                  }`}
                  onClick={() => setSelectedSlot(slot)}
                >
                  {slot.startTime} - {slot.endTime}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* BUTTON */}
        <Button
          className="book-btn"
          disabled={!selectedSlot || !date}
          onClick={handleBooking}
        >
          Book Now
        </Button>

      </div>
    </div>
  );
}