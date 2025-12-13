import { useState, useEffect } from "react";
import { getProducts } from "../../services/productService";
import { Button, Form, Modal } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import "../../assets/styles/Product.css";

export default function Product() {
  const [products, setProducts] = useState([]);
  const [pageNo, setPageNo] = useState(0);
  const [pageSize] = useState(8);
  const [sortBy, setSortBy] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [search, setSearch] = useState("");

  const [pageData, setPageData] = useState(null);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const navigate = useNavigate();

  const loadProducts = async () => {
    try {
      const data = await getProducts(pageNo, pageSize, sortBy, sortDir, search);

      if (data?.content) {
        setProducts(data.content);
        setPageData(data);
      } else {
        setProducts([]);
      }
    } catch (err) {
      console.error("Fetch failed:", err);
    }
  };

  useEffect(() => {
    loadProducts();
  }, [pageNo, sortBy, sortDir, search]);

  const handleAddProduct = () => {
    const token = localStorage.getItem("token")
    if(!token){
      navigate("/login");
    }
  }
  return (
    <div className="client-products">
      <h2 className="title">⚽ Shop Đồ Bóng Đá – Sản Phẩm</h2>

      {/* Bộ lọc */}
      <div className="filter-bar">
        <Form.Control
          type="text"
          placeholder="Tìm sản phẩm theo tên..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPageNo(0);
          }}
          style={{ width: "250px" }}
        />
      </div>

      {/* Card sản phẩm */}
      <div className="product-grid">
        {products.map((p) => (
          <div key={p.id} className="product-card" onClick={() => setSelectedProduct(p)}>
            <img src={p.imageUrl} alt={p.name} />
            <h4>{p.name}</h4>
            <p className="price">{p.price.toLocaleString()} đ</p>

            <Button variant="success">Xem chi tiết</Button>
          </div>
        ))}
      </div>

      {/* Phân trang */}
      {pageData && (
        <div className="pagination">
          <Button disabled={pageNo === 0} onClick={() => setPageNo(pageNo - 1)}>
            Prev
          </Button>

          <span>Page {pageNo + 1} / {pageData.totalPages}</span>

          <Button
            disabled={pageNo + 1 >= pageData.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Next
          </Button>
        </div>
      )}

      {/* Modal chi tiết */}
      <Modal show={!!selectedProduct} onHide={() => setSelectedProduct(null)}>
        <Modal.Header closeButton>
          <Modal.Title>{selectedProduct?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <img
            src={selectedProduct?.imageUrl}
            style={{ width: "100%", borderRadius: "10px" }}
          />

          <h4 className="mt-3">{selectedProduct?.price?.toLocaleString()} đ</h4>
          <p>{selectedProduct?.description}</p>

          <p><b>Stock:</b> {selectedProduct?.stock}</p>
          <p><b>Giảm giá:</b> {selectedProduct?.discount}%</p>
          <p><b>Kích thước:</b> {selectedProduct?.dimension}</p>
          <p><b>Mô tả:</b> {selectedProduct?.description}</p>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setSelectedProduct(null)}>
            Đóng
          </Button>

          <Button variant="primary" onClick={handleAddProduct}>Thêm vào giỏ hàng</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
