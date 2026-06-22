import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/admin/holiday-pricing-rules";

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

export const getHolidayPricingRules = async ({ fieldId, holidayId } = {}) => {
  const params = new URLSearchParams();
  if (fieldId) params.append("fieldId", fieldId);
  if (holidayId) params.append("holidayId", holidayId);

  const query = params.toString();
  const response = await fetchWithAuth(query ? `${API_URL}?${query}` : API_URL);
  return handleResponse(response);
};

export const createHolidayPricingRule = async (payload) => {
  const response = await fetchWithAuth(API_URL, {
    method: "POST",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const updateHolidayPricingRule = async (id, payload) => {
  const response = await fetchWithAuth(`${API_URL}/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const deleteHolidayPricingRule = async (id) => {
  const response = await fetchWithAuth(`${API_URL}/${id}`, {
    method: "DELETE",
  });
  return handleResponse(response);
};
