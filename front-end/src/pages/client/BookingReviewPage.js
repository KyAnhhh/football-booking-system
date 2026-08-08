import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Swal from "sweetalert2";
import { ChevronLeft, Star } from "lucide-react";
import { getMyBookings } from "../../services/bookingService";
import { getMyReviews } from "../../services/bookingReviewService";
import BookingReviewForm from "../../components/review/BookingReviewForm";
import "../../assets/styles/Review.css";

export default function BookingReviewPage() {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [canReview, setCanReview] = useState(false);

  useEffect(() => {
    const load = async () => {
      try {
        const [bookings, reviews] = await Promise.all([getMyBookings(), getMyReviews()]);
        const currentBooking = (bookings || []).find((item) => String(item.id) === String(bookingId));
        setBooking(currentBooking || null);
        const alreadyReviewed = (reviews || []).some((review) => String(review.bookingId) === String(bookingId));
        setCanReview(currentBooking?.status === "COMPLETED" && !alreadyReviewed);
      } catch (error) {
        Swal.fire("Lỗi", "Không thể tải thông tin đánh giá sân.", "error");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [bookingId]);

  if (loading) return <div className="review-page-state">Đang tải thông tin đánh giá...</div>;
  if (!booking) return <div className="review-page-state"><p>Không tìm thấy lịch đặt sân.</p><button onClick={() => navigate("/my-bookings")}>Quay về lịch đặt sân</button></div>;

  return <div className="review-page">
    <button className="review-back-btn" type="button" onClick={() => navigate("/my-bookings")}><ChevronLeft size={18} />Quay về lịch đặt sân</button>
    <section className="review-shell">
      <div className="review-hero-card"><Star size={26} /><p>Đánh giá sân bóng</p><h1>{booking.fieldName || "Sân bóng"}</h1><span>{new Date(booking.startTime).toLocaleDateString("vi-VN")}</span></div>
      {canReview ? <BookingReviewForm bookingId={bookingId} onSuccess={() => {
        Swal.fire({ icon: "success", title: "Đánh giá sân thành công", timer: 1500, showConfirmButton: false });
        navigate("/my-bookings");
      }} /> : <div className="review-warning-card">Bạn chỉ có thể đánh giá một lần sau khi booking đã hoàn tất.</div>}
    </section>
  </div>;
}
