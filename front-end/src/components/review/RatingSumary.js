import { useEffect, useState } from "react";
import { getAverageRating } from "../../services/reviewService";
import "../../assets/styles/RatingSummary.css";

export default function RatingSummary({ productId }) {

  const [rating, setRating] = useState({
    average: 0,
    count: 0
  });

  useEffect(() => {
    if (!productId) return;

    getAverageRating(productId)
      .then((data) => {
        setRating({
        average: data?.averageRating ?? 0,
        count: data?.totalReviews ?? 0
      });
      })
      .catch(console.error);

  }, [productId]);

  return (
    <div className="rating-summary">
      ⭐ {(rating.average ?? 0).toFixed(1)} / 5
      <span> ({rating.count ?? 0} đánh giá)</span>
    </div>
  );
}