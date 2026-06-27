import { useCallback, useEffect, useState } from "react";
import { Button, Modal, Form, Table } from "react-bootstrap";
import swal from "sweetalert";

import {
  createProductVariant,
  updateProductVariant,
  deleteProductVariant,
  getAllProductVariant,
} from "../../services/productVariantService";
import { getInventoryItems } from "../../services/inventoryService";

const emptyForm = {
  sizeId: "",
  price: "",
  stock: "",
  inventoryItemId: "",
};

export default function ProductVariantPage({ product, onClose }) {
  const [variants, setVariants] = useState([]);
  const [inventoryItems, setInventoryItems] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});

  const loadVariants = useCallback(async () => {
    const data = await getAllProductVariant();
    setVariants(data.filter((variant) => variant.productId === product.id));
  }, [product.id]);

  const loadInventoryItems = useCallback(async () => {
    const data = await getInventoryItems({
      page: 0,
      size: 200,
      itemType: "PRODUCT_VARIANT",
    });
    setInventoryItems(data.content || []);
  }, []);

  useEffect(() => {
    loadVariants();
    loadInventoryItems().catch(console.error);
  }, [loadVariants, loadInventoryItems]);

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
      (variant) =>
        variant.sizeId === Number(form.sizeId) &&
        (!editing || variant.id !== editing.id)
    );

    if (duplicated) {
      newErrors.sizeId = "Kích cỡ này đã tồn tại";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setErrors({});
  };

  const openEdit = (variant) => {
    setEditing(variant);
    setForm({
      sizeId: variant.sizeId,
      price: variant.price,
      stock: variant.stock,
      inventoryItemId: variant.inventoryItemId || "",
    });
    setErrors({});
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!validateVariant()) return;

    const payload = {
      productId: product.id,
      sizeId: Number(form.sizeId),
      price: Number(form.price),
      stock: Number(form.stock),
      inventoryItemId: form.inventoryItemId
        ? Number(form.inventoryItemId)
        : null,
    };

    if (editing) {
      await updateProductVariant(editing.id, payload);
      swal("Đã cập nhật", "Biến thể đã được cập nhật", "success");
    } else {
      await createProductVariant(payload);
      swal("Đã tạo", "Biến thể đã được tạo", "success");
    }

    await loadVariants();
    await loadInventoryItems();
    openCreate();
  };

  const handleDelete = async (id) => {
    const confirmed = await swal({
      text: "Bạn có chắc muốn xóa biến thể này?",
      buttons: true,
    });
    if (!confirmed) return;

    await deleteProductVariant(id);
    await loadVariants();
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
          <div className="d-flex gap-2 align-items-start flex-wrap">
            <Form.Group>
              <Form.Control
                placeholder="ID kích cỡ"
                value={form.sizeId}
                isInvalid={!!errors.sizeId}
                onChange={(event) =>
                  setForm({ ...form, sizeId: event.target.value })
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
                onChange={(event) =>
                  setForm({ ...form, price: event.target.value })
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
                onChange={(event) =>
                  setForm({ ...form, stock: event.target.value })
                }
              />
              <Form.Control.Feedback type="invalid">
                {errors.stock}
              </Form.Control.Feedback>
            </Form.Group>

            <Form.Group style={{ minWidth: 220 }}>
              <Form.Select
                value={form.inventoryItemId}
                onChange={(event) =>
                  setForm({ ...form, inventoryItemId: event.target.value })
                }
              >
                <option value="">Tự tạo item kho</option>
                {inventoryItems.map((item) => (
                  <option key={item.id} value={item.id}>
                    {item.itemCode} - {item.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Button type="submit">{editing ? "Cập nhật" : "Thêm"}</Button>
          </div>
        </Form>

        <Table bordered hover responsive>
          <thead>
            <tr>
              <th>Kích cỡ</th>
              <th>Giá</th>
              <th>Tồn kho</th>
              <th>Đã bán</th>
              <th>Kho</th>
              <th width="140">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {variants.map((variant) => (
              <tr key={variant.id}>
                <td>{variant.sizeCode}</td>
                <td>{variant.price}</td>
                <td>{variant.stock}</td>
                <td>{variant.soldQuantity}</td>
                <td>{variant.inventoryItemId || "Tự động"}</td>
                <td>
                  <Button size="sm" onClick={() => openEdit(variant)}>
                    Sửa
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDelete(variant.id)}
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
