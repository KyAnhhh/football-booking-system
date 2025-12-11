import { useEffect, useState } from "react";
import { getProfile, updateProfile } from "../../services/authService";
import { Container, Card, Button, Form, Alert } from "react-bootstrap";
import "../../assets/styles/Profile.css";

export default function Profile() {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [updating, setUpdating] = useState(false);

    // Edit form data
    const [editData, setEditData] = useState({
        username: "",
        email: "",
        phone: "",
        password: ""
    });
    const [editError, setEditError] = useState(null);

    useEffect(() => {
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getProfile();
            setProfile(data);
            setEditData({
                username: data.username,
                email: data.email,
                phone: data.phone,
                password: ""
            });
        } catch (err) {
            console.error("Error load profile:", err);
            setError("Lỗi tải thông tin cá nhân: " + err.message);
        }
        setLoading(false);
    };

    const handleUpdate = async () => {
        setEditError(null);
        setUpdating(true);
        try {
            if (!editData.username || !editData.email || !editData.phone) {
                setEditError("Vui lòng điền đầy đủ thông tin bắt buộc");
                setUpdating(false);
                return;
            }

            const payload = {
                username: editData.username,
                email: editData.email,
                phone: editData.phone,
            };

            if (editData.password && editData.password.trim()) {
                payload.password = editData.password;
            }

            const result = await updateProfile(payload);
            setProfile(result);
            setEditMode(false);
            setEditError(null);
        } catch (err) {
            console.error("Update error:", err);
            setEditError(err.message || "Lỗi khi cập nhật thông tin");
        }
        setUpdating(false);
    };

    if (loading) return (
        <Container style={{ marginTop: 30 }}>
            <p>Đang tải...</p>
        </Container>
    );

    return (
        <Container style={{ marginTop: 30, marginBottom: 30 }}>
            <h2 style={{ marginBottom: 20 }}>Thông tin cá nhân</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            {!editMode ? (
                <Card style={{ marginBottom: 20 }}>
                    <Card.Header bg="primary" text="white">
                        <Card.Title style={{ margin: 0 }}>Hồ sơ cá nhân</Card.Title>
                    </Card.Header>
                    <Card.Body>
                        <div style={{ marginBottom: 10 }}>
                            <p><strong>Họ và tên:</strong> {profile?.username}</p>
                            <p><strong>Email:</strong> {profile?.email}</p>
                            <p><strong>Số điện thoại:</strong> {profile?.phone}</p>
                            <p><strong>Trạng thái:</strong> <span style={{ backgroundColor: "#e6f5e6", padding: "2px 6px", borderRadius: 3 }}>{profile?.status}</span></p>
                        </div>
                    </Card.Body>
                </Card>
            ) : (
                <Card style={{ marginBottom: 20 }}>
                    <Card.Header bg="primary" text="white">
                        <Card.Title style={{ margin: 0 }}>Cập nhật thông tin</Card.Title>
                    </Card.Header>
                    <Card.Body>
                        {editError && <Alert variant="danger">{editError}</Alert>}

                        <Form>
                            <Form.Group className="mb-3">
                                <Form.Label>Họ và tên</Form.Label>
                                <Form.Control
                                    type="text"
                                    value={editData.username}
                                    onChange={(e) => setEditData({ ...editData, username: e.target.value })}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Email</Form.Label>
                                <Form.Control
                                    type="email"
                                    value={editData.email}
                                    onChange={(e) => setEditData({ ...editData, email: e.target.value })}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Số điện thoại</Form.Label>
                                <Form.Control
                                    type="tel"
                                    value={editData.phone}
                                    onChange={(e) => setEditData({ ...editData, phone: e.target.value })}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Mật khẩu mới (tuỳ chọn)</Form.Label>
                                <Form.Control
                                    type="password"
                                    value={editData.password}
                                    onChange={(e) => setEditData({ ...editData, password: e.target.value })}
                                    placeholder="Để trống nếu không đổi mật khẩu"
                                />
                            </Form.Group>
                        </Form>
                    </Card.Body>
                </Card>
            )}

            <div style={{ display: "flex", gap: 10 }}>
                {!editMode ? (
                    <Button variant="primary" onClick={() => setEditMode(true)}>
                        Chỉnh sửa thông tin
                    </Button>
                ) : (
                    <>
                        <Button variant="primary" onClick={handleUpdate} disabled={updating}>
                            {updating ? "Đang lưu..." : "Lưu thay đổi"}
                        </Button>
                        <Button variant="secondary" onClick={() => setEditMode(false)} disabled={updating}>
                            Hủy
                        </Button>
                    </>
                )}
            </div>
        </Container>
    );
}
