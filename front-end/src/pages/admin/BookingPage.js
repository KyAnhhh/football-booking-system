import { useEffect, useState } from "react";
import {
  getBookings,
  confirmBooking,
  deleteBooking
} from "../../services/bookingService";

import { Button, Form, Modal } from "react-bootstrap";
import "../../assets/styles/BookingPage.css";

export default function BookingPage() {
  const [bookings, setBookings] = useState([]);
  const [pageData, setPageData] = useState(null);

  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);

  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState("");

  const [confirmDeleteModal, setConfirmDeleteModal] = useState(null);

  const loadBookings = async () => {
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
  };

  useEffect(() => {
    loadBookings();
  }, [pageNo, keyword, status]);

  const handleConfirm = async (id) => {
    try {
      await confirmBooking(id);
      await loadBookings();
    } catch (err) {
      console.error("Confirm failed:", err);
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

  return (
    <div className="booking-page">
      <div className="booking-header">
        <h2>Booking Management</h2>

        <div className="filters">
          <Form.Control
            placeholder="Search user / field..."
            value={keyword}
            onChange={(e) => {
              setKeyword(e.target.value);
              setPageNo(0);
            }}
            style={{ width: "220px" }}
          />

          <Form.Select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            <option value="">All Status</option>
            <option value="PENDING">Pending</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="CANCELLED">Cancelled</option>
          </Form.Select>
        </div>
      </div>

      <table className="booking-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>User</th>
            <th>Field</th>
            <th>Date</th>
            <th>Time</th>
            <th>Total Price</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {bookings.length > 0 ? (
            bookings.map((b) => (
              <tr key={b.id}>
                <td>{b.id}</td>
                <td>{b.user?.username}</td>
                <td>{b.field?.name}</td>
                <td>{b.date}</td>
                <td>
                  {b.startTime} - {b.endTime}
                </td>
                <td>{b.totalPrice} VND</td>

                <td>
                  <span className={`status-${b.status?.toLowerCase()}`}>
                    {b.status}
                  </span>
                </td>

                <td>
                  {b.status === "PENDING" && (
                    <Button
                      size="sm"
                      variant="success"
                      onClick={() => handleConfirm(b.id)}
                    >
                      Confirm
                    </Button>
                  )}{" "}
                  <Button
                    size="sm"
                    variant="danger"
                    onClick={() => setConfirmDeleteModal(b)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="8" style={{ textAlign: "center" }}>
                No bookings found
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {pageData && (
        <div className="pagination">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
            Prev
          </Button>

          <span>
            Page {pageNo + 1} / {pageData.page.totalPages}
          </span>

          <Button
            disabled={pageNo + 1 >= pageData.page.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Next
          </Button>
        </div>
      )}

      <Modal
        show={!!confirmDeleteModal}
        onHide={() => setConfirmDeleteModal(null)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Confirm Delete</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          Delete booking <b>{confirmDeleteModal?.id}</b> ?
        </Modal.Body>

        <Modal.Footer>
          <Button onClick={() => setConfirmDeleteModal(null)}>
            Cancel
          </Button>
          <Button variant="danger" onClick={handleDelete}>
            Delete
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}