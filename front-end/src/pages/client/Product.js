import { useEffect, useState } from "react";
import { Search } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { getProducts } from "../../services/productService";
import "../../assets/styles/Product.css";

export default function Product() {
  const [products, setProducts] = useState([]);
  const [pageNo, setPageNo] = useState(0);
  const [search, setSearch] = useState("");
  const [totalPages, setTotalPages] = useState(0);

  const navigate = useNavigate();

  useEffect(() => {
    getProducts(pageNo, 10, "id", "asc", search).then((res) => {
      console.log("API response:", res);
    setProducts(res.content || []);
    setTotalPages(res.page?.totalPages || 0);
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

                <div className="price-wrapper">
                  {p.discount && (
                    <span className="price-old">
                      {minPrice?.toLocaleString()}đ
                    </span>
                  )}

                  <span className="price-new">
                    {minPrice
                      ? (p.discount
                          ? (minPrice - (minPrice * p.discount) / 100)
                          : minPrice
                        ).toLocaleString() + "đ"
                      : "Liên hệ"}
                  </span>
                </div>
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
      <div className="pagination">
        <button
          disabled={pageNo === 0}
          onClick={() => setPageNo((prev) => prev - 1)}
        >
          ← Trước
        </button>

        <span>
          Trang {pageNo + 1} / {totalPages}
        </span>

        <button
          disabled={pageNo + 1 >= totalPages}
          onClick={() => setPageNo((prev) => prev + 1)}
        >
          Sau →
        </button>
      </div>
    </div>
  );
}
