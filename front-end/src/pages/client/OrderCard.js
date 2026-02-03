import "../../assets/styles/OrderCard.css";

export default function OrderCard({ order, openQr }) {
  return (
    <div className="order-card">
      <div className="order-header">
        <span className="order-id">#ORDER-{order.id}</span>

        <span
          className={`order-status ${
            order.orderStatus === "PENDING"
              ? "status-pending"
              : order.orderStatus === "COMPLETED"
              ? "status-success"
              : "status-cancel"
          }`}
        >
          {order.orderStatus}
        </span>
      </div>

      <div className="order-info">
        <p>Người nhận: {order.fullName}</p>
        <p>SĐT: {order.phone}</p>
        <p>Thanh toán: {order.paymentMethod}</p>
      </div>

      <div className="order-total">
        Tổng tiền: {order.totalAmount?.toLocaleString()} ₫
      </div>
    </div>
  );
}
