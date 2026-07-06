import { useCallback, useEffect, useState } from "react";
import {
  getOrders,
  updateOrderStatus,
  deleteOrder,
} from "../../services/orderService";
import { confirmOrderPayment } from "../../services/paymentService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/OrderPage.css";
import Swal from "sweetalert2";

export default function OrderPage() {
  const [orders, setOrders] = useState([]);
  const [pageData, setPageData] = useState(null);

  // filter – paging – sort
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(10);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  // modal
  const [show, setShow] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [status, setStatus] = useState("");
  const [confirmOrder, setConfirmOrder] = useState(null);

  const orderStatusColor = {
    PENDING: "bg-secondary",
    CONFIRMED: "bg-primary",
    SHIPPING: "bg-info",
    DELIVERED: "bg-success",
    CANCELLED: "bg-danger",
    COMPLETED: "bg-dark",
  };

  const orderStatusLabels = {
    PENDING: "Chờ xác nhận",
    CONFIRMED: "Đã xác nhận",
    SHIPPING: "Đang giao",
    DELIVERED: "Đã giao",
    CANCELLED: "Đã hủy",
    COMPLETED: "Hoàn tất",
  };

  const paymentMethodColor = {
    COD: "bg-secondary",
    VIETQR: "bg-success",
    CASH: "bg-dark",
  };

  const allowedStatusTransitions = {
    PENDING: ["CONFIRMED", "CANCELLED"],
    CONFIRMED: ["SHIPPING", "CANCELLED"],
    SHIPPING: ["DELIVERED"],
    DELIVERED: ["COMPLETED"],
  };

  const getAllowedStatuses = (order) => {
    const transitions = allowedStatusTransitions[order?.orderStatus] || [];
    return transitions.filter(
      (nextStatus) => nextStatus !== "CONFIRMED"
        || order?.paymentMethod !== "VIETQR"
        || order?.paymentStatus === "PAID"
    );
  };

  const loadOrders = useCallback(async () => {
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
      Swal.fire({
        icon: "error",
        title: "Failed to load orders",
        text: err.response?.data?.message || "An error occurred while loading orders.",
      });
    }
  }, [pageNo, pageSize, sortBy, sortDir, search, statusFilter]);

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  const toggleSort = (field) => {
    setPageNo(0);
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
    const allowedStatuses = getAllowedStatuses(order);
    if (allowedStatuses.length === 0) {
      Swal.fire({
        icon: "info",
        title: "Chưa thể cập nhật",
        text: order.paymentMethod === "VIETQR" && order.paymentStatus !== "PAID"
          ? "Hãy xác nhận thanh toán VietQR trước khi xác nhận đơn hàng."
          : "Đơn hàng không còn trạng thái hợp lệ để chuyển tiếp.",
      });
      return;
    }
    setSelectedOrder(order);
    setStatus(allowedStatuses[0]);
    setShow(true);
  };

  const handleUpdateStatus = async (e) => {
    e.preventDefault();
    if (!selectedOrder) return;
    try {
      await updateOrderStatus(selectedOrder.id, status);
      await loadOrders();
      setShow(false);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Không thể cập nhật trạng thái",
        text: err.message || "Không thể cập nhật trạng thái đơn hàng.",
      });
    }
  };

  const handleConfirmPayment = async (order) => {
    try {
      await confirmOrderPayment(order.id);
      await loadOrders();
      Swal.fire({
        icon: "success",
        title: "Đã xác nhận thanh toán",
        text: "Đã nhận tiền VietQR. Bây giờ có thể xác nhận đơn hàng.",
      });
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Không thể xác nhận thanh toán",
        text: err.message || "Giao dịch VietQR chưa ở trạng thái chờ xác nhận.",
      });
    }
  };

  const confirmDelete = async () => {
    try {
      await deleteOrder(confirmOrder.id);
      await loadOrders();
      setConfirmOrder(null);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Delete failed",
        text: err.response?.data?.message || "Failed to delete order",
      });
      setConfirmOrder(null);
    }
};

  return (
    <div className="order-page">
      <div className="order-header">
        <h2>Quản lý đơn hàng</h2>

        <div className="order-tools">
          <Form.Control
            type="text"
            placeholder="Tìm tên hoặc số điện thoại..."
            value={search}
            onChange={handleSearchChange}
            style={{ width: "220px" }}
          />

          <Form.Select
            value={statusFilter}
            onChange={handleStatusFilter}
            style={{ width: "200px" }}
          >
            <option value="">Tất cả trạng thái</option>
            <option value="PENDING">Chờ xác nhận</option>
            <option value="CONFIRMED">Đã xác nhận</option>
            <option value="SHIPPING">Đang giao</option>
            <option value="DELIVERED">Đã giao</option>
            <option value="CANCELLED">Đã huỷ</option>
          </Form.Select>
        </div>
      </div>

      <table className="order-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("id")}>ID</th>
            <th>Người nhận</th>
            <th>Số điện thoại</th>
            <th>Địa chỉ</th>
            <th onClick={() => toggleSort("orderDate")}>Ngày đặt</th>
            <th onClick={() => toggleSort("totalAmount")}>Tổng tiền</th>
            <th onClick={() => toggleSort("orderStatus")}>Trạng thái</th>
            <th>Phương thức thanh toán</th>
            <th>Thao tác</th>

          </tr>
        </thead>

        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.fullName}</td>
              <td>{o.phone}</td>
              <td>{o.address}</td>
              <td>{o.orderDate}</td>
              <td>{o.totalAmount.toLocaleString()}</td>
              <td>
                <span className={`badge ${orderStatusColor[o.orderStatus]}`}>
                  {orderStatusLabels[o.orderStatus] || o.orderStatus}
                </span>
              </td>
              <td>
                <span className={`badge ${paymentMethodColor[o.paymentMethod]}`}>
                  {o.paymentMethod}
                </span>
                <div className="mt-1">
                  <small>{o.paymentStatus || "UNPAID"}</small>
                </div>
              </td>

              <td>
                {o.orderStatus === "PENDING"
                  && o.paymentMethod === "VIETQR"
                  && o.paymentStatus === "PENDING" && (
                  <span
                    className="badge bg-success action-badge"
                    onClick={() => handleConfirmPayment(o)}
                  >
                    Xác nhận tiền
                  </span>
                )}

                {o.orderStatus === "PENDING"
                  && o.paymentMethod === "VIETQR"
                  && o.paymentStatus === "UNPAID" && (
                  <span className="badge bg-secondary action-badge disabled">
                    Chờ khách chuyển khoản
                  </span>
                )}

                <span
                  className={`badge bg-warning action-badge ${
                    getAllowedStatuses(o).length === 0 ? "disabled" : ""
                  }`}
                  onClick={() => {
                    if (getAllowedStatuses(o).length > 0) {
                      openStatusModal(o);
                    }
                  }}
                >
                  Cập nhật
                </span>

                {o.orderStatus === "PENDING" && (
                  <span
                    className="badge bg-danger action-badge"
                    onClick={() => setConfirmOrder(o)}
                  >
                    Hủy đơn
                  </span>
                )}
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
            Trước
          </Button>

          <span>
            Trang {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button
            disabled={pageNo + 1 >= pageData.page.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Sau
          </Button>
        </div>
      )}

      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Cập nhật trạng thái đơn hàng</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <p>
            Mã đơn hàng: <b>#{selectedOrder?.id}</b>
          </p>
          <Form onSubmit={handleUpdateStatus}>
            <Form.Select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              {getAllowedStatuses(selectedOrder).map((nextStatus) => (
                <option key={nextStatus} value={nextStatus}>
                  {orderStatusLabels[nextStatus] || nextStatus}
                </option>
              ))}
            </Form.Select>
            <Button type="submit" className="mt-3" variant="success">
              Cập nhật
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={!!confirmOrder} onHide={() => setConfirmOrder(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc muốn xóa đơn hàng <b>#{confirmOrder?.id}</b> không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setConfirmOrder(null)}>
            Hủy
          </Button>
          <Button variant="danger" onClick={confirmDelete}>
            Xóa
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
