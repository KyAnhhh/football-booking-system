import { useState, useEffect } from "react";
import { getUsers, createUser, updateUser, deleteUser } from "../../services/userService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/UserPage.css";

export default function UserPage() {
  const [users, setUsers] = useState([]);
  const [show, setShow] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [form, setForm] = useState({ username: "", email: "", phone: "", password: "" });

  // modal confirm delete
  const [confirmUser, setConfirmUser] = useState(null);

  const loadUsers = async () => {
    try {
      const data = await getUsers();
      setUsers(data);
    } catch (err) {
      console.error("Fetch users failed:", err);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  // open modal create / edit
  const handleShow = (user = null) => {
    if (user) {
      setEditingUser(user);
      setForm({ username: user.username, email: user.email, phone: user.phone, password: user.password });
    } else {
      setEditingUser(null);
      setForm({ username: "", email: "", phone: "", password: "" });
    }
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setEditingUser(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingUser) {
        await updateUser(editingUser.id, form);
      } else {
        await createUser(form);
      }
      await loadUsers();
      handleClose();
    } catch (err) {
      console.error("Save user failed:", err);
    }
  };

  // mở modal confirm delete
  const handleDeleteClick = (user) => {
    setConfirmUser(user);
  };

  // xác nhận delete
  const confirmDelete = async () => {
    if (!confirmUser) return;
    try {
      await deleteUser(confirmUser.id);
      await loadUsers();
      setConfirmUser(null);
    } catch (err) {
      console.error("Delete user failed:", err);
    }
  };

  return (
    <div className="user-page">
      <div className="user-header">
        <h2>User Management</h2>
        <Button variant="primary" onClick={() => handleShow()}>
          + Add User
        </Button>
      </div>

      <table className="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>UserName</th>
            <th>Phone</th>
            <th>Email</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>{u.phone}</td>
              <td>{u.email}</td>
              <td>
                <Button size="sm" variant="warning" onClick={() => handleShow(u)}>
                  Edit
                </Button>{" "}
                <Button size="sm" variant="danger" onClick={() => handleDeleteClick(u)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal Create / Edit User */}
      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{editingUser ? "Edit User" : "Create User"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Phone</Form.Label>
              <Form.Control
                type="number"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                placeholder={editingUser ? "Leave blank to keep old password" : ""}
              />
            </Form.Group>

            <Button variant="success" type="submit" style={{backgroundColor: "blue"}}>
              {editingUser ? "Update" : "Create"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      {/* Modal Confirm Delete */}
      <Modal show={!!confirmUser} onHide={() => setConfirmUser(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc chắn muốn xóa user{" "}
          <b>{confirmUser?.username}</b> không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setConfirmUser(null)}>
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
