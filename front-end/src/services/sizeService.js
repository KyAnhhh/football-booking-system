import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/sizes";
const API_URL_PUBLIC = "http://localhost:8080/api/public/sizes";

const handleResponse = async (response) => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
    const result = await response.json();
    return result.data ?? result.message;
};

function getAuthHeader() {
    const token = sessionStorage.getItem("accessToken");
    return token ? { Authorization: `Bearer ${token}` } : {};
}

export const getSizes = async () => {
    const headers = {
        "Content-Type": "application/json",
    }
    const response = await fetch(`${API_URL_PUBLIC}`, { headers });
    return handleResponse(response);
}

export const createSize = async (sizeRequest) => {
    const headers = {
        "Content-Type": "application/json",
        ...getAuthHeader()
    }
    const response = await fetchWithAuth(`${API_URL_ADMIN}`, {
        method: "POST",
        headers,
        body: JSON.stringify(sizeRequest)
    });
    return handleResponse(response);
}

export const updateSize = async (id, sizeRequest) => {
    const headers = {
        "Content-Type": "application/json",
        ...getAuthHeader()
    }
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
        method: "PUT",
        headers,
        body: JSON.stringify(sizeRequest)
    });
    return handleResponse(response);
}

export const getSizeById = async (id) => {
    const headers = {
        "Content-Type": "application/json",
    }
    const response = await fetch(`${API_URL_PUBLIC}/${id}`, {
        method: "GET",
        headers
    });
    return handleResponse(response);
}

export const deleteSize = async (id) => {
    const headers = {
        "Content-Type": "application/json",
        ...getAuthHeader()
    }
    const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
        method: "DELETE",
        headers
    });
    return handleResponse(response);
}