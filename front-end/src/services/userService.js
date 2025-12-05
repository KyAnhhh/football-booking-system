const API_URL = "http://localhost:8080/api/public/users";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }

  const result = await response.json();
  return result.data; 
};

export const getUsers = async () => {
  const response = await fetch(API_URL);
  return await handleResponse(response);
};

export const getUser = async (id) => {
  const response = await fetch(`${API_URL}/${id}`);
  return await handleResponse(response);
};

export const createUser = async (user) => {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(user),
  });

  return await handleResponse(response);
};

export const updateUser = async (id, user) => {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(user),
  });

  return await handleResponse(response);
};

export const deleteUser = async (id) => {
  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
  });

  return await handleResponse(response);
};