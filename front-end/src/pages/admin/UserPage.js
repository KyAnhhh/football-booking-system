import { useState, useEffect } from "react";
import { getUsers, createUser, updateUser, deleteUser } from "../../services/userService";
import { Modal, Button, Form } from "react-bootstrap";
import "../../assets/styles/UserPage.css";

export default function UserPage() {
  const [users, setUsers] = useState([]);
  const [show, setShow] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [form, setForm] = useState({ username: "", email: "", phone: "", password: "" });

  // Paging + Sorting + Search
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(5);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const [pageData, setPageData] = useState(null);

  // modal confirm delete
  const [confirmUser, setConfirmUser] = useState(null);

  const loadUsers = async () => {
    try {
      const data = await getUsers(pageNo, pageSize, sortBy, sortDir, search);
      setUsers(data.content);
      setPageData(data);
    } catch (err) {
      console.error("Fetch users failed:", err);
    }
  };

  useEffect(() => {
    loadUsers();
  }, [pageNo, sortBy, sortDir, search]);

  // Sort toggle
  const toggleSort = (field) => {
    if (sortBy === field) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortBy(field);
      setSortDir("asc");
    }
  };

  // Search debounce
  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    setPageNo(0); // reset page
  };

  // open modal create / edit
  const handleShow = (user = null) => {
    if (user) {
      setEditingUser(user);
      setForm({
        username: user.username,
        email: user.email,
        phone: user.phone,
        password: "",
      });
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

  // delete
  const handleDeleteClick = (user) => {
    setConfirmUser(user);
  };

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

        <div className="user-actions">
          <Form.Control
            type="text"
            placeholder="Search by email..."
            value={search}
            onChange={handleSearchChange}
            style={{ width: "250px" }}
          />

          <Button variant="primary" onClick={() => handleShow()}>
            + Thêm người dùng
          </Button>
        </div>
      </div>

      <table className="user-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("id")}>ID</th>
            <th onClick={() => toggleSort("username")}>UserName</th>
            <th onClick={() => toggleSort("phone")}>Phone</th>
            <th onClick={() => toggleSort("email")}>Email</th>
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

      {/* Pagination */}
      {pageData && (
        <div className="pagination-section">
          <Button
            disabled={pageNo === 0}
            onClick={() => setPageNo(pageNo - 1)}
          >
            Prev
          </Button>

          <span style={{ margin: "0 10px" }}>
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

            <Button variant="success" type="submit" style={{ backgroundColor: "blue" }}>
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
          Bạn có chắc chắn muốn xóa user <b>{confirmUser?.username}</b> không?
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
