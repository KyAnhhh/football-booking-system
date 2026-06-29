package vn.phankyanh.shopbongda.inventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReservationStatus;
import vn.phankyanh.shopbongda.inventory.domain.StockImportStatus;
import vn.phankyanh.shopbongda.inventory.dto.request.InventoryItemRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.StockAdjustmentRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.StockImportRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.SupplierRequest;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryItemResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryConsistencyResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryReservationResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryStockResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryTransactionResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.ProductVariantSyncPreviewResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.ProductVariantSyncResultResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.StockImportResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.SupplierResponse;
import vn.phankyanh.shopbongda.inventory.service.InventoryService;
import vn.phankyanh.shopbongda.inventory.service.ProductVariantInventorySyncService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/inventory")
public class AdminInventoryController {

    private final InventoryService inventoryService;
    private final ProductVariantInventorySyncService productVariantSyncService;

    @PostMapping("/items")
    public ApiResponse<InventoryItemResponse> createItem(
            @Valid @RequestBody InventoryItemRequest request
    ) {
        return response(
                "Tạo item kho thành công",
                inventoryService.createItem(request)
        );
    }

    @PutMapping("/items/{id}")
    public ApiResponse<InventoryItemResponse> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryItemRequest request
    ) {
        return response(
                "Cập nhật item kho thành công",
                inventoryService.updateItem(id, request)
        );
    }

    @GetMapping("/items")
    public ApiResponse<Page<InventoryItemResponse>> getItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) InventoryItemType itemType
    ) {
        return response(
                "Lấy danh sách item kho thành công",
                inventoryService.getItems(page, size, search, itemType)
        );
    }

    @GetMapping("/stocks")
    public ApiResponse<Page<InventoryStockResponse>> getStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) InventoryItemType itemType
    ) {
        return response(
                "Lấy tồn kho thành công",
                inventoryService.getStocks(page, size, search, itemType)
        );
    }

    @GetMapping("/transactions")
    public ApiResponse<Page<InventoryTransactionResponse>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long inventoryItemId
    ) {
        return response(
                "Lấy lịch sử kho thành công",
                inventoryService.getTransactions(page, size, inventoryItemId)
        );
    }

    @GetMapping("/reservations")
    public ApiResponse<Page<InventoryReservationResponse>> getReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) InventoryReservationStatus status
    ) {
        return response(
                "Lấy danh sách giữ kho thành công",
                inventoryService.getReservations(page, size, status)
        );
    }

    @PostMapping("/suppliers")
    public ApiResponse<SupplierResponse> createSupplier(
            @Valid @RequestBody SupplierRequest request
    ) {
        return response(
                "Tạo nhà cung cấp thành công",
                inventoryService.createSupplier(request)
        );
    }

    @PutMapping("/suppliers/{id}")
    public ApiResponse<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request
    ) {
        return response(
                "Cập nhật nhà cung cấp thành công",
                inventoryService.updateSupplier(id, request)
        );
    }

    @GetMapping("/suppliers")
    public ApiResponse<Page<SupplierResponse>> getSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return response(
                "Lấy danh sách nhà cung cấp thành công",
                inventoryService.getSuppliers(page, size)
        );
    }

    @PostMapping("/imports")
    public ApiResponse<StockImportResponse> importStock(
            @Valid @RequestBody StockImportRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return response(
                "Nhập kho thành công",
                inventoryService.importStock(request, jwt.getClaim("id"))
        );
    }

    @GetMapping("/imports")
    public ApiResponse<Page<StockImportResponse>> getStockImports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) StockImportStatus status,
            @RequestParam(required = false) Long supplierId
    ) {
        return response(
                "Lấy danh sách phiếu nhập thành công",
                inventoryService.getStockImports(
                        page,
                        size,
                        search,
                        status,
                        supplierId
                )
        );
    }

    @GetMapping("/imports/{id}")
    public ApiResponse<StockImportResponse> getStockImport(
            @PathVariable Long id
    ) {
        return response(
                "Lấy chi tiết phiếu nhập thành công",
                inventoryService.getStockImport(id)
        );
    }

    @PostMapping("/imports/{id}/post")
    public ApiResponse<StockImportResponse> postStockImport(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return response(
                "Ghi sổ phiếu nhập thành công",
                inventoryService.postStockImport(id, jwt.getClaim("id"))
        );
    }

    @PostMapping("/imports/{id}/cancel")
    public ApiResponse<StockImportResponse> cancelStockImport(
            @PathVariable Long id
    ) {
        return response(
                "Hủy phiếu nhập thành công",
                inventoryService.cancelStockImport(id)
        );
    }

    @GetMapping("/consistency")
    public ApiResponse<InventoryConsistencyResponse> checkConsistency() {
        return response(
                "Kiểm tra dữ liệu kho hoàn tất",
                inventoryService.checkConsistency()
        );
    }

    @PostMapping("/adjustments")
    public ApiResponse<InventoryTransactionResponse> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return response(
                "Điều chỉnh kho thành công",
                inventoryService.adjustStock(request, jwt.getClaim("id"))
        );
    }

    @GetMapping("/product-variant-sync/preview")
    public ApiResponse<ProductVariantSyncPreviewResponse>
            previewProductVariantSync() {
        return response(
                "Xem trước đồng bộ sản phẩm thành công",
                productVariantSyncService.preview()
        );
    }

    @PostMapping("/product-variant-sync")
    public ApiResponse<ProductVariantSyncResultResponse>
            syncProductVariants() {
        return response(
                "Đồng bộ sản phẩm vào kho hoàn tất",
                productVariantSyncService.sync()
        );
    }

    private <T> ApiResponse<T> response(String message, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        return apiResponse;
    }
}
