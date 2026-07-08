import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  BsBoxSeam,
  BsCalendarCheck,
  BsCashCoin,
  BsCreditCard,
  BsExclamationTriangle,
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
  PAID: "Đã thanh toán",
  UNPAID: "Chưa thanh toán",
  FAILED: "Thất bại",
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

function WorkQueueCard({ icon: Icon, label, value, note, to, tone = "green" }) {
  return (
    <Link className={`dashboard-work-card ${tone}`} to={to}>
      <span className="dashboard-work-icon">
        <Icon />
      </span>
      <span>
        <small>{label}</small>
        <strong>{value}</strong>
        <em>{note}</em>
      </span>
    </Link>
  );
}

function HeroStat({ label, value, tone = "green" }) {
  return (
    <div className={`dashboard-hero-stat ${tone}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function RevenueSourceCard({ icon: Icon, label, value, note, tone = "green" }) {
  return (
    <article className={`dashboard-source-card ${tone}`}>
      <span className="dashboard-source-icon">
        <Icon />
      </span>
      <div>
        <span>{label}</span>
        <strong>{formatMoney(value)}</strong>
        <small>{note}</small>
      </div>
    </article>
  );
}

function EmptyState({ children }) {
  return <div className="dashboard-empty">{children}</div>;
}

function PaymentQueueItem({ payment }) {
  return (
    <article className="dashboard-list-item queue">
      <div>
        <strong>
          {payment.targetType === "BOOKING" ? "Booking" : "Đơn hàng"} #
          {payment.targetId || payment.id}
        </strong>
        <span>{payment.customerName || "Khách hàng"}</span>
        <small>Cập nhật {formatDateTime(payment.updatedAt)}</small>
      </div>
      <div className="dashboard-list-right">
        <strong>{formatMoney(payment.totalAmount)}</strong>
        <StatusBadge status={payment.paymentStatus} />
      </div>
    </article>
  );
}

function LowStockQueueItem({ item }) {
  return (
    <article className="dashboard-list-item queue">
      <div>
        <strong>{item.name}</strong>
        <span>
          {item.itemCode} · {item.itemType}
        </span>
        <small>
          Ngưỡng cảnh báo: {item.lowStockThreshold} {item.unit || ""}
        </small>
      </div>
      <div className="dashboard-stock-count">
        <strong>{item.availableQuantity}</strong>
        <span>{item.unit || "còn"}</span>
      </div>
    </article>
  );
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
        note: "Sân + dịch vụ + sản phẩm hoàn tất",
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
        icon: BsCreditCard,
        label: "VietQR chờ duyệt",
        value: overview?.pendingVietQrPayments || 0,
        note: "Cần xác nhận tiền",
        tone: "violet",
      },
      {
        icon: BsReceipt,
        label: "Đơn hàng mới",
        value: overview?.newOrders || 0,
        note: "Đang chờ xử lý",
        tone: "red",
      },
      {
        icon: BsExclamationTriangle,
        label: "Tồn kho thấp",
        value: overview?.lowStockItems || 0,
        note: "Dưới ngưỡng cảnh báo",
        tone: "slate",
      },
      {
        icon: BsTools,
        label: "Sân đang bảo trì",
        value: overview?.activeMaintenances || 0,
        note: "Đang diễn ra",
        tone: "amber",
      },
    ],
    [overview, summary]
  );

  const workQueues = useMemo(
    () => [
      {
        icon: BsCalendarCheck,
        label: "Booking hôm nay",
        value: overview?.todayBookings || 0,
        note: "Mở lịch sân",
        to: "/admin/field-calendar",
        tone: "green",
      },
      {
        icon: BsCreditCard,
        label: "VietQR chờ duyệt",
        value: overview?.pendingVietQrPayments || 0,
        note: "Xác nhận thanh toán",
        to: "/admin/payments",
        tone: "violet",
      },
      {
        icon: BsReceipt,
        label: "Đơn hàng mới",
        value: overview?.newOrders || 0,
        note: "Xử lý đơn",
        to: "/admin/orders",
        tone: "red",
      },
      {
        icon: BsBoxSeam,
        label: "Tồn kho thấp",
        value: overview?.lowStockItems || 0,
        note: "Kiểm tra kho",
        to: "/admin/inventory",
        tone: "amber",
      },
    ],
    [overview]
  );

  const revenueSources = useMemo(
    () => [
      {
        icon: BsCalendarCheck,
        label: "Doanh thu sân",
        value: overview?.bookingRevenue || summary?.bookingRevenue || 0,
        note: "Booking hoàn tất, chưa gồm dịch vụ thêm",
        tone: "green",
      },
      {
        icon: BsTools,
        label: "Dịch vụ thêm",
        value: overview?.extraServiceRevenue || summary?.extraServiceRevenue || 0,
        note: `${summary?.totalExtraServicesSold || 0} lượt dịch vụ đã bán`,
        tone: "violet",
      },
      {
        icon: BsBoxSeam,
        label: "Sản phẩm",
        value: overview?.orderRevenue || summary?.orderRevenue || 0,
        note: `${summary?.totalProductsSold || 0} sản phẩm đã bán`,
        tone: "amber",
      },
    ],
    [overview, summary]
  );

  const heroStats = useMemo(
    () => [
      {
        label: "Booking hôm nay",
        value: overview?.todayBookings || 0,
        tone: "green",
      },
      {
        label: "VietQR chờ",
        value: overview?.pendingVietQrPayments || 0,
        tone: "violet",
      },
      {
        label: "Tồn kho thấp",
        value: overview?.lowStockItems || 0,
        tone: "amber",
      },
    ],
    [overview]
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
            Theo dõi các việc cần xử lý trong ngày: booking, thanh toán VietQR,
            đơn hàng, bảo trì sân và tồn kho thấp.
          </p>
        </div>

        <div className="dashboard-hero-side">
          <div className="dashboard-hero-stats">
            {heroStats.map((stat) => (
              <HeroStat key={stat.label} {...stat} />
            ))}
          </div>
          <button type="button" onClick={loadDashboard} disabled={loading}>
            {loading ? "Đang làm mới..." : "Làm mới"}
          </button>
        </div>
      </section>

      {error && <div className="dashboard-error">{error}</div>}

      <section className="dashboard-work-queue">
        <div className="dashboard-work-heading">
          <span className="dashboard-eyebrow">Việc cần xử lý</span>
          <h2>Ưu tiên vận hành hôm nay</h2>
        </div>
        <div className="dashboard-work-grid">
          {workQueues.map((queue) => (
            <WorkQueueCard key={queue.label} {...queue} />
          ))}
        </div>
      </section>

      <section className="dashboard-metrics">
        {metrics.map((metric) => (
          <MetricCard key={metric.label} {...metric} />
        ))}
      </section>

      <section className="dashboard-revenue-sources">
        <div className="dashboard-source-heading">
          <span className="dashboard-eyebrow">Cơ cấu doanh thu</span>
          <h2>Doanh thu hoàn tất theo nguồn</h2>
        </div>
        <div className="dashboard-source-grid">
          {revenueSources.map((source) => (
            <RevenueSourceCard key={source.label} {...source} />
          ))}
        </div>
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
            <Link to="/admin/bookings">Mở booking</Link>
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

        <div className="dashboard-panel urgent">
          <div className="dashboard-panel-header">
            <div>
              <h2>Thanh toán VietQR chờ duyệt</h2>
              <p>Ưu tiên xác nhận tiền trước khi xác nhận booking hoặc đơn hàng.</p>
            </div>
            <Link to="/admin/payments">Xem tất cả</Link>
          </div>

          {overview?.pendingPaymentItems?.length ? (
            <div className="dashboard-list">
              {overview.pendingPaymentItems.map((payment) => (
                <PaymentQueueItem key={payment.id} payment={payment} />
              ))}
            </div>
          ) : (
            <EmptyState>Không có thanh toán VietQR nào đang chờ duyệt.</EmptyState>
          )}
        </div>

        <div className="dashboard-panel">
          <div className="dashboard-panel-header">
            <div>
              <h2>Sân đang bảo trì</h2>
              <p>Các lịch bảo trì đang chặn booking.</p>
            </div>
            <Link to="/admin/maintenance">Mở bảo trì</Link>
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
            <Link to="/admin/orders">Mở đơn hàng</Link>
          </div>

          {overview?.newOrderItems?.length ? (
            <div className="dashboard-list">
              {overview.newOrderItems.map((order) => (
                <article key={order.id} className="dashboard-list-item order">
                  <div>
                    <strong>
                      #{order.id} - {order.customerName}
                    </strong>
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

        <div className="dashboard-panel urgent">
          <div className="dashboard-panel-header">
            <div>
              <h2>Tồn kho thấp</h2>
              <p>Các item đã chạm hoặc thấp hơn ngưỡng cảnh báo.</p>
            </div>
            <Link to="/admin/inventory">Mở kho</Link>
          </div>

          {overview?.lowStockItemList?.length ? (
            <div className="dashboard-list">
              {overview.lowStockItemList.map((item) => (
                <LowStockQueueItem key={item.id} item={item} />
              ))}
            </div>
          ) : (
            <EmptyState>Không có item nào dưới ngưỡng cảnh báo.</EmptyState>
          )}
        </div>

        <div className="dashboard-panel chart">
          <RevenueChart data={revenueData} />
        </div>
      </section>
    </div>
  );
}
