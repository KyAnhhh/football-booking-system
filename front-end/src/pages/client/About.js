import "../../assets/styles/About.css";
import { BadgeCheck, CalendarDays, Goal, UsersRound } from "lucide-react";

const timeline = [
  {
    year: "2015",
    title: "Bắt đầu từ niềm đam mê",
    description: "Shop được xây dựng bởi những người chơi bóng và yêu bóng đá.",
  },
  {
    year: "2017",
    title: "Cửa hàng đầu tiên",
    description: "Mở điểm bán đầu tiên và tập trung vào sản phẩm bóng đá chính hãng.",
  },
  {
    year: "2020",
    title: "Mở rộng online",
    description: "Ra mắt kênh bán hàng trực tuyến và hỗ trợ giao hàng toàn quốc.",
  },
  {
    year: "2025",
    title: "Kết hợp đặt sân",
    description: "Bổ sung hệ thống đặt sân, voucher, extra và quản lý lịch chơi.",
  },
];

export default function About() {
  return (
    <div className="about-core-page">
      <section className="about-core-hero">
        <div>
          <p>Về chúng tôi</p>
          <h1>Shop bóng đá cho người thật sự ra sân</h1>
          <span>
            Chúng tôi kết hợp cửa hàng sản phẩm bóng đá và hệ thống đặt sân để
            mỗi trận đấu được chuẩn bị gọn gàng hơn.
          </span>
        </div>
      </section>

      <section className="about-core-section about-core-story">
        <div>
          <p className="about-core-eyebrow">Câu chuyện</p>
          <h2>Từ cửa hàng nhỏ đến trải nghiệm bóng đá trọn gói</h2>
          <p>
            Shop Bóng Đá bắt đầu từ nhu cầu rất đời thường: tìm được đôi giày,
            áo đấu, phụ kiện phù hợp và có một nơi đặt sân rõ lịch, rõ giá.
            Vì vậy hệ thống được xây để phục vụ cả mua sắm lẫn đặt sân.
          </p>
          <p>
            Mục tiêu của chúng tôi là giúp người chơi tiết kiệm thời gian chuẩn
            bị, tránh nhầm lịch, tránh mập mờ giá và có thể theo dõi mọi thứ
            trong cùng tài khoản.
          </p>
        </div>

        <div className="about-core-image">
          <img
            src="https://images.pexels.com/photos/6077805/pexels-photo-6077805.jpeg"
            alt="Cửa hàng bóng đá"
          />
        </div>
      </section>

      <section className="about-core-band">
        <div className="about-core-stats">
          <article>
            <BadgeCheck size={26} />
            <strong>1000+</strong>
            <span>Sản phẩm đã quản lý</span>
          </article>
          <article>
            <UsersRound size={26} />
            <strong>50.000+</strong>
            <span>Khách hàng phục vụ</span>
          </article>
          <article>
            <Goal size={26} />
            <strong>5+</strong>
            <span>Năm kinh nghiệm</span>
          </article>
          <article>
            <CalendarDays size={26} />
            <strong>Online</strong>
            <span>Đặt sân và theo dõi lịch</span>
          </article>
        </div>
      </section>

      <section className="about-core-section">
        <div className="about-core-section-header">
          <p className="about-core-eyebrow">Hành trình</p>
          <h2>Các mốc phát triển</h2>
        </div>
        <div className="about-core-timeline">
          {timeline.map((item) => (
            <article key={item.year}>
              <span>{item.year}</span>
              <h3>{item.title}</h3>
              <p>{item.description}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="about-core-section">
        <div className="about-core-section-header">
          <p className="about-core-eyebrow">Đội ngũ</p>
          <h2>Những người đứng sau hệ thống</h2>
        </div>
        <div className="about-core-team">
          {[
            ["Nguyễn Văn A", "Người sáng lập", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=500"],
            ["Trần Thị B", "Quản lý cửa hàng", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=500"],
            ["Lê Văn C", "Tư vấn sản phẩm", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500"],
          ].map(([name, role, image]) => (
            <article key={name}>
              <img src={image} alt={name} />
              <h3>{name}</h3>
              <p>{role}</p>
            </article>
          ))}
        </div>
      </section>
    </div>
  );
}
