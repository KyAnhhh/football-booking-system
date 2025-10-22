const API_URL = "http://localhost:8080/api/admin/users";

const getAuthHeader = () => {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
    };
};

export const getUsers = async () => {
  const res = await fetch(API_URL, {
    headers: getAuthHeader(),
  });
  if(!res.ok) throw new Error("Không thể lấy danh sách user")
  return res.json();
};

export const getUser = async (id) => {
  const res = await fetch(`${API_URL}/${id}, {
    headers: getAuthHeader(),
  }`);
  if(!res.ok) throw new Error("Không thể lấy user có id: " + id)
//  return res.json();
};  

export const createUser = async (user) => {
  return await fetch(API_URL, {
    method: "POST",
    headers: getAuthHeader(),
    body: JSON.stringify(user),
  });
//  if (!res.ok) throw new Error("Không thể tạo user mới");
//    return res.json();
};

export const updateUser = async (id, user) => {
  const res = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: getAuthHeader(),
    body: JSON.stringify(user),
  });
  if(!res.ok) throw new Error("Không thể update user")
  return res.json();
};

export const deleteUser = async (id) => {
  await fetch(`${API_URL}/${id}`, {
      method: "DELETE" ,
      headers: getAuthHeader(),
  });
//  if(!res.ok) throw new Error("Không thể xóa user")
};
