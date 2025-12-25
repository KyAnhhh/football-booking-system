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

function getAuthHeader() {
  const token = localStorage.getItem("token");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

export const getCategories = async (pageNo = 0, pageSize = 5, sortBy = "id", sortDir = "asc", search = "") => {
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

// Get all categories for dropdown (no pagination, public endpoint) 
export const getAllCategoriesForDropdown = async () => {
  try {
    const response = await fetch(`${API_URL_PUBLIC}`);
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

export const createCategory = async (Category) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(API_URL_ADMIN, {
    method: "POST",
    headers,
    body: JSON.stringify(Category),
  });

  return await handleResponse(response);
};

export const updateCategory = async (id, Category) => {
  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetch(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(Category),
  });

  return await handleResponse(response);
};

export const deleteCategory = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetch(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
