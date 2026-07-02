import { useCallback, useEffect, useMemo, useState } from "react";
import { Button, Form, Modal, Table } from "react-bootstrap";
import swal from "sweetalert";
import { BsArrowRepeat } from "react-icons/bs";

import {
  adjustStock,
  cancelStockImport,
  checkInventoryConsistency,
  createInventoryItem,
  createSupplier,
  getInventoryItems,
  getInventoryReservations,
  getInventoryProfitSummary,
  getInventoryStocks,
  getInventoryTransactions,
  getStockImportById,
  getStockImports,
  getProductVariantSyncPreview,
  getSuppliers,
  importStock,
  postStockImport,
  syncProductVariantsToInventory,
  updateLowStockThreshold,
  updateInventoryBehavior,
  updateSupplier,
} from "../../services/inventoryService";
import "../../assets/styles/AdminCrudPage.css";
import "../../assets/styles/InventoryPage.css";

const itemTypes = [
  "PRODUCT_VARIANT",
  "EXTRA_SERVICE",
  "FIELD_ASSET",
  "CONSUMABLE",
  "OTHER",
];

const inventoryBehaviors = [
  ["SELLABLE", "Hàng bán"],
  ["CONSUMABLE", "Hàng tiêu hao"],
  ["RENTAL", "Hàng cho thuê"],
];

const adjustmentTypes = [
  "ADJUSTMENT_INCREASE",
  "ADJUSTMENT_DECREASE",
  "DAMAGED",
  "LOST",
  "EXPORT",
];

const emptyItemForm = {
  itemCode: "",
  name: "",
  itemType: "OTHER",
  inventoryBehavior: "CONSUMABLE",
  referenceId: "",
  unit: "cái",
  lowStockThreshold: 0,
  active: true,
};

const emptySupplierForm = {
  name: "",
  phone: "",
  email: "",
  address: "",
  active: true,
};

const getPageTotal = (pageData) =>
  pageData?.page?.totalPages || pageData?.totalPages || 1;

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

