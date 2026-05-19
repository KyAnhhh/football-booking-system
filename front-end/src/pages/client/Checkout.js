import { useEffect, useState } from "react";
import Swal from "sweetalert2";
import { useLocation } from "react-router-dom";
import { getMyCart } from "../../services/cartService";
import { checkout, createOrder } from "../../services/orderService";
import {
  getMyAddress,
  createAddress,
} from "../../services/shippingAddressService";
import "../../assets/styles/Checkout.css";
import { getQrCode } from "../../services/qrCodeService";

const phoneRegex = /^(0|\+84)[0-9]{9}$/;

const calculateShippingFee = (items) => {
  if (items.length === 0) return 0;

  const totalQty = items.reduce(
    (sum, i) => sum + i.totalQuantity,
    0
  );

  if (totalQty >= 10) return 0;
  if (totalQty >= 5) return 30000;
  return 50000;
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

const orderDateObj = new Date(Date.now());
const deliveryDateObj = new Date(Date.now() + 3 * 24 * 60 * 60 * 1000);
const orderDayOnly = orderDateObj.toLocaleDateString('vi-VN');
const deliveryDayOnly = deliveryDateObj.toLocaleDateString('vi-VN');
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
        name="addressType"
        checked={!useNewAddress}
        onChange={() => setUseNewAddress(false)}
      />
      <span className="custom-radio"></span>
      Địa chỉ đã lưu
    </label>

    {!useNewAddress && (
    <>
      {addresses.length > 0 ? (
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
      ) : (
        <p style={{ color: "#888", marginTop: "8px" }}>
          Bạn chưa có địa chỉ. Hãy chọn "Thêm địa chỉ mới".
        </p>
      )}
    </>
  )}

    <label className="radio-group">
      <input
        type="radio"
        name="addressType"
        checked={useNewAddress}
        onChange={() => setUseNewAddress(true)}
      />
      <span className="custom-radio"></span>
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
    <div className="payment-methods">
      <label className={`payment-card ${paymentMethod === "COD" ? "active" : ""}`}>
        <input
          type="radio"
          name="paymentMethod"
          value="COD"
          checked={paymentMethod === "COD"}
          onChange={(e) => setPaymentMethod(e.target.value)}
        />
        <div>
          <strong>COD</strong>
          <p>Thanh toán khi nhận hàng</p>
        </div>
      </label>

        <label className={`payment-card ${paymentMethod === "VNPAY" ? "active" : ""}`}>
          <input
            type="radio"
            name="paymentMethod"
            value="VNPAY"
            checked={paymentMethod === "VNPAY"}
            onChange={(e) => setPaymentMethod(e.target.value)}
          />

          <div className="payment-content">

            <div className="payment-logo">
              <img
                src="https://res.cloudinary.com/do8twj1im/image/upload/v1779120854/ylfqrkcbaadgs73wrcgn.png"
                alt="vnpay"
              />
            </div>

            <div>
              <strong>VNPAY</strong>
              <p>Thanh toán online qua VNPay</p>
            </div>

          </div>
        </label>

      <label className={`payment-card ${paymentMethod === "VIETQR" ? "active" : ""}`}>
        <input
          type="radio"
          name="paymentMethod"
          value="VIETQR"
          checked={paymentMethod === "VIETQR"}
          onChange={(e) => setPaymentMethod(e.target.value)}
        />

        <div className="payment-content">

          <div className="payment-logo">
            <img
              src="https://res.cloudinary.com/do8twj1im/image/upload/v1779120893/o4oefylronic76jhgkq9.png"
              alt="vietqr"
            />
          </div>

          <div>
            <strong>VIETQR</strong>
            <p>Chuyển khoản QR ngân hàng</p>
          </div>
        </div>
      </label>

    </div>
    <h3>Thời gian đặt hàng: {orderDayOnly}</h3>
    <h3>Thời gian giao hàng dự kiến: {deliveryDayOnly} </h3>
  </div>
);

const CheckoutSummary = ({ items, loading, onCheckout, paymentMethod, showQR }) => {
  const subTotal = items.reduce((sum, i) => {
  const discountPercent = i.discount || 0;
  const priceAfterDiscount =
    i.priceAtAddedTime * (100 - discountPercent) / 100;
    

  return sum + priceAfterDiscount * i.totalQuantity;
}, 0);

const totalDiscount = items.reduce((sum, i) => {
  const discountPercent = i.discount || 0;
  return (
    sum +
    (i.priceAtAddedTime * discountPercent / 100) *
      i.totalQuantity
  );
}, 0);

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

      <div className="summary-row discount">
        <span>Giảm giá</span>
        <span>-{totalDiscount.toLocaleString()} ₫</span>
      </div>

      <div className="summary-total">
        <span>Tổng cộng</span>
        <span>{total.toLocaleString()} ₫</span>
      </div>

      <button
        className="checkout-btn"
        onClick={onCheckout}
        disabled={loading || showQR}
      >
        {loading ? "Đang xử lý..." 
          : paymentMethod === "COD" 
          ? "Đặt hàng" 
          : "Tạo đơn & hiển thị QR"
        }
      </button>
    </div>
  );
};

