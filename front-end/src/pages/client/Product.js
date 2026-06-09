import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getProducts } from "../../services/productService";
import { getCategories } from "../../services/categoryService";
import { getBrands } from "../../services/brandService";

import "../../assets/styles/Product.css";

export default function Product() {
  const [products, setProducts] = useState([]);
  const [pageNo, setPageNo] = useState(0);
  const [search, setSearch] = useState("");
  const [totalPages, setTotalPages] = useState(0);

  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);

  const [selectedCategory, setSelectedCategory] = useState("");
  const [selectedBrand, setSelectedBrand] = useState("");
  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);

  const [selectedPriceRange, setSelectedPriceRange] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    getCategories(0, 100).then((res) => {
      setCategories(res.content || []);
    });

    getBrands(0, 100).then((res) => {
      setBrands(res.content || []);
    });
  }, []);

  useEffect(() => {
    getProducts(
      pageNo,
      10,
      "id",
      "asc",
      search,
      selectedCategory,
      selectedBrand,
      minPrice,
      maxPrice
    ).then((res) => {
      setProducts(res.content || []);
      setTotalPages(res.page?.totalPages || 0);
    });
  }, [pageNo, search, selectedCategory, selectedBrand, minPrice, maxPrice]);

  const checkLogin = (callback) => {
    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      navigate("/login");
      return;
    }
    callback();
  };

  return (
    <div className="client-products-layout">

      {/* SIDEBAR */}
      <div className="filter-sidebar">
        <h3>Bộ lọc sản phẩm</h3>

        <input
          className="search-input"
          placeholder="Tìm sản phẩm..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPageNo(0);
          }}
        />

        <div className="filter-group">

  <h4>Giá</h4>

  <div className="filter-buttons">

    <button
      className={selectedPriceRange === "" ? "active" : ""}
      onClick={() => {
        setSelectedPriceRange("");
        setMinPrice("");
        setMaxPrice("");
        setPageNo(0);
      }}
    >
      Tất cả
    </button>

    <button
      className={
        selectedPriceRange === "under200"
          ? "active"
          : ""
      }
      onClick={() => {
        setSelectedPriceRange("under200");
        setMinPrice("");
        setMaxPrice(200000);
        setPageNo(0);
      }}
    >
      Dưới 200k
    </button>

    <button
      className={
        selectedPriceRange === "200to500"
          ? "active"
          : ""
      }
      onClick={() => {
        setSelectedPriceRange("200to500");
        setMinPrice(200000);
        setMaxPrice(500000);
        setPageNo(0);
      }}
    >
      200k - 500k
    </button>

    <button
      className={
        selectedPriceRange === "500to1000"
          ? "active"
          : ""
      }
      onClick={() => {
        setSelectedPriceRange("500to1000");
        setMinPrice(500000);
        setMaxPrice(1000000);
        setPageNo(0);
      }}
    >
      500k - 1tr
    </button>

    <button
      className={
        selectedPriceRange === "1000to2000"
          ? "active"
          : ""
      }
      onClick={() => {
        setSelectedPriceRange("1000to2000");
        setMinPrice(1000000);
        setMaxPrice(2000000);
        setPageNo(0);
      }}
    >
      1tr - 2tr
    </button>

    <button
      className={
        selectedPriceRange === "over2000"
          ? "active"
          : ""
      }
      onClick={() => {
        setSelectedPriceRange("over2000");
        setMinPrice(2000000);
        setMaxPrice("");
        setPageNo(0);
      }}
    >
      Trên 2tr
    </button>

  </div>

</div>

        <div className="filter-group">

  <h4>Danh mục</h4>

  <div className="filter-buttons">

    <button
      className={selectedCategory === "" ? "active" : ""}
      onClick={() => {
        setSelectedCategory("");
        setPageNo(0);
      }}
    >
      Tất cả
    </button>

    {categories.map((c) => (

      <button
        key={c.id}
        className={
          selectedCategory === c.name
            ? "active"
            : ""
        }
        onClick={() => {
          setSelectedCategory(c.name);
          setPageNo(0);
        }}
      >
        {c.name}
      </button>

    ))}

  </div>

</div>

        <div className="filter-group">

  <h4>Thương hiệu</h4>

  <div className="filter-buttons">

    <button
      className={selectedBrand === "" ? "active" : ""}
      onClick={() => {
        setSelectedBrand("");
        setPageNo(0);
      }}
    >
      Tất cả
    </button>

    {brands.map((b) => (

      <button
        key={b.id}
        className={
          selectedBrand === b.name
            ? "active"
            : ""
        }
        onClick={() => {
          setSelectedBrand(b.name);
          setPageNo(0);
        }}
      >
        {b.name}
      </button>

    ))}

  </div>

</div>
      </div>

      {/* PRODUCT CONTENT */}
      <div className="product-content">

        <h2 className="title">⚽ Shop Đồ Bóng Đá</h2>

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
                    <span className="discount-badge">
                      -{p.discount}%
                    </span>
                  )}
                </div>

                <div className="product-info">
                  <h4 className="product-name">{p.name}</h4>

                  <div className="price-wrapper">
                    {p.discount > 0 && (
                      <span className="price-old">
                        {minPrice?.toLocaleString()}đ
                      </span>
                    )}

                    <span className="price-new">
                      {minPrice
                        ? (
                            p.discount > 0
                              ? minPrice -
                                (minPrice * p.discount) / 100
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
                      checkLogin(() => {
                        navigate(`/products/product-details/${p.id}`);
                      }
                      )
                    }}
                  >
                    Xem chi tiết
                  </button>

                  <button
                    className="buy-btn"
                    onClick={(e) => {
                      e.stopPropagation();

                      checkLogin(() => {
                        navigate(`/products/product-details/${p.id}`);
                      })
                    }}
                  >
                    Mua ngay
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        {/* PAGINATION */}
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
    </div>
  );
}