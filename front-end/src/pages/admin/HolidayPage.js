import { useCallback, useEffect, useState } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import {
  createHoliday,
  deleteHoliday,
  getHolidays,
  updateHoliday,
} from "../../services/holidayService";
import "../../assets/styles/AdminCrudPage.css";

const emptyForm = {
  name: "",
  holidayDate: "",
  description: "",
};

export default function HolidayPage() {
  const [holidays, setHolidays] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const loadHolidays = useCallback(async () => {
    const data = await getHolidays();
    setHolidays(data || []);
  }, []);

  useEffect(() => {
    loadHolidays().catch(console.error);
  }, [loadHolidays]);

  const openModal = (holiday = null) => {
    setEditing(holiday);
    setForm(
      holiday
        ? {
            name: holiday.name || "",
            holidayDate: holiday.holidayDate || "",
            description: holiday.description || "",
          }
        : emptyForm
    );
    setShowModal(true);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (editing) {
      await updateHoliday(editing.id, form);
    } else {
      await createHoliday(form);
    }

    setShowModal(false);
    await loadHolidays();
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc muốn xóa ngày lễ này?")) return;
    await deleteHoliday(id);
    await loadHolidays();
  };

  return (
    <div className="admin-crud-page">
      <div className="admin-crud-header">
        <h2>Quản lý ngày lễ</h2>
        <Button onClick={() => openModal()}>+ Thêm ngày lễ</Button>
      </div>

      {holidays.length === 0 ? (
        <div className="admin-crud-empty">Chưa có ngày lễ nào</div>
      ) : (
        <table className="admin-crud-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên ngày lễ</th>
              <th>Ngày</th>
              <th>Mô tả</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {holidays.map((holiday) => (
              <tr key={holiday.id}>
                <td>{holiday.id}</td>
                <td>{holiday.name}</td>
                <td>{holiday.holidayDate}</td>
                <td>{holiday.description}</td>
                <td>
                  <div className="admin-crud-row-actions">
                    <Button size="sm" variant="warning" onClick={() => openModal(holiday)}>
                      Sửa
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => handleDelete(holiday.id)}>
                      Xóa
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{editing ? "Sửa ngày lễ" : "Thêm ngày lễ"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Tên ngày lễ</Form.Label>
              <Form.Control value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ngày</Form.Label>
              <Form.Control type="date" value={form.holidayDate} onChange={(e) => setForm({ ...form, holidayDate: e.target.value })} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </Form.Group>
            <Button type="submit" variant="success">
              Lưu
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
