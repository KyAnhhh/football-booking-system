import { useState } from "react";
import swal from "sweetalert";
import "../../assets/styles/Contact.css";
import { Clock, Mail, MapPin, MessageCircle, Phone } from "lucide-react";

export default function Contact() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    message: "",
  });

  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    swal({
      text: "Cảm ơn bạn đã liên hệ. Chúng tôi sẽ phản hồi sớm nhất.",
      icon: "success",
    });
    setFormData({ name: "", email: "", message: "" });
  };

  return (
    <div className="contact-core-page">
      <section className="contact-core-hero">
        <div>
          <p>Liên hệ</p>
          <h1>Chúng tôi luôn sẵn sàng hỗ trợ</h1>
          <span>
            Gửi câu hỏi về sản phẩm, đơn hàng hoặc đặt sân. Đội ngũ hỗ trợ sẽ
            phản hồi trong thời gian sớm nhất.
          </span>
        </div>
      </section>

      <section className="contact-core-shell">
        <div className="contact-core-info">
          <div className="contact-core-card-title">
            <MessageCircle size={22} />
            <div>
              <h2>Thông tin liên hệ</h2>
              <p>Kết nối nhanh với Shop Bóng Đá</p>
            </div>
          </div>

          <div className="contact-core-details">
            <article>
              <MapPin size={20} />
              <div>
                <h3>Địa chỉ</h3>
                <p>123 Đường Bóng Đá, Thành phố Thể Thao, TP.HCM</p>
              </div>
            </article>
            <article>
              <Phone size={20} />
              <div>
                <h3>Điện thoại</h3>
                <p>+84 123 456 789</p>
              </div>
            </article>
            <article>
              <Mail size={20} />
              <div>
                <h3>Email</h3>
                <p>contact@shopbongda.com</p>
              </div>
            </article>
            <article>
              <Clock size={20} />
              <div>
                <h3>Giờ làm việc</h3>
                <p>Thứ 2 - Chủ nhật: 8:00 - 22:00</p>
              </div>
            </article>
          </div>
        </div>

        <form className="contact-core-form" onSubmit={handleSubmit}>
          <div className="contact-core-card-title">
            <Mail size={22} />
            <div>
              <h2>Gửi tin nhắn</h2>
              <p>Chúng tôi sẽ phản hồi trong vòng 24 giờ.</p>
            </div>
          </div>

          <label>
            <span>Họ và tên</span>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Nhập họ và tên"
              required
            />
          </label>

          <label>
            <span>Email</span>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Nhập email"
              required
            />
          </label>

          <label>
            <span>Tin nhắn</span>
            <textarea
              name="message"
              rows="5"
              value={formData.message}
              onChange={handleChange}
              placeholder="Bạn cần hỗ trợ gì?"
              required
            />
          </label>

          <button type="submit">Gửi tin nhắn</button>
        </form>
      </section>

      <section className="contact-core-map">
        <div>
          <MapPin size={28} />
          <h2>Tìm chúng tôi</h2>
          <p>Vị trí cửa hàng sẽ được tích hợp bản đồ khi bạn cấu hình Google Maps.</p>
          <button type="button">Mở bản đồ</button>
        </div>
      </section>
    </div>
  );
}
