import { useEffect, useState } from "react";
import {
  getOrders,
  updateOrderStatus,
  deleteOrder,
} from "../../services/orderService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/OrderPage.css";

export default function OrderPage() {
  const [orders, setOrders] = useState([]);
  const [pageData, setPageData] = useState(null);

  // filter – paging – sort
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  // modal
  const [show, setShow] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [status, setStatus] = useState("");
  const [confirmOrder, setConfirmOrder] = useState(null);

  const loadOrders = async () => {
    try {
      const data = await getOrders(
        pageNo,
        pageSize,
        sortBy,
        sortDir,
        search,
        statusFilter
      );
      setOrders(data.content);
      setPageData(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadOrders();
  }, [pageNo, sortBy, sortDir, search, statusFilter]);

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

  const handleStatusFilter = (e) => {
    setStatusFilter(e.target.value);
    setPageNo(0);
  };

  const openStatusModal = (order) => {
    setSelectedOrder(order);
    setStatus(order.status);
    setShow(true);
  };

  const handleUpdateStatus = async (e) => {
    e.preventDefault();
    try {
      await updateOrderStatus(selectedOrder.id, status);
      await loadOrders();
      setShow(false);
    } catch (err) {
      console.error(err);
    }
  };

  const confirmDelete = async () => {
    await deleteOrder(confirmOrder.id);
    await loadOrders();
    setConfirmOrder(null);
  };

  return (
    <div className="order-page">
      <div className="order-header">
        <h2>Order Management</h2>

        <div className="order-tools">
          <Form.Control
            type="text"
            placeholder="Search name / phone..."
            value={search}
            onChange={handleSearchChange}
            style={{ width: "220px" }}
          />

          <Form.Select
            value={statusFilter}
            onChange={handleStatusFilter}
            style={{ width: "200px" }}
          >
            <option value="">All Status</option>
            <option value="CHO_XAC_NHAN">Chờ xác nhận</option>
            <option value="DA_XAC_NHAN">Đã xác nhận</option>
            <option value="DANG_GIAO">Đang giao</option>
            <option value="DA_GIAO">Đã giao</option>
            <option value="DA_HUY">Đã huỷ</option>
          </Form.Select>
        </div>
      </div>

      <table className="order-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("id")}>ID</th>
            <th>User</th>
            <th onClick={() => toggleSort("totalAmount")}>Total</th>
            <th onClick={() => toggleSort("status")}>Status</th>
            <th>Receiver</th>
            <th>Phone</th>
            <th>Address</th>
            <th onClick={() => toggleSort("orderDate")}>Order Date</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.userId}</td>
              <td>{o.totalAmount.toLocaleString()} ₫</td>
              <td>{o.status}</td>
              <td>{o.fullName}</td>
              <td>{o.phone}</td>
              <td>{o.address}</td>
              <td>{o.orderDate}</td>
              <td>
                <Button
                  size="sm"
                  variant="warning"
                  onClick={() => openStatusModal(o)}
                >
                  Update
                </Button>{" "}
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => setConfirmOrder(o)}
                >
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {pageData && (
        <div className="pagination-section">
          <Button
            disabled={pageNo === 0}
            onClick={() => setPageNo(pageNo - 1)}
          >
            Prev
          </Button>

          <span>
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

      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Update Order Status</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleUpdateStatus}>
            <Form.Select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              <option value="CHO_XAC_NHAN">Chờ xác nhận</option>
              <option value="DA_XAC_NHAN">Đã xác nhận</option>
              <option value="DANG_GIAO">Đang giao</option>
              <option value="DA_GIAO">Đã giao</option>
              <option value="DA_HUY">Đã huỷ</option>
            </Form.Select>

            <Button type="submit" className="mt-3" variant="success">
              Update
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={!!confirmOrder} onHide={() => setConfirmOrder(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Confirm delete</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Delete order <b>#{confirmOrder?.id}</b>?
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
