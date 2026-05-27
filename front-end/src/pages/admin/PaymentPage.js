import { useState, useEffect } from "react";
import { Button, Form } from "react-bootstrap";
import Swal from "sweetalert2";

import {
  getPayment,
  deletePaymentById,
} from "../../services/paymentService";

import "../../assets/styles/PaymentPage.css";

export default function PaymentPage() {
  const [payments, setPayments] = useState([]);
  const [pageData, setPageData] = useState(null);

  // Paging + Sort + Search
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const loadPayments = async () => {
    try {
      const data = await getPayment(
        pageNo,
        pageSize,
        sortBy,
        sortDir,
        search
      );

      if (data && data.content) {
        setPayments(data.content);
        setPageData(data);
      } else {
        setPayments([]);
        setPageData(null);
      }
    } catch (err) {
      console.error("Fetch payments failed:", err);
      setPayments([]);
    }
  };

  useEffect(() => {
    loadPayments();
  }, [pageNo, sortBy, sortDir, search]);

  const toggleSort = (field) => {
    if (sortBy === field) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortBy(field);
      setSortDir("asc");
    }
  };

  const handleDeletePayment = async (payment) => {
    const result = await Swal.fire({
      title: "Xác nhận xoá?",
      text: `Bạn có chắc muốn xoá payment #${payment.paymentId} không?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Xoá",
      cancelButtonText: "Huỷ",
      confirmButtonColor: "#d33",
    });

    if (!result.isConfirmed) return;

    try {
      await deletePaymentById(payment.id);

      Swal.fire({
        icon: "success",
        title: "Đã xoá",
        text: "Payment đã được xoá thành công",
        timer: 1500,
        showConfirmButton: false,
      });

      // Nếu xoá item cuối trang → lùi page
      if (payments.length === 1 && pageNo > 0) {
        setPageNo(pageNo - 1);
      } else {
        loadPayments();
      }
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Lỗi",
        text: err.message || "Xoá payment thất bại",
      });
    }
  };

  return (
    <div className="payment-page">
      {/* HEADER */}
      <div className="payment-header">
        <h2>Payment Management</h2>

        <div className="payment-actions">
          <Form.Control
            type="text"
            placeholder="Search by payment status..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPageNo(0);
            }}
          />
        </div>
      </div>

      {/* TABLE */}
      <table className="payment-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("paymentId")}>Payment ID</th>
            <th onClick={() => toggleSort("paymentMethod")}>Payment Method</th>
            <th onClick={() => toggleSort("transactionCode")}>Transaction Code</th>
            <th onClick={() => toggleSort("orderStatus")}>Order Status</th>
            <th onClick={() => toggleSort("createdAt")}>Created At</th>
            <th>Total Amount</th>
            <th onClick={() => toggleSort("paymentStatus")}>Payment Status</th>
            <th width="160">Actions</th>
          </tr>
        </thead>

        <tbody>
          {payments.length > 0 ? (
            payments.map((p) => (
              <tr key={p.paymentId}>
                <td>{p.paymentId}</td>
                <td>{p.paymentMethod}</td>
                <td>{p.transactionCode}</td>
                <td>{p.orderStatus}</td>
                <td>{p.createdAt}</td>
                <td>{p.totalAmount.toLocaleString()}</td>
                <td>{p.paymentStatus}</td>
                <td>
                  <Button size="sm" variant="warning">
                    Edit
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDeletePayment(p)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4" className="text-center text-muted">
                No payments found
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* PAGINATION */}
      {pageData && (
        <div className="pagination-section">
          <Button
            size="sm"
            disabled={pageNo === 0}
            onClick={() => setPageNo(pageNo - 1)}
          >
            Prev
          </Button>

          <span>
            Page {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button
            size="sm"
            disabled={pageNo + 1 >= pageData.page.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Next
          </Button>
        </div>
      )}
    </div>
  );
}
