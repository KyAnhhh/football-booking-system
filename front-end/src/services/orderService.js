import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = "http://localhost:8080/api/public/orders";
const API_URL_ADMIN = "http://localhost:8080/api/admin/orders";

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
  const token = localStorage.getItem("accessToken");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

export const getOrders = async (
  pageNo = 0,
  pageSize = 10,
  sortBy = "id",
  sortDir = "asc",
  search = "",
  status = ""
) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };


  let url = `${API_URL_ADMIN}?pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}`;
  

  if (search) url += `&search=${encodeURIComponent(search)}`;
  if (status) url += `&status=${encodeURIComponent(status)}`;

  const response = await fetchWithAuth(url, {
    headers,
  });

  return await handleResponse(response);
};


export const createOrder = async (Order) => { 
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetchWithAuth(API_URL_PUBLIC, {
    method: "POST",
    headers,
    body: JSON.stringify(Order),
  });

  return await handleResponse(response);
};

export const getOrderById = async (id) => {
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

export const getMyOrders = async () => {
  const headers = { 
    "Content-Type": "application/json", 
    ...getAuthHeader() };
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/me`, { headers });
  return await handleResponse(response);
};

export const updateOrder = async (id, Order) => {
  const headers = { 
    "Content-Type": "application/json",
    ...getAuthHeader() };
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(Order),
  });

  return await handleResponse(response);
};

export const updateOrderStatus = async (orderId, status) => {
  const headers = { 
    "Content-Type": "application/json", 
    ...getAuthHeader() };
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${orderId}/status`, {
    method: "PUT",
    headers,
    body: JSON.stringify( status ),
  });

  return await handleResponse(response);
};

export const deleteOrder = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};

export const cancelOrder = async (orderId) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  }
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/${orderId}/cancel`, {
    method: "PUT",
    headers,
  });
  return await handleResponse(response);
}

export const checkout = async (request) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  }
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/checkout-cart`, {
    method: "POST",
    headers,
    body: JSON.stringify(request),
  });
  return await handleResponse(response);
}