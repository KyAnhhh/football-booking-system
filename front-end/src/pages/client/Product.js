import { useEffect, useMemo, useState } from "react";
import { getProducts } from "../../services/productService";
import { getCategories } from "../../services/categoryService";
import { getBrands } from "../../services/brandService";
import "../../assets/styles/Product.css";
import { useNavigate } from "react-router-dom";
import {
  Eye,
  RotateCcw,
  Search,
  ShoppingBag,
  SlidersHorizontal,
  Tag,
} from "lucide-react";

const priceRanges = [
  { label: "Duoi 500k", min: 0, max: 500000 },
  { label: "500k - 1 trieu", min: 500000, max: 1000000 },
  { label: "Tren 1 trieu", min: 1000000, max: null },
];

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const getLowestVariant = (product) => {
  const variants = product?.variants || [];
  if (!variants.length) return null;

  return variants.reduce((lowest, current) =>
    Number(current.price || 0) < Number(lowest.price || 0) ? current : lowest
  );
};

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
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const loadFilters = async () => {
      try {
        const [categoryRes, brandRes] = await Promise.all([
          getCategories(),
          getBrands(),
        ]);
        setCategories(categoryRes?.content || categoryRes || []);
        setBrands(brandRes?.content || brandRes || []);
      } catch (error) {
        console.error(error);
      }
    };

    loadFilters();
  }, []);

  useEffect(() => {
    const loadProducts = async () => {
      try {
        setLoading(true);
        const response = await getProducts(
          pageNo,
          10,
          "id",
          "asc",
          search,
          selectedCategory,
          selectedBrand,
          minPrice,
          maxPrice
        );

        setProducts(response?.content || []);
        setTotalPages(response?.totalPages || 0);
      } catch (error) {
        console.error(error);
        setProducts([]);
        setTotalPages(0);
      } finally {
        setLoading(false);
      }
    };

    loadProducts();
  }, [pageNo, search, selectedCategory, selectedBrand, minPrice, maxPrice]);

  const activeFilterCount = useMemo(
    () =>
      [search, selectedCategory, selectedBrand, selectedPriceRange].filter(
        Boolean
      ).length,
    [search, selectedCategory, selectedBrand, selectedPriceRange]
  );

  const checkLogin = () => {
    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      navigate("/login");
      return false;
    }
    return true;
  };

  const handlePriceRangeChange = (rangeLabel) => {
    setSelectedPriceRange(rangeLabel);
    setPageNo(0);

    const range = priceRanges.find((item) => item.label === rangeLabel);
    if (!range) {
      setMinPrice(null);
      setMaxPrice(null);
      return;
    }

    setMinPrice(range.min);
    setMaxPrice(range.max);
  };

  const resetFilters = () => {
    setSearch("");
    setSelectedCategory("");
    setSelectedBrand("");
    setSelectedPriceRange("");
    setMinPrice(null);
    setMaxPrice(null);
    setPageNo(0);
  };

  return (
    <div className="shop-page">
      <section className="shop-hero">
        <div>
          <p className="shop-eyebrow">Football gear</p>
          <h1>Sản phẩm bóng đá</h1>
          <p>
            Tìm giày, áo đấu và phụ kiện với bộ lọc rõ ràng, giá hiển thị theo biến thể thấp nhất
          </p>
        </div>

        <div className="shop-hero-stat">
          <ShoppingBag size={22} />
          <strong>{products.length}</strong>
          <span>sản phẩm đang hiển thị</span>
        </div>
      </section>

      <div className="shop-layout">
        <aside className="shop-filter-panel">
          <div className="shop-filter-header">
            <div>
              <span>Bo loc</span>
              <strong>{activeFilterCount} đang áp dụng</strong>
            </div>
            <SlidersHorizontal size={22} />
          </div>

          <label className="shop-search-box">
            <Search size={18} />
            <input
              type="text"
              value={search}
              placeholder="Tìm tên sản phẩm..."
              onChange={(event) => {
                setSearch(event.target.value);
                setPageNo(0);
              }}
            />
          </label>

          <div className="shop-filter-group">
            <label>Danh mục</label>
            <select
              value={selectedCategory}
              onChange={(event) => {
                setSelectedCategory(event.target.value);
                setPageNo(0);
              }}
            >
              <option value="">Tất cả danh mục</option>
              {categories.map((category) => (
                <option key={category.id || category.name} value={category.name}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div className="shop-filter-group">
            <label>Thương hiệu</label>
            <select
              value={selectedBrand}
              onChange={(event) => {
                setSelectedBrand(event.target.value);
                setPageNo(0);
              }}
            >
              <option value="">Tất cả thương hiệu</option>
              {brands.map((brand) => (
                <option key={brand.id || brand.name} value={brand.name}>
                  {brand.name}
                </option>
              ))}
            </select>
          </div>

          <div className="shop-filter-group">
            <label>Khoảng giá</label>
            <div className="shop-price-options">
              <button
                type="button"
                className={!selectedPriceRange ? "active" : ""}
                onClick={() => handlePriceRangeChange("")}
              >
                Tat ca
              </button>
              {priceRanges.map((range) => (
                <button
                  type="button"
                  key={range.label}
                  className={selectedPriceRange === range.label ? "active" : ""}
                  onClick={() => handlePriceRangeChange(range.label)}
                >
                  {range.label}
                </button>
              ))}
            </div>
          </div>

          <button className="shop-reset-btn" type="button" onClick={resetFilters}>
            <RotateCcw size={17} />
            Đặt lại bộ lọc
          </button>
        </aside>

        <main className="shop-content">
          <div className="shop-content-header">
            <div>
              <p className="shop-eyebrow">Catalogue</p>
              <h2>Danh sách sản phẩm</h2>
            </div>
            <span>Trang {pageNo + 1} / {Math.max(totalPages, 1)}</span>
          </div>

          {loading ? (
            <div className="shop-state">Đang tải sản phẩm...</div>
          ) : products.length === 0 ? (
            <div className="shop-state">
              Không có sản phẩm phù hợp với bộ lọc hiện tại
            </div>
          ) : (
            <div className="shop-grid">
              {products.map((product) => {
                const lowestVariant = getLowestVariant(product);
                const price = Number(lowestVariant?.price || 0);
                const discount = Number(product.discount || 0);
                const finalPrice = price - (price * discount) / 100;
                const thumbnail =
                  product.imageUrls?.[0] ||
                  "https://placehold.co/600x600/f1f5f9/0f172a?text=Product";

                return (
                  <article className="shop-product-card" key={product.id}>
                    <button
                      type="button"
                      className="shop-product-media"
                      onClick={() =>
                        checkLogin() &&
                        navigate(`/products/product-details/${product.id}`)
                      }
                    >
                      {discount > 0 && (
                        <span className="shop-discount-badge">-{discount}%</span>
                      )}
                      <img src={thumbnail} alt={product.name} />
                    </button>

                    <div className="shop-product-info">
                      <div className="shop-product-meta">
                        <Tag size={15} />
                        <span>
                          {product.brandName ||
                            product.brand?.name ||
                            product.categoryName ||
                            "San pham"}
                        </span>
                      </div>
                      <h3>{product.name}</h3>

                      <div className="shop-price-row">
                        {discount > 0 && (
                          <span className="shop-old-price">
                            {formatCurrency(price)}
                          </span>
                        )}
                        <strong>{formatCurrency(finalPrice)}</strong>
                      </div>

                      <div className="shop-card-actions">
                        <button
                          type="button"
                          className="shop-secondary-btn"
                          onClick={() =>
                            checkLogin() && navigate(`/products/product-details/${product.id}`)
                          }
                        >
                          <Eye size={17} />
                          Chi tiết
                        </button>
                        <button
                          type="button"
                          className="shop-primary-btn"
                          onClick={() =>
                            checkLogin() && navigate(`/products/product-details/${product.id}`)
                          }
                        >
                          Mua ngay
                        </button>
                      </div>
                    </div>
                  </article>
                );
              })}
            </div>
          )}

          {totalPages > 1 && (
            <div className="shop-pagination">
              {Array.from({ length: totalPages }, (_, index) => (
                <button
                  type="button"
                  key={index}
                  className={pageNo === index ? "active" : ""}
                  onClick={() => setPageNo(index)}
                >
                  {index + 1}
                </button>
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
