const API_URL_PUBLIC = "http://localhost:8080/api/public/orders";
const API_URL_ADMIN = "http://localhost:8080/api/admin/orders";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }
  const result = await response.json();

  return result.data;
};

function getAuthHeader() {
  const token = localStorage.getItem("token");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

export const getOrders = async () => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetch(API_URL_ADMIN, {headers});
  return await handleResponse(response);
};

export const createOrder = async (Order) => { 
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(API_URL_PUBLIC, {
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
  const response = await fetch(`${API_URL_PUBLIC}/${id}`,{
    method: "GET",
    headers
  });
  return await handleResponse(response);
};

export const getMyOrders = async () => {
  const headers = { 
    "Content-Type": "application/json", 
    ...getAuthHeader() };
  const response = await fetch(`${API_URL_PUBLIC}/my`, { headers });
  return await handleResponse(response);
};

export const updateOrder = async (id, Order) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(`${API_URL_PUBLIC}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(Order),
  });

  return await handleResponse(response);
};

export const deleteOrder = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetch(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
