import { useEffect, useState } from "react";
import { getDashboardSummary } from "../../services/dashboardService";
import StatCard from "../../components/admin/StatCard";
import "../../assets/styles/AdminDashboard.css";

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getDashboardSummary()
      .then(setSummary)
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="loading">Đang tải dashboard...</p>;

  return (
    <div className="admin-dashboard">
      <h2>📊 Admin Dashboard</h2>

      <div className="stats-grid">
        <StatCard
          title="Người dùng"
          value={summary.totalUsers}
          icon="👤"
          color="#3498db"
        />
        <StatCard
          title="Sản phẩm"
          value={summary.totalProducts}
          icon="⚽"
          color="#2ecc71"
        />
        <StatCard
          title="Đơn hàng"
          value={summary.totalOrders}
          icon="🧾"
          color="#f39c12"
        />
        <StatCard
          title="Sản phẩm đã bán"
          value={summary.totalProductsSold}
          icon="🔥"
          color="#e74c3c"
        />
      </div>

      <div className="revenue-box">
        <h3>💰 Tổng doanh thu</h3>
        <p>{summary.totalRevenue.toLocaleString()} ₫</p>
      </div>
    </div>
  );
}
