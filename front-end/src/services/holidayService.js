import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = "http://localhost:8080/api/public/holidays";
const API_URL_ADMIN = "http://localhost:8080/api/admin/holidays";

const handleResponse = async (response) => {
  if (!response.ok) {
    const text = await response.text();
    try {
      const json = JSON.parse(text);
      throw new Error(json.message || json.error || text);
    } catch {
      throw new Error(text || "Request failed");
    }
  }

  const result = await response.json();
  return result.data ?? result;
};

export const getHolidays = async () => {
  const response = await fetch(API_URL_PUBLIC);
  return handleResponse(response);
};

export const createHoliday = async (payload) => {
  const response = await fetchWithAuth(API_URL_ADMIN, {
    method: "POST",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const updateHoliday = async (id, payload) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const deleteHoliday = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
  });
  return handleResponse(response);
};
