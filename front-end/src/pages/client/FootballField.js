import { useEffect, useState } from "react";
import { getFields } from "../../services/fieldService";
import { useNavigate } from "react-router-dom";
import { Button } from "react-bootstrap";
import "../../assets/styles/FootballField.css";

export default function FootballField() {
  const [fields, setFields] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const load = async () => {
      const data = await getFields({ page: 0, size: 10 });
      setFields(data.content || []);
    };
    load();
  }, []);

  return (
    <div className="field-page">
      <h2 className="title">Sân bóng</h2>

      <div className="field-grid">
        {fields.map((f) => (
          <div key={f.id} className="field-card">
            <h3>{f.name}</h3>
            <img src={f.imageUrl} alt={f.name} />
            <p>{f.location}</p>
            <p className="price">Giá: {f.pricePerHour} VND/h</p>
            <Button
              className="btn-custom"
              onClick={() => navigate(`/football-fields/${f.id}/detail`)}
            >
              Xem chi tiết
            </Button>
          </div>
        ))}
      </div>
    </div>
  );
}