import { fetchWithAuth } from "./apiService";

const API_URL = "http://localhost:8080/api/admin/imports";

export const IMPORT_TYPES = [
  {
    value: "sizes",
    label: "Size",
    description: "Import mã size, tên size và mô tả.",
  },
  {
    value: "products",
    label: "Sản phẩm",
    description: "Import thông tin sản phẩm. Giá và tồn kho sẽ được import ở biến thể.",
  },
  {
    value: "product-variants",
    label: "Biến thể sản phẩm",
    description: "Import giá bán và tồn kho theo sản phẩm + size.",
  },
  {
    value: "football-fields",
    label: "Sân bóng",
    description: "Import danh sách sân bóng, loại sân, giá cơ bản và trạng thái.",
  },
  {
    value: "product-images",
    label: "Ảnh sản phẩm",
    description: "Import URL ảnh, ảnh chính và thứ tự hiển thị cho sản phẩm.",
  },
  {
    value: "football-field-images",
    label: "Ảnh sân bóng",
    description: "Import gallery ảnh cho sân bóng bằng URL.",
  },
  {
    value: "field-pricing-rules",
    label: "Bảng giá sân",
    description: "Import giá theo sân, ngày trong tuần và khung giờ.",
  },
  {
    value: "holidays",
    label: "Ngày lễ",
    description: "Import danh sách ngày lễ theo năm.",
  },
  {
    value: "holiday-pricing-rules",
    label: "Giá ngày lễ",
    description: "Import giá riêng cho từng sân trong ngày lễ.",
  },
];

const handleResponse = async (response) => {
  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Không thể import dữ liệu");
  }

  const result = await response.json();
  return result.data ?? result;
};

export const importCsv = async (type, file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetchWithAuth(`${API_URL}/${type}`, {
    method: "POST",
    body: formData,
  });

  return handleResponse(response);
};

export const downloadImportTemplate = async (type) => {
  const response = await fetchWithAuth(`${API_URL}/templates/${type}`);

  if (!response.ok) {
    const errText = await response.text();
    throw new Error(errText || "Không thể tải file mẫu");
  }

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = `${type}-template.csv`;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};
