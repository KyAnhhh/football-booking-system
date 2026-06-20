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
};

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
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{item.name}</td>
                <td>{item.description}</td>
                <td>{Number(item.price || 0).toLocaleString("vi-VN")} VND</td>
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
            ))}
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
              <Form.Control value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Giá</Form.Label>
              <Form.Control type="number" min="0" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required />
            </Form.Group>
            <Form.Check
              type="checkbox"
              label="Đang bật"
              checked={form.isActive}
              onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
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
