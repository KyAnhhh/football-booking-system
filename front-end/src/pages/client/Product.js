import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getProducts } from "../../services/productService";
import "../../assets/styles/Product.css";

export default function Product() {
  const [products, setProducts] = useState([]);
  const [pageNo, setPageNo] = useState(0);
  const [search, setSearch] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    getProducts(pageNo, 8, "id", "asc", search).then((res) => {
      setProducts(res.content || []);
    });
  }, [pageNo, search]);

  return (
    <div className="client-products">
      <h2 className="title">⚽ Shop Đồ Bóng Đá</h2>

      <input
        className="search-input"
        placeholder="Tìm sản phẩm..."
        value={search}
        onChange={(e) => {
          setSearch(e.target.value);
          setPageNo(0);
        }}
      />

      <div className="product-grid">
        {products.map((p) => {
          const minPrice = p.variants?.length
            ? Math.min(...p.variants.map((v) => v.price))
            : null;

          return (
            <div key={p.id} className="product-card">
              <div className="product-image">
                <img
                  src={p.imageUrls?.[0] || "/no-image.png"}
                  alt={p.name}
                />

                {p.discount > 0 && (
                  <span className="discount-badge">-{p.discount}%</span>
                )}
              </div>

              <div className="product-info">
                <h4 className="product-name">{p.name}</h4>

                <p className="price">
                  {minPrice ? minPrice.toLocaleString() + " đ" : "Liên hệ"}
                </p>
              </div>

              <div className="product-actions">
                <button
                  className="detail-btn"
                  onClick={(e) => {
                    e.stopPropagation();
                    navigate(`/products/product-details/${p.id}`);
                  }}
                >
                  Xem chi tiết
                </button>

                <button
                  className="buy-btn"
                  onClick={() =>
                    navigate(`/products/product-details/${p.id}`)
                  }
                >
                  Mua ngay
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
