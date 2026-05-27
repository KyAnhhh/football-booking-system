import { refresh } from "./authService";

let isRefreshing = false;
let pendingRequests = [];

const processQueue = (error, token = null) => {
  pendingRequests.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve(token);
    }
  });
  pendingRequests = [];
};

export const fetchWithAuth = async (url, options = {}) => {
  const accessToken = sessionStorage.getItem("accessToken");
  const isFormData = options.body instanceof FormData;

  const makeRequest = (token) =>
    fetch(url, {
      ...options,
      headers: {
        ...( !isFormData && { "Content-Type": "application/json" }),
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
      },
    });

  let response = await makeRequest(accessToken);

  // ❗ chỉ xử lý 401 và không phải refresh API
  if (response.status !== 401 || url.includes("/refresh")) {
    return response;
  }

  // refresh token đã hết hạn, cần refresh
  if (!isRefreshing) {
    isRefreshing = true;

    try {
      const refreshRes = await refresh();
      if (!refreshRes.ok) throw new Error("Refresh failed");

      const data = await refreshRes.json();

      sessionStorage.setItem("accessToken", data.accessToken);
      sessionStorage.setItem("refreshToken", data.refreshToken);

      processQueue(null, data.accessToken);
      isRefreshing = false;

      // retry request gốc
      return await makeRequest(data.accessToken);

    } catch (err) {
      processQueue(err, null);
      isRefreshing = false;

      sessionStorage.removeItem("accessToken");
      sessionStorage.removeItem("refreshToken");

      window.location.href = "/login";
      throw err;
    }
  }

  // đang refresh, đẩy request vào hàng đợi
  return new Promise((resolve, reject) => {
    pendingRequests.push({
      resolve: async (token) => {
        try {
          const res = await makeRequest(token);
          resolve(res);
        } catch (err) {
          reject(err);
        }
      },
      reject,
    });
  });
};