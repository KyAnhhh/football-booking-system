import { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  ArrowRight,
  Award,
  BadgeCheck,
  CalendarDays,
  CheckCircle2,
  Clock,
  MapPin,
  PackageCheck,
  RefreshCw,
  Search,
  ShieldCheck,
  ShoppingBag,
  Sparkles,
  Trophy,
  Truck,
} from "lucide-react";

import "../../assets/styles/Home.css";
import { getAvailableFields } from "../../services/fieldService";
import { getTop4PopularProducts } from "../../services/productService";
import { getProfile } from "../../services/userService";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const formatSold = (value) => {
  const number = Number(value || 0);
  return number >= 1000 ? `${(number / 1000).toFixed(1)}k` : String(number);
};

const getLowestPrice = (product) => {
  const prices =
    product.variants
      ?.map((variant) => Number(variant.price || 0))
      .filter((price) => price > 0) || [];
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
  const showcaseSearch = useMemo(getShowcaseSearch, []);
  const [products, setProducts] = useState([]);
  const [fields, setFields] = useState([]);
  const [profile, setProfile] = useState(null);
  const [productsLoading, setProductsLoading] = useState(true);
  const [fieldsLoading, setFieldsLoading] = useState(true);
  const [productsError, setProductsError] = useState("");
  const [fieldsError, setFieldsError] = useState("");
  const [searchForm, setSearchForm] = useState(() => ({
    date: showcaseSearch.date,
    slot: `${showcaseSearch.startTime}|${showcaseSearch.endTime}`,
    type: "",
  }));

  const loadHomeData = useCallback(async () => {
    setProductsLoading(true);
    setFieldsLoading(true);
    setProductsError("");
    setFieldsError("");

    const hasSession = Boolean(sessionStorage.getItem("accessToken"));
    const requests = [
      getTop4PopularProducts(),
      getAvailableFields({
        page: 0,
        size: 3,
        ...showcaseSearch,
      }),
      hasSession ? getProfile() : Promise.resolve(null),
    ];
    const [productResult, fieldResult, profileResult] =
      await Promise.allSettled(requests);

    if (productResult.status === "fulfilled") {
      const response = productResult.value;
      setProducts(response?.data || response || []);
    } else {
      setProducts([]);
      setProductsError("Chưa thể tải sản phẩm nổi bật.");
    }

    if (fieldResult.status === "fulfilled") {
      setFields(fieldResult.value?.content || []);
    } else {
      setFields([]);
      setFieldsError("Chưa thể kiểm tra sân trống lúc này.");
    }

    setProfile(
      profileResult.status === "fulfilled" ? profileResult.value : null
    );
    setProductsLoading(false);
    setFieldsLoading(false);
  }, [showcaseSearch]);

  useEffect(() => {
    loadHomeData();
  }, [loadHomeData]);

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
    <main className="home-page">
      <section className="home-hero">
        <div className="home-hero-inner">
          <div className="home-hero-copy">
            <p className="home-eyebrow">Football Shop · Matchday hub</p>
            <h1>
              Đặt sân tối nay.
              <span>Chuẩn bị trọn bộ cho ngày thi đấu.</span>
            </h1>
            <p className="home-hero-description">
              Tìm sân còn trống theo giờ thực tế, mua dụng cụ bóng đá và theo
              dõi mọi lịch chơi trong cùng một tài khoản.
            </p>
            <div className="home-hero-actions">
              <a href="#field-search">Tìm sân trống</a>
              <Link to="/products">Khám phá sản phẩm</Link>
            </div>
            <div className="home-hero-proof" aria-label="Cam kết dịch vụ">
              <span>
                <CheckCircle2 size={16} /> Giá hiển thị trước
              </span>
              <span>
                <CheckCircle2 size={16} /> Slot 90 phút
              </span>
              <span>
                <CheckCircle2 size={16} /> Theo dõi trực tuyến
              </span>
            </div>
          </div>

          <aside className="home-match-card" aria-label="Lịch thi đấu gợi ý">
            <div>
              <span>Khung giờ được chọn nhiều</span>
              <strong>
                {new Date(`${showcaseSearch.date}T00:00:00`).toLocaleDateString(
                  "vi-VN",
                  { weekday: "long", day: "2-digit", month: "2-digit" }
                )}
              </strong>
            </div>
            <div className="home-match-time">
              <Clock size={19} />
              {showcaseSearch.startTime} — {showcaseSearch.endTime}
            </div>
            <Link
              to={`/football-fields?date=${showcaseSearch.date}&startTime=${showcaseSearch.startTime}&endTime=${showcaseSearch.endTime}`}
            >
              Xem sân còn trống <ArrowRight size={16} />
            </Link>
          </aside>
        </div>

        <form
          id="field-search"
          className="home-search"
          onSubmit={handleFieldSearch}
        >
          <div className="home-search-heading">
            <Search size={21} />
            <div>
              <strong>Tìm sân phù hợp</strong>
              <span>Chọn lịch, hệ thống sẽ kiểm tra slot và giá.</span>
            </div>
          </div>
          <label>
            <span>
              <CalendarDays size={15} /> Ngày đá
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
              <Clock size={15} /> Khung giờ
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
              <Trophy size={15} /> Loại sân
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
            Kiểm tra sân <ArrowRight size={17} />
          </button>
        </form>
      </section>

      {profile ? (
        <section className="home-member-strip" aria-label="Tài khoản của bạn">
          <div className="home-member-copy">
            <span className="home-member-icon">
              <Award size={23} />
            </span>
            <div>
              <small>Xin chào, {profile.username}</small>
              <strong>
                {profile.membershipTierName || "Chưa xếp hạng"} ·{" "}
                {profile.loyaltyPoints || 0} điểm
              </strong>
            </div>
          </div>
          <nav aria-label="Lối tắt tài khoản">
            <Link to="/my-bookings">Lịch đặt sân</Link>
            <Link to="/my-orders">Đơn hàng</Link>
            <Link to="/wishlist">Yêu thích</Link>
            <Link to="/profile">Xem hạng thành viên</Link>
          </nav>
        </section>
      ) : (
        <section className="home-utility-strip" aria-label="Tiện ích hệ thống">
          <span>
            <CalendarDays size={20} />
            Đặt sân theo slot thực tế
          </span>
          <span>
            <ShoppingBag size={20} />
            Mua sắm trong cùng tài khoản
          </span>
          <span>
            <PackageCheck size={20} />
            Theo dõi đơn và booking
          </span>
        </section>
      )}

      <section className="home-section">
        <header className="home-section-header">
          <div>
            <p className="home-eyebrow">Có thể đặt gần nhất</p>
            <h2>Sân trống cho trận đấu sắp tới</h2>
            <span className="home-section-context">
              {new Date(`${showcaseSearch.date}T00:00:00`).toLocaleDateString(
                "vi-VN"
              )}{" "}
              · {showcaseSearch.startTime} - {showcaseSearch.endTime}
            </span>
          </div>
          <Link
            to={`/football-fields?date=${showcaseSearch.date}&startTime=${showcaseSearch.startTime}&endTime=${showcaseSearch.endTime}`}
          >
            Xem tất cả sân <ArrowRight size={17} />
          </Link>
        </header>

        {fieldsLoading ? (
          <div className="home-card-grid home-field-grid" aria-label="Đang tải sân">
            {Array.from({ length: 3 }, (_, index) => (
              <div className="home-card home-skeleton" key={index} />
            ))}
          </div>
        ) : fieldsError ? (
          <ErrorState message={fieldsError} onRetry={loadHomeData} />
        ) : fields.length ? (
          <div className="home-card-grid home-field-grid">
            {fields.map((field) => (
              <FieldCard
                field={field}
                key={field.id}
                search={showcaseSearch}
              />
            ))}
          </div>
        ) : (
          <div className="home-empty">
            <Clock size={26} />
            <strong>Khung giờ này vừa hết sân trống</strong>
            <span>Chọn ngày hoặc giờ khác để tìm thêm sân phù hợp.</span>
            <Link to="/football-fields">Tìm khung giờ khác</Link>
          </div>
        )}
      </section>

      <section className="home-section">
        <header className="home-section-header">
          <div>
            <p className="home-eyebrow">Trang bị cho trận đấu</p>
            <h2>Sản phẩm được chọn nhiều gần đây</h2>
          </div>
          <Link to="/products">
            Xem cửa hàng <ArrowRight size={17} />
          </Link>
        </header>

        {productsLoading ? (
          <div className="home-card-grid home-product-grid" aria-label="Đang tải sản phẩm">
            {Array.from({ length: 4 }, (_, index) => (
              <div className="home-card home-skeleton" key={index} />
            ))}
          </div>
        ) : productsError ? (
          <ErrorState message={productsError} onRetry={loadHomeData} />
        ) : products.length ? (
          <div className="home-card-grid home-product-grid">
            {products.map((product) => (
              <ProductCard product={product} key={product.id} />
            ))}
          </div>
        ) : (
          <div className="home-empty">
            <ShoppingBag size={26} />
            <strong>Chưa có sản phẩm nổi bật</strong>
            <span>Khám phá toàn bộ danh mục sản phẩm đang bán.</span>
            <Link to="/products">Đến cửa hàng</Link>
          </div>
        )}
      </section>

      <section className="home-steps">
        <header>
          <p className="home-eyebrow">Từ tìm sân đến ra sân</p>
          <h2>Đặt lịch trong ba bước rõ ràng</h2>
        </header>
        <div>
          <article>
            <span>01</span>
            <CalendarDays size={24} />
            <h3>Chọn thời gian</h3>
            <p>Nhập ngày, khung giờ và loại sân bạn cần.</p>
          </article>
          <article>
            <span>02</span>
            <Trophy size={24} />
            <h3>Chọn sân và dịch vụ</h3>
            <p>Xem giá, slot còn trống và thêm dịch vụ đi kèm.</p>
          </article>
          <article>
            <span>03</span>
            <BadgeCheck size={24} />
            <h3>Xác nhận booking</h3>
            <p>Áp dụng voucher, ưu đãi thành viên và theo dõi lịch.</p>
          </article>
        </div>
      </section>

      <section className="home-trust">
        <div className="home-trust-heading">
          <p className="home-eyebrow">Một tài khoản cho cả đội</p>
          <h2>Mọi thứ cần thiết trước giờ bóng lăn</h2>
          <Link to={profile ? "/profile" : "/register"}>
            {profile ? "Xem quyền lợi của bạn" : "Tạo tài khoản miễn phí"}
            <ArrowRight size={17} />
          </Link>
        </div>
        <div className="home-benefits">
          <article>
            <BadgeCheck size={24} />
            <h3>Sản phẩm rõ nguồn gốc</h3>
            <p>Giá, biến thể và tình trạng sản phẩm hiển thị trước khi mua.</p>
          </article>
          <article>
            <ShieldCheck size={24} />
            <h3>Slot được kiểm tra</h3>
            <p>Lịch bảo trì và booking trùng giờ được kiểm tra khi đặt sân.</p>
          </article>
          <article>
            <Award size={24} />
            <h3>Tích điểm thành viên</h3>
            <p>Booking hoàn tất giúp mở khóa hạng và ưu đãi tiếp theo.</p>
          </article>
          <article>
            <Truck size={24} />
            <h3>Theo dõi tập trung</h3>
            <p>Đơn hàng, booking và thông báo nằm trong cùng tài khoản.</p>
          </article>
        </div>
      </section>

      <section className="home-faq">
        <header>
          <p className="home-eyebrow">Trước khi bắt đầu</p>
          <h2>Câu hỏi thường gặp</h2>
        </header>
        <div>
          <details>
            <summary>Sản phẩm có chính hãng không?</summary>
            <p>
              Shop ưu tiên sản phẩm có nguồn gốc rõ ràng và công khai giá,
              biến thể cùng tình trạng trước khi bán.
            </p>
          </details>
          <details>
            <summary>Tôi có thể biết giá sân trước khi đặt không?</summary>
            <p>
              Có. Hệ thống tính giá theo sân, khung giờ và ngày lễ trước khi
              bạn xác nhận booking.
            </p>
          </details>
          <details>
            <summary>Ưu đãi thành viên được áp dụng thế nào?</summary>
            <p>
              Hạng thành viên được xác định từ điểm loyalty và ưu đãi được
              tính tự động trong quá trình đặt sân.
            </p>
          </details>
        </div>
      </section>
    </main>
  );
}

