import React, { useState, useEffect } from "react"; // Thêm useEffect
import swal from 'sweetalert';
import "../../assets/styles/Contact.css";

export default function Contact() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    message: ''
  });

  // Thêm Intersection Observer
  useEffect(() => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('animate-in');
        }
      });
    }, { threshold: 0.1 });

    const animatedElements = document.querySelectorAll('.animate-on-scroll');
    animatedElements.forEach(el => observer.observe(el));

    return () => {
      animatedElements.forEach(el => observer.unobserve(el));
    };
  }, []);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    swal({ text: 'Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất.', icon: 'success' });
    setFormData({ name: '', email: '', message: '' });
  };

 return (
    <div className="contact-container">
      {/* Hero Header */}
      <section className="contact-hero">
        <div className="hero-bg-animation">
          <div className="floating-ball ball-contact-1"></div>
          <div className="floating-ball ball-contact-2"></div>
        </div>
        <div className="hero-content">
          <h1 className="hero-title">Liên Hệ Với Chúng Tôi</h1>
          <p className="hero-subtitle">Chúng tôi luôn sẵn sàng hỗ trợ bạn</p>
        </div>
      </section>

      {/* Main Contact Section */}
      <section className="contact-section animate-on-scroll">
        <div className="container">
          <div className="contact-grid">
            {/* Contact Info */}
            <div className="contact-info-card">
              <div className="contact-header">
                <div className="contact-icon">📍</div>
                <h2>Thông Tin Liên Hệ</h2>
              </div>
              
              <div className="contact-details">
                <div className="contact-item">
                  <span className="contact-icon-small">📍</span>
                  <div>
                    <h4>Địa Chỉ</h4>
                    <p>123 Đường Bóng Đá, Thành Phố Thể Thao, TP.HCM</p>
                  </div>
                </div>
                
                <div className="contact-item">
                  <span className="contact-icon-small">📞</span>
                  <div>
                    <h4>Điện Thoại</h4>
                    <p>+84 123 456 789</p>
                  </div>
                </div>
                
                <div className="contact-item">
                  <span className="contact-icon-small">✉️</span>
                  <div>
                    <h4>Email</h4>
                    <p>contact@shopbongda.com</p>
                  </div>
                </div>
                
                <div className="contact-item">
                  <span className="contact-icon-small">🕒</span>
                  <div>
                    <h4>Giờ Làm Việc</h4>
                    <p>Thứ 2 - CN: 8:00 - 22:00</p>
                  </div>
                </div>
              </div>

              <div className="contact-buttons">
                <button className="whatsapp-btn">
                  <span>💬</span> WhatsApp
                </button>
                <button className="zalo-btn">
                  <span>📱</span> Zalo
                </button>
              </div>
            </div>

            {/* Contact Form */}
            <div className="contact-form-card">
              <div className="form-header">
                <div className="form-icon">✉️</div>
                <h3>Gửi Tin Nhắn Cho Chúng Tôi</h3>
                <p>Chúng tôi sẽ phản hồi trong vòng 24 giờ</p>
              </div>

              <form onSubmit={handleSubmit} className="contact-form">
                <div className="form-group">
                  <input
                    type="text"
                    name="name"
                    placeholder="Họ và tên của bạn"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="form-input"
                  />
                  <label className="floating-label">Họ và tên</label>
                </div>

                <div className="form-group">
                  <input
                    type="email"
                    name="email"
                    placeholder="Email của bạn"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    className="form-input"
                  />
                  <label className="floating-label">Email</label>
                </div>

                <div className="form-group">
                  <textarea
                    name="message"
                    rows="5"
                    placeholder="Tin nhắn của bạn"
                    value={formData.message}
                    onChange={handleChange}
                    required
                    className="form-textarea"
                  ></textarea>
                  <label className="floating-label">Tin nhắn</label>
                </div>

                <button type="submit" className="submit-btn">
                  <span>Gửi Tin Nhắn</span>
                  <div className="btn-loader"></div>
                </button>
              </form>
            </div>
          </div>
        </div>
      </section>

      {/* Map Section */}
      <section className="map-section animate-on-scroll">
        <div className="container">
          <h2 className="section-title">Tìm Chúng Tôi</h2>
          <div className="map-container">
            <div className="map-placeholder">
              <div className="map-icon">🗺️</div>
              <p>Nhấp để xem vị trí trên Google Maps</p>
              <button className="map-btn">Mở Bản Đồ</button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}