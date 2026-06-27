import { useCallback, useEffect, useMemo, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import { jwtDecode } from "jwt-decode";

import {
  getUsers,
  createUser,
  updateUser,
  updateUserRole,
  updateUserStatus,
  deleteUser,
} from "../../services/userService";
import "../../assets/styles/UserPage.css";

const allRoleOptions = [
  { value: "ROLE_CLIENT", label: "Khách hàng" },
  { value: "ROLE_STAFF", label: "Nhân viên" },
  { value: "ROLE_MANAGER", label: "Quản lý" },
  { value: "ROLE_ADMIN", label: "Quản trị viên" },
];

const statusOptions = [
  { value: "", label: "Tất cả trạng thái" },
  { value: "active", label: "Đang hoạt động" },
  { value: "locked", label: "Đã khóa" },
  { value: "inactive", label: "Ngừng hoạt động" },
];

const emptyForm = {
  username: "",
  email: "",
  phone: "",
  password: "",
  role: "ROLE_CLIENT",
};

const normalizeRole = (value) =>
  String(value || "")
    .replace("ROLE_", "")
    .toUpperCase();

const getRoleLabel = (role) =>
  allRoleOptions.find((option) => option.value === role)?.label ||
  String(role || "").replace("ROLE_", "");

const getStatusLabel = (status) => {
  if (status === "locked") return "Đã khóa";
  if (status === "inactive") return "Ngừng hoạt động";
  return "Đang hoạt động";
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
  const [roleFilter, setRoleFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [pageData, setPageData] = useState(null);
  const [confirmUser, setConfirmUser] = useState(null);
  const [error, setError] = useState("");

  const currentRole = useMemo(() => {
    const token = sessionStorage.getItem("accessToken");
    if (!token) return "";

    try {
      return normalizeRole(jwtDecode(token).role);
    } catch {
      return "";
    }
  }, []);

  const isAdmin = currentRole === "ADMIN";
  const roleOptions = useMemo(
    () =>
      isAdmin
        ? allRoleOptions
        : allRoleOptions.filter((option) =>
            ["ROLE_CLIENT", "ROLE_STAFF"].includes(option.value)
          ),
    [isAdmin]
  );

  const roleTabs = useMemo(
    () => [{ value: "", label: "Tất cả" }, ...roleOptions],
    [roleOptions]
  );

  const loadUsers = useCallback(async () => {
    try {
      setError("");
      const data = await getUsers({
        pageNo,
        pageSize,
        sortBy,
        sortDir,
        search,
        role: roleFilter,
        status: statusFilter,
      });

      setUsers(data.content || []);
      setPageData(data);
    } catch (err) {
      console.error("Fetch users failed:", err);
      setError(err.message || "Không thể tải danh sách người dùng.");
      setUsers([]);
      setPageData(null);
    }
  }, [pageNo, pageSize, sortBy, sortDir, search, roleFilter, statusFilter]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const toggleSort = (field) => {
    setPageNo(0);

    if (sortBy === field) {
      setSortDir((current) => (current === "asc" ? "desc" : "asc"));
      return;
    }

    setSortBy(field);
    setSortDir("asc");
  };

  const handleShow = (user = null) => {
    const defaultRole = roleOptions[0]?.value || "ROLE_CLIENT";

    setEditingUser(user);
    setForm(
      user
        ? {
            username: user.username || "",
            email: user.email || "",
            phone: user.phone || "",
            password: "",
            role: user.role || defaultRole,
          }
        : {
            ...emptyForm,
            role: defaultRole,
          }
    );
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setEditingUser(null);
    setForm(emptyForm);
    setError("");
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setError("");

      if (editingUser) {
        await updateUser(editingUser.id, form);
        if (form.role && form.role !== editingUser.role) {
          await updateUserRole(editingUser.id, form.role);
        }
      } else {
        await createUser(form);
      }

      await loadUsers();
      handleClose();
    } catch (err) {
      console.error("Save user failed:", err);
      setError(err.message || "Không thể lưu người dùng.");
    }
  };

  const confirmDelete = async () => {
    if (!confirmUser) return;

    try {
      setError("");
      await deleteUser(confirmUser.id);
      await loadUsers();
      setConfirmUser(null);
    } catch (err) {
      console.error("Delete user failed:", err);
      setError(err.message || "Không thể xóa người dùng.");
    }
  };

  const toggleUserStatus = async (user) => {
    const nextStatus = user.status === "active" ? "locked" : "active";

    try {
      setError("");
      await updateUserStatus(user.id, nextStatus);
      await loadUsers();
    } catch (err) {
      console.error("Update user status failed:", err);
      setError(err.message || "Không thể cập nhật trạng thái người dùng.");
    }
  };

  const totalPages = pageData?.page?.totalPages || pageData?.totalPages || 1;

  return (
    <div className="user-page">
      <div className="user-header">
        <div>
          <h2>Quản lý người dùng</h2>
          <p>
            Admin quản lý toàn bộ tài khoản. Manager chỉ quản lý Nhân viên và
            Khách hàng.
          </p>
        </div>

        <div className="user-actions">
          <Form.Control
            type="text"
            placeholder="Tìm username, email, số điện thoại..."
            value={search}
            onChange={(event) => {
              setSearch(event.target.value);
              setPageNo(0);
            }}
            style={{ width: "300px" }}
          />

          <Button variant="primary" onClick={() => handleShow()}>
            + Thêm người dùng
          </Button>
        </div>
      </div>

      <div className="mb-3 d-flex flex-wrap gap-2 align-items-center">
        {roleTabs.map((tab) => (
          <Button
            key={tab.value || "all"}
            size="sm"
            variant={roleFilter === tab.value ? "primary" : "outline-primary"}
            onClick={() => {
              setRoleFilter(tab.value);
              setPageNo(0);
            }}
          >
            {tab.label}
          </Button>
        ))}

        <Form.Select
          value={statusFilter}
          onChange={(event) => {
            setStatusFilter(event.target.value);
            setPageNo(0);
          }}
          style={{ width: "210px" }}
        >
          {statusOptions.map((option) => (
            <option key={option.value || "all-status"} value={option.value}>
              {option.label}
            </option>
          ))}
        </Form.Select>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <table className="user-table">
        <thead>
          <tr>
            <th onClick={() => toggleSort("id")}>ID</th>
            <th onClick={() => toggleSort("username")}>Tên đăng nhập</th>
            <th onClick={() => toggleSort("phone")}>Số điện thoại</th>
            <th onClick={() => toggleSort("email")}>Email</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {users.length > 0 ? (
            users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.username}</td>
                <td>{user.phone}</td>
                <td>{user.email}</td>
                <td>{getRoleLabel(user.role)}</td>
                <td>
                  <span className={`user-status user-status-${user.status || "active"}`}>
                    {getStatusLabel(user.status)}
                  </span>
                </td>
                <td>
                  <Button size="sm" variant="warning" onClick={() => handleShow(user)}>
                    Sửa
                  </Button>{" "}
                  <Button
                    size="sm"
                    variant={user.status === "active" ? "outline-danger" : "outline-success"}
                    onClick={() => toggleUserStatus(user)}
                  >
                    {user.status === "active" ? "Khóa" : "Mở"}
                  </Button>{" "}
                  {isAdmin && (
                    <Button size="sm" variant="danger" onClick={() => setConfirmUser(user)}>
                      Xóa
                    </Button>
                  )}
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" style={{ textAlign: "center" }}>
                Chưa có người dùng phù hợp.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {pageData && (
        <div className="pagination-section">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
            Trước
          </Button>

          <span style={{ margin: "0 10px" }}>
            Trang {pageNo + 1} / {totalPages}
          </span>

          <Button
            disabled={pageNo + 1 >= totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Sau
          </Button>
        </div>
      )}

      <Modal show={show} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingUser ? "Sửa người dùng" : "Thêm người dùng"}
          </Modal.Title>
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

            {!editingUser && (
              <Form.Group className="mb-3">
                <Form.Label>Mật khẩu</Form.Label>
                <Form.Control
                  type="password"
                  value={form.password}
                  onChange={(event) => setForm({ ...form, password: event.target.value })}
                  required
                />
              </Form.Group>
            )}

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
          Bạn có chắc chắn muốn xóa người dùng{" "}
          <b>{confirmUser?.username}</b> không?
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
