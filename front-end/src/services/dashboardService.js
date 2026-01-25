const API_URL = "http://localhost:8080/api/admin/dashboard-summary";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }
  const result = await response.json();
  return result.data ?? result.message;
};

function getAuthHeader() {
  const token = localStorage.getItem("accessToken");
  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

export const getDashboardSummary = async () => {
    const response = await fetch(API_URL, {
        method: "GET",
        headers: getAuthHeader(),
    });
    return handleResponse(response);
}