import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getFields,
  createField,
  updateField,
  deleteField,
  updateFieldStatus
} from "../../services/fieldService";

import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/FootballFieldPage.css";

export default function FootballFieldPage() {
  const [fields, setFields] = useState([]);
  const [pageData, setPageData] = useState(null);

  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);

  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState("");
  const [type, setType] = useState("");

  const [show, setShow] = useState(false);
  const [editingField, setEditingField] = useState(null);
  const [confirmField, setConfirmField] = useState(null);

  const [form, setForm] = useState({
    name: "",
    address: "",
    pricePerHour: "",
    type: "",
    imageUrl: "",
    description: ""
  });

  const loadFields = useCallback(async () => {
    try {
      const data = await getFields({
        page: pageNo,
        size: pageSize,
        keyword,
        status,
        type
      });

      if (data && data.content) {
        setFields(data.content);
        setPageData(data);
      } else {
        setFields([]);
      }
    } catch (err) {
      console.error("Fetch fields failed:", err);
      setFields([]);
    }
  }, [pageNo, pageSize, keyword, status, type]);

  useEffect(() => {
    loadFields();
  }, [loadFields]);

  const handleShow = (field = null) => {
    if (field) {
      setEditingField(field);
      setForm({
        name: field.name || "",
        address: field.address || "",
        pricePerHour: field.pricePerHour || "",
        type: field.type || "",
        imageUrl: field.imageUrl || "",
        description: field.description || ""
      });
    } else {
      setEditingField(null);
      setForm({
        name: "",
        address: "",
        pricePerHour: "",
        type: "",
        imageUrl: "",
        description: ""
      });
    }
    setShow(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingField) {
        await updateField(editingField.id, form);
      } else {
        await createField(form);
      }

      await loadFields();
      setShow(false);
    } catch (err) {
      console.error("Save field failed:", err);
    }
  };

  const confirmDelete = async () => {
    if (!confirmField) return;

    try {
      await deleteField(confirmField.id);
      await loadFields();
      setConfirmField(null);
    } catch (err) {
      console.error("Delete failed:", err);
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      await updateFieldStatus(id, newStatus);
      await loadFields();
    } catch (err) {
      console.error("Update status failed:", err);
    }
  };

  return (
    <div className="field-page">
      <div className="field-header">
        <h2>Quản lý sân bóng</h2>

        <div className="filters">
          <Form.Control
            placeholder="Tìm sân bóng..."
            value={keyword}
            onChange={(e) => {
              setKeyword(e.target.value);
              setPageNo(0);
            }}
            style={{ width: "200px" }}
          />

          <Form.Select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            <option value="">Tất cả trạng thái</option>
            <option value="AVAILABLE">Khả dụng</option>
            <option value="UNAVAILABLE">Không khả dụng</option>
          </Form.Select>

          <Form.Select
            value={type}
            onChange={(e) => setType(e.target.value)}
          >
            <option value="">Tất cả loại sân</option>
            <option value="FIVE_A_SIDE">5 vs 5</option>
            <option value="SEVEN_A_SIDE">7 vs 7</option>
            <option value="ELEVEN_A_SIDE">11 vs 11</option>
          </Form.Select>

          <Button className="add-field" onClick={() => handleShow()}>+ Thêm sân</Button>
          <Link to="/admin/maintenance" style={{ textDecoration: "none" }}>
            <Button className="add-field" variant="info" style={{ marginLeft: "10px" }}>
              Quản lý bảo trì
            </Button>
          </Link>
        </div>
      </div>

      <table className="field-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên sân</th>
            <th>Địa chỉ</th>
            <th>Giá</th>
            <th>Loại sân</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {fields.length > 0 ? (
            fields.map((f) => (
              <tr key={f.id}>
                <td>{f.id}</td>
                <td>{f.name}</td>
                <td>{f.address}</td>
                <td>{f.pricePerHour} VND</td>
                <td>{f.type}</td>

                <td>
                  <span className={`status-${f.status?.toLowerCase()}`}>
                    {f.status}
                  </span>

                  <Form.Select
                    size="sm"
                    style={{ marginTop: "5px" }}
                    value={f.status}
                    onChange={(e) =>
                      handleStatusChange(f.id, e.target.value)
                    }
                  >
                    <option value="AVAILABLE">Khả dụng</option>
                    <option value="UNAVAILABLE">Không khả dụng</option>
                  </Form.Select>
                </td>

                <td>
                  <Button
                    size="sm"
                    variant="warning"
                    onClick={() => handleShow(f)}
                  >
                    Sửa
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => setConfirmField(f)}
                  >
                    Xóa
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" style={{ textAlign: "center" }}>
                Chưa có sân bóng nào
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {pageData && (
        <div className="pagination">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
            Trước
          </Button>

          <span>
            Trang {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button
            disabled={pageNo + 1 >= pageData.page.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Sau
          </Button>
        </div>
      )}

      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingField ? "Sửa sân bóng" : "Thêm sân bóng"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group>
              <Form.Label>Tên sân</Form.Label>
              <Form.Control
                value={form.name}
                onChange={(e) =>
                  setForm({ ...form, name: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Địa chỉ</Form.Label>
              <Form.Control
                value={form.address}
                onChange={(e) =>
                  setForm({ ...form, address: e.target.value })
                }
                required
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Giá theo giờ</Form.Label>
              <Form.Control
                type="number"
                min="0"
                value={form.pricePerHour}
                onChange={(e) =>
                  setForm({ ...form, pricePerHour: e.target.value })
                }
                required
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Loại sân</Form.Label>
              <Form.Select
                value={form.type}
                onChange={(e) =>
                  setForm({ ...form, type: e.target.value })
                }
              >
                <option value="">Chọn loại sân</option>
                <option value="FIVE_A_SIDE">5 vs 5</option>
                <option value="SEVEN_A_SIDE">7 vs 7</option>
                <option value="ELEVEN_A_SIDE">11 vs 11</option>
              </Form.Select>
            </Form.Group>

            <Form.Group>
              <Form.Label>URL hình ảnh</Form.Label>
              <Form.Control
                value={form.imageUrl}
                onChange={(e) =>
                  setForm({ ...form, imageUrl: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={form.description}
                onChange={(e) =>
                  setForm({ ...form, description: e.target.value })
                }
              />
            </Form.Group>

            <Button type="submit" variant="success" className="mt-3">
              {editingField ? "Cập nhật" : "Tạo mới"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={!!confirmField} onHide={() => setConfirmField(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          Bạn có chắc muốn xóa sân <b>{confirmField?.name}</b> không?
        </Modal.Body>

        <Modal.Footer>
          <Button onClick={() => setConfirmField(null)}>Hủy</Button>
          <Button variant="danger" onClick={confirmDelete}>
            Xóa
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
