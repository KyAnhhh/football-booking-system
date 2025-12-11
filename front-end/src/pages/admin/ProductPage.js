import { useState, useEffect } from "react";
import swal from 'sweetalert';
import { getProducts, createProduct, updateProduct, deleteProduct } from "../../services/productService";
import { getAllBrandsForDropdown } from "../../services/brandService";
import { getAllCategoriesForDropdown } from "../../services/categoryService";
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
    discount: "",
    dimension: "",
    imageUrl: "",
    imageFile: null,
    categoryId: "",
    brandId: "",
  });

  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);

  // Paging + Sorting + Search
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const [pageData, setPageData] = useState(null);

  // confirm delete
  const [confirmProduct, setConfirmProduct] = useState(null);

  // Load products
  const loadProducts = async () => {
    try {
      const data = await getProducts(pageNo, pageSize, sortBy, sortDir, search);

      if (data?.content) {
        setProducts(data.content);
        setPageData(data);
      } else {
        setProducts([]);
      }
    } catch (err) {
      console.error("Fetch products failed:", err);
    }
  };

  useEffect(() => {
    loadProducts();

    // tải brands + categories cho dropdown
    (async () => {
      const [b, c] = await Promise.all([getAllBrandsForDropdown(), getAllCategoriesForDropdown()]);
      setBrands(b || []);
      setCategories(c || []);
    })();
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

  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    setPageNo(0);
  };

  // Modal create / edit
  const handleShow = (product = null) => {
    if (product) {
      setEditingProduct(product);
      setForm({
        name: product.name,
        description: product.description,
        price: product.price,
        stock: product.stock,
        discount: product.discount,
        dimension: product.dimension,
        imageUrl: product.imageUrl,
        imageFile: null,
        categoryId: product.categoryId,
        brandId: product.brandId,
      });
    } else {
      setEditingProduct(null);
      setForm({
        name: "",
        description: "",
        price: "",
        stock: "",
        discount: "",
        dimension: "",
        imageUrl: "",
        imageFile: null,
        categoryId: "",
        brandId: "",
      });
    }
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setEditingProduct(null);
  };

  // Submit
  const handleSubmit = async (e) => {
    e.preventDefault();

    let payload;

    // Nếu có file thì gửi FormData
    if (form.imageFile) {
      const fd = new FormData();
      Object.keys(form).forEach((key) => {
        if (key === "imageFile" && form.imageFile) fd.append("image", form.imageFile);
        else fd.append(key, form[key]);
      });
      payload = fd;
    } else {
      payload = {
        ...form,
        price: Number(form.price),
        stock: Number(form.stock),
        discount: Number(form.discount),
        dimension: Number(form.dimension),
        categoryId: Number(form.categoryId),
        brandId: Number(form.brandId),
        imageUrl: form.imageUrl || null,
      };
    }

    try {
      if (editingProduct) {
        await updateProduct(editingProduct.id, payload);
        swal({ text: "Product updated successfully!", icon: "success" });
      } else {
        await createProduct(payload);
        swal({ text: "Product created successfully!", icon: "success" });
      }

      loadProducts();
      handleClose();
    } catch (err) {
      console.error("Save product failed:", err);
      swal({ text: err.message || "Error saving product", icon: "error" });
    }
  };

  const handleDeleteClick = (product) => {
    setConfirmProduct(product);
  };

  const confirmDelete = async () => {
    if (!confirmProduct) return;
    try {
      await deleteProduct(confirmProduct.id);
      swal({ text: "Product deleted successfully!", icon: "success" });
      loadProducts();
      setConfirmProduct(null);
    } catch (err) {
      console.error("Delete failed:", err);
      swal({ text: err.message || "Error deleting product", icon: "error" });
    }
  };

  return (
    <div className="product-page">
      <div className="product-header">
        <h2>Product Management</h2>

        <div className="product-actions">
          <Form.Control
            type="text"
            placeholder="Search by name..."
            value={search}
            onChange={handleSearchChange}
            style={{ width: "250px" }}
          />

          <Button variant="primary" onClick={() => handleShow()}>
            + Thêm sản phẩm
          </Button>
        </div>
      </div>

      <table className="product-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("id")}>ID</th>
            <th onClick={() => toggleSort("name")}>Name</th>
            <th>Category</th>
            <th>Brand</th>
            <th onClick={() => toggleSort("price")}>Price</th>
            <th onClick={() => toggleSort("stock")}>Stock</th>
            <th>Dimension</th>
            <th>Discount</th>
            <th>Image</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.categoryId}</td>
              <td>{p.brandId}</td>
              <td>{p.price}</td>
              <td>{p.stock}</td>
              <td>{p.dimension}</td>
              <td>{p.discount}</td>
              <td>
                {p.imageUrl ? <img src={p.imageUrl} alt="" style={{ width: "60px" }} /> : "No image"}
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

      {/* Pagination */}
      {pageData && (
        <div className="pagination-section">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
            Prev
          </Button>

          <span style={{ margin: "0 10px" }}>
            Page {pageNo + 1} / {pageData.totalPages}
          </span>

          <Button disabled={pageNo + 1 >= pageData.totalPages} onClick={() => setPageNo(pageNo + 1)}>
            Next
          </Button>
        </div>
      )}

      {/* Modal Create / Edit */}
      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{editingProduct ? "Edit Product" : "Create Product"}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            {/* name */}
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                required
              />
            </Form.Group>

            {/* description */}
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
              />
            </Form.Group>

            {/* price */}
            <Form.Group className="mb-3">
              <Form.Label>Price</Form.Label>
              <Form.Control
                type="number"
                value={form.price}
                onChange={(e) => setForm({ ...form, price: e.target.value })}
                required
              />
            </Form.Group>

            {/* stock */}
            <Form.Group className="mb-3">
              <Form.Label>Stock</Form.Label>
              <Form.Control
                type="number"
                value={form.stock}
                onChange={(e) => setForm({ ...form, stock: e.target.value })}
                required
              />
            </Form.Group>

            {/* dimension */}
            <Form.Group className="mb-3">
              <Form.Label>Dimension</Form.Label>
              <Form.Control
                type="number"
                value={form.dimension}
                onChange={(e) => setForm({ ...form, dimension: e.target.value })}
                required
              />
            </Form.Group>

            {/* discount */}
            <Form.Group className="mb-3">
              <Form.Label>Discount</Form.Label>
              <Form.Control
                type="number"
                value={form.discount}
                onChange={(e) => setForm({ ...form, discount: e.target.value })}
              />
            </Form.Group>

            {/* imageUrl */}
            <Form.Group className="mb-3">
              <Form.Label>Image URL</Form.Label>
              <Form.Control
                type="text"
                value={form.imageUrl}
                onChange={(e) => setForm({ ...form, imageUrl: e.target.value })}
                placeholder="Paste image URL or upload file"
              />
            </Form.Group>

            {/* image file */}
            <Form.Group className="mb-3">
              <Form.Label>Upload Image</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={(e) =>
                  setForm({ ...form, imageFile: e.target.files?.[0] || null })
                }
              />
            </Form.Group>

            {/* category */}
            <Form.Group className="mb-3">
              <Form.Label>Category</Form.Label>
              <Form.Select
                value={form.categoryId}
                onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                required
              >
                <option value="">-- Select Category --</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            {/* brand */}
            <Form.Group className="mb-3">
              <Form.Label>Brand</Form.Label>
              <Form.Select
                value={form.brandId}
                onChange={(e) => setForm({ ...form, brandId: e.target.value })}
                required
              >
                <option value="">-- Select Brand --</option>
                {brands.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Button type="submit" variant="success" style={{ backgroundColor: "blue" }}>
              {editingProduct ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Confirm Delete */}
      <Modal show={!!confirmProduct} onHide={() => setConfirmProduct(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc chắn muốn xóa product <b>{confirmProduct?.name}</b>?
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
