import { useCallback, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import swal from "sweetalert";
import {
  createMembershipTier,
  deleteMembershipTier,
  getMembershipTiers,
  updateMembershipTier,
} from "../../services/membershipTierService";
import "../../assets/styles/AdminCrudPage.css";

const emptyForm = {
  name: "",
  requiredPoints: 0,
  discountPercent: 0,
  isActive: true,
};

export default function MembershipTierPage() {
  const [tiers, setTiers] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [showModal, setShowModal] = useState(false);

  const loadTiers = useCallback(async () => {
    const data = await getMembershipTiers();
    setTiers(
      [...(data || [])].sort(
        (left, right) => left.requiredPoints - right.requiredPoints
      )
    );
  }, []);

  useEffect(() => {
    loadTiers().catch((error) => swal("Lỗi", error.message, "error"));
  }, [loadTiers]);

  const openModal = (tier = null) => {
    setEditing(tier);
    setForm(tier ? { ...tier } : emptyForm);
    setShowModal(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      name: form.name.trim(),
      requiredPoints: Number(form.requiredPoints),
      discountPercent: Number(form.discountPercent),
      isActive: Boolean(form.isActive),
    };

    try {
      if (editing) {
        await updateMembershipTier(editing.id, payload);
      } else {
        await createMembershipTier(payload);
      }
      setShowModal(false);
      await loadTiers();
    } catch (error) {
      swal("Lỗi", error.message, "error");
    }
  };

  const handleDelete = async (id) => {
    const confirmed = await swal({
      title: "Xóa hạng thành viên?",
      text: "Hạng đang được sử dụng sẽ được chuyển sang trạng thái ngừng hoạt động.",
      icon: "warning",
      buttons: ["Giữ lại", "Xóa"],
      dangerMode: true,
    });
    if (!confirmed) return;
    await deleteMembershipTier(id);
    await loadTiers();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Quản lý hạng thành viên</h2>
        <Button onClick={() => openModal()}>+ Thêm hạng</Button>
      </div>

      <table className="admin-crud-table">
        <thead>
          <tr>
            <th>Tên hạng</th>
            <th>Điểm tối thiểu</th>
            <th>Giảm giá</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {tiers.map((tier) => (
            <tr key={tier.id}>
              <td>{tier.name}</td>
              <td>{tier.requiredPoints}</td>
              <td>{tier.discountPercent}%</td>
              <td>
                <span
                  className={`admin-crud-badge ${
                    tier.isActive ? "active" : "inactive"
                  }`}
                >
                  {tier.isActive ? "Hoạt động" : "Đã tắt"}
                </span>
              </td>
              <td>
                <div className="admin-crud-row-actions">
                  <Button
                    size="sm"
                    variant="warning"
                    onClick={() => openModal(tier)}
                  >
                    Sửa
                  </Button>
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDelete(tier.id)}
                  >
                    Xóa
                  </Button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Form onSubmit={handleSubmit}>
          <Modal.Header closeButton>
            <Modal.Title>{editing ? "Sửa hạng" : "Thêm hạng"}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Tên hạng</Form.Label>
              <Form.Control
                value={form.name}
                maxLength={100}
                onChange={(event) =>
                  setForm({ ...form, name: event.target.value })
                }
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Điểm tối thiểu</Form.Label>
              <Form.Control
                type="number"
                min="0"
                value={form.requiredPoints}
                onChange={(event) =>
                  setForm({ ...form, requiredPoints: event.target.value })
                }
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Phần trăm giảm giá</Form.Label>
              <Form.Control
                type="number"
                min="0"
                max="100"
                value={form.discountPercent}
                onChange={(event) =>
                  setForm({ ...form, discountPercent: event.target.value })
                }
                required
              />
            </Form.Group>
            <Form.Check
              type="checkbox"
              label="Đang hoạt động"
              checked={form.isActive}
              onChange={(event) =>
                setForm({ ...form, isActive: event.target.checked })
              }
            />
          </Modal.Body>
          <Modal.Footer>
            <Button type="submit">Lưu</Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
}
