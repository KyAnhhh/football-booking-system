import { useState } from "react";
import Swal from "sweetalert2";
import { createBookingReview } from "../../services/bookingReviewService";
import "../../assets/styles/ReviewForm.css";

export default function BookingReviewForm({ bookingId, onSuccess }) {
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [loading, setLoading] = useState(false);
  const isValid = comment.trim().length >= 10;

  const submitReview = async () => {
    if (!bookingId || !isValid) return;
    try {
      setLoading(true);
      await createBookingReview(bookingId, { rating, comment: comment.trim() });
      onSuccess?.();
    } catch (error) {
      Swal.fire("Lỗi", error.message || "Không thể gửi đánh giá sân.", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="review-form">
      <h4>Viết đánh giá</h4>
      <label><span>Số sao</span><select value={rating} onChange={(event) => setRating(Number(event.target.value))}>
        {[5, 4, 3, 2, 1].map((value) => <option key={value} value={value}>{value} sao</option>)}
      </select></label>
      <label><span>Nhận xét</span><textarea rows={4} placeholder="Chia sẻ trải nghiệm tại sân..." value={comment}
        onChange={(event) => setComment(event.target.value)} /></label>
      <small>Nhận xét tối thiểu 10 ký tự.</small>
      <button type="button" onClick={submitReview} disabled={loading || !isValid}>{loading ? "Đang gửi..." : "Gửi đánh giá"}</button>
    </div>
  );
}
