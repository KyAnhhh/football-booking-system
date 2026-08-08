import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/admin/reviews";

const handleResponse = async (response) => {
  const result = await response.json();
  if (!response.ok) throw new Error(result.message || "Không thể xử lý đánh giá.");
  return result.data;
};

export const getAdminReviews = async (type = "") => {
  const query = type ? `?type=${encodeURIComponent(type)}` : "";
  return handleResponse(await fetchWithAuth(`${API_URL}${query}`));
};

export const deleteAdminReview = async (type, reviewId) =>
  handleResponse(await fetchWithAuth(`${API_URL}/${type}/${reviewId}`, { method: "DELETE" }));
