import { useState, useEffect } from "react";
import { getProducts, createProduct, updateProduct, deleteProduct } from "../../services/productService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/ProductPage.css";

export default function ProductPage() {
  const [products, setProducts] = useState([]);
  const [show, setShow] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stock: "",
    category: "",
    imageUrl: "",
  });

  // modal confirm delete
  const [confirmProduct, setConfirmProduct] = useState(null);

  const loadProducts = async () => {
    try {
      const data = await getProducts();
      setProducts(data);
    } catch (err) {
      console.error("Fetch products failed:", err);
    }
  };

  useEffect(() => {
    loadProducts();
  }, []);

  // open modal create / edit
  const handleShow = (product = null) => {
    if (product) {
      setEditingProduct(product);
      setForm({
        name: product.name,
        description: product.description,
        price: product.price,
        stock: product.stock,
        category: product.category,
        imageUrl: product.imageUrl || "",
      });
    } else {
      setEditingProduct(null);
      setForm({ name: "", description: "", price: "", stock: "", category: "", imageUrl: "" });
    }
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setEditingProduct(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingProduct) {
        await updateProduct(editingProduct.id, form);
      } else {
        await createProduct(form);
      }
      await loadProducts();
      handleClose();
    } catch (err) {
      console.error("Save product failed:", err);
    }
  };

  // mở modal confirm delete
  const handleDeleteClick = (product) => {
    setConfirmProduct(product);
  };

  // xác nhận delete
  const confirmDelete = async () => {
    if (!confirmProduct) return;
    try {
      await deleteProduct(confirmProduct.id);
      await loadProducts();
      setConfirmProduct(null);
    } catch (err) {
      console.error("Delete product failed:", err);
    }
  };

  return (
    <div className="product-page">
      <div className="product-header">
        <h2>Product Management</h2>
        <Button variant="primary" onClick={() => handleShow()}>
          + Add Product
        </Button>
      </div>

      <table className="product-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Category</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Image</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.category}</td>
              <td>{p.price}</td>
              <td>{p.stock}</td>
              <td>
                {p.imageUrl ? <img src={p.imageUrl} alt={p.name} style={{ width: "60px" }} /> : "N/A"}
              </td>
              <td>
                <Button size="sm" variant="warning" onClick={() => handleShow(p)}>
                  Edit
                </Button>{" "}
                <Button size="sm" variant="danger" onClick={() => handleDeleteClick(p)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal Create / Edit Product */}
      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{editingProduct ? "Edit Product" : "Create Product"}</Modal.Title>
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
              <Form.Label>Price</Form.Label>
              <Form.Control
                type="number"
                step="0.01"
                value={form.price}
                onChange={(e) => setForm({ ...form, price: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Stock</Form.Label>
              <Form.Control
                type="number"
                value={form.stock}
                onChange={(e) => setForm({ ...form, stock: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Category</Form.Label>
              <Form.Control
                type="text"
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Image URL</Form.Label>
              <Form.Control
                type="text"
                value={form.imageUrl}
                onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
              />
            </Form.Group>

            <Button variant="success" type="submit" style={{ backgroundColor: "blue" }}>
              {editingProduct ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Modal Confirm Delete */}
      <Modal show={!!confirmProduct} onHide={() => setConfirmProduct(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc chắn muốn xóa product <b>{confirmProduct?.name}</b> không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setConfirmProduct(null)}>
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