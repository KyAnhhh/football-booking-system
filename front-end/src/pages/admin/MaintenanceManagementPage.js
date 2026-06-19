import { useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import { BsSearch, BsPlus, BsPencil, BsTrash } from "react-icons/bs";
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
      const data = await getFields({ page: 0, size: 1000 });
      setFields(data?.content || []);
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

      setMaintenances(data?.content || []);
      setPageData(data || null);
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

  const displayedMaintenances = fieldFilter
    ? maintenances.filter((maintenance) => String(maintenance.fieldId) === String(fieldFilter))
    : maintenances;

  const formatDateTimeForInput = (dateTimeStr) => {
    if (!dateTimeStr) return "";
    return new Date(dateTimeStr).toISOString().slice(0, 16);
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

  const handleSubmit = async (event) => {
    event.preventDefault();
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
    if (!window.confirm("Delete this maintenance schedule?")) return;
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
        <h2>Maintenance Management</h2>

        <div className="filters">
          <div className="search-box">
            <BsSearch className="search-icon" />
            <input
              type="text"
              placeholder="Search reason..."
              value={keyword}
              onChange={(event) => {
                setKeyword(event.target.value);
                setPageNo(0);
              }}
            />
          </div>

          <div className="field-filter">
            <select value={fieldFilter} onChange={(event) => setFieldFilter(event.target.value)}>
              <option value="">All fields</option>
              {fields.map((field) => (
                <option key={field.id} value={field.id}>
                  {field.name}
                </option>
              ))}
            </select>
          </div>

          <Button className="add-btn" onClick={() => handleShow()}>
            <BsPlus /> Add maintenance
          </Button>
        </div>
      </div>

      {loading ? (
        <div className="loading">Loading...</div>
      ) : displayedMaintenances.length === 0 ? (
        <div className="no-data">No maintenance schedules found</div>
      ) : (
        <table className="maintenance-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Field</th>
              <th>Start time</th>
              <th>End time</th>
              <th>Reason</th>
              <th>Status</th>
              <th>Actions</th>
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
                    <BsPencil /> Edit
                  </Button>
                  <Button size="sm" variant="danger" onClick={() => confirmDelete(maintenance.id)}>
                    <BsTrash /> Delete
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
            Prev
          </Button>

          <span>
            Page {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button disabled={pageNo + 1 >= pageData.page.totalPages} onClick={() => setPageNo(pageNo + 1)}>
            Next
          </Button>
        </div>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editingMaintenance ? "Edit maintenance" : "Add maintenance"}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Field</Form.Label>
              <Form.Select
                value={form.fieldId}
                onChange={(event) => setForm({ ...form, fieldId: Number(event.target.value) })}
                required
              >
                <option value="">Select field</option>
                {fields.map((field) => (
                  <option key={field.id} value={field.id}>
                    {field.name} ({field.status})
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Start time</Form.Label>
              <Form.Control
                type="datetime-local"
                value={form.startTime}
                onChange={(event) => setForm({ ...form, startTime: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>End time</Form.Label>
              <Form.Control
                type="datetime-local"
                value={form.endTime}
                onChange={(event) => setForm({ ...form, endTime: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Reason</Form.Label>
              <Form.Control
                type="text"
                value={form.reason}
                onChange={(event) => setForm({ ...form, reason: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
              />
            </Form.Group>

            <Button type="submit" variant="success">
              {editingMaintenance ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
