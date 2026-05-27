import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/categories";
const API_URL_PUBLIC = "http://localhost:8080/api/public/categories";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }
  const result = await response.json();
  return result.data ?? result.message;
};

export const getCategories = async (pageNo = 0, pageSize = 10, sortBy = "id", sortDir = "asc", search = "") => {
  const params = new URLSearchParams({
    pageNo,
    pageSize,
    sortBy,
    sortDir
  });
  if (search) params.append("search", search);
  
  const response = await fetchWithAuth(`${API_URL_PUBLIC}?${params.toString()}`);
  return handleResponse(response);
};

export const getAllCategoriesForDropdown = async () => {
  try {
    const response = await fetchWithAuth(`${API_URL_PUBLIC}`);
    const res = await handleResponse(response);

    if (Array.isArray(res)) return res;
    if (Array.isArray(res?.content)) return res.content;

    return [];
  } catch (err) {
    console.error("Error fetching categories:", err);
    return [];
  }
};


export const getCategoryById = async (id) => {
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/${id}`);
  return handleResponse(response);
};

export const createCategory = async (categoryData) => {
  const response = await fetchWithAuth(API_URL_ADMIN, {
    method: "POST",
    body: JSON.stringify(categoryData),
  });

  return await handleResponse(response);
};

export const updateCategory = async (id, categoryData) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    body: JSON.stringify(categoryData),
  });

  return await handleResponse(response);
};

export const deleteCategory = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
  });

  return await handleResponse(response);
};
