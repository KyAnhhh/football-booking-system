package vn.phankyanh.shopbongda.inventory.service;

import org.springframework.data.domain.Page;

import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReferenceType;
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
import vn.phankyanh.shopbongda.inventory.dto.response.StockImportResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.SupplierResponse;

public interface InventoryService {

    InventoryItemResponse createItem(InventoryItemRequest request);

    InventoryItemResponse updateItem(Long id, InventoryItemRequest request);

    Page<InventoryItemResponse> getItems(
            int page,
            int size,
            String search,
            InventoryItemType itemType
    );

    Page<InventoryStockResponse> getStocks(
            int page,
            int size,
            String search,
            InventoryItemType itemType
    );

    Page<InventoryTransactionResponse> getTransactions(
            int page,
            int size,
            Long inventoryItemId
    );

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    Page<SupplierResponse> getSuppliers(int page, int size);

    StockImportResponse importStock(StockImportRequest request, Long actorId);

    Page<StockImportResponse> getStockImports(
            int page,
            int size,
            String search,
            StockImportStatus status,
            Long supplierId
    );

    StockImportResponse getStockImport(Long id);

    StockImportResponse postStockImport(Long id, Long actorId);

    StockImportResponse cancelStockImport(Long id);

    InventoryConsistencyResponse checkConsistency();

    InventoryTransactionResponse adjustStock(
            StockAdjustmentRequest request,
            Long actorId
    );

    Long createLinkedItem(
            String itemCode,
            String name,
            InventoryItemType itemType,
            Long referenceId,
            String unit,
            Integer initialQuantity
    );

    InventoryTransactionResponse deductForOrder(
            Long inventoryItemId,
            Integer quantity,
            Long orderId,
            Long actorId
    );

    InventoryTransactionResponse returnForOrder(
            Long inventoryItemId,
            Integer quantity,
            Long orderId,
            Long actorId
    );

    InventoryTransactionResponse deductForBookingExtra(
            Long inventoryItemId,
            Integer quantity,
            Long bookingId,
            Long actorId
    );

    InventoryTransactionResponse returnForBookingExtra(
            Long inventoryItemId,
            Integer quantity,
            Long bookingId,
            Long actorId
    );

    InventoryStockResponse getStockByItemId(Long inventoryItemId);

    InventoryReservationResponse reserveStock(
            Long inventoryItemId,
            Integer quantity,
            InventoryReferenceType referenceType,
            Long referenceId,
            Long referenceLineId,
            Long actorId
    );

    InventoryReservationResponse commitReservation(
            Long reservationId,
            Long actorId
    );

    InventoryReservationResponse releaseReservation(
            Long reservationId,
            Long actorId
    );

    Page<InventoryReservationResponse> getReservations(
            int page,
            int size,
            InventoryReservationStatus status
    );
}
