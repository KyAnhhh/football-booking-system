import { useState, useEffect } from "react";
import { 
  getBrands, 
  createBrand, 
  updateBrand, 
  deleteBrand 
} from "../../services/brandService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/BrandPage.css"

export default function BrandPage() {
  const [brands, setBrands] = useState([]);
  const [show, setShow] = useState(false);
  const [editingBrand, setEditingBrand] = useState(null);
  const [confirmBrand, setConfirmBrand] = useState(null);

  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const [pageData, setPageData] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: "",
    logoUrl: ""
  });

  const loadBrands = async () => {
    try {
      const data = await getBrands(pageNo, pageSize, sortBy, sortDir, search);

      // Trường hợp API trả về dạng Page
      if (data && typeof data === "object" && Array.isArray(data.content)) {
        setBrands(data.content);
        setPageData(data);
      }
      // Trả về list bình thường
      else if (Array.isArray(data)) {
        setBrands(data);
        setPageData(data);
      } 
      else {
        setBrands([]); // tránh lỗi map
      }

    } catch (err) {
      console.error("Fetch brands failed:", err);
      setBrands([]);
    }
  };

  useEffect(() => {
    loadBrands();
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

  // Mở modal create/edit
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingBrand) {
        await updateBrand(editingBrand.id, form);
      } else {
        await createBrand(form);
      }

      await loadBrands();
      setShow(false);
    } catch (err) {
      console.error("Save brand failed:", err);
    }
  };

  // Confirm delete
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
    <div className="brand-page">
      <div className="brand-header">
        <h2>Brand Management</h2>
        <div className="user-actions">
          <Form.Control
            type="text"
            placeholder="Search by email..."
            value={search}
            onChange={handleSearchChange}
            style={{ width: "250px" }}
          />

          <Button variant="primary" onClick={() => handleShow()}>
            + Thêm brand
          </Button>
        </div>
      </div>

      <table className="brand-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("id")}>ID</th>
            <th onClick={() => toggleSort("name")}>Name</th>
            <th onClick={() => toggleSort("description")}>Description</th>
            <th onClick={() => toggleSort("logo")}>Logo</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {brands.length > 0 ? (
            brands.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{b.name}</td>
                <td>{b.description}</td>
                <td>
                  {b.logoUrl ? (
                    <img 
                      src={b.logoUrl} 
                      alt={b.name} 
                      style={{ width: "60px" }} 
                    />
                  ) : "N/A"}
                </td>
                <td>
                  <Button 
                    size="sm" 
                    variant="warning" 
                    onClick={() => handleShow(b)}
                  >
                    Edit
                  </Button>{" "}
                  <Button 
                    size="sm" 
                    variant="danger"
                    onClick={() => setConfirmBrand(b)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="5" style={{ textAlign: "center", color: "gray" }}>
                No brands found.
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

      {/* Modal Create / Edit */}
      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingBrand ? "Edit Brand" : "Create Brand"}
          </Modal.Title>
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

            <Button variant="success" type="submit">
              {editingBrand ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Confirm Delete Modal */}
      <Modal show={!!confirmBrand} onHide={() => setConfirmBrand(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc muốn xóa brand <b>{confirmBrand?.name}</b> không?
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
