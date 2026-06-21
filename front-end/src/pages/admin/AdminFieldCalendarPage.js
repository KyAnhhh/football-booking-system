import { useCallback, useEffect, useMemo, useState } from "react";
import { Button, Modal } from "react-bootstrap";
import { BsArrowClockwise, BsCalendarWeek, BsClock, BsSearch } from "react-icons/bs";

import "../../assets/styles/AdminFieldCalendar.css";
import { getAdminFieldCalendar } from "../../services/adminCalendarService";

const todayIso = () => new Date().toISOString().slice(0, 10);

const statusLabels = {
  AVAILABLE: "Trống",
  MAINTENANCE: "Bảo trì",
  PENDING: "Chờ xác nhận",
  CONFIRMED: "Đã xác nhận",
  COMPLETED: "Hoàn tất",
  CANCELLED: "Đã hủy",
  EXPIRED: "Hết hạn",
};

const typeLabels = {
  FIVE_A_SIDE: "Sân 5",
  SEVEN_A_SIDE: "Sân 7",
  ELEVEN_A_SIDE: "Sân 11",
};

const formatMoney = (value) =>
  `${Number(value || 0).toLocaleString("vi-VN")} đ`;

const formatTime = (value) =>
  value ? String(value).slice(0, 5) : "--:--";

const formatSlotTime = (slot) =>
  `${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}`;

function SlotModal({ slot, field, onHide }) {
  if (!slot) return null;

  const isBooking = slot.type === "BOOKING";
  const isMaintenance = slot.type === "MAINTENANCE";

  return (
    <Modal show onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Chi tiết khung giờ</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div className="calendar-detail">
          <div>
            <span>Sân</span>
            <strong>{field?.fieldName || slot.fieldName}</strong>
          </div>
          <div>
            <span>Khung giờ</span>
            <strong>{formatSlotTime(slot)}</strong>
          </div>
          <div>
            <span>Trạng thái</span>
            <strong>{statusLabels[slot.status] || slot.status}</strong>
          </div>
          <div>
            <span>Giá</span>
            <strong>{formatMoney(slot.price)}</strong>
          </div>

          {isBooking && (
            <>
              <div>
                <span>Mã booking</span>
                <strong>#{slot.bookingId}</strong>
              </div>
              <div>
                <span>Khách đặt</span>
                <strong>{slot.customerName || "Không có dữ liệu"}</strong>
              </div>
            </>
          )}

          {isMaintenance && (
            <>
              <div>
                <span>Mã bảo trì</span>
                <strong>#{slot.maintenanceId}</strong>
              </div>
              <div>
                <span>Lý do</span>
                <strong>{slot.reason || "Bảo trì sân"}</strong>
              </div>
            </>
          )}
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Đóng
        </Button>
      </Modal.Footer>
    </Modal>
  );
}

