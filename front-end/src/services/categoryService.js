const API_URL = "http://localhost:8080/api/admin/categories";

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

export const getCategories = async () => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetch(API_URL, {headers});
  return await handleResponse(response);
};

export const getCategoryById = async (id) => {
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

export const createCategory = async (Category) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(API_URL, {
    method: "POST",
    headers,
    body: JSON.stringify(Category),
  });

  return await handleResponse(response);
};

export const updateCategory = async (id, Category) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(Category),
  });

  return await handleResponse(response);
};

export const deleteCategory = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
