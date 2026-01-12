import { useEffect, useState } from "react";
import Swal from "sweetalert2";
import { useLocation } from "react-router-dom";
import { getCartByUser } from "../../services/cartService";
import { checkout } from "../../services/orderService";
import {
  getAddressOfMe,
  createAddress,
} from "../../services/shippingAddressService";
import "../../assets/styles/Checkout.css";

const phoneRegex = /^(0|\+84)[0-9]{9}$/;

const calculateShippingFee = (items) => {
  if (items.length === 0) return 0;
  const totalQty = items.reduce((sum, i) => sum + i.totalQuantity, 0);
  if (totalQty <= 2) return 20000;
  if (totalQty <= 5) return 30000;
  return 40000;
};

const validateCheckout = ({
  selectedItemIds,
  useNewAddress,
  newAddress,
  selectedAddressId,
}) => {
  if (selectedItemIds.length === 0)
    return "Vui lòng chọn ít nhất 1 sản phẩm";

  if (useNewAddress) {
    if (!newAddress.fullName || newAddress.fullName.length < 2)
      return "Họ tên không hợp lệ";
    if (!phoneRegex.test(newAddress.phone))
      return "Số điện thoại không hợp lệ";
    if (!newAddress.address || newAddress.address.length < 5)
      return "Địa chỉ không hợp lệ";
  } else {
    if (!selectedAddressId)
      return "Vui lòng chọn địa chỉ giao hàng";
  }
  return null;
};

const CheckoutItems = ({ cartItems, selectedItemIds, toggleItem }) => (
  <div className="checkout-card">
    <h3>Sản phẩm</h3>

    {cartItems.length === 0 && <p>Giỏ hàng trống</p>}

    {cartItems.map((item) => (
      <div className="cart-item" key={item.id}>
        <div className="cart-left">
          <input
            type="checkbox"
            checked={selectedItemIds.includes(item.id)}
            onChange={() => toggleItem(item.id)}
          />

          <img
            src={item.productThumbnail || "/no-image.png"}
            className="cart-product-image"
            alt=""
          />

          <div className="cart-info">
            <span className="cart-name">
              {item.productName} ({item.sizeName})
            </span>
            <span className="cart-meta">
              SL: {item.totalQuantity}
            </span>
          </div>
        </div>
      </div>
    ))}
  </div>
);

const CheckoutAddress = ({
  addresses,
  useNewAddress,
  setUseNewAddress,
  selectedAddressId,
  setSelectedAddressId,
  newAddress,
  setNewAddress,
  paymentMethod,
  setPaymentMethod,
}) => (
  <div className="checkout-card">
    <h3>Địa chỉ giao hàng</h3>

    <label className="radio-group">
      <input
        type="radio"
        checked={!useNewAddress}
        onChange={() => setUseNewAddress(false)}
      />
      Địa chỉ đã lưu
    </label>

    {!useNewAddress && (
      <select
        value={selectedAddressId || ""}
        onChange={(e) => setSelectedAddressId(Number(e.target.value))}
      >
        {addresses.map((addr) => (
          <option key={addr.id} value={addr.id}>
            {addr.fullName} – {addr.address}
          </option>
        ))}
      </select>
    )}

    <label className="radio-group">
      <input
        type="radio"
        checked={useNewAddress}
        onChange={() => setUseNewAddress(true)}
      />
      Thêm địa chỉ mới
    </label>

    {useNewAddress && (
      <>
        <input
          placeholder="Họ tên"
          value={newAddress.fullName}
          onChange={(e) =>
            setNewAddress({ ...newAddress, fullName: e.target.value })
          }
        />
        <input
          placeholder="Số điện thoại"
          value={newAddress.phone}
          onChange={(e) =>
            setNewAddress({ ...newAddress, phone: e.target.value })
          }
        />
        <input
          placeholder="Địa chỉ"
          value={newAddress.address}
          onChange={(e) =>
            setNewAddress({ ...newAddress, address: e.target.value })
          }
        />
      </>
    )}

    <h3>Thanh toán</h3>
    <select
      value={paymentMethod}
      onChange={(e) => setPaymentMethod(e.target.value)}
    >
      <option value="COD">Thanh toán khi nhận hàng</option>
      <option value="BANK_TRANSFER">Chuyển khoản</option>
      <option value="E_WALLET">Ví điện tử</option>
    </select>
  </div>
);

