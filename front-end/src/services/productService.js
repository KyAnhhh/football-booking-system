const API_URL_ADMIN = "http://localhost:8080/api/admin/products";
const API_URL_PUBLIC = "http://localhost:8080/api/public/products";

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

export const getProducts = async (pageNo = 0, pageSize = 5, sortBy = "id", sortDir = "asc", search = "") => {
  const headers = {
    "Content-Type": "application/json"
  };
  const params = new URLSearchParams({
    pageNo,
    pageSize,
    sortBy,
    sortDir
  });
  if (search) params.append("search", search);
  
  const response = await fetch(`${API_URL_PUBLIC}?${params.toString()}`, {headers});
  return await handleResponse(response);
};

export const getProductById = async (id) => {
  const headers = {
    "Content-Type": "application/json"
  };
  const response = await fetch(`${API_URL_PUBLIC}/${id}`,{
    method: "GET",
    headers
  });
  return await handleResponse(response);
};

export const createProduct = async (product) => {
  // if product is FormData (file upload), let fetch set Content-Type boundary
  if (product instanceof FormData) {
    const headers = { ...getAuthHeader() };
    const response = await fetch(API_URL_ADMIN, {
      method: "POST",
      headers,
      body: product,
    });
    return await handleResponse(response);
  }

  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(API_URL_ADMIN, {
    method: "POST",
    headers,
    body: JSON.stringify(product),
  });

  return await handleResponse(response);
};

export const updateProduct = async (id, product) => {
  if (product instanceof FormData) {
    const headers = { ...getAuthHeader() };
    const response = await fetch(`${API_URL_ADMIN}/${id}`, {
      method: "PUT",
      headers,
      body: product,
    });
    return await handleResponse(response);
  }

  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(product),
  });

  return await handleResponse(response);
};

export const deleteProduct = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetch(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
