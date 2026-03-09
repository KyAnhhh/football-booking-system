import React, { useEffect } from "react";
import "../../assets/styles/Home.css";
import { Link } from "react-router-dom";

export default function Home() {

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("animate-in");
          }
        });
      },
      { threshold: 0.1 }
    );

    const animatedElements = document.querySelectorAll(".animate-on-scroll");
    animatedElements.forEach((el) => observer.observe(el));

    return () => {
      animatedElements.forEach((el) => observer.unobserve(el));
    };
  }, []);

  return (
    <div className="home-container">
      <section className="hero-section">
        <div className="hero-bg-animation">
          <div className="floating-ball ball-1"></div>
          <div className="floating-ball ball-2"></div>
          <div className="floating-ball ball-3"></div>
        </div>
        <div className="hero-content">
          <h1 className="hero-title">Chào Mừng Đến Với Football Shop</h1>
          <p className="hero-subtitle">
            Khám phá bộ sưu tập giày bóng đá cao cấp và thiết bị chuyên nghiệp
          </p>
          <Link to="/products" className="cta-button">
            Mua Ngay
          </Link>
        </div>
      </section>

      <section className="top-brands-section animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Thương Hiệu Hàng Đầu</h2>
          <div className="brands-grid">
            <div className="brand-item">
              <img
                src="https://logos-world.net/wp-content/uploads/2020/04/Nike-Logo.png"
                alt="Nike"
                className="brand-logo"
              />
            </div>
            <div className="brand-item">
              <img
                src="https://logos-world.net/wp-content/uploads/2020/04/Adidas-Logo.png"
                alt="Adidas"
                className="brand-logo"
              />
            </div>
            <div className="brand-item">
              <img
                src="https://logos-world.net/wp-content/uploads/2020/04/Puma-Logo.png"
                alt="Puma"
                className="brand-logo"
              />
            </div>
            <div className="brand-item">
              <img
                src="https://res.cloudinary.com/do8twj1im/image/upload/v1766897225/ey0msayn32izigq8fyw5.png"
                alt="Mizuno"
                className="brand-logo"
              />
            </div>
          </div>
        </div>
      </section>

      <section className="why-choose-us-section animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Tại Sao Chọn Chúng Tôi?</h2>
          <div className="why-choose-us-content">
            <div className="why-choose-us-item">
              <h3>Chất Lượng Chính Hãng</h3>
              <p>
                Tất cả sản phẩm đều là hàng chính hãng từ các thương hiệu uy tín như Nike, Adidas, Puma, Mizuno.
              </p>
            </div>
            <div className="why-choose-us-item">
              <h3>Giá Cả Cạnh Tranh</h3>
              <p>
                Chúng tôi cam kết mang đến mức giá tốt nhất cho khách hàng, kèm theo nhiều ưu đãi hấp dẫn.
              </p>
            </div>
            <div className="why-choose-us-item">
              <h3>Hỗ Trợ Tận Tình</h3>
              <p>
                Đội ngũ tư vấn viên sẵn sàng hỗ trợ 24/7 để giúp bạn chọn sản phẩm phù hợp nhất.
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="faq-section animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Câu Hỏi Thường Gặp</h2>
          <div className="faq-content">
            <div className="faq-item">
              <h3>Sản phẩm có chính hãng không?</h3>
              <p>
                Có, tất cả sản phẩm tại shop đều là hàng chính hãng, có giấy tờ chứng nhận từ các thương hiệu.
              </p>
            </div>
            <div className="faq-item">
              <h3>Thời gian giao hàng bao lâu?</h3>
              <p>
                Giao hàng miễn phí toàn quốc trong 2-3 ngày làm việc, tùy thuộc vào khu vực.
              </p>
            </div>
            <div className="faq-item">
              <h3>Chính sách đổi trả như thế nào?</h3>
              <p>
                Chúng tôi hỗ trợ đổi trả trong 30 ngày nếu sản phẩm có lỗi từ nhà sản xuất hoặc không đúng mô tả.
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="features-section animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Ưu Điểm Nổi Bật</h2>
          <div className="features-content">
            <div className="feature-item">
              <div className="feature-icon">🚚</div>
              <h3>Giao Hàng Nhanh</h3>
              <p>Giao hàng miễn phí toàn quốc trong 2-3 ngày</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon">✅</div>
              <h3>Chất Lượng Đảm Bảo</h3>
              <p>Hàng chính hãng 100% từ các thương hiệu lớn</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon">💰</div>
              <h3>Hoàn Tiền 30 Ngày</h3>
              <p>Đổi trả dễ dàng nếu không hài lòng</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}