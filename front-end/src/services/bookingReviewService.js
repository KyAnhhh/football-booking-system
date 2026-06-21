import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = "http://localhost:8080/api/public/booking-reviews";
const API_URL_USER = "http://localhost:8080/api/user/booking-reviews";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();

    try {
      const errJson = JSON.parse(errText);
      throw new Error(errJson.message || errJson.error);
    } catch {
      throw new Error(errText || "Server error");
    }
  }

  const result = await response.json();
  return result.data ?? result.message;
};

export const createBookingReview = async (bookingId, reviewData) => {
  const response = await fetchWithAuth(`${API_URL_USER}/${bookingId}`, {
    method: "POST",
    body: JSON.stringify(reviewData),
  });

  return handleResponse(response);
};

export const getFieldReviews = async (fieldId) => {
  const response = await fetch(`${API_URL_PUBLIC}/field/${fieldId}`);

  return handleResponse(response);
};

export const getFieldReviewSummary = async (fieldId) => {
  const response = await fetch(`${API_URL_PUBLIC}/field/${fieldId}/summary`);

  return handleResponse(response);
};

export const getMyReviews = async () => {
  const response = await fetchWithAuth(`${API_URL_USER}/my-reviews`);

  return handleResponse(response);
};