export default function InventoryPage() {
  const [activeTab, setActiveTab] = useState("stocks");
  const [items, setItems] = useState([]);
  const [stocks, setStocks] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [stockImports, setStockImports] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [profitSummary, setProfitSummary] = useState(null);
  const [itemPage, setItemPage] = useState(null);
  const [stockPage, setStockPage] = useState(null);
  const [transactionPage, setTransactionPage] = useState(null);
  const [reservationPage, setReservationPage] = useState(null);
  const [stockImportPage, setStockImportPage] = useState(null);
  const [pageNo, setPageNo] = useState(0);
  const [search, setSearch] = useState("");
  const [itemType, setItemType] = useState("");
  const [lowStockOnly, setLowStockOnly] = useState(false);
  const [loading, setLoading] = useState(false);

  const [showItemModal, setShowItemModal] = useState(false);
  const [showSupplierModal, setShowSupplierModal] = useState(false);
  const [showImportModal, setShowImportModal] = useState(false);
  const [showAdjustModal, setShowAdjustModal] = useState(false);
  const [showProductSyncModal, setShowProductSyncModal] = useState(false);
  const [showImportDetailModal, setShowImportDetailModal] = useState(false);
  const [showConsistencyModal, setShowConsistencyModal] = useState(false);
  const [editingItemId, setEditingItemId] = useState(null);
  const [selectedStockImport, setSelectedStockImport] = useState(null);
  const [consistencyResult, setConsistencyResult] = useState(null);
  const [consistencyLoading, setConsistencyLoading] = useState(false);
  const [productSyncPreview, setProductSyncPreview] = useState(null);
  const [productSyncResult, setProductSyncResult] = useState(null);
  const [productSyncLoading, setProductSyncLoading] = useState(false);
  const [itemForm, setItemForm] = useState(emptyItemForm);
  const [supplierForm, setSupplierForm] = useState(emptySupplierForm);
  const [editingSupplierId, setEditingSupplierId] = useState(null);
  const [importForm, setImportForm] = useState({
    importCode: "",
    supplierId: "",
    note: "",
    items: [{ inventoryItemId: "", quantity: 1, unitCost: 0 }],
  });
  const [adjustForm, setAdjustForm] = useState({
    inventoryItemId: "",
    transactionType: "ADJUSTMENT_INCREASE",
    quantity: 1,
    note: "",
  });

  const inventoryOptions = useMemo(
    () => items.map((item) => ({ value: item.id, label: `${item.itemCode} - ${item.name}` })),
    [items]
  );

  const importTotal = useMemo(
    () =>
      importForm.items.reduce(
        (total, item) =>
          total
          + Number(item.quantity || 0) * Number(item.unitCost || 0),
        0
      ),
    [importForm.items]
  );

  const loadItems = useCallback(async () => {
    const data = await getInventoryItems({
      page: pageNo,
      size: 20,
      search,
      itemType,
    });
    setItems(data.content || []);
    setItemPage(data);
  }, [pageNo, search, itemType]);

  const loadStocks = useCallback(async () => {
    const data = await getInventoryStocks({
      page: pageNo,
      size: 20,
      search,
      itemType,
      lowStockOnly,
    });
    setStocks(data.content || []);
    setStockPage(data);
  }, [pageNo, search, itemType, lowStockOnly]);

  const loadTransactions = useCallback(async () => {
    const data = await getInventoryTransactions({ page: pageNo, size: 20 });
    setTransactions(data.content || []);
    setTransactionPage(data);
  }, [pageNo]);

  const loadReservations = useCallback(async () => {
    const data = await getInventoryReservations({ page: pageNo, size: 20 });
    setReservations(data.content || []);
    setReservationPage(data);
  }, [pageNo]);

  const loadStockImports = useCallback(async () => {
    const data = await getStockImports({
      page: pageNo,
      size: 20,
      search,
    });
    setStockImports(data.content || []);
    setStockImportPage(data);
  }, [pageNo, search]);

  const loadSuppliers = useCallback(async () => {
    const data = await getSuppliers({ page: 0, size: 100 });
    setSuppliers(data.content || []);
  }, []);

  const loadProfitSummary = useCallback(async () => {
    const data = await getInventoryProfitSummary();
    setProfitSummary(data);
  }, []);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      await Promise.all([
        loadItems(),
        activeTab === "stocks" ? loadStocks() : Promise.resolve(),
        activeTab === "transactions" ? loadTransactions() : Promise.resolve(),
        activeTab === "reservations" ? loadReservations() : Promise.resolve(),
        activeTab === "imports" ? loadStockImports() : Promise.resolve(),
        activeTab === "stocks" ? loadProfitSummary() : Promise.resolve(),
        loadSuppliers(),
      ]);
    } catch (error) {
      swal("Lỗi", error.message || "Không thể tải dữ liệu kho.", "error");
    } finally {
      setLoading(false);
    }
  }, [
    activeTab,
    loadItems,
    loadStocks,
    loadTransactions,
    loadReservations,
    loadStockImports,
    loadProfitSummary,
    loadSuppliers,
  ]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const currentPageData =
    activeTab === "suppliers"
      ? null
      : activeTab === "imports"
        ? stockImportPage
      : activeTab === "items"
      ? itemPage
      : activeTab === "reservations"
        ? reservationPage
      : activeTab === "transactions"
        ? transactionPage
        : stockPage;
  const totalPages = getPageTotal(currentPageData);

  const openCreateItem = () => {
    setEditingItemId(null);
    setItemForm(emptyItemForm);
    setShowItemModal(true);
  };

  const openEditItemThreshold = (item) => {
    setEditingItemId(item.inventoryItemId || item.id);
    setItemForm({
      itemCode: item.itemCode,
      name: item.name || item.itemName,
      itemType: item.itemType,
      inventoryBehavior: item.inventoryBehavior || "CONSUMABLE",
      referenceId: item.referenceId || "",
      unit: item.unit || "cái",
      lowStockThreshold: item.lowStockThreshold || 0,
      active: item.active,
    });
    setShowItemModal(true);
  };

  const handleUpdateItemThreshold = async (event) => {
    event.preventDefault();
    try {
      await Promise.all([
        updateLowStockThreshold(
          editingItemId,
          Number(itemForm.lowStockThreshold || 0)
        ),
        updateInventoryBehavior(
          editingItemId,
          itemForm.inventoryBehavior
        ),
      ]);
      setShowItemModal(false);
      setEditingItemId(null);
      setItemForm(emptyItemForm);
      await loadData();
      swal("Thành công", "Đã cập nhật cấu hình item kho.", "success");
    } catch (error) {
      swal(
        "Lỗi",
        error.message || "Không thể cập nhật ngưỡng cảnh báo.",
        "error"
      );
    }
  };

  const handleCreateItem = async (event) => {
    event.preventDefault();
    try {
      await createInventoryItem({
        ...itemForm,
        referenceId: itemForm.referenceId ? Number(itemForm.referenceId) : null,
        inventoryBehavior: itemForm.inventoryBehavior,
        lowStockThreshold: Number(itemForm.lowStockThreshold || 0),
      });
      setShowItemModal(false);
      setItemForm(emptyItemForm);
      await loadData();
      swal("Thành công", "Đã tạo item kho.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể tạo item kho.", "error");
    }
  };

  const handleSaveSupplier = async (event) => {
    event.preventDefault();
    try {
      if (editingSupplierId) {
        await updateSupplier(editingSupplierId, supplierForm);
      } else {
        await createSupplier(supplierForm);
      }
      setShowSupplierModal(false);
      setSupplierForm(emptySupplierForm);
      setEditingSupplierId(null);
      await loadSuppliers();
      swal("Thành công", "Đã tạo nhà cung cấp.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể tạo nhà cung cấp.", "error");
    }
  };

  const openCreateSupplier = () => {
    setEditingSupplierId(null);
    setSupplierForm(emptySupplierForm);
    setShowSupplierModal(true);
  };

  const openEditSupplier = (supplier) => {
    setEditingSupplierId(supplier.id);
    setSupplierForm({
      name: supplier.name || "",
      phone: supplier.phone || "",
      email: supplier.email || "",
      address: supplier.address || "",
      active: Boolean(supplier.active),
    });
    setShowSupplierModal(true);
  };

  const handleToggleSupplier = async (supplier) => {
    const nextActive = !supplier.active;
    const confirmed = await swal({
      title: nextActive
        ? "Kích hoạt nhà cung cấp?"
        : "Ngừng hợp tác với nhà cung cấp?",
      text: nextActive
        ? "Nhà cung cấp sẽ được sử dụng lại trong phiếu nhập."
        : "Nhà cung cấp sẽ được ẩn khỏi các phiếu nhập mới.",
      icon: "warning",
      buttons: ["Không", "Xác nhận"],
      dangerMode: !nextActive,
    });
    if (!confirmed) return;

    try {
      await updateSupplier(supplier.id, {
        name: supplier.name,
        phone: supplier.phone,
        email: supplier.email,
        address: supplier.address,
        active: nextActive,
      });
      await loadSuppliers();
    } catch (error) {
      swal("Lỗi", error.message || "Không thể cập nhật trạng thái.", "error");
    }
  };

  const handleImportStock = async (event) => {
    event.preventDefault();
    try {
      await importStock({
        importCode: importForm.importCode || null,
        supplierId: importForm.supplierId ? Number(importForm.supplierId) : null,
        note: importForm.note,
        items: importForm.items.map((item) => ({
          inventoryItemId: Number(item.inventoryItemId),
          quantity: Number(item.quantity),
          unitCost: Number(item.unitCost || 0),
        })),
      });
      setShowImportModal(false);
      setImportForm({
        importCode: "",
        supplierId: "",
        note: "",
        items: [{ inventoryItemId: "", quantity: 1, unitCost: 0 }],
      });
      setActiveTab("imports");
      setPageNo(0);
      await loadData();
      swal("Thành công", "Nhập kho thành công.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể nhập kho.", "error");
    }
  };

  const openStockImportDetail = async (id) => {
    try {
      const data = await getStockImportById(id);
      setSelectedStockImport(data);
      setShowImportDetailModal(true);
    } catch (error) {
      swal("Lỗi", error.message || "Không thể tải phiếu nhập.", "error");
    }
  };

  const handlePostStockImport = async (stockImport) => {
    const confirmed = await swal({
      title: "Ghi sổ phiếu nhập?",
      text: "Tồn kho sẽ được cộng và phiếu không thể chỉnh sửa sau bước này.",
      icon: "warning",
      buttons: ["Không", "Ghi sổ"],
    });
    if (!confirmed) return;

    try {
      await postStockImport(stockImport.id);
      setShowImportDetailModal(false);
      await Promise.all([loadStockImports(), loadStocks()]);
      swal("Thành công", "Phiếu nhập đã được ghi sổ.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể ghi sổ phiếu nhập.", "error");
    }
  };

  const handleCancelStockImport = async (stockImport) => {
    const confirmed = await swal({
      title: "Hủy phiếu nhập?",
      text: "Phiếu nháp sẽ bị hủy và không tác động đến tồn kho.",
      icon: "warning",
      buttons: ["Không", "Hủy phiếu"],
      dangerMode: true,
    });
    if (!confirmed) return;

    try {
      await cancelStockImport(stockImport.id);
      setShowImportDetailModal(false);
      await loadStockImports();
      swal("Đã hủy", "Phiếu nhập đã được hủy.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể hủy phiếu nhập.", "error");
    }
  };

  const handleCheckConsistency = async () => {
    try {
      setConsistencyLoading(true);
      setShowConsistencyModal(true);
      const result = await checkInventoryConsistency();
      setConsistencyResult(result);
    } catch (error) {
      setShowConsistencyModal(false);
      swal("Lỗi", error.message || "Không thể kiểm tra dữ liệu kho.", "error");
    } finally {
      setConsistencyLoading(false);
    }
  };

  const handleAdjustStock = async (event) => {
    event.preventDefault();
    try {
      await adjustStock({
        ...adjustForm,
        inventoryItemId: Number(adjustForm.inventoryItemId),
        quantity: Number(adjustForm.quantity),
      });
      setShowAdjustModal(false);
      setAdjustForm({
        inventoryItemId: "",
        transactionType: "ADJUSTMENT_INCREASE",
        quantity: 1,
        note: "",
      });
      await loadData();
      swal("Thành công", "Điều chỉnh kho thành công.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể điều chỉnh kho.", "error");
    }
  };

  const updateImportLine = (index, field, value) => {
    setImportForm((current) => ({
      ...current,
      items: current.items.map((item, itemIndex) =>
        itemIndex === index ? { ...item, [field]: value } : item
      ),
    }));
  };

  const openProductSyncModal = async () => {
    try {
      setProductSyncLoading(true);
      setProductSyncResult(null);
      setShowProductSyncModal(true);
      const preview = await getProductVariantSyncPreview();
      setProductSyncPreview(preview);
    } catch (error) {
      setShowProductSyncModal(false);
      swal(
        "Lỗi",
        error.message || "Không thể kiểm tra sản phẩm chưa liên kết kho.",
        "error"
      );
    } finally {
      setProductSyncLoading(false);
    }
  };

  const handleProductSync = async () => {
    try {
      setProductSyncLoading(true);
      const result = await syncProductVariantsToInventory();
      setProductSyncResult(result);
      setActiveTab("stocks");
      setItemType("PRODUCT_VARIANT");
      setPageNo(0);
      await loadStocks();
    } catch (error) {
      swal(
        "Lỗi",
        error.message || "Không thể đồng bộ sản phẩm vào kho.",
        "error"
      );
    } finally {
      setProductSyncLoading(false);
    }
  };

  return (
    <div className="admin-crud-page inventory-page">
      <div className="admin-crud-header">
        <div>
          <span className="admin-crud-kicker">Quản lý kho</span>
          <h2>Inventory</h2>
          <p>Theo dõi tồn kho, nhập kho và lịch sử biến động.</p>
        </div>

        <div className="inventory-actions">
          <Button variant="outline-dark" onClick={openProductSyncModal}>
            <BsArrowRepeat />
            Đồng bộ sản phẩm
          </Button>
          <Button variant="outline-warning" onClick={handleCheckConsistency}>
            Kiểm tra dữ liệu
          </Button>
          <Button variant="outline-primary" onClick={openCreateSupplier}>
            + Nhà cung cấp
          </Button>
          <Button variant="outline-success" onClick={openCreateItem}>
            + Item kho
          </Button>
          <Button variant="success" onClick={() => setShowImportModal(true)}>
            Nhập kho
          </Button>
          <Button variant="warning" onClick={() => setShowAdjustModal(true)}>
            Điều chỉnh
          </Button>
        </div>
      </div>

      <div className="inventory-toolbar">
        {[
          ["stocks", "Tồn kho"],
          ["items", "Item kho"],
          ["transactions", "Lịch sử"],
          ["reservations", "Giữ kho"],
          ["suppliers", "Nhà cung cấp"],
          ["imports", "Phiếu nhập"],
        ].map(([value, label]) => (
          <Button
            key={value}
            size="sm"
            variant={activeTab === value ? "primary" : "outline-primary"}
            onClick={() => {
              setActiveTab(value);
              setPageNo(0);
            }}
          >
            {label}
          </Button>
        ))}

        <Form.Control
          placeholder="Tìm item kho..."
          value={search}
          onChange={(event) => {
            setSearch(event.target.value);
            setPageNo(0);
          }}
        />

        <Form.Select
          value={itemType}
          onChange={(event) => {
            setItemType(event.target.value);
            setPageNo(0);
          }}
        >
          <option value="">Tất cả loại</option>
          {itemTypes.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </Form.Select>

        {activeTab === "stocks" && (
          <Form.Check
            type="switch"
            id="low-stock-only"
            label="Chỉ sắp hết"
            checked={lowStockOnly}
            onChange={(event) => {
              setLowStockOnly(event.target.checked);
              setPageNo(0);
            }}
          />
        )}
      </div>

      {activeTab === "stocks" && (
        <>
        {profitSummary && (
          <div className="inventory-profit-summary">
            <div>
              <span>Doanh thu ghi nhận</span>
              <strong>{formatCurrency(profitSummary.totalRevenue)}</strong>
            </div>
            <div>
              <span>Giá vốn</span>
              <strong>{formatCurrency(profitSummary.totalCost)}</strong>
            </div>
            <div>
              <span>Lợi nhuận gộp</span>
              <strong>{formatCurrency(profitSummary.totalGrossProfit)}</strong>
            </div>
          </div>
        )}
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Mã</th>
              <th>Tên item</th>
              <th>Loại</th>
              <th>Hành vi</th>
              <th>Tồn</th>
              <th>Đang giữ</th>
              <th>Có thể dùng</th>
              <th>Cảnh báo</th>
              <th>Giá vốn TB</th>
              <th>Giá trị tồn</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map((stock) => (
              <tr key={stock.id}>
                <td>{stock.itemCode}</td>
                <td>{stock.itemName}</td>
                <td>{stock.itemType}</td>
                <td>{stock.inventoryBehavior}</td>
                <td>{stock.quantityOnHand} {stock.unit}</td>
                <td>{stock.reservedQuantity}</td>
                <td>{stock.availableQuantity}</td>
                <td>
                  <span className={stock.lowStock ? "inventory-badge danger" : "inventory-badge success"}>
                    {stock.lowStock ? "Sắp hết" : "Ổn"}
                  </span>
                </td>
                <td>{formatCurrency(stock.averageUnitCost)}</td>
                <td>{formatCurrency(stock.inventoryValue)}</td>
                <td>
                  <Button
                    size="sm"
                    variant="outline-warning"
                    onClick={() => openEditItemThreshold(stock)}
                  >
                    Cấu hình
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
        </>
      )}

      {activeTab === "items" && (
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Mã</th>
              <th>Tên</th>
              <th>Loại</th>
              <th>Hành vi</th>
              <th>Reference</th>
              <th>Đơn vị</th>
              <th>Ngưỡng cảnh báo</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.itemCode}</td>
                <td>{item.name}</td>
                <td>{item.itemType}</td>
                <td>{item.inventoryBehavior}</td>
                <td>{item.referenceId || "-"}</td>
                <td>{item.unit}</td>
                <td>{item.lowStockThreshold}</td>
                <td>{item.active ? "Đang dùng" : "Ngừng"}</td>
                <td>
                  <Button
                    size="sm"
                    variant="outline-warning"
                    onClick={() => openEditItemThreshold(item)}
                  >
                    Cấu hình
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {activeTab === "transactions" && (
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Thời gian</th>
              <th>Item</th>
              <th>Loại</th>
              <th>Số lượng</th>
              <th>Trước</th>
              <th>Sau</th>
              <th>Ghi chú</th>
              <th>Người tạo</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((transaction) => (
              <tr key={transaction.id}>
                <td>{new Date(transaction.createdAt).toLocaleString("vi-VN")}</td>
                <td>{transaction.itemName}</td>
                <td>{transaction.transactionType}</td>
                <td>{transaction.quantity}</td>
                <td>{transaction.beforeQuantity}</td>
                <td>{transaction.afterQuantity}</td>
                <td>{transaction.note || "-"}</td>
                <td>{transaction.createdByName || "-"}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {activeTab === "reservations" && (
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Item</th>
              <th>Nghiệp vụ</th>
              <th>Mã tham chiếu</th>
              <th>Số lượng</th>
              <th>Trạng thái</th>
              <th>Thời gian giữ</th>
              <th>Đã chốt</th>
              <th>Đã giải phóng</th>
            </tr>
          </thead>
          <tbody>
            {reservations.map((reservation) => (
              <tr key={reservation.id}>
                <td>#{reservation.inventoryItemId}</td>
                <td>{reservation.referenceType}</td>
                <td>
                  #{reservation.referenceId} / dòng #{reservation.referenceLineId}
                </td>
                <td>{reservation.quantity}</td>
                <td>
                  <span
                    className={`inventory-badge ${
                      reservation.status === "ACTIVE" ? "warning" : "success"
                    }`}
                  >
                    {reservation.status}
                  </span>
                </td>
                <td>{new Date(reservation.reservedAt).toLocaleString("vi-VN")}</td>
                <td>
                  {reservation.committedAt
                    ? new Date(reservation.committedAt).toLocaleString("vi-VN")
                    : "-"}
                </td>
                <td>
                  {reservation.releasedAt
                    ? new Date(reservation.releasedAt).toLocaleString("vi-VN")
                    : "-"}
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {activeTab === "imports" && (
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Mã phiếu</th>
              <th>Nhà cung cấp</th>
              <th>Tổng giá trị</th>
              <th>Người tạo</th>
              <th>Ngày tạo</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {stockImports.length > 0 ? (
              stockImports.map((stockImport) => (
                <tr key={stockImport.id}>
                  <td><strong>{stockImport.importCode}</strong></td>
                  <td>{stockImport.supplierName || "Không có"}</td>
                  <td>{formatCurrency(stockImport.totalAmount)}</td>
                  <td>{stockImport.createdByName || "-"}</td>
                  <td>
                    {new Date(stockImport.createdAt).toLocaleString("vi-VN")}
                  </td>
                  <td>
                    <span
                      className={`inventory-badge ${
                        stockImport.status === "POSTED"
                          ? "success"
                          : stockImport.status === "CANCELLED"
                            ? "danger"
                            : "warning"
                      }`}
                    >
                      {stockImport.status === "POSTED"
                        ? "Đã ghi sổ"
                        : stockImport.status === "CANCELLED"
                          ? "Đã hủy"
                          : "Phiếu nháp"}
                    </span>
                  </td>
                  <td>
                    <div className="inventory-row-actions">
                      <Button
                        size="sm"
                        variant="outline-primary"
                        onClick={() => openStockImportDetail(stockImport.id)}
                      >
                        Xem
                      </Button>
                      {stockImport.status === "DRAFT" && (
                        <>
                          <Button
                            size="sm"
                            variant="outline-success"
                            onClick={() => handlePostStockImport(stockImport)}
                          >
                            Ghi sổ
                          </Button>
                          <Button
                            size="sm"
                            variant="outline-danger"
                            onClick={() => handleCancelStockImport(stockImport)}
                          >
                            Hủy
                          </Button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="7" className="text-center">
                  Chưa có phiếu nhập.
                </td>
              </tr>
            )}
          </tbody>
        </Table>
      )}

      {activeTab === "suppliers" && (
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Nhà cung cấp</th>
              <th>Điện thoại</th>
              <th>Email</th>
              <th>Địa chỉ</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {suppliers.length > 0 ? (
              suppliers.map((supplier) => (
                <tr key={supplier.id}>
                  <td><strong>{supplier.name}</strong></td>
                  <td>{supplier.phone || "-"}</td>
                  <td>{supplier.email || "-"}</td>
                  <td>{supplier.address || "-"}</td>
                  <td>
                    <span
                      className={`inventory-badge ${
                        supplier.active ? "success" : "danger"
                      }`}
                    >
                      {supplier.active ? "Đang hợp tác" : "Đã ngừng"}
                    </span>
                  </td>
                  <td>
                    <div className="inventory-row-actions">
                      <Button
                        size="sm"
                        variant="outline-primary"
                        onClick={() => openEditSupplier(supplier)}
                      >
                        Sửa
                      </Button>
                      <Button
                        size="sm"
                        variant={
                          supplier.active
                            ? "outline-danger"
                            : "outline-success"
                        }
                        onClick={() => handleToggleSupplier(supplier)}
                      >
                        {supplier.active ? "Ngừng" : "Kích hoạt"}
                      </Button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" className="text-center">
                  Chưa có nhà cung cấp.
                </td>
              </tr>
            )}
          </tbody>
        </Table>
      )}

      {activeTab !== "suppliers" && (
      <div className="inventory-pagination">
        <Button disabled={pageNo === 0 || loading} onClick={() => setPageNo(pageNo - 1)}>
          Trước
        </Button>
        <span>Trang {pageNo + 1} / {totalPages}</span>
        <Button
          disabled={pageNo + 1 >= totalPages || loading}
          onClick={() => setPageNo(pageNo + 1)}
        >
          Sau
        </Button>
      </div>
      )}

      <Modal
        show={showItemModal}
        onHide={() => {
          setShowItemModal(false);
          setEditingItemId(null);
          setItemForm(emptyItemForm);
        }}
      >
        <Modal.Header closeButton>
          <Modal.Title>
            {editingItemId ? "Cấu hình item kho" : "Tạo item kho"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form
            onSubmit={
              editingItemId
                ? handleUpdateItemThreshold
                : handleCreateItem
            }
          >
            {!editingItemId && (
              <>
                <Form.Control className="mb-2" placeholder="Mã kho" value={itemForm.itemCode} onChange={(e) => setItemForm({ ...itemForm, itemCode: e.target.value })} required />
                <Form.Control className="mb-2" placeholder="Tên item" value={itemForm.name} onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })} required />
                <Form.Select className="mb-2" value={itemForm.itemType} onChange={(e) => setItemForm({ ...itemForm, itemType: e.target.value })}>
                  {itemTypes.map((type) => <option key={type} value={type}>{type}</option>)}
                </Form.Select>
                <Form.Select className="mb-2" value={itemForm.inventoryBehavior} onChange={(e) => setItemForm({ ...itemForm, inventoryBehavior: e.target.value })}>
                  {inventoryBehaviors.map(([value, label]) => <option key={value} value={value}>{label}</option>)}
                </Form.Select>
                <Form.Control className="mb-2" placeholder="Reference ID nếu có" value={itemForm.referenceId} onChange={(e) => setItemForm({ ...itemForm, referenceId: e.target.value })} />
                <Form.Control className="mb-2" placeholder="Đơn vị" value={itemForm.unit} onChange={(e) => setItemForm({ ...itemForm, unit: e.target.value })} />
              </>
            )}
            {editingItemId && (
              <Form.Select className="mb-2" value={itemForm.inventoryBehavior} onChange={(e) => setItemForm({ ...itemForm, inventoryBehavior: e.target.value })}>
                {inventoryBehaviors.map(([value, label]) => <option key={value} value={value}>{label}</option>)}
              </Form.Select>
            )}
            <Form.Control className="mb-3" type="number" min="0" placeholder="Ngưỡng cảnh báo" value={itemForm.lowStockThreshold} onChange={(e) => setItemForm({ ...itemForm, lowStockThreshold: e.target.value })} />
            <Button type="submit">Lưu</Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal
        show={showProductSyncModal}
        onHide={() => !productSyncLoading && setShowProductSyncModal(false)}
        size="lg"
        centered
      >
        <Modal.Header closeButton={!productSyncLoading}>
          <Modal.Title>Đồng bộ sản phẩm vào kho</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {productSyncLoading && !productSyncPreview ? (
            <p>Đang kiểm tra dữ liệu sản phẩm...</p>
          ) : (
            <>
              <p>
                Hệ thống sẽ tạo một item kho cho từng biến thể sản phẩm chưa
                liên kết. Sản phẩm, đơn hàng và giỏ hàng hiện tại không bị xóa.
              </p>

              {productSyncPreview && (
                <div className="inventory-sync-summary">
                  <div><span>Tổng biến thể</span><strong>{productSyncPreview.totalVariants}</strong></div>
                  <div><span>Đã liên kết</span><strong>{productSyncPreview.linkedVariants}</strong></div>
                  <div><span>Chưa liên kết</span><strong>{productSyncPreview.unlinkedVariants}</strong></div>
                  <div><span>Xung đột</span><strong>{productSyncPreview.conflictVariants}</strong></div>
                </div>
              )}

              {productSyncResult && (
                <>
                  <div className="inventory-sync-result">
                    Đã đồng bộ {productSyncResult.successRows}, bỏ qua{" "}
                    {productSyncResult.skippedRows}, lỗi{" "}
                    {productSyncResult.failedRows}.
                  </div>

                  {productSyncResult.errors?.length > 0 && (
                    <Table responsive size="sm" className="mt-3">
                      <thead>
                        <tr>
                          <th>Variant</th>
                          <th>Mã kho</th>
                          <th>Lỗi</th>
                        </tr>
                      </thead>
                      <tbody>
                        {productSyncResult.errors.map((error) => (
                          <tr key={error.variantId}>
                            <td>#{error.variantId}</td>
                            <td>{error.itemCode}</td>
                            <td>{error.message}</td>
                          </tr>
                        ))}
                      </tbody>
                    </Table>
                  )}
                </>
              )}
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant="outline-secondary"
            disabled={productSyncLoading}
            onClick={() => setShowProductSyncModal(false)}
          >
            Đóng
          </Button>
          {!productSyncResult && (
            <Button
              variant="primary"
              disabled={
                productSyncLoading
                || !productSyncPreview
                || productSyncPreview.unlinkedVariants === 0
              }
              onClick={handleProductSync}
            >
              <BsArrowRepeat />
              {productSyncLoading ? "Đang đồng bộ..." : "Xác nhận đồng bộ"}
            </Button>
          )}
        </Modal.Footer>
      </Modal>

      <Modal
        show={showImportDetailModal}
        onHide={() => setShowImportDetailModal(false)}
        size="lg"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>
            Phiếu nhập {selectedStockImport?.importCode || ""}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedStockImport && (
            <>
              <div className="inventory-import-detail-meta">
                <div>
                  <span>Nhà cung cấp</span>
                  <strong>
                    {selectedStockImport.supplierName || "Không có"}
                  </strong>
                </div>
                <div>
                  <span>Trạng thái</span>
                  <strong>{selectedStockImport.status}</strong>
                </div>
                <div>
                  <span>Người tạo</span>
                  <strong>{selectedStockImport.createdByName || "-"}</strong>
                </div>
                <div>
                  <span>Ngày tạo</span>
                  <strong>
                    {new Date(
                      selectedStockImport.createdAt
                    ).toLocaleString("vi-VN")}
                  </strong>
                </div>
              </div>

              <Table responsive size="sm">
                <thead>
                  <tr>
                    <th>Hàng hóa</th>
                    <th>Số lượng</th>
                    <th>Giá nhập</th>
                    <th>Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedStockImport.items?.map((item) => (
                    <tr key={item.id}>
                      <td>{item.itemCode} - {item.itemName}</td>
                      <td>{item.quantity}</td>
                      <td>{formatCurrency(item.unitCost)}</td>
                      <td>{formatCurrency(item.totalCost)}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>

              <div className="inventory-import-total">
                <span>Tổng giá trị phiếu nhập</span>
                <strong>
                  {formatCurrency(selectedStockImport.totalAmount)}
                </strong>
              </div>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          {selectedStockImport?.status === "DRAFT" && (
            <>
              <Button
                variant="outline-danger"
                onClick={() => handleCancelStockImport(selectedStockImport)}
              >
                Hủy phiếu
              </Button>
              <Button
                variant="success"
                onClick={() => handlePostStockImport(selectedStockImport)}
              >
                Ghi sổ
              </Button>
            </>
          )}
          <Button
            variant="outline-secondary"
            onClick={() => setShowImportDetailModal(false)}
          >
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal
        show={showConsistencyModal}
        onHide={() => !consistencyLoading && setShowConsistencyModal(false)}
        size="lg"
        centered
      >
        <Modal.Header closeButton={!consistencyLoading}>
          <Modal.Title>Kiểm tra tính nhất quán kho</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {consistencyLoading ? (
            <p>Đang đối chiếu dữ liệu kho...</p>
          ) : consistencyResult ? (
            <>
              <div
                className={`inventory-consistency-summary ${
                  consistencyResult.valid ? "valid" : "invalid"
                }`}
              >
                <strong>
                  {consistencyResult.valid
                    ? "Dữ liệu kho đang nhất quán"
                    : `Phát hiện ${consistencyResult.issueCount} vấn đề`}
                </strong>
                <span>
                  {consistencyResult.itemsChecked} item ·{" "}
                  {consistencyResult.stocksChecked} dòng tồn ·{" "}
                  {consistencyResult.activeReservationsChecked} reservation
                </span>
              </div>

              {consistencyResult.issues?.length > 0 && (
                <ul className="inventory-consistency-issues">
                  {consistencyResult.issues.map((issue, index) => (
                    <li key={`${issue}-${index}`}>{issue}</li>
                  ))}
                </ul>
              )}
            </>
          ) : null}
        </Modal.Body>
      </Modal>

      <Modal show={showSupplierModal} onHide={() => setShowSupplierModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo nhà cung cấp</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSaveSupplier}>
            <Form.Control className="mb-2" placeholder="Tên nhà cung cấp" value={supplierForm.name} onChange={(e) => setSupplierForm({ ...supplierForm, name: e.target.value })} required />
            <Form.Control className="mb-2" placeholder="Số điện thoại" value={supplierForm.phone} onChange={(e) => setSupplierForm({ ...supplierForm, phone: e.target.value })} />
            <Form.Control className="mb-2" placeholder="Email" value={supplierForm.email} onChange={(e) => setSupplierForm({ ...supplierForm, email: e.target.value })} />
            <Form.Control className="mb-3" placeholder="Địa chỉ" value={supplierForm.address} onChange={(e) => setSupplierForm({ ...supplierForm, address: e.target.value })} />
            <Button type="submit">Lưu</Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={showImportModal} onHide={() => setShowImportModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Nhập kho</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleImportStock}>
            <Form.Control className="mb-2" placeholder="Mã phiếu nhập tự sinh nếu bỏ trống" value={importForm.importCode} onChange={(e) => setImportForm({ ...importForm, importCode: e.target.value })} />
            <Form.Select className="mb-2" value={importForm.supplierId} onChange={(e) => setImportForm({ ...importForm, supplierId: e.target.value })}>
              <option value="">Không chọn nhà cung cấp</option>
              {suppliers
                .filter((supplier) => supplier.active)
                .map((supplier) => (
                  <option key={supplier.id} value={supplier.id}>
                    {supplier.name}
                  </option>
                ))}
            </Form.Select>
            <Form.Control className="mb-3" placeholder="Ghi chú" value={importForm.note} onChange={(e) => setImportForm({ ...importForm, note: e.target.value })} />

            <div className="inventory-import-head" aria-hidden="true">
              <span>Hàng hóa</span>
              <span>Số lượng</span>
              <span>Giá nhập/đơn vị</span>
              <span>Thành tiền</span>
              <span />
            </div>

            {importForm.items.map((line, index) => (
              <div className="inventory-import-line" key={index}>
                <Form.Select value={line.inventoryItemId} onChange={(e) => updateImportLine(index, "inventoryItemId", e.target.value)} required>
                  <option value="">Chọn item</option>
                  {inventoryOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
                </Form.Select>
                <Form.Control type="number" min="1" value={line.quantity} onChange={(e) => updateImportLine(index, "quantity", e.target.value)} required />
                <Form.Control type="number" min="1" step="1000" value={line.unitCost} onChange={(e) => updateImportLine(index, "unitCost", e.target.value)} required />
                <strong className="inventory-line-total">
                  {formatCurrency(
                    Number(line.quantity || 0) * Number(line.unitCost || 0)
                  )}
                </strong>
                <Button variant="outline-danger" onClick={() => setImportForm((current) => ({ ...current, items: current.items.filter((_, itemIndex) => itemIndex !== index) }))}>
                  Xóa
                </Button>
              </div>
            ))}

            <Button className="mb-3" variant="outline-primary" type="button" onClick={() => setImportForm((current) => ({ ...current, items: [...current.items, { inventoryItemId: "", quantity: 1, unitCost: 0 }] }))}>
              + Thêm dòng
            </Button>
            <div className="inventory-import-total">
              <span>Tổng giá trị phiếu nhập</span>
              <strong>{formatCurrency(importTotal)}</strong>
            </div>
            <div>
              <Button type="submit" variant="success">Lưu phiếu nhập</Button>
            </div>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={showAdjustModal} onHide={() => setShowAdjustModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Điều chỉnh kho</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleAdjustStock}>
            <Form.Select className="mb-2" value={adjustForm.inventoryItemId} onChange={(e) => setAdjustForm({ ...adjustForm, inventoryItemId: e.target.value })} required>
              <option value="">Chọn item</option>
              {inventoryOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
            </Form.Select>
            <Form.Select className="mb-2" value={adjustForm.transactionType} onChange={(e) => setAdjustForm({ ...adjustForm, transactionType: e.target.value })}>
              {adjustmentTypes.map((type) => <option key={type} value={type}>{type}</option>)}
            </Form.Select>
            <Form.Control className="mb-2" type="number" min="1" value={adjustForm.quantity} onChange={(e) => setAdjustForm({ ...adjustForm, quantity: e.target.value })} required />
            <Form.Control className="mb-3" placeholder="Lý do điều chỉnh" value={adjustForm.note} onChange={(e) => setAdjustForm({ ...adjustForm, note: e.target.value })} required />
            <Button type="submit" variant="warning">Lưu điều chỉnh</Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
  );
}