const CheckoutSummary = ({ items, loading, onCheckout }) => {
  const subTotal = items.reduce(
    (sum, i) => sum + i.priceAtAddedTime * i.totalQuantity,
    0
  );

  const shippingFee = calculateShippingFee(items);
  const total = subTotal + shippingFee;

  return (
    <div className="checkout-card checkout-summary">
      <h3>Đơn hàng</h3>

      <div className="summary-row">
        <span>Tạm tính</span>
        <span>{subTotal.toLocaleString()} ₫</span>
      </div>

      <div className="summary-row">
        <span>Phí vận chuyển</span>
        <span>{shippingFee.toLocaleString()} ₫</span>
      </div>

      <div className="summary-total">
        <span>Tổng cộng</span>
        <span>{total.toLocaleString()} ₫</span>
      </div>

      <button
        className="checkout-btn"
        onClick={onCheckout}
        disabled={loading}
      >
        {loading ? "Đang xử lý..." : "Đặt hàng"}
      </button>
    </div>
  );
};

export default function Checkout() {
  const location = useLocation();

  const {
    mode = "CART",
    itemIds = [],
  } = location.state || {};

  const [cartItems, setCartItems] = useState([]);
  const [selectedItemIds, setSelectedItemIds] = useState([]);

  const [addresses, setAddresses] = useState([]);
  const [useNewAddress, setUseNewAddress] = useState(false);
  const [selectedAddressId, setSelectedAddressId] = useState(null);

  const [newAddress, setNewAddress] = useState({
    fullName: "",
    phone: "",
    address: "",
  });

  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getCartByUser().then((res) => {
      const allItems = res.items || [];

      if (mode === "BUY_NOW") {
        const buyNowItems = allItems.filter((i) =>
          itemIds.includes(i.id)
        );
        setCartItems(buyNowItems);
        setSelectedItemIds(buyNowItems.map((i) => i.id));
      } else {
        setCartItems(allItems);
        setSelectedItemIds(
          itemIds.length ? itemIds : allItems.map((i) => i.id)
        );
      }
    });

    getAddressOfMe().then((res) => {
      if (res) {
        setAddresses([res]);
        setSelectedAddressId(res.id);
      }
    });
  }, []);

  const toggleItem = (id) => {
    setSelectedItemIds((prev) =>
      prev.includes(id)
        ? prev.filter((x) => x !== id)
        : [...prev, id]
    );
  };

  const handleCheckout = async () => {
    const error = validateCheckout({
      selectedItemIds,
      useNewAddress,
      newAddress,
      selectedAddressId,
    });

    if (error) {
      Swal.fire({ icon: "warning", title: "Lỗi", text: error });
      return;
    }

    try {
      setLoading(true);
      let addressId = selectedAddressId;

      if (useNewAddress) {
        const res = await createAddress(newAddress);
        addressId = res.id;
      }

      const res = await checkout({
        shippingAddressId: addressId,
        paymentMethod,
        cartItemIds: selectedItemIds,
      });

      Swal.fire({
        icon: "success",
        title: "Đặt hàng thành công 🎉",
        text: `Mã đơn hàng: ${res.id}`,
      });
    } catch (e) {
      Swal.fire({
        icon: "error",
        title: "Checkout thất bại",
        text: e?.response?.message || "Có lỗi xảy ra",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="checkout-container">
      <h2>Checkout</h2>

      <div className="checkout-grid">
        <div className="checkout-left">
          <CheckoutItems
            cartItems={cartItems}
            selectedItemIds={selectedItemIds}
            toggleItem={toggleItem}
          />

          <CheckoutAddress
            addresses={addresses}
            useNewAddress={useNewAddress}
            setUseNewAddress={setUseNewAddress}
            selectedAddressId={selectedAddressId}
            setSelectedAddressId={setSelectedAddressId}
            newAddress={newAddress}
            setNewAddress={setNewAddress}
            paymentMethod={paymentMethod}
            setPaymentMethod={setPaymentMethod}
          />
        </div>

        <CheckoutSummary
          items={cartItems.filter((i) =>
            selectedItemIds.includes(i.id)
          )}
          loading={loading}
          onCheckout={handleCheckout}
        />
      </div>
    </div>
  );
}
