import { useEffect, useState } from "react";
import { Button, Modal, Form, Table } from "react-bootstrap";
import swal from "sweetalert";

import {
  createProductVariant,
  updateProductVariant,
  deleteProductVariant,
  getAllProductVariant,
} from "../../services/productVariantService";

export default function ProductVariantPage({ product, onClose }) {
  const [variants, setVariants] = useState([]);
  const [editing, setEditing] = useState(null);

  const [form, setForm] = useState({
    sizeId: "",
    price: "",
    stock: "",
  });

  const [errors, setErrors] = useState({});

  const loadVariants = async () => {
    const data = await getAllProductVariant();
    setVariants(data.filter(v => v.productId === product.id));
  };

  useEffect(() => {
    loadVariants();
  }, [product.id]);

  const validateVariant = () => {
    const newErrors = {};

    if (!form.sizeId) {
      newErrors.sizeId = "Please select size";
    }

    if (!form.price || Number(form.price) <= 0) {
      newErrors.price = "Price must be greater than 0";
    }

    if (form.stock === "" || Number(form.stock) < 0) {
      newErrors.stock = "Stock must be 0 or greater";
    }

    const duplicated = variants.find(
      v =>
        v.sizeId === Number(form.sizeId) &&
        (!editing || v.id !== editing.id)
    );

    if (duplicated) {
      newErrors.sizeId = "This size already exists";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const openCreate = () => {
    setEditing(null);
    setForm({ sizeId: "", price: "", stock: "" });
    setErrors({});
  };

  const openEdit = (v) => {
    setEditing(v);
    setForm({
      sizeId: v.sizeId,
      price: v.price,
      stock: v.stock,
    });
    setErrors({});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateVariant()) return;

    const payload = {
      productId: product.id,
      sizeId: Number(form.sizeId),
      price: Number(form.price),
      stock: Number(form.stock),
    };

    if (editing) {
      await updateProductVariant(editing.id, payload);
      swal("Updated", "Variant updated", "success");
    } else {
      await createProductVariant(payload);
      swal("Created", "Variant created", "success");
    }

    loadVariants();
    openCreate();
  };

  const handleDelete = async (id) => {
    if (!(await swal({ text: "Delete this variant?", buttons: true }))) return;
    await deleteProductVariant(id);
    loadVariants();
  };

  return (
    <Modal show onHide={onClose} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>
          Variants of <b>{product.name}</b>
        </Modal.Title>
      </Modal.Header>

      <Modal.Body>
        <Form onSubmit={handleSubmit} className="mb-3">
          <div className="d-flex gap-2 align-items-start">
            <Form.Group>
              <Form.Control
                placeholder="Size ID"
                value={form.sizeId}
                isInvalid={!!errors.sizeId}
                onChange={(e) =>
                  setForm({ ...form, sizeId: e.target.value })
                }
              />
              <Form.Control.Feedback type="invalid">
                {errors.sizeId}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group>
              <Form.Control
                type="number"
                placeholder="Price"
                value={form.price}
                isInvalid={!!errors.price}
                onChange={(e) =>
                  setForm({ ...form, price: e.target.value })
                }
              />
              <Form.Control.Feedback type="invalid">
                {errors.price}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group>
              <Form.Control
                type="number"
                placeholder="Stock"
                value={form.stock}
                isInvalid={!!errors.stock}
                onChange={(e) =>
                  setForm({ ...form, stock: e.target.value })
                }
              />
              <Form.Control.Feedback type="invalid">
                {errors.stock}
              </Form.Control.Feedback>
            </Form.Group>

            <Button type="submit">
              {editing ? "Update" : "Add"}
            </Button>
          </div>
        </Form>

        <Table bordered hover>
          <thead>
            <tr>
              <th>Size</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Sold</th>
              <th width="140">Action</th>
            </tr>
          </thead>
          <tbody>
            {variants.map(v => (
              <tr key={v.id}>
                <td>{v.sizeCode}</td>
                <td>{v.price}</td>
                <td>{v.stock}</td>
                <td>{v.soldQuantity}</td>
                <td>
                  <Button size="sm" onClick={() => openEdit(v)}>
                    Edit
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDelete(v.id)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </Modal.Body>
    </Modal>
  );
}
