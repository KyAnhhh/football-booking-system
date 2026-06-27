import { useEffect, useMemo, useState } from "react";
import "../../assets/styles/Home.css";
import { Link, useNavigate } from "react-router-dom";
import { getTop4PopularProducts } from "../../services/productService";
import { getAvailableFields } from "../../services/fieldService";
import {
  ArrowRight,
  BadgeCheck,
  CalendarDays,
  Clock,
  MapPin,
  PackageCheck,
  Search,
  ShieldCheck,
  Trophy,
  Truck,
} from "lucide-react";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const formatSold = (num) => {
  if (!num) return "0";
  if (num >= 1000) return `${(num / 1000).toFixed(1)}k`;
  return num;
};

const getLowestPrice = (product) => {
  const prices = product.variants?.map((variant) => Number(variant.price || 0)).filter(Boolean) || [];
  return prices.length ? Math.min(...prices) : null;
};

const FIELD_TYPES = [
  { value: "", label: "Tất cả loại sân" },
  { value: "FIVE_A_SIDE", label: "Sân 5 người" },
  { value: "SEVEN_A_SIDE", label: "Sân 7 người" },
  { value: "ELEVEN_A_SIDE", label: "Sân 11 người" },
];

const SLOT_OPTIONS = [
  ["07:00", "08:30"],
  ["08:30", "10:00"],
  ["10:00", "11:30"],
  ["13:00", "14:30"],
  ["14:30", "16:00"],
  ["16:00", "17:30"],
  ["17:30", "19:00"],
  ["19:00", "20:30"],
  ["20:30", "22:00"],
  ["22:00", "23:30"],
].map(([startTime, endTime]) => ({
  value: `${startTime}|${endTime}`,
  label: `${startTime} - ${endTime}`,
  startTime,
  endTime,
}));

const toDateValue = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const getShowcaseSearch = () => {
  const now = new Date();
  const date = new Date(now);
  if (now.getHours() >= 19) date.setDate(date.getDate() + 1);
  return {
    date: toDateValue(date),
    startTime: "19:00",
    endTime: "20:30",
  };
};

const getFieldTypeLabel = (type) =>
  FIELD_TYPES.find((item) => item.value === type)?.label || type;

