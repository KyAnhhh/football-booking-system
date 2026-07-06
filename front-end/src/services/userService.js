import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/users";
const API_URL_PROFILE = "http://localhost:8080/api/user/profile";

const handleResponse = async (response) => {
  if (!response.ok) {
    let errText = await response.text();
    throw new Error(errText || `HTTP Error ${response.status}`);
  }

  const result = await response.json();
  return result.data ?? result.message;
};

export const getUsers = async ({
  pageNo = 0,
  pageSize = 10,
  sortBy = "id",
  sortDir = "asc",
  search = "",
  role = "",
  status = "",
} = {}) => {
  const headers = {
    "Content-Type": "application/json",
  };

  const params = new URLSearchParams({
    pageNo,
    pageSize,
    sortBy,
    sortDir,
  });

  if (search) params.append("search", search);
  if (role) params.append("role", role);
  if (status) params.append("status", status);

  const url = `${API_URL_ADMIN}?${params.toString()}`;

  const response = await fetchWithAuth(url, { headers });
  return await handleResponse(response);
};

export const createUser = async (user) => {
  const headers = {
    "Content-Type": "application/json",
  };
  const response = await fetchWithAuth(`${API_URL_ADMIN}`, {
    method: "POST",
    headers,
    body: JSON.stringify(user),
  });

  return await handleResponse(response);
};

export const updateUser = async (id, user) => {
  const headers = {
    "Content-Type": "application/json",
  };

  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(user),
  });

  return await handleResponse(response);
};

export const updateUserRole = async (id, role) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}/role`, {
    method: "PUT",
    body: JSON.stringify({ role }),
  });

  return await handleResponse(response);
};

export const updateUserStatus = async (id, status) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}/status`, {
    method: "PUT",
    body: JSON.stringify({ status }),
  });

  return await handleResponse(response);
};

export const deleteUser = async (id) => {
  const headers = {
    "Content-Type": "application/json",
  };

  const response = await fetchWithAuth(`${API_URL_ADMIN}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};

export const getProfile = async () => {
  const headers = {
    "Content-Type": "application/json",
  };

  const response = await fetchWithAuth(API_URL_PROFILE, {
    method: "GET",
    headers,
  });

  return await handleResponse(response);
};

export const updateProfile = async (profile) => {
  const headers = {
    "Content-Type": "application/json",
  };

  const response = await fetchWithAuth(API_URL_PROFILE, {
    method: "PUT",
    headers,
    body: JSON.stringify({
      username: profile.username,
      email: profile.email,
      phone: profile.phone
    })
  });

  return await handleResponse(response);
};


