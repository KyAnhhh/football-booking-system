import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/payments";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    let errorMsg = "Lỗi không xác định từ server";
    
    if (errText) {
      try {
        const errorJson = JSON.parse(errText);
        errorMsg = errorJson.message || errorJson.error || errText;
      } catch (e) {
        // Nếu không phải JSON, dùng plain text
        errorMsg = errText;
      }
    }
    
    throw new Error(errorMsg);
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

export const getPayment = async (pageNo, pageSize, sortBy, sortDir, search) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetchWithAuth(`${API_URL_ADMIN}?pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}${search ? `&search=${search}` : ''}`, {
    method: "GET",
    headers,
  });
  return await handleResponse(response);
}

export const deletePaymentById = async (id) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });
  return await handleResponse(response);
}