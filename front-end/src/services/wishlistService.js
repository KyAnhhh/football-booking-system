import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/user/wishlist";

const handleResponse = async (response) => {
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Không thể xử lý danh sách yêu thích");
  }
  const result = await response.json();
  return result.data ?? result.message;
};

export const getWishlist = async () =>
  handleResponse(await fetchWithAuth(API_URL));

export const addToWishlist = async (productId) =>
  handleResponse(
    await fetchWithAuth(`${API_URL}/${productId}`, { method: "POST" })
  );

export const removeFromWishlist = async (productId) =>
  handleResponse(
    await fetchWithAuth(`${API_URL}/${productId}`, { method: "DELETE" })
  );

export const isInWishlist = async (productId) => {
  const result = await handleResponse(
    await fetchWithAuth(`${API_URL}/${productId}/exists`)
  );
  return Boolean(result?.exists);
};
