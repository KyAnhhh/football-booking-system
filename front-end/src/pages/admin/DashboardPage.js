import { useEffect, useState } from "react";

import "../../assets/styles/Dashboard.css";

import KPICard from "../../components/dashboard/KPICard";
import "../../../src/assets/styles/Dashboard.css";

import RevenueChart
from "../../components/dashboard/RevenueChart";

import {
    getDashboardSummary,
    getMonthlyRevenue
} from "../../services/dashboardService";

export default function DashboardPage() {

    const [summary, setSummary]
        = useState(null);

    const [revenueData, setRevenueData]
        = useState([]);

    useEffect(() => {

        fetchDashboard();

    }, []);

    const fetchDashboard = async () => {

        try {

            const summaryRes =
                await getDashboardSummary();

            const revenueRes =
                await getMonthlyRevenue();

            console.log(summaryRes);
            console.log(revenueRes);

            setSummary(summaryRes);

            setRevenueData(revenueRes);

        } catch (error) {

            console.log(error);
        }
    };

    if (!summary) {

        return <h1>Loading...</h1>;
    }

    return (

        <div className="dashboard-page">

            {/* HEADER */}

            <div className="dashboard-header">

                <h1>
                    Dashboard
                </h1>

                <p>
                    Football Shop Analytics
                </p>

            </div>

            {/* KPI GRID */}

            <div className="kpi-grid">

                <KPICard
                    title="Users"
                    value={summary.totalUsers}
                />

                <KPICard
                    title="Orders"
                    value={summary.totalOrders}
                />

                <KPICard
                    title="Products"
                    value={summary.totalProducts}
                />

                <KPICard
                    title="Revenue"
                    value={`₫${summary.totalRevenue}`}
                />

            </div>

            {/* CHART */}

            <RevenueChart
                data={revenueData}
            />

        </div>
    );
}