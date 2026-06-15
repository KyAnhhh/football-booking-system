import { useEffect, useState } from "react";
import { getFields, getMaintenanceStatus } from "../../services/fieldService";
import { useNavigate } from "react-router-dom";
import { Button, Form } from "react-bootstrap";
import "../../assets/styles/FootballField.css";

export default function FootballField() {

  const [fields, setFields] = useState([]);
  const [maintenanceMap, setMaintenanceMap] = useState({});

  const [keyword, setKeyword] = useState("");

  const [filters, setFilters] = useState({
    type: "",
    minPrice: "",
    maxPrice: "",
  });

  const navigate = useNavigate();

  // LOAD FIELDS
  const loadFields = async (
    customKeyword = keyword,
    customFilters = filters
  ) => {

    try {

      const params = {
        page: 0,
        size: 10,
      };

      // KEYWORD
      if (customKeyword?.trim()) {
        params.keyword = customKeyword.trim();
      }

      // TYPE
      if (customFilters.type) {
        params.type = customFilters.type;
      }

      // PRICE
      if (customFilters.minPrice) {
        params.minPrice = customFilters.minPrice;
      }

      if (customFilters.maxPrice) {
        params.maxPrice = customFilters.maxPrice;
      }

      console.log("PARAMS:", params);

      const data = await getFields(params);

      console.log("DATA:", data);

      setFields(data.content || []);

      // Load maintenance status for each field
      if (data.content && data.content.length > 0) {
        const maintenancePromises = data.content.map(async (field) => {
          try {
            const maintenanceStatus = await getMaintenanceStatus(field.id);
            // Fix: Trích xuất đúng object data bên trong response nếu API trả về bọc qua một lớp data vỏ
            const maintenanceData = maintenanceStatus.data ? maintenanceStatus.data : maintenanceStatus;
            return { fieldId: field.id, ...maintenanceData };
          } catch (error) {
            console.error(`Failed to load maintenance for field ${field.id}:`, error);
            return { fieldId: field.id, underMaintenance: false };
          }
        });

        const maintenanceResults = await Promise.all(maintenancePromises);
        const newMaintenanceMap = {};
        maintenanceResults.forEach((result) => {
          newMaintenanceMap[result.fieldId] = result;
        });
        setMaintenanceMap(newMaintenanceMap);
      }

    } catch (error) {
      console.error(error);
    }
  };

  // LOAD FIRST
  useEffect(() => {
    loadFields();
  }, []);

  // AUTO SEARCH
  useEffect(() => {

    const debounce = setTimeout(() => {
      loadFields(keyword, filters);
    }, 400);

    return () => clearTimeout(debounce);

  }, [keyword, filters]);

  // PRICE FILTER
  const handlePriceFilter = (value) => {

    let newFilters = {
      ...filters,
      minPrice: "",
      maxPrice: "",
    };

    if (value === "UNDER_200") {
      newFilters.maxPrice = 200000;
    }

    if (value === "200_500") {
      newFilters.minPrice = 200000;
      newFilters.maxPrice = 500000;
    }

    if (value === "OVER_500") {
      newFilters.minPrice = 500000;
    }

    setFilters(newFilters);
  };

  // TYPE FILTER
  const handleTypeFilter = (value) => {

    const newFilters = {
      ...filters,
      type: value,
    };

    setFilters(newFilters);
  };

  return (
    <div className="field-page">

      <h2 className="title">Sân bóng</h2>

      <div className="field-layout">

        {/* SIDEBAR */}
        <div className="field-sidebar">

          <h4>Bộ lọc</h4>

          {/* SEARCH */}
          <Form.Group className="mb-4">

            <Form.Label>Tìm sân</Form.Label>

            <Form.Control
              type="text"
              placeholder="Nhập tên sân..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />

          </Form.Group>

          <h6>Loại sân</h6>
          <Button
            variant={filters.type === "" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("")}
          >
            Tất cả
          </Button>

          <Button
            variant={filters.type === "FIVE_A_SIDE" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("FIVE_A_SIDE")}
          >
            Sân 5
          </Button>

          <Button
            variant={filters.type === "SEVEN_A_SIDE" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("SEVEN_A_SIDE")}
          >
            Sân 7
          </Button>

          <Button
            variant={filters.type === "ELEVEN_A_SIDE" ? "primary" : "outline-dark"}
            onClick={() => handleTypeFilter("ELEVEN_A_SIDE")}
          >
            Sân 11
          </Button>

          <h6>Mức giá</h6>
          <Button
            variant={
              filters.maxPrice === 200000 &&
              !filters.minPrice
                ? "primary"
                : "outline-dark"
            }
            onClick={() => handlePriceFilter("UNDER_200")}
          >
            Dưới 200k
          </Button>

          <Button
            variant={
              filters.minPrice === 200000 &&
              filters.maxPrice === 500000
                ? "primary"
                : "outline-dark"
            }
            onClick={() => handlePriceFilter("200_500")}
          >
            200k - 500k
          </Button>

          <Button
            variant={
              filters.minPrice === 500000 &&
              !filters.maxPrice
                ? "primary"
                : "outline-dark"
            }
            onClick={() => handlePriceFilter("OVER_500")}
          >
            Trên 500k
          </Button>
        </div>

        {/* FIELD LIST */}
        <div className="field-grid">

          {fields.length > 0 ? (

            fields.map((f) => {
              // Use maintenance info from API response if available, otherwise from maintenanceMap
              const maintenanceFromApi = f.underMaintenance ? f : null;
              const maintenanceFromMap = maintenanceMap[f.id];
              const maintenance = maintenanceFromApi || maintenanceFromMap;
              // Fix: Thay đổi từ .isUnderMaintenance sang .underMaintenance theo đúng cấu trúc JSON trả về
              const isUnderMaintenance = maintenance?.underMaintenance || false;
              const maintenanceStartTime = maintenance?.maintenanceStartTime;
              const maintenanceEndTime = maintenance?.maintenanceEndTime;
              const maintenanceReason = maintenance?.maintenanceReason;

              return (
                <div key={f.id} className={`field-card ${isUnderMaintenance ? 'under-maintenance' : ''}`}>
                  {isUnderMaintenance && (
                    <div className="maintenance-badge">
                      <span className="badge-dot" /> Đang bảo trì
                    </div>
                  )}

                  <h3>{f.name}</h3>

                  <img
                    src={f.imageUrl}
                    alt={f.name}
                  />

                  <p>{f.address}</p>

                  <p className="field-price">
                    Giá: {f.pricePerHour} VND/h
                  </p>

                  <Button
                    className="btn-custom"
                    disabled={isUnderMaintenance}
                    onClick={() =>
                      !isUnderMaintenance && navigate(`/football-fields/${f.id}/detail`)
                    }
                    title={isUnderMaintenance ? `Đang bảo trì từ ${new Date(maintenanceStartTime).toLocaleString('vi-VN')} đến ${new Date(maintenanceEndTime).toLocaleString('vi-VN')}. Lý do: ${maintenanceReason}` : ""}
                    aria-disabled={isUnderMaintenance}
                  >
                    {isUnderMaintenance ? "Đang bảo trì" : "Xem chi tiết"}
                  </Button>

                  {isUnderMaintenance && maintenanceStartTime && maintenanceEndTime && (
                    <div className="maintenance-info">
                      <span className="info-icon">⚠️</span>
                      <span>Bảo trì từ: {new Date(maintenanceStartTime).toLocaleString('vi-VN')} đến {new Date(maintenanceEndTime).toLocaleString('vi-VN')}</span>
                      {maintenanceReason && (
                        <span className="info-reason">
                          <br />Lý do: {maintenanceReason}
                        </span>
                      )}
                    </div>
                  )}

                  {!isUnderMaintenance && (
                    <div className="status-active">
                      <span className="status-dot" /> Đang hoạt động
                    </div>
                  )}
                </div>
              );
            })

          ) : (

            <div className="no-data">
              Không tìm thấy sân phù hợp
            </div>

          )}

        </div>
      </div>
    </div>
  );
}