import { useEffect, useState } from "react";
import { getMyOrders } from "../../services/orderService";
import { getMyRefunds } from "../../services/refundService";
import OrderCard from "../../components/order/OrderCard";
import "../../assets/styles/MyOrders.css";
import { PackageCheck } from "lucide-react";

export default function MyOrders() {
  const [orders, setOrders] = useState([]);
  const [refunds, setRefunds] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchOrders() {
      try {
        const [data, refundData] = await Promise.all([
          getMyOrders(),
          getMyRefunds().catch(() => []),
        ]);
        setOrders(Array.isArray(data) ? data : []);
        setRefunds(Array.isArray(refundData) ? refundData : []);
      } catch (error) {
        console.error("Get my orders error:", error);
        setOrders([]);
      } finally {
        setLoading(false);
      }
    }

    fetchOrders();
  }, []);

  return (
    <div className="my-orders-page">
      <section className="my-orders-hero">
        <div>
          <p>Đơn hàng</p>
          <h1>Đơn hàng của tôi</h1>
          <span>Theo dõi trạng thái, xác nhận đã nhận hàng và đánh giá sản phẩm.</span>
        </div>
      </section>

      {loading ? (
        <div className="my-orders-state">Đang tải đơn hàng...</div>
      ) : orders.length === 0 ? (
        <div className="my-orders-state">
          <PackageCheck size={34} />
          <h2>Bạn chưa có đơn hàng nào</h2>
          <p>Các đơn hàng sau khi thanh toán sẽ xuất hiện tại đây.</p>
        </div>
      ) : (
        <div className="my-orders-list">
          {orders.map((order) => (
            <OrderCard
              key={order.id}
              order={order}
              refund={refunds.find((item) => item.orderId === order.id)}
            />
          ))}
        </div>
      )}
    </div>
  );
}
