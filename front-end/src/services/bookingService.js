import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = "http://localhost:8080/api/public/bookings";
const API_URL_ADMIN = "http://localhost:8080/api/admin/bookings";
const API_URL_USER = "http://localhost:8080/api/user/bookings";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();

    try {
      const errJson = JSON.parse(errText);
      throw new Error(errJson.message || errJson.error);
    } catch {
      throw new Error(errText || "Lỗi không xác định từ server");
    }
  }

  const result = await response.json();
  return result.data ?? result.message;
};

export const createBooking = async (bookingData) => {
    const response = await fetchWithAuth(API_URL_USER, {
        method: "POST",
        body: JSON.stringify(bookingData),
    });
    return handleResponse(response);
}

export const getBookings = async ({
    page = 0,
    size = 10,
    keyword,
    status,
}) => {
    const params = new URLSearchParams({
        page,
        size,
    });
    if(keyword) params.append("keyword", keyword);
    if(status) params.append("status", status);
    const response = await fetchWithAuth(`${API_URL_ADMIN}?${params.toString()}`);
    return handleResponse(response);
}

export const getMyBookings = async () => {
    const response = await fetchWithAuth(`${API_URL_USER}`);
    return handleResponse(response);
}

export const confirmBooking = async (bookingId) => {
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${bookingId}/confirm`, {
        method: "PATCH",
    });
    return handleResponse(response);
}

export const cancelBooking = async (bookingId) => {
    const response = await fetchWithAuth(`${API_URL_USER}/${bookingId}/cancel`, {
        method: "PATCH",
    });
    return handleResponse(response);
}

export const deleteBooking = async (bookingId) => {
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${bookingId}`, {
        method: "DELETE",
    });
    return handleResponse(response);
}