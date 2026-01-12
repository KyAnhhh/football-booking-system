// src/pages/product/Product.jsx
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
        {products.map((p) => (
          <div key={p.id} className="product-card">
            <img
              src={p.imageUrls?.[0] || "/no-image.png"}
              alt={p.name}
            />

            <h4>{p.name}</h4>

            <p className="price">
              {Math.min(...p.variants.map(v => v.price)).toLocaleString()} đ
            </p>

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
                onClick={() => navigate(`/products/product-details/${p.id}`)}
              >
                Mua ngay
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
