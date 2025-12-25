import { useEffect, useState } from "react";
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
  const [primaryIndex, setPrimaryIndex] = useState(null);

  const loadImages = async () => {
    const res = await getImageByProduct(product.id);
    setImages(res);
  };

  useEffect(() => {
    loadImages();
  }, [product.id]);

  const handleUpload = async () => {
    if (files.length === 0) return;

    await uploadProductImages(product.id, files, primaryIndex);
    setFiles([]);
    setPrimaryIndex(null);
    loadImages();
    swal("Success", "Images uploaded", "success");
  };

  const handleDelete = async (id) => {
    if (!(await swal({ text: "Delete image?", buttons: true }))) return;
    await deleteImage(id);
    loadImages();
  };

  const handleSetPrimary = async (id) => {
    await setPrimary(id);
    loadImages();
  };

  return (
    <div className="card mt-4">
      <div className="card-header d-flex justify-content-between">
        <strong>Images of: {product.name}</strong>
        <button className="btn btn-sm btn-secondary" onClick={onClose}>
          Close
        </button>
      </div>

      <div className="card-body">
        {/* Upload */}
        <input
          type="file"
          multiple
          onChange={(e) => setFiles([...e.target.files])}
        />

        <button className="btn btn-primary ms-2" onClick={handleUpload}>
          Upload
        </button>

        <hr />

        {/* Image list */}
        <div className="row">
          {images.map((img) => (
            <div key={img.id} className="col-md-3 mb-3 text-center">
              <img
                src={img.url}
                alt=""
                className="img-fluid border"
                style={{
                  border:
                    img.primary === true ? "3px solid green" : "1px solid #ccc",
                }}
              />
              <div className="mt-1">
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
      </div>
    </div>
  );
}
