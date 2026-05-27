import "../../assets/styles/Service.css";

const services = [
  {
    icon: "⚽",
    title: "In Tên & Số Áo",
    desc: "In tên cầu thủ, số áo theo yêu cầu chuẩn form đội bóng.",
  },
  {
    icon: "🚚",
    title: "Giao Hàng Nhanh",
    desc: "Giao hàng toàn quốc, hỗ trợ COD tiện lợi.",
  },
  {
    icon: "🛡️",
    title: "Hàng Chính Hãng",
    desc: "Cam kết sản phẩm chính hãng, hoàn tiền nếu phát hiện giả.",
  },
  {
    icon: "🔄",
    title: "Đổi Trả Linh Hoạt",
    desc: "Hỗ trợ đổi trả trong vòng 7 ngày.",
  },
];

export default function Service() {
  return (
    <div className="service-page">
      <div className="service-hero">
        <h1>Dịch Vụ Của Chúng Tôi</h1>
        <p>Luôn đồng hành cùng đam mê bóng đá của bạn</p>
      </div>

      <div className="service-grid">
        {services.map((s, index) => (
          <div className="service-card" key={index}>
            <div className="service-icon">{s.icon}</div>
            <h3>{s.title}</h3>
            <p>{s.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
