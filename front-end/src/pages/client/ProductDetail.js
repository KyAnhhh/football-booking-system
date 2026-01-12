import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { getProductById } from "../../services/productService";
import { addItemToCart } from "../../services/cartService";
import Swal from "sweetalert2";
import "../../assets/styles/ProductDetail.css";

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [mainImage, setMainImage] = useState("");

  useEffect(() => {
    getProductById(id).then((res) => {
      setProduct(res);
      setMainImage(res.imageUrls?.[0]);
    });
  }, [id]);

  if (!product) return null;

  const addToCart = async (buyNow = false) => {
    if (!selectedVariant) {
      return Swal.fire("Thiếu size", "Vui lòng chọn size", "warning");
    }

    if (buyNow) {
      navigate("/cart/checkout", {
        state: {
          mode: "BUY_NOW",
          product: {
            productId: product.id,
            productName: product.name,
            productThumbnail: product.imageUrls?.[0],
            productVariantId: selectedVariant.id,
            sizeName: selectedVariant.sizeCode,
            priceAtAddedTime: selectedVariant.price,
            totalQuantity: quantity,
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

    Swal.fire("Thành công", "Đã thêm vào giỏ hàng", "success");
  };
  
  return (
    <div className="product-detail">
      <div className="gallery">
        <img src={mainImage} className="main-image" />

        <div className="thumbs">
          {product.imageUrls.map((img) => (
            <img
              key={img}
              src={img}
              onClick={() => setMainImage(img)}
            />
          ))}
        </div>
      </div>

      <div className="info">
        <h2>{product.name}</h2>

        <p className="price">
          {Math.min(...product.variants.map(v => v.price)).toLocaleString()} đ
        </p>

        <h4>Chọn size</h4>
        <div className="size-group">
          {product.variants.map((v) => (
            <button
              key={v.id}
              disabled={v.stock === 0}
              className={selectedVariant?.id === v.id ? "active" : ""}
              onClick={() => setSelectedVariant(v)}
            >
              {v.sizeCode}
            </button>
          ))}
        </div>

        <div className="quantity">
          <label>Số lượng</label>
          <input
            type="number"
            min={1}
            value={quantity}
            onChange={(e) => setQuantity(+e.target.value)}
          />
        </div>

        <div className="actions">
          <button onClick={() => addToCart(false)}>Thêm vào giỏ</button>
          <button className="buy-now" onClick={() => addToCart(true)}>
            Mua ngay
          </button>
        </div>
      </div>
    </div>
  );
}
