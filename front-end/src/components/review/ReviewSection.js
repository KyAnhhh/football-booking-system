import { useCallback, useEffect, useState } from "react";
import { getReviewsByProductId } from "../../services/reviewService";
import "../../assets/styles/ReviewSection.css";
import ReviewItem from "./ReviewItem";
import RatingSummary from "./RatingSumary";

export default function ReviewSection({ productId }) {
  const [reviews, setReviews] = useState([]);

  const fetchReviews = useCallback(async () => {
    if (!productId) return;

    try {
      const data = await getReviewsByProductId(productId);
      setReviews(data || []);
    } catch (err) {
      console.error(err);
    }
  }, [productId]);

  useEffect(() => {
    fetchReviews();
  }, [fetchReviews]);

  return (
    <section className="review-section">
      <h3>Đánh giá sản phẩm</h3>

      <RatingSummary productId={productId} />

      {reviews.length === 0 ? (
        <p className="review-section-empty">Chưa có đánh giá nào.</p>
      ) : (
        <div className="review-section-list">
          {reviews.map((review) => (
            <ReviewItem key={review.id} review={review} />
          ))}
        </div>
      )}
    </section>
  );
}
