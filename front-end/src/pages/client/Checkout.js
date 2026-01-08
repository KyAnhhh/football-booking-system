import { useEffect, useState } from "react";
import { getCartByUser } from "../../services/cartService";
import { checkout } from "../../services/orderService";
import {
  getAddressOfMe,
  createAddress,
} from "../../services/shippingAddressService";

const phoneRegex = /^(0|\+84)[0-9]{9}$/;

const validateCheckout = ({
  selectedItemIds,
  useNewAddress,
  newAddress,
  selectedAddressId,
}) => {
  if (!selectedItemIds || selectedItemIds.length === 0) {
    return "Vui lòng chọn ít nhất một sản phẩm";
  }

  if (useNewAddress) {
    if (!newAddress.fullName || newAddress.fullName.trim().length < 2) {
      return "Họ tên người nhận không hợp lệ";
    }

    if (!phoneRegex.test(newAddress.phone)) {
      return "Số điện thoại không hợp lệ";
    }

    if (!newAddress.address || newAddress.address.trim().length < 5) {
      return "Địa chỉ giao hàng không hợp lệ";
    }
  } else {
    if (!selectedAddressId) {
      return "Vui lòng chọn địa chỉ giao hàng";
    }
  }

  return null;
};

export default function Checkout() {
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
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadCart() {
      try {
        const res = await getCartByUser();
        setCartItems(res.data);
        setSelectedItemIds(res.data.map((i) => i.id));
      } catch {
        setError("Không thể load giỏ hàng");
      }
    }
    loadCart();
  }, []);

  useEffect(() => {
    async function loadAddress() {
      try {
        const res = await getAddressOfMe();
        setAddresses(res.data);
        if (res.data.length > 0) {
          setSelectedAddressId(res.data[0].id);
        }
      } catch {
        setError("Không thể load địa chỉ");
      }
    }
    loadAddress();
  }, []);

  const toggleItem = (id) => {
    setSelectedItemIds((prev) =>
      prev.includes(id)
        ? prev.filter((x) => x !== id)
        : [...prev, id]
    );
  };

  const handleCheckout = async () => {
    const errorMessage = validateCheckout({
      selectedItemIds,
      useNewAddress,
      newAddress,
      selectedAddressId,
    });

    if (errorMessage) {
      setError(errorMessage);
      return;
    }

    let shippingAddressId = selectedAddressId;

    try {
      setLoading(true);
      setError("");

      if (useNewAddress) {
        const res = await createAddress(newAddress);
        shippingAddressId = res.data.id;
      }

      const res = await checkout({
        shippingAddressId,
        paymentMethod,
        cartItemIds: selectedItemIds,
      });

      alert(`Đặt hàng thành công! Order ID: ${res.data.id}`);
    } catch (e) {
      setError(e.response?.data?.message || "Checkout thất bại");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="checkout-container">
      <h2>Checkout</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <h3>Sản phẩm</h3>
      {cartItems.map((item) => (
        <div key={item.id}>
          <input
            type="checkbox"
            checked={selectedItemIds.includes(item.id)}
            onChange={() => toggleItem(item.id)}
          />
          {item.productName} ({item.sizeName}) × {item.totalQuantity}
        </div>
      ))}

      <h3>Địa chỉ giao hàng</h3>

      <label>
        <input
          type="radio"
          checked={!useNewAddress}
          onChange={() => setUseNewAddress(false)}
        />
        Chọn địa chỉ đã lưu
      </label>

      {!useNewAddress && (
        <select
          value={selectedAddressId || ""}
          onChange={(e) => setSelectedAddressId(e.target.value)}
        >
          {addresses.map((addr) => (
            <option key={addr.id} value={addr.id}>
              {addr.fullName} - {addr.address}
            </option>
          ))}
        </select>
      )}

      <br />

      <label>
        <input
          type="radio"
          checked={useNewAddress}
          onChange={() => setUseNewAddress(true)}
        />
        Nhập địa chỉ mới
      </label>

      {useNewAddress && (
        <>
          <input
            placeholder="Họ tên"
            onChange={(e) =>
              setNewAddress({ ...newAddress, fullName: e.target.value })
            }
          />
          <input
            placeholder="Số điện thoại"
            onChange={(e) =>
              setNewAddress({ ...newAddress, phone: e.target.value })
            }
          />
          <input
            placeholder="Địa chỉ"
            onChange={(e) =>
              setNewAddress({ ...newAddress, address: e.target.value })
            }
          />
        </>
      )}

      <h3>Thanh toán</h3>
      <select onChange={(e) => setPaymentMethod(e.target.value)}>
        <option value="COD">COD</option>
        <option value="BANK_TRANSFER">Chuyển khoản</option>
        <option value="E_WALLET">Ví điện tử</option>
      </select>

      <br /><br />

      <button onClick={handleCheckout} disabled={loading}>
        {loading ? "Đang xử lý..." : "Đặt hàng"}
      </button>
    </div>
  );
}
