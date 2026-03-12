import { useNavigate } from "react-router-dom";
import "../../assets/styles/OrderCard.css";
import { confirmReceived, getOrderById } from "../../services/orderService";
import { checkCanReview } from "../../services/reviewService";
import Swal from "sweetalert2";
import { useEffect, useState } from "react";

export default function OrderCard({ order }) {
  const navigate = useNavigate();

  const items = order.orderItems || order.items || [];

  const [status, setStatus] = useState(order.orderStatus);

  useEffect(() => {
    setStatus(order.orderStatus);
  }, [order.orderStatus]);

  const [reviewedItems, setReviewedItems] = useState({});

  const statusClass =
    status === "PENDING"
      ? "status-pending"
      : status === "COMPLETED"
      ? "status-success"
      : "status-cancel";

  const handleReceive = async () => {
    const result = await Swal.fire({
      title: "Xác nhận đã nhận hàng?",
      text: "Bạn có chắc đã nhận được đơn hàng?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Đã nhận",
      cancelButtonText: "Hủy",
    });

    if (!result.isConfirmed) return;

    try {
      await confirmReceived(order.id);
      setStatus("COMPLETED");

      Swal.fire({
        icon: "success",
        title: "Cảm ơn bạn!",
        text: "Hãy đánh giá sản phẩm của bạn",
        timer: 1500,
        showConfirmButton: false,
      }).then(async () => {
        let firstItem = items[0];
        if (!firstItem) {
          try {
            const fullOrder = await getOrderById(order.id);
            firstItem = (fullOrder.orderItems || fullOrder.items || [])[0];
          } catch (e) {
            console.error("fetch order detail failed", e);
          }
        }

        if (firstItem) {
          navigate(`/review/${firstItem.id}`, {
            state: { orderItem: firstItem },
          });
        }
      });
    } catch (err) {
      console.error("Confirm order error:", err);
    }
  };

  useEffect(() => {
    if (status === "COMPLETED") {
      items.forEach((item) => {
        checkCanReview(item.id)
          .then((canReview) => {
            setReviewedItems((prev) => ({
              ...prev,
              [item.id]: !Boolean(canReview),
            }));
          })
          .catch((err) => console.error("checkCanReview error", err));
      });
    }
  }, [status, items]);

  return (
    <div className="order-card">
      <div className="order-header">
        <span className="order-id">#ORDER-{order.id}</span>

        <span className={`order-status ${statusClass}`}>{status}</span>
      </div>

      <div className="order-info">
        <p>Người nhận: {order.fullName}</p>
        <p>SĐT: {order.phone}</p>
        <p>Thanh toán: {order.paymentMethod}</p>
      </div>

      {/* danh sách sản phẩm */}
      <div className="order-items">
        {items.map((item) => (
          <div key={item.id} className="order-item">
            <p className="product-name">{item.product?.name}</p>
            <p>Số lượng: {item.quantity}</p>

            {status === "COMPLETED" && !reviewedItems[item.id] && (
              <button
                className="review-btn"
                onClick={() =>
                  navigate(`/review/${item.id}`, {
                    state: { orderItem: item },
                  })
                }
              >
                Đánh giá
              </button>
            )}

            {status === "COMPLETED" && reviewedItems[item.id] && (
              <span className="reviewed-label">Đã đánh giá</span>
            )}
          </div>
        ))}
      </div>

      <div className="order-total">
        Tổng tiền: {order.totalAmount?.toLocaleString()} ₫
      </div>

      {status === "DELIVERED" && (
        <button className="receive-btn" onClick={handleReceive}>
          Xác nhận đã nhận hàng
        </button>
      )}
    </div>
  );
}

