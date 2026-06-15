import { fetchWithAuth } from "./apiService";

const API_URL_PUBLIC = "http://localhost:8080/api/public/fields";
const API_URL_ADMIN = "http://localhost:8080/api/admin/fields";
const API_URL_MAINTENANCE_ADMIN = "http://localhost:8080/api/admin/maintenance";
const API_URL_MAINTENANCE_PUBLIC = "http://localhost:8080/api/public/fields";

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

export const createField = async (fieldData) => {
    const response = await fetchWithAuth(API_URL_ADMIN, {
        method: "POST",
        body: JSON.stringify(fieldData),
    });
    return await handleResponse(response);
};

export const getFields = async ({
    page = 0,
    size = 10,
    keyword,
    status,
    type,
    minPrice,
    maxPrice
}) => {
    const params = new URLSearchParams({
        page,
        size,
    });
    if(keyword) params.append("keyword", keyword);
    if(status) params.append("status", status);
    if(type) params.append("type", type);
    if(minPrice) params.append("minPrice", minPrice);
    if(maxPrice) params.append("maxPrice", maxPrice);

    const response = await fetch(`${API_URL_PUBLIC}?${params.toString()}`);
    
    return await handleResponse(response);
}

export const getFieldById = async (id) => {
    const response = await fetch(`${API_URL_PUBLIC}/${id}`);
    return await handleResponse(response);
}

export const getAvailableSlots = async (fieldId, date) => {
    const params = new URLSearchParams({date});
    const response = await fetch(`${API_URL_PUBLIC}/${fieldId}/slots?${params.toString()}`);
    return await handleResponse(response);
}

export const updateFieldStatus = async (id, status) => {
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}/status`, {
        method: "PUT",
        body: JSON.stringify({ status }),
    });
    return await handleResponse(response);
}

export const updateField = async (id, fieldData) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    body: JSON.stringify(fieldData),
  });
  return await handleResponse(response);
}

export const deleteField = async (id) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
  });
  return await handleResponse(response);
}

// Maintenance APIs
export const createMaintenance = async (maintenanceData) => {
  const response = await fetchWithAuth(API_URL_MAINTENANCE_ADMIN, {
    method: "POST",
    body: JSON.stringify(maintenanceData),
  });
  return await handleResponse(response);
};

export const getMaintenances = async ({
  page = 0,
  size = 10,
  keyword
}) => {
  const params = new URLSearchParams({
    page,
    size,
  });
  if (keyword) params.append("keyword", keyword);

  const response = await fetchWithAuth(`${API_URL_MAINTENANCE_ADMIN}?${params.toString()}`);
  return await handleResponse(response);
};

export const getMaintenanceById = async (id) => {
  const response = await fetchWithAuth(`${API_URL_MAINTENANCE_ADMIN}/${id}`);
  return await handleResponse(response);
};

export const getMaintenancesByFieldId = async (fieldId) => {
  const response = await fetchWithAuth(`${API_URL_MAINTENANCE_ADMIN}/field/${fieldId}`);
  return await handleResponse(response);
};

export const updateMaintenance = async (id, maintenanceData) => {
  const response = await fetchWithAuth(`${API_URL_MAINTENANCE_ADMIN}/${id}`, {
    method: "PUT",
    body: JSON.stringify(maintenanceData),
  });
  return await handleResponse(response);
};

export const deleteMaintenance = async (id) => {
  const response = await fetchWithAuth(`${API_URL_MAINTENANCE_ADMIN}/${id}`, {
    method: "DELETE",
  });
  return await handleResponse(response);
};

export const getMaintenanceStatus = async (fieldId, checkTime) => {
  const params = new URLSearchParams();
  if (checkTime) params.append("checkTime", checkTime);
  const response = await fetch(`${API_URL_MAINTENANCE_PUBLIC}/${fieldId}/maintenance-status?${params.toString()}`);
  return await handleResponse(response);
};
