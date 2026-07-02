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

export const confirmBookingPayment = async (bookingId) => {
  const response = await fetchWithAuth(
    `${API_URL_ADMIN}/bookings/${bookingId}/confirm`,
    { method: "PUT" }
  );
  return handleResponse(response);
}

export const confirmOrderPayment = async (orderId) => {
  const response = await fetchWithAuth(
    `${API_URL_ADMIN}/orders/${orderId}/confirm`,
    { method: "PUT" }
  );
  return handleResponse(response);
}
