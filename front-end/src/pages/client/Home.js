import { useEffect, useState } from "react";
import "../../assets/styles/Home.css";
import { Link } from "react-router-dom";
import { getTop4PopularProducts } from "../../services/productService";
import { ArrowRight, BadgeCheck, PackageCheck, ShieldCheck, Truck } from "lucide-react";

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

export default function Home() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    getTop4PopularProducts()
      .then((res) => setProducts(res?.data || res || []))
      .catch(() => setProducts([]));
  }, []);

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
        </div>
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
