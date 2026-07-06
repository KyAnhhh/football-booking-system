import { useCallback, useEffect, useState } from "react";
import swal from "sweetalert";

import {
  uploadProductImages,
  getImageByProduct,
  deleteImage,
  setPrimary,
} from "../../services/productImageService";

export default function ImagePage({ product, onClose }) {
  const [images, setImages] = useState([]);
  const [files, setFiles] = useState([]);
  const [previews, setPreviews] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadImages = useCallback(async () => {
    if (!product?.id) return;
    const res = await getImageByProduct(product.id);
    setImages(res);
  }, [product?.id]);

  useEffect(() => {
    loadImages();
  }, [loadImages]);

  useEffect(
    () => () => previews.forEach((url) => URL.revokeObjectURL(url)),
    [previews]
  );

  const validateFiles = (fileList) => {
    if (!fileList || fileList.length === 0) {
      swal("Error", "Chưa chọn ảnh", "error");
      return false;
    }
    if (fileList.length > 10) {
      swal("Error", "Mỗi lần chỉ được upload tối đa 10 ảnh", "error");
      return false;
    }

    for (let file of fileList) {
      if (!file.type.startsWith("image/")) {
        swal("Error", "Chỉ được upload file ảnh", "error");
        return false;
      }
      if (file.size > 2 * 1024 * 1024) {
        swal("Error", "Ảnh phải nhỏ hơn 2MB", "error");
        return false;
      }
    }
    return true;
  };

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    if (!validateFiles(selectedFiles)) return;

    previews.forEach((url) => URL.revokeObjectURL(url));
    setFiles(selectedFiles);

    // preview
    const previewUrls = selectedFiles.map((file) =>
      URL.createObjectURL(file)
    );
    setPreviews(previewUrls);
  };

  const handleUpload = async () => {
    if (!product?.id) {
      swal("Error", "Chưa có product", "error");
      return;
    }

    if (files.length === 0) {
      swal("Error", "Chưa chọn ảnh", "error");
      return;
    }

    try {
      setLoading(true);
      await uploadProductImages(product.id, files);
      swal("Success", "Upload ảnh thành công", "success");
      setFiles([]);
      setPreviews([]);
      loadImages();
    } catch (err) {
      swal("Error", err.message || "Upload thất bại", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    const ok = await swal({
      title: "Xóa ảnh?",
      text: "Không thể khôi phục",
      icon: "warning",
      buttons: true,
      dangerMode: true,
    });

    if (!ok) return;

    await deleteImage(id);
    loadImages();
    swal("Deleted", "Ảnh đã bị xóa", "success");
  };

  const handleSetPrimary = async (id) => {
    await setPrimary(id);
    loadImages();
  };

  return (
    <div className="card mt-4">
      <div className="card-header d-flex justify-content-between">
        <strong>Ảnh: {product?.name}</strong>
        <button className="btn btn-sm btn-secondary" onClick={onClose}>
          Close
        </button>
      </div>

      <div className="card-body">
        <div className="mb-3">
          <input
            type="file"
            multiple
            accept="image/*"
            className="form-control"
            onChange={handleFileChange}
          />
        </div>

        {/* Preview */}
        {previews.length > 0 && (
          <>
            <strong>Preview</strong>
            <div className="row mt-2">
              {previews.map((src, idx) => (
                <div key={idx} className="col-md-3 mb-2">
                  <img
                    src={src}
                    className="img-fluid border"
                    alt=""
                  />
                </div>
              ))}
            </div>
          </>
        )}

        <button
          className="btn btn-primary mt-2"
          disabled={loading}
          onClick={handleUpload}
        >
          {loading ? "Uploading..." : "Upload"}
        </button>

        <hr />

       <div className="row">
        {images.map((img) => (
          <div key={img.id} className="col-md-3 mb-3 text-center">
            <img
              src={img.imageUrl}
              alt=""
              style={{
                width: "150px",
                height: "150px",
                objectFit: "cover",
                borderRadius: "6px",
                border: img.primary
                  ? "3px solid green"
                  : "1px solid #ccc",
              }}
            />

            <div className="mt-2">
              {!img.primary && (
                <button
                  className="btn btn-sm btn-outline-success me-1"
                  onClick={() => handleSetPrimary(img.id)}
                >
                  Primary
                </button>
              )}
              <button
                className="btn btn-sm btn-outline-danger"
                onClick={() => handleDelete(img.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>

        {images.length === 0 && (
          <p className="text-muted">Chưa có ảnh nào</p>
        )}
      </div>
    </div>
  );
}
