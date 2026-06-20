import { useCallback, useEffect, useMemo, useState } from "react";
import {
  BsCalendarCheck,
  BsCashCoin,
  BsClipboardPulse,
  BsReceipt,
  BsTools,
} from "react-icons/bs";

import "../../assets/styles/Dashboard.css";
import RevenueChart from "../../components/dashboard/RevenueChart";
import {
  getAdminOverview,
  getDashboardSummary,
  getMonthlyRevenue,
} from "../../services/dashboardService";

const bookingStatusOptions = [
  { value: "", label: "Tất cả booking" },
  { value: "PENDING", label: "Chờ xác nhận" },
  { value: "CONFIRMED", label: "Đã xác nhận" },
  { value: "COMPLETED", label: "Hoàn tất" },
  { value: "CANCELLED", label: "Đã hủy" },
  { value: "EXPIRED", label: "Hết hạn" },
];

const orderStatusOptions = [
  { value: "", label: "Tất cả đơn hàng" },
  { value: "PENDING", label: "Chờ xác nhận" },
  { value: "CONFIRMED", label: "Đã xác nhận" },
  { value: "SHIPPING", label: "Đang giao" },
  { value: "DELIVERED", label: "Đã giao" },
  { value: "COMPLETED", label: "Hoàn tất" },
  { value: "CANCELLED", label: "Đã hủy" },
];

const statusLabels = {
  PENDING: "Chờ xác nhận",
  CONFIRMED: "Đã xác nhận",
  COMPLETED: "Hoàn tất",
  CANCELLED: "Đã hủy",
  EXPIRED: "Hết hạn",
  SHIPPING: "Đang giao",
  DELIVERED: "Đã giao",
};

const formatMoney = (value) =>
  `${Number(value || 0).toLocaleString("vi-VN")} đ`;

const formatDateTime = (value) =>
  value
    ? new Date(value).toLocaleString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      })
    : "-";

const formatTimeRange = (startTime, endTime) =>
  `${formatDateTime(startTime)} - ${
    endTime
      ? new Date(endTime).toLocaleTimeString("vi-VN", {
          hour: "2-digit",
          minute: "2-digit",
        })
      : "-"
  }`;

function StatusBadge({ status }) {
  return (
    <span className={`dashboard-badge ${String(status || "").toLowerCase()}`}>
      {statusLabels[status] || status || "-"}
    </span>
  );
}

function MetricCard({ icon: Icon, label, value, note, tone = "green" }) {
  return (
    <article className={`dashboard-metric ${tone}`}>
      <div className="dashboard-metric-icon">
        <Icon />
      </div>
      <div>
        <span>{label}</span>
        <strong>{value}</strong>
        {note && <small>{note}</small>}
      </div>
    </article>
  );
}

function EmptyState({ children }) {
  return <div className="dashboard-empty">{children}</div>;
}

