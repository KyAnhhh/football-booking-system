import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/admin/calendar";

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

export const getAdminFieldCalendar = async (date) => {
  const params = new URLSearchParams({ date });
  const response = await fetchWithAuth(`${API_URL}/fields?${params.toString()}`);
  return handleResponse(response);
};
