import { useCallback, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import swal from "sweetalert";
import { getFields } from "../../services/fieldService";
import {
  createPricingRule,
  deletePricingRule,
  getPricingRules,
  getPricingRulesByField,
  updatePricingRule,
} from "../../services/pricingRuleService";
import "../../assets/styles/AdminCrudPage.css";

const days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
const emptyForm = {
  fieldId: "",
  dayOfWeek: "MONDAY",
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

export default function PricingRulePage() {
  const [fields, setFields] = useState([]);
  const [selectedFieldId, setSelectedFieldId] = useState("");
  const [rules, setRules] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const loadFields = useCallback(async () => {
    const data = await getFields({ page: 0, size: 1000 });
    const content = data?.content || [];
    setFields(content);
  }, []);

  const loadRules = useCallback(async (fieldId = selectedFieldId) => {
    const data = fieldId
      ? await getPricingRulesByField(fieldId)
      : await getPricingRules();
    setRules(data || []);
  }, [selectedFieldId]);

  useEffect(() => {
    loadFields().catch(console.error);
  }, [loadFields]);

  useEffect(() => {
    loadRules().catch(console.error);
  }, [loadRules]);

  const openModal = (rule = null) => {
    setEditing(rule);
    setForm(
      rule
        ? {
            fieldId: rule.fieldId,
            dayOfWeek: rule.dayOfWeek,
            startTime: rule.startTime,
            endTime: rule.endTime,
            pricePerSlot: String(rule.pricePerSlot || ""),
            slotDuration: rule.slotDuration || 90,
          }
        : { ...emptyForm, fieldId: selectedFieldId }
    );
    setShowModal(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      ...form,
      fieldId: Number(form.fieldId),
      pricePerSlot: parseMoneyInput(form.pricePerSlot),
      slotDuration: Number(form.slotDuration),
    };

    if (!Number.isFinite(payload.pricePerSlot) || payload.pricePerSlot <= 0) {
      swal("Giá không hợp lệ", "Giá mỗi slot phải lớn hơn 0.", "warning");
      return;
    }

    if (editing) {
      await updatePricingRule(editing.id, payload);
    } else {
      await createPricingRule(payload);
    }

    setShowModal(false);
    await loadRules(selectedFieldId);
  };

  const handleDelete = async (id) => {
    const confirmed = await swal({
      title: "Xóa bảng giá sân?",
      text: "Dữ liệu đã xóa sẽ không thể khôi phục.",
      icon: "warning",
      buttons: ["Giữ lại", "Xóa"],
      dangerMode: true,
    });
    if (!confirmed) return;
    await deletePricingRule(id);
    await loadRules();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Quản lý bảng giá</h2>
        <div className="admin-crud-actions">
          <Form.Select value={selectedFieldId} onChange={(e) => setSelectedFieldId(e.target.value)}>
            <option value="">Tất cả sân</option>
            {fields.map((field) => (
              <option key={field.id} value={field.id}>
                {field.name}
              </option>
            ))}
          </Form.Select>
          <Button onClick={() => openModal()}>
            + Thêm bảng giá
          </Button>
        </div>
      </div>

      {rules.length === 0 ? (
        <div className="admin-crud-empty">Chưa có bảng giá nào</div>
      ) : (
        <table className="admin-crud-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Sân</th>
              <th>Ngày</th>
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
                <td>{rule.dayOfWeek}</td>
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
          <Modal.Title>{editing ? "Sửa bảng giá" : "Thêm bảng giá"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Sân</Form.Label>
              <Form.Select value={form.fieldId} onChange={(e) => setForm({ ...form, fieldId: e.target.value })} required>
                <option value="">Chọn sân</option>
                {fields.map((field) => (
                  <option key={field.id} value={field.id}>
                    {field.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Thứ trong tuần</Form.Label>
              <Form.Select value={form.dayOfWeek} onChange={(e) => setForm({ ...form, dayOfWeek: e.target.value })}>
                {days.map((day) => (
                  <option key={day} value={day}>
                    {day}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Giờ bắt đầu</Form.Label>
              <Form.Control type="time" value={form.startTime} onChange={(e) => setForm({ ...form, startTime: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Giờ kết thúc</Form.Label>
              <Form.Control type="time" value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Giá mỗi slot</Form.Label>
              <Form.Control
                type="text"
                inputMode="numeric"
                placeholder="450000"
                value={form.pricePerSlot}
                onChange={(e) => setForm({ ...form, pricePerSlot: e.target.value })}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Thời lượng slot</Form.Label>
              <Form.Control type="number" min="1" value={form.slotDuration} onChange={(e) => setForm({ ...form, slotDuration: e.target.value })} required />
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
