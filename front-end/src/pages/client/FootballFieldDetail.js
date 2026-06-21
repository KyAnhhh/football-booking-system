import { useCallback, useEffect, useMemo, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Button, Form } from "react-bootstrap";
import { CalendarDays, Clock, MapPin, MessageSquareText, ShieldCheck, Star, Ticket, Wrench } from "lucide-react";
import swal from "sweetalert";
import { getAvailableSlots, getFieldById } from "../../services/fieldService";
import { getFieldReviewSummary, getFieldReviews } from "../../services/bookingReviewService";
import { createBooking } from "../../services/bookingService";
import { getExtraServices } from "../../services/extraService";
import { previewVoucher } from "../../services/voucherService";
import "../../assets/styles/FootballFieldDetail.css";

const formatMoney = (value) => `${Number(value || 0).toLocaleString("vi-VN")} VND`;
const REVIEW_RATINGS = [5, 4, 3, 2, 1];

const renderStars = (rating) =>
  REVIEW_RATINGS.map((star) => (
    <Star
      key={star}
      size={16}
      fill={star <= Number(rating || 0) ? "currentColor" : "none"}
    />
  ));

export default function FootballFieldDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [field, setField] = useState(null);
  const [date, setDate] = useState("");
  const [slots, setSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [loading, setLoading] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [bookingLoading, setBookingLoading] = useState(false);
  const [reviews, setReviews] = useState([]);
  const [reviewSummary, setReviewSummary] = useState(null);
  const [reviewFilter, setReviewFilter] = useState(0);
  const [maintenanceInfo, setMaintenanceInfo] = useState(null);
  const [extras, setExtras] = useState([]);
  const [extraQuantities, setExtraQuantities] = useState({});
  const [voucherCode, setVoucherCode] = useState("");
  const [voucherFinalAmount, setVoucherFinalAmount] = useState(null);
  const [voucherLoading, setVoucherLoading] = useState(false);

  const loadSlots = useCallback(async (selectedDate) => {
    try {
      setLoading(true);
      const data = await getAvailableSlots(id, selectedDate);
      setSlots(data || []);
      setSelectedSlot(null);
      setVoucherFinalAmount(null);
    } catch (error) {
      console.error("Load slots error:", error);
    } finally {
      setLoading(false);
    }
  }, [id]);

  const loadReviews = useCallback(async () => {
    try {
      const [reviewData, summaryData] = await Promise.all([
        getFieldReviews(id),
        getFieldReviewSummary(id),
      ]);
      setReviews(reviewData || []);
      setReviewSummary(summaryData || null);
    } catch (error) {
      console.error("Load reviews error:", error);
    }
  }, [id]);

  useEffect(() => {
    const loadField = async () => {
      try {
        const data = await getFieldById(id);
        setField(data);
        setMaintenanceInfo(
          data
            ? {
                isUnderMaintenance: data.isUnderMaintenance || data.underMaintenance || false,
                maintenanceStartTime: data.maintenanceStartTime,
                maintenanceEndTime: data.maintenanceEndTime,
                maintenanceReason: data.maintenanceReason,
              }
            : null
        );
      } catch (error) {
        console.error("Load field error:", error);
      }
    };

    const loadExtras = async () => {
      try {
        const data = await getExtraServices();
        setExtras((data || []).filter((item) => item.isActive !== false));
      } catch (error) {
        console.error("Load extras error:", error);
      }
    };

    loadField();
    loadReviews();
    loadExtras();
  }, [id, loadReviews]);

  const selectedServices = useMemo(
    () =>
      extras
        .map((service) => ({
          ...service,
          quantity: Number(extraQuantities[service.id] || 0),
        }))
        .filter((service) => service.quantity > 0),
    [extras, extraQuantities]
  );

  const slotPrice = Number(selectedSlot?.price || 0);
  const extraTotal = selectedServices.reduce(
    (sum, service) => sum + Number(service.price || 0) * service.quantity,
    0
  );
  const estimatedSubtotal = slotPrice + extraTotal;
  const estimatedFinal = voucherFinalAmount !== null ? Number(voucherFinalAmount) : estimatedSubtotal;
  const estimatedVoucherDiscount = Math.max(estimatedSubtotal - estimatedFinal, 0);
  const totalReviews = Number(reviewSummary?.totalReviews || reviews.length || 0);
  const averageRating = Number(reviewSummary?.averageRating || 0);
  const filteredReviews = reviewFilter
    ? reviews.filter((review) => Number(review.rating) === reviewFilter)
    : reviews;
  const ratingCounts = {
    5: Number(reviewSummary?.fiveStarCount || 0),
    4: Number(reviewSummary?.fourStarCount || 0),
    3: Number(reviewSummary?.threeStarCount || 0),
    2: Number(reviewSummary?.twoStarCount || 0),
    1: Number(reviewSummary?.oneStarCount || 0),
  };

  const updateExtraQuantity = (serviceId, quantity) => {
    const normalizedQuantity = Math.max(Number(quantity || 0), 0);
    setExtraQuantities((current) => ({
      ...current,
      [serviceId]: normalizedQuantity,
    }));
    setVoucherFinalAmount(null);
  };

  const handleApplyVoucher = async () => {
    if (!voucherCode.trim()) {
      swal("Nhập mã voucher trước khi áp dụng.", "", "warning");
      return;
    }

    if (!selectedSlot) {
      swal("Chọn khung giờ trước khi áp dụng voucher.", "", "warning");
      return;
    }

    try {
      setVoucherLoading(true);
      const finalAmount = await previewVoucher(voucherCode.trim(), estimatedSubtotal);
      setVoucherFinalAmount(finalAmount);
      swal("Voucher hợp lệ", "Giá tạm tính đã được cập nhật.", "success");
    } catch (error) {
      setVoucherFinalAmount(null);
      swal("Voucher không hợp lệ", error.message || "Vui lòng thử lại.", "error");
    } finally {
      setVoucherLoading(false);
    }
  };

  const handleBooking = async () => {
    if (!selectedSlot || !date) return;
    setBookingLoading(true);

    try {
      const token = sessionStorage.getItem("accessToken");
      if (!token) {
        swal("Vui lòng đăng nhập để đặt sân.", "", "warning");
        navigate("/login");
        return;
      }

      if (!paymentMethod) {
        swal("Vui lòng chọn phương thức thanh toán", "", "warning");
        return;
      }

      const bookingData = {
        fieldId: Number(id),
        startTime: `${date}T${selectedSlot.startTime}`,
        endTime: `${date}T${selectedSlot.endTime}`,
        paymentMethod,
        voucherCode: voucherCode.trim() || null,
        services: selectedServices.map((service) => ({
          serviceId: service.id,
          quantity: service.quantity,
        })),
      };

      const booking = await createBooking(bookingData);

      switch (paymentMethod) {
        case "VNPAY":
          if (booking.paymentUrl) {
            window.location.href = booking.paymentUrl;
          } else {
            navigate(`/football-fields/${booking.id}/payment`);
          }
          break;
        case "VIETQR":
          navigate(`/football-fields/${booking.id}/payment`);
          break;
        case "CASH":
          await swal(
            "Đặt sân thành công!",
            `Tổng thanh toán: ${formatMoney(booking.finalPrice || booking.totalPrice)}`,
            "success"
          );
          navigate("/my-bookings");
          break;
        default:
          break;
      }
    } catch (error) {
      console.error("Booking error:", error);
      swal("Đặt sân thất bại!", error.message || "Vui lòng thử lại sau.", "error");
    } finally {
      setBookingLoading(false);
    }
  };

  const isSlotBlockedByMaintenance = (slot) => {
    const maintenanceStart = maintenanceInfo?.maintenanceStartTime;
    const maintenanceEnd = maintenanceInfo?.maintenanceEndTime;
    if (!maintenanceInfo?.isUnderMaintenance || !maintenanceStart || !maintenanceEnd) return false;

    const slotStart = new Date(`${date}T${slot.startTime}`);
    const slotEnd = new Date(`${date}T${slot.endTime}`);
    return slotStart < new Date(maintenanceEnd) && slotEnd > new Date(maintenanceStart);
  };

  return (
    <div className="detail-page">
      <div className="detail-shell">
        <section className="detail-hero-card">
          <div className="detail-media">
            <img src={field?.imageUrl || "/no-image.png"} alt={field?.name || "Football field"} />
            <span className={`detail-status ${maintenanceInfo?.isUnderMaintenance ? "danger" : "success"}`}>
              {maintenanceInfo?.isUnderMaintenance ? (
                <>
                  <Wrench size={15} /> Bảo trì
                </>
              ) : (
                <>
                  <ShieldCheck size={15} /> Sẵn sàng
                </>
              )}
            </span>
          </div>

          <div className="detail-info">
            <span className="detail-eyebrow">{field?.type || "Football field"}</span>
            <h1>{field?.name || "Sân bóng"}</h1>
            <p className="detail-address">
              <MapPin size={18} />
              {field?.address || "Chưa có địa chỉ"}
            </p>
            <p className="detail-description">{field?.description || "Chưa có mô tả cho sân này."}</p>

            <div className="detail-meta-grid">
              <div>
                <span>Giá tham khảo</span>
                <strong>{formatMoney(field?.pricePerHour)}</strong>
              </div>
              <div>
                <span>Đánh giá</span>
                <strong>{totalReviews ? `${averageRating.toFixed(1)}/5 từ ${totalReviews} lượt` : "Chưa có đánh giá"}</strong>
              </div>
            </div>
          </div>
        </section>

        {maintenanceInfo?.isUnderMaintenance && (
          <div className="maintenance-banner">
            <Wrench size={20} />
            <div>
              <strong>Sân đang bảo trì</strong>
              <p>
                {new Date(maintenanceInfo.maintenanceStartTime).toLocaleString("vi-VN")} -{" "}
                {new Date(maintenanceInfo.maintenanceEndTime).toLocaleString("vi-VN")}
              </p>
              {maintenanceInfo.maintenanceReason && <p>Lý do: {maintenanceInfo.maintenanceReason}</p>}
            </div>
          </div>
        )}

        <div className="detail-booking-layout">
          <main className="detail-booking-main">
            <section className="detail-panel">
              <div className="detail-panel-title">
                <CalendarDays size={20} />
                <h2>Chọn ngày</h2>
              </div>
              <Form.Control
                type="date"
                min={new Date().toISOString().split("T")[0]}
                value={date}
                onChange={(event) => {
                  const selectedDate = event.target.value;
                  setDate(selectedDate);
                  if (selectedDate) loadSlots(selectedDate);
                }}
              />
            </section>

            <section className="detail-panel">
              <div className="detail-panel-title">
                <Clock size={20} />
                <h2>Khung giờ</h2>
              </div>

              {loading ? (
                <p className="detail-muted">Đang tải khung giờ...</p>
              ) : slots.length === 0 ? (
                <p className="detail-muted">Chọn ngày để xem khung giờ trống.</p>
              ) : (
                <div className="slot-grid">
                  {slots.map((slot) => {
                    const isSlotUnderMaintenance = isSlotBlockedByMaintenance(slot);
                    const isActive =
                      selectedSlot?.startTime === slot.startTime && selectedSlot?.endTime === slot.endTime;

                    return (
                      <button
                        key={`${slot.startTime}-${slot.endTime}`}
                        disabled={!slot.available || isSlotUnderMaintenance}
                        className={`slot ${isActive ? "active" : ""} ${isSlotUnderMaintenance ? "maintenance" : ""}`}
                        onClick={() => {
                          setSelectedSlot(slot);
                          setVoucherFinalAmount(null);
                        }}
                      >
                        <span className="slot-time">
                          {slot.startTime} - {slot.endTime}
                        </span>
                        <span className="slot-price">
                          {slot.price ? formatMoney(slot.price) : "Chưa có giá"}
                        </span>
                      </button>
                    );
                  })}
                </div>
              )}
            </section>

            <section className="detail-panel">
              <div className="detail-panel-title">
                <Ticket size={20} />
                <h2>Dịch vụ thêm</h2>
              </div>

              {extras.length === 0 ? (
                <p className="detail-muted">Chưa có dịch vụ thêm đang hoạt động.</p>
              ) : (
                <div className="extra-list">
                  {extras.map((service) => (
                    <div className="extra-item" key={service.id}>
                      <div>
                        <strong>{service.name}</strong>
                        <span>{formatMoney(service.price)}</span>
                      </div>
                      <Form.Control
                        type="number"
                        min="0"
                        value={extraQuantities[service.id] || 0}
                        onChange={(event) => updateExtraQuantity(service.id, event.target.value)}
                      />
                    </div>
                  ))}
                </div>
              )}
            </section>
          </main>

          <aside className="detail-summary">
            <section className="detail-panel sticky">
              <h2>Tóm tắt đặt sân</h2>

              <div className="voucher-row">
                <Form.Control
                  placeholder="Nhập mã voucher"
                  value={voucherCode}
                  onChange={(event) => {
                    setVoucherCode(event.target.value);
                    setVoucherFinalAmount(null);
                  }}
                />
                <Button type="button" disabled={voucherLoading || !selectedSlot} onClick={handleApplyVoucher}>
                  {voucherLoading ? "Đang áp dụng" : "Áp dụng"}
                </Button>
              </div>

              <div className="price-breakdown">
                <div>
                  <span>Giá khung giờ</span>
                  <strong>{formatMoney(slotPrice)}</strong>
                </div>
                <div>
                  <span>Dịch vụ thêm</span>
                  <strong>{formatMoney(extraTotal)}</strong>
                </div>
                <div>
                  <span>Giảm voucher</span>
                  <strong>-{formatMoney(estimatedVoucherDiscount)}</strong>
                </div>
                <div className="price-breakdown-total">
                  <span>Tổng tạm tính</span>
                  <strong>{formatMoney(estimatedFinal)}</strong>
                </div>
              </div>

              <div className="payment-method-section">
                <h3>Thanh toán</h3>
                <div className="payment-methods">
                  {["VNPAY", "VIETQR", "CASH"].map((method) => (
                    <button
                      key={method}
                      type="button"
                      className={`payment-method ${paymentMethod === method ? "active" : ""}`}
                      onClick={() => setPaymentMethod(method)}
                    >
                      {method === "CASH" ? "Tiền mặt" : method}
                    </button>
                  ))}
                </div>
              </div>

              <Button
                className="book-btn"
                disabled={!selectedSlot || !date || !paymentMethod}
                onClick={handleBooking}
              >
                {bookingLoading ? "Đang xử lý..." : "Đặt sân"}
              </Button>
            </section>
          </aside>
        </div>

        <section className="reviews-section">
          <div className="reviews-heading">
            <div>
              <span className="detail-eyebrow">Cảm nhận người chơi</span>
              <h2>Đánh giá sân bóng</h2>
            </div>
            <p>{totalReviews} lượt đánh giá sau khi hoàn tất lịch đặt sân.</p>
          </div>

          <div className="reviews-overview">
            <div className="review-score-card">
              <strong>{averageRating.toFixed(1)}</strong>
              <div className="review-stars">{renderStars(Math.round(averageRating))}</div>
              <span>{totalReviews ? `${totalReviews} đánh giá đã xác thực` : "Chưa có đánh giá"}</span>
            </div>

            <div className="review-bars">
              {REVIEW_RATINGS.map((rating) => {
                const count = ratingCounts[rating];
                const width = totalReviews ? Math.round((count / totalReviews) * 100) : 0;

                return (
                  <button
                    type="button"
                    key={rating}
                    className={`review-bar-row ${reviewFilter === rating ? "active" : ""}`}
                    onClick={() => setReviewFilter((current) => (current === rating ? 0 : rating))}
                  >
                    <span>{rating} sao</span>
                    <div className="review-bar-track">
                      <i style={{ width: `${width}%` }} />
                    </div>
                    <strong>{count}</strong>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="review-filter-row">
            <button
              type="button"
              className={reviewFilter === 0 ? "active" : ""}
              onClick={() => setReviewFilter(0)}
            >
              Tất cả
            </button>
            {REVIEW_RATINGS.map((rating) => (
              <button
                type="button"
                key={rating}
                className={reviewFilter === rating ? "active" : ""}
                onClick={() => setReviewFilter(rating)}
              >
                {rating} sao
              </button>
            ))}
          </div>

          {reviews.length === 0 ? (
            <div className="review-empty-card">
              <MessageSquareText size={28} />
              <strong>Chưa có đánh giá nào</strong>
              <p>Những khách đã hoàn tất lịch đặt sân sẽ có thể để lại đánh giá tại đây.</p>
            </div>
          ) : filteredReviews.length === 0 ? (
            <div className="review-empty-card">
              <MessageSquareText size={28} />
              <strong>Không có đánh giá phù hợp</strong>
              <p>Thử chọn lại bộ lọc khác để xem thêm nhận xét.</p>
            </div>
          ) : (
            <div className="review-grid">
              {filteredReviews.map((review) => (
                <article key={review.id} className="review-card">
                  <div className="review-top">
                    <div>
                      <strong>{review.userName || review.user?.username || "Người dùng"}</strong>
                      <small>{new Date(review.createdAt).toLocaleDateString("vi-VN")}</small>
                    </div>
                    <span className="review-rating">{renderStars(review.rating)}</span>
                  </div>
                  <p className="review-comment">{review.comment}</p>
                  {review.fieldName && <span className="review-field-name">{review.fieldName}</span>}
                </article>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
