import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../assets/styles/OrderCard.css";
import {
  cancelOrder,
  confirmReceived,
  getOrderById,
} from "../../services/orderService";
import { checkCanReview } from "../../services/reviewService";
import Swal from "sweetalert2";
import { CheckCircle2, CreditCard, Package, RotateCcw, Star } from "lucide-react";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const statusConfig = {
  PENDING: { label: "Đang xử lý", className: "pending" },
  CONFIRMED: { label: "Đã xác nhận", className: "confirmed" },
  SHIPPING: { label: "Đang giao", className: "confirmed" },
  DELIVERED: { label: "Đã giao", className: "delivered" },
  COMPLETED: { label: "Hoàn tất", className: "completed" },
  CANCELLED: { label: "Đã hủy", className: "cancelled" },
};

export default function OrderCard({ order }) {
  const navigate = useNavigate();
  const items = useMemo(
    () => order.orderItems || order.items || [],
    [order.orderItems, order.items]
  );

  const [status, setStatus] = useState(order.orderStatus);
  const [reviewedItems, setReviewedItems] = useState({});

  useEffect(() => {
    setStatus(order.orderStatus);
  }, [order.orderStatus]);

  useEffect(() => {
    if (status !== "COMPLETED") return;

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
  }, [status, items]);

  const currentStatus = statusConfig[status] || {
    label: status,
    className: "pending",
  };

  const handleReceive = async () => {
    const result = await Swal.fire({
      title: "Xác nhận đã nhận hàng?",
      text: "Bạn có chắc đã nhận được đơn hàng này không?",
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
        text: "Bạn có thể đánh giá sản phẩm trong đơn hàng.",
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
      Swal.fire("Lỗi", "Không thể xác nhận đơn hàng lúc này.", "error");
    }
  };

  const handleCancel = async () => {
    const result = await Swal.fire({
      title: "Hủy đơn hàng?",
      text: "Sản phẩm đang giữ trong kho sẽ được hoàn lại.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Hủy đơn",
      cancelButtonText: "Giữ lại",
    });
    if (!result.isConfirmed) return;

    try {
      await cancelOrder(order.id);
      setStatus("CANCELLED");
      Swal.fire("Đã hủy", "Đơn hàng đã được hủy.", "success");
    } catch (error) {
      Swal.fire(
        "Không thể hủy",
        error.message || "Đơn hàng không còn ở trạng thái cho phép hủy.",
        "error"
      );
    }
  };

  return (
    <article className="order-card">
      <header className="order-card-header">
        <div>
          <span className="order-card-code">#ORDER-{order.id}</span>
          <h3>Đơn hàng {order.id}</h3>
        </div>
        <span className={`order-card-status ${currentStatus.className}`}>
          {currentStatus.label}
        </span>
      </header>

      <div className="order-card-meta">
        <div>
          <span>Người nhận</span>
          <strong>{order.fullName || "Chưa cập nhật"}</strong>
        </div>
        <div>
          <span>Số điện thoại</span>
          <strong>{order.phone || "Chưa cập nhật"}</strong>
        </div>
        <div>
          <span>Thanh toán</span>
          <strong>{order.paymentMethod || "N/A"}</strong>
        </div>
      </div>

      <div className="order-card-items">
        {items.map((item) => (
          <div key={item.id} className="order-card-item">
            <img
              src={
                item.imageUrl ||
                item.productThumbnail ||
                "https://placehold.co/120x120/f1f5f9/0f172a?text=Product"
              }
              alt={item.productName || item.product?.name || "Sản phẩm"}
            />
            <div>
              <h4>{item.productName || item.product?.name || "Sản phẩm"}</h4>
              <span>
                Size: {item.sizeName || item.sizeCode || "N/A"} · Số lượng:{" "}
                {item.quantity}
              </span>
            </div>
            <strong>{formatCurrency(item.total || item.finalPrice)}</strong>

            {status === "COMPLETED" && !reviewedItems[item.id] && (
              <button
                type="button"
                className="order-review-btn"
                onClick={() =>
                  navigate(`/review/${item.id}`, {
                    state: { orderItem: item },
                  })
                }
              >
                <Star size={16} />
                Đánh giá
              </button>
            )}

            {status === "COMPLETED" && reviewedItems[item.id] && (
              <span className="order-reviewed-label">Đã đánh giá</span>
            )}
          </div>
        ))}

        {!items.length && (
          <div className="order-card-empty-item">
            <Package size={18} />
            Chưa có thông tin sản phẩm.
          </div>
        )}
      </div>

      <footer className="order-card-footer">
        <div>
          <span>Tổng tiền</span>
          <strong>{formatCurrency(order.totalAmount)}</strong>
        </div>

        {status === "DELIVERED" && (
          <button type="button" className="order-receive-btn" onClick={handleReceive}>
            <CheckCircle2 size={18} />
            Xác nhận đã nhận hàng
          </button>
        )}

        {status === "PENDING" && (
          <div className="order-card-footer-actions">
            {order.paymentMethod === "VIETQR"
              && order.paymentStatus === "UNPAID" && (
              <button
                type="button"
                className="order-payment-btn"
                onClick={() => navigate(`/orders/${order.id}/payment`)}
              >
                <CreditCard size={18} />
                Thanh toán VietQR
              </button>
            )}

            <button
              type="button"
              className="order-cancel-btn"
              onClick={handleCancel}
            >
              <RotateCcw size={18} />
              Hủy đơn
            </button>
          </div>
        )}
      </footer>
    </article>
  );
}
