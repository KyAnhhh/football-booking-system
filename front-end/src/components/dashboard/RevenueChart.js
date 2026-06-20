import "./RevenueChart.css";

import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ResponsiveContainer
} from "recharts";

export default function RevenueChart({
    data
}) {
    const formatMoney = (value) =>
        `${Number(value || 0).toLocaleString("vi-VN")} đ`;

    return (

        <div className="revenue-chart">

            <h2 className="revenue-chart-title">
                Doanh thu theo tháng
            </h2>

            <ResponsiveContainer
                width="100%"
                height={400}
            >

                <LineChart data={data}>

                    <CartesianGrid
                        strokeDasharray="3 3"
                    />

                    <XAxis dataKey="month" />

                    <YAxis />

                    <Tooltip formatter={(value) => formatMoney(value)} />

                    <Line
                        type="monotone"
                        dataKey="revenue"
                        strokeWidth={4}
                    />

                </LineChart>

            </ResponsiveContainer>

        </div>
    );
}
