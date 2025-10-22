const API_URL = "http://localhost:8080/api/admin/users";

export const getUsers = async () => {
  const res = await fetch(API_URL);
  return res.json();
};

export const getUser = async (id) => {
  const res = await fetch(`${API_URL}/${id}`);
  return res.json();
};  

//export const createUser = async (user) => {
//  return await fetch(API_URL, {
//    method: "POST",
//    headers: { "Content-Type": "application/json" },
//    body: JSON.stringify(user),
//  });
//};

export const updateUser = async (id, user) => {
  const res = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(user),
  });
  return res.json();
};

export const deleteUser = async (id) => {
  await fetch(`${API_URL}/${id}`, { method: "DELETE" });
};
