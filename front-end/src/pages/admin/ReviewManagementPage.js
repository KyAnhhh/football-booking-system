import { useCallback, useEffect, useState } from "react";
import { Button, Form } from "react-bootstrap";
import Swal from "sweetalert2";
import { deleteAdminReview, getAdminReviews } from "../../services/adminReviewService";
import "../../assets/styles/AdminCrudPage.css";

const typeLabel = { PRODUCT: "Sản phẩm", BOOKING: "Sân bóng" };

export default function ReviewManagementPage() {
  const [type, setType] = useState("");
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      setReviews(await getAdminReviews(type));
    } catch (error) {
      Swal.fire("Lỗi", error.message || "Không thể tải đánh giá.", "error");
      setReviews([]);
    } finally {
      setLoading(false);
    }
  }, [type]);

  useEffect(() => { load(); }, [load]);

  const remove = async (review) => {
    const result = await Swal.fire({ title: "Xóa đánh giá?", text: "Thao tác này không thể hoàn tác.", icon: "warning", showCancelButton: true, confirmButtonText: "Xóa", cancelButtonText: "Hủy" });
    if (!result.isConfirmed) return;
    try {
      await deleteAdminReview(review.reviewType, review.id);
      await load();
      Swal.fire("Đã xóa", "Đánh giá đã được gỡ khỏi hệ thống.", "success");
    } catch (error) {
      Swal.fire("Lỗi", error.message || "Không thể xóa đánh giá.", "error");
    }
  };

  return <div className="admin-crud-page">
    <div className="admin-crud-header"><div><h2>Quản lý đánh giá</h2><p>Theo dõi và gỡ đánh giá sản phẩm hoặc sân bóng không phù hợp.</p></div>
      <Form.Select value={type} onChange={(event) => setType(event.target.value)} style={{ maxWidth: 220 }}><option value="">Tất cả loại đánh giá</option><option value="PRODUCT">Sản phẩm</option><option value="BOOKING">Sân bóng</option></Form.Select>
    </div>
    <div className="table-responsive"><table className="table table-hover align-middle"><thead><tr><th>Loại</th><th>Đối tượng</th><th>Khách hàng</th><th>Số sao</th><th>Nhận xét</th><th>Ngày tạo</th><th>Thao tác</th></tr></thead>
      <tbody>{loading ? <tr><td colSpan="7" className="text-center">Đang tải...</td></tr> : reviews.length === 0 ? <tr><td colSpan="7" className="text-center">Chưa có đánh giá.</td></tr> : reviews.map((review) => <tr key={`${review.reviewType}-${review.id}`}><td>{typeLabel[review.reviewType] || review.reviewType}</td><td>{review.subjectName}</td><td>{review.userName}</td><td>{"★".repeat(review.rating)}<span className="text-muted">{"☆".repeat(5 - review.rating)}</span></td><td style={{ minWidth: 240 }}>{review.comment}</td><td>{review.createdAt ? new Date(review.createdAt).toLocaleString("vi-VN") : "-"}</td><td><Button size="sm" variant="outline-danger" onClick={() => remove(review)}>Xóa</Button></td></tr>)}</tbody>
    </table></div>
  </div>;
}
