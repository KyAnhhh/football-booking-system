import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/user/notifications";

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

export const getNotifications = async (limit = 10) => {
  const response = await fetchWithAuth(`${API_URL}?limit=${limit}`);
  return handleResponse(response);
};

export const getUnreadNotificationCount = async () => {
  const response = await fetchWithAuth(`${API_URL}/unread-count`);
  return handleResponse(response);
};

export const markNotificationAsRead = async (notificationId) => {
  const response = await fetchWithAuth(`${API_URL}/${notificationId}/read`, {
    method: "PATCH",
  });
  return handleResponse(response);
};

export const markAllNotificationsAsRead = async () => {
  const response = await fetchWithAuth(`${API_URL}/read-all`, {
    method: "PATCH",
  });
  return handleResponse(response);
};
