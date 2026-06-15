import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Modal, Button, Form } from "react-bootstrap";
import { BsSearch, BsPlus, BsPencil, BsTrash, BsCalendarEvent } from "react-icons/bs";
import "../../assets/styles/MaintenanceManagementPage.css";

import {
  getMaintenances,
  createMaintenance,
  updateMaintenance,
  deleteMaintenance,
  getFields,
} from "../../services/fieldService";

export default function MaintenanceManagementPage() {
  const [maintenances, setMaintenances] = useState([]);
  const [pageData, setPageData] = useState(null);
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(10);
  const [keyword, setKeyword] = useState("");
  const [fieldFilter, setFieldFilter] = useState("");
  const [fields, setFields] = useState([]);

  const [showModal, setShowModal] = useState(false);
  const [editingMaintenance, setEditingMaintenance] = useState(null);
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    fieldId: "",
    startTime: "",
    endTime: "",
    reason: "",
    description: "",
  });

  const loadFields = async () => {
    try {
      const data = await getFields({
        page: 0,
        size: 1000,
      });
      if (data && data.content) {
        setFields(data.content);
      }
    } catch (err) {
      console.error("Load fields failed:", err);
    }
  };

  const loadMaintenances = async () => {
    try {
      setLoading(true);
      const data = await getMaintenances({
        page: pageNo,
        size: pageSize,
        keyword: keyword || undefined,
      });

      if (data && data.content) {
        setMaintenances(data.content);
        setPageData(data);
      } else {
        setMaintenances([]);
      }
    } catch (err) {
      console.error("Load maintenances failed:", err);
      setMaintenances([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFields();
  }, []);

  useEffect(() => {
    loadMaintenances();
  }, [pageNo, keyword]);

  const handleShow = (maintenance = null) => {
    if (maintenance) {
      setEditingMaintenance(maintenance);
      setForm({
        fieldId: maintenance.fieldId,
        startTime: formatDateTimeForInput(maintenance.startTime),
        endTime: formatDateTimeForInput(maintenance.endTime),
        reason: maintenance.reason || "",
        description: maintenance.description || "",
      });
    } else {
      setEditingMaintenance(null);
      setForm({
        fieldId: "",
        startTime: "",
        endTime: "",
        reason: "",
        description: "",
      });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingMaintenance) {
        await updateMaintenance(editingMaintenance.id, form);
      } else {
        await createMaintenance(form);
      }

      await loadMaintenances();
      setShowModal(false);
    } catch (err) {
      console.error("Save maintenance failed:", err);
    }
  };

  const confirmDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa lịch bảo trì này?")) {
      try {
        await deleteMaintenance(id);
        await loadMaintenances();
      } catch (err) {
        console.error("Delete maintenance failed:", err);
      }
    }
  };

  const getStatusBadge = (startTime, endTime) => {
    const now = new Date();
    const start = new Date(startTime);
    const end = new Date(endTime);

    if (start > now) {
      return <span className="badge badge-upcoming">Sắp tới</span>;
    } else if (end < now) {
      return <span className="badge badge-completed">Đã hoàn tất</span>;
    } else {
      return <span className="badge badge-ongoing">Đang diễn ra</span>;
    }
  };

  const formatDateTime = (dateTimeStr) => {
    const date = new Date(dateTimeStr);
    return date.toLocaleString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatDateTimeForInput = (dateTimeStr) => {
    const date = new Date(dateTimeStr);
    return date.toISOString().slice(0, 16);
  };

  return (
    <div className="maintenance-page">
      <div className="maintenance-header">
        <h2>Quản lý bảo trì</h2>

        <div className="filters">
          <div className="search-box">
            <BsSearch className="search-icon" />
            <input
              type="text"
              placeholder="Tìm kiếm theo lý do..."
              value={keyword}
              onChange={(e) => {
                setKeyword(e.target.value);
                setPageNo(0);
              }}
            />
          </div>

          <div className="field-filter">
            <select
              value={fieldFilter}
              onChange={(e) => setFieldFilter(e.target.value)}
            >
              <option value="">Tất cả sân</option>
              {fields.map((f) => (
                <option key={f.id} value={f.id}>
                  {f.name}
                </option>
              ))}
            </select>
          </div>

          <Button className="add-btn" onClick={() => handleShow()}>
            <BsPlus /> Thêm lịch bảo trì
          </Button>
        </div>
      </div>

      {loading ? (
        <div className="loading">Đang tải...</div>
      ) : maintenances.length === 0 ? (
        <div className="no-data">Không có lịch bảo trì nào</div>
      ) : (
        <table className="maintenance-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên sân</th>
              <th>Thời gian bắt đầu</th>
              <th>Thời gian kết thúc</th>
              <th>Lý do</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>

          <tbody>
            {maintenances.map((m) => (
              <tr key={m.id}>
                <td>{m.id}</td>
                <td>{m.fieldName}</td>
                <td>{formatDateTime(m.startTime)}</td>
                <td>{formatDateTime(m.endTime)}</td>
                <td>{m.reason}</td>
                <td>{getStatusBadge(m.startTime, m.endTime)}</td>
                <td>
                  <Button
                    size="sm"
                    variant="warning"
                    onClick={() => handleShow(m)}
                  >
                    <BsPencil /> Chỉnh sửa
                  </Button>
                  {" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => confirmDelete(m.id)}
                  >
                    <BsTrash /> Xóa
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {pageData && (
        <div className="pagination">
          <Button
            disabled={pageNo === 0}
            onClick={() => setPageNo(pageNo - 1)}
          >
            Prev
          </Button>

          <span>
            Page {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button
            disabled={pageNo + 1 >= pageData.page.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Next
          </Button>
        </div>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingMaintenance ? "Chỉnh sửa lịch bảo trì" : "Thêm lịch bảo trì"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group>
              <Form.Label>Sân</Form.Label>
              <Form.Select
                value={form.fieldId}
                onChange={(e) =>
                  setForm({ ...form, fieldId: parseInt(e.target.value) })
                }
                required
              >
                <option value="">Chọn sân</option>
                {fields.map((f) => (
                  <option key={f.id} value={f.id}>
                    {f.name} ({f.status})
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group>
              <Form.Label>Thời gian bắt đầu</Form.Label>
              <Form.Control
                type="datetime-local"
                value={form.startTime}
                onChange={(e) =>
                  setForm({ ...form, startTime: e.target.value })
                }
                required
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Thời gian kết thúc</Form.Label>
              <Form.Control
                type="datetime-local"
                value={form.endTime}
                onChange={(e) =>
                  setForm({ ...form, endTime: e.target.value })
                }
                required
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Lý do</Form.Label>
              <Form.Control
                type="text"
                value={form.reason}
                onChange={(e) =>
                  setForm({ ...form, reason: e.target.value })
                }
                required
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
              {editingMaintenance ? "Cập nhật" : "Tạo"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}