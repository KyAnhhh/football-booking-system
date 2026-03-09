import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/product-variants";
const API_URL_PUBLIC = "http://localhost:8080/api/public/product-variants";

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

export const createProductVariant = async (productVariant) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetchWithAuth(API_URL_ADMIN, {
    method: "POST",
    headers,
    body: JSON.stringify(productVariant),
  });

  return await handleResponse(response);
};

export const updateProductVariant = async (id, productVariant) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(productVariant),
  });

  return await handleResponse(response);
};

export const getAllProductVariant = async () => {
    const headers = {
        "Content-Type": "application/json",
        ...getAuthHeader(),
    };
    const response = await fetchWithAuth(API_URL_PUBLIC, {
        method: "GET",
        headers
    });
    return await handleResponse(response);
}

export const getProductVariant = async (id) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/${id}`,{
    method: "GET",
    headers
  });
  return await handleResponse(response);
};

export const deleteProductVariant = async (id) => {
    const headers = {
        "Content-Type": "application/json",
        ...getAuthHeader(),
    };
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
        method: "DELETE",
        headers
    });
    return await handleResponse(response);
}