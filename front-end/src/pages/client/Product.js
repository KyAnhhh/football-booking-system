import { useState, useEffect } from "react";
import { getProducts } from "../../services/productService";
import { addItemToCart } from "../../services/cartService";
import { Badge, Button, Form, Modal } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../../assets/styles/Product.css";

export default function Product() {
  const [products, setProducts] = useState([]);
  const [pageData, setPageData] = useState(null);

  const [pageNo, setPageNo] = useState(0);
  const pageSize = 8;

  const [search, setSearch] = useState("");

  const [selectedProduct, setSelectedProduct] = useState(null);
  const [selectedVariant, setSelectedVariant] = useState(null);
  const [quantity, setQuantity] = useState(1);

  const navigate = useNavigate();

  const loadProducts = async () => {
    try {
      const data = await getProducts(pageNo, pageSize, "id", "asc", search);
      setProducts(data?.content || []);
      setPageData(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadProducts();
  }, [pageNo, search]);

  const getMinPrice = (variants = []) =>
    variants.length ? Math.min(...variants.map(v => v.price)) : 0;

  const getTotalStock = (variants = []) =>
    variants.reduce((sum, v) => sum + v.stock, 0);

  const getSoldQuantity = (variants = []) =>
    variants.reduce((sum, v) => sum + (v.soldQuantity || 0), 0);

  const handleAddToCart = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    if (!selectedVariant) {
      Swal.fire("Thiếu thông tin", "Vui lòng chọn size", "warning");
      return;
    }

    try {
      await addItemToCart({
        productVariantId: selectedVariant.id,
        sizeId: selectedVariant.sizeId,
        quantity,
      });

      Swal.fire("Thành công", "Đã thêm vào giỏ hàng", "success");
      setSelectedProduct(null);
      setSelectedVariant(null);
      setQuantity(1);
    } catch (err) {
      Swal.fire("Lỗi", err.message || "Không thể thêm vào giỏ hàng", "error");
    }
  };

  return (
    <div className="client-products">
      <h2 className="title">⚽ Shop Đồ Bóng Đá</h2>

      {/* Search */}
      <div className="filter-bar">
        <Form.Control
          placeholder="Tìm theo tên sản phẩm..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPageNo(0);
          }}
        />
      </div>

      <div className="product-grid">
        {products.map(p => (
          <div
            key={p.id}
            className="product-card"
            onClick={() => setSelectedProduct(p)}
          >
            <img
              src={p.imageUrls?.[0] || "/no-image.png"}
              alt={p.name}
              className="product-image"
            />

            <h4>{p.name}</h4>
            <p className="price">
              {getMinPrice(p.variants).toLocaleString()} đ
            </p>

            <div className="product-card-badge">
              <Badge bg={getTotalStock(p.variants) > 0 ? "success" : "danger"}>
              {getTotalStock(p.variants) > 0 ? "Còn hàng" : "Hết hàng"}
            </Badge>
            
            <Badge bg={getSoldQuantity(p.variants) > 0 ? "success" : "danger"}>
              {"Đã bán: " + getSoldQuantity(p.variants)}
            </Badge>
            </div>


            <div className="product-card-button">

            <Button
              variant="primary"
              onClick={(e) => {
                e.stopPropagation();
                setSelectedProduct(p);
              }}
            >
              Xem chi tiết
            </Button>

              <Button
              variant="primary"
              onClick={(e) => {
                setSelectedProduct(p);
              }}
            >
              Mua ngay
            </Button>
            </div>
          </div>
        ))}
      </div>

      {/* Pagination */}
      {pageData && (
        <div className="pagination">
          <Button
            disabled={pageNo === 0}
            onClick={() => setPageNo(pageNo - 1)}
          >
            Prev
          </Button>

          <span>
            Trang {pageNo + 1} / {pageData.totalPages}
          </span>

          <Button
            disabled={pageNo + 1 >= pageData.totalPages}
            onClick={() => setPageNo(pageNo + 1)}
          >
            Next
          </Button>
        </div>
      )}

      <Modal
        show={!!selectedProduct}
        onHide={() => {
          setSelectedProduct(null);
          setSelectedVariant(null);
        }}
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>{selectedProduct?.name}</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <img
            src={selectedProduct?.imageUrls?.[0] || "/no-image.png"}
            className="modal-image"
            alt=""
          />

          <p>{selectedProduct?.description}</p>

          <h6>Chọn size</h6>
          <div className="size-group">
            {selectedProduct?.variants.map(v => (
              <Button
                key={v.id}
                variant={
                  selectedVariant?.id === v.id
                    ? "primary"
                    : "outline-primary"
                }
                disabled={v.stock === 0}
                onClick={() => setSelectedVariant(v)}
              >
                {v.sizeCode}
              </Button>
            ))}
          </div>

          <Form.Group className="mt-3">
            <Form.Label>Số lượng</Form.Label>
            <Form.Control
              type="number"
              min={1}
              value={quantity}
              onChange={(e) => setQuantity(+e.target.value)}
            />
          </Form.Group>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setSelectedProduct(null)}>
            Đóng
          </Button>
          <Button variant="success" onClick={handleAddToCart}>
            Thêm vào giỏ hàng
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
