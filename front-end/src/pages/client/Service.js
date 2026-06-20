import { useNavigate } from "react-router-dom";
import "../../assets/styles/Service.css";
import { BadgeCheck, PackageCheck, RotateCcw, Shirt, Truck } from "lucide-react";

const services = [
  {
    icon: Shirt,
    title: "In tên và số áo",
    desc: "Tùy chỉnh tên, số áo theo form đội với chất liệu bền màu và sắc nét.",
  },
  {
    icon: Truck,
    title: "Giao nhanh toàn quốc",
    desc: "Theo dõi đơn hàng, hỗ trợ COD và nhiều lựa chọn vận chuyển phù hợp.",
  },
  {
    icon: BadgeCheck,
    title: "Sản phẩm chính hãng",
    desc: "Kiểm tra nguồn gốc, chất lượng và thông tin sản phẩm trước khi bán.",
  },
  {
    icon: RotateCcw,
    title: "Đổi trả linh hoạt",
    desc: "Hỗ trợ đổi trả theo chính sách rõ ràng để bạn yên tâm mua sắm.",
  },
];

export default function Service() {
  const navigate = useNavigate();

  return (
    <div className="service-core-page">
      <section className="service-core-hero">
        <div>
          <p>Dịch vụ</p>
          <h1>Hỗ trợ người chơi từ sản phẩm đến trận đấu</h1>
          <span>
            Các dịch vụ được thiết kế để việc mua sắm, giao hàng và chuẩn bị
            thi đấu trở nên nhẹ nhàng hơn.
          </span>
          <div className="service-core-actions">
            <button type="button" onClick={() => navigate("/products")}>
              Khám phá sản phẩm
            </button>
            <button
              type="button"
              className="secondary"
              onClick={() => navigate("/contact")}
            >
              Liên hệ hỗ trợ
            </button>
          </div>
        </div>
      </section>

      <section className="service-core-grid">
        {services.map((service) => {
          const Icon = service.icon;
          return (
            <article className="service-core-card" key={service.title}>
              <div className="service-core-icon">
                <Icon size={26} />
              </div>
              <h3>{service.title}</h3>
              <p>{service.desc}</p>
            </article>
          );
        })}
      </section>

      <section className="service-core-note">
        <PackageCheck size={28} />
        <div>
          <h2>Cần hỗ trợ theo đội bóng?</h2>
          <p>
            Bạn có thể liên hệ shop để được tư vấn size, số lượng, in áo và lịch
            giao phù hợp trước ngày thi đấu.
          </p>
        </div>
        <button type="button" onClick={() => navigate("/contact")}>
          Gửi yêu cầu
        </button>
      </section>
    </div>
  );
}
