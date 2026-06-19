import { useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import {
  createVoucher,
  deleteVoucher,
  getVouchers,
  updateVoucher,
} from "../../services/voucherService";
import "../../assets/styles/AdminCrudPage.css";

const emptyForm = {
  code: "",
  description: "",
  discountType: "PERCENT",
  discountValue: "",
  quantity: "",
  isActive: true,
  startDate: "",
  endDate: "",
};

const toLocalDateTime = (value) => (value ? `${value}:00` : null);
const toInputDateTime = (value) => (value ? value.slice(0, 16) : "");

export default function VoucherPage() {
  const [vouchers, setVouchers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const loadVouchers = async () => {
    const data = await getVouchers();
    setVouchers(data || []);
  };

  useEffect(() => {
    loadVouchers().catch(console.error);
  }, []);

  const openModal = (voucher = null) => {
    setEditing(voucher);
    setForm(
      voucher
        ? {
            code: voucher.code || "",
            description: voucher.description || "",
            discountType: voucher.discountType || "PERCENT",
            discountValue: voucher.discountValue || "",
            quantity: voucher.quantity || "",
            isActive: voucher.isActive !== false,
            startDate: toInputDateTime(voucher.startDate),
            endDate: toInputDateTime(voucher.endDate),
          }
        : emptyForm
    );
    setShowModal(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      ...form,
      discountValue: Number(form.discountValue),
      quantity: Number(form.quantity),
      startDate: toLocalDateTime(form.startDate),
      endDate: toLocalDateTime(form.endDate),
    };

    if (editing) {
      await updateVoucher(editing.id, payload);
    } else {
      await createVoucher(payload);
    }

    setShowModal(false);
    await loadVouchers();
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this voucher?")) return;
    await deleteVoucher(id);
    await loadVouchers();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Voucher management</h2>
        <Button onClick={() => openModal()}>+ Add voucher</Button>
      </div>

      {vouchers.length === 0 ? (
        <div className="admin-crud-empty">No vouchers found</div>
      ) : (
        <table className="admin-crud-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Code</th>
              <th>Type</th>
              <th>Value</th>
              <th>Quantity</th>
              <th>Active</th>
              <th>Window</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {vouchers.map((voucher) => (
              <tr key={voucher.id}>
                <td>{voucher.id}</td>
                <td>{voucher.code}</td>
                <td>{voucher.discountType}</td>
                <td>{voucher.discountValue}</td>
                <td>{voucher.quantity}</td>
                <td>
                  <span className={`admin-crud-badge ${voucher.isActive ? "active" : "inactive"}`}>
                    {voucher.isActive ? "Active" : "Inactive"}
                  </span>
                </td>
                <td>{voucher.startDate?.slice(0, 10) || "-"} - {voucher.endDate?.slice(0, 10) || "-"}</td>
                <td>
                  <div className="admin-crud-row-actions">
                    <Button size="sm" variant="warning" onClick={() => openModal(voucher)}>
                      Edit
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => handleDelete(voucher.id)}>
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
          <Modal.Title>{editing ? "Edit voucher" : "Add voucher"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Code</Form.Label>
              <Form.Control value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Discount type</Form.Label>
              <Form.Select value={form.discountType} onChange={(e) => setForm({ ...form, discountType: e.target.value })}>
                <option value="PERCENT">PERCENT</option>
                <option value="FIXED">FIXED</option>
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Discount value</Form.Label>
              <Form.Control type="number" min="0" value={form.discountValue} onChange={(e) => setForm({ ...form, discountValue: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Quantity</Form.Label>
              <Form.Control type="number" min="0" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Start date</Form.Label>
              <Form.Control type="datetime-local" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>End date</Form.Label>
              <Form.Control type="datetime-local" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
            </Form.Group>
            <Form.Check type="checkbox" label="Active" checked={form.isActive} onChange={(e) => setForm({ ...form, isActive: e.target.checked })} />
            <Button className="mt-3" type="submit" variant="success">
              Save
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
