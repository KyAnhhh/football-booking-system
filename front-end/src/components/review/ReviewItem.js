import "../../assets/styles/ReviewItem.css";

export default function ReviewItem({ review }) {
  return (
    <article className="review-item">
      <div className="review-header">
        <strong>{review.username || "Khách hàng"}</strong>
        {review.createdAt && (
          <span>{new Date(review.createdAt).toLocaleDateString("vi-VN")}</span>
        )}
      </div>

      <div className="review-stars">{"★".repeat(review.rating || 0)}</div>

      <p className="review-comment">{review.comment}</p>
    </article>
  );
}
