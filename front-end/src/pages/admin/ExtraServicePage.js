import { useEffect, useState } from "react";
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

  const loadItems = async () => {
    const data = await getAdminExtraServices();
    setItems(data || []);
  };

  useEffect(() => {
    loadItems().catch(console.error);
  }, []);

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
    if (!window.confirm("Delete this extra service?")) return;
    await deleteExtraService(id);
    await loadItems();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Extra service management</h2>
        <Button onClick={() => openModal()}>+ Add service</Button>
      </div>

      {items.length === 0 ? (
        <div className="admin-crud-empty">No extra services found</div>
      ) : (
        <table className="admin-crud-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Description</th>
              <th>Price</th>
              <th>Status</th>
              <th>Actions</th>
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
                    {item.isActive ? "Active" : "Inactive"}
                  </span>
                </td>
                <td>
                  <div className="admin-crud-row-actions">
                    <Button size="sm" variant="warning" onClick={() => openModal(item)}>
                      Edit
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => handleDelete(item.id)}>
                      Delete
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
          <Modal.Title>{editing ? "Edit extra service" : "Add extra service"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Price</Form.Label>
              <Form.Control type="number" min="0" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required />
            </Form.Group>
            <Form.Check
              type="checkbox"
              label="Active"
              checked={form.isActive}
              onChange={(e) => setForm({ ...form, isActive: e.target.checked })}
            />
            <Button className="mt-3" type="submit" variant="success">
              Save
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
