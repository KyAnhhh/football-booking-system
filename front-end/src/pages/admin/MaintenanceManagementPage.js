import { useCallback, useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import { BsSearch, BsPlus, BsPencil, BsTrash } from "react-icons/bs";
import swal from "sweetalert";
import "../../assets/styles/MaintenanceManagementPage.css";

import {
  getMaintenances,
  createMaintenance,
  updateMaintenance,
  deleteMaintenance,
  getFields,
} from "../../services/fieldService";

const toLocalDateTimeInput = (date) => {
  const pad = (value) => String(value).padStart(2, "0");
  return [
    date.getFullYear(),
    pad(date.getMonth() + 1),
    pad(date.getDate()),
  ].join("-") + `T${pad(date.getHours())}:${pad(date.getMinutes())}`;
};

const getDefaultMaintenanceTimes = () => {
  const start = new Date();
  start.setSeconds(0, 0);
  start.setMinutes(Math.ceil((start.getMinutes() + 1) / 15) * 15);
  const end = new Date(start.getTime() + 2 * 60 * 60 * 1000);
  return {
    startTime: toLocalDateTimeInput(start),
    endTime: toLocalDateTimeInput(end),
  };
};

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

  const loadFields = useCallback(async () => {
    try {
      const data = await getFields({ page: 0, size: 1000 });
      setFields(data?.content || []);
    } catch (err) {
      console.error("Load fields failed:", err);
    }
  }, []);

  const loadMaintenances = useCallback(async () => {
    try {
      setLoading(true);
      const data = await getMaintenances({
        page: pageNo,
        size: pageSize,
        keyword: keyword || undefined,
      });

      setMaintenances(data?.content || []);
      setPageData(data || null);
    } catch (err) {
      console.error("Load maintenances failed:", err);
      setMaintenances([]);
    } finally {
      setLoading(false);
    }
  }, [pageNo, pageSize, keyword]);

  useEffect(() => {
    loadFields();
  }, [loadFields]);

  useEffect(() => {
    loadMaintenances();
  }, [loadMaintenances]);

  const displayedMaintenances = fieldFilter
    ? maintenances.filter((maintenance) => String(maintenance.fieldId) === String(fieldFilter))
    : maintenances;

  const formatDateTimeForInput = (dateTimeStr) => {
    if (!dateTimeStr) return "";
    return String(dateTimeStr).slice(0, 16);
  };

  const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return "-";
    return new Date(dateTimeStr).toLocaleString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getStatusBadge = (startTime, endTime) => {
    const now = new Date();
    const start = new Date(startTime);
    const end = new Date(endTime);

    if (start > now) {
      return <span className="badge badge-upcoming">Upcoming</span>;
    }

    if (end < now) {
      return <span className="badge badge-completed">Completed</span>;
    }

    return <span className="badge badge-ongoing">Ongoing</span>;
  };

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
      const defaultTimes = getDefaultMaintenanceTimes();
      setEditingMaintenance(null);
      setForm({
        fieldId: "",
        ...defaultTimes,
        reason: "",
        description: "",
      });
    }
    setShowModal(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const start = new Date(form.startTime);
    const end = new Date(form.endTime);
    const currentMinute = new Date();
    currentMinute.setSeconds(0, 0);

    if (!editingMaintenance && start < currentMinute) {
      swal(
        "Thời gian không hợp lệ",
        "Thời gian bắt đầu bảo trì không được ở trong quá khứ.",
        "warning"
      );
      return;
    }
    if (end <= start) {
      swal(
        "Thời gian không hợp lệ",
        "Thời gian kết thúc phải sau thời gian bắt đầu.",
        "warning"
      );
      return;
    }

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
      swal(
        "Không thể lưu lịch bảo trì",
        err.message || "Vui lòng kiểm tra lại thông tin.",
        "error"
      );
    }
  };

  const confirmDelete = async (id) => {
    const confirmed = await swal({
      title: "Xóa lịch bảo trì?",
      text: "Dữ liệu đã xóa sẽ không thể khôi phục.",
      icon: "warning",
      buttons: ["Giữ lại", "Xóa"],
      dangerMode: true,
    });
    if (!confirmed) return;
    try {
      await deleteMaintenance(id);
      await loadMaintenances();
    } catch (err) {
      console.error("Delete maintenance failed:", err);
    }
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
              placeholder="Tìm lý do bảo trì..."
              value={keyword}
              onChange={(event) => {
                setKeyword(event.target.value);
                setPageNo(0);
              }}
            />
          </div>

          <div className="field-filter">
            <select value={fieldFilter} onChange={(event) => setFieldFilter(event.target.value)}>
              <option value="">Tất cả sân</option>
              {fields.map((field) => (
                <option key={field.id} value={field.id}>
                  {field.name}
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
      ) : displayedMaintenances.length === 0 ? (
        <div className="no-data">Chưa có lịch bảo trì nào</div>
      ) : (
        <table className="maintenance-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Sân</th>
              <th>Bắt đầu</th>
              <th>Kết thúc</th>
              <th>Lý do</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>

          <tbody>
            {displayedMaintenances.map((maintenance) => (
              <tr key={maintenance.id}>
                <td>{maintenance.id}</td>
                <td>{maintenance.fieldName}</td>
                <td>{formatDateTime(maintenance.startTime)}</td>
                <td>{formatDateTime(maintenance.endTime)}</td>
                <td>{maintenance.reason}</td>
                <td>{getStatusBadge(maintenance.startTime, maintenance.endTime)}</td>
                <td>
                  <Button size="sm" variant="warning" onClick={() => handleShow(maintenance)}>
                    <BsPencil /> Sửa
                  </Button>
                  <Button size="sm" variant="danger" onClick={() => confirmDelete(maintenance.id)}>
                    <BsTrash /> Xóa
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {pageData?.page && (
        <div className="pagination">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
            Trước
          </Button>

          <span>
            Trang {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button disabled={pageNo + 1 >= pageData.page.totalPages} onClick={() => setPageNo(pageNo + 1)}>
            Sau
          </Button>
        </div>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editingMaintenance ? "Sửa lịch bảo trì" : "Thêm lịch bảo trì"}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Sân</Form.Label>
              <Form.Select
                value={form.fieldId}
                onChange={(event) => setForm({ ...form, fieldId: Number(event.target.value) })}
                required
              >
                <option value="">Chọn sân</option>
                {fields.map((field) => (
                  <option key={field.id} value={field.id}>
                    {field.name} ({field.status})
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Thời gian bắt đầu</Form.Label>
              <Form.Control
                type="datetime-local"
                value={form.startTime}
                min={editingMaintenance ? undefined : toLocalDateTimeInput(new Date())}
                onChange={(event) => setForm({ ...form, startTime: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Thời gian kết thúc</Form.Label>
              <Form.Control
                type="datetime-local"
                value={form.endTime}
                onChange={(event) => setForm({ ...form, endTime: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Lý do</Form.Label>
              <Form.Control
                type="text"
                value={form.reason}
                onChange={(event) => setForm({ ...form, reason: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
              />
            </Form.Group>

            <Button type="submit" variant="success">
              {editingMaintenance ? "Cập nhật" : "Tạo mới"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
