import { refresh } from "./authService";

let isRefreshing = false;
let pendingRequests = [];

const processQueue = (token) => {
  pendingRequests.forEach(cb => cb(token));
  pendingRequests = [];
};

export const fetchWithAuth = async (url, options = {}) => {
  const accessToken = localStorage.getItem("accessToken");

  const response = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
      ...options.headers,
    }
  });

  // chỉ xử lý 401,403 KHÔNG áp dụng cho refresh API
  if (response.status !== 401 || url.includes("/refresh")) {
    return response;
  }

  if (!isRefreshing) {
    isRefreshing = true;

    try {
      const refreshRes = await refresh();
      if (!refreshRes.ok) throw new Error("Refresh failed");

      const data = await refreshRes.json();

      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);

      processQueue(data.accessToken);
      isRefreshing = false;

      // retry request gốc
      return fetch(url, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${data.accessToken}`,
          ...options.headers,
        }
      });

    } catch (err) {
      isRefreshing = false;
      pendingRequests = [];
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      window.location.href = "/login";
      throw err;
    }
  }

  // chờ refresh xong
  return new Promise((resolve) => {
    pendingRequests.push((newToken) => {
      resolve(fetch(url, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${newToken}`,
          ...options.headers,
        }
      }));
    });
  });
};
