import React, { useEffect, useRef } from "react";
import "../../assets/styles/Service.css";

const services = [
  {
    icon: (
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden>
        <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="1.2" />
        <path d="M4 4c4 4 8 4 16 0" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" />
      </svg>
    ),
    title: "In Tên & Số Áo",
    desc: "Tùy chỉnh tên, số theo form đội — bền màu, đẹp chuẩn.",
  },
  {
    icon: (
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden>
        <path d="M3 7h13l4 7v4H6L3 7z" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" />
        <circle cx="7" cy="20" r="1" fill="currentColor" />
        <circle cx="18" cy="20" r="1" fill="currentColor" />
      </svg>
    ),
    title: "Giao Nhanh & Toàn Quốc",
    desc: "Theo dõi đơn, nhiều lựa chọn vận chuyển và COD hỗ trợ.",
  },
  {
    icon: (
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden>
        <rect x="3" y="3" width="18" height="12" rx="2" stroke="currentColor" strokeWidth="1.2" />
        <path d="M7 15v3" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" />
        <path d="M17 15v3" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" />
      </svg>
    ),
    title: "Sản Phẩm Chính Hãng",
    desc: "Kiểm định chất lượng — chính hãng hoặc hoàn tiền.",
  },
  {
    icon: (
      <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden>
        <path d="M12 2v6" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" />
        <path d="M20 12a8 8 0 11-16 0" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    ),
    title: "Đổi Trả Linh Hoạt",
    desc: "Hỗ trợ đổi trả trong 7 ngày với điều kiện rõ ràng.",
  },
];

export default function Service() {
  const containerRef = useRef(null);

  useEffect(() => {
    const el = containerRef.current;
    if (!el || typeof IntersectionObserver === "undefined") return;

    const items = el.querySelectorAll(".service-card");
    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("reveal");
            io.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.12 }
    );

    items.forEach((it) => io.observe(it));

    return () => io.disconnect();
  }, []);

  return (
    <section className="service-page modern" aria-labelledby="services-heading">
      <div className="service-hero modern-hero">
        <div className="hero-inner">
          <h1 id="services-heading">Dịch Vụ Của Chúng Tôi</h1>
          <p className="lead">Chất lượng — Nhanh chóng — Tận tâm. Đồng hành cùng đam mê bóng đá của bạn.</p>
          <div className="hero-ctas">
            <button className="btn primary" onClick={() => (window.location.href = "/products")}>
              Khám Phá Sản Phẩm
            </button>
            <button className="btn ghost" onClick={() => (window.location.href = "/contact")}>
              Liên Hệ Hỗ Trợ
            </button>
          </div>
        </div>
      </div>

      <div className="service-grid modern-grid" ref={containerRef}>
        {services.map((s, index) => (
          <article
            className="service-card"
            key={s.title}
            style={{ transitionDelay: `${index * 120}ms` }}
            tabIndex={0}
            aria-label={s.title}
          >
            <div className="service-icon">{s.icon}</div>
            <h3 className="service-title">{s.title}</h3>
            <p className="service-desc">{s.desc}</p>
            <div className="card-footer">
              <button className="btn small">Tìm hiểu</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
