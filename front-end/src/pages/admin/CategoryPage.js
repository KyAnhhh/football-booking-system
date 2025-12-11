import { useState, useEffect } from "react";
import { 
  getCategories, 
  createCategory, 
  updateCategory, 
  deleteCategory 
} from "../../services/categoryService";
import { Modal, Button, Form } from "react-bootstrap";

export default function CategoryPage() {
  const [categories, setCategories] = useState([]);
  const [show, setShow] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: ""
  });

  const [confirmCategory, setConfirmCategory] = useState(null);

  // -------- FIXED LOAD CATEGORIES --------
  const loadCategories = async () => {
    try {
      const data = await getCategories();

      // Trường hợp API trả về dạng Page (Spring Data)
      if (data && typeof data === "object" && Array.isArray(data.content)) {
        setCategories(data.content);
      } 
      // Trường hợp API trả về mảng
      else if (Array.isArray(data)) {
        setCategories(data);
      } 
      else {
        setCategories([]); // fallback tránh map lỗi
      }

    } catch (err) {
      console.error("Fetch categories failed:", err);
      setCategories([]); // tránh break UI
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const handleShow = (category = null) => {
    if (category) {
      setEditingCategory(category);
      setForm({
        name: category.name,
        description: category.description
      });
    } else {
      setEditingCategory(null);
      setForm({ name: "", description: "" });
    }
    setShow(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCategory) {
        await updateCategory(editingCategory.id, form);
      } else {
        await createCategory(form);
      }
      await loadCategories();
      setShow(false);
    } catch (err) {
      console.error("Save category failed:", err);
    }
  };

  const confirmDelete = async () => {
    if (!confirmCategory) return;
    try {
      await deleteCategory(confirmCategory.id);
      await loadCategories();
      setConfirmCategory(null);
    } catch (err) {
      console.error("Delete category failed:", err);
    }
  };

  return (
    <div className="product-page">
      <div className="product-header">
        <h2>Category Management</h2>
        <Button variant="primary" onClick={() => handleShow()}>
          + Add Category
        </Button>
      </div>

      <table className="product-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {categories.length > 0 ? (
            categories.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>{c.name}</td>
                <td>{c.description}</td>
                <td>
                  <Button variant="warning" size="sm" onClick={() => handleShow(c)}>
                    Edit
                  </Button>{" "}
                  <Button variant="danger" size="sm" onClick={() => setConfirmCategory(c)}>
                    Delete
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4" style={{ textAlign: "center", color: "gray" }}>
                No categories found.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Create/Edit Modal */}
      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editingCategory ? "Edit Category" : "Create Category"}</Modal.Title>
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
                type="text" 
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
              />
            </Form.Group>

            <Button type="submit" variant="success">
              {editingCategory ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Confirm Delete Modal */}
      <Modal show={!!confirmCategory} onHide={() => setConfirmCategory(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc chắn muốn xóa category <b>{confirmCategory?.name}</b> không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setConfirmCategory(null)}>Cancel</Button>
          <Button variant="danger" onClick={confirmDelete}>Delete</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
