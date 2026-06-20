import { Link } from "react-router-dom";
import "../../assets/styles/Collection.css";
import { ArrowRight } from "lucide-react";

const collections = [
  {
    id: 1,
    title: "Áo câu lạc bộ châu Âu",
    desc: "Manchester United, Real Madrid, Barcelona, PSG và nhiều đội bóng lớn.",
    image: "https://images.unsplash.com/photo-1605733160314-4fc7dac4bb16?auto=format&fit=crop&w=1200&q=80",
  },
  {
    id: 2,
    title: "Áo đội tuyển quốc gia",
    desc: "Việt Nam, Brazil, Argentina, Pháp, Đức và các mẫu áo thi đấu nổi bật.",
    image: "https://images.unsplash.com/photo-1518091043644-c1d4457512c6?auto=format&fit=crop&w=1200&q=80",
  },
  {
    id: 3,
    title: "Giày bóng đá",
    desc: "Nike Mercurial, Adidas Predator, Puma Future và nhiều dòng giày sân cỏ.",
    image: "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=1200&q=80",
  },
  {
    id: 4,
    title: "Phụ kiện bóng đá",
    desc: "Găng tay, balo, băng đội trưởng và phụ kiện tập luyện hằng ngày.",
    image: "https://images.unsplash.com/photo-1521412644187-c49fa049e84d?auto=format&fit=crop&w=1200&q=80",
  },
];

export default function Collection() {
  return (
    <div className="collection-core-page">
      <section className="collection-core-hero">
        <div>
          <p>Bộ sưu tập</p>
          <h1>Chọn nhanh theo nhu cầu thi đấu</h1>
          <span>
            Các nhóm sản phẩm được sắp xếp theo thói quen mua sắm của người chơi
            bóng đá.
          </span>
        </div>
      </section>

      <section className="collection-core-grid">
        {collections.map((item) => (
          <article className="collection-core-card" key={item.id}>
            <img src={item.image} alt={item.title} />
            <div>
              <h3>{item.title}</h3>
              <p>{item.desc}</p>
              <Link to="/products">
                Xem sản phẩm
                <ArrowRight size={17} />
              </Link>
            </div>
          </article>
        ))}
      </section>
    </div>
  );
}
