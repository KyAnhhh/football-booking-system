import { fetchWithAuth } from "./apiService";

const API_URL_ADMIN = "http://localhost:8080/api/admin/refunds";
const API_URL_USER = "http://localhost:8080/api/user/refunds";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    let errorMsg = "Lỗi không xác định từ server";

    if (errText) {
      try {
        const errorJson = JSON.parse(errText);
        errorMsg = errorJson.message || errorJson.error || errText;
      } catch {
        errorMsg = errText;
      }
    }

    throw new Error(errorMsg);
  }

  const result = await response.json();
  return result.data ?? result.message;
};

export const getMyRefunds = async () => {
  const response = await fetchWithAuth(API_URL_USER);
  return handleResponse(response);
};

export const submitRefundBankAccount = async (refundId, request) => {
  const response = await fetchWithAuth(`${API_URL_USER}/${refundId}/bank-account`, {
    method: "PUT",
    body: JSON.stringify(request),
  });
  return handleResponse(response);
};

export const getAdminRefunds = async ({
  pageNo = 0,
  pageSize = 10,
  sortBy = "createdAt",
  sortDir = "desc",
  status = "",
} = {}) => {
  const params = new URLSearchParams({
    pageNo,
    pageSize,
    sortBy,
    sortDir,
  });

  if (status) params.append("status", status);

  const response = await fetchWithAuth(`${API_URL_ADMIN}?${params.toString()}`);
  return handleResponse(response);
};

export const reviewRefundPayment = async (refundId, paymentReceived) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${refundId}/review-payment`, {
    method: "PUT",
    body: JSON.stringify({ paymentReceived }),
  });
  return handleResponse(response);
};

export const startRefundProcessing = async (refundId) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${refundId}/processing`, {
    method: "PUT",
  });
  return handleResponse(response);
};

export const completeRefund = async (refundId, request) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${refundId}/complete`, {
    method: "PUT",
    body: JSON.stringify(request),
  });
  return handleResponse(response);
};

export const failRefund = async (refundId, reason) => {
  const response = await fetchWithAuth(`${API_URL_ADMIN}/${refundId}/failed`, {
    method: "PUT",
    body: JSON.stringify({ reason }),
  });
  return handleResponse(response);
};