export default function AdminFieldCalendarPage() {
  const [date, setDate] = useState(todayIso());
  const [calendar, setCalendar] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [keyword, setKeyword] = useState("");
  const [fieldType, setFieldType] = useState("");
  const [slotStatus, setSlotStatus] = useState("");
  const [selected, setSelected] = useState(null);

  const loadCalendar = useCallback(async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getAdminFieldCalendar(date);
      setCalendar(data);
    } catch (err) {
      setError(err.message || "Không thể tải lịch sân.");
      setCalendar(null);
    } finally {
      setLoading(false);
    }
  }, [date]);

  useEffect(() => {
    loadCalendar();
  }, [loadCalendar]);

  const fields = useMemo(() => {
    const rows = calendar?.fields || [];

    return rows.filter((field) => {
      const matchesKeyword =
        !keyword ||
        field.fieldName?.toLowerCase().includes(keyword.toLowerCase());
      const matchesType = !fieldType || field.fieldType === fieldType;
      const matchesStatus =
        !slotStatus ||
        field.slots?.some((slot) => slot.status === slotStatus);

      return matchesKeyword && matchesType && matchesStatus;
    });
  }, [calendar, keyword, fieldType, slotStatus]);

  const summary = useMemo(() => {
    const slots = (calendar?.fields || []).flatMap((field) => field.slots || []);

    return {
      available: slots.filter((slot) => slot.status === "AVAILABLE").length,
      pending: slots.filter((slot) => slot.status === "PENDING").length,
      booked: slots.filter((slot) => ["CONFIRMED", "COMPLETED"].includes(slot.status)).length,
      maintenance: slots.filter((slot) => slot.status === "MAINTENANCE").length,
    };
  }, [calendar]);

  return (
    <div className="calendar-page">
      <section className="calendar-header">
        <div>
          <span className="calendar-eyebrow">Lịch vận hành sân</span>
          <h2>Calendar / Slot View</h2>
          <p>Quan sát nhanh slot trống, booking và bảo trì theo từng sân trong ngày.</p>
        </div>

        <Button onClick={loadCalendar} disabled={loading}>
          <BsArrowClockwise />
          {loading ? "Đang tải..." : "Làm mới"}
        </Button>
      </section>

      <section className="calendar-toolbar">
        <label>
          <span>Ngày</span>
          <input type="date" value={date} onChange={(event) => setDate(event.target.value)} />
        </label>

        <label>
          <span>Tìm sân</span>
          <div className="calendar-search">
            <BsSearch />
            <input
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
              placeholder="Nhập tên sân..."
            />
          </div>
        </label>

        <label>
          <span>Loại sân</span>
          <select value={fieldType} onChange={(event) => setFieldType(event.target.value)}>
            <option value="">Tất cả loại sân</option>
            <option value="FIVE_A_SIDE">Sân 5</option>
            <option value="SEVEN_A_SIDE">Sân 7</option>
            <option value="ELEVEN_A_SIDE">Sân 11</option>
          </select>
        </label>

        <label>
          <span>Trạng thái slot</span>
          <select value={slotStatus} onChange={(event) => setSlotStatus(event.target.value)}>
            <option value="">Tất cả trạng thái</option>
            <option value="AVAILABLE">Trống</option>
            <option value="PENDING">Chờ xác nhận</option>
            <option value="CONFIRMED">Đã xác nhận</option>
            <option value="COMPLETED">Hoàn tất</option>
            <option value="MAINTENANCE">Bảo trì</option>
          </select>
        </label>
      </section>

      <section className="calendar-summary">
        <div>
          <strong>{summary.available}</strong>
          <span>Slot trống</span>
        </div>
        <div>
          <strong>{summary.pending}</strong>
          <span>Chờ xác nhận</span>
        </div>
        <div>
          <strong>{summary.booked}</strong>
          <span>Đã đặt</span>
        </div>
        <div>
          <strong>{summary.maintenance}</strong>
          <span>Bảo trì</span>
        </div>
      </section>

      {error && <div className="calendar-error">{error}</div>}

      <section className="calendar-legend">
        {Object.entries({
          AVAILABLE: "Trống",
          PENDING: "Chờ xác nhận",
          CONFIRMED: "Đã xác nhận",
          COMPLETED: "Hoàn tất",
          MAINTENANCE: "Bảo trì",
        }).map(([status, label]) => (
          <span key={status}>
            <i className={`legend-dot ${status.toLowerCase()}`} />
            {label}
          </span>
        ))}
      </section>

      <section className="calendar-grid-wrap">
        <div className="calendar-grid" style={{ "--slot-count": calendar?.slotStarts?.length || 1 }}>
          <div className="calendar-header-row">
            <div className="calendar-corner">
              <BsCalendarWeek />
              <span>Sân / giờ</span>
            </div>

            {(calendar?.slotStarts || []).map((slotStart) => (
              <div key={slotStart} className="calendar-time">
                <BsClock />
                <span>{formatTime(slotStart)}</span>
              </div>
            ))}
          </div>

          {fields.map((field) => (
            <div className="calendar-row" key={field.fieldId}>
              <div className="calendar-field-cell">
                <strong>{field.fieldName}</strong>
                <span>{typeLabels[field.fieldType] || field.fieldType}</span>
              </div>

              {field.slots.map((slot) => (
                <button
                  type="button"
                  key={`${field.fieldId}-${slot.startTime}`}
                  className={`calendar-slot ${String(slot.status || "").toLowerCase()}`}
                  onClick={() => setSelected({ field, slot })}
                >
                  <strong>{statusLabels[slot.status] || slot.status}</strong>
                  <span>{formatTime(slot.startTime)} - {formatTime(slot.endTime)}</span>
                  {slot.type === "BOOKING" && <small>{slot.customerName}</small>}
                  {slot.type === "MAINTENANCE" && <small>{slot.reason || "Bảo trì"}</small>}
                </button>
              ))}
            </div>
          ))}
        </div>

        {!loading && fields.length === 0 && (
          <div className="calendar-empty">Không có sân phù hợp với bộ lọc hiện tại.</div>
        )}
      </section>

      <SlotModal
        field={selected?.field}
        slot={selected?.slot}
        onHide={() => setSelected(null)}
      />
    </div>
  );
}
