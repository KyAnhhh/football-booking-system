import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { getProductById } from "../../services/productService";
import { addItemToCart } from "../../services/cartService";
import swal from "sweetalert";
import "../../assets/styles/ProductDetail.css";
import ReviewSection from "../../components/review/ReviewSection";
import { ChevronLeft, Minus, Plus, ShoppingBag, Star } from "lucide-react";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [mainImage, setMainImage] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadProduct = async () => {
      try {
        setLoading(true);
        const response = await getProductById(id);
        setProduct(response);
        setMainImage(response?.imageUrls?.[0] || "");
        setSelectedVariant(null);
        setQuantity(1);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    loadProduct();
  }, [id]);

  const priceInfo = useMemo(() => {
    const variants = product?.variants || [];
    if (!variants.length) {
      return {
        minPrice: 0,
        maxPrice: 0,
        discount: 0,
        finalPrice: 0,
        displayPrice: "Lien he",
      };
    }

    const prices = variants.map((variant) => Number(variant.price || 0));
    const minPrice = Math.min(...prices);
    const maxPrice = Math.max(...prices);
    const discount = Number(product?.discount || 0);
    const basePrice = Number(selectedVariant?.price || minPrice);
    const finalPrice = basePrice - (basePrice * discount) / 100;

    return {
      minPrice,
      maxPrice,
      discount,
      finalPrice,
      displayPrice:
        minPrice === maxPrice
          ? formatCurrency(minPrice)
          : `${formatCurrency(minPrice)} - ${formatCurrency(maxPrice)}`,
    };
  }, [product, selectedVariant]);

  const selectedHasStockField =
    selectedVariant?.stock !== undefined ||
    selectedVariant?.stockQuantity !== undefined ||
    selectedVariant?.quantity !== undefined;
  const selectedStock = Number(
    selectedVariant?.stock ?? selectedVariant?.stockQuantity ?? selectedVariant?.quantity ?? 0
  );
  const canBuy = Boolean(selectedVariant) && (!selectedHasStockField || selectedStock > 0);

  const changeQuantity = (nextValue) => {
    const max = selectedHasStockField && selectedStock > 0 ? selectedStock : 99;
    const safeValue = Math.max(1, Math.min(max, nextValue));
    setQuantity(safeValue);
  };

  const addToCartHandler = async (buyNow = false) => {
    if (!selectedVariant) {
      swal("Chọn phân loại", "Vui lòng chọn size trước khi tiếp tục.", "warning");
      return;
    }

    if (!canBuy) {
      swal("Hết hàng", "Biến thể này hiện không còn hàng.", "warning");
      return;
    }

    try {
      if (buyNow) {
        navigate("/cart/checkout", {
          state: {
            mode: "BUY_NOW",
            product: {
              productId: product.id,
              productName: product.name,
              thumbnail: mainImage || product.imageUrls?.[0],
              productVariantId: selectedVariant.id,
              sizeName: selectedVariant.sizeName || selectedVariant.sizeCode,
              priceAtAddedTime: selectedVariant.price,
              totalQuantity: quantity,
              discount: product.discount,
            },
          },
        });
        return;
      }

      await addItemToCart({
        productVariantId: selectedVariant.id,
        sizeId: selectedVariant.sizeId,
        quantity,
      });

      swal("Đã thêm vào giỏ hàng", "Sản phẩm đã được thêm thành công.", "success");
    } catch (error) {
      swal("Không thể thêm sản phẩm", error.message, "error");
    }
  };

  if (loading) {
    return <div className="product-detail-state">Đang tải sản phẩm...</div>;
  }

  if (!product) {
    return <div className="product-detail-state">Không tìm thấy sản phẩm.</div>;
  }

  const images = product.imageUrls?.length
    ? product.imageUrls
    : ["https://placehold.co/900x900/f1f5f9/0f172a?text=Product"];

  return (
    <div className="product-detail-page">
      <button
        type="button"
        className="product-detail-back"
        onClick={() => navigate("/products")}
      >
        <ChevronLeft size={18} />
        Quay lai san pham
      </button>

      <section className="product-detail-shell">
        <div className="product-detail-gallery">
          <div className="product-detail-main-image">
            <img src={mainImage || images[0]} alt={product.name} />
          </div>

          <div className="product-detail-thumbs">
            {images.map((image, index) => (
              <button
                type="button"
                key={`${image}-${index}`}
                className={(mainImage || images[0]) === image ? "active" : ""}
                onClick={() => setMainImage(image)}
              >
                <img src={image} alt={`${product.name} ${index + 1}`} />
              </button>
            ))}
          </div>
        </div>

        <div className="product-detail-info">
          <div className="product-detail-rating">
            <Star size={17} fill="currentColor" />
            <span>Sản phẩm bóng đá</span>
          </div>

          <h1>{product.name}</h1>
          <p className="product-detail-description">
            {product.description || "Thong tin san pham dang duoc cap nhat."}
          </p>

          <div className="product-detail-price-panel">
            {selectedVariant && priceInfo.discount > 0 ? (
              <>
                <span className="product-detail-old-price">
                  {formatCurrency(selectedVariant.price)}
                </span>
                <strong>{formatCurrency(priceInfo.finalPrice)}</strong>
              </>
            ) : (
              <strong>{priceInfo.displayPrice}</strong>
            )}
            {priceInfo.discount > 0 && (
              <span className="product-detail-discount">
                Giam {priceInfo.discount}%
              </span>
            )}
          </div>

          <div className="product-detail-section">
            <div className="product-detail-section-title">Chọn size</div>
            <div className="product-detail-variants">
              {(product.variants || []).map((variant) => {
                const hasStockField =
                  variant.stock !== undefined ||
                  variant.stockQuantity !== undefined ||
                  variant.quantity !== undefined;
                const stock = Number(
                  variant.stock ?? variant.stockQuantity ?? variant.quantity ?? 0
                );
                const disabled = hasStockField && stock === 0;

                return (
                  <button
                    type="button"
                    key={variant.id}
                    disabled={disabled}
                    className={selectedVariant?.id === variant.id ? "active" : ""}
                    onClick={() => {
                      setSelectedVariant(variant);
                      setQuantity(1);
                    }}
                  >
                    <span>{variant.sizeName || variant.sizeCode}</span>
                    <small>
                      {disabled
                        ? "Hết hàng"
                        : hasStockField
                          ? `${stock} con`
                          : "Co san"}
                    </small>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="product-detail-section">
            <div className="product-detail-section-title">Số lượng</div>
            <div className="product-detail-quantity">
              <button type="button" onClick={() => changeQuantity(quantity - 1)}>
                <Minus size={16} />
              </button>
              <input
                type="number"
                min="1"
                value={quantity}
                onChange={(event) => changeQuantity(Number(event.target.value || 1))}
              />
              <button type="button" onClick={() => changeQuantity(quantity + 1)}>
                <Plus size={16} />
              </button>
            </div>
          </div>

          <div className="product-detail-actions">
            <button
              type="button"
              className="product-detail-cart-btn"
              onClick={() => addToCartHandler(false)}
            >
              <ShoppingBag size={19} />
              Thêm vào giỏ hàng
            </button>
            <button
              type="button"
              className="product-detail-buy-btn"
              onClick={() => addToCartHandler(true)}
            >
              Mua ngay
            </button>
          </div>
        </div>
      </section>

      <section className="product-detail-review">
        <ReviewSection productId={id} />
      </section>
    </div>
  );
}