export default function Checkout() {
  const [cartItems, setCartItems] = useState([]);
  const [selectedItemIds, setSelectedItemIds] = useState([]);

  const [addresses, setAddresses] = useState([]);
  const [useNewAddress, setUseNewAddress] = useState(false);
  const [selectedAddressId, setSelectedAddressId] = useState(null);

  const location = useLocation();
  const checkoutMode = location.state?.mode || "CART_CHECKOUT";
  const buyNowProduct = location.state?.product || null;
  
  const [newAddress, setNewAddress] = useState({
    fullName: "",
    phone: "",
    address: "",
  });

  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [loading, setLoading] = useState(false);

  const [showQR, setShowQR] = useState(false);

  const [orderId, setOrderId] = useState(null);

  const [qrCodeUrl, setQrCodeUrl] = useState(null);

  useEffect(() => {
    if(!orderId) return;

    let objectUrl;

    const loadUrl = async () => {
      try {
        const url = await getQrCode(orderId);
        objectUrl = url;
        setQrCodeUrl(url);
      } catch (error) {
        console.error("Lỗi khi tải QR code:", error);
      }
    };

    loadUrl();

    return () => {
      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
  }, [orderId]);

  useEffect(() => {
    const checkoutItemIds = location.state?.itemIds || [];
    
    const loadCheckoutData = async () => {
      if (checkoutMode === "BUY_NOW" && buyNowProduct) {
        setCartItems([{
          id: `temp_${buyNowProduct.productVariantId}`,
          productId: buyNowProduct.productId,
          productName: buyNowProduct.productName,
          productThumbnail: buyNowProduct.productThumbnail,
          productVariantId: buyNowProduct.productVariantId,
          sizeName: buyNowProduct.sizeName,
          priceAtAddedTime: buyNowProduct.priceAtAddedTime,
          totalQuantity: buyNowProduct.totalQuantity,
          discount: buyNowProduct.discount || 0
        }]);
        setSelectedItemIds([`temp_${buyNowProduct.productVariantId}`]);
      } else {

        const cartRes = await getMyCart();

        const allItems = (cartRes.items || []).map(item => ({
          ...item,
          discount: item.discount ?? 0
        }));

        const filteredItems = checkoutItemIds.length
          ? allItems.filter((i) => checkoutItemIds.includes(i.id))
          : allItems;
        setCartItems(filteredItems);
        setSelectedItemIds(filteredItems.map((i) => i.id));
      }
    };

    const loadAddresses = async () => {
      try {
        const res = await getMyAddress();

            console.log("FULL RES:", res);
    console.log("TYPE:", typeof res);
    console.log("DATA:", res.data);

        if (!res) {
          setAddresses([]);
          return;
        }

        const address = res;

        if (address) {
          setAddresses([address]);
          setSelectedAddressId(address.id);
        } else {
          setAddresses([]);
        }

      } catch (error) {
        console.error("Lỗi khi tải địa chỉ:", error);
        setAddresses([]);
      }
    };

    loadCheckoutData();
    loadAddresses();
  }, [checkoutMode, buyNowProduct, location.state?.itemIds]);

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

      let response;
      if (checkoutMode === "BUY_NOW") {
        const totalQty = buyNowProduct.totalQuantity;
        let shippingCost = 0;
        if (totalQty >= 10) shippingCost = 0;
        else if (totalQty >= 5) shippingCost = 30000;
        else shippingCost = 50000;
        response = await createOrder({
          shippingAddressId: addressId,
          paymentMethod,
          items: [{
            productVariantId: buyNowProduct.productVariantId,
            quantity: buyNowProduct.totalQuantity,
          }],
          shippingCost: shippingCost,
        });
        setOrderId(response.id);
      } else {
        const requestBody = {
          shippingAddressId: addressId,
          paymentMethod,
          cartItemIds: selectedItemIds.map(id => Number(id)),
        };
        console.log("Checkout request body:", requestBody);
        response = await checkout(requestBody);

        setOrderId(response.id);
      }

      Swal.fire({
        icon: paymentMethod === "VIETQR" ? "info" : "success",
        title:
          paymentMethod === "VIETQR"
            ? "Đơn hàng đã được tạo"
            : "Đặt hàng thành công 🎉",
        text:
          paymentMethod === "VIETQR"
            ? "Vui lòng quét mã QR để thanh toán đơn hàng"
            : "Đơn hàng của bạn đã được đặt thành công. Shop sẽ sớm liên hệ!",
        confirmButtonText: "OK"
      }).then(() => {
        if (paymentMethod === "VIETQR") {
          setShowQR(true);
        } else {
          window.location.href = "/";
        }
      });

    } catch (e) {
      console.error("Checkout error:", e);
      Swal.fire({
        icon: "error",
        title: "Checkout thất bại",
        text: e?.message || e?.response?.message || "Có lỗi xảy ra",
      });
    } finally {
      setLoading(false);
    }
  };
  const handleConfirmTransfer = async () => {
    Swal.fire({
      icon: "info",
      title: "Đã ghi nhận",
      text: "Đơn hàng của bạn đang được xử lý."
    });
    setTimeout(() => {
      window.location.href = "/";
    }, 3000);
  }

  return (
    <div className="checkout-container">
      <h2>Trang thanh toán</h2>

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
          paymentMethod={paymentMethod}
          showQR={showQR}
        />

        {/* Hiển thị QR-CODE */}
        {paymentMethod === "VIETQR" && showQR && orderId && (
          <div className="checkout-card qr-box">
  <div className="qr-content">

    <div className="qr-info">
      <h3>Quét mã QR để thanh toán</h3>

      <p className="qr-status">
        ⏳ Trạng thái: <b>Chờ thanh toán</b>
      </p>

      <p>
        Đơn hàng đã được tạo, vui lòng hoàn tất chuyển khoản
      </p>

      <p>
        Nội dung chuyển khoản:
        <b> Đơn hàng #{orderId}</b>
      </p>
    </div>

    <div className="qr-image-wrapper">
      <img
        src={qrCodeUrl}
        alt="QR thanh toán"
      />
    </div>

    <div className="qr-action">
      <button
        className="checkout-btn"
        onClick={handleConfirmTransfer}
      >
        Tôi đã chuyển khoản
      </button>
    </div>

  </div>
</div>
        )}
      </div>
    </div>
  );
}