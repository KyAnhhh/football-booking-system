import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/products";
const API_URL_PUBLIC = "http://localhost:8080/api/public/products";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
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

// export const getProducts = async (pageNo = 0, pageSize = 12, sortBy = "id", sortDir = "asc", search = "") => {
//   const headers = {
//     "Content-Type": "application/json"
//   };
//   const params = new URLSearchParams({
//     pageNo,
//     pageSize,
//     sortBy,
//     sortDir
//   });
//   if (search) params.append("search", search);
  
//   const response = await fetchWithAuth(`${API_URL_PUBLIC}?${params.toString()}`, {headers});
//   return await handleResponse(response);
// };

export const getProducts = async (
  pageNo,
  pageSize,
  sortBy = "id",
  sortDir = "asc",
  search = ""
) => {
  const headers = {
    "Content-Type": "application/json"
  };

const params = new URLSearchParams({
  pageNo: pageNo,
  pageSize: pageSize,
  sortBy: sortBy,
  sortDir: sortDir
});

  if (search) params.append("search", search);

  const response = await fetchWithAuth(
    `${API_URL_PUBLIC}?${params.toString()}`,
    { headers }
  );

  return await handleResponse(response);
};

export const getProductById = async (id) => {
  const headers = {
    "Content-Type": "application/json"
  };
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/${id}`,{
    method: "GET",
    headers
  });
  return await handleResponse(response);
};

export const createProduct = async (product) => {

    const headers = {
      "Content-Type": "application/json",
      ...getAuthHeader()
    };
    const response = await fetchWithAuth(API_URL_ADMIN, {
      method: "POST",
      headers,
      body: JSON.stringify(product),
    });
    return await handleResponse(response);

};

export const updateProduct = async (id, product) => {

  const headers = { "Content-Type": "application/json", ...getAuthHeader() };
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(product),
  });

  return await handleResponse(response);
};

export const deleteProduct = async (id) => {
  const headers = { ...getAuthHeader() };
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
