import { useEffect, useState } from "react";
import { getReviewsByProductId } from "../../services/reviewService";
import "../../assets/styles/ReviewSection.css";
import ReviewForm from "./ReviewForm";
import ReviewItem from "./ReviewItem";
import RatingSummary from "./RatingSumary";

export default function ReviewSection({ productId }) {

  const [reviews, setReviews] = useState([]);

  const fetchReviews = async () => {
    if (!productId) return;

    try {
      const data = await getReviewsByProductId(productId);
      setReviews(data || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchReviews();
    console.log("ReviewSection productId:", productId);

  }, [productId]);

  return (
    <div className="review-section">

      <h3>Đánh giá sản phẩm</h3>

      <RatingSummary productId={productId} />

      {reviews.length === 0 && (
        <p>Chưa có đánh giá nào</p>
      )}

      {reviews.map((r) => (
        <ReviewItem key={r.id} review={r} />
      ))}

    </div>
  );
}