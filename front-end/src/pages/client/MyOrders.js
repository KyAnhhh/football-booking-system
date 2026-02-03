import { useEffect, useState } from "react";
import { getMyOrders } from "../../services/orderService";
import OrderCard from "./OrderCard";
import "../../assets/styles/MyOrders.css"


export default function MyOrders() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    async function fetchOrders() {
      try {
        const data = await getMyOrders();
        console.log("MY ORDERS:", data);

        setOrders(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Get my orders error:", error);
        setOrders([]);
      }
    }

    fetchOrders();
  }, []);

  return (
    <div className="my-orders">
      <h2><center>Đơn hàng của tôi</center></h2>

      {orders.length === 0 && <p className="empty-orders">Bạn chưa có đơn hàng nào</p>}

      {orders.map(order => (
        <OrderCard
          key={order.id}
          order={order}
        />
      ))}
    </div>
  );
}
