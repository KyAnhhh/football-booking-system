import { useEffect, useState } from "react";
import swal from "sweetalert";
import { Button, Modal, Form, Table } from "react-bootstrap";

import {
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct
} from "../../services/productService";

import { getAllBrandsForDropdown } from "../../services/brandService";
import { getAllCategoriesForDropdown } from "../../services/categoryService";

import ImagePage from "./ImagePage";
import ProductVariantPage from "./ProductVariant";
import "../../assets/styles/ProductPage.css"

export default function ProductPage() {
  const [products, setProducts] = useState([]);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);

  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);

  const [selectedImageProduct, setSelectedImageProduct] = useState(null);
  const [selectedVariantProduct, setSelectedVariantProduct] = useState(null);

  const [form, setForm] = useState({
    name: "",
    description: "",
    brandId: "",
    categoryId: ""
  });

  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};

    if (!form.name || form.name.trim().length < 3) {
      newErrors.name = "Product name must be at least 3 characters";
    }

    if (form.description && form.description.length > 500) {
      newErrors.description = "Description must be less than 500 characters";
    }

    if (!form.brandId) {
      newErrors.brandId = "Please select a brand";
    }

    if (!form.categoryId) {
      newErrors.categoryId = "Please select a category";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

const loadData = async () => {
  try {
    const [pRes, bRes, cRes] = await Promise.all([
      getProducts(),
      getAllBrandsForDropdown(),
      getAllCategoriesForDropdown()
    ]);

    if (Array.isArray(pRes)) {
      setProducts(pRes);
    } else if (Array.isArray(pRes?.content)) {
      setProducts(pRes.content);
    } else {
      console.error("Product API response invalid:", pRes);
      setProducts([]);
    }

    setBrands(Array.isArray(bRes) ? bRes : []);
    setCategories(Array.isArray(cRes) ? cRes : []);

  } catch (err) {
    swal("Error", err.message, "error");
  }
};


  useEffect(() => {
    loadData();
  }, []);

  const openCreate = () => {
    setEditingProduct(null);
    setForm({
      name: "",
      description: "",
      brandId: "",
      categoryId: ""
    });
    setShowModal(true);
  };

  const openEdit = (p) => {
    setEditingProduct(p);
    setForm({
      name: p.name,
      description: p.description,
      brandId: p.brand?.id || "",
      categoryId: p.category?.id || ""
    });
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    const payload = {
      name: form.name,
      description: form.description,
      brandId: Number(form.brandId),
      categoryId: Number(form.categoryId)
    };

    try {
      if (editingProduct) {
        await updateProduct(editingProduct.id, payload);
        swal("Success", "Product updated", "success");
      } else {
        await createProduct(payload);
        swal("Success", "Product created", "success");
      }

      setShowModal(false);
      loadData();
    } catch (err) {
      swal("Error", err.message, "error");
    }
  };

  const handleDelete = async (id) => {
    const ok = await swal({
      text: "Delete this product?",
      buttons: true
    });
    if (!ok) return;

    await deleteProduct(id);
    loadData();
  };

  return (
    <div className="container mt-4">
      <div className="product-header">
          <h2>Product Management</h2>

        <Button className="mb-3" onClick={openCreate}>
          + New Product
        </Button>
      </div>


      <Table bordered hover>
        <thead>
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Brand</th>
            <th>Category</th>
            <th width="320">Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p, i) => (
            <tr key={p.id}>
              <td>{i + 1}</td>
              <td>{p.name}</td>
              <td>{p.brandName}</td>
              <td>{p.categoryName}</td>
              <td>
                <Button size="sm" onClick={() => openEdit(p)}>
                  Edit
                </Button>{" "}
                <Button
                  size="sm"
                  variant="info"
                  onClick={() => setSelectedImageProduct(p)}
                >
                  Images
                </Button>{" "}
                <Button
                  size="sm"
                  variant="secondary"
                  onClick={() => setSelectedVariantProduct(p)}
                >
                  Variants
                </Button>{" "}
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => handleDelete(p.id)}
                >
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      {selectedImageProduct && (
        <ImagePage
          product={selectedImageProduct}
          onClose={() => setSelectedImageProduct(null)}
        />
      )}

      {selectedVariantProduct && (
        <ProductVariantPage
          product={selectedVariantProduct}
          onClose={() => setSelectedVariantProduct(null)}
        />
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Form onSubmit={handleSubmit}>
          <Modal.Header closeButton>
            <Modal.Title>
              {editingProduct ? "Edit Product" : "New Product"}
            </Modal.Title>
          </Modal.Header>

          <Modal.Body>
            <Form.Group>
              <Form.Label>Name</Form.Label>
              <Form.Control
                value={form.name}
                onChange={(e) =>
                  setForm({ ...form, name: e.target.value })
                }
                required
              />
            </Form.Group>

            <Form.Group className="mt-2">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={form.description}
                onChange={(e) =>
                  setForm({ ...form, description: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group className="mt-2">
              <Form.Label>Brand</Form.Label>
              <Form.Select
                value={form.brandId}
                onChange={(e) =>
                  setForm({ ...form, brandId: e.target.value })
                }
                required
              >
                <option value="">-- Select brand --</option>
                {brands.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mt-2">
              <Form.Label>Category</Form.Label>
              <Form.Select
                value={form.categoryId}
                onChange={(e) =>
                  setForm({ ...form, categoryId: e.target.value })
                }
                required
              >
                <option value="">-- Select category --</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Modal.Body>

          <Modal.Footer>
            <Button type="submit">Save</Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
}
