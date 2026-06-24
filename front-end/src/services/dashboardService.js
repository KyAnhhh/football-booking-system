import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/public/dashboard";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Lỗi không xác định từ server");
  }

  const result = await response.json();
  return result.data ?? result.message;
};

export const getDashboardSummary = async () => {
  const response = await fetchWithAuth(`${API_URL}/summary`, {
    method: "GET",
  });

  return handleResponse(response);
};

export const getMonthlyRevenue = async () => {
  const response = await fetchWithAuth(`${API_URL}/monthly-revenue`, {
    method: "GET",
  });

  return handleResponse(response);
};

export const getAdminOverview = async ({
  bookingStatus = "",
  orderStatus = "",
} = {}) => {
  const params = new URLSearchParams();

  if (bookingStatus) {
    params.append("bookingStatus", bookingStatus);
  }

  if (orderStatus) {
    params.append("orderStatus", orderStatus);
  }

  const query = params.toString();
  const response = await fetchWithAuth(
    `${API_URL}/admin-overview${query ? `?${query}` : ""}`,
    {
      method: "GET",
    }
  );

  return handleResponse(response);
};
