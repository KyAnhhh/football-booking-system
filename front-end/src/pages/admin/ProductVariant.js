import { useEffect, useState } from "react";
import { Button, Modal, Form, Table } from "react-bootstrap";
import swal from "sweetalert";
import {
  createProductVariant,
  updateProductVariant,
  deleteProductVariant,
  getAllProductVariant
} from "../../services/productVariantService";

export default function ProductVariantPage({ product, onClose }) {
  const [variants, setVariants] = useState([]);
  const [editing, setEditing] = useState(null);

  const [form, setForm] = useState({
    sizeId: "",
    sizeCode: "",
    price: "",
    stock: ""
  });

  const loadVariants = async () => {
    const data = await getAllProductVariant();
    setVariants(data.filter(v => v.productId === product.id));
  };

  useEffect(() => {
    loadVariants();
  }, [product.id]);

  const openCreate = () => {
    setEditing(null);
    setForm({ sizeId: "", sizeCode: "", price: "", stock: "" });
  };

  const openEdit = (v) => {
    setEditing(v);
    setForm({
      sizeId: v.sizeId,
      sizeCode: v.sizeCode,
      price: v.price,
      stock: v.stock
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      productId: product.id,
      sizeId: form.sizeId,
      price: Number(form.price),
      stock: Number(form.stock)
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
    if (!(await swal({ text: "Delete variant?", buttons: true }))) return;
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
          <div className="d-flex gap-2">
            <Form.Control
              placeholder="Size (S, M, L)"
              value={form.sizeId}
              onChange={(e) =>
                setForm({ ...form, sizeId: e.target.value })
              }
              required
            />

            <Form.Control
              type="number"
              placeholder="Price"
              value={form.price}
              onChange={(e) =>
                setForm({ ...form, price: e.target.value })
              }
              required
            />

            <Form.Control
              type="number"
              placeholder="Stock"
              value={form.stock}
              onChange={(e) =>
                setForm({ ...form, stock: e.target.value })
              }
              required
            />

            <Button type="submit">
              {editing ? "Update" : "Add"}
            </Button>
          </div>
        </Form>

        <Table bordered>
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
