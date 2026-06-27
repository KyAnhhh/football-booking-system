import { useCallback, useEffect, useMemo, useState } from "react";
import { getMyCart, removeCartItem } from "../../services/cartService";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../../assets/styles/Cart.css";
import { ShoppingBag, Trash2 } from "lucide-react";
import { useCart } from "../../contexts/CartContext";

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const getItemDiscount = (item) => Number(item.discount || 0);

const getItemFinalPrice = (item) => {
  const price = Number(item.priceAtAddedTime || 0);
  const discount = getItemDiscount(item);
  return price - (price * discount) / 100;
};

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedItems, setSelectedItems] = useState([]);
  const navigate = useNavigate();
  const { updateCartCountFromCart, refreshCartCount } = useCart();

  const loadCart = useCallback(async () => {
    try {
      const data = await getMyCart();
      setCart(data);
      updateCartCountFromCart(data);
      setSelectedItems([]);
    } catch {
      setCart(null);
      updateCartCountFromCart(null);
    } finally {
      setLoading(false);
    }
  }, [updateCartCountFromCart]);

  useEffect(() => {
    loadCart();
  }, [loadCart]);

  const items = useMemo(() => cart?.items || [], [cart]);

  const selectedSummary = useMemo(() => {
    const selected = items.filter((item) => selectedItems.includes(item.id));

    return selected.reduce(
      (summary, item) => {
        const quantity = Number(item.totalQuantity || 0);
        const original = Number(item.priceAtAddedTime || 0) * quantity;
        const final = getItemFinalPrice(item) * quantity;

        return {
          originalTotal: summary.originalTotal + original,
          discountTotal: summary.discountTotal + (original - final),
          finalTotal: summary.finalTotal + final,
        };
      },
      { originalTotal: 0, discountTotal: 0, finalTotal: 0 }
    );
  }, [items, selectedItems]);

  const toggleSelectItem = (itemId) => {
    setSelectedItems((prev) =>
      prev.includes(itemId)
        ? prev.filter((id) => id !== itemId)
        : [...prev, itemId]
    );
  };

  const toggleSelectAllItem = () => {
    if (selectedItems.length === items.length) {
      setSelectedItems([]);
      return;
    }
    setSelectedItems(items.map((item) => item.id));
  };

  const handleRemoveItem = async (itemId) => {
    const result = await Swal.fire({
      title: "Xóa sản phẩm?",
      text: "Sản phẩm sẽ được xóa khỏi giỏ hàng của bạn.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
    });

    if (!result.isConfirmed) return;

    await removeCartItem(itemId);
    Swal.fire("Đã xóa", "Sản phẩm đã được xóa khỏi giỏ hàng.", "success");
    await refreshCartCount();
    loadCart();
  };

  const handleCheckout = () => {
    if (selectedItems.length === 0) {
      Swal.fire("Chưa chọn sản phẩm", "Vui lòng chọn sản phẩm để thanh toán.");
      return;
    }

    navigate("/cart/checkout", {
      state: { itemIds: selectedItems },
    });
  };

  if (loading) {
    return <div className="cart-state">Đang tải giỏ hàng...</div>;
  }

  if (!items.length) {
    return (
      <div className="cart-state cart-empty-state">
        <ShoppingBag size={34} />
        <h2>Giỏ hàng đang trống</h2>
        <p>Chọn sản phẩm bóng đá yêu thích và quay lại đây để thanh toán.</p>
        <button type="button" onClick={() => navigate("/products")}>
          Tiếp tục mua sắm
        </button>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <section className="cart-hero">
        <div>
          <p>Giỏ hàng</p>
          <h1>Sản phẩm đã chọn</h1>
          <span>Kiểm tra size, số lượng và giá sau giảm trước khi thanh toán.</span>
        </div>
      </section>

      <div className="cart-shell">
        <div className="cart-list-panel">
          <label className="cart-select-all">
            <input
              type="checkbox"
              checked={selectedItems.length === items.length}
              onChange={toggleSelectAllItem}
            />
            <span>Chọn tất cả ({items.length} sản phẩm)</span>
          </label>

          <div className="cart-items">
            {items.map((item) => {
              const discount = getItemDiscount(item);
              const finalPrice = getItemFinalPrice(item);
              const lineTotal = finalPrice * Number(item.totalQuantity || 0);

              return (
                <article
                  key={item.id}
                  className={`cart-item-card ${
                    selectedItems.includes(item.id) ? "selected" : ""
                  }`}
                >
                  <input
                    type="checkbox"
                    checked={selectedItems.includes(item.id)}
                    onChange={() => toggleSelectItem(item.id)}
                  />

                  <img
                    src={
                      item.productThumbnail ||
                      "https://placehold.co/180x180/f1f5f9/0f172a?text=Product"
                    }
                    alt={item.productName}
                  />

                  <div className="cart-item-info">
                    <h3>{item.productName}</h3>
                    <span>Size: {item.sizeCode || "N/A"}</span>
                    <div className="cart-item-price">
                      {discount > 0 && (
                        <small>{formatCurrency(item.priceAtAddedTime)}</small>
                      )}
                      <strong>{formatCurrency(finalPrice)}</strong>
                      {discount > 0 && <em>-{discount}%</em>}
                    </div>
                  </div>

                  <div className="cart-item-quantity">x{item.totalQuantity}</div>

                  <div className="cart-item-total">
                    <span>Tạm tính</span>
                    <strong>{formatCurrency(lineTotal)}</strong>
                  </div>

                  <button
                    type="button"
                    className="cart-remove-btn"
                    onClick={() => handleRemoveItem(item.id)}
                    aria-label="Xóa sản phẩm"
                  >
                    <Trash2 size={18} />
                  </button>
                </article>
              );
            })}
          </div>
        </div>

        <aside className="cart-summary">
          <h2>Tổng giỏ hàng</h2>
          <div className="cart-summary-lines">
            <div>
              <span>Tạm tính</span>
              <strong>{formatCurrency(selectedSummary.originalTotal)}</strong>
            </div>
            <div>
              <span>Giảm giá</span>
              <strong>-{formatCurrency(selectedSummary.discountTotal)}</strong>
            </div>
          </div>
          <div className="cart-summary-total">
            <span>Cần thanh toán</span>
            <strong>{formatCurrency(selectedSummary.finalTotal)}</strong>
          </div>
          <button
            type="button"
            disabled={selectedItems.length === 0}
            onClick={handleCheckout}
          >
            Thanh toán {selectedItems.length > 0 ? `(${selectedItems.length})` : ""}
          </button>
        </aside>
      </div>
    </div>
  );
}
