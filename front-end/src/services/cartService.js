import { fetchWithAuth } from "./apiService";

const API_URL_USER = `http://localhost:8080/api/user/carts`;
let pendingCartRequest = null;

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }
  const result = await response.json();
  return result.data ?? result.message;
};

function getAuthHeader() {
  const token = sessionStorage.getItem("accessToken");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

export const getMyCart = async () => {
  if (pendingCartRequest) {
    return pendingCartRequest;
  }

  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };

  pendingCartRequest = fetchWithAuth(`${API_URL_USER}`, {
      method: "GET",
      headers,
    })
    .then(handleResponse)
    .finally(() => {
      pendingCartRequest = null;
    });

  return pendingCartRequest;
};

export const addItemToCart = async (request) => {
    const headers = {
         "Content-Type": "application/json",
            ...getAuthHeader(),
    }
    const response = await fetchWithAuth(`${API_URL_USER}/items`, {
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
  const response = await fetchWithAuth(`${API_URL_USER}/items/${cartItemId}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
}

export const updateCartItemQuantity = async (cartItemId, quantity) => {
  const response = await fetchWithAuth(`${API_URL_USER}/items/${cartItemId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...getAuthHeader() },
    body: JSON.stringify({ quantity }),
  });
  return handleResponse(response);
};