export default function Home() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [fields, setFields] = useState([]);
  const [fieldsLoading, setFieldsLoading] = useState(true);
  const [searchForm, setSearchForm] = useState(() => {
    const showcase = getShowcaseSearch();
    return {
      date: showcase.date,
      slot: `${showcase.startTime}|${showcase.endTime}`,
      type: "",
    };
  });
  const showcaseSearch = useMemo(getShowcaseSearch, []);

  useEffect(() => {
    const loadHomeData = async () => {
      const [productResult, fieldResult] = await Promise.allSettled([
        getTop4PopularProducts(),
        getAvailableFields({
          page: 0,
          size: 3,
          ...showcaseSearch,
        }),
      ]);

      if (productResult.status === "fulfilled") {
        const response = productResult.value;
        setProducts(response?.data || response || []);
      }

      if (fieldResult.status === "fulfilled") {
        setFields(fieldResult.value?.content || []);
      }
      setFieldsLoading(false);
    };

    loadHomeData();
  }, [showcaseSearch]);

  const handleFieldSearch = (event) => {
    event.preventDefault();
    const selectedSlot = SLOT_OPTIONS.find(
      (option) => option.value === searchForm.slot
    );
    if (!searchForm.date || !selectedSlot) return;

    const params = new URLSearchParams({
      date: searchForm.date,
      startTime: selectedSlot.startTime,
      endTime: selectedSlot.endTime,
    });
    if (searchForm.type) params.set("type", searchForm.type);
    navigate(`/football-fields?${params.toString()}`);
  };

  return (
    <div className="home-core-page">
      <section className="home-core-hero">
        <div className="home-core-hero-content">
          <p>Cửa hàng & đặt sân bóng đá</p>
          <h1>Trang bị tốt hơn cho trận đấu tiếp theo</h1>
          <span>
            Mua sản phẩm bóng đá chính hãng, đặt sân nhanh và theo dõi mọi lịch
            chơi trong một hệ thống duy nhất.
          </span>
          <div className="home-core-actions">
            <Link to="/products">Mua sản phẩm</Link>
            <Link to="/football-fields" className="secondary">
              Đặt sân ngay
            </Link>
          </div>

          <form className="home-field-search" onSubmit={handleFieldSearch}>
            <label>
              <span>
                <CalendarDays size={16} />
                Ngày đá
              </span>
              <input
                type="date"
                min={toDateValue(new Date())}
                value={searchForm.date}
                onChange={(event) =>
                  setSearchForm((current) => ({
                    ...current,
                    date: event.target.value,
                  }))
                }
                required
              />
            </label>

            <label>
              <span>
                <Clock size={16} />
                Khung giờ
              </span>
              <select
                value={searchForm.slot}
                onChange={(event) =>
                  setSearchForm((current) => ({
                    ...current,
                    slot: event.target.value,
                  }))
                }
                required
              >
                {SLOT_OPTIONS.map((slot) => (
                  <option key={slot.value} value={slot.value}>
                    {slot.label}
                  </option>
                ))}
              </select>
            </label>

            <label>
              <span>
                <Trophy size={16} />
                Loại sân
              </span>
              <select
                value={searchForm.type}
                onChange={(event) =>
                  setSearchForm((current) => ({
                    ...current,
                    type: event.target.value,
                  }))
                }
              >
                {FIELD_TYPES.map((type) => (
                  <option key={type.value || "all"} value={type.value}>
                    {type.label}
                  </option>
                ))}
              </select>
            </label>

            <button type="submit">
              <Search size={18} />
              Tìm sân trống
            </button>
          </form>
        </div>
      </section>

      <section className="home-core-section home-field-section">
        <div className="home-core-section-header">
          <div>
            <p>Có thể đặt gần nhất</p>
            <h2>Sân trống cho trận đấu sắp tới</h2>
            <span className="home-field-window">
              {new Date(`${showcaseSearch.date}T00:00:00`).toLocaleDateString(
                "vi-VN"
              )}{" "}
              · {showcaseSearch.startTime} - {showcaseSearch.endTime}
            </span>
          </div>
          <Link
            to={`/football-fields?date=${showcaseSearch.date}&startTime=${showcaseSearch.startTime}&endTime=${showcaseSearch.endTime}`}
          >
            Xem lịch sân
            <ArrowRight size={17} />
          </Link>
        </div>

        {fieldsLoading ? (
          <div className="home-field-grid" aria-label="Đang tải sân bóng">
            {Array.from({ length: 3 }, (_, index) => (
              <div className="home-field-card skeleton" key={index} />
            ))}
          </div>
        ) : fields.length > 0 ? (
          <div className="home-field-grid">
            {fields.map((field) => (
              <article className="home-field-card" key={field.id}>
                <Link
                  className="home-field-media"
                  to={`/football-fields/${field.id}/detail?date=${showcaseSearch.date}&startTime=${showcaseSearch.startTime}&endTime=${showcaseSearch.endTime}`}
                >
                  <img
                    src={field.imageUrl || "/no-image.png"}
                    alt={field.name}
                  />
                  <span>Còn trống</span>
                </Link>
                <div className="home-field-content">
                  <div>
                    <small>{getFieldTypeLabel(field.type)}</small>
                    <h3>{field.name}</h3>
                  </div>
                  <p>
                    <MapPin size={16} />
                    {field.address || "Chưa cập nhật địa chỉ"}
                  </p>
                  <div className="home-field-price">
                    <span>Giá khung giờ</span>
                    <strong>{formatCurrency(field.slotPrice)}</strong>
                  </div>
                  <Link
                    to={`/football-fields/${field.id}/detail?date=${showcaseSearch.date}&startTime=${showcaseSearch.startTime}&endTime=${showcaseSearch.endTime}`}
                  >
                    Xem và đặt sân
                    <ArrowRight size={16} />
                  </Link>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <div className="home-field-empty">
            <Clock size={24} />
            <strong>Khung giờ này vừa hết sân trống</strong>
            <span>Chọn ngày và giờ khác để xem các sân có thể đặt.</span>
            <Link to="/football-fields">Tìm khung giờ khác</Link>
          </div>
        )}
      </section>

      <section className="home-core-section">
        <div className="home-core-section-header">
          <div>
            <p>Sản phẩm nổi bật</p>
            <h2>Được chọn nhiều gần đây</h2>
          </div>
          <Link to="/products">
            Xem tất cả
            <ArrowRight size={17} />
          </Link>
        </div>

        <div className="home-core-product-grid">
          {products.length === 0
            ? Array.from({ length: 4 }, (_, index) => (
                <div className="home-core-product-card skeleton" key={index} />
              ))
            : products.map((item) => {
                const minPrice = getLowestPrice(item);
                const discount = Number(item.discount || 0);
                const finalPrice = minPrice
                  ? minPrice - (minPrice * discount) / 100
                  : null;
                const image =
                  item.imageUrls?.[0] ||
                  item.variants?.[0]?.image ||
                  "https://placehold.co/600x600/f1f5f9/0f172a?text=Product";
                const soldQuantity =
                  item.variants?.reduce(
                    (total, variant) => total + Number(variant.soldQuantity || 0),
                    0
                  ) || 0;

                return (
                  <article className="home-core-product-card" key={item.id}>
                    <Link
                      to={`/products/product-details/${item.id}`}
                      className="home-core-product-media"
                    >
                      {discount > 0 && <span>-{discount}%</span>}
                      <img src={image} alt={item.name} />
                    </Link>
                    <div className="home-core-product-info">
                      <h3>{item.name}</h3>
                      <div className="home-core-price-row">
                        {finalPrice ? (
                          <>
                            <strong>{formatCurrency(finalPrice)}</strong>
                            {discount > 0 && minPrice && (
                              <small>{formatCurrency(minPrice)}</small>
                            )}
                          </>
                        ) : (
                          <strong>Liên hệ</strong>
                        )}
                      </div>
                      <p>{formatSold(soldQuantity)} đã bán</p>
                    </div>
                  </article>
                );
              })}
        </div>
      </section>

      <section className="home-core-band">
        <div className="home-core-section-header">
          <div>
            <p>Lý do chọn chúng tôi</p>
            <h2>Mua sắm và đặt sân mượt hơn</h2>
          </div>
        </div>

        <div className="home-core-benefits">
          <article>
            <BadgeCheck size={26} />
            <h3>Hàng chính hãng</h3>
            <p>Sản phẩm được kiểm tra nguồn gốc, giá và tình trạng trước khi bán.</p>
          </article>
          <article>
            <Truck size={26} />
            <h3>Giao hàng rõ ràng</h3>
            <p>Chi phí vận chuyển được hiển thị trước khi bạn xác nhận đơn hàng.</p>
          </article>
          <article>
            <ShieldCheck size={26} />
            <h3>Đặt sân an toàn</h3>
            <p>Khung giờ, bảo trì, giá và phụ phí được kiểm tra ngay khi đặt sân.</p>
          </article>
          <article>
            <PackageCheck size={26} />
            <h3>Theo dõi dễ dàng</h3>
            <p>Quản lý đơn hàng, lịch đặt sân và đánh giá trong cùng tài khoản.</p>
          </article>
        </div>
      </section>

      <section className="home-core-section">
        <div className="home-core-faq">
          <div>
            <p>Câu hỏi thường gặp</p>
            <h2>Bạn cần biết gì trước khi mua?</h2>
          </div>
          <article>
            <h3>Sản phẩm có chính hãng không?</h3>
            <p>Có. Shop ưu tiên sản phẩm có nguồn gốc rõ ràng và thông tin giá minh bạch.</p>
          </article>
          <article>
            <h3>Tôi có thể đặt sân online không?</h3>
            <p>Có. Bạn có thể chọn sân, ngày, khung giờ, extra, voucher và xem giá trước khi tạo booking.</p>
          </article>
          <article>
            <h3>Thanh toán được xử lý thế nào?</h3>
            <p>Đơn hàng và lịch đặt sân có trạng thái riêng để bạn theo dõi sau khi tạo yêu cầu.</p>
          </article>
        </div>
      </section>
    </div>
  );
}
