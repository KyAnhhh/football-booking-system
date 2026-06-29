import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/admin/inventory";

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || `HTTP Error ${response.status}`);
  }

  const result = await response.json();
  return result.data ?? result.message;
};

const getJsonHeaders = () => ({
  "Content-Type": "application/json",
});

export const getInventoryItems = async ({
  page = 0,
  size = 20,
  search = "",
  itemType = "",
} = {}) => {
  const params = new URLSearchParams({ page, size });
  if (search) params.append("search", search);
  if (itemType) params.append("itemType", itemType);

  const response = await fetchWithAuth(`${API_URL}/items?${params.toString()}`, {
    headers: getJsonHeaders(),
  });

  return handleResponse(response);
};

export const createInventoryItem = async (payload) => {
  const response = await fetchWithAuth(`${API_URL}/items`, {
    method: "POST",
    headers: getJsonHeaders(),
    body: JSON.stringify(payload),
  });

  return handleResponse(response);
};

export const updateInventoryItem = async (id, payload) => {
  const response = await fetchWithAuth(`${API_URL}/items/${id}`, {
    method: "PUT",
    headers: getJsonHeaders(),
    body: JSON.stringify(payload),
  });

  return handleResponse(response);
};

export const getInventoryStocks = async ({
  page = 0,
  size = 20,
  search = "",
  itemType = "",
} = {}) => {
  const params = new URLSearchParams({ page, size });
  if (search) params.append("search", search);
  if (itemType) params.append("itemType", itemType);

  const response = await fetchWithAuth(`${API_URL}/stocks?${params.toString()}`, {
    headers: getJsonHeaders(),
  });

  return handleResponse(response);
};

export const getInventoryTransactions = async ({
  page = 0,
  size = 20,
  inventoryItemId = "",
} = {}) => {
  const params = new URLSearchParams({ page, size });
  if (inventoryItemId) params.append("inventoryItemId", inventoryItemId);

  const response = await fetchWithAuth(
    `${API_URL}/transactions?${params.toString()}`,
    { headers: getJsonHeaders() }
  );

  return handleResponse(response);
};

export const getInventoryReservations = async ({
  page = 0,
  size = 20,
  status = "",
} = {}) => {
  const params = new URLSearchParams({ page, size });
  if (status) params.append("status", status);

  const response = await fetchWithAuth(
    `${API_URL}/reservations?${params.toString()}`,
    { headers: getJsonHeaders() }
  );

  return handleResponse(response);
};

export const getSuppliers = async ({ page = 0, size = 50 } = {}) => {
  const params = new URLSearchParams({ page, size });
  const response = await fetchWithAuth(
    `${API_URL}/suppliers?${params.toString()}`,
    { headers: getJsonHeaders() }
  );

  return handleResponse(response);
};

export const createSupplier = async (payload) => {
  const response = await fetchWithAuth(`${API_URL}/suppliers`, {
    method: "POST",
    headers: getJsonHeaders(),
    body: JSON.stringify(payload),
  });

  return handleResponse(response);
};

export const updateSupplier = async (id, payload) => {
  const response = await fetchWithAuth(`${API_URL}/suppliers/${id}`, {
    method: "PUT",
    headers: getJsonHeaders(),
    body: JSON.stringify(payload),
  });

  return handleResponse(response);
};

export const importStock = async (payload) => {
  const response = await fetchWithAuth(`${API_URL}/imports`, {
    method: "POST",
    headers: getJsonHeaders(),
    body: JSON.stringify(payload),
  });

  return handleResponse(response);
};

export const getStockImports = async ({
  page = 0,
  size = 20,
  search = "",
  status = "",
  supplierId = "",
} = {}) => {
  const params = new URLSearchParams({ page, size });
  if (search) params.append("search", search);
  if (status) params.append("status", status);
  if (supplierId) params.append("supplierId", supplierId);

  const response = await fetchWithAuth(
    `${API_URL}/imports?${params.toString()}`,
    { headers: getJsonHeaders() }
  );
  return handleResponse(response);
};

export const getStockImportById = async (id) => {
  const response = await fetchWithAuth(`${API_URL}/imports/${id}`, {
    headers: getJsonHeaders(),
  });
  return handleResponse(response);
};

export const postStockImport = async (id) => {
  const response = await fetchWithAuth(`${API_URL}/imports/${id}/post`, {
    method: "POST",
    headers: getJsonHeaders(),
  });
  return handleResponse(response);
};

export const cancelStockImport = async (id) => {
  const response = await fetchWithAuth(`${API_URL}/imports/${id}/cancel`, {
    method: "POST",
    headers: getJsonHeaders(),
  });
  return handleResponse(response);
};

export const checkInventoryConsistency = async () => {
  const response = await fetchWithAuth(`${API_URL}/consistency`, {
    headers: getJsonHeaders(),
  });
  return handleResponse(response);
};

export const adjustStock = async (payload) => {
  const response = await fetchWithAuth(`${API_URL}/adjustments`, {
    method: "POST",
    headers: getJsonHeaders(),
    body: JSON.stringify(payload),
  });

  return handleResponse(response);
};

export const getProductVariantSyncPreview = async () => {
  const response = await fetchWithAuth(
    `${API_URL}/product-variant-sync/preview`,
    { headers: getJsonHeaders() }
  );

  return handleResponse(response);
};

export const syncProductVariantsToInventory = async () => {
  const response = await fetchWithAuth(
    `${API_URL}/product-variant-sync`,
    {
      method: "POST",
      headers: getJsonHeaders(),
    }
  );

  return handleResponse(response);
};
