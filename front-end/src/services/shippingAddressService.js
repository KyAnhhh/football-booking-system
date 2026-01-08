const API_URL_PUBLIC = "http://localhost:8080/api/public/shipping-addresses";

const handleResponse = async (response) => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
    const result = await response.json();
    return result.data ?? result.message;
};

function getAuthHeader() {
    const token = localStorage.getItem("token");
    return token ? { Authorization: `Bearer ${token}` } : {};
}

export const createAddress = async (addressData, userId) => {
  const response = await fetch(API_URL_PUBLIC, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
    body: JSON.stringify({ ...addressData, userId }),
  });
  return handleResponse(response);
};

export const getAddressOfMe = async () => {
    const response = await fetch(`${API_URL_PUBLIC}/me`, {
        method: "GET",
        headers: {
            ...getAuthHeader(),
        }
    });
    return handleResponse(response);
}

export const updateAddress = async (id, addressData) => {
    const response = await fetch(`${API_URL_PUBLIC}/${id}`, {
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
    const response = await fetch(`${API_URL_PUBLIC}/${id}`, {
        method: "DELETE",
        headers: {
            ...getAuthHeader(),
        }
    });
    return handleResponse(response);
}