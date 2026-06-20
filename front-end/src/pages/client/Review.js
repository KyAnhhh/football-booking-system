import { useParams, useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { checkCanReview } from "../../services/reviewService";
import { getMyOrders } from "../../services/orderService";
import ReviewForm from "../../components/review/ReviewForm";
import Swal from "sweetalert2";
import "../../assets/styles/Review.css";
import { ChevronLeft, Star } from "lucide-react";

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
        for (const order of orders) {
          const found = (order.orderItems || order.items || []).find(
            (item) => String(item.id) === String(orderItemId)
          );
          if (found) {
            setOrderItem(found);
            break;
          }
        }
      } catch (error) {
        console.error("Error fetching orders for review", error);
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
        Swal.fire("Lỗi", "Không thể tải dữ liệu đánh giá.", "error");
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

  if (loading || loadingItem) {
    return <div className="review-page-state">Đang tải thông tin đánh giá...</div>;
  }

  if (!orderItem) {
    return (
      <div className="review-page-state">
        <p>Không tìm thấy sản phẩm cần đánh giá.</p>
        <button type="button" onClick={() => navigate("/my-orders")}>
          Quay về đơn hàng
        </button>
      </div>
    );
  }

  return (
    <div className="review-page">
      <button className="review-back-btn" type="button" onClick={() => navigate("/my-orders")}>
        <ChevronLeft size={18} />
        Quay về đơn hàng
      </button>

      <section className="review-shell">
        <div className="review-hero-card">
          <Star size={26} />
          <p>Đánh giá sản phẩm</p>
          <h1>{orderItem.productName || orderItem.product?.name || "Sản phẩm"}</h1>
          <span>Số lượng: {orderItem.quantity || 1}</span>
        </div>

        {!canReview ? (
          <div className="review-warning-card">
            Bạn không thể đánh giá sản phẩm này hoặc bạn đã đánh giá rồi.
          </div>
        ) : (
          <ReviewForm orderItemId={orderItemId} onSuccess={handleSuccess} />
        )}
      </section>
    </div>
  );
}