export default function DashboardPage() {
  const [summary, setSummary] = useState(null);
  const [overview, setOverview] = useState(null);
  const [revenueData, setRevenueData] = useState([]);
  const [bookingStatus, setBookingStatus] = useState("");
  const [orderStatus, setOrderStatus] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadDashboard = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [summaryRes, overviewRes, revenueRes] = await Promise.all([
        getDashboardSummary(),
        getAdminOverview({ bookingStatus, orderStatus }),
        getMonthlyRevenue(),
      ]);

      setSummary(summaryRes);
      setOverview(overviewRes);
      setRevenueData(revenueRes || []);
    } catch (err) {
      setError(err.message || "Không thể tải dashboard.");
    } finally {
      setLoading(false);
    }
  }, [bookingStatus, orderStatus]);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  const metrics = useMemo(
    () => [
      {
        icon: BsCashCoin,
        label: "Tổng doanh thu",
        value: formatMoney(overview?.totalRevenue || summary?.totalRevenue),
        note: "Đơn hàng đã hoàn tất",
        tone: "green",
      },
      {
        icon: BsCalendarCheck,
        label: "Booking hôm nay",
        value: overview?.todayBookings || 0,
        note: formatMoney(overview?.todayBookingRevenue),
        tone: "blue",
      },
      {
        icon: BsTools,
        label: "Sân đang bảo trì",
        value: overview?.activeMaintenances || 0,
        note: "Đang diễn ra",
        tone: "amber",
      },
      {
        icon: BsReceipt,
        label: "Đơn hàng mới",
        value: overview?.newOrders || 0,
        note: "Đang chờ xử lý",
        tone: "red",
      },
      {
        icon: BsClipboardPulse,
        label: "Tổng lượt đặt sân",
        value: summary?.totalBooking || 0,
        note: "Toàn hệ thống",
        tone: "slate",
      },
    ],
    [overview, summary]
  );

  if (loading && !overview) {
    return <div className="dashboard-loading">Đang tải dashboard...</div>;
  }

  return (
    <div className="dashboard-page">
      <section className="dashboard-hero">
        <div>
          <span className="dashboard-eyebrow">Trung tâm vận hành</span>
          <h1>Tổng quan hệ thống</h1>
          <p>
            Theo dõi doanh thu, booking trong ngày, sân đang bảo trì và các đơn
            hàng cần xử lý.
          </p>
        </div>

        <button type="button" onClick={loadDashboard} disabled={loading}>
          {loading ? "Đang làm mới..." : "Làm mới"}
        </button>
      </section>

      {error && <div className="dashboard-error">{error}</div>}

      <section className="dashboard-metrics">
        {metrics.map((metric) => (
          <MetricCard key={metric.label} {...metric} />
        ))}
      </section>

      <section className="dashboard-filters">
        <label>
          <span>Trạng thái booking</span>
          <select
            value={bookingStatus}
            onChange={(event) => setBookingStatus(event.target.value)}
          >
            {bookingStatusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label>
          <span>Trạng thái đơn hàng</span>
          <select
            value={orderStatus}
            onChange={(event) => setOrderStatus(event.target.value)}
          >
            {orderStatusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </section>

      <section className="dashboard-grid">
        <div className="dashboard-panel wide">
          <div className="dashboard-panel-header">
            <div>
              <h2>Booking hôm nay</h2>
              <p>Danh sách slot trong ngày theo bộ lọc trạng thái.</p>
            </div>
          </div>

          {overview?.todayBookingItems?.length ? (
            <div className="dashboard-table-wrap">
              <table className="dashboard-table">
                <thead>
                  <tr>
                    <th>Mã</th>
                    <th>Người đặt</th>
                    <th>Sân</th>
                    <th>Khung giờ</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  {overview.todayBookingItems.map((booking) => (
                    <tr key={booking.id}>
                      <td>#{booking.id}</td>
                      <td>{booking.userName}</td>
                      <td>{booking.fieldName}</td>
                      <td>{formatTimeRange(booking.startTime, booking.endTime)}</td>
                      <td>{formatMoney(booking.totalPrice)}</td>
                      <td>
                        <StatusBadge status={booking.status} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <EmptyState>Không có booking nào phù hợp hôm nay.</EmptyState>
          )}
        </div>

        <div className="dashboard-panel">
          <div className="dashboard-panel-header">
            <div>
              <h2>Sân đang bảo trì</h2>
              <p>Các lịch bảo trì đang chặn booking.</p>
            </div>
          </div>

          {overview?.activeMaintenanceItems?.length ? (
            <div className="dashboard-list">
              {overview.activeMaintenanceItems.map((item) => (
                <article key={item.id} className="dashboard-list-item">
                  <strong>{item.fieldName}</strong>
                  <span>{item.reason || "Bảo trì sân"}</span>
                  <small>{formatTimeRange(item.startTime, item.endTime)}</small>
                </article>
              ))}
            </div>
          ) : (
            <EmptyState>Hiện không có sân nào đang bảo trì.</EmptyState>
          )}
        </div>

        <div className="dashboard-panel">
          <div className="dashboard-panel-header">
            <div>
              <h2>Đơn hàng mới</h2>
              <p>Các đơn hàng gần đây theo bộ lọc trạng thái.</p>
            </div>
          </div>

          {overview?.newOrderItems?.length ? (
            <div className="dashboard-list">
              {overview.newOrderItems.map((order) => (
                <article key={order.id} className="dashboard-list-item order">
                  <div>
                    <strong>#{order.id} - {order.customerName}</strong>
                    <span>{order.phone || "Chưa có số điện thoại"}</span>
                  </div>
                  <div className="dashboard-list-right">
                    <strong>{formatMoney(order.totalAmount)}</strong>
                    <StatusBadge status={order.orderStatus} />
                  </div>
                </article>
              ))}
            </div>
          ) : (
            <EmptyState>Không có đơn hàng phù hợp.</EmptyState>
          )}
        </div>

        <div className="dashboard-panel chart">
          <RevenueChart data={revenueData} />
        </div>
      </section>
    </div>
  );
}
