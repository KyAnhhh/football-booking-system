import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/admin/membership-tiers";

const handleResponse = async (response) => {
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || "Không thể xử lý hạng thành viên");
  }
  if (response.status === 204) return null;
  const result = await response.json();
  return result.data ?? result;
};

export const getMembershipTiers = async () =>
  handleResponse(await fetchWithAuth(API_URL));

export const createMembershipTier = async (payload) =>
  handleResponse(
    await fetchWithAuth(API_URL, {
      method: "POST",
      body: JSON.stringify(payload),
    })
  );

export const updateMembershipTier = async (id, payload) =>
  handleResponse(
    await fetchWithAuth(`${API_URL}/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    })
  );

export const deleteMembershipTier = async (id) => {
  const response = await fetchWithAuth(`${API_URL}/${id}`, {
    method: "DELETE",
  });
  if (response.ok) return true;
  return handleResponse(response);
};
