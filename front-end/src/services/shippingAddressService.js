import { fetchWithAuth } from "./apiService";

const API_URL_USER = "http://localhost:8080/api/user/shipping-addresses";

const handleResponse = async (response) => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
    const result = await response.json();
    return result.data ?? result.message;
};

function getAuthHeader() {
    const token = sessionStorage.getItem("accessToken");
    return token ? { Authorization: `Bearer ${token}` } : {};
}

export const createAddress = async (addressData, userId) => {
  const response = await fetchWithAuth(API_URL_USER, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
    body: JSON.stringify({ ...addressData, userId }),
  });
  return handleResponse(response);
};

export const getMyAddress = async () => {
    const response = await fetchWithAuth(`${API_URL_USER}/me`, {
        method: "GET",
        headers: {
            ...getAuthHeader(),
        }
    });
    if(response.status === 404) {
        return null; // Trả về null nếu không tìm thấy địa chỉ giao hàng nào
    }
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return handleResponse(response);
}

export const updateAddress = async (id, addressData) => {
    const response = await fetchWithAuth(`${API_URL_USER}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            ...getAuthHeader(),
        },
        body: JSON.stringify(addressData),
    });
    return handleResponse(response);
}

export const deleteAddress = async (id) => {
    const response = await fetchWithAuth(`${API_URL_USER}/${id}`, {
        method: "DELETE",
        headers: {
            ...getAuthHeader(),
        }
    });
    return handleResponse(response);
}