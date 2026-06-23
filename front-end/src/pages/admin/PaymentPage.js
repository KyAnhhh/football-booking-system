import { useCallback, useEffect, useState } from "react";
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
  const [pageSize] = useState(10);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const loadPayments = useCallback(async () => {
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
  }, [pageNo, pageSize, sortBy, sortDir, search]);

  useEffect(() => {
    loadPayments();
  }, [loadPayments]);

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
        <h2>Quản lý thanh toán</h2>

        <div className="payment-actions">
          <Form.Control
            type="text"
            placeholder="Tìm theo trạng thái thanh toán..."
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
            <th onClick={() => toggleSort("paymentId")}>Mã thanh toán</th>
            <th onClick={() => toggleSort("paymentMethod")}>Phương thức</th>
            <th onClick={() => toggleSort("transactionCode")}>Mã giao dịch</th>
            <th onClick={() => toggleSort("orderStatus")}>Trạng thái đơn</th>
            <th onClick={() => toggleSort("createdAt")}>Ngày tạo</th>
            <th>Tổng tiền</th>
            <th onClick={() => toggleSort("paymentStatus")}>Trạng thái thanh toán</th>
            <th width="160">Thao tác</th>
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
                    Sửa
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => handleDeletePayment(p)}
                  >
                    Xóa
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="8" className="text-center text-muted">
                Chưa có thanh toán nào
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
            Trước
          </Button>

          <span>
            Trang {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button
            size="sm"
            disabled={pageNo + 1 >= pageData.page.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Sau
          </Button>
        </div>
      )}
    </div>
  );
}
