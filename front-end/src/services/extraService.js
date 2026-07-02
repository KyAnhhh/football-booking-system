import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = "http://localhost:8080/api/public/extra-services";
const API_URL_ADMIN = "http://localhost:8080/api/admin/extra-services";

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

export const getExtraServices = async () => {
  const response = await fetch(API_URL_PUBLIC);
  return handleResponse(response);
};

export const getAdminExtraServices = async () => {
  const response = await fetchWithAuth(API_URL_ADMIN);
  return handleResponse(response);
};

export const createExtraService = async (payload) => {
  const response = await fetchWithAuth(API_URL_ADMIN, {
    method: "POST",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const updateExtraService = async (id, payload) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
  return handleResponse(response);
};

export const deleteExtraService = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
  });
  if (response.status === 204 || response.ok) return true;
  return handleResponse(response);
};

export const checkExtraServicePublication = async (id) => {
  const response = await fetchWithAuth(
    `${API_URL_ADMIN}/${id}/publication-check`
  );
  return handleResponse(response);
};

export const publishExtraService = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}/publish`, {
    method: "POST",
  });
  return handleResponse(response);
};

export const unpublishExtraService = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}/unpublish`, {
    method: "POST",
  });
  return handleResponse(response);
};
