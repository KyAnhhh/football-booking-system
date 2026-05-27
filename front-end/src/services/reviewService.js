import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = "http://localhost:8080/api/public";
const API_URL_ADM = "http://localhost:8080/api/admin";
const API_URL_USER = "http://localhost:8080/api/user";

const handleResponse = async (response) => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
    const result = await response.json();
    return result.data ?? result.message;
};

export const createReview = async (orderItemId, reviewData) => {
  const response = await fetchWithAuth(`${API_URL_USER}/order-items/${orderItemId}/reviews`, {
    method: "POST",
    body: JSON.stringify({ ...reviewData }),
  });
  return handleResponse(response);
}

export const getReviewsByProductId = async (productId) => {
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/products/${productId}/reviews`, {
    method: "GET",
    headers: {
    },
  });
  return handleResponse(response);
}  

export const getAverageRating = async (productId) => {
  const response = await fetchWithAuth(`${API_URL_PUBLIC}/products/${productId}/average-rating`, {
    method: "GET",
    headers: {
    },
  });
  return handleResponse(response);
}

export const checkCanReview = async (orderItemId) => {
  const response = await fetchWithAuth(`${API_URL_USER}/order-items/${orderItemId}/can-review`);
  return handleResponse(response);
}

export const deleteReview = async (reviewId) => {
  const response = await fetchWithAuth(`${API_URL_ADM}/reviews/${reviewId}`, {
    method: "DELETE",
  });
  return handleResponse(response);
}