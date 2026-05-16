import { useEffect, useState } from "react";
import { getMyBookings } from "../../services/bookingService";
import "../../assets/styles/PaymentFootballField.css";
import { getQrCodeForBooking } from "../../services/qrCodeService";
import { useNavigate, useParams } from "react-router-dom";
import swal from "sweetalert";

export default function PaymentFootballField() {
  const [booking, setBooking] = useState(null);
  const [qrCodeUrl, setQrCodeUrl] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();

  const statusText = {
    PENDING: "Đang chờ xác nhận",
    CONFIRMED: "Đã xác nhận",
    CANCELLED: "Đã hủy",
  }

  // load booking
  useEffect(() => {
    const loadBooking = async () => {
      try {
        const response = await getMyBookings();
        const currentBooking = response.find(
          (item) => item.id === Number(id)
        );

        setBooking(currentBooking);
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
        const url = await getQrCodeForBooking(id);
        setQrCodeUrl(url);
      } catch (error) {
        console.error(error);
      }
    };

    loadQrCode();
  }, [booking, id]);

  if (!booking) return <div>Loading...</div>;

  const confirmPayment = () => {
    swal({
      title: "Xác nhận thanh toán",
      text: "Bạn đã thực hiện thanh toán chưa?",
      icon: "warning",
      buttons: ["Chưa", "Đã thanh toán"],
    }).then((willConfirm) => {
      if (willConfirm) {
        swal("Cảm ơn bạn đã thanh toán! Admin sẽ xử lý đơn đặt sân.", {
          icon: "success",
        });
        navigate("/");
      }
    });
  }
  return (
    <div className="payment-page">

      <div className="payment-container">

        <h2 className="payment-title">
          Thanh toán sân
        </h2>

        <div className="payment-box">

          <h3 className="field-name">
            {booking.fieldName}
          </h3>

          <div className="payment-info">

            <p>
              Ngày:
                <span>
                  {new Date(booking.startTime)
                    .toLocaleDateString("vi-VN")}
                </span>
            </p>

            <p>
              Khung giờ:
              <span>
                <span>
                  {new Date(booking.startTime)
                    .toLocaleTimeString("vi-VN", {
                      hour: "2-digit",
                      minute: "2-digit"
                    })}

                  {" - "}

                  {new Date(booking.endTime)
                    .toLocaleTimeString("vi-VN", {
                      hour: "2-digit",
                      minute: "2-digit"
                    })}
                </span>
              </span>
            </p>

          </div>

          <div className="price-box">
            <div className="price-label">
              Tổng thanh toán
            </div>

            <div className="price">
              {booking.totalPrice} VND
            </div>
          </div>

          <div className="qr-box">
            {qrCodeUrl && (
              <img
                src={qrCodeUrl}
                alt="QR Code"
              />
            )}
          </div>

          <p className={`status ${booking.status?.toLowerCase()}`}>
            {statusText[booking.status] || booking.status}
          </p>

          <button 
          className="confirm-btn" 
          onClick={confirmPayment}>
            Tôi đã thanh toán
          </button>

        </div>
      </div>
    </div>
  );
}