import { useCallback, useEffect, useState } from "react";
import { Heart, Trash2 } from "lucide-react";
import { useNavigate } from "react-router-dom";
import swal from "sweetalert";
import { getWishlist, removeFromWishlist } from "../../services/wishlistService";
import "../../assets/styles/Wishlist.css";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

export default function Wishlist() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const loadItems = useCallback(async () => {
    try {
      setLoading(true);
      setItems((await getWishlist()) || []);
    } catch (error) {
      swal("Lỗi", error.message, "error");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadItems();
  }, [loadItems]);

  const remove = async (productId) => {
    await removeFromWishlist(productId);
    setItems((current) =>
      current.filter((item) => item.productId !== productId)
    );
  };

  return (
    <div className="wishlist-page">
      <div className="wishlist-heading">
        <Heart />
        <div>
          <h1>Sản phẩm yêu thích</h1>
          <p>Lưu lại sản phẩm để xem và mua sau.</p>
        </div>
      </div>

      {loading ? (
        <div className="wishlist-state">Đang tải danh sách yêu thích...</div>
      ) : items.length === 0 ? (
        <div className="wishlist-state">Bạn chưa lưu sản phẩm nào.</div>
      ) : (
        <div className="wishlist-grid">
          {items.map((item) => {
            const discount = Number(item.discount || 0);
            const finalPrice =
              Number(item.minimumPrice || 0) * (1 - discount / 100);
            return (
              <article key={item.id} className="wishlist-card">
                <button
                  className="wishlist-image"
                  type="button"
                  onClick={() =>
                    navigate(`/products/product-details/${item.productId}`)
                  }
                >
                  <img
                    src={
                      item.imageUrl ||
                      "https://placehold.co/600x600/f1f5f9/0f172a?text=Product"
                    }
                    alt={item.productName}
                  />
                </button>
                <div>
                  <h2>{item.productName}</h2>
                  <strong>{formatCurrency(finalPrice)}</strong>
                  <div className="wishlist-actions">
                    <button
                      type="button"
                      onClick={() =>
                        navigate(`/products/product-details/${item.productId}`)
                      }
                    >
                      Xem sản phẩm
                    </button>
                    <button
                      type="button"
                      className="danger"
                      aria-label={`Xóa ${item.productName}`}
                      onClick={() => remove(item.productId)}
                    >
                      <Trash2 size={17} />
                    </button>
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      )}
    </div>
  );
}
