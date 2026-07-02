import { useCallback, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import swal from "sweetalert";
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

  const loadVouchers = useCallback(async () => {
    const data = await getVouchers();
    setVouchers(data || []);
  }, []);

  useEffect(() => {
    loadVouchers().catch(console.error);
  }, [loadVouchers]);

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
    if (form.startDate && form.endDate && form.endDate <= form.startDate) {
      swal(
        "Thời gian không hợp lệ",
        "Ngày kết thúc phải sau ngày bắt đầu.",
        "warning"
      );
      return;
    }

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
    const confirmed = await swal({
      title: "Xóa voucher?",
      text: "Dữ liệu đã xóa sẽ không thể khôi phục.",
      icon: "warning",
      buttons: ["Giữ lại", "Xóa"],
      dangerMode: true,
    });
    if (!confirmed) return;
    await deleteVoucher(id);
    await loadVouchers();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Quản lý voucher</h2>
        <Button onClick={() => openModal()}>+ Thêm voucher</Button>
      </div>

      {vouchers.length === 0 ? (
        <div className="admin-crud-empty">Chưa có voucher nào</div>
      ) : (
        <table className="admin-crud-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Mã</th>
              <th>Loại</th>
              <th>Giá trị</th>
              <th>Số lượng</th>
              <th>Trạng thái</th>
              <th>Thời gian áp dụng</th>
              <th>Thao tác</th>
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
                    {voucher.isActive ? "Đang bật" : "Đã tắt"}
                  </span>
                </td>
                <td>{voucher.startDate?.slice(0, 10) || "-"} - {voucher.endDate?.slice(0, 10) || "-"}</td>
                <td>
                  <div className="admin-crud-row-actions">
                    <Button size="sm" variant="warning" onClick={() => openModal(voucher)}>
                      Sửa
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => handleDelete(voucher.id)}>
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
          <Modal.Title>{editing ? "Sửa voucher" : "Thêm voucher"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Mã voucher</Form.Label>
              <Form.Control value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Loại giảm giá</Form.Label>
              <Form.Select value={form.discountType} onChange={(e) => setForm({ ...form, discountType: e.target.value })}>
                <option value="PERCENT">Theo phần trăm</option>
                <option value="FIXED_AMOUNT">Giảm tiền cố định</option>
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Giá trị giảm</Form.Label>
              <Form.Control type="number" min="0" value={form.discountValue} onChange={(e) => setForm({ ...form, discountValue: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Số lượng</Form.Label>
              <Form.Control type="number" min="0" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ngày bắt đầu</Form.Label>
              <Form.Control type="datetime-local" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ngày kết thúc</Form.Label>
              <Form.Control type="datetime-local" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
            </Form.Group>
            <Form.Check type="checkbox" label="Đang bật" checked={form.isActive} onChange={(e) => setForm({ ...form, isActive: e.target.checked })} />
            <Button className="mt-3" type="submit" variant="success">
              Lưu
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