function ErrorState({ message, onRetry }) {
  return (
    <div className="home-empty home-error" role="alert">
      <RefreshCw size={25} />
      <strong>{message}</strong>
      <span>Kiểm tra kết nối hoặc thử tải lại dữ liệu.</span>
      <button type="button" onClick={onRetry}>
        <RefreshCw size={16} /> Thử lại
      </button>
    </div>
  );
}

function FieldCard({ field, search }) {
  const detailUrl = `/football-fields/${field.id}/detail?date=${search.date}&startTime=${search.startTime}&endTime=${search.endTime}`;
  return (
    <article className="home-card home-field-card">
      <Link className="home-card-media home-field-media" to={detailUrl}>
        <img
          src={field.imageUrl || "/no-image.png"}
          alt={field.name}
          loading="lazy"
        />
        <span className="home-availability">Còn trống</span>
      </Link>
      <div className="home-card-body">
        <small>{getFieldTypeLabel(field.type)}</small>
        <Link className="home-card-title" to={detailUrl}>
          {field.name}
        </Link>
        <p className="home-card-location">
          <MapPin size={16} />
          {field.address || "Chưa cập nhật địa chỉ"}
        </p>
        <div className="home-card-meta">
          <span>Giá khung giờ</span>
          <strong>{formatCurrency(field.slotPrice)}</strong>
        </div>
        <Link className="home-card-action" to={detailUrl}>
          Xem và đặt sân <ArrowRight size={16} />
        </Link>
      </div>
    </article>
  );
}

