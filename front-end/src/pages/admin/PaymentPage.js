import { useCallback, useEffect, useState } from "react";
import { Button, Form } from "react-bootstrap";
import Swal from "sweetalert2";

import {
  confirmOrderPayment,
  getPayment,
} from "../../services/paymentService";
import {
  completeRefund,
  failRefund,
  getAdminRefunds,
  reviewRefundPayment,
  startRefundProcessing,
} from "../../services/refundService";

import "../../assets/styles/PaymentPage.css";

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN").format(Number(value || 0));

const refundStatusLabel = {
  PAYMENT_REVIEW: "Chờ kiểm tra tiền",
  AWAITING_INFORMATION: "Chờ khách cung cấp tài khoản",
  PENDING: "Chờ hoàn tiền",
  PROCESSING: "Đang xử lý",
  COMPLETED: "Đã hoàn tiền",
  FAILED: "Thất bại/cần bổ sung",
};

export default function PaymentPage() {
  const [payments, setPayments] = useState([]);
  const [pageData, setPageData] = useState(null);
  const [refunds, setRefunds] = useState([]);
  const [refundPageData, setRefundPageData] = useState(null);

  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(10);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const [refundPageNo, setRefundPageNo] = useState(0);
  const [refundStatus, setRefundStatus] = useState("");

  const loadPayments = useCallback(async () => {
    try {
      const data = await getPayment(pageNo, pageSize, sortBy, sortDir, search);

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

  const loadRefunds = useCallback(async () => {
    try {
      const data = await getAdminRefunds({
        pageNo: refundPageNo,
        pageSize,
        status: refundStatus,
      });

      if (data && data.content) {
        setRefunds(data.content);
        setRefundPageData(data);
      } else {
        setRefunds([]);
        setRefundPageData(null);
      }
    } catch (err) {
      console.error("Fetch refunds failed:", err);
      setRefunds([]);
    }
  }, [refundPageNo, pageSize, refundStatus]);

  useEffect(() => {
    loadPayments();
  }, [loadPayments]);

  useEffect(() => {
    loadRefunds();
  }, [loadRefunds]);

  const toggleSort = (field) => {
    if (sortBy === field) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortBy(field);
      setSortDir("asc");
    }
  };

  const refreshAll = () => {
    loadPayments();
    loadRefunds();
  };

  const handleConfirmPayment = async (payment) => {
    const result = await Swal.fire({
      title: "Xác nhận giao dịch?",
      text: `Xác nhận đã nhận tiền cho đơn #${payment.orderId}?`,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Xác nhận",
      cancelButtonText: "Giữ lại",
    });

    if (!result.isConfirmed) return;

    try {
      await confirmOrderPayment(payment.orderId);
      Swal.fire({
        icon: "success",
        title: "Đã xác nhận",
        text: "Thanh toán VietQR đã được ghi nhận",
        timer: 1500,
        showConfirmButton: false,
      });
      refreshAll();
    } catch (err) {
      Swal.fire("Lỗi", err.message || "Xác nhận payment thất bại", "error");
    }
  };

  const handleReviewPayment = async (refund, paymentReceived) => {
    const result = await Swal.fire({
      title: paymentReceived ? "Đã nhận tiền?" : "Chưa nhận tiền?",
      text: paymentReceived
        ? "Yêu cầu sẽ chuyển sang bước chờ khách cung cấp tài khoản hoàn tiền."
        : "Payment sẽ được đánh dấu thất bại và refund không tiếp tục xử lý.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: paymentReceived ? "Đã nhận tiền" : "Chưa nhận tiền",
      cancelButtonText: "Hủy",
    });
    if (!result.isConfirmed) return;

    try {
      await reviewRefundPayment(refund.id, paymentReceived);
      Swal.fire("Đã cập nhật", "Trạng thái refund đã được cập nhật.", "success");
      refreshAll();
    } catch (err) {
      Swal.fire("Lỗi", err.message || "Không thể review payment.", "error");
    }
  };

  const handleStartProcessing = async (refund) => {
    try {
      await startRefundProcessing(refund.id);
      Swal.fire("Đang xử lý", "Refund đã chuyển sang trạng thái xử lý.", "success");
      refreshAll();
    } catch (err) {
      Swal.fire("Lỗi", err.message || "Không thể bắt đầu xử lý refund.", "error");
    }
  };

  const handleCompleteRefund = async (refund) => {
    const result = await Swal.fire({
      title: "Hoàn tất hoàn tiền",
      html: `
        <input id="refund-code" class="swal2-input" placeholder="Mã giao dịch hoàn tiền">
        <textarea id="refund-note" class="swal2-textarea" placeholder="Ghi chú nội bộ (nếu có)"></textarea>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: "Hoàn tất",
      cancelButtonText: "Hủy",
      preConfirm: () => {
        const refundTransactionCode = document.getElementById("refund-code")?.value?.trim();
        const adminNote = document.getElementById("refund-note")?.value?.trim();
        if (!refundTransactionCode) {
          Swal.showValidationMessage("Vui lòng nhập mã giao dịch hoàn tiền");
          return false;
        }
        return { refundTransactionCode, adminNote };
      },
    });
    if (!result.isConfirmed) return;

    try {
      await completeRefund(refund.id, result.value);
      Swal.fire("Đã hoàn tiền", "Payment đã được chuyển sang REFUNDED.", "success");
      refreshAll();
    } catch (err) {
      Swal.fire("Lỗi", err.message || "Không thể hoàn tất refund.", "error");
    }
  };

  const handleFailRefund = async (refund) => {
    const result = await Swal.fire({
      title: "Đánh dấu refund thất bại",
      input: "textarea",
      inputPlaceholder: "Nhập lý do thất bại hoặc thông tin cần khách bổ sung",
      inputValidator: (value) => (!value?.trim() ? "Vui lòng nhập lý do" : undefined),
      showCancelButton: true,
      confirmButtonText: "Lưu lý do",
      cancelButtonText: "Hủy",
    });
    if (!result.isConfirmed) return;

    try {
      await failRefund(refund.id, result.value);
      Swal.fire("Đã lưu", "Khách có thể cập nhật lại thông tin tài khoản.", "success");
      refreshAll();
    } catch (err) {
      Swal.fire("Lỗi", err.message || "Không thể đánh dấu refund thất bại.", "error");
    }
  };

  const totalPages = pageData?.page?.totalPages ?? pageData?.totalPages ?? 0;
  const refundTotalPages = refundPageData?.page?.totalPages ?? refundPageData?.totalPages ?? 0;

  return (
    <div className="payment-page">
      <div className="payment-header">
        <div>
          <p className="payment-eyebrow">Finance operations</p>
          <h2>Quản lý thanh toán & hoàn tiền</h2>
        </div>

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

      <section className="payment-section-card">
        <div className="payment-section-title">
          <h3>Thanh toán</h3>
          <span>Kiểm tra giao dịch VietQR trước khi xác nhận đơn hàng.</span>
        </div>

        <div className="table-wrapper">
          <table className="payment-table">
            <thead>
              <tr>
                <th onClick={() => toggleSort("paymentId")}>Mã thanh toán</th>
                <th onClick={() => toggleSort("paymentMethod")}>Phương thức</th>
                <th onClick={() => toggleSort("transactionCode")}>Mã giao dịch</th>
                <th onClick={() => toggleSort("orderStatus")}>Trạng thái đơn</th>
                <th onClick={() => toggleSort("createdAt")}>Ngày tạo</th>
                <th>Tổng tiền</th>
                <th onClick={() => toggleSort("paymentStatus")}>Trạng thái</th>
                <th width="160">Thao tác</th>
              </tr>
            </thead>

            <tbody>
              {payments.length > 0 ? (
                payments.map((p) => (
                  <tr key={p.paymentId}>
                    <td>{p.paymentId}</td>
                    <td>{p.paymentMethod}</td>
                    <td>{p.transactionCode || "-"}</td>
                    <td>{p.orderStatus || "-"}</td>
                    <td>{p.createdAt || "-"}</td>
                    <td>{formatMoney(p.totalAmount)}</td>
                    <td>
                      <span className={`payment-status-badge ${String(p.paymentStatus || "").toLowerCase()}`}>
                        {p.paymentStatus}
                      </span>
                    </td>
                    <td>
                      {p.orderId
                        && p.orderStatus !== "CANCELLED"
                        && p.paymentMethod === "VIETQR"
                        && p.paymentStatus === "PENDING" ? (
                        <Button size="sm" variant="success" onClick={() => handleConfirmPayment(p)}>
                          Xác nhận tiền
                        </Button>
                      ) : (
                        <span>-</span>
                      )}
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
        </div>

        {pageData && (
          <div className="pagination-section">
            <Button size="sm" disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
              Trước
            </Button>
            <span>Trang {pageNo + 1} / {totalPages || 1}</span>
            <Button size="sm" disabled={pageNo + 1 >= totalPages} onClick={() => setPageNo(pageNo + 1)}>
              Sau
            </Button>
          </div>
        )}
      </section>

      <section className="payment-section-card refund-section">
        <div className="payment-section-title">
          <div>
            <h3>Yêu cầu hoàn tiền</h3>
            <span>Luồng an toàn: kiểm tra tiền → lấy tài khoản khách → xử lý chuyển khoản → hoàn tất.</span>
          </div>
          <Form.Select
            value={refundStatus}
            onChange={(e) => {
              setRefundStatus(e.target.value);
              setRefundPageNo(0);
            }}
          >
            <option value="">Tất cả trạng thái</option>
            {Object.entries(refundStatusLabel).map(([value, label]) => (
              <option key={value} value={value}>{label}</option>
            ))}
          </Form.Select>
        </div>

        <div className="table-wrapper">
          <table className="payment-table refund-table">
            <thead>
              <tr>
                <th>Mã refund</th>
                <th>Khách hàng</th>
                <th>Nguồn</th>
                <th>Số tiền</th>
                <th>Trạng thái</th>
                <th>Tài khoản nhận</th>
                <th>Lý do/ghi chú</th>
                <th width="260">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {refunds.length > 0 ? (
                refunds.map((refund) => (
                  <tr key={refund.id}>
                    <td>#{refund.id}</td>
                    <td>{refund.username || `User #${refund.userId}`}</td>
                    <td>
                      {refund.orderId ? `Order #${refund.orderId}` : ""}
                      {refund.bookingId ? `Booking #${refund.bookingId}` : ""}
                    </td>
                    <td>{formatMoney(refund.amount)}</td>
                    <td>
                      <span className={`payment-status-badge ${String(refund.status || "").toLowerCase()}`}>
                        {refundStatusLabel[refund.status] || refund.status}
                      </span>
                    </td>
                    <td>
                      {refund.bankCode && refund.bankAccountNumber
                        ? `${refund.bankCode} - ${refund.bankAccountNumber} - ${refund.bankAccountName || ""}`
                        : "Chưa có"}
                    </td>
                    <td>{refund.failureReason || refund.adminNote || refund.reason || "-"}</td>
                    <td>
                      <div className="refund-actions">
                        {refund.status === "PAYMENT_REVIEW" && (
                          <>
                            <Button size="sm" variant="success" onClick={() => handleReviewPayment(refund, true)}>
                              Đã nhận tiền
                            </Button>
                            <Button size="sm" variant="outline-danger" onClick={() => handleReviewPayment(refund, false)}>
                              Chưa nhận
                            </Button>
                          </>
                        )}
                        {refund.status === "PENDING" && (
                          <Button size="sm" variant="primary" onClick={() => handleStartProcessing(refund)}>
                            Xử lý
                          </Button>
                        )}
                        {refund.status === "PROCESSING" && (
                          <>
                            <Button size="sm" variant="success" onClick={() => handleCompleteRefund(refund)}>
                              Hoàn tất
                            </Button>
                            <Button size="sm" variant="outline-danger" onClick={() => handleFailRefund(refund)}>
                              Thất bại
                            </Button>
                          </>
                        )}
                        {["AWAITING_INFORMATION", "COMPLETED", "FAILED"].includes(refund.status) && <span>-</span>}
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="8" className="text-center text-muted">
                    Chưa có yêu cầu hoàn tiền
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {refundPageData && (
          <div className="pagination-section">
            <Button size="sm" disabled={refundPageNo === 0} onClick={() => setRefundPageNo(refundPageNo - 1)}>
              Trước
            </Button>
            <span>Trang {refundPageNo + 1} / {refundTotalPages || 1}</span>
            <Button
              size="sm"
              disabled={refundPageNo + 1 >= refundTotalPages}
              onClick={() => setRefundPageNo(refundPageNo + 1)}
            >
              Sau
            </Button>
          </div>
        )}
      </section>
    </div>
  );
}
