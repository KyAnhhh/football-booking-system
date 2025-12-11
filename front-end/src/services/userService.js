const API_URL = "http://localhost:8080/api/admin/users";

const handleResponse = async (response) => {
  if (!response.ok) {
    let errText = await response.text();
    throw new Error(errText || `HTTP Error ${response.status}`);
  }

  const result = await response.json();
  return result.data; 
};

function getAuthHeader() {
  const token = localStorage.getItem("token");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

// export const getUsers = async () => {
//   const headers = {
//     "Content-Type": "application/json",
//     ...getAuthHeader(),
//   };

//   const response = await fetch(API_URL, { headers });
//   return await handleResponse(response);
// };

export const getUsers = async (pageNo = 0, pageSize = 5, sortBy = "id", sortDir = "asc", search = "") => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };

  const url = `${API_URL}?pageNo=${pageNo}&pageSize=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}&search=${search}`;

  const response = await fetch(url, { headers });
  return await handleResponse(response);
};


export const getUser = async (id) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };

  const response = await fetch(`${API_URL}/${id}`, {
    method: "GET",
    headers,
  });

  return await handleResponse(response);
};

export const createUser = async (user) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };
  const response = await fetch(`${API_URL}`, {
    method: "POST",
    headers,
    body: JSON.stringify(user),
  });

  return await handleResponse(response);
};

export const updateUser = async (id, user) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };

  const response = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers,
    body: JSON.stringify(user),
  });

  return await handleResponse(response);
};

export const deleteUser = async (id) => {
  const headers = {
    "Content-Type": "application/json",
    ...getAuthHeader(),
  };

  const response = await fetch(`${API_URL}/${id}`, {
    method: "DELETE",
    headers,
  });

  return await handleResponse(response);
};
