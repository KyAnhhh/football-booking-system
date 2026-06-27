import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/orders";
const API_URL_USER = "http://localhost:8080/api/user/orders";

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

export const getOrders = async (
  pageNo = 0,
  pageSize = 10,
  sortBy = "id",
  sortDir = "asc",
  search = "",
  status = ""
) => {

  let url = `${API_URL_ADMIN}?pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}`;
  

  if (search) url += `&search=${encodeURIComponent(search)}`;
  if (status) url += `&status=${encodeURIComponent(status)}`;

  const response = await fetchWithAuth(url);

  return handleResponse(response);
};


export const createOrder = async (orderData) => { 
  const response = await fetchWithAuth(API_URL_USER, {
    method: "POST",
    body: JSON.stringify(orderData),
  });

  return handleResponse(response);
};

export const getOrderById = async (id) => {
  const response = await fetchWithAuth(`${API_URL_USER}/${id}`);
  return await handleResponse(response);
};

export const getMyOrders = async () => {
  const response = await fetchWithAuth(`${API_URL_USER}/me`);
  return handleResponse(response);
};

export const updateOrderStatus = async (orderId, status) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${orderId}/status`, {
    method: "PUT",
    body: JSON.stringify( status ),
  });

  return await handleResponse(response);
};

export const deleteOrder = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE"
  });

  return await handleResponse(response);
};

export const cancelOrder = async (orderId) => {
  const response = await fetchWithAuth(`${API_URL_USER}/${orderId}/cancel`, {
    method: "PUT",
  });
  return await handleResponse(response);
}

export const confirmReceived = async (orderId) => {
  const response = await fetchWithAuth(`${API_URL_USER}/${orderId}/confirm-receipt`, {
    method: "PUT",
  });
  return await handleResponse(response);
}

export const checkout = async (request) => {
  const response = await fetchWithAuth(`${API_URL_USER}/checkout-cart`, {
    method: "POST",
    body: JSON.stringify(request),
  });
  return await handleResponse(response);
}

export const markOrderPaymentSubmitted = async (orderId) => {
  const response = await fetchWithAuth(
    `${API_URL_USER}/${orderId}/payment-submitted`,
    { method: "PUT" }
  );
  return handleResponse(response);
};
