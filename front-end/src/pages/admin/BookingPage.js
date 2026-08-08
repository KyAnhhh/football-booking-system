import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  checkInBooking,
  checkOutBooking,
  createBookingForAdmin,
  getBookings,
  deleteBooking,
  updateBookingStatus
} from "../../services/bookingService";
import { getFields } from "../../services/fieldService";
import { getUsers } from "../../services/userService";
import { confirmBookingPayment } from "../../services/paymentService";

import { Button, Form, Modal } from "react-bootstrap";
import swal from "sweetalert";
import "../../assets/styles/BookingPage.css";

export default function BookingPage() {
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);
  const [fields, setFields] = useState([]);
  const [users, setUsers] = useState([]);
  const [pageData, setPageData] = useState(null);

  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(10);

  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState("");
  const [updatingBookingId, setUpdatingBookingId] = useState(null);
  const updatingBookingRef = useRef(false);

  const [confirmDeleteModal, setConfirmDeleteModal] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [createForm, setCreateForm] = useState({
    userId: "",
    fieldId: "",
    date: "",
    startTime: "",
    endTime: "",
    contactPhone: "",
    paymentMethod: "CASH",
    confirmImmediately: true,
    note: "Admin tạo booking hộ khách",
  });

  const loadBookings = useCallback(async () => {
    try {
      const data = await getBookings({
        page: pageNo,
        size: pageSize,
        keyword,
        status
      });

      if (data && data.content) {
        setBookings(data.content);
        setPageData(data);
      } else {
        setBookings([]);
      }
    } catch (err) {
      console.error("Fetch bookings failed:", err);
      setBookings([]);
    }
  }, [pageNo, pageSize, keyword, status]);

  useEffect(() => {
    loadBookings();
  }, [loadBookings]);

  useEffect(() => {
    const loadOptions = async () => {
      try {
        const [fieldData, userData] = await Promise.all([
          getFields({ page: 0, size: 1000 }),
          getUsers({
            pageNo: 0,
            pageSize: 1000,
            sortBy: "id",
            sortDir: "asc",
            role: "ROLE_CLIENT",
            status: "active",
          }),
        ]);
        setFields(fieldData?.content || []);
        setUsers(userData?.content || []);
      } catch (err) {
        console.error("Load booking form options failed:", err);
      }
    };

    loadOptions();
  }, []);

  const handleUpdateStatus = async (id, status) => {
    if (updatingBookingRef.current) return;
    updatingBookingRef.current = true;
    try {
      setUpdatingBookingId(id);
      await updateBookingStatus(id, status);
      await loadBookings();
    } catch (err) {
      console.error("Update status failed:", err);
      swal(
        "Không thể cập nhật",
        err.message || "Không thể cập nhật trạng thái booking.",
        "error"
      );
    } finally {
      setUpdatingBookingId(null);
      updatingBookingRef.current = false;
    }
  };

  const handleConfirmPayment = async (id) => {
    if (updatingBookingRef.current) return;
    updatingBookingRef.current = true;
    try {
      setUpdatingBookingId(id);
      await confirmBookingPayment(id);
      await loadBookings();
      swal(
        "Đã xác nhận thanh toán",
        "Đã nhận tiền VietQR. Bây giờ có thể xác nhận booking.",
        "success"
      );
    } catch (err) {
      console.error("Confirm booking payment failed:", err);
      swal(
        "Không thể xác nhận",
        err.message || "Không thể xác nhận thanh toán VietQR.",
        "error"
      );
    } finally {
      setUpdatingBookingId(null);
      updatingBookingRef.current = false;
    }
  };

  const handleCheckIn = async (id) => {
    if (updatingBookingRef.current) return;
    updatingBookingRef.current = true;
    try {
      setUpdatingBookingId(id);
      await checkInBooking(id);
      await loadBookings();
    } catch (err) {
      console.error("Check-in failed:", err);
    } finally {
      setUpdatingBookingId(null);
      updatingBookingRef.current = false;
    }
  };

  const handleCheckOut = async (id) => {
    if (updatingBookingRef.current) return;
    updatingBookingRef.current = true;
    try {
      setUpdatingBookingId(id);
      await checkOutBooking(id);
      await loadBookings();
    } catch (err) {
      console.error("Check-out failed:", err);
    } finally {
      setUpdatingBookingId(null);
      updatingBookingRef.current = false;
    }
  };

  const handleDelete = async () => {
    if (!confirmDeleteModal) return;

    try {
      await deleteBooking(confirmDeleteModal.id);
      await loadBookings();
      setConfirmDeleteModal(null);
    } catch (err) {
      console.error("Delete failed:", err);
    }
  };

  const handleCreateBooking = async (event) => {
    event.preventDefault();

    try {
      await createBookingForAdmin({
        userId: Number(createForm.userId),
        fieldId: Number(createForm.fieldId),
        startTime: `${createForm.date}T${createForm.startTime}`,
        endTime: `${createForm.date}T${createForm.endTime}`,
        contactPhone: createForm.contactPhone,
        paymentMethod: createForm.paymentMethod,
        confirmImmediately: createForm.confirmImmediately,
        note: createForm.note,
        services: [],
      });

      setShowCreateModal(false);
      await loadBookings();
    } catch (err) {
      console.error("Create booking failed:", err);
      swal(
        "Không thể tạo booking",
        err.message || "Không thể tạo booking hộ khách.",
        "error"
      );
    }
  };

  const formatMoney = (value) => {
    return `${Number(value || 0).toLocaleString("vi-VN")} VND`;
  };

  const formatDate = (value) => {
    return value ? new Date(value).toLocaleDateString("vi-VN") : "-";
  };

  const formatTime = (value) => {
    return value
      ? new Date(value).toLocaleTimeString("vi-VN", {
          hour: "2-digit",
          minute: "2-digit",
        })
      : "-";
  };

  const statusLabels = {
    PENDING: "Chờ xác nhận",
    CONFIRMED: "Đã xác nhận",
    IN_PROGRESS: "Đang đá",
    CANCELLED: "Đã hủy",
    COMPLETED: "Hoàn tất",
    EXPIRED: "Hết hạn",
  };

  return (
    <div className="booking-page">
      <div className="booking-header">
        <h2>Quản lý đặt sân</h2>

        <div className="filters">
          <Form.Control
            placeholder="Tìm người đặt hoặc sân..."
            value={keyword}
            onChange={(e) => {
              setKeyword(e.target.value);
              setPageNo(0);
            }}
            style={{ width: "220px" }}
          />

          <Form.Select
            value={status}
            onChange={(e) => {
              setStatus(e.target.value);
              setPageNo(0);
            }}
          >
            <option value="">Tất cả trạng thái</option>
            <option value="PENDING">Chờ xác nhận</option>
            <option value="CONFIRMED">Đã xác nhận</option>
            <option value="CANCELLED">Đã hủy</option>
            <option value="COMPLETED">Hoàn tất</option>
            <option value="EXPIRED">Hết hạn</option>
          </Form.Select>

          <Button onClick={() => setShowCreateModal(true)}>
            + Tạo booking hộ khách
          </Button>
        </div>
      </div>

      <table className="booking-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Người đặt</th>
            <th>Sân</th>
            <th>Ngày</th>
            <th>Giờ</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {bookings.length > 0 ? (
            bookings.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{b.userName}</td>
                <td>{b.fieldName}</td>
                <td>{formatDate(b.startTime)}</td>
                <td>
                  {formatTime(b.startTime)} - {formatTime(b.endTime)}
                </td>
                <td>
                  <strong>{formatMoney(b.finalPrice || b.totalPrice)}</strong>
                  <div className="admin-booking-breakdown">
                    <span>Sân: {formatMoney(b.originalPrice || b.totalPrice)}</span>
                    <span>Dịch vụ thêm: {formatMoney(b.extraServicePrice)}</span>
                    <span>Thành viên: -{formatMoney(b.membershipDiscount)}</span>
                    <span>Voucher: -{formatMoney(b.voucherDiscount)}</span>
                  </div>
                </td>

                <td>
                  <span className={`status-${b.status?.toLowerCase()}`}>
                    {statusLabels[b.status] || b.status}
                  </span>
                </td>

                <td className="booking-actions">

                <Button
                  size="sm"
                  variant="outline-primary"
                  onClick={() => navigate(`/admin/bookings/${b.id}`)}
                >
                  Chi tiết
                </Button>

                {/* PENDING */}
                {b.status === "PENDING" && (
                  <>
                    {b.paymentMethod === "VIETQR"
                      && b.paymentStatus === "PENDING" && (
                      <Button
                        size="sm"
                        variant="success"
                        onClick={() => handleConfirmPayment(b.id)}
                        disabled={updatingBookingId !== null}
                      >
                        {updatingBookingId === b.id ? "Đang cập nhật..." : "Xác nhận đã nhận tiền"}
                      </Button>
                    )}

                    {(b.paymentMethod !== "VIETQR"
                      || b.paymentStatus === "PAID") && (
                      <Button
                        size="sm"
                        variant="success"
                        onClick={() =>
                          handleUpdateStatus(b.id, "CONFIRMED")
                        }
                        disabled={updatingBookingId !== null}
                      >
                        {updatingBookingId === b.id ? "Đang cập nhật..." : "Xác nhận"}
                      </Button>
                    )}

                    {b.paymentMethod === "VIETQR"
                      && b.paymentStatus === "UNPAID" && (
                      <Button size="sm" variant="outline-secondary" disabled>
                        Chờ khách chuyển khoản
                      </Button>
                    )}

                    <Button
                      size="sm"
                      variant="warning"
                      onClick={() =>
                        handleUpdateStatus(b.id, "CANCELLED")
                      }
                      disabled={updatingBookingId !== null}
                    >
                      {updatingBookingId === b.id ? "Đang cập nhật..." : "Hủy"}
                    </Button>

                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() =>
                        handleUpdateStatus(b.id, "EXPIRED")
                      }
                      disabled={updatingBookingId !== null}
                    >
                      {updatingBookingId === b.id ? "Đang cập nhật..." : "Hết hạn"}
                    </Button>
                  </>
                )}

                {/* CONFIRMED */}
                {b.status === "CONFIRMED" && (
                  <>
                    <Button
                      size="sm"
                      variant="primary"
                      onClick={() => handleCheckIn(b.id)}
                      disabled={updatingBookingId !== null}
                    >
                      {updatingBookingId === b.id ? "Đang cập nhật..." : "Check-in"}
                    </Button>

                    <Button
                      size="sm"
                      variant="warning"
                      onClick={() =>
                        handleUpdateStatus(b.id, "CANCELLED")
                      }
                      disabled={updatingBookingId !== null}
                    >
                      {updatingBookingId === b.id ? "Đang cập nhật..." : "Hủy"}
                    </Button>
                  </>
                )}

                {b.status === "IN_PROGRESS" && (
                  <Button
                    size="sm"
                    variant="success"
                    onClick={() => handleCheckOut(b.id)}
                    disabled={updatingBookingId !== null}
                  >
                    {updatingBookingId === b.id ? "Đang cập nhật..." : "Check-out"}
                  </Button>
                )}

                {/* DELETE */}
                {["PENDING", "CONFIRMED"].includes(b.status) && (
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => setConfirmDeleteModal(b)}
                  >
                    Hủy booking
                  </Button>
                )}

              </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="8" style={{ textAlign: "center" }}>
                Chưa có lượt đặt sân nào
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {pageData && (
        <div className="pagination">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
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

      <Modal
        show={!!confirmDeleteModal}
        onHide={() => setConfirmDeleteModal(null)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận hủy booking</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          Booking <b>#{confirmDeleteModal?.id}</b> sẽ được chuyển sang trạng thái đã hủy và hoàn lại dữ liệu liên quan.
        </Modal.Body>

        <Modal.Footer>
          <Button onClick={() => setConfirmDeleteModal(null)}>
            Hủy
          </Button>
          <Button variant="danger" onClick={handleDelete}>
            Hủy booking
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showCreateModal} onHide={() => setShowCreateModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo booking hộ khách</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleCreateBooking}>
            <Form.Group className="mb-3">
              <Form.Label>Khách hàng</Form.Label>
              <Form.Select
                value={createForm.userId}
                onChange={(event) => {
                  const selectedUser = users.find((user) => String(user.id) === event.target.value);
                  setCreateForm({
                    ...createForm,
                    userId: event.target.value,
                    contactPhone: selectedUser?.phone || createForm.contactPhone,
                  });
                }}
                required
              >
                <option value="">Chọn khách hàng</option>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.username} - {user.phone}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Sân</Form.Label>
              <Form.Select
                value={createForm.fieldId}
                onChange={(event) => setCreateForm({ ...createForm, fieldId: event.target.value })}
                required
              >
                <option value="">Chọn sân</option>
                {fields.map((field) => (
                  <option key={field.id} value={field.id}>
                    {field.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Ngày đá</Form.Label>
              <Form.Control
                type="date"
                value={createForm.date}
                onChange={(event) => setCreateForm({ ...createForm, date: event.target.value })}
                required
              />
            </Form.Group>

            <div className="admin-booking-form-grid">
              <Form.Group className="mb-3">
                <Form.Label>Giờ bắt đầu</Form.Label>
                <Form.Control
                  type="time"
                  value={createForm.startTime}
                  onChange={(event) => setCreateForm({ ...createForm, startTime: event.target.value })}
                  required
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label>Giờ kết thúc</Form.Label>
                <Form.Control
                  type="time"
                  value={createForm.endTime}
                  onChange={(event) => setCreateForm({ ...createForm, endTime: event.target.value })}
                  required
                />
              </Form.Group>
            </div>

            <Form.Group className="mb-3">
              <Form.Label>Số điện thoại liên hệ</Form.Label>
              <Form.Control
                value={createForm.contactPhone}
                onChange={(event) => setCreateForm({ ...createForm, contactPhone: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Thanh toán</Form.Label>
              <Form.Select
                value={createForm.paymentMethod}
                onChange={(event) => {
                  const paymentMethod = event.target.value;
                  setCreateForm({
                    ...createForm,
                    paymentMethod,
                    confirmImmediately: paymentMethod === "VIETQR"
                      ? false
                      : createForm.confirmImmediately,
                  });
                }}
              >
                <option value="CASH">Tiền mặt</option>
                <option value="VIETQR">VIETQR</option>
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                label="Tạo xong xác nhận luôn"
                checked={createForm.confirmImmediately}
                onChange={(event) => setCreateForm({ ...createForm, confirmImmediately: event.target.checked })}
                disabled={createForm.paymentMethod === "VIETQR"}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Ghi chú</Form.Label>
              <Form.Control
                value={createForm.note}
                onChange={(event) => setCreateForm({ ...createForm, note: event.target.value })}
              />
            </Form.Group>

            <Button type="submit" variant="success">
              Tạo booking
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
