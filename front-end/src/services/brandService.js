import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/brands";
const API_URL_PUBLIC = "http://localhost:8080/api/public/brands";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }
  const result = await response.json();
  return result.data ?? result.message;
};

export const getBrands = async (pageNo = 0, pageSize = 5, sortBy = "id", sortDir = "asc", search = "") => {
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

// Get all brands for dropdown (no pagination, public endpoint)
export const getAllBrandsForDropdown = async () => {
  try {
    const response = await fetchWithAuth(`${API_URL_PUBLIC}`);
    const res = await handleResponse(response);

    // res có thể là Page hoặc List
    if (Array.isArray(res)) return res;
    if (Array.isArray(res?.content)) return res.content;

    return [];
  } catch (err) {
    console.error("Error fetching brands:", err);
    return [];
  }
};


export const getBrandById = async (id) => {
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/${id}`);
  return await handleResponse(response);
};

export const createBrand = async (Brand) => {
  const response = await fetchWithAuth(API_URL_ADMIN, {
    method: "POST",
    body: JSON.stringify(Brand),
  });

  return await handleResponse(response);
};

export const updateBrand = async (id, Brand) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    body: JSON.stringify(Brand),
  });

  return await handleResponse(response);
};

export const deleteBrand = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE"
  });

  return await handleResponse(response);
};
