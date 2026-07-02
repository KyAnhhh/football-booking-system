import { useCallback, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import swal from "sweetalert";
import { getFields } from "../../services/fieldService";
import { getHolidays } from "../../services/holidayService";
import {
  createHolidayPricingRule,
  deleteHolidayPricingRule,
  getHolidayPricingRules,
  updateHolidayPricingRule,
} from "../../services/holidayPricingRuleService";
import "../../assets/styles/AdminCrudPage.css";

const emptyForm = {
  fieldId: "",
  holidayId: "",
  startTime: "",
  endTime: "",
  pricePerSlot: "",
  slotDuration: 90,
};

const formatMoney = (value) => `${Number(value || 0).toLocaleString("vi-VN")} VND`;

const parseMoneyInput = (value) => {
  const normalized = String(value || "").replace(/[.,\s]/g, "");
  return Number(normalized);
};

export default function HolidayPricingRulePage() {
  const [fields, setFields] = useState([]);
  const [holidays, setHolidays] = useState([]);
  const [rules, setRules] = useState([]);
  const [selectedFieldId, setSelectedFieldId] = useState("");
  const [selectedHolidayId, setSelectedHolidayId] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const loadOptions = useCallback(async () => {
    const [fieldData, holidayData] = await Promise.all([
      getFields({ page: 0, size: 1000 }),
      getHolidays(),
    ]);

    setFields(fieldData?.content || []);
    setHolidays(holidayData || []);
  }, []);

  const loadRules = useCallback(async () => {
    const data = await getHolidayPricingRules({
      fieldId: selectedFieldId,
      holidayId: selectedHolidayId,
    });
    setRules(data || []);
  }, [selectedFieldId, selectedHolidayId]);

  useEffect(() => {
    loadOptions().catch(console.error);
  }, [loadOptions]);

  useEffect(() => {
    loadRules().catch(console.error);
  }, [loadRules]);

  const openModal = (rule = null) => {
    setEditing(rule);
    setForm(
      rule
        ? {
            fieldId: rule.fieldId,
            holidayId: rule.holidayId,
            startTime: rule.startTime,
            endTime: rule.endTime,
            pricePerSlot: String(rule.pricePerSlot || ""),
            slotDuration: rule.slotDuration || 90,
          }
        : {
            ...emptyForm,
            fieldId: selectedFieldId,
            holidayId: selectedHolidayId,
          }
    );
    setShowModal(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const payload = {
      ...form,
      fieldId: Number(form.fieldId),
      holidayId: Number(form.holidayId),
      pricePerSlot: parseMoneyInput(form.pricePerSlot),
      slotDuration: Number(form.slotDuration),
    };

    if (!payload.fieldId || !payload.holidayId) {
      swal("Thiếu thông tin", "Vui lòng chọn sân và ngày lễ.", "warning");
      return;
    }

    if (!Number.isFinite(payload.pricePerSlot) || payload.pricePerSlot <= 0) {
      swal("Giá không hợp lệ", "Giá mỗi slot phải lớn hơn 0.", "warning");
      return;
    }

    if (editing) {
      await updateHolidayPricingRule(editing.id, payload);
    } else {
      await createHolidayPricingRule(payload);
    }

    setShowModal(false);
    await loadRules();
  };

  const handleDelete = async (id) => {
    const confirmed = await swal({
      title: "Xóa giá ngày lễ?",
      text: "Dữ liệu đã xóa sẽ không thể khôi phục.",
      icon: "warning",
      buttons: ["Giữ lại", "Xóa"],
      dangerMode: true,
    });
    if (!confirmed) return;
    await deleteHolidayPricingRule(id);
    await loadRules();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Quản lý giá ngày lễ</h2>
        <div className="admin-crud-actions">
          <Form.Select value={selectedFieldId} onChange={(event) => setSelectedFieldId(event.target.value)}>
            <option value="">Tất cả sân</option>
            {fields.map((field) => (
              <option key={field.id} value={field.id}>
                {field.name}
              </option>
            ))}
          </Form.Select>

          <Form.Select value={selectedHolidayId} onChange={(event) => setSelectedHolidayId(event.target.value)}>
            <option value="">Tất cả ngày lễ</option>
            {holidays.map((holiday) => (
              <option key={holiday.id} value={holiday.id}>
                {holiday.name} - {holiday.holidayDate}
              </option>
            ))}
          </Form.Select>

          <Button onClick={() => openModal()}>+ Thêm giá ngày lễ</Button>
        </div>
      </div>

      {rules.length === 0 ? (
        <div className="admin-crud-empty">Chưa có giá ngày lễ nào</div>
      ) : (
        <table className="admin-crud-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Sân</th>
              <th>Ngày lễ</th>
              <th>Khung giờ</th>
              <th>Giá / slot</th>
              <th>Thời lượng slot</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {rules.map((rule) => (
              <tr key={rule.id}>
                <td>{rule.id}</td>
                <td>{rule.fieldName}</td>
                <td>
                  <strong>{rule.holidayName}</strong>
                  <div className="admin-crud-subtext">{rule.holidayDate}</div>
                </td>
                <td>{rule.startTime} - {rule.endTime}</td>
                <td>{formatMoney(rule.pricePerSlot)}</td>
                <td>{rule.slotDuration} phút</td>
                <td>
                  <div className="admin-crud-row-actions">
                    <Button size="sm" variant="warning" onClick={() => openModal(rule)}>
                      Sửa
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => handleDelete(rule.id)}>
                      Xóa
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editing ? "Sửa giá ngày lễ" : "Thêm giá ngày lễ"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Sân</Form.Label>
              <Form.Select value={form.fieldId} onChange={(event) => setForm({ ...form, fieldId: event.target.value })} required>
                <option value="">Chọn sân</option>
                {fields.map((field) => (
                  <option key={field.id} value={field.id}>
                    {field.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Ngày lễ</Form.Label>
              <Form.Select value={form.holidayId} onChange={(event) => setForm({ ...form, holidayId: event.target.value })} required>
                <option value="">Chọn ngày lễ</option>
                {holidays.map((holiday) => (
                  <option key={holiday.id} value={holiday.id}>
                    {holiday.name} - {holiday.holidayDate}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Giờ bắt đầu</Form.Label>
              <Form.Control type="time" value={form.startTime} onChange={(event) => setForm({ ...form, startTime: event.target.value })} required />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Giờ kết thúc</Form.Label>
              <Form.Control type="time" value={form.endTime} onChange={(event) => setForm({ ...form, endTime: event.target.value })} required />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Giá mỗi slot</Form.Label>
              <Form.Control
                type="text"
                inputMode="numeric"
                placeholder="700000"
                value={form.pricePerSlot}
                onChange={(event) => setForm({ ...form, pricePerSlot: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Thời lượng slot</Form.Label>
              <Form.Control
                type="number"
                min="1"
                value={form.slotDuration}
                onChange={(event) => setForm({ ...form, slotDuration: event.target.value })}
                required
              />
            </Form.Group>

            <Button type="submit" variant="success">
              Lưu
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
