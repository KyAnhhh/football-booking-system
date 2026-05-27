import { useState } from "react";
import { createReview } from "../../services/reviewService";
import Swal from "sweetalert2";
import "../../assets/styles/ReviewForm.css";

export default function ReviewForm({ orderItemId, onSuccess }) {

  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [loading, setLoading] = useState(false);

  const isValid = comment.trim().length >= 5;

  const submitReview = async () => {

    if (!orderItemId) {
      Swal.fire("Không tìm thấy đơn hàng", "", "warning");
      return;
    }

    if (!isValid) {
      Swal.fire({
        icon: "warning",
        title: "Nhận xét phải có ít nhất 5 ký tự"
      });
      return;
    }

    try {

      setLoading(true);

      await createReview(orderItemId, {
        rating: Number(rating),
        comment
      });

      setComment("");
      setRating(5);

      if (onSuccess) onSuccess();

    } catch (err) {

      console.error(err);

      Swal.fire({
        icon: "error",
        title: "Lỗi",
        text: "Không thể gửi đánh giá! Bạn chưa hoàn tất đơn hàng hoặc đã đánh giá sản phẩm này rồi."
      });

    } finally {

      setLoading(false);

    }
  };

  return (
    <div className="review-form">

      <h4>Viết đánh giá</h4>

      <select
        value={rating}
        onChange={(e) => setRating(Number(e.target.value))}
      >
        <option value={5}>5 ⭐</option>
        <option value={4}>4 ⭐</option>
        <option value={3}>3 ⭐</option>
        <option value={2}>2 ⭐</option>
        <option value={1}>1 ⭐</option>
      </select>

      <textarea
        rows={4}
        placeholder="Nhận xét của bạn..."
        value={comment}
        onChange={(e) => setComment(e.target.value)}
      />

      <button onClick={submitReview} disabled={loading || !isValid}>
        {loading ? "Đang gửi..." : "Gửi đánh giá"}
      </button>

    </div>
  );
}
