import { useCallback, useEffect, useMemo, useState } from "react";
import { Button, Form, Modal, Table } from "react-bootstrap";
import swal from "sweetalert";

import {
  adjustStock,
  createInventoryItem,
  createSupplier,
  getInventoryItems,
  getInventoryReservations,
  getInventoryStocks,
  getInventoryTransactions,
  getSuppliers,
  importStock,
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

export default function InventoryPage() {
  const [activeTab, setActiveTab] = useState("stocks");
  const [items, setItems] = useState([]);
  const [stocks, setStocks] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [itemPage, setItemPage] = useState(null);
  const [stockPage, setStockPage] = useState(null);
  const [transactionPage, setTransactionPage] = useState(null);
  const [reservationPage, setReservationPage] = useState(null);
  const [pageNo, setPageNo] = useState(0);
  const [search, setSearch] = useState("");
  const [itemType, setItemType] = useState("");
  const [loading, setLoading] = useState(false);

  const [showItemModal, setShowItemModal] = useState(false);
  const [showSupplierModal, setShowSupplierModal] = useState(false);
  const [showImportModal, setShowImportModal] = useState(false);
  const [showAdjustModal, setShowAdjustModal] = useState(false);
  const [itemForm, setItemForm] = useState(emptyItemForm);
  const [supplierForm, setSupplierForm] = useState(emptySupplierForm);
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
    const data = await getInventoryStocks({ page: pageNo, size: 20 });
    setStocks(data.content || []);
    setStockPage(data);
  }, [pageNo]);

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

  const loadSuppliers = useCallback(async () => {
    const data = await getSuppliers({ page: 0, size: 100 });
    setSuppliers(data.content || []);
  }, []);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      await Promise.all([
        loadItems(),
        activeTab === "stocks" ? loadStocks() : Promise.resolve(),
        activeTab === "transactions" ? loadTransactions() : Promise.resolve(),
        activeTab === "reservations" ? loadReservations() : Promise.resolve(),
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
    loadSuppliers,
  ]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const currentPageData =
    activeTab === "items"
      ? itemPage
      : activeTab === "reservations"
        ? reservationPage
      : activeTab === "transactions"
        ? transactionPage
        : stockPage;
  const totalPages = getPageTotal(currentPageData);

  const handleCreateItem = async (event) => {
    event.preventDefault();
    try {
      await createInventoryItem({
        ...itemForm,
        referenceId: itemForm.referenceId ? Number(itemForm.referenceId) : null,
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

  const handleCreateSupplier = async (event) => {
    event.preventDefault();
    try {
      await createSupplier(supplierForm);
      setShowSupplierModal(false);
      setSupplierForm(emptySupplierForm);
      await loadSuppliers();
      swal("Thành công", "Đã tạo nhà cung cấp.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể tạo nhà cung cấp.", "error");
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
      await loadData();
      swal("Thành công", "Nhập kho thành công.", "success");
    } catch (error) {
      swal("Lỗi", error.message || "Không thể nhập kho.", "error");
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

  return (
    <div className="admin-crud-page inventory-page">
      <div className="admin-crud-header">
        <div>
          <span className="admin-crud-kicker">Quản lý kho</span>
          <h2>Inventory</h2>
          <p>Theo dõi tồn kho, nhập kho và lịch sử biến động.</p>
        </div>

        <div className="inventory-actions">
          <Button variant="outline-primary" onClick={() => setShowSupplierModal(true)}>
            + Nhà cung cấp
          </Button>
          <Button variant="outline-success" onClick={() => setShowItemModal(true)}>
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
      </div>

      {activeTab === "stocks" && (
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Mã</th>
              <th>Tên item</th>
              <th>Loại</th>
              <th>Tồn</th>
              <th>Đang giữ</th>
              <th>Có thể dùng</th>
              <th>Cảnh báo</th>
            </tr>
          </thead>
          <tbody>
            {stocks.map((stock) => (
              <tr key={stock.id}>
                <td>{stock.itemCode}</td>
                <td>{stock.itemName}</td>
                <td>{stock.itemType}</td>
                <td>{stock.quantityOnHand} {stock.unit}</td>
                <td>{stock.reservedQuantity}</td>
                <td>{stock.availableQuantity}</td>
                <td>
                  <span className={stock.lowStock ? "inventory-badge danger" : "inventory-badge success"}>
                    {stock.lowStock ? "Sắp hết" : "Ổn"}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {activeTab === "items" && (
        <Table responsive hover className="admin-crud-table">
          <thead>
            <tr>
              <th>Mã</th>
              <th>Tên</th>
              <th>Loại</th>
              <th>Reference</th>
              <th>Đơn vị</th>
              <th>Ngưỡng cảnh báo</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.itemCode}</td>
                <td>{item.name}</td>
                <td>{item.itemType}</td>
                <td>{item.referenceId || "-"}</td>
                <td>{item.unit}</td>
                <td>{item.lowStockThreshold}</td>
                <td>{item.active ? "Đang dùng" : "Ngừng"}</td>
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

      <Modal show={showItemModal} onHide={() => setShowItemModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo item kho</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleCreateItem}>
            <Form.Control className="mb-2" placeholder="Mã kho" value={itemForm.itemCode} onChange={(e) => setItemForm({ ...itemForm, itemCode: e.target.value })} required />
            <Form.Control className="mb-2" placeholder="Tên item" value={itemForm.name} onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })} required />
            <Form.Select className="mb-2" value={itemForm.itemType} onChange={(e) => setItemForm({ ...itemForm, itemType: e.target.value })}>
              {itemTypes.map((type) => <option key={type} value={type}>{type}</option>)}
            </Form.Select>
            <Form.Control className="mb-2" placeholder="Reference ID nếu có" value={itemForm.referenceId} onChange={(e) => setItemForm({ ...itemForm, referenceId: e.target.value })} />
            <Form.Control className="mb-2" placeholder="Đơn vị" value={itemForm.unit} onChange={(e) => setItemForm({ ...itemForm, unit: e.target.value })} />
            <Form.Control className="mb-3" type="number" min="0" placeholder="Ngưỡng cảnh báo" value={itemForm.lowStockThreshold} onChange={(e) => setItemForm({ ...itemForm, lowStockThreshold: e.target.value })} />
            <Button type="submit">Lưu</Button>
          </Form>
        </Modal.Body>
      </Modal>

      <Modal show={showSupplierModal} onHide={() => setShowSupplierModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo nhà cung cấp</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleCreateSupplier}>
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
              {suppliers.map((supplier) => <option key={supplier.id} value={supplier.id}>{supplier.name}</option>)}
            </Form.Select>
            <Form.Control className="mb-3" placeholder="Ghi chú" value={importForm.note} onChange={(e) => setImportForm({ ...importForm, note: e.target.value })} />

            {importForm.items.map((line, index) => (
              <div className="inventory-import-line" key={index}>
                <Form.Select value={line.inventoryItemId} onChange={(e) => updateImportLine(index, "inventoryItemId", e.target.value)} required>
                  <option value="">Chọn item</option>
                  {inventoryOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
                </Form.Select>
                <Form.Control type="number" min="1" value={line.quantity} onChange={(e) => updateImportLine(index, "quantity", e.target.value)} required />
                <Form.Control type="number" min="0" value={line.unitCost} onChange={(e) => updateImportLine(index, "unitCost", e.target.value)} />
                <Button variant="outline-danger" onClick={() => setImportForm((current) => ({ ...current, items: current.items.filter((_, itemIndex) => itemIndex !== index) }))}>
                  Xóa
                </Button>
              </div>
            ))}

            <Button className="mb-3" variant="outline-primary" type="button" onClick={() => setImportForm((current) => ({ ...current, items: [...current.items, { inventoryItemId: "", quantity: 1, unitCost: 0 }] }))}>
              + Thêm dòng
            </Button>
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
