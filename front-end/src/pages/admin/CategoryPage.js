import { useState, useEffect } from "react";
import { 
  getCategories, 
  createCategory, 
  updateCategory, 
  deleteCategory 
} from "../../services/categoryService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/CategoryPage.css";


export default function CategoryPage() {
  const [categories, setCategories] = useState([]);
  const [show, setShow] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);

    // Paging + Sorting + Search
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const [pageData, setPageData] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: ""
  });

  const [confirmCategory, setConfirmCategory] = useState(null);

  const loadCategories = async () => {
    try {
      const data = await getCategories(pageNo, pageSize, sortBy, sortDir, search);
      
      // Trường hợp API trả về dạng Page (Spring Data)
      if (data && typeof data === "object" && Array.isArray(data.content)) {
        setCategories(data.content);
        setPageData(data);
      } 
      // Trường hợp API trả về mảng
      else if (Array.isArray(data)) {
        setCategories(data);
        setPageData(data);
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
  }, [pageNo, sortBy, sortDir, search]);

  // Sort toggle
  const toggleSort = (field) => {
    if (sortBy === field) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortBy(field);
      setSortDir("asc");
    }
  };

  // Search debounce
  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    setPageNo(0); // reset page
  };

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
    <div className="category-page">
      <div className="category-header">
        <h2>Category Management</h2>
        <div className="user-actions">
          <Form.Control
            type="text"
            placeholder="Search by email..."
            value={search}
            onChange={handleSearchChange}
            style={{ width: "250px" }}
          />

          <Button variant="primary" onClick={() => handleShow()}>
            + Thêm Category
          </Button>
        </div>
      </div>

      <table className="category-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("id")}>ID</th>
            <th onClick={() => toggleSort("name")}>Name</th>
            <th onClick={() => toggleSort("description")}>Description</th>
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
      {/* Pagination */}
      {pageData && (
        <div className="pagination-section">
          <Button
            disabled={pageNo === 0}
            onClick={() => setPageNo(pageNo - 1)}
          >
            Prev
          </Button>

          <span style={{ margin: "0 10px" }}>
            Page {pageNo + 1} / {pageData.totalPages}
          </span>

          <Button
            disabled={pageNo + 1 >= pageData.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Next
          </Button>
        </div>
      )}

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
