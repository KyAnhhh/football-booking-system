import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = `http://localhost:8080/api/public/carts`;

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }
  const result = await response.json();
  return result.data ?? result.message;
};

function getAuthHeader() {
  const token = localStorage.getItem("accessToken");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

export const getCartByUser = async () => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/items/me`, {
    method: "GET",
    headers,
  });

  return await handleResponse(response);
}

export const addItemToCart = async (request) => {
    const headers = {
         "Content-Type": "application/json",
            ...getAuthHeader(),
    }
    const response = await fetchWithAuth(`${API_URL_PUBLIC}/items`, {
        method: "POST",
        headers,
        body: JSON.stringify(request),
    });

    return await handleResponse(response);
}

export const removeCartItem = async (cartItemId) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/items/${cartItemId}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
}