import "../../assets/styles/ReviewItem.css";

export default function ReviewItem({ review }) {
  return (
    <div className="review-item">

      <div className="review-header">
        <strong>{review.username || "Khách hàng"}</strong>
      </div>

      <div className="review-stars">
        {"⭐".repeat(review.rating || 0)}
      </div>

      <p className="review-comment">
        {review.comment}
      </p>

      {review.createdAt && (
        <span className="review-date">
          {new Date(review.createdAt).toLocaleDateString()}
        </span>
      )}

    </div>
  );
}