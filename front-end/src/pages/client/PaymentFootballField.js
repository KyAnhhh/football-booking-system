import { useEffect, useMemo, useState } from "react";
import { getMyBookings } from "../../services/bookingService";
import "../../assets/styles/PaymentFootballField.css";
import { getQrCodeForBooking } from "../../services/qrCodeService";
import { useNavigate, useParams } from "react-router-dom";
import swal from "sweetalert";
import { CalendarDays, CheckCircle2, Clock, MapPin, QrCode } from "lucide-react";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const statusText = {
  PENDING: "Đang chờ xác nhận",
  CONFIRMED: "Đã xác nhận",
  CANCELLED: "Đã hủy",
  EXPIRED: "Đã hết hạn",
};

export default function PaymentFootballField() {
  const [booking, setBooking] = useState(null);
  const [qrCodeUrl, setQrCodeUrl] = useState("");
  const [loading, setLoading] = useState(true);
  const { id } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const loadBooking = async () => {
      try {
        setLoading(true);
        const response = await getMyBookings();
        const currentBooking = response.find((item) => item.id === Number(id));
        setBooking(currentBooking || null);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    loadBooking();
  }, [id]);

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

  const bookingDate = useMemo(() => {
    if (!booking?.startTime) return "";
    return new Date(booking.startTime).toLocaleDateString("vi-VN");
  }, [booking]);

  const bookingTime = useMemo(() => {
    if (!booking?.startTime || !booking?.endTime) return "";
    const start = new Date(booking.startTime).toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });
    const end = new Date(booking.endTime).toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });
    return `${start} - ${end}`;
  }, [booking]);

  const confirmPayment = () => {
    swal({
      title: "Xác nhận thanh toán",
      text: "Bạn đã thực hiện chuyển khoản cho lịch đặt sân này chưa?",
      icon: "warning",
      buttons: ["Chưa", "Đã thanh toán"],
    }).then((willConfirm) => {
      if (willConfirm) {
        swal(
          "Cảm ơn bạn đã thanh toán. Admin sẽ kiểm tra giao dịch và xử lý lịch đặt sân.",
          {
            icon: "success",
          }
        );
        navigate("/");
      }
    });
  };

  if (loading) {
    return <div className="booking-payment-state">Đang tải thông tin thanh toán...</div>;
  }

  if (!booking) {
    return <div className="booking-payment-state">Không tìm thấy lịch đặt sân.</div>;
  }

  return (
    <div className="booking-payment-page">
      <section className="booking-payment-shell">
        <div className="booking-payment-hero">
          <p>Thanh toán sân</p>
          <h1>Thanh toán lịch đặt sân</h1>
          <span>Quét QR theo đúng số tiền, sau đó bấm xác nhận để admin xử lý.</span>
        </div>

        <div className="booking-payment-grid">
          <article className="booking-payment-card">
            <div className="booking-payment-card-header">
              <div>
                <span>Lịch đặt sân</span>
                <h2>{booking.fieldName}</h2>
              </div>
              <p className={`booking-payment-status ${booking.status?.toLowerCase()}`}>
                {statusText[booking.status] || booking.status}
              </p>
            </div>

            <div className="booking-payment-info">
              <div>
                <CalendarDays size={19} />
                <span>Ngày</span>
                <strong>{bookingDate}</strong>
              </div>

              <div>
                <Clock size={19} />
                <span>Khung giờ</span>
                <strong>{bookingTime}</strong>
              </div>

              {booking.fieldAddress && (
                <div>
                  <MapPin size={19} />
                  <span>Địa chỉ</span>
                  <strong>{booking.fieldAddress}</strong>
                </div>
              )}
            </div>

            <div className="booking-payment-total">
              <span>Tổng thanh toán</span>
              <strong>{formatCurrency(booking.totalPrice || booking.finalPrice)}</strong>
            </div>

            <button
              type="button"
              className="booking-payment-confirm"
              onClick={confirmPayment}
            >
              <CheckCircle2 size={19} />
              Tôi đã thanh toán
            </button>
          </article>

          <aside className="booking-payment-qr-card">
            <div className="booking-payment-qr-title">
              <QrCode size={20} />
              <div>
                <h2>Mã QR</h2>
                <p>Chuyển khoản đúng nội dung và số tiền trên hóa đơn.</p>
              </div>
            </div>

            {qrCodeUrl ? (
              <img src={qrCodeUrl} alt="QR thanh toán sân" />
            ) : (
              <div className="booking-payment-qr-loading">Đang tạo mã QR...</div>
            )}
          </aside>
        </div>
      </section>
    </div>
  );
}
