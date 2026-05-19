import { useState, useEffect } from "react";
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
    location: "",
    pricePerHour: "",
    type: "",
    status: ""
  });

  const loadFields = async () => {
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
  };

  useEffect(() => {
    loadFields();
  }, [pageNo, keyword, status, type]);

  const handleShow = (field = null) => {
    if (field) {
      setEditingField(field);
      setForm({
        name: field.name,
        location: field.location,
        pricePerHour: field.pricePerHour,
        type: field.type,
        status: field.status
      });
    } else {
      setEditingField(null);
      setForm({
        name: "",
        location: "",
        pricePerHour: "",
        type: "",
        status: ""
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
        <h2>Football Field Management</h2>

        <div className="filters">
          <Form.Control
            placeholder="Search..."
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
            <option value="">All Status</option>
            <option value="AVAILABLE">Available</option>
            <option value="MAINTENANCE">Maintenance</option>
            <option value="UNAVAILABLE">Unavailability</option>
          </Form.Select>

          <Form.Select
            value={type}
            onChange={(e) => setType(e.target.value)}
          >
            <option value="">All Type</option>
            <option value="FIVE_A_SIDE">5 vs 5</option>
            <option value="SEVEN_A_SIDE">7 vs 7</option>
            <option value="ELEVEN_A_SIDE">11 vs 11</option>
          </Form.Select>

          <Button className="add-field" onClick={() => handleShow()}>+ Add Field</Button>
        </div>
      </div>

      <table className="field-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Address</th>
            <th>Price</th>
            <th>Type</th>
            <th>Status</th>
            <th>Actions</th>
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
                    <option value="AVAILABLE">Available</option>
                    <option value="MAINTENANCE">Maintenance</option>
                    <option value="UNAVAILABLE">Unavailability</option>
                  </Form.Select>
                </td>

                <td>
                  <Button
                    size="sm"
                    variant="warning"
                    onClick={() => handleShow(f)}
                  >
                    Edit
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => setConfirmField(f)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" style={{ textAlign: "center" }}>
                No fields found
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {pageData && (
        <div className="pagination">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
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

      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingField ? "Edit Field" : "Create Field"}
          </Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group>
              <Form.Label>Name</Form.Label>
              <Form.Control
                value={form.name}
                onChange={(e) =>
                  setForm({ ...form, name: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Location</Form.Label>
              <Form.Control
                value={form.location}
                onChange={(e) =>
                  setForm({ ...form, location: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Price Per Hour</Form.Label>
              <Form.Control
                type="number"
                value={form.pricePerHour}
                onChange={(e) =>
                  setForm({ ...form, pricePerHour: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Type</Form.Label>
              <Form.Select
                value={form.type}
                onChange={(e) =>
                  setForm({ ...form, type: e.target.value })
                }
              >
                <option value="">Select</option>
                <option value="5">5 vs 5</option>
                <option value="7">7 vs 7</option>
                <option value="11">11 vs 11</option>
              </Form.Select>
            </Form.Group>

            <Form.Group>
              <Form.Label>Status</Form.Label>
              <Form.Select
                value={form.status}
                onChange={(e) =>
                  setForm({ ...form, status: e.target.value })
                }
              >
                <option value="">Select</option>
                <option value="AVAILABLE">Available</option>
                <option value="MAINTENANCE">Maintenance</option>
              </Form.Select>
            </Form.Group>

            <Button type="submit" variant="success" className="mt-3">
              {editingField ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={!!confirmField} onHide={() => setConfirmField(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Confirm Delete</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          Delete field <b>{confirmField?.name}</b> ?
        </Modal.Body>

        <Modal.Footer>
          <Button onClick={() => setConfirmField(null)}>Cancel</Button>
          <Button variant="danger" onClick={confirmDelete}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}