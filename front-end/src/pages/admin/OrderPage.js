import { useState, useEffect } from "react";
import {
  getOrders,
  // getOrderById,
  createOrder,
  updateOrder,
  deleteOrder,
} from "../../services/orderService";
import { Modal, Button, Form } from "react-bootstrap";

export default function OrderPage() {
  const [orders, setOrders] = useState([]);
  const [show, setShow] = useState(false);
  const [editingOrder, setEditingOrder] = useState(null);
  const [confirmOrder, setConfirmOrder] = useState(null);

  const [form, setForm] = useState({
    userId: "",
    totalAmount: "",
    shippingCost: "",
    status: "",
    fullName: "",
    phone: "",
    address: "",
    orderDate: "",
  });

  // Load Order list (Admin)
  const loadOrders = async () => {
    try {
      const data = await getOrders();
      setOrders(data);
    } catch (err) {
      console.error("Fetch Orders failed:", err);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  // open modal create / edit
  const handleShow = (order = null) => {
    if (order) {
      setEditingOrder(order);
      setForm({
        userId: order.userId,
        totalAmount: order.totalAmount,
        shippingCost: order.shippingCost,
        status: order.status,
        fullName: order.fullName,
        phone: order.phone,
        address: order.address,
        orderDate: order.orderDate,
      });
    } else {
      setEditingOrder(null);
      setForm({
        userId: "",
        totalAmount: "",
        shippingCost: "",
        status: "",
        fullName: "",
        phone: "",
        address: "",
        orderDate: "",
      });
    }
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setEditingOrder(null);
  };

  // submit form
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingOrder) {
        await updateOrder(editingOrder.id, form);
      } else {
        await createOrder(form);
      }
      await loadOrders();
      handleClose();
    } catch (err) {
      console.error("Save Order failed:", err);
    }
  };

  // open modal delete confirm
  const handleDeleteClick = (order) => {
    setConfirmOrder(order);
  };

  // confirm delete
  const confirmDelete = async () => {
    if (!confirmOrder) return;
    try {
      await deleteOrder(confirmOrder.id);
      await loadOrders();
      setConfirmOrder(null);
    } catch (err) {
      console.error("Delete Order failed:", err);
    }
  };

  return (
    <div className="product-page">
      <div className="product-header">
        <h2>Order Management</h2>
        <Button variant="primary" onClick={() => handleShow()}>
          + Create Order
        </Button>
      </div>

      <table className="product-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>User</th>
            <th>Total</th>
            <th>Status</th>
            <th>Receiver</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Order Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.userId}</td>
              <td>{o.totalAmount}₫</td>
              <td>{o.status}</td>
              <td>{o.fullName}</td>
              <td>{o.phone}</td>
              <td>{o.address}</td>
              <td>{o.orderDate}</td>
              <td>
                <Button size="sm" variant="warning" onClick={() => handleShow(o)}>
                  Edit
                </Button>{" "}
                <Button size="sm" variant="danger" onClick={() => handleDeleteClick(o)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal Create / Edit */}
      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{editingOrder ? "Edit Order" : "Create Order"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>

            <Form.Group className="mb-3">
              <Form.Label>User ID</Form.Label>
              <Form.Control
                type="number"
                value={form.userId}
                onChange={(e) => setForm({ ...form, userId: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Total Amount</Form.Label>
              <Form.Control
                type="number"
                value={form.totalAmount}
                onChange={(e) => setForm({ ...form, totalAmount: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Shipping Cost</Form.Label>
              <Form.Control
                type="number"
                value={form.shippingCost}
                onChange={(e) => setForm({ ...form, shippingCost: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Status</Form.Label>
              <Form.Control
                type="text"
                value={form.status}
                onChange={(e) => setForm({ ...form, status: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Full Name</Form.Label>
              <Form.Control
                type="text"
                value={form.fullName}
                onChange={(e) => setForm({ ...form, fullName: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Phone</Form.Label>
              <Form.Control
                type="text"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Address</Form.Label>
              <Form.Control
                type="text"
                value={form.address}
                onChange={(e) => setForm({ ...form, address: e.target.value })}
              />
            </Form.Group>

            <Button variant="success" type="submit">
              {editingOrder ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Modal Confirm Delete */}
      <Modal show={!!confirmOrder} onHide={() => setConfirmOrder(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Confirm delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Are you sure you want to delete order <b>#{confirmOrder?.id}</b> ?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setConfirmOrder(null)}>
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
