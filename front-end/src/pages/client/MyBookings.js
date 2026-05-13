import { useEffect, useState } from "react";
import { getMyBookings, cancelBooking } from "../../services/bookingService";
import "../../assets/styles/MyBookings.css";

export default function MyBookings() {
  const [bookings, setBookings] = useState([]);

  const load = async () => {
    try {
      const data = await getMyBookings();
      setBookings(data || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    load();
  }, []);

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
      case "PENDING_PAYMENT":
        return "status pending";
      case "CANCELLED":
        return "status cancelled";
      default:
        return "status";
    }
  };

  return (
    <div className="my-bookings-page">
      <h2>My Bookings</h2>

      <div className="booking-grid">
        {bookings.length === 0 ? (
          <p>No bookings found</p>
        ) : (
          bookings.map((b) => (
            <div key={b.id} className="booking-card">
              <h3>{b.field?.name}</h3>

              <p>📅 {b.date}</p>
              <p>⏰ {b.startTime} - {b.endTime}</p>
              <p>💰 {b.totalPrice} VND</p>

              <span className={getStatusClass(b.status)}>
                {b.status}
              </span>

              <div className="actions">
                {b.status === "PENDING_PAYMENT" && (
                  <button
                    onClick={() => handleCancel(b.id)}
                    className="btn-cancel"
                  >
                    Cancel
                  </button>
                )}

                {b.status === "CONFIRMED" && (
                  <button className="btn-ok">
                    Completed
                  </button>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}