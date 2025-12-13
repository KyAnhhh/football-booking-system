import React, { useState, useEffect } from "react";
import "../../assets/styles/Home.css";
import { Link } from "react-router-dom";

export default function Home() {
  const [featuredProducts] = useState([
    {
      id: 1,
      title: "Nike Mercurial Vapor 15",
      price: "2.990.000 VNĐ",
      image: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&fit=crop",
    },
    {
      id: 2,
      title: "Adidas Predator Edge",
      price: "2.790.000 VNĐ",
      image: "https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400&fit=crop",
    },
    {
      id: 3,
      title: "Puma Future Z",
      price: "2.590.000 VNĐ",
      image: "https://images.unsplash.com/photo-1605348540609-85bea8d192b9?w=400&fit=crop",
    },
    {
      id: 4,
      title: "Mizuno Rebula 3",
      price: "2.490.000 VNĐ",
      image: "https://images.unsplash.com/photo-1599556943850-d0257396db20?w=400&fit=crop",
    },
  ]);

  const [reviews] = useState([
    {
      id: 1,
      name: "Nguyễn Văn A",
      rating: 5,
      comment: "Giày rất chất lượng, giao hàng nhanh, shop hỗ trợ nhiệt tình!",
      avatar: "https://randomuser.me/api/portraits/men/1.jpg",
    },
    {
      id: 2,
      name: "Trần Thị B",
      rating: 4,
      comment: "Sản phẩm đúng mô tả, giá cả hợp lý, sẽ quay lại mua thêm.",
      avatar: "https://randomuser.me/api/portraits/women/2.jpg",
    },
    {
      id: 3,
      name: "Lê Minh C",
      rating: 5,
      comment: "Thiết kế giày đẹp, đi êm chân, đúng là hàng chính hãng!",
      avatar: "https://randomuser.me/api/portraits/men/3.jpg",
    },
  ]);

  const [blogs] = useState([
    {
      id: 1,
      title: "Top 5 Mẫu Giày Bóng Đá Đẹp Nhất 2023",
      excerpt: "Khám phá những mẫu giày bóng đá đang làm mưa làm gió trên thị trường.",
      image: "https://images.unsplash.com/photo-1543332164-6e82f355badc?w=400&fit=crop",
    },
    {
      id: 2,
      title: "Cách Chọn Giày Bóng Đá Phù Hợp Với Vị Trí Thi Đấu",
      excerpt: "Hướng dẫn chi tiết để chọn giày phù hợp với phong cách chơi của bạn.",
      image: "https://images.unsplash.com/photo-1558793058-82853d6453f1?w=400&fit=crop",
    },
  ]);

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
      {/* Hero Section */}
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

      {/* Featured Products Section */}
      <section className="featured-products animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Sản Phẩm Nổi Bật</h2>
          <div className="products-grid">
            {featuredProducts.map((product) => (
              <div key={product.id} className="product-card">
                <div className="card-image-container">
                  <img
                    src={product.image}
                    alt={product.title}
                    className="card-image"
                  />
                  <div className="card-overlay">
                    <button className="add-to-cart-btn">Thêm Vào Giỏ</button>
                  </div>
                </div>
                <div className="card-content">
                  <h3 className="card-title">{product.title}</h3>
                  <p className="card-price">{product.price}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Top Brands Section */}
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
                src="https://res.cloudinary.com/do8twj1im/image/upload/v1765553051/a9o3lnyu9ga0tuqjjxp6.png"
                alt="Mizuno"
                className="brand-logo"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Why Choose Us Section */}
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

      {/* Customer Reviews Section */}
      <section className="reviews-section animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Đánh Giá Từ Khách Hàng</h2>
          <div className="reviews-grid">
            {reviews.map((review) => (
              <div key={review.id} className="review-card">
                <img
                  src={review.avatar}
                  alt={review.name}
                  className="review-avatar"
                />
                <h3 className="review-name">{review.name}</h3>
                <div className="review-rating">
                  {"★".repeat(review.rating)}
                  {"☆".repeat(5 - review.rating)}
                </div>
                <p className="review-comment">{review.comment}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Blog/News Section */}
      <section className="blog-section animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Tin Tức & Blog</h2>
          <div className="blog-grid">
            {blogs.map((blog) => (
              <div key={blog.id} className="blog-card">
                <img
                  src={blog.image}
                  alt={blog.title}
                  className="blog-image"
                />
                <div className="blog-content">
                  <h3 className="blog-title">{blog.title}</h3>
                  <p className="blog-excerpt">{blog.excerpt}</p>
                  <Link to={`/blog/${blog.id}`} className="blog-read-more">
                    Đọc Thêm
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ Section */}
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

      {/* Features Section */}
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