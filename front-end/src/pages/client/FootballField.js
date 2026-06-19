import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Form } from "react-bootstrap";
import { MapPin, Search, ShieldCheck, SlidersHorizontal, Wrench } from "lucide-react";
import { getFields, getMaintenanceStatus } from "../../services/fieldService";
import "../../assets/styles/FootballField.css";

const fieldTypes = [
  { label: "Tat ca", value: "" },
  { label: "San 5", value: "FIVE_A_SIDE" },
  { label: "San 7", value: "SEVEN_A_SIDE" },
  { label: "San 11", value: "ELEVEN_A_SIDE" },
];

const priceRanges = [
  { label: "Tat ca", value: "" },
  { label: "Duoi 200k", value: "UNDER_200" },
  { label: "200k - 500k", value: "200_500" },
  { label: "Tren 500k", value: "OVER_500" },
];

const formatMoney = (value) => `${Number(value || 0).toLocaleString("vi-VN")} VND`;

export default function FootballField() {
  const navigate = useNavigate();
  const [fields, setFields] = useState([]);
  const [maintenanceMap, setMaintenanceMap] = useState({});
  const [keyword, setKeyword] = useState("");
  const [priceRange, setPriceRange] = useState("");
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState({
    type: "",
    minPrice: "",
    maxPrice: "",
  });

  const activeFilterCount = useMemo(() => {
    return [keyword.trim(), filters.type, priceRange].filter(Boolean).length;
  }, [keyword, filters.type, priceRange]);

  const loadFields = async (customKeyword = keyword, customFilters = filters) => {
    try {
      setLoading(true);

      const params = {
        page: 0,
        size: 24,
      };

      if (customKeyword?.trim()) params.keyword = customKeyword.trim();
      if (customFilters.type) params.type = customFilters.type;
      if (customFilters.minPrice) params.minPrice = customFilters.minPrice;
      if (customFilters.maxPrice) params.maxPrice = customFilters.maxPrice;

      const data = await getFields(params);
      const content = data?.content || [];
      setFields(content);

      if (content.length === 0) {
        setMaintenanceMap({});
        return;
      }

      const maintenanceResults = await Promise.all(
        content.map(async (field) => {
          try {
            const status = await getMaintenanceStatus(field.id);
            const maintenanceData = status?.data || status || {};
            return { fieldId: field.id, ...maintenanceData };
          } catch (error) {
            console.error(`Failed to load maintenance for field ${field.id}:`, error);
            return { fieldId: field.id, underMaintenance: false };
          }
        })
      );

      setMaintenanceMap(
        maintenanceResults.reduce((map, item) => {
          map[item.fieldId] = item;
          return map;
        }, {})
      );
    } catch (error) {
      console.error(error);
      setFields([]);
      setMaintenanceMap({});
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const debounce = setTimeout(() => {
      loadFields(keyword, filters);
    }, 350);

    return () => clearTimeout(debounce);
  }, [keyword, filters]);

  const handlePriceFilter = (value) => {
    const nextFilters = {
      ...filters,
      minPrice: "",
      maxPrice: "",
    };

    if (value === "UNDER_200") nextFilters.maxPrice = 200000;
    if (value === "200_500") {
      nextFilters.minPrice = 200000;
      nextFilters.maxPrice = 500000;
    }
    if (value === "OVER_500") nextFilters.minPrice = 500000;

    setPriceRange(value);
    setFilters(nextFilters);
  };

  const handleTypeFilter = (value) => {
    setFilters((current) => ({
      ...current,
      type: value,
    }));
  };

  const resetFilters = () => {
    setKeyword("");
    setPriceRange("");
    setFilters({
      type: "",
      minPrice: "",
      maxPrice: "",
    });
  };

  return (
    <div className="football-page">
      <section className="football-hero">
        <div>
          <span className="football-eyebrow">Đặt sân nhanh</span>
          <h1>Chọn sân bóng phù hợp cho trận đấu tiếp theo</h1>
          <p>Lọc theo loại sân, mức giá và trạng thái bảo trì để đặt lịch nhanh hơn.</p>
        </div>
        <div className="football-hero-stat">
          <strong>{fields.length}</strong>
          <span>sân đang hiển thị</span>
        </div>
      </section>

      <div className="football-layout">
        <aside className="football-filters">
          <div className="football-filter-heading">
            <SlidersHorizontal size={20} />
            <h2>Bộ lọc</h2>
          </div>

          <Form.Group className="football-search">
            <Form.Label>Tìm sân</Form.Label>
            <div className="football-search-control">
              <Search size={18} />
              <Form.Control
                type="text"
                placeholder="Nhập tên sân..."
                value={keyword}
                onChange={(event) => setKeyword(event.target.value)}
              />
            </div>
          </Form.Group>

          <div className="football-filter-block">
            <h3>Loại sân</h3>
            <div className="football-segments">
              {fieldTypes.map((type) => (
                <button
                  key={type.value}
                  type="button"
                  className={filters.type === type.value ? "active" : ""}
                  onClick={() => handleTypeFilter(type.value)}
                >
                  {type.label}
                </button>
              ))}
            </div>
          </div>

          <div className="football-filter-block">
            <h3>Mức giá</h3>
            <div className="football-segments">
              {priceRanges.map((range) => (
                <button
                  key={range.value}
                  type="button"
                  className={priceRange === range.value ? "active" : ""}
                  onClick={() => handlePriceFilter(range.value)}
                >
                  {range.label}
                </button>
              ))}
            </div>
          </div>

          {activeFilterCount > 0 && (
            <Button className="football-reset" type="button" onClick={resetFilters}>
              Xóa bộ lọc
            </Button>
          )}
        </aside>

        <main className="football-results">
          {loading ? (
            <div className="football-empty">Đang tải danh sách sân...</div>
          ) : fields.length === 0 ? (
            <div className="football-empty">Không tìm thấy sân phù hợp</div>
          ) : (
            <div className="football-grid">
              {fields.map((field) => {
                const maintenance = field.underMaintenance ? field : maintenanceMap[field.id];
                const isUnderMaintenance = maintenance?.underMaintenance || false;
                const maintenanceStartTime = maintenance?.maintenanceStartTime;
                const maintenanceEndTime = maintenance?.maintenanceEndTime;
                const maintenanceReason = maintenance?.maintenanceReason;

                return (
                  <article key={field.id} className={`football-card ${isUnderMaintenance ? "maintenance" : ""}`}>
                    <div className="football-card-media">
                      <img src={field.imageUrl || "/no-image.png"} alt={field.name} />
                      <span className={`football-status ${isUnderMaintenance ? "danger" : "success"}`}>
                        {isUnderMaintenance ? (
                          <>
                            <Wrench size={14} /> Bảo trì
                          </>
                        ) : (
                          <>
                            <ShieldCheck size={14} /> Sẵn sàng
                          </>
                        )}
                      </span>
                    </div>

                    <div className="football-card-body">
                      <div className="football-card-top">
                        <h2>{field.name}</h2>
                        <span>{field.type}</span>
                      </div>

                      <p className="football-address">
                        <MapPin size={16} />
                        {field.address || "Chưa có địa chỉ"}
                      </p>

                      <div className="football-price-row">
                        <span>Giá tham khảo</span>
                        <strong>{formatMoney(field.pricePerHour)}</strong>
                      </div>

                      {isUnderMaintenance && maintenanceStartTime && maintenanceEndTime && (
                        <div className="football-maintenance-note">
                          <strong>Bảo trì:</strong>{" "}
                          {new Date(maintenanceStartTime).toLocaleString("vi-VN")} -{" "}
                          {new Date(maintenanceEndTime).toLocaleString("vi-VN")}
                          {maintenanceReason && <span>Lý do: {maintenanceReason}</span>}
                        </div>
                      )}

                      <Button
                        className="football-card-action"
                        disabled={isUnderMaintenance}
                        onClick={() => navigate(`/football-fields/${field.id}/detail`)}
                      >
                        {isUnderMaintenance ? "Đang bảo trì" : "Xem lịch sân"}
                      </Button>
                    </div>
                  </article>
                );
              })}
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
