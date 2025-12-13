const API_URL_ADMIN = "http://localhost:8080/api/admin/brands";
const API_URL_PUBLIC = "http://localhost:8080/api/public/brands";

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

export const getBrands = async (pageNo = 0, pageSize = 5, sortBy = "id", sortDir = "asc", search = "") => {
  const headers = {
    "Content-Type": "application/json",
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

// Get all brands for dropdown (no pagination, public endpoint)
export const getAllBrandsForDropdown = async () => {
  const headers = {
    "Content-Type": "application/json",
  };
  try {
    const response = await fetch(`${API_URL_PUBLIC}`, {headers});
    const data = await handleResponse(response);
    return data.content || [];
  } catch (err) {
    console.error("Error fetching brands:", err);
    return [];
  }
};

export const getBrandById = async (id) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetch(`${API_URL_ADMIN}/${id}`,{
    method: "GET",
    headers
  });
  return await handleResponse(response);
};

export const createBrand = async (Brand) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(API_URL_ADMIN, {
    method: "POST",
    headers,
    body: JSON.stringify(Brand),
  });

  return await handleResponse(response);
};

export const updateBrand = async (id, Brand) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(Brand),
  });

  return await handleResponse(response);
};

export const deleteBrand = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetch(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
