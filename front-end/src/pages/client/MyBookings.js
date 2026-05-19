import { useEffect, useState } from "react";
import {
  getMyBookings,
  cancelBooking
} from "../../services/bookingService";

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

      case "PENDING":
        return "status pending";

      case "CANCELLED":
        return "status cancelled";

      default:
        return "status";
    }
  };

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString(
      "vi-VN"
    );
  };

  const formatTime = (date) => {
    return new Date(date).toLocaleTimeString(
      "vi-VN",
      {
        hour: "2-digit",
        minute: "2-digit"
      }
    );
  };

  return (
    <div className="my-bookings-page">

      <div className="booking-header">
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

              {/* CONTENT */}
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

                {/* ACTIONS */}
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

                  {b.status ===
                    "CONFIRMED" && (
                    <button className="btn-ok">
                      Booking Confirmed
                    </button>
                  )}

                </div>

              </div>

            </div>

          ))

        )}

      </div>

    </div>
  );
}