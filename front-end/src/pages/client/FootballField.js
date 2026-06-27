import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Button, Form } from "react-bootstrap";
import {
  CalendarDays,
  Clock,
  MapPin,
  Sparkles,
  Search,
  ShieldCheck,
  SlidersHorizontal,
  Wrench,
} from "lucide-react";
import { getAvailableFields, getFieldSuggestions, getFields, getMaintenanceStatus } from "../../services/fieldService";
import "../../assets/styles/FootballField.css";

const fieldTypes = [
  { label: "Tất cả", value: "" },
  { label: "Sân 5", value: "FIVE_A_SIDE" },
  { label: "Sân 7", value: "SEVEN_A_SIDE" },
  { label: "Sân 11", value: "ELEVEN_A_SIDE" },
];

const priceRanges = [
  { label: "Tất cả", value: "" },
  { label: "Dưới 200k", value: "UNDER_200" },
  { label: "200k - 500k", value: "200_500" },
  { label: "Trên 500k", value: "OVER_500" },
];

const PAGE_SIZE = 9;
const OPEN_MINUTE = 7 * 60;
const CLOSE_MINUTE = 23 * 60 + 45;
const SLOT_MINUTES = 90;

const formatMoney = (value) => `${Number(value || 0).toLocaleString("vi-VN")} VND`;
const todayValue = () => new Date().toISOString().split("T")[0];

const toTimeLabel = (minute) => {
  const hour = String(Math.floor(minute / 60)).padStart(2, "0");
  const minutes = String(minute % 60).padStart(2, "0");
  return `${hour}:${minutes}`;
};

const slotOptions = Array.from(
  { length: Math.floor((CLOSE_MINUTE - OPEN_MINUTE) / SLOT_MINUTES) },
  (_, index) => {
    const startMinute = OPEN_MINUTE + index * SLOT_MINUTES;
    const endMinute = startMinute + SLOT_MINUTES;

    return {
      label: `${toTimeLabel(startMinute)} - ${toTimeLabel(endMinute)}`,
      startTime: toTimeLabel(startMinute),
      endTime: toTimeLabel(endMinute),
      value: `${toTimeLabel(startMinute)}|${toTimeLabel(endMinute)}`,
    };
  }
);

const getPageMetadata = (data, fallbackCount) => {
  const page = data?.page || data || {};

  return {
    totalPages: Number(page.totalPages || 0),
    totalElements: Number(page.totalElements || fallbackCount || 0),
  };
};

