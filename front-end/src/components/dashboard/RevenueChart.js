import { useMemo, useState } from "react";
import {
  Bar,
  CartesianGrid,
  ComposedChart,
  Legend,
  Line,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

import "./RevenueChart.css";

const formatMoney = (value) =>
  `${Number(value || 0).toLocaleString("vi-VN")} đ`;

const formatCompactMoney = (value) => {
  const amount = Number(value || 0);
  if (amount >= 1_000_000_000) {
    return `${(amount / 1_000_000_000).toLocaleString("vi-VN", {
      maximumFractionDigits: 1,
    })}B`;
  }
  if (amount >= 1_000_000) {
    return `${(amount / 1_000_000).toLocaleString("vi-VN", {
      maximumFractionDigits: 1,
    })}M`;
  }
  if (amount >= 1_000) {
    return `${Math.round(amount / 1_000)}K`;
  }
  return amount.toLocaleString("vi-VN");
};

function RevenueTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;

  const row = payload[0]?.payload || {};
  return (
    <div className="revenue-tooltip">
      <span>Kỳ {label}</span>
      <div>
        <i className="field" />
        <p>Đặt sân</p>
        <strong>{formatMoney(row.bookingRevenue)}</strong>
      </div>
      <div>
        <i className="shop" />
        <p>Sản phẩm</p>
        <strong>{formatMoney(row.orderRevenue)}</strong>
      </div>
      <div className="total">
        <p>Tổng doanh thu</p>
        <strong>{formatMoney(row.revenue)}</strong>
      </div>
    </div>
  );
}

export default function RevenueChart({ data = [] }) {
  const [period, setPeriod] = useState("12");

  const visibleData = useMemo(() => {
    if (period === "all") return data;
    return data.slice(-Number(period));
  }, [data, period]);

  const summary = useMemo(() => {
    const total = visibleData.reduce(
      (sum, item) => sum + Number(item.revenue || 0),
      0
    );
    const latest = Number(visibleData.at(-1)?.revenue || 0);
    const previous = Number(visibleData.at(-2)?.revenue || 0);
    const growth = previous > 0
      ? ((latest - previous) / previous) * 100
      : null;

    return { total, growth };
  }, [visibleData]);

  return (
    <div className="revenue-chart">
      <div className="revenue-chart-head">
        <div>
          <span className="revenue-chart-eyebrow">Nhịp kinh doanh</span>
          <h2>Doanh thu theo tháng</h2>
          <p>So sánh nguồn thu từ sân bóng và cửa hàng.</p>
        </div>

        <label className="revenue-period">
          <span>Khoảng thời gian</span>
          <select
            value={period}
            onChange={(event) => setPeriod(event.target.value)}
          >
            <option value="6">6 tháng</option>
            <option value="12">12 tháng</option>
            <option value="all">Toàn bộ</option>
          </select>
        </label>
      </div>

      <div className="revenue-scoreboard">
        <div>
          <span>Tổng trong kỳ</span>
          <strong>{formatMoney(summary.total)}</strong>
        </div>
        <div className={summary.growth == null
          ? "neutral"
          : summary.growth >= 0 ? "up" : "down"}
        >
          <span>So với tháng trước</span>
          <strong>
            {summary.growth == null
              ? "Chưa đủ dữ liệu"
              : `${summary.growth >= 0 ? "+" : ""}${summary.growth.toFixed(1)}%`}
          </strong>
        </div>
      </div>

      {visibleData.length === 0 ? (
        <div className="revenue-chart-empty">
          Chưa có booking hoặc đơn hàng hoàn tất để ghi nhận doanh thu.
        </div>
      ) : (
        <div className="revenue-chart-canvas">
          <ResponsiveContainer width="100%" height={390}>
            <ComposedChart
              data={visibleData}
              margin={{ top: 20, right: 10, left: 4, bottom: 0 }}
            >
              <CartesianGrid vertical={false} stroke="#e7edf3" />
              <XAxis
                dataKey="month"
                axisLine={false}
                tickLine={false}
                tick={{ fill: "#64748b", fontSize: 12, fontWeight: 700 }}
              />
              <YAxis
                axisLine={false}
                tickLine={false}
                tickFormatter={formatCompactMoney}
                tick={{ fill: "#64748b", fontSize: 12 }}
                width={52}
              />
              <Tooltip content={<RevenueTooltip />} cursor={{ fill: "#f4f8fb" }} />
              <Legend
                iconType="circle"
                formatter={(value) => (
                  <span className="revenue-legend-label">{value}</span>
                )}
              />
              <Bar
                dataKey="bookingRevenue"
                name="Đặt sân"
                stackId="revenue"
                fill="#16a36a"
                radius={[0, 0, 5, 5]}
                maxBarSize={42}
              />
              <Bar
                dataKey="orderRevenue"
                name="Sản phẩm"
                stackId="revenue"
                fill="#f59e42"
                radius={[7, 7, 0, 0]}
                maxBarSize={42}
              />
              <Line
                type="monotone"
                dataKey="revenue"
                name="Tổng doanh thu"
                stroke="#173f5f"
                strokeWidth={3}
                dot={{ r: 4, fill: "#fff", strokeWidth: 3 }}
                activeDot={{ r: 6 }}
              />
            </ComposedChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  );
}
