import { useEffect, useState } from "react";
import { getMyBookings } from "../../services/bookingService";
import "../../assets/styles/PaymentFootballField.css";
import { getQrCodeForBooking } from "../../services/qrCodeService";

export default function PaymentFootballField() {
  const [booking, setBooking] = useState(null);
  const [qrCodeUrl, setQrCodeUrl] = useState("");

  // load booking
  useEffect(() => {
    const loadBooking = async () => {
      try {
        const data = await getMyBookings();
        setBooking(data);
      } catch (error) {
        console.error(error);
      }
    };

    loadBooking();
  }, []);

  // load qr code
  useEffect(() => {
    const loadQrCode = async () => {
      if (!booking) return;

      try {
        const url = await getQrCodeForBooking(booking.id);
        setQrCodeUrl(url);
      } catch (error) {
        console.error(error);
      }
    };

    loadQrCode();
  }, [booking]);

  if (!booking) return <div>Loading...</div>;

  return (
    <div className="payment-page">
      <h2>Payment</h2>

      <div className="payment-box">
        <h3>{booking.field?.name}</h3>

        <p>{booking.date}</p>

        <p>
          {booking.startTime} - {booking.endTime}
        </p>

        <h3 className="price">
          {booking.totalPrice} VND
        </h3>

        <div className="qr-box">
          {qrCodeUrl && (
            <img
              src={qrCodeUrl}
              alt="QR Code"
            />
          )}
        </div>

        <p className={`status ${booking.status.toLowerCase()}`}>
          {booking.status}
        </p>
      </div>
    </div>
  );
}