function ProductCard({ product }) {
  const minPrice = getLowestPrice(product);
  const discount = Number(product.discount || 0);
  const finalPrice = minPrice
    ? minPrice - (minPrice * discount) / 100
    : null;
  const image =
    product.imageUrls?.[0] ||
    product.variants?.[0]?.image ||
    "https://placehold.co/600x600/f1f5f9/0f172a?text=Product";
  const soldQuantity =
    product.variants?.reduce(
      (total, variant) => total + Number(variant.soldQuantity || 0),
      0
    ) || 0;
  const detailUrl = `/products/product-details/${product.id}`;

  return (
    <article className="home-card home-product-card">
      <Link className="home-card-media home-product-media" to={detailUrl}>
        {discount > 0 ? <span className="home-discount">-{discount}%</span> : null}
        <img src={image} alt={product.name} loading="lazy" />
      </Link>
      <div className="home-card-body">
        <Link className="home-card-title" to={detailUrl}>
          {product.name}
        </Link>
        <div className="home-product-price">
          {finalPrice ? (
            <>
              <strong>{formatCurrency(finalPrice)}</strong>
              {discount > 0 ? <del>{formatCurrency(minPrice)}</del> : null}
            </>
          ) : (
            <strong>Liên hệ</strong>
          )}
        </div>
        <p className="home-sold">
          <Sparkles size={15} /> {formatSold(soldQuantity)} đã bán
        </p>
        <Link className="home-card-action" to={detailUrl}>
          Xem sản phẩm <ArrowRight size={16} />
        </Link>
      </div>
    </article>
  );
}
