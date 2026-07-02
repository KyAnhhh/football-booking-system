package vn.phankyanh.shopbongda.inventory.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.extra.repository.ExtraServiceRepository;
import vn.phankyanh.shopbongda.extra.repository.BookingExtraServiceRepository;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItem;
import vn.phankyanh.shopbongda.inventory.domain.InventoryBehavior;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReferenceType;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReservation;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReservationStatus;
import vn.phankyanh.shopbongda.inventory.domain.InventoryStock;
import vn.phankyanh.shopbongda.inventory.domain.InventoryTransaction;
import vn.phankyanh.shopbongda.inventory.domain.InventoryTransactionType;
import vn.phankyanh.shopbongda.inventory.domain.StockImport;
import vn.phankyanh.shopbongda.inventory.domain.StockImportItem;
import vn.phankyanh.shopbongda.inventory.domain.StockImportStatus;
import vn.phankyanh.shopbongda.inventory.domain.Supplier;
import vn.phankyanh.shopbongda.inventory.dto.request.InventoryItemRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.InventoryBehaviorRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.LowStockThresholdRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.StockAdjustmentRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.StockImportItemRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.StockImportRequest;
import vn.phankyanh.shopbongda.inventory.dto.request.SupplierRequest;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryItemResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryConsistencyResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryProfitSummaryResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryReservationResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryStockResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryTransactionResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.StockImportItemResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.StockImportResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.SupplierResponse;
import vn.phankyanh.shopbongda.inventory.repository.InventoryItemRepository;
import vn.phankyanh.shopbongda.inventory.repository.InventoryReservationRepository;
import vn.phankyanh.shopbongda.inventory.repository.InventoryStockRepository;
import vn.phankyanh.shopbongda.inventory.repository.InventoryTransactionRepository;
import vn.phankyanh.shopbongda.inventory.repository.StockImportRepository;
import vn.phankyanh.shopbongda.inventory.repository.SupplierRepository;
import vn.phankyanh.shopbongda.inventory.service.InventoryService;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.catalog.service.CatalogPublicationService;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository itemRepository;
    private final InventoryStockRepository stockRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final InventoryReservationRepository reservationRepository;
    private final SupplierRepository supplierRepository;
    private final StockImportRepository stockImportRepository;
    private final UserRepository userRepository;
    private final ExtraServiceRepository extraServiceRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookingExtraServiceRepository bookingExtraServiceRepository;
    private final CatalogPublicationService catalogPublicationService;

    @Override
    @Transactional
    public InventoryItemResponse createItem(InventoryItemRequest request) {
        if (itemRepository.existsByItemCode(request.getItemCode())) {
            throw new RuntimeException("Mã kho đã tồn tại");
        }
        validateUniqueReference(
                request.getItemType(),
                request.getReferenceId(),
                null
        );

        InventoryItem item = InventoryItem.builder()
                .itemCode(request.getItemCode().trim())
                .name(request.getName().trim())
                .itemType(request.getItemType())
                .inventoryBehavior(defaultBehavior(
                        request.getInventoryBehavior(),
                        request.getItemType()
                ))
                .referenceId(request.getReferenceId())
                .unit(defaultText(request.getUnit(), "cái"))
                .active(request.getActive() == null || request.getActive())
                .lowStockThreshold(defaultNumber(request.getLowStockThreshold()))
                .build();

        InventoryItem savedItem = itemRepository.save(item);

        InventoryStock stock = InventoryStock.builder()
                .inventoryItem(savedItem)
                .quantityOnHand(0)
                .reservedQuantity(0)
                .availableQuantity(0)
                .build();

        stockRepository.save(stock);

        return toItemResponse(savedItem);
    }

    @Override
    @Transactional
    public InventoryItemResponse updateItem(Long id, InventoryItemRequest request) {
        InventoryItem item = getItem(id);

        if (!item.getItemCode().equals(request.getItemCode())
                && itemRepository.existsByItemCode(request.getItemCode())) {
            throw new RuntimeException("Mã kho đã tồn tại");
        }
        validateUniqueReference(
                request.getItemType(),
                request.getReferenceId(),
                id
        );

        item.setItemCode(request.getItemCode().trim());
        item.setName(request.getName().trim());
        item.setItemType(request.getItemType());
        item.setInventoryBehavior(defaultBehavior(
                request.getInventoryBehavior(),
                request.getItemType()
        ));
        item.setReferenceId(request.getReferenceId());
        item.setUnit(defaultText(request.getUnit(), item.getUnit()));
        item.setActive(request.getActive() == null || request.getActive());
        item.setLowStockThreshold(defaultNumber(request.getLowStockThreshold()));

        return toItemResponse(itemRepository.save(item));
    }

    @Override
    @Transactional
    public InventoryItemResponse updateLowStockThreshold(
            Long id,
            LowStockThresholdRequest request
    ) {
        InventoryItem item = getItem(id);
        item.setLowStockThreshold(request.getLowStockThreshold());
        return toItemResponse(itemRepository.save(item));
    }

    @Override
    @Transactional
    public InventoryItemResponse updateInventoryBehavior(
            Long id,
            InventoryBehaviorRequest request
    ) {
        InventoryItem item = getItem(id);
        item.setInventoryBehavior(request.getInventoryBehavior());
        return toItemResponse(itemRepository.save(item));
    }

    @Override
    public Page<InventoryItemResponse> getItems(
            int page,
            int size,
            String search,
            InventoryItemType itemType
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        String normalizedSearch = search == null || search.isBlank()
                ? null
                : search.trim();

        return itemRepository.search(normalizedSearch, itemType, pageable)
                .map(this::toItemResponse);
    }

    @Override
    public Page<InventoryStockResponse> getStocks(
            int page,
            int size,
            String search,
            InventoryItemType itemType,
            boolean lowStockOnly
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("updatedAt").descending()
        );

        String normalizedSearch = search == null || search.isBlank()
                ? null
                : search.trim();

        return stockRepository.search(
                        normalizedSearch,
                        itemType,
                        lowStockOnly,
                        pageable
                )
                .map(this::toStockResponse);
    }

    @Override
    public Page<InventoryTransactionResponse> getTransactions(
            int page,
            int size,
            Long inventoryItemId
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        if (inventoryItemId != null) {
            return transactionRepository
                    .findByInventoryItemId(inventoryItemId, pageable)
                    .map(this::toTransactionResponse);
        }

        return transactionRepository.findAll(pageable)
                .map(this::toTransactionResponse);
    }

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new IllegalArgumentException("Tên nhà cung cấp đã tồn tại");
        }

        Supplier supplier = Supplier.builder()
                .name(request.getName().trim())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .active(request.getActive() == null || request.getActive())
                .build();

        return toSupplierResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        supplierRepository.findByNameIgnoreCase(request.getName().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Tên nhà cung cấp đã tồn tại"
                    );
                });

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

        supplier.setName(request.getName().trim());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setActive(request.getActive() == null || request.getActive());

        return toSupplierResponse(supplierRepository.save(supplier));
    }

    @Override
    public Page<SupplierResponse> getSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        return supplierRepository.findAll(pageable)
                .map(this::toSupplierResponse);
    }

    @Override
    @Transactional
    public StockImportResponse importStock(
            StockImportRequest request,
            Long actorId
    ) {
        User actor = getActor(actorId);
        Supplier supplier = request.getSupplierId() == null
                ? null
                : supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

        if (supplier != null && !Boolean.TRUE.equals(supplier.getActive())) {
            throw new IllegalStateException(
                    "Nhà cung cấp đã ngừng hoạt động, không thể nhập kho"
            );
        }

        String importCode = resolveImportCode(request.getImportCode());
        if (stockImportRepository.existsByImportCode(importCode)) {
            throw new IllegalArgumentException("Mã phiếu nhập đã tồn tại");
        }

        StockImport stockImport = StockImport.builder()
                .importCode(importCode)
                .supplier(supplier)
                .note(request.getNote())
                .createdBy(actor)
                .totalAmount(BigDecimal.ZERO)
                .status(StockImportStatus.DRAFT)
                .items(new ArrayList<>())
                .build();

        StockImport savedImport = stockImportRepository.save(stockImport);
        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<Long> itemIds = new HashSet<>();

        for (StockImportItemRequest itemRequest : request.getItems()) {
            InventoryItem item = getItem(itemRequest.getInventoryItemId());
            if (!Boolean.TRUE.equals(item.getActive())) {
                throw new IllegalStateException(
                        "Item kho đã ngừng hoạt động: " + item.getName()
                );
            }
            if (!itemIds.add(item.getId())) {
                throw new IllegalArgumentException(
                        "Item kho bị lặp trong cùng phiếu: " + item.getItemCode()
                );
            }
            BigDecimal unitCost = itemRequest.getUnitCost() == null
                    ? BigDecimal.ZERO
                    : itemRequest.getUnitCost();
            BigDecimal totalCost = unitCost.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            );

            StockImportItem importItem = StockImportItem.builder()
                    .stockImport(savedImport)
                    .inventoryItem(item)
                    .quantity(itemRequest.getQuantity())
                    .unitCost(unitCost)
                    .totalCost(totalCost)
                    .build();

            savedImport.getItems().add(importItem);
            totalAmount = totalAmount.add(totalCost);

        }

        savedImport.setTotalAmount(totalAmount);

        return toStockImportResponse(stockImportRepository.save(savedImport));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockImportResponse> getStockImports(
            int page,
            int size,
            String search,
            StockImportStatus status,
            Long supplierId
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );
        String normalizedSearch =
                search == null || search.isBlank() ? null : search.trim();

        return stockImportRepository.search(
                normalizedSearch,
                status,
                supplierId,
                pageable
        ).map(this::toStockImportResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StockImportResponse getStockImport(Long id) {
        return toStockImportResponse(
                stockImportRepository.findDetailById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy phiếu nhập"))
        );
    }

    @Override
    @Transactional
    public StockImportResponse postStockImport(Long id, Long actorId) {
        StockImport stockImport = stockImportRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phiếu nhập"));

        if (stockImport.getStatus() == StockImportStatus.POSTED) {
            return toStockImportResponse(stockImport);
        }
        if (stockImport.getStatus() != StockImportStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể ghi sổ phiếu nháp");
        }
        if (stockImport.getSupplier() != null
                && !Boolean.TRUE.equals(stockImport.getSupplier().getActive())) {
            throw new IllegalStateException("Nhà cung cấp đã ngừng hoạt động");
        }

        User actor = getActor(actorId);
        for (StockImportItem line : stockImport.getItems()) {
            if (!Boolean.TRUE.equals(line.getInventoryItem().getActive())) {
                throw new IllegalStateException(
                        "Item kho đã ngừng hoạt động: "
                                + line.getInventoryItem().getName()
                );
            }

            receiveStock(
                    line.getInventoryItem(),
                    line.getQuantity(),
                    line.getUnitCost(),
                    stockImport,
                    actor
            );
        }

        stockImport.setStatus(StockImportStatus.POSTED);
        stockImport.setPostedAt(LocalDateTime.now());
        return toStockImportResponse(stockImportRepository.save(stockImport));
    }

    @Override
    @Transactional
    public StockImportResponse cancelStockImport(Long id) {
        StockImport stockImport = stockImportRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phiếu nhập"));

        if (stockImport.getStatus() == StockImportStatus.CANCELLED) {
            return toStockImportResponse(stockImport);
        }
        if (stockImport.getStatus() != StockImportStatus.DRAFT) {
            throw new IllegalStateException(
                    "Không thể hủy phiếu đã ghi sổ"
            );
        }

        stockImport.setStatus(StockImportStatus.CANCELLED);
        stockImport.setCancelledAt(LocalDateTime.now());
        return toStockImportResponse(stockImportRepository.save(stockImport));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryConsistencyResponse checkConsistency() {
        List<InventoryItem> items = itemRepository.findAll();
        List<InventoryStock> stocks = stockRepository.findAll();
        List<InventoryReservation> activeReservations =
                reservationRepository.findAllByStatus(
                        InventoryReservationStatus.ACTIVE
                );
        List<String> issues = new ArrayList<>();

        Map<Long, InventoryStock> stocksByItem = new HashMap<>();
        for (InventoryStock stock : stocks) {
            Long itemId = stock.getInventoryItem().getId();
            stocksByItem.put(itemId, stock);

            if (stock.getQuantityOnHand() < 0
                    || stock.getReservedQuantity() < 0
                    || stock.getAvailableQuantity() < 0) {
                issues.add("Item #" + itemId + " có số lượng âm");
            }
            if (stock.getReservedQuantity() > stock.getQuantityOnHand()) {
                issues.add("Item #" + itemId + " giữ vượt tồn");
            }
            int expectedAvailable =
                    stock.getQuantityOnHand() - stock.getReservedQuantity();
            if (stock.getAvailableQuantity() != expectedAvailable) {
                issues.add("Item #" + itemId + " sai tồn khả dụng");
            }
            BigDecimal expectedValue = stock.getAverageUnitCost()
                    .multiply(BigDecimal.valueOf(stock.getQuantityOnHand()))
                    .setScale(2, RoundingMode.HALF_UP);
            if (stock.getAverageUnitCost().compareTo(BigDecimal.ZERO) < 0
                    || stock.getInventoryValue().compareTo(expectedValue) != 0) {
                issues.add("Item #" + itemId + " sai giá trị tồn kho");
            }

            transactionRepository
                    .findTopByInventoryItemIdOrderByIdDesc(itemId)
                    .ifPresent(transaction -> {
                        if (!transaction.getAfterQuantity()
                                .equals(stock.getQuantityOnHand())) {
                            issues.add(
                                    "Item #" + itemId
                                            + " không khớp transaction cuối"
                            );
                        }
                    });
        }

        for (InventoryItem item : items) {
            if (!stocksByItem.containsKey(item.getId())) {
                issues.add("Item #" + item.getId() + " chưa có dòng tồn kho");
            }
        }

        Map<Long, Integer> activeReservedByItem = new HashMap<>();
        for (InventoryReservation reservation : activeReservations) {
            activeReservedByItem.merge(
                    reservation.getInventoryItem().getId(),
                    reservation.getQuantity(),
                    Integer::sum
            );
        }
        for (InventoryStock stock : stocks) {
            int expectedReserved = activeReservedByItem.getOrDefault(
                    stock.getInventoryItem().getId(),
                    0
            );
            if (stock.getReservedQuantity() != expectedReserved) {
                issues.add(
                        "Item #" + stock.getInventoryItem().getId()
                                + " không khớp tổng reservation đang hoạt động"
                );
            }
        }

        return InventoryConsistencyResponse.builder()
                .valid(issues.isEmpty())
                .checkedAt(LocalDateTime.now())
                .itemsChecked(items.size())
                .stocksChecked(stocks.size())
                .activeReservationsChecked(activeReservations.size())
                .issueCount(issues.size())
                .issues(issues)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryProfitSummaryResponse getProfitSummary() {
        List<OrderStatus> orderStatuses = List.of(
                OrderStatus.CONFIRMED,
                OrderStatus.SHIPPING,
                OrderStatus.DELIVERED,
                OrderStatus.COMPLETED
        );
        List<BookingStatus> bookingStatuses = List.of(
                BookingStatus.CONFIRMED,
                BookingStatus.IN_PROGRESS,
                BookingStatus.COMPLETED
        );

        BigDecimal productRevenue =
                orderItemRepository.sumRecognizedRevenue(orderStatuses);
        BigDecimal productCost =
                orderItemRepository.sumRecognizedCost(orderStatuses);
        BigDecimal productProfit =
                orderItemRepository.sumRecognizedGrossProfit(orderStatuses);
        BigDecimal extraRevenue =
                bookingExtraServiceRepository.sumRecognizedRevenue(
                        bookingStatuses
                );
        BigDecimal extraCost =
                bookingExtraServiceRepository.sumRecognizedCost(
                        bookingStatuses
                );
        BigDecimal extraProfit =
                bookingExtraServiceRepository.sumRecognizedGrossProfit(
                        bookingStatuses
                );

        return InventoryProfitSummaryResponse.builder()
                .productRevenue(productRevenue)
                .productCost(productCost)
                .productGrossProfit(productProfit)
                .extraRevenue(extraRevenue)
                .extraCost(extraCost)
                .extraGrossProfit(extraProfit)
                .totalRevenue(productRevenue.add(extraRevenue))
                .totalCost(productCost.add(extraCost))
                .totalGrossProfit(productProfit.add(extraProfit))
                .build();
    }

    @Override
    @Transactional
    public InventoryTransactionResponse adjustStock(
            StockAdjustmentRequest request,
            Long actorId
    ) {
        User actor = getActor(actorId);

        InventoryTransactionType type = request.getTransactionType();
        if (type != InventoryTransactionType.ADJUSTMENT_INCREASE
                && type != InventoryTransactionType.ADJUSTMENT_DECREASE
                && type != InventoryTransactionType.DAMAGED
                && type != InventoryTransactionType.LOST
                && type != InventoryTransactionType.EXPORT) {
            throw new RuntimeException("Loại điều chỉnh kho không hợp lệ");
        }

        int delta = type == InventoryTransactionType.ADJUSTMENT_INCREASE
                ? request.getQuantity()
                : -request.getQuantity();

        InventoryTransaction transaction = changeStock(
                getItem(request.getInventoryItemId()),
                delta,
                type,
                InventoryReferenceType.MANUAL_ADJUSTMENT,
                null,
                request.getNote(),
                actor
        );

        return toTransactionResponse(transaction);
    }

    @Override
    @Transactional
    public Long createLinkedItem(
            String itemCode,
            String name,
            InventoryItemType itemType,
            Long referenceId,
            String unit
    ) {
        if (itemRepository.existsByItemCode(itemCode)) {
            throw new RuntimeException("Mã kho đã tồn tại");
        }
        validateUniqueReference(itemType, referenceId, null);

        InventoryItem item = itemRepository.save(
                InventoryItem.builder()
                        .itemCode(itemCode)
                        .name(name)
                        .itemType(itemType)
                        .inventoryBehavior(defaultBehavior(null, itemType))
                        .referenceId(referenceId)
                        .unit(defaultText(unit, "cái"))
                        .active(true)
                        .lowStockThreshold(0)
                        .build()
        );

        stockRepository.save(
                InventoryStock.builder()
                        .inventoryItem(item)
                        .quantityOnHand(0)
                        .reservedQuantity(0)
                        .availableQuantity(0)
                        .build()
        );

        return item.getId();
    }

    @Override
    @Transactional
    public InventoryTransactionResponse deductForOrder(
            Long inventoryItemId,
            Integer quantity,
            Long orderId,
            Long actorId
    ) {
        return toTransactionResponse(
                changeStock(
                        getItem(inventoryItemId),
                        -quantity,
                        InventoryTransactionType.SALE,
                        InventoryReferenceType.ORDER,
                        orderId,
                        "Trừ kho khi tạo đơn hàng",
                        getActor(actorId)
                )
        );
    }

    @Override
    @Transactional
    public InventoryTransactionResponse returnForOrder(
            Long inventoryItemId,
            Integer quantity,
            Long orderId,
            Long actorId
    ) {
        return toTransactionResponse(
                changeStock(
                        getItem(inventoryItemId),
                        quantity,
                        InventoryTransactionType.RETURN,
                        InventoryReferenceType.ORDER,
                        orderId,
                        "Hoàn kho khi hủy đơn hàng",
                        getActor(actorId)
                )
        );
    }

    @Override
    @Transactional
    public InventoryTransactionResponse deductForBookingExtra(
            Long inventoryItemId,
            Integer quantity,
            Long bookingId,
            Long actorId
    ) {
        return toTransactionResponse(
                changeStock(
                        getItem(inventoryItemId),
                        -quantity,
                        InventoryTransactionType.BOOKING_EXTRA,
                        InventoryReferenceType.BOOKING,
                        bookingId,
                        "Trừ kho dịch vụ thêm khi tạo booking",
                        getActor(actorId)
                )
        );
    }

    @Override
    @Transactional
    public InventoryTransactionResponse returnForBookingExtra(
            Long inventoryItemId,
            Integer quantity,
            Long bookingId,
            Long actorId
    ) {
        return toTransactionResponse(
                changeStock(
                        getItem(inventoryItemId),
                        quantity,
                        InventoryTransactionType.RETURN,
                        InventoryReferenceType.BOOKING,
                        bookingId,
                        "Hoàn kho dịch vụ thêm khi hủy booking",
                        getActor(actorId)
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryStockResponse getStockByItemId(Long inventoryItemId) {
        InventoryStock stock = stockRepository.findByInventoryItemId(inventoryItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho"));

        return toStockResponse(stock);
    }

    @Override
    @Transactional
    public InventoryReservationResponse reserveStock(
            Long inventoryItemId,
            Integer quantity,
            InventoryReferenceType referenceType,
            Long referenceId,
            Long referenceLineId,
            Long actorId
    ) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng giữ kho phải lớn hơn 0");
        }
        if (referenceType == null || referenceId == null || referenceLineId == null) {
            throw new IllegalArgumentException("Thông tin tham chiếu giữ kho không đầy đủ");
        }

        InventoryItem item = getItem(inventoryItemId);
        InventoryStock stock = getStockForUpdate(item);
        int available = stock.getQuantityOnHand() - stock.getReservedQuantity();
        if (available < quantity) {
            throw new RuntimeException("Tồn kho khả dụng không đủ");
        }

        int beforeReserved = stock.getReservedQuantity();
        stock.setReservedQuantity(beforeReserved + quantity);
        stock.setAvailableQuantity(
                stock.getQuantityOnHand() - stock.getReservedQuantity()
        );
        updateInventoryValue(stock);
        stockRepository.save(stock);
        syncLinkedStock(item, stock);

        User actor = getActor(actorId);
        InventoryReservation reservation = reservationRepository.save(
                InventoryReservation.builder()
                        .inventoryItem(item)
                        .referenceType(referenceType)
                        .referenceId(referenceId)
                        .referenceLineId(referenceLineId)
                        .quantity(quantity)
                        .status(InventoryReservationStatus.ACTIVE)
                        .expiresAt(referenceType == InventoryReferenceType.BOOKING
                                ? LocalDateTime.now().plusMinutes(15)
                                : null)
                        .createdBy(actor)
                        .build()
        );

        recordInventoryEvent(
                item,
                InventoryTransactionType.RESERVE,
                quantity,
                stock.getQuantityOnHand(),
                stock.getQuantityOnHand(),
                beforeReserved,
                stock.getReservedQuantity(),
                referenceType,
                referenceId,
                "Giữ tồn kho",
                actor
        );

        return toReservationResponse(reservation, stock);
    }

    @Override
    @Transactional
    public InventoryReservationResponse commitReservation(
            Long reservationId,
            Long actorId
    ) {
        InventoryReservation reservation = getReservationForUpdate(reservationId);
        if (reservation.getStatus() == InventoryReservationStatus.COMMITTED) {
            return toReservationResponse(
                    reservation,
                    getStockForUpdate(reservation.getInventoryItem())
            );
        }
        if (reservation.getStatus() != InventoryReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation không còn hiệu lực");
        }

        InventoryStock stock = getStockForUpdate(reservation.getInventoryItem());
        int quantity = reservation.getQuantity();
        int beforeQuantity = stock.getQuantityOnHand();
        int beforeReserved = stock.getReservedQuantity();
        if (beforeReserved < quantity || beforeQuantity < quantity) {
            throw new IllegalStateException("Dữ liệu giữ kho không hợp lệ");
        }

        stock.setQuantityOnHand(beforeQuantity - quantity);
        stock.setReservedQuantity(beforeReserved - quantity);
        stock.setAvailableQuantity(
                stock.getQuantityOnHand() - stock.getReservedQuantity()
        );
        updateInventoryValue(stock);
        stockRepository.save(stock);
        syncLinkedStock(reservation.getInventoryItem(), stock);

        reservation.setStatus(InventoryReservationStatus.COMMITTED);
        reservation.setCommittedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        recordInventoryEvent(
                reservation.getInventoryItem(),
                InventoryTransactionType.COMMIT_RESERVATION,
                quantity,
                beforeQuantity,
                stock.getQuantityOnHand(),
                beforeReserved,
                stock.getReservedQuantity(),
                reservation.getReferenceType(),
                reservation.getReferenceId(),
                "Chốt giữ kho",
                getActor(actorId)
        );

        return toReservationResponse(reservation, stock);
    }

    @Override
    @Transactional
    public InventoryReservationResponse releaseReservation(
            Long reservationId,
            Long actorId
    ) {
        InventoryReservation reservation = getReservationForUpdate(reservationId);
        if (reservation.getStatus() == InventoryReservationStatus.RELEASED
                || reservation.getStatus() == InventoryReservationStatus.EXPIRED) {
            return toReservationResponse(
                    reservation,
                    getStockForUpdate(reservation.getInventoryItem())
            );
        }
        if (reservation.getStatus() != InventoryReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation đã được chốt, không thể giải phóng");
        }

        InventoryStock stock = getStockForUpdate(reservation.getInventoryItem());
        int beforeReserved = stock.getReservedQuantity();
        if (beforeReserved < reservation.getQuantity()) {
            throw new IllegalStateException("Dữ liệu giữ kho không hợp lệ");
        }

        stock.setReservedQuantity(beforeReserved - reservation.getQuantity());
        stock.setAvailableQuantity(
                stock.getQuantityOnHand() - stock.getReservedQuantity()
        );
        stockRepository.save(stock);
        syncLinkedStock(reservation.getInventoryItem(), stock);

        reservation.setStatus(InventoryReservationStatus.RELEASED);
        reservation.setReleasedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        recordInventoryEvent(
                reservation.getInventoryItem(),
                InventoryTransactionType.RELEASE,
                reservation.getQuantity(),
                stock.getQuantityOnHand(),
                stock.getQuantityOnHand(),
                beforeReserved,
                stock.getReservedQuantity(),
                reservation.getReferenceType(),
                reservation.getReferenceId(),
                "Giải phóng giữ kho",
                getActor(actorId)
        );

        return toReservationResponse(reservation, stock);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryReservationResponse> getReservations(
            int page,
            int size,
            InventoryReservationStatus status
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );
        Page<InventoryReservation> reservations = status == null
                ? reservationRepository.findAll(pageable)
                : reservationRepository.findByStatus(status, pageable);

        return reservations.map(reservation -> {
            InventoryStock stock = stockRepository
                    .findByInventoryItemId(reservation.getInventoryItem().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tồn kho"));
            return toReservationResponse(reservation, stock);
        });
    }

    private InventoryTransaction changeStock(
            InventoryItem item,
            int delta,
            InventoryTransactionType type,
            InventoryReferenceType referenceType,
            Long referenceId,
            String note,
            User actor
    ) {
        InventoryStock stock = getStockForUpdate(item);

        int before = stock.getQuantityOnHand();
        int after = before + delta;
        if (after < stock.getReservedQuantity()) {
            throw new RuntimeException("Tồn kho không đủ");
        }

        stock.setQuantityOnHand(after);
        stock.setAvailableQuantity(Math.max(after - stock.getReservedQuantity(), 0));
        updateInventoryValue(stock);
        stockRepository.save(stock);
        syncLinkedStock(item, stock);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryItem(item)
                .transactionType(type)
                .quantity(Math.abs(delta))
                .beforeQuantity(before)
                .afterQuantity(after)
                .beforeReservedQuantity(stock.getReservedQuantity())
                .afterReservedQuantity(stock.getReservedQuantity())
                .referenceType(referenceType)
                .referenceId(referenceId)
                .note(note)
                .createdBy(actor)
                .build();

        return transactionRepository.save(transaction);
    }

    private void receiveStock(
            InventoryItem item,
            int quantity,
            BigDecimal unitCost,
            StockImport stockImport,
            User actor
    ) {
        InventoryStock stock = getStockForUpdate(item);
        int before = stock.getQuantityOnHand();
        int after = before + quantity;
        BigDecimal oldValue = stock.getAverageUnitCost()
                .multiply(BigDecimal.valueOf(before));
        BigDecimal receivedValue = unitCost.multiply(
                BigDecimal.valueOf(quantity)
        );
        BigDecimal averageCost = oldValue.add(receivedValue).divide(
                BigDecimal.valueOf(after),
                2,
                RoundingMode.HALF_UP
        );

        stock.setQuantityOnHand(after);
        stock.setAverageUnitCost(averageCost);
        stock.setAvailableQuantity(after - stock.getReservedQuantity());
        updateInventoryValue(stock);
        stockRepository.save(stock);
        syncLinkedStock(item, stock);
        catalogPublicationService.refreshReadinessForInventoryItem(item.getId());

        transactionRepository.save(
                InventoryTransaction.builder()
                        .inventoryItem(item)
                        .transactionType(InventoryTransactionType.IMPORT)
                        .quantity(quantity)
                        .beforeQuantity(before)
                        .afterQuantity(after)
                        .beforeReservedQuantity(stock.getReservedQuantity())
                        .afterReservedQuantity(stock.getReservedQuantity())
                        .referenceType(InventoryReferenceType.STOCK_IMPORT)
                        .referenceId(stockImport.getId())
                        .note("Ghi sổ phiếu nhập "
                                + stockImport.getImportCode())
                        .createdBy(actor)
                        .build()
        );
    }

    private void updateInventoryValue(InventoryStock stock) {
        BigDecimal averageCost = stock.getAverageUnitCost() == null
                ? BigDecimal.ZERO
                : stock.getAverageUnitCost();
        stock.setInventoryValue(
                averageCost.multiply(
                        BigDecimal.valueOf(stock.getQuantityOnHand())
                ).setScale(2, RoundingMode.HALF_UP)
        );
    }

    private InventoryStock getStockForUpdate(InventoryItem item) {
        return stockRepository
                .findByInventoryItemIdForUpdate(item.getId())
                .orElseGet(() -> stockRepository.save(
                        InventoryStock.builder()
                                .inventoryItem(item)
                                .quantityOnHand(0)
                                .reservedQuantity(0)
                                .availableQuantity(0)
                                .build()
                ));
    }

    private InventoryReservation getReservationForUpdate(Long reservationId) {
        return reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy reservation"));
    }

    private void recordInventoryEvent(
            InventoryItem item,
            InventoryTransactionType type,
            int quantity,
            int beforeQuantity,
            int afterQuantity,
            int beforeReserved,
            int afterReserved,
            InventoryReferenceType referenceType,
            Long referenceId,
            String note,
            User actor
    ) {
        transactionRepository.save(
                InventoryTransaction.builder()
                        .inventoryItem(item)
                        .transactionType(type)
                        .quantity(quantity)
                        .beforeQuantity(beforeQuantity)
                        .afterQuantity(afterQuantity)
                        .beforeReservedQuantity(beforeReserved)
                        .afterReservedQuantity(afterReserved)
                        .referenceType(referenceType)
                        .referenceId(referenceId)
                        .note(note)
                        .createdBy(actor)
                        .build()
        );
    }

    private InventoryItem getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item kho"));
    }

    private void syncLinkedStock(
            InventoryItem item,
            InventoryStock stock
    ) {
        if (item.getReferenceId() == null) {
            return;
        }

        if (item.getItemType() == InventoryItemType.EXTRA_SERVICE) {
            extraServiceRepository.findById(item.getReferenceId())
                    .ifPresent(extraService -> {
                        extraService.setStockQuantity(stock.getQuantityOnHand());
                        extraService.setRemainingQuantity(
                                stock.getAvailableQuantity()
                        );
                        extraServiceRepository.save(extraService);
                    });
            return;
        }

        if (item.getItemType() == InventoryItemType.PRODUCT_VARIANT) {
            productVariantRepository.findById(item.getReferenceId())
                    .ifPresent(productVariant -> {
                        productVariant.setStock(stock.getAvailableQuantity());
                        productVariantRepository.save(productVariant);
                    });
        }
    }

    private void validateUniqueReference(
            InventoryItemType itemType,
            Long referenceId,
            Long currentItemId
    ) {
        if (itemType == null || referenceId == null) {
            return;
        }

        itemRepository.findByItemTypeAndReferenceId(itemType, referenceId)
                .filter(existing -> !existing.getId().equals(currentItemId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Dữ liệu nguồn đã được liên kết với item kho "
                                    + existing.getItemCode()
                    );
                });
    }

    private User getActor(Long actorId) {
        if (actorId == null) {
            return null;
        }

        return userRepository.findById(actorId)
                .orElse(null);
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank()
                ? fallback
                : value.trim();
    }

    private int defaultNumber(Integer value) {
        return value == null ? 0 : Math.max(value, 0);
    }

    private InventoryBehavior defaultBehavior(
            InventoryBehavior behavior,
            InventoryItemType itemType
    ) {
        if (behavior != null) {
            return behavior;
        }
        if (itemType == InventoryItemType.PRODUCT_VARIANT) {
            return InventoryBehavior.SELLABLE;
        }
        if (itemType == InventoryItemType.FIELD_ASSET) {
            return InventoryBehavior.RENTAL;
        }
        return InventoryBehavior.CONSUMABLE;
    }

    private String resolveImportCode(String importCode) {
        if (importCode != null && !importCode.isBlank()) {
            return importCode.trim();
        }

        return "IMP-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private InventoryItemResponse toItemResponse(InventoryItem item) {
        return InventoryItemResponse.builder()
                .id(item.getId())
                .itemCode(item.getItemCode())
                .name(item.getName())
                .itemType(item.getItemType())
                .inventoryBehavior(item.getInventoryBehavior())
                .referenceId(item.getReferenceId())
                .unit(item.getUnit())
                .active(item.getActive())
                .lowStockThreshold(item.getLowStockThreshold())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private InventoryStockResponse toStockResponse(InventoryStock stock) {
        InventoryItem item = stock.getInventoryItem();
        int threshold = item.getLowStockThreshold() == null
                ? 0
                : item.getLowStockThreshold();

        return InventoryStockResponse.builder()
                .id(stock.getId())
                .inventoryItemId(item.getId())
                .itemCode(item.getItemCode())
                .itemName(item.getName())
                .itemType(item.getItemType())
                .inventoryBehavior(item.getInventoryBehavior())
                .unit(item.getUnit())
                .lowStockThreshold(threshold)
                .quantityOnHand(stock.getQuantityOnHand())
                .reservedQuantity(stock.getReservedQuantity())
                .availableQuantity(stock.getAvailableQuantity())
                .lowStock(threshold > 0 && stock.getAvailableQuantity() <= threshold)
                .averageUnitCost(stock.getAverageUnitCost())
                .inventoryValue(stock.getInventoryValue())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

    private InventoryTransactionResponse toTransactionResponse(
            InventoryTransaction transaction
    ) {
        InventoryItem item = transaction.getInventoryItem();
        User actor = transaction.getCreatedBy();

        return InventoryTransactionResponse.builder()
                .id(transaction.getId())
                .inventoryItemId(item.getId())
                .itemCode(item.getItemCode())
                .itemName(item.getName())
                .transactionType(transaction.getTransactionType())
                .quantity(transaction.getQuantity())
                .beforeQuantity(transaction.getBeforeQuantity())
                .afterQuantity(transaction.getAfterQuantity())
                .beforeReservedQuantity(transaction.getBeforeReservedQuantity())
                .afterReservedQuantity(transaction.getAfterReservedQuantity())
                .referenceType(transaction.getReferenceType())
                .referenceId(transaction.getReferenceId())
                .note(transaction.getNote())
                .createdById(actor == null ? null : actor.getId())
                .createdByName(actor == null ? null : actor.getUsername())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private InventoryReservationResponse toReservationResponse(
            InventoryReservation reservation,
            InventoryStock stock
    ) {
        return InventoryReservationResponse.builder()
                .id(reservation.getId())
                .inventoryItemId(reservation.getInventoryItem().getId())
                .referenceType(reservation.getReferenceType())
                .referenceId(reservation.getReferenceId())
                .referenceLineId(reservation.getReferenceLineId())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus())
                .quantityOnHand(stock.getQuantityOnHand())
                .reservedQuantity(stock.getReservedQuantity())
                .availableQuantity(stock.getAvailableQuantity())
                .reservedAt(reservation.getReservedAt())
                .expiresAt(reservation.getExpiresAt())
                .committedAt(reservation.getCommittedAt())
                .releasedAt(reservation.getReleasedAt())
                .build();
    }

    private SupplierResponse toSupplierResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .active(supplier.getActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    private StockImportResponse toStockImportResponse(StockImport stockImport) {
        Supplier supplier = stockImport.getSupplier();
        User actor = stockImport.getCreatedBy();
        List<StockImportItemResponse> items = stockImport.getItems() == null
                ? List.of()
                : stockImport.getItems().stream()
                .map(this::toStockImportItemResponse)
                .toList();

        return StockImportResponse.builder()
                .id(stockImport.getId())
                .importCode(stockImport.getImportCode())
                .supplierId(supplier == null ? null : supplier.getId())
                .supplierName(supplier == null ? null : supplier.getName())
                .totalAmount(stockImport.getTotalAmount())
                .status(stockImport.getStatus())
                .note(stockImport.getNote())
                .createdById(actor == null ? null : actor.getId())
                .createdByName(actor == null ? null : actor.getUsername())
                .createdAt(stockImport.getCreatedAt())
                .postedAt(stockImport.getPostedAt())
                .cancelledAt(stockImport.getCancelledAt())
                .items(items)
                .build();
    }

    private StockImportItemResponse toStockImportItemResponse(
            StockImportItem item
    ) {
        InventoryItem inventoryItem = item.getInventoryItem();

        return StockImportItemResponse.builder()
                .id(item.getId())
                .inventoryItemId(inventoryItem.getId())
                .itemCode(inventoryItem.getItemCode())
                .itemName(inventoryItem.getName())
                .quantity(item.getQuantity())
                .unitCost(item.getUnitCost())
                .totalCost(item.getTotalCost())
                .build();
    }
}
