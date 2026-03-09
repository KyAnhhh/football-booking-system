import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/product-images";
const API_URL_ADMIN_UPLOAD = "http://localhost:8080/api/admin/products";
const API_URL_PUBLIC = "http://localhost:8080/api/public/products";

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

export const uploadProductImages = async (productId, files, primaryIndex) => {
  const formData = new FormData();
  files.forEach(file => formData.append("images", file));

  if (primaryIndex !== undefined) {
    formData.append("primaryIndex", primaryIndex);
  }

  const response = await fetchWithAuth(`${API_URL_ADMIN_UPLOAD}/${productId}/images`, {
    method: "POST",
    headers: {
      // ...getAuthHeader()
      Authorization: getAuthHeader().Authorization
    },
    body: formData
  });

  return await handleResponse(response);
};

export const getImageByProduct = async (productId) => {
    const response = await fetchWithAuth(`${API_URL_PUBLIC}/${productId}/images`, {
        method: "GET",
        headers: {
      ...getAuthHeader()
        }
    });
    return await handleResponse(response);
}

export const setPrimary = async (imageId) => {
    const headers = {
        "Content-Type": "application/json",
        ...getAuthHeader(),
    };
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${imageId}/primary`, {
        method: "PUT",
        headers
    });
    return await handleResponse(response);
}

export const deleteImage = async (imageId) => {
    const headers = {
        ...getAuthHeader(),
    };
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${imageId}`, {
        method: "DELETE",
        headers
    });
    return await handleResponse(response);
}