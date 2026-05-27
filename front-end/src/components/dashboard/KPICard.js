import "./KPICard.css";

export default function KPICard({
    title,
    value
}) {

    return (

        <div className="kpi-card">

            <h3 className="kpi-title">
                {title}
            </h3>

            <h1 className="kpi-value">
                {value}
            </h1>

        </div>
    );
}