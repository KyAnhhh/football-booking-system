import { createContext, useCallback, useContext, useMemo, useState } from "react";
import { getMyCart } from "../services/cartService";

const CartContext = createContext(null);

const calculateCartCount = (cart) => {
  const items = cart?.items || [];

  return items.reduce((total, item) => {
    const quantity = Number(item.totalQuantity ?? item.quantity ?? 1);
    return total + Math.max(quantity, 0);
  }, 0);
};

export function CartProvider({ children }) {
  const [cartCount, setCartCount] = useState(0);

  const refreshCartCount = useCallback(async () => {
    const token = sessionStorage.getItem("accessToken");

    if (!token) {
      setCartCount(0);
      return 0;
    }

    try {
      const cart = await getMyCart();
      const nextCount = calculateCartCount(cart);
      setCartCount(nextCount);
      return nextCount;
    } catch (error) {
      setCartCount(0);
      return 0;
    }
  }, []);

  const updateCartCountFromCart = useCallback((cart) => {
    const nextCount = calculateCartCount(cart);
    setCartCount(nextCount);
    return nextCount;
  }, []);

  const clearCartCount = useCallback(() => {
    setCartCount(0);
  }, []);

  const value = useMemo(
    () => ({
      cartCount,
      refreshCartCount,
      updateCartCountFromCart,
      clearCartCount,
    }),
    [cartCount, refreshCartCount, updateCartCountFromCart, clearCartCount]
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);

  if (!context) {
    throw new Error("useCart must be used inside CartProvider");
  }

  return context;
}
