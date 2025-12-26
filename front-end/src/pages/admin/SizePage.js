import { useEffect, useState } from "react";
import { Button, Modal, Form, Table } from "react-bootstrap";
import swal from "sweetalert";
import "../../assets/styles/SizePage.css"

import {
  getSizes,
  createSize,
  updateSize,
  deleteSize,
} from "../../services/sizeService";

export default function SizePage() {
  const [sizes, setSizes] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);

  const [form, setForm] = useState({
    code: "",
    name: "",
    description: ""
  });

  const [errors, setErrors] = useState({});

  const loadSizes = async () => {
    try {
      const data = await getSizes();
      setSizes(data || []);
    } catch (err) {
      swal("Error", err.message, "error");
    }
  };

  useEffect(() => {
    loadSizes();
  }, []);

  const validate = () => {
    const e = {};

    if (!form.code.trim()) {
      e.code = "Size code is required";
    } else if (form.code.length > 10) {
      e.code = "Max 10 characters";
    } else if (
      sizes.some(
        (s) =>
          s.code === form.code.toUpperCase() &&
          s.id !== editing?.id
      )
    ) {
      e.code = "Size code already exists";
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const openCreate = () => {
    setEditing(null);
    setForm({ code: "", name: "" });
    setErrors({});
    setShowModal(true);
  };

  const openEdit = (size) => {
    setEditing(size);
    setForm({
      code: size.code,
      name: size.name || "",
      description: size.description
    });
    setErrors({});
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      code: form.code.toUpperCase(),
      name: form.name,
    };

    try {
      if (editing) {
        await updateSize(editing.id, payload);
        swal("Success", "Size updated", "success");
      } else {
        await createSize(payload);
        swal("Success", "Size created", "success");
      }

      setShowModal(false);
      loadSizes();
    } catch (err) {
      swal("Error", err.message, "error");
    }
  };

  const handleDelete = async (id) => {
    const ok = await swal({
      text: "Delete this size?",
      icon: "warning",
      buttons: true,
      dangerMode: true,
    });

    if (!ok) return;

    try {
      await deleteSize(id);
      swal("Deleted", "Size removed", "success");
      loadSizes();
    } catch (err) {
      swal("Error", err.message, "error");
    }
  };

  return (
    <div className="container mt-4">
      <div className="size-header">
        <h3>Size Management</h3>

        <Button className="mb-3" onClick={openCreate}>
          + Thêm Size
        </Button>
      </div>

      <Table bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Code</th>
            <th>Name</th>
            <th>Description</th>
            <th width="180">Actions</th>
          </tr>
        </thead>
        <tbody>
          {sizes.map((s, i) => (
            <tr key={s.id}>
              <td>{i + 1}</td>
              <td><b>{s.code}</b></td>
              <td>{s.name}</td>
              <td>{s.description}</td>
              <td>
                <Button
                  size="sm"
                  onClick={() => openEdit(s)}
                >
                  Edit
                </Button>{" "}
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => handleDelete(s.id)}
                >
                  Delete
                </Button>
              </td>
            </tr>
          ))}
          {sizes.length === 0 && (
            <tr>
              <td colSpan={4} className="text-center">
                No sizes found
              </td>
            </tr>
          )}
        </tbody>
      </Table>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Form onSubmit={handleSubmit}>
          <Modal.Header closeButton>
            <Modal.Title>
              {editing ? "Edit Size" : "Create Size"}
            </Modal.Title>
          </Modal.Header>

          <Modal.Body>
            <Form.Group>
              <Form.Label>Size Code</Form.Label>
              <Form.Control
                value={form.code}
                onChange={(e) =>
                  setForm({ ...form, code: e.target.value })
                }
                isInvalid={!!errors.code}
              />
              <Form.Control.Feedback type="invalid">
                {errors.code}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mt-2">
              <Form.Label>Name (optional)</Form.Label>
              <Form.Control
                value={form.name}
                onChange={(e) =>
                  setForm({ ...form, name: e.target.value })
                }
              />
            </Form.Group>
            
            <Form.Group className="mt-2">
              <Form.Label>Description (optional)</Form.Label>
              <Form.Control
                value={form.description}
                onChange={(e) =>
                  setForm({ ...form, description: e.target.value })
                }
              />
            </Form.Group>
          </Modal.Body>

          <Modal.Footer>
            <Button type="submit">
              Save
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
}
