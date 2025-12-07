import { useState, useEffect } from "react";
import { getBrands, createBrand, updateBrand, deleteBrand } from "../../services/brandService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/ProductPage.css";

export default function BrandPage() {
  const [brands, setBrands] = useState([]);
  const [show, setShow] = useState(false);
  const [editingBrand, setEditingBrand] = useState(null);
  const [confirmBrand, setConfirmBrand] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: "",
    logoUrl: ""
  });

  // load brand list
  const loadBrands = async () => {
    try {
      const data = await getBrands();
      setBrands(data);
    } catch (err) {
      console.error("Fetch brands failed:", err);
    }
  };

  useEffect(() => {
    loadBrands();
  }, []);

  // open modal create / edit
  const handleShow = (brand = null) => {
    if (brand) {
      setEditingBrand(brand);
      setForm({
        name: brand.name,
        description: brand.description,
        logoUrl: brand.logoUrl
      });
    } else {
      setEditingBrand(null);
      setForm({ name: "", description: "", logoUrl: "" });
    }
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setEditingBrand(null);
  };

  // submit form
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingBrand) {
        await updateBrand(editingBrand.id, form);
      } else {
        await createBrand(form);
      }
      await loadBrands();
      handleClose();
    } catch (err) {
      console.error("Save brand failed:", err);
    }
  };

  // open modal delete confirm
  const handleDeleteClick = (brand) => {
    setConfirmBrand(brand);
  };

  // confirm delete
  const confirmDelete = async () => {
    if (!confirmBrand) return;
    try {
      await deleteBrand(confirmBrand.id);
      await loadBrands();
      setConfirmBrand(null);
    } catch (err) {
      console.error("Delete brand failed:", err);
    }
  };

  return (
    <div className="product-page">
      <div className="product-header">
        <h2>Brand Management</h2>
        <Button variant="primary" onClick={() => handleShow()}>
          + Add Brand
        </Button>
      </div>

      <table className="product-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Logo</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {brands.map((b) => (
            <tr key={b.id}>
              <td>{b.id}</td>
              <td>{b.name}</td>
              <td>{b.description}</td>
              <td>
                {b.logoUrl ? (
                  <img src={b.logoUrl} alt={b.name} style={{ width: "60px" }} />
                ) : (
                  "N/A"
                )}
              </td>
              <td>
                <Button size="sm" variant="warning" onClick={() => handleShow(b)}>
                  Edit
                </Button>{" "}
                <Button size="sm" variant="danger" onClick={() => handleDeleteClick(b)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal Create / Edit Brand */}
      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{editingBrand ? "Edit Brand" : "Create Brand"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Logo URL</Form.Label>
              <Form.Control
                type="text"
                value={form.logoUrl}
                onChange={(e) => setForm({ ...form, logoUrl: e.target.value })}
              />
            </Form.Group>

            <Button variant="success" type="submit" style={{ backgroundColor: "blue" }}>
              {editingBrand ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Modal Confirm Delete */}
      <Modal show={!!confirmBrand} onHide={() => setConfirmBrand(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc chắn muốn xóa brand <b>{confirmBrand?.name}</b> không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setConfirmBrand(null)}>
            Cancel
          </Button>
          <Button variant="danger" onClick={confirmDelete}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
