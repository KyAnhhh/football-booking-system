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

    return (

        <div className="revenue-chart">

            <h2 className="revenue-chart-title">
                Monthly Revenue
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

                    <Tooltip />

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