import { useEffect, useMemo, useState } from "react";
import Swal from "sweetalert";
import { useLocation, useNavigate } from "react-router-dom";
import { Modal } from "react-bootstrap";
import { getMyCart, updateCartItemQuantity } from "../../services/cartService";
import {
  checkout,
  createOrder,
  markOrderPaymentSubmitted,
} from "../../services/orderService";
import {
  getMyAddress,
  createAddress,
} from "../../services/shippingAddressService";
import "../../assets/styles/Checkout.css";
import { getQrCode } from "../../services/qrCodeService";
import {
  CheckCircle2,
  MapPin,
  Package,
  QrCode,
  Truck,
} from "lucide-react";
import { useCart } from "../../contexts/CartContext";

const phoneRegex = /^(0|\+84)(3|5|7|8|9)[0-9]{8}$/;

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const calculateShippingFee = (items) => {
  if (!items.length) return 0;
  const totalQuantity = items.reduce(
    (sum, item) => sum + Number(item.quantity || item.totalQuantity || 0),
    0
  );

  if (totalQuantity >= 10) return 0;
  if (totalQuantity >= 5) return 30000;
  return 50000;
};

const getItemId = (item) => item.cartItemId ?? item.id ?? item.productVariantId;

const getItemBasePrice = (item) => Number(item.priceAtAddedTime || item.price || 0);

const getItemDiscountPercent = (item) => Number(item.discount || 0);

const getItemPrice = (item) => {
  const price = getItemBasePrice(item);
  const discount = getItemDiscountPercent(item);
  return price - (price * discount) / 100;
};

const getItemQuantity = (item) => Number(item.quantity || item.totalQuantity || 0);

function CheckoutItems({ items, selectedItemIds, toggleItem, checkoutMode, onQuantityChange, updatingItemId }) {
  return (
    <section className="checkout-panel">
      <div className="checkout-panel-title">
        <Package size={20} />
        <div>
          <h2>Sản phẩm</h2>
          <p>{items.length} sản phẩm trong đơn hàng</p>
        </div>
      </div>

      <div className="checkout-items">
        {items.map((item) => {
          const itemId = getItemId(item);
          const selected = selectedItemIds.includes(itemId);
          const discount = getItemDiscountPercent(item);
          const image =
            item.thumbnail ||
            item.productThumbnail ||
            "https://placehold.co/180x180/f1f5f9/0f172a?text=Product";

          return (
            <label className="checkout-item" key={itemId}>
              {checkoutMode !== "BUY_NOW" && (
                <input
                  type="checkbox"
                  checked={selected}
                  onChange={() => toggleItem(itemId)}
                />
              )}
              <img src={image} alt={item.productName} />
              <div className="checkout-item-info">
                <h3>{item.productName}</h3>
                <span>Size: {item.sizeName || item.sizeCode || "N/A"}</span>
                <div className="checkout-item-price">
                  {discount > 0 && (
                    <small>{formatCurrency(getItemBasePrice(item))}</small>
                  )}
                  <strong>{formatCurrency(getItemPrice(item))}</strong>
                  {discount > 0 && <em>-{discount}%</em>}
                </div>
              </div>
              {checkoutMode === "BUY_NOW" ? <div className="checkout-item-qty">x{getItemQuantity(item)}</div> : (
                <div className="checkout-item-qty checkout-item-qty-controls">
                  <button type="button" disabled={getItemQuantity(item) <= 1 || updatingItemId === itemId}
                    onClick={(event) => { event.preventDefault(); event.stopPropagation(); onQuantityChange(item, getItemQuantity(item) - 1); }}>−</button>
                  <strong>{getItemQuantity(item)}</strong>
                  <button type="button" disabled={updatingItemId === itemId}
                    onClick={(event) => { event.preventDefault(); event.stopPropagation(); onQuantityChange(item, getItemQuantity(item) + 1); }}>+</button>
                </div>
              )}
            </label>
          );
        })}
      </div>
    </section>
  );
}

function CheckoutAddress({
  addresses,
  selectedAddressId,
  setSelectedAddressId,
  useNewAddress,
  setUseNewAddress,
  newAddress,
  setNewAddress,
  paymentMethod,
  setPaymentMethod,
}) {
  const deliveryDate = useMemo(() => {
    const date = new Date();
    date.setDate(date.getDate() + 3);
    return date.toLocaleDateString("vi-VN");
  }, []);

  return (
    <section className="checkout-panel">
      <div className="checkout-panel-title">
        <MapPin size={20} />
        <div>
          <h2>Giao hàng và thanh toán</h2>
          <p>Dự kiến giao hàng: {deliveryDate}</p>
        </div>
      </div>

      {addresses.length > 0 && !useNewAddress && (
        <div className="checkout-address-list">
          {addresses.map((address) => (
            <label className="checkout-address-card" key={address.id}>
              <input
                type="radio"
                checked={selectedAddressId === address.id}
                onChange={() => setSelectedAddressId(address.id)}
              />
              <div>
                <strong>{address.receiverName}</strong>
                <span>{address.phone}</span>
                <p>
                  {address.addressLine}, {address.ward}, {address.district},{" "}
                  {address.city}
                </p>
              </div>
            </label>
          ))}
        </div>
      )}

      <button
        type="button"
        className="checkout-link-btn"
        onClick={() => setUseNewAddress(!useNewAddress)}
      >
        {useNewAddress ? "Dùng địa chỉ đã lưu" : "Thêm địa chỉ mới"}
      </button>

      {(useNewAddress || addresses.length === 0) && (
        <div className="checkout-address-form">
          <input
            placeholder="Tên người nhận"
            value={newAddress.receiverName}
            onChange={(event) =>
              setNewAddress({ ...newAddress, receiverName: event.target.value })
            }
          />
          <input
            placeholder="Số điện thoại"
            value={newAddress.phone}
            onChange={(event) =>
              setNewAddress({ ...newAddress, phone: event.target.value })
            }
          />
          <input
            placeholder="Tỉnh / Thành phố"
            value={newAddress.city}
            onChange={(event) =>
              setNewAddress({ ...newAddress, city: event.target.value })
            }
          />
          <input
            placeholder="Quận / Huyện"
            value={newAddress.district}
            onChange={(event) =>
              setNewAddress({ ...newAddress, district: event.target.value })
            }
          />
          <input
            placeholder="Xã"
            value={newAddress.ward}
            onChange={(event) =>
              setNewAddress({ ...newAddress, ward: event.target.value })
            }
          />
          <input
            placeholder="Địa chỉ chi tiết"
            value={newAddress.addressLine}
            onChange={(event) =>
              setNewAddress({ ...newAddress, addressLine: event.target.value })
            }
          />
        </div>
      )}

      <div className="checkout-payment-methods">
        {[
          { value: "COD", label: "COD", icon: Truck },
          { value: "VIETQR", label: "VietQR", icon: QrCode },
        ].map((method) => {
          const Icon = method.icon;
          return (
            <button
              type="button"
              key={method.value}
              className={paymentMethod === method.value ? "active" : ""}
              onClick={() => setPaymentMethod(method.value)}
            >
              <Icon size={18} />
              {method.label}
            </button>
          );
        })}
      </div>
    </section>
  );
}

function CheckoutSummary({
  selectedItems,
  shippingFee,
  onCheckout,
  loading,
  orderCreated,
}) {
  const originalSubtotal = selectedItems.reduce(
    (sum, item) => sum + getItemBasePrice(item) * getItemQuantity(item),
    0
  );
  const totalDiscount = selectedItems.reduce((sum, item) => {
    const price = getItemBasePrice(item);
    const discount = getItemDiscountPercent(item);
    return sum + (price * discount) / 100 * getItemQuantity(item);
  }, 0);
  const total = originalSubtotal - totalDiscount + shippingFee;

  return (
    <aside className="checkout-summary">
      <div className="checkout-panel-title">
        <CheckCircle2 size={20} />
        <div>
          <h2>Tổng tiền</h2>
          <p>Kiểm tra trước khi đặt hàng</p>
        </div>
      </div>

      <div className="checkout-summary-lines">
        <div>
          <span>Tạm tính</span>
          <strong>{formatCurrency(originalSubtotal)}</strong>
        </div>
        <div>
          <span>Giảm giá sản phẩm</span>
          <strong>-{formatCurrency(totalDiscount)}</strong>
        </div>
        <div>
          <span>Phí vận chuyển</span>
          <strong>{shippingFee === 0 ? "Mien phi" : formatCurrency(shippingFee)}</strong>
        </div>
      </div>

      <div className="checkout-total-row">
        <span>Cần thanh toán</span>
        <strong>{formatCurrency(total)}</strong>
      </div>

      <button
        type="button"
        className="checkout-submit-btn"
        onClick={onCheckout}
        disabled={loading || !selectedItems.length || orderCreated}
      >
        {loading ? "Đang xử lý..." : "Đặt hàng"}
      </button>
    </aside>
  );
}

export default function Checkout() {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [selectedItemIds, setSelectedItemIds] = useState([]);
  const [addresses, setAddresses] = useState([]);
  const [useNewAddress, setUseNewAddress] = useState(false);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [loading, setLoading] = useState(false);
  const [showQR, setShowQR] = useState(false);
  const [orderId, setOrderId] = useState(null);
  const [qrCodeUrl, setQrCodeUrl] = useState("");
  const [orderTotal, setOrderTotal] = useState(0);
  const [submittingTransfer, setSubmittingTransfer] = useState(false);
  const [updatingItemId, setUpdatingItemId] = useState(null);
  const [newAddress, setNewAddress] = useState({
    receiverName: "",
    phone: "",
    city: "",
    district: "",
    ward: "",
    addressLine: "",
  });

  const location = useLocation();
  const { refreshCartCount, updateCartCountFromCart } = useCart();
  const checkoutMode = location.state?.mode || "CART";
  const buyNowProduct = location.state?.product || null;

  useEffect(() => {
    const loadQr = async () => {
      if (!showQR || !orderId) return;
      try {
        const url = await getQrCode(orderId);
        setQrCodeUrl(url);
      } catch (error) {
        Swal("Lỗi", "Không lấy được mã QR thanh toán.", "error");
      }
    };

    loadQr();
  }, [showQR, orderId]);

  useEffect(() => {
    const loadData = async () => {
      try {
        if (checkoutMode === "BUY_NOW" && buyNowProduct) {
          const tempItem = {
            ...buyNowProduct,
            cartItemId: `temp_${buyNowProduct.productVariantId}`,
            quantity: buyNowProduct.totalQuantity,
          };
          setCartItems([tempItem]);
          setSelectedItemIds([tempItem.cartItemId]);
        } else {
          const response = await getMyCart();
          const items = response?.items || [];
          const selectedFromCart = location.state?.itemIds || [];
          const filteredItems = selectedFromCart.length
            ? items.filter((item) => selectedFromCart.includes(getItemId(item)))
            : items;

          setCartItems(filteredItems);
          setSelectedItemIds(filteredItems.map((item) => getItemId(item)));
        }

        const addressResponse = await getMyAddress();
        const loadedAddresses = Array.isArray(addressResponse)
          ? addressResponse
          : addressResponse
            ? [addressResponse]
            : [];

        setAddresses(loadedAddresses);
        if (loadedAddresses.length > 0) {
          setSelectedAddressId(loadedAddresses[0].id);
        } else {
          setUseNewAddress(true);
        }
      } catch (error) {
        Swal("Lỗi", error.message || "Không thể tải dữ liệu thanh toán.", "error");
      }
    };

    loadData();
  }, [checkoutMode, buyNowProduct, location.state?.itemIds]);

  const selectedItems = useMemo(
    () =>
      cartItems.filter((item) =>
        selectedItemIds.includes(getItemId(item))
      ),
    [cartItems, selectedItemIds]
  );

  const shippingFee = useMemo(
    () => calculateShippingFee(selectedItems),
    [selectedItems]
  );

  const toggleItem = (id) => {
    setSelectedItemIds((prev) =>
      prev.includes(id) ? prev.filter((itemId) => itemId !== id) : [...prev, id]
    );
  };

  const handleQuantityChange = async (item, nextQuantity) => {
    if (nextQuantity < 1 || updatingItemId) return;
    try {
      setUpdatingItemId(getItemId(item));
      const updatedCart = await updateCartItemQuantity(getItemId(item), nextQuantity);
      const selectedIds = new Set(selectedItemIds);
      setCartItems((updatedCart?.items || []).filter((cartItem) => selectedIds.has(getItemId(cartItem))));
      updateCartCountFromCart(updatedCart);
    } catch (error) {
      Swal("Không thể cập nhật", error.message || "Số lượng vượt quá tồn kho.", "error");
    } finally {
      setUpdatingItemId(null);
    }
  };

  const validateCheckout = () => {
    if (!selectedItems.length) {
      Swal("Chưa chọn sản phẩm", "Vui lòng chọn ít nhất một sản phẩm.", "warning");
      return false;
    }

    if (useNewAddress || addresses.length === 0) {
      const isMissing = Object.values(newAddress).some((value) => !value.trim());
      if (isMissing) {
        Swal("Thiếu địa chỉ", "Vui lòng nhập đầy đủ thông tin giao hàng.", "warning");
        return false;
      }

      if (!phoneRegex.test(newAddress.phone)) {
        Swal("Số điện thoại không hợp lệ", "Vui lòng kiểm tra lại số điện thoại.", "warning");
        return false;
      }
    } else if (!selectedAddressId) {
      Swal("Chưa chọn địa chỉ", "Vui lòng chọn địa chỉ giao hàng.", "warning");
      return false;
    }

    return true;
  };

  const handleCheckout = async () => {
    if (!validateCheckout()) return;

    try {
      setLoading(true);
      let shippingAddressId = selectedAddressId;

      if (useNewAddress || addresses.length === 0) {
        const createdAddress = await createAddress(newAddress);
        shippingAddressId = createdAddress.id;
      }

      let response;
      if (checkoutMode === "BUY_NOW") {
        response = await createOrder({
          shippingAddressId,
          paymentMethod,
          shippingCost: shippingFee,
          items: selectedItems.map((item) => ({
            productVariantId: item.productVariantId,
            quantity: getItemQuantity(item),
            priceAtAddedTime: item.priceAtAddedTime,
          })),
        });
      } else {
        response = await checkout({
          shippingAddressId,
          paymentMethod,
          cartItemIds: selectedItemIds.map(Number),
          shippingCost: shippingFee,
        });
      }

      setOrderId(response.id);
      setOrderTotal(response.totalAmount);
      if (checkoutMode !== "BUY_NOW") {
        await refreshCartCount();
      }

      if (paymentMethod === "VIETQR") {
        setShowQR(true);
      } else {
        Swal("Thành công", "Đơn hàng đã được tạo thành công.", "success").then(
          () => {
            navigate("/");
          }
        );
      }
    } catch (error) {
      Swal("Lỗi", error.message || "Không thể đặt hàng.", "error");
    } finally {
      setLoading(false);
    }
  };

  const handlePaymentSubmitted = async () => {
    try {
      setSubmittingTransfer(true);
      await markOrderPaymentSubmitted(orderId);
      setShowQR(false);
      handleConfirmTransfer();
    } catch (error) {
      Swal(
        "Không thể ghi nhận",
        error.message || "Vui lòng thử lại sau.",
        "error"
      );
    } finally {
      setSubmittingTransfer(false);
    }
  };

  const handleConfirmTransfer = () => {
    Swal(
      "Đã ghi nhận",
      "Cảm ơn bạn. Admin sẽ kiểm tra giao dịch và xử lí đơn hàng.",
      "success"
    ).then(() => {
      navigate("/my-orders");
    });
  };

  return (
    <div className="checkout-page">
      <section className="checkout-hero">
        <div>
          <p>Thanh toán</p>
          <h1>Hoàn tất đơn hàng</h1>
          <span>
            Kiểm tra sản phẩm, địa chỉ và phương thanh toán trước khi thanh toán.
          </span>
        </div>
      </section>

      <div className="checkout-layout">
        <div className="checkout-main">
          <CheckoutItems
            items={cartItems}
            selectedItemIds={selectedItemIds}
            toggleItem={toggleItem}
            checkoutMode={checkoutMode}
            onQuantityChange={handleQuantityChange}
            updatingItemId={updatingItemId}
          />

          <CheckoutAddress
            addresses={addresses}
            selectedAddressId={selectedAddressId}
            setSelectedAddressId={setSelectedAddressId}
            useNewAddress={useNewAddress}
            setUseNewAddress={setUseNewAddress}
            newAddress={newAddress}
            setNewAddress={setNewAddress}
            paymentMethod={paymentMethod}
            setPaymentMethod={setPaymentMethod}
          />

          {showQR && (
            <Modal
              show={showQR}
              onHide={() => setShowQR(false)}
              centered
              backdrop="static"
              contentClassName="checkout-qr-modal"
            >
              <div className="checkout-panel-title">
                <QrCode size={20} />
                <div>
                  <h2>Thanh toán VietQR</h2>
                  <p>Quét mã và xác nhận sau thanh toán.</p>
                </div>
              </div>

              <div className="checkout-qr-order">
                <span>Đơn hàng #{orderId}</span>
                <strong>{formatCurrency(orderTotal)}</strong>
              </div>

              {qrCodeUrl ? (
                <img src={qrCodeUrl} alt="QR thanh toan" />
              ) : (
                <div className="checkout-qr-loading">Đang tạo mã QR...</div>
              )}

              <button
                type="button"
                onClick={handlePaymentSubmitted}
                disabled={!qrCodeUrl || submittingTransfer}
              >
                Tôi đã chuyển khoản
              </button>
            </Modal>
          )}
        </div>

        <CheckoutSummary
          selectedItems={selectedItems}
          shippingFee={shippingFee}
          onCheckout={handleCheckout}
          loading={loading}
          orderCreated={Boolean(orderId)}
        />
      </div>
    </div>
  );
}
