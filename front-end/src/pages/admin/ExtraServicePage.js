import { useCallback, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import {
  createExtraService,
  deleteExtraService,
  getAdminExtraServices,
  updateExtraService,
} from "../../services/extraService";
import "../../assets/styles/AdminCrudPage.css";

const emptyForm = {
  name: "",
  description: "",
  price: "",
  isActive: true,
  trackStock: false,
  stockQuantity: 0,
  remainingQuantity: 0,
  unit: "",
};

const formatMoney = (value) => `${Number(value || 0).toLocaleString("vi-VN")} VND`;

export default function ExtraServicePage() {
  const [items, setItems] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const loadItems = useCallback(async () => {
    const data = await getAdminExtraServices();
    setItems(data || []);
  }, []);

  useEffect(() => {
    loadItems().catch(console.error);
  }, [loadItems]);

  const openModal = (item = null) => {
    setEditing(item);
    setForm(
      item
        ? {
            name: item.name || "",
            description: item.description || "",
            price: item.price || "",
            isActive: item.isActive !== false,
            trackStock: item.trackStock === true,
            stockQuantity: item.stockQuantity ?? 0,
            remainingQuantity: item.remainingQuantity ?? 0,
            unit: item.unit || "",
          }
        : emptyForm
    );
    setShowModal(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const payload = {
      ...form,
      price: Number(form.price),
      stockQuantity: form.trackStock ? Number(form.stockQuantity || 0) : 0,
      remainingQuantity: form.trackStock ? Number(form.remainingQuantity || 0) : 0,
      unit: form.unit?.trim() || null,
    };

    if (editing) {
      await updateExtraService(editing.id, payload);
    } else {
      await createExtraService(payload);
    }

    setShowModal(false);
    await loadItems();
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc muốn xóa dịch vụ thêm này?")) return;
    await deleteExtraService(id);
    await loadItems();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Quản lý dịch vụ thêm</h2>
        <Button onClick={() => openModal()}>+ Thêm dịch vụ</Button>
      </div>

      {items.length === 0 ? (
        <div className="admin-crud-empty">Chưa có dịch vụ thêm nào</div>
      ) : (
        <table className="admin-crud-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên dịch vụ</th>
              <th>Mô tả</th>
              <th>Giá</th>
              <th>Tồn kho</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => {
              const lowStock =
                item.trackStock && Number(item.remainingQuantity || 0) <= 5;

              return (
                <tr key={item.id}>
                  <td>{item.id}</td>
                  <td>{item.name}</td>
                  <td>{item.description}</td>
                  <td>{formatMoney(item.price)}</td>
                  <td>
                    {item.trackStock ? (
                      <span className={`admin-crud-badge ${lowStock ? "inactive" : "active"}`}>
                        Còn {item.remainingQuantity || 0}/{item.stockQuantity || 0} {item.unit || ""}
                      </span>
                    ) : (
                      <span className="admin-crud-badge active">Không giới hạn</span>
                    )}
                  </td>
                  <td>
                    <span className={`admin-crud-badge ${item.isActive ? "active" : "inactive"}`}>
                      {item.isActive ? "Đang bật" : "Đã tắt"}
                    </span>
                  </td>
                  <td>
                    <div className="admin-crud-row-actions">
                      <Button size="sm" variant="warning" onClick={() => openModal(item)}>
                        Sửa
                      </Button>
                      <Button size="sm" variant="danger" onClick={() => handleDelete(item.id)}>
                        Xóa
                      </Button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editing ? "Sửa dịch vụ thêm" : "Thêm dịch vụ thêm"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Tên dịch vụ</Form.Label>
              <Form.Control
                value={form.name}
                onChange={(event) => setForm({ ...form, name: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Giá</Form.Label>
              <Form.Control
                type="number"
                min="0"
                value={form.price}
                onChange={(event) => setForm({ ...form, price: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Đơn vị</Form.Label>
              <Form.Control
                placeholder="chai, bộ, quả, lượt..."
                value={form.unit}
                onChange={(event) => setForm({ ...form, unit: event.target.value })}
              />
            </Form.Group>

            <Form.Check
              className="mb-3"
              type="checkbox"
              label="Quản lý tồn kho"
              checked={form.trackStock}
              onChange={(event) =>
                setForm({
                  ...form,
                  trackStock: event.target.checked,
                })
              }
            />

            {form.trackStock && (
              <div className="admin-crud-form-grid">
                <Form.Group className="mb-3">
                  <Form.Label>Tổng tồn</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    value={form.stockQuantity}
                    onChange={(event) =>
                      setForm({
                        ...form,
                        stockQuantity: event.target.value,
                      })
                    }
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Còn lại</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    value={form.remainingQuantity}
                    onChange={(event) =>
                      setForm({
                        ...form,
                        remainingQuantity: event.target.value,
                      })
                    }
                    required
                  />
                </Form.Group>
              </div>
            )}

            <Form.Check
              type="checkbox"
              label="Đang bật"
              checked={form.isActive}
              onChange={(event) => setForm({ ...form, isActive: event.target.checked })}
            />

            <Button className="mt-3" type="submit" variant="success">
              Lưu
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
