import { useCallback, useEffect, useState } from "react";
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

  const loadVariants = useCallback(async () => {
    const data = await getAllProductVariant();
    setVariants(data.filter(v => v.productId === product.id));
  }, [product.id]);

  useEffect(() => {
    loadVariants();
  }, [loadVariants]);

  const validateVariant = () => {
    const newErrors = {};

    if (!form.sizeId) {
      newErrors.sizeId = "Vui lòng chọn kích cỡ";
    }

    if (!form.price || Number(form.price) <= 0) {
      newErrors.price = "Giá phải lớn hơn 0";
    }

    if (form.stock === "" || Number(form.stock) < 0) {
      newErrors.stock = "Tồn kho phải lớn hơn hoặc bằng 0";
    }

    const duplicated = variants.find(
      v =>
        v.sizeId === Number(form.sizeId) &&
        (!editing || v.id !== editing.id)
    );

    if (duplicated) {
      newErrors.sizeId = "Kích cỡ này đã tồn tại";
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
      swal("Đã cập nhật", "Biến thể đã được cập nhật", "success");
    } else {
      await createProductVariant(payload);
      swal("Đã tạo", "Biến thể đã được tạo", "success");
    }

    loadVariants();
    openCreate();
  };

  const handleDelete = async (id) => {
    if (!(await swal({ text: "Bạn có chắc muốn xóa biến thể này?", buttons: true }))) return;
    await deleteProductVariant(id);
    loadVariants();
  };

  return (
    <Modal show onHide={onClose} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>
          Biến thể của <b>{product.name}</b>
        </Modal.Title>
      </Modal.Header>

      <Modal.Body>
        <Form onSubmit={handleSubmit} className="mb-3">
          <div className="d-flex gap-2 align-items-start">
            <Form.Group>
              <Form.Control
                placeholder="ID kích cỡ"
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
                placeholder="Giá"
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
                placeholder="Tồn kho"
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
              {editing ? "Cập nhật" : "Thêm"}
            </Button>
          </div>
        </Form>

        <Table bordered hover>
          <thead>
            <tr>
              <th>Kích cỡ</th>
              <th>Giá</th>
              <th>Tồn kho</th>
              <th>Đã bán</th>
              <th width="140">Thao tác</th>
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
                    Sửa
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDelete(v.id)}
                  >
                    Xóa
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
