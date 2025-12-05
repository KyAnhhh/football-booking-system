const API_URL = "http://localhost:8080/api/public/products";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }

  const result = await response.json();

  return result.data;
};

export const getProducts = async () => {
  const response = await fetch(API_URL);
  return await handleResponse(response);
};

export const getProductById = async (id) => {
  const response = await fetch(`${API_URL}/${id}`);
  return await handleResponse(response);
};

export const createProduct = async (product) => {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(product),
  });

  return await handleResponse(response);
};

export const updateProduct = async (id, product) => {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(product),
  });

  return await handleResponse(response);
};

export const deleteProduct = async (id) => {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
  });

  return await handleResponse(response);
};
