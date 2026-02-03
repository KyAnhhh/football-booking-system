import "../../assets/styles/Collection.css";

const collections = [
  {
    id: 1,
    title: "Áo CLB Châu Âu",
    desc: "Manchester United, Real Madrid, Barcelona, PSG...",
    image:
      "https://images.unsplash.com/photo-1605733160314-4fc7dac4bb16",
  },
  {
    id: 2,
    title: "Áo Đội Tuyển Quốc Gia",
    desc: "Việt Nam, Brazil, Argentina, Pháp, Đức...",
    image:
      "https://images.unsplash.com/photo-1518091043644-c1d4457512c6",
  },
  {
    id: 3,
    title: "Giày Bóng Đá",
    desc: "Nike Mercurial, Adidas Predator, Puma Future...",
    image:
      "https://images.unsplash.com/photo-1517649763962-0c623066013b",
  },
 {
  id: 4,
  title: "Phụ Kiện Bóng Đá",
  desc: "Găng tay, băng đội trưởng, balo thể thao...",
  image:
    "https://images.unsplash.com/photo-1521412644187-c49fa049e84d?auto=format&fit=crop&w=1200&q=80",
  },
];

export default function Collection() {
  return (
    <div className="collection-page">
      <div className="collection-hero">
        <h1>Bộ Sưu Tập</h1>
        <p>Khám phá thế giới bóng đá cùng Football Shop</p>
      </div>

      <div className="collection-grid">
        {collections.map((item) => (
          <div className="collection-card" key={item.id}>
            <img src={item.image} alt={item.title} />
            <div className="collection-content">
              <h3>{item.title}</h3>
              <p>{item.desc}</p>
              <a href="/products" className="collection-btn">
                Xem sản phẩm →
              </a>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
