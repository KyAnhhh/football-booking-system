import { useParams, useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { checkCanReview } from "../../services/reviewService";
import { getMyOrders } from "../../services/orderService";
import ReviewForm from "../../components/review/ReviewForm";
import Swal from "sweetalert2";
import "../../assets/styles/Review.css";

export default function ReviewPage() {

  const { id: orderItemId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [orderItem, setOrderItem] = useState(location.state?.orderItem);
  const [canReview, setCanReview] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadingItem, setLoadingItem] = useState(!orderItem);

  useEffect(() => {
    if (orderItem || !orderItemId) return;

    const loadItem = async () => {
      try {
        const data = await getMyOrders();
        const orders = Array.isArray(data) ? data : [];
        for (const o of orders) {
          const found = (o.orderItems || o.items || []).find(
            (it) => String(it.id) === String(orderItemId)
          );
          if (found) {
            setOrderItem(found);
            break;
          }
        }
      } catch (e) {
        console.error("Error fetching orders for review", e);
      } finally {
        setLoadingItem(false);
      }
    };

    loadItem();
  }, [orderItemId, orderItem]);

  useEffect(() => {
    if (!orderItemId) return;

    const loadData = async () => {
      try {
        const canReviewData = await checkCanReview(orderItemId);
        setCanReview(Boolean(canReviewData));
      } catch (err) {
        console.error(err);
        Swal.fire("Lỗi", "Không thể tải dữ liệu", "error");
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [orderItemId]);

  const handleSuccess = () => {
    Swal.fire({
      icon: "success",
      title: "Đánh giá thành công",
      timer: 1500,
      showConfirmButton: false,
    });
    navigate("/my-orders");
  };

  if (loading || loadingItem) return <p>Đang tải...</p>;

  if (!orderItem) {
    return (
      <p>
        Không tìm thấy sản phẩm. <button onClick={() => navigate("/my-orders")}>Quay về đơn hàng</button>
      </p>
    );
  }

  return (
    <div className="review-page">
      <h2>Đánh giá sản phẩm</h2>

      {/* <div className="product-review-info">
        <img
          src={orderItem.product?.imageUrls?.[0]}
          alt={orderItem.product?.name}
          width="120"
        />
        <div>
          <h3>{orderItem.product?.name}</h3>
          <p>Số lượng: {orderItem.quantity}</p>
        </div>
      </div> */}

      {!canReview ? (
        <p>Bạn không thể đánh giá sản phẩm này hoặc bạn đã đánh giá rồi.</p>
      ) : (
        <ReviewForm orderItemId={orderItemId} onSuccess={handleSuccess} />
      )}
    </div>
  );
}
