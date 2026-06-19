import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/admin/pricing-rules";

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

export const getPricingRulesByField = async (fieldId) => {
  const response = await fetchWithAuth(`${API_URL}/field/${fieldId}`);
  return handleResponse(response);
};

export const getPricingRules = async () => {
  const response = await fetchWithAuth(API_URL);
  return handleResponse(response);
};

export const createPricingRule = async (payload) => {
  const response = await fetchWithAuth(API_URL, {
    method: "POST",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const updatePricingRule = async (id, payload) => {
  const response = await fetchWithAuth(`${API_URL}/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const deletePricingRule = async (id) => {
  const response = await fetchWithAuth(`${API_URL}/${id}`, {
    method: "DELETE",
  });
  return handleResponse(response);
};
