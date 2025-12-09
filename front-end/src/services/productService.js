const API_URL = "http://localhost:8080/api/admin/products";

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

export const getProducts = async () => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetch(API_URL, {headers});
  return await handleResponse(response);
};

export const getProductById = async (id) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetch(`${API_URL}/${id}`,{
    method: "GET",
    headers
  });
  return await handleResponse(response);
};

export const createProduct = async (product) => {
  // if product is FormData (file upload), let fetch set Content-Type boundary
  if (product instanceof FormData) {
    const headers = { ...getAuthHeader() };
    const response = await fetch(API_URL, {
      method: "POST",
      headers,
      body: product,
    });
    return await handleResponse(response);
  }

  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(API_URL, {
    method: "POST",
    headers,
    body: JSON.stringify(product),
  });

  return await handleResponse(response);
};

export const updateProduct = async (id, product) => {
  if (product instanceof FormData) {
    const headers = { ...getAuthHeader() };
    const response = await fetch(`${API_URL}/${id}`, {
      method: "PUT",
      headers,
      body: product,
    });
    return await handleResponse(response);
  }

  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(product),
  });

  return await handleResponse(response);
};

export const deleteProduct = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