export default function FootballField() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const initialStartTime = searchParams.get("startTime") || "";
  const initialEndTime = searchParams.get("endTime") || "";
  const initialSlotValue =
    initialStartTime && initialEndTime
      ? `${initialStartTime}|${initialEndTime}`
      : "";
  const [fields, setFields] = useState([]);
  const [maintenanceMap, setMaintenanceMap] = useState({});
  const [keyword, setKeyword] = useState("");
  const [priceRange, setPriceRange] = useState("");
  const [selectedDate, setSelectedDate] = useState(
    searchParams.get("date") || ""
  );
  const [selectedSlotValue, setSelectedSlotValue] = useState(
    slotOptions.some((slot) => slot.value === initialSlotValue)
      ? initialSlotValue
      : ""
  );
  const [loading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [suggestions, setSuggestions] = useState([]);
  const [filters, setFilters] = useState({
    type: searchParams.get("type") || "",
    minPrice: "",
    maxPrice: "",
  });

  const selectedSlot = useMemo(
    () => slotOptions.find((slot) => slot.value === selectedSlotValue),
    [selectedSlotValue]
  );

  const isAvailabilitySearch = Boolean(selectedDate && selectedSlot);

  const activeFilterCount = useMemo(() => {
    return [
      keyword.trim(),
      filters.type,
      priceRange,
      selectedDate,
      selectedSlotValue,
    ].filter(Boolean).length;
  }, [keyword, filters.type, priceRange, selectedDate, selectedSlotValue]);

  const loadFields = useCallback(async (customKeyword, customFilters, customPageNo, date, slot) => {
    try {
      setLoading(true);

      const params = {
        page: customPageNo,
        size: PAGE_SIZE,
      };

      if (customKeyword?.trim()) params.keyword = customKeyword.trim();
      if (customFilters.type) params.type = customFilters.type;
      if (customFilters.minPrice) params.minPrice = customFilters.minPrice;
      if (customFilters.maxPrice) params.maxPrice = customFilters.maxPrice;

      const data = date && slot
        ? await getAvailableFields({
            ...params,
            date,
            startTime: slot.startTime,
            endTime: slot.endTime,
          })
        : await getFields(params);

      const content = data?.content || [];
      const pageMetadata = getPageMetadata(data, content.length);

      setFields(content);
      setTotalPages(pageMetadata.totalPages);
      setTotalElements(pageMetadata.totalElements);

      if (date && slot) {
        if (content.length === 0) {
          try {
            const suggestionData = await getFieldSuggestions({
              date,
              startTime: slot.startTime,
              endTime: slot.endTime,
              fieldType: customFilters.type,
            });
            setSuggestions(suggestionData?.suggestions || []);
          } catch (error) {
            console.error("Load field suggestions failed:", error);
            setSuggestions([]);
          }
        } else {
          setSuggestions([]);
        }
        setMaintenanceMap({});
        return;
      }

      if (content.length === 0) {
        setMaintenanceMap({});
        setSuggestions([]);
        return;
      }

      setSuggestions([]);

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
      setSuggestions([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const debounce = setTimeout(() => {
      loadFields(keyword, filters, pageNo, selectedDate, selectedSlot);
    }, 350);

    return () => clearTimeout(debounce);
  }, [keyword, filters, pageNo, selectedDate, selectedSlot, loadFields]);

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
    setPageNo(0);
  };

  const handleTypeFilter = (value) => {
    setFilters((current) => ({
      ...current,
      type: value,
    }));
    setPageNo(0);
  };

  const resetFilters = () => {
    setKeyword("");
    setPriceRange("");
    setSelectedDate("");
    setSelectedSlotValue("");
    setFilters({
      type: "",
      minPrice: "",
      maxPrice: "",
    });
    setPageNo(0);
  };

  const goToFieldDetail = (field) => {
    const query = isAvailabilitySearch
      ? `?date=${selectedDate}&startTime=${selectedSlot.startTime}&endTime=${selectedSlot.endTime}`
      : "";

    navigate(`/football-fields/${field.id}/detail${query}`);
  };

  const goToSuggestion = (suggestion) => {
    navigate(
      `/football-fields/${suggestion.fieldId}/detail?date=${suggestion.date}&startTime=${suggestion.startTime}&endTime=${suggestion.endTime}`
    );
  };

  return (
    <div className="football-page">
      <section className="football-hero">
        <div>
          <span className="football-eyebrow">Đặt sân nhanh</span>
          <h1>Chọn sân bóng phù hợp cho trận đấu tiếp theo</h1>
          <p>
            Lọc theo ngày, khung giờ, loại sân và mức giá để chỉ thấy những sân còn trống có thể đặt ngay.
          </p>
        </div>
        <div className="football-hero-stat">
          <strong>{totalElements}</strong>
          <span>{isAvailabilitySearch ? "sân còn trống" : "sân phù hợp"}</span>
        </div>
      </section>

      <div className="football-layout">
        <aside className="football-filters">
          <div className="football-filter-heading">
            <SlidersHorizontal size={20} />
            <h2>Bộ lọc</h2>
          </div>

          <div className="football-filter-block availability-picker">
            <h3>Tìm sân trống</h3>
            <Form.Group>
              <Form.Label>Ngày đá</Form.Label>
              <div className="football-search-control">
                <CalendarDays size={18} />
                <Form.Control
                  type="date"
                  min={todayValue()}
                  value={selectedDate}
                  onChange={(event) => {
                    setSelectedDate(event.target.value);
                    setPageNo(0);
                  }}
                />
              </div>
            </Form.Group>

            <Form.Group>
              <Form.Label>Khung giờ</Form.Label>
              <div className="football-search-control">
                <Clock size={18} />
                <Form.Select
                  value={selectedSlotValue}
                  onChange={(event) => {
                    setSelectedSlotValue(event.target.value);
                    setPageNo(0);
                  }}
                >
                  <option value="">Chọn khung giờ</option>
                  {slotOptions.map((slot) => (
                    <option key={slot.value} value={slot.value}>
                      {slot.label}
                    </option>
                  ))}
                </Form.Select>
              </div>
            </Form.Group>
          </div>

          <Form.Group className="football-search">
            <Form.Label>Tìm sân</Form.Label>
            <div className="football-search-control">
              <Search size={18} />
              <Form.Control
                type="text"
                placeholder="Nhập tên sân..."
                value={keyword}
                onChange={(event) => {
                  setKeyword(event.target.value);
                  setPageNo(0);
                }}
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
          {isAvailabilitySearch && (
            <div className="availability-summary">
              <CalendarDays size={18} />
              <span>
                Đang tìm sân trống ngày {new Date(selectedDate).toLocaleDateString("vi-VN")} lúc{" "}
                {selectedSlot.label}
              </span>
            </div>
          )}

          <div className="football-results-header">
            <div>
              <span>{isAvailabilitySearch ? "Sân còn trống" : "Danh sách sân"}</span>
              <strong>
                {totalElements} {isAvailabilitySearch ? "sân còn trống" : "sân phù hợp"}
              </strong>
            </div>
            <p>Trang {pageNo + 1} / {Math.max(totalPages, 1)}</p>
          </div>

          {loading ? (
            <div className="football-empty">Đang tải danh sách sân...</div>
          ) : fields.length === 0 ? (
            <div className="football-empty">
              {isAvailabilitySearch
                ? "Không có sân trống trong khung giờ đã chọn."
                : "Không tìm thấy sân phù hợp."}
            </div>
          ) : (
            <div className="football-grid">
              {fields.map((field) => {
                const maintenance = field.underMaintenance ? field : maintenanceMap[field.id];
                const isUnderMaintenance = isAvailabilitySearch
                  ? false
                  : maintenance?.underMaintenance || false;
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
                            <ShieldCheck size={14} /> {isAvailabilitySearch ? "Còn trống" : "Sẵn sàng"}
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
                        <span>{isAvailabilitySearch ? "Giá khung giờ" : "Giá tham khảo"}</span>
                        <strong>
                          {formatMoney(isAvailabilitySearch ? field.slotPrice : field.pricePerHour)}
                        </strong>
                      </div>

                      {isAvailabilitySearch && (
                        <div className="football-slot-note">
                          <Clock size={15} />
                          <span>{selectedSlot.label}</span>
                        </div>
                      )}

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
                        onClick={() => goToFieldDetail(field)}
                      >
                        {isUnderMaintenance
                          ? "Đang bảo trì"
                          : isAvailabilitySearch
                            ? "Đặt khung giờ này"
                            : "Xem lịch sân"}
                      </Button>
                    </div>
                  </article>
                );
              })}
            </div>
          )}

          {isAvailabilitySearch && !loading && fields.length === 0 && suggestions.length > 0 && (
            <section className="field-suggestion-section">
              <div className="field-suggestion-heading">
                <Sparkles size={20} />
                <div>
                  <span>Gợi ý thay thế</span>
                  <strong>Những sân và khung giờ gần nhất bạn có thể đặt</strong>
                </div>
              </div>

              <div className="field-suggestion-grid">
                {suggestions.map((suggestion) => (
                  <article
                    className="field-suggestion-card"
                    key={`${suggestion.fieldId}-${suggestion.date}-${suggestion.startTime}-${suggestion.endTime}`}
                  >
                    <img src={suggestion.imageUrl || "/no-image.png"} alt={suggestion.fieldName} />
                    <div>
                      <span className="field-suggestion-type">{suggestion.fieldType}</span>
                      <h3>{suggestion.fieldName}</h3>
                      <p>{suggestion.reason}</p>
                      <div className="field-suggestion-meta">
                        <span>
                          {suggestion.startTime} - {suggestion.endTime}
                        </span>
                        <strong>{formatMoney(suggestion.price)}</strong>
                      </div>
                      <Button
                        className="football-card-action"
                        type="button"
                        onClick={() => goToSuggestion(suggestion)}
                      >
                        Chọn gợi ý này
                      </Button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          )}

          {totalPages > 1 && (
            <div className="football-pagination">
              {Array.from({ length: totalPages }, (_, index) => (
                <button
                  type="button"
                  key={index}
                  className={pageNo === index ? "active" : ""}
                  onClick={() => setPageNo(index)}
                >
                  {index + 1}
                </button>
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
