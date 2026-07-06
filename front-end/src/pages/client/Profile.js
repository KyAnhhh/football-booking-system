import { useEffect, useState } from "react";
import { getProfile, updateProfile } from "../../services/userService";
import "../../assets/styles/Profile.css";
import { Award, Mail, Phone, Save, Sparkles, UserRound } from "lucide-react";

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [editData, setEditData] = useState({
    username: "",
    email: "",
    phone: "",
  });
  const [editError, setEditError] = useState(null);

  const fetchProfile = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getProfile();
      setProfile(data);
      setEditData({
        username: data.username || "",
        email: data.email || "",
        phone: data.phone || "",
      });
    } catch (err) {
      console.error("Error load profile:", err);
      setError("Lỗi tải thông tin cá nhân: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleUpdate = async () => {
    setEditError(null);
    setUpdating(true);
    try {
      if (!editData.username || !editData.email || !editData.phone) {
        setEditError("Vui lòng điền đầy đủ thông tin bắt buộc.");
        return;
      }

      const result = await updateProfile({
        username: editData.username,
        email: editData.email,
        phone: editData.phone,
      });

      setProfile(result);
      setEditMode(false);
      setEditError(null);
    } catch (err) {
      console.error("Update error:", err);
      setEditError(err.message || "Lỗi khi cập nhật thông tin.");
    } finally {
      setUpdating(false);
    }
  };

  const cancelEdit = () => {
    setEditMode(false);
    setEditError(null);
    setEditData({
      username: profile?.username || "",
      email: profile?.email || "",
      phone: profile?.phone || "",
    });
  };

  if (loading) {
    return <div className="profile-state">Đang tải hồ sơ...</div>;
  }

  return (
    <div className="profile-page">
      <section className="profile-hero">
        <div>
          <p>Tài khoản</p>
          <h1>Thông tin cá nhân</h1>
          <span>Cập nhật tên, email và số điện thoại để đơn hàng được xử lý chính xác.</span>
        </div>
      </section>

      <div className="profile-shell">
        {error && <div className="profile-alert error">{error}</div>}

        <section className="profile-card">
          <div className="profile-card-header">
            <div className="profile-avatar">
              <UserRound size={32} />
            </div>
            <div>
              <span>Hồ sơ</span>
              <h2>{profile?.username || "Người dùng"}</h2>
            </div>
          </div>

          <section className="profile-membership" aria-label="Hạng thành viên">
            <div className="profile-membership-main">
              <div className="profile-membership-icon">
                <Award size={28} />
              </div>
              <div>
                <span>Hạng thành viên</span>
                <h3>{profile?.membershipTierName || "Chưa xếp hạng"}</h3>
                <p>
                  {profile?.membershipTierName
                    ? `Ưu đãi ${profile.membershipDiscountPercent || 0}% khi đặt sân`
                    : "Tích điểm từ booking hoàn tất để mở khóa ưu đãi."}
                </p>
              </div>
            </div>

            <div className="profile-membership-stats">
              <div>
                <span>Điểm hiện có</span>
                <strong>{profile?.loyaltyPoints || 0}</strong>
              </div>
              <div>
                <span>Ưu đãi</span>
                <strong>{profile?.membershipDiscountPercent || 0}%</strong>
              </div>
            </div>

            <div className="profile-membership-progress">
              <div className="profile-membership-progress-label">
                {profile?.nextMembershipTierName ? (
                  <>
                    <span>
                      Còn <strong>{profile.pointsToNextTier}</strong> điểm tới{" "}
                      <strong>{profile.nextMembershipTierName}</strong>
                    </span>
                    <span>{profile.membershipProgressPercent || 0}%</span>
                  </>
                ) : (
                  <span className="profile-membership-max">
                    <Sparkles size={16} />
                    Bạn đang ở hạng cao nhất
                  </span>
                )}
              </div>
              <div
                className="profile-membership-progress-track"
                role="progressbar"
                aria-valuemin="0"
                aria-valuemax="100"
                aria-valuenow={profile?.membershipProgressPercent || 0}
              >
                <span
                  style={{
                    width: `${profile?.membershipProgressPercent || 0}%`,
                  }}
                />
              </div>
            </div>
          </section>

          {!editMode ? (
            <div className="profile-info-grid">
              <div>
                <UserRound size={18} />
                <span>Họ và tên</span>
                <strong>{profile?.username || "Chưa cập nhật"}</strong>
              </div>
              <div>
                <Mail size={18} />
                <span>Email</span>
                <strong>{profile?.email || "Chưa cập nhật"}</strong>
              </div>
              <div>
                <Phone size={18} />
                <span>Số điện thoại</span>
                <strong>{profile?.phone || "Chưa cập nhật"}</strong>
              </div>
            </div>
          ) : (
            <div className="profile-form">
              {editError && <div className="profile-alert error">{editError}</div>}

              <label>
                <span>Họ và tên</span>
                <input
                  type="text"
                  value={editData.username}
                  onChange={(event) =>
                    setEditData({ ...editData, username: event.target.value })
                  }
                />
              </label>

              <label>
                <span>Email</span>
                <input
                  type="email"
                  value={editData.email}
                  onChange={(event) =>
                    setEditData({ ...editData, email: event.target.value })
                  }
                />
              </label>

              <label>
                <span>Số điện thoại</span>
                <input
                  type="tel"
                  value={editData.phone}
                  onChange={(event) =>
                    setEditData({ ...editData, phone: event.target.value })
                  }
                />
              </label>
            </div>
          )}

          <div className="profile-actions">
            {!editMode ? (
              <button type="button" onClick={() => setEditMode(true)}>
                Chỉnh sửa thông tin
              </button>
            ) : (
              <>
                <button type="button" onClick={handleUpdate} disabled={updating}>
                  <Save size={18} />
                  {updating ? "Đang lưu..." : "Lưu thay đổi"}
                </button>
                <button
                  type="button"
                  className="profile-secondary-btn"
                  onClick={cancelEdit}
                  disabled={updating}
                >
                  Hủy
                </button>
              </>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
