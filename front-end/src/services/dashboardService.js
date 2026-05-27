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
    const response = await fetch(`${API_URL}/summary`, {
        method: "GET",
    });
    return handleResponse(response);
}

export const getMonthlyRevenue = async () => {
    const response = await fetch(`${API_URL}/monthly-revenue`, {
        method: "GET",
    });
    return handleResponse(response);
}