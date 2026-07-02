package vn.phankyanh.shopbongda.extra.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryStockResponse;
import vn.phankyanh.shopbongda.inventory.service.InventoryService;
import vn.phankyanh.shopbongda.extra.domain.ExtraService;
import vn.phankyanh.shopbongda.extra.dto.request.ExtraServiceRequest;
import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;
import vn.phankyanh.shopbongda.extra.mapper.ExtraServiceMapper;
import vn.phankyanh.shopbongda.extra.repository.ExtraServiceRepository;
import vn.phankyanh.shopbongda.extra.service.ExtraServiceService;
import vn.phankyanh.shopbongda.catalog.domain.PublicationStatus;
import vn.phankyanh.shopbongda.catalog.service.CatalogPublicationService;

@Service
@RequiredArgsConstructor
public class ExtraServiceServiceImpl
        implements ExtraServiceService {

    private final ExtraServiceRepository extraServiceRepository;

    private final ExtraServiceMapper extraServiceMapper;

    private final InventoryService inventoryService;
    private final CatalogPublicationService publicationService;

    @Override
    public ExtraServiceResponse create(
            ExtraServiceRequest request
    ) {

        if (request.getName() == null
                || request.getName().isBlank()) {

            throw new RuntimeException(
                    "Service name is required");
        }

        if (request.getPrice() == null
                || request.getPrice()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Price must be greater than 0");
        }

        validateStockRequest(request);

        ExtraService service =
                extraServiceMapper.toEntity(request);

        if (service.getIsActive() == null) {
            service.setIsActive(true);
        }

        normalizeStockFields(service);

        ExtraService saved =
                extraServiceRepository.save(service);

        ensureInventoryForExtraService(saved);
        publicationService.refreshExtraServiceReadiness(saved.getId());

        return extraServiceMapper.toResponse(
                extraServiceRepository.findById(saved.getId()).orElseThrow()
        );
    }

    @Override
    public List<ExtraServiceResponse> getAll() {

        return extraServiceRepository.findAll()
                .stream()
                .map(extraServiceMapper::toResponse)
                .toList();
    }

    @Override
    public List<ExtraServiceResponse> getPublicServices() {
        return extraServiceRepository
                .findByIsActiveTrueAndPublicationStatus(
                        PublicationStatus.ACTIVE
                )
                .stream()
                .filter(this::isPubliclyAvailable)
                .map(extraServiceMapper::toResponse)
                .toList();
    }

    @Override
    public ExtraServiceResponse getById(
            Long id
    ) {

        ExtraService service =
                extraServiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extra service not found"));

        return extraServiceMapper.toResponse(service);
    }

    @Override
    public ExtraServiceResponse getPublicById(Long id) {
        ExtraService service = extraServiceRepository
                .findByIdAndPublicationStatus(id, PublicationStatus.ACTIVE)
                .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                .filter(this::isPubliclyAvailable)
                .orElseThrow(() -> new RuntimeException(
                        "Extra service is not available"
                ));
        return extraServiceMapper.toResponse(service);
    }

    private boolean isPubliclyAvailable(ExtraService service) {
        if (!Boolean.TRUE.equals(service.getTrackStock())) {
            return true;
        }
        if (service.getInventoryItemId() == null) {
            return false;
        }
        return inventoryService.getStockByItemId(
                service.getInventoryItemId()
        ).getAvailableQuantity() > 0;
    }

    @Override
    public ExtraServiceResponse update(
            Long id,
            ExtraServiceRequest request
    ) {

        ExtraService service =
                extraServiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extra service not found"));

        if (request.getPrice() != null
                && request.getPrice()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Price must be greater than 0");
        }

        validateStockRequest(request);

        extraServiceMapper.updateEntity(
                request,
                service
        );

        normalizeStockFields(service);

        ExtraService updated =
                extraServiceRepository.save(service);

        ensureInventoryForExtraService(updated);

        return extraServiceMapper.toResponse(updated);
    }

    @Override
    public void delete(
            Long id
    ) {

        ExtraService service =
                extraServiceRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Extra service not found"));

        extraServiceRepository.delete(service);
    }

    private void validateStockRequest(
            ExtraServiceRequest request
    ) {

        boolean trackStock =
                Boolean.TRUE.equals(request.getTrackStock());

        if (!trackStock) {
            return;
        }

        if (request.getStockQuantity() == null
                || request.getStockQuantity() < 0) {

            throw new RuntimeException(
                    "Stock quantity must be greater than or equal to 0");
        }

        if (request.getRemainingQuantity() != null
                && request.getRemainingQuantity() < 0) {

            throw new RuntimeException(
                    "Remaining quantity must be greater than or equal to 0");
        }

        if (request.getRemainingQuantity() != null
                && request.getRemainingQuantity()
                > request.getStockQuantity()) {

            throw new RuntimeException(
                    "Remaining quantity cannot exceed stock quantity");
        }
    }

    private void normalizeStockFields(
            ExtraService service
    ) {

        if (service.getTrackStock() == null) {
            service.setTrackStock(false);
        }

        if (!Boolean.TRUE.equals(service.getTrackStock())) {
            if (service.getStockQuantity() == null) {
                service.setStockQuantity(0);
            }
            if (service.getRemainingQuantity() == null) {
                service.setRemainingQuantity(0);
            }
            return;
        }

        if (service.getStockQuantity() == null) {
            service.setStockQuantity(0);
        }

        if (service.getRemainingQuantity() == null) {
            service.setRemainingQuantity(
                    service.getStockQuantity()
            );
        }
    }

    private void ensureInventoryForExtraService(
            ExtraService service
    ) {
        if (!Boolean.TRUE.equals(service.getTrackStock())) {
            return;
        }

        if (service.getInventoryItemId() == null) {
            Long inventoryItemId =
                    inventoryService.createLinkedItem(
                            "EX-" + service.getId(),
                            service.getName(),
                            InventoryItemType.EXTRA_SERVICE,
                            service.getId(),
                            service.getUnit()
                    );

            service.setInventoryItemId(inventoryItemId);
            extraServiceRepository.save(service);
        }

        InventoryStockResponse stock =
                inventoryService.getStockByItemId(
                        service.getInventoryItemId()
                );
        service.setStockQuantity(stock.getQuantityOnHand());
        service.setRemainingQuantity(stock.getAvailableQuantity());
        extraServiceRepository.save(service);
    }
}
