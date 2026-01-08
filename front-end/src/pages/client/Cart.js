import { useEffect, useState } from "react";
import { Container, Table, Button, Alert, Spinner } from "react-bootstrap";
import { getCartByUser, removeCartItem } from "../../services/cartService";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../../assets/styles/Cart.css";

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedItems, setSelectedItems] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  const loadCart = async () => {
    try {
      const data = await getCartByUser();
      setCart(data);
      setSelectedItems([]);
    } catch {
      setCart(null);
    } finally {
      setLoading(false);
    }
  };

  const toggleSelectItem = (itemId) => {
    setSelectedItems((prev) =>
      prev.includes(itemId)
        ? prev.filter((id) => id !== itemId)
        : [...prev, itemId]
    );
  };

  const toggleSelectAllItem = () => {
    if (selectedItems.length === cart.items.length) {
      setSelectedItems([]);
    } else {
      setSelectedItems(cart.items.map((item) => item.id));
    }
  };

  const selectedTotalPrice = cart
    ? cart.items
        .filter((item) => selectedItems.includes(item.id))
        .reduce(
          (total, item) =>
            total + item.priceAtAddedTime * item.totalQuantity,
          0
        )
    : 0;

  const handleRemoveItem = async (itemId) => {
    const result = await Swal.fire({
      title: "Xóa sản phẩm?",
      text: "Sản phẩm sẽ bị xóa khỏi giỏ hàng",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
    });

    if (!result.isConfirmed) return;

    await removeCartItem(itemId);
    Swal.fire("Đã xóa!", "", "success");
    loadCart();
  };

  const handleCheckout = () => {
    if (selectedItems.length === 0) {
      Swal.fire("Vui lòng chọn sản phẩm để thanh toán");
      return;
    }

    navigate("/cart/checkout", {
      state: { itemIds: selectedItems },
    });
  };

  if (loading) {
    return (
      <Container className="cart-loading">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <Container className="cart-empty">
        <Alert variant="info">🛒 Giỏ hàng của bạn đang trống</Alert>
        <Button onClick={() => navigate("/products")}>
          Tiếp tục mua sắm
        </Button>
      </Container>
    );
  }

  return (
    <Container className="cart-page">
      <h2 className="cart-title">🛒 Giỏ hàng của bạn</h2>

      <Table responsive className="cart-table">
        <thead>
          <tr>
            <th>
              <input
                type="checkbox"
                checked={selectedItems.length === cart.items.length}
                onChange={toggleSelectAllItem}
              />
            </th>
            <th>Sản phẩm</th>
            <th>Đơn giá</th>
            <th>Số lượng</th>
            <th>Tạm tính</th>
            <th></th>
          </tr>
        </thead>

        <tbody>
          {cart.items.map((item) => (
            <tr
              key={item.id}
              className={
                selectedItems.includes(item.id)
                  ? "cart-row-selected"
                  : ""
              }
            >
              <td>
                <input
                  type="checkbox"
                  checked={selectedItems.includes(item.id)}
                  onChange={() => toggleSelectItem(item.id)}
                />
              </td>

              <td>
                <div className="cart-product-cell">
                  <img
                    src={item.productThumbnail || "/no-image.png"}
                    alt={item.productName}
                    className="cart-product-image"
                  />
                  <div className="cart-product-info">
                    <div className="cart-product-name">
                      {item.productName}
                    </div>
                    <div className="cart-product-size">
                      Size: {item.sizeCode}
                    </div>
                  </div>
                </div>
              </td>

              <td>{item.priceAtAddedTime.toLocaleString()} đ</td>

              <td>
                <div className="cart-quantity">
                  <Button size="sm" variant="light" disabled>
                    −
                  </Button>
                  <span>{item.totalQuantity}</span>
                  <Button size="sm" variant="light" disabled>
                    +
                  </Button>
                </div>
              </td>

              <td>
                {(item.priceAtAddedTime * item.totalQuantity).toLocaleString()} đ
              </td>

              <td>
                <Button
                  variant="outline-danger"
                  size="sm"
                  onClick={() => handleRemoveItem(item.id)}
                >
                  ✕
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      {/* Sticky Footer */}
      <div className="cart-footer sticky">
        <div>
          <strong>
            Tổng thanh toán ({selectedItems.length} sản phẩm):
          </strong>
          <span className="cart-total">
            {selectedTotalPrice.toLocaleString()} đ
          </span>
        </div>

        <Button
          variant="danger"
          size="lg"
          disabled={selectedItems.length === 0}
          onClick={handleCheckout}
        >
          Thanh toán
        </Button>
      </div>
    </Container>
  );
}
