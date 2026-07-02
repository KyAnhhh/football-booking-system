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

export const getProducts = async (
  pageNo,
  pageSize,
  sortBy = "id",
  sortDir = "asc",
  search = "",
  category = "",
  brand = "",
  minPrice = null,
  maxPrice = null
) => {

const params = new URLSearchParams({
  pageNo: pageNo,
  pageSize: pageSize,
  sortBy: sortBy,
  sortDir: sortDir
});

  if (search) params.append("search", search);
  if (category) params.append("category", category);
  if (brand) params.append("brand", brand);
  if (minPrice !== null) params.append("minPrice", minPrice);
  if (maxPrice !== null) params.append("maxPrice", maxPrice);

  const response = await fetch(
    `${API_URL_PUBLIC}?${params.toString()}`,
  );

  return handleResponse(response);
};

export const getAdminProducts = async (
  pageNo,
  pageSize,
  sortBy = "id",
  sortDir = "asc",
  search = ""
) => {
  const params = new URLSearchParams({ pageNo, pageSize, sortBy, sortDir });
  if (search) params.append("search", search);
  const response = await fetchWithAuth(
    `${API_URL_ADMIN}?${params.toString()}`
  );
  return handleResponse(response);
};

export const checkProductPublication = async (id) => {
  const response = await fetchWithAuth(
    `${API_URL_ADMIN}/${id}/publication-check`
  );
  return handleResponse(response);
};

export const publishProduct = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}/publish`, {
    method: "POST",
  });
  return handleResponse(response);
};

export const unpublishProduct = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}/unpublish`, {
    method: "POST",
  });
  return handleResponse(response);
};

export const getTop4PopularProducts = async () => {
  const response = await fetch(`${API_URL_PUBLIC}/top4-popular`);
  return handleResponse(response);
}

export const getProductById = async (id) => {
  const response = await fetch(`${API_URL_PUBLIC}/${id}`);
  return handleResponse(response);
};

export const createProduct = async (product) => {
    const response = await fetchWithAuth(API_URL_ADMIN, {
      method: "POST",
      body: JSON.stringify(product),
    });
    return handleResponse(response);

};

export const updateProduct = async (id, product) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    body: JSON.stringify(product),
  });

  return handleResponse(response);
};

export const deleteProduct = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
  });

  return handleResponse(response);
};
