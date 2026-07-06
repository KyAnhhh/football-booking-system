import { useCallback, useEffect, useMemo, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { jwtDecode } from "jwt-decode";
import swal from "sweetalert";
import {
  createExtraService,
  deleteExtraService,
  getAdminExtraServices,
  updateExtraService,
  checkExtraServicePublication,
  publishExtraService,
  unpublishExtraService,
} from "../../services/extraService";
import { getInventoryItems } from "../../services/inventoryService";
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
  inventoryItemId: "",
};

const formatMoney = (value) =>
  `${Number(value || 0).toLocaleString("vi-VN")} VND`;

export default function ExtraServicePage() {
  const [items, setItems] = useState([]);
  const [inventoryItems, setInventoryItems] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const canManage = useMemo(() => {
    try {
      const role = String(
        jwtDecode(sessionStorage.getItem("accessToken") || "").role || ""
      )
        .replace("ROLE_", "")
        .toUpperCase();
      return role === "ADMIN" || role === "MANAGER";
    } catch {
      return false;
    }
  }, []);

  const loadItems = useCallback(async () => {
    const data = await getAdminExtraServices();
    setItems(data || []);
  }, []);

  const loadInventoryItems = useCallback(async () => {
    const data = await getInventoryItems({
      page: 0,
      size: 200,
      itemType: "EXTRA_SERVICE",
    });
    setInventoryItems(data.content || []);
  }, []);

  useEffect(() => {
    const requests = [loadItems()];
    if (canManage) requests.push(loadInventoryItems());
    Promise.all(requests).catch(console.error);
  }, [canManage, loadItems, loadInventoryItems]);

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
            inventoryItemId: item.inventoryItemId || "",
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
      stockQuantity: 0,
      remainingQuantity: 0,
      unit: form.unit?.trim() || null,
      inventoryItemId:
        form.trackStock && form.inventoryItemId
          ? Number(form.inventoryItemId)
          : null,
    };

    if (editing) {
      await updateExtraService(editing.id, payload);
    } else {
      await createExtraService(payload);
    }

    setShowModal(false);
    await Promise.all([loadItems(), loadInventoryItems()]);
  };

  const handleDelete = async (id) => {
    const confirmed = await swal({
      title: "Xóa dịch vụ thêm?",
      text: "Dữ liệu đã xóa sẽ không thể khôi phục.",
      icon: "warning",
      buttons: ["Giữ lại", "Xóa"],
      dangerMode: true,
    });
    if (!confirmed) return;
    await deleteExtraService(id);
    await loadItems();
  };

  const handlePublication = async (item) => {
    if (item.publicationStatus === "ACTIVE") {
      await unpublishExtraService(item.id);
    } else {
      const check = await checkExtraServicePublication(item.id);
      if (!check.publishable) {
        swal(
          "Chưa thể lên kệ",
          (check.reasons || []).join("\n"),
          "warning"
        );
        return;
      }
      await publishExtraService(item.id);
    }
    await loadItems();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Quản lý dịch vụ thêm</h2>
        {canManage && (
          <Button onClick={() => openModal()}>+ Thêm dịch vụ</Button>
        )}
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
              <th>Mã kho</th>
              <th>Trạng thái</th>
              <th>Đăng bán</th>
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
                      <span
                        className={`admin-crud-badge ${
                          lowStock ? "inactive" : "active"
                        }`}
                      >
                        Còn {item.remainingQuantity || 0}/
                        {item.stockQuantity || 0} {item.unit || ""}
                      </span>
                    ) : (
                      <span className="admin-crud-badge active">
                        Không giới hạn
                      </span>
                    )}
                  </td>
                  <td>{item.inventoryItemId || "-"}</td>
                  <td>
                    <span
                      className={`admin-crud-badge ${
                        item.isActive ? "active" : "inactive"
                      }`}
                    >
                      {item.isActive ? "Đang bật" : "Đã tắt"}
                    </span>
                  </td>
                  <td>{item.publicationStatus || "DRAFT"}</td>
                  <td>
                    {canManage ? (
                    <div className="admin-crud-row-actions">
                      <Button
                        size="sm"
                        variant={item.publicationStatus === "ACTIVE" ? "outline-danger" : "success"}
                        onClick={() => handlePublication(item)}
                      >
                        {item.publicationStatus === "ACTIVE" ? "Gỡ kệ" : "Lên kệ"}
                      </Button>
                      <Button
                        size="sm"
                        variant="warning"
                        onClick={() => openModal(item)}
                      >
                        Sửa
                      </Button>
                      <Button
                        size="sm"
                        variant="danger"
                        onClick={() => handleDelete(item.id)}
                      >
                        Xóa
                      </Button>
                    </div>
                    ) : (
                      <span className="text-muted">Chỉ xem</span>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editing ? "Sửa dịch vụ thêm" : "Thêm dịch vụ thêm"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Tên dịch vụ</Form.Label>
              <Form.Control
                value={form.name}
                onChange={(event) =>
                  setForm({ ...form, name: event.target.value })
                }
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                value={form.description}
                onChange={(event) =>
                  setForm({ ...form, description: event.target.value })
                }
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Giá</Form.Label>
              <Form.Control
                type="number"
                min="0"
                value={form.price}
                onChange={(event) =>
                  setForm({ ...form, price: event.target.value })
                }
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Đơn vị</Form.Label>
              <Form.Control
                placeholder="chai, bộ, quả, lượt..."
                value={form.unit}
                onChange={(event) =>
                  setForm({ ...form, unit: event.target.value })
                }
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
                  inventoryItemId: event.target.checked
                    ? form.inventoryItemId
                    : "",
                })
              }
            />

            {form.trackStock && (
              <>
                <Form.Group className="mb-3">
                  <Form.Label>Liên kết item kho</Form.Label>
                  <Form.Select
                    value={form.inventoryItemId}
                    onChange={(event) =>
                      setForm({
                        ...form,
                        inventoryItemId: event.target.value,
                      })
                    }
                  >
                    <option value="">Tự tạo item kho</option>
                    {inventoryItems.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.itemCode} - {item.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>

                <div className="alert alert-info">
                  Tồn ban đầu bằng 0. Hãy tạo và ghi sổ phiếu nhập kho
                  trước khi đưa dịch vụ lên kệ.
                </div>
              </>
            )}

            <Form.Check
              type="checkbox"
              label="Đang bật"
              checked={form.isActive}
              onChange={(event) =>
                setForm({ ...form, isActive: event.target.checked })
              }
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
