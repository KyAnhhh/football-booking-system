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

export const getPayment = async (pageNo, pageSize, sortBy, sortDir, search) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}?pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}${search ? `&search=${search}` : ''}`);
  return handleResponse(response);
}

export const deletePaymentById = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
  });
  return handleResponse(response);
}