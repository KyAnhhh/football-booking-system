import { useCallback, useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";

import {
  getUsers,
  createUser,
  updateUser,
  updateUserRole,
  deleteUser,
} from "../../services/userService";
import "../../assets/styles/UserPage.css";

const roleOptions = [
  { value: "ROLE_CLIENT", label: "Khách hàng" },
  { value: "ROLE_STAFF", label: "Nhân viên" },
  { value: "ROLE_MANAGER", label: "Quản lý" },
  { value: "ROLE_ADMIN", label: "Quản trị viên" },
];

const emptyForm = {
  username: "",
  email: "",
  phone: "",
  password: "",
  role: "ROLE_CLIENT",
};

export default function UserPage() {
  const [users, setUsers] = useState([]);
  const [show, setShow] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(10);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");
  const [pageData, setPageData] = useState(null);
  const [confirmUser, setConfirmUser] = useState(null);

  const loadUsers = useCallback(async () => {
    try {
      const data = await getUsers(pageNo, pageSize, sortBy, sortDir, search);
      setUsers(data.content || []);
      setPageData(data);
    } catch (err) {
      console.error("Fetch users failed:", err);
      setUsers([]);
      setPageData(null);
    }
  }, [pageNo, pageSize, sortBy, sortDir, search]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const toggleSort = (field) => {
    if (sortBy === field) {
      setSortDir((current) => (current === "asc" ? "desc" : "asc"));
      return;
    }

    setSortBy(field);
    setSortDir("asc");
  };

  const handleShow = (user = null) => {
    setEditingUser(user);
    setForm(
      user
        ? {
            username: user.username || "",
            email: user.email || "",
            phone: user.phone || "",
            password: "",
            role: user.role || "ROLE_CLIENT",
          }
        : emptyForm
    );
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setEditingUser(null);
    setForm(emptyForm);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      if (editingUser) {
        await updateUser(editingUser.id, form);
        if (form.role && form.role !== editingUser.role) {
          await updateUserRole(editingUser.id, form.role);
        }
      } else {
        const createdUser = await createUser(form);
        if (form.role !== "ROLE_CLIENT" && createdUser?.id) {
          await updateUserRole(createdUser.id, form.role);
        }
      }

      await loadUsers();
      handleClose();
    } catch (err) {
      console.error("Save user failed:", err);
    }
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
        <h2>Quản lý người dùng</h2>

        <div className="user-actions">
          <Form.Control
            type="text"
            placeholder="Tìm theo email..."
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setPageNo(0);
            }}
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
            <th onClick={() => toggleSort("username")}>Tên đăng nhập</th>
            <th onClick={() => toggleSort("phone")}>Số điện thoại</th>
            <th onClick={() => toggleSort("email")}>Email</th>
            <th>Vai trò</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td>{user.phone}</td>
              <td>{user.email}</td>
              <td>{String(user.role || "").replace("ROLE_", "")}</td>
              <td>
                <Button size="sm" variant="warning" onClick={() => handleShow(user)}>
                  Sửa
                </Button>{" "}
                <Button size="sm" variant="danger" onClick={() => setConfirmUser(user)}>
                  Xóa
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {pageData && (
        <div className="pagination-section">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
            Trước
          </Button>

          <span style={{ margin: "0 10px" }}>
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

      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>{editingUser ? "Sửa người dùng" : "Thêm người dùng"}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Tên đăng nhập</Form.Label>
              <Form.Control
                type="text"
                value={form.username}
                onChange={(event) => setForm({ ...form, username: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                value={form.email}
                onChange={(event) => setForm({ ...form, email: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Số điện thoại</Form.Label>
              <Form.Control
                type="text"
                value={form.phone}
                onChange={(event) => setForm({ ...form, phone: event.target.value })}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mật khẩu</Form.Label>
              <Form.Control
                type="password"
                value={form.password}
                onChange={(event) => setForm({ ...form, password: event.target.value })}
                placeholder={editingUser ? "Để trống nếu muốn giữ mật khẩu cũ" : ""}
                required={!editingUser}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Vai trò</Form.Label>
              <Form.Select
                value={form.role}
                onChange={(event) => setForm({ ...form, role: event.target.value })}
              >
                {roleOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Button variant="success" type="submit">
              {editingUser ? "Cập nhật" : "Tạo mới"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={!!confirmUser} onHide={() => setConfirmUser(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc chắn muốn xóa người dùng <b>{confirmUser?.username}</b> không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setConfirmUser(null)}>
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
