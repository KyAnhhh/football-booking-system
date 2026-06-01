import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/users";
const API_URL_PUBLIC = "http://localhost:8080/api/public/users";

const handleResponse = async (response) => {
  if (!response.ok) {
    let errText = await response.text();
    throw new Error(errText || `HTTP Error ${response.status}`);
  }

  const result = await response.json();
  return result.data ?? result.message;
};

export const getUsers = async (pageNo = 0, pageSize = 10, sortBy = "id", sortDir = "asc", search = "") => {
  const headers = {
    "Content-Type": "application/json",
  };

  const url = `${API_URL_ADMIN}?pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}&search=${search}`;

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

  const response = await fetchWithAuth(`${API_URL_PUBLIC}/profile`, {
    method: "GET",
    headers,
  });

  return await handleResponse(response);
};

export const updateProfile = async (profile) => {
  const headers = {
    "Content-Type": "application/json",
  };

  const response = await fetchWithAuth(`${API_URL_PUBLIC}/profile/update`, {
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


