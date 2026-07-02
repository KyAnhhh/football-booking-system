package vn.phankyanh.shopbongda.catalog.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.catalog.domain.PublicationStatus;
import vn.phankyanh.shopbongda.catalog.dto.PublicationCheckResponse;
import vn.phankyanh.shopbongda.catalog.service.CatalogPublicationService;
import vn.phankyanh.shopbongda.extra.domain.ExtraService;
import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;
import vn.phankyanh.shopbongda.extra.mapper.ExtraServiceMapper;
import vn.phankyanh.shopbongda.extra.repository.ExtraServiceRepository;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItem;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.repository.InventoryItemRepository;
import vn.phankyanh.shopbongda.inventory.repository.InventoryStockRepository;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.dto.response.ProductResponse;
import vn.phankyanh.shopbongda.product.mapper.ProductMapper;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;

@Service
@RequiredArgsConstructor
public class CatalogPublicationServiceImpl
        implements CatalogPublicationService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ExtraServiceRepository extraServiceRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final ProductMapper productMapper;
    private final ExtraServiceMapper extraServiceMapper;

    @Override
    @Transactional(readOnly = true)
    public PublicationCheckResponse checkProduct(Long id) {
        return productCheck(getProduct(id));
    }

    @Override
    @Transactional
    public ProductResponse publishProduct(Long id) {
        Product product = getProduct(id);
        PublicationCheckResponse check = productCheck(product);
        requirePublishable(check);
        product.setPublicationStatus(PublicationStatus.ACTIVE);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse unpublishProduct(Long id) {
        Product product = getProduct(id);
        product.setPublicationStatus(PublicationStatus.INACTIVE);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public PublicationCheckResponse checkExtraService(Long id) {
        return extraCheck(getExtraService(id));
    }

    @Override
    @Transactional
    public ExtraServiceResponse publishExtraService(Long id) {
        ExtraService extraService = getExtraService(id);
        PublicationCheckResponse check = extraCheck(extraService);
        requirePublishable(check);
        extraService.setPublicationStatus(PublicationStatus.ACTIVE);
        return extraServiceMapper.toResponse(
                extraServiceRepository.save(extraService)
        );
    }

    @Override
    @Transactional
    public ExtraServiceResponse unpublishExtraService(Long id) {
        ExtraService extraService = getExtraService(id);
        extraService.setPublicationStatus(PublicationStatus.INACTIVE);
        return extraServiceMapper.toResponse(
                extraServiceRepository.save(extraService)
        );
    }

    @Override
    @Transactional
    public void refreshReadinessForInventoryItem(Long inventoryItemId) {
        InventoryItem item = inventoryItemRepository.findById(inventoryItemId)
                .orElse(null);
        if (item == null || item.getReferenceId() == null) {
            return;
        }

        if (item.getItemType() == InventoryItemType.PRODUCT_VARIANT) {
            productVariantRepository.findById(item.getReferenceId())
                    .map(ProductVariant::getProduct)
                    .ifPresent(this::markProductReadyIfEligible);
        } else if (item.getItemType() == InventoryItemType.EXTRA_SERVICE) {
            extraServiceRepository.findById(item.getReferenceId())
                    .ifPresent(this::markExtraReadyIfEligible);
        }
    }

    @Override
    @Transactional
    public void refreshExtraServiceReadiness(Long extraServiceId) {
        extraServiceRepository.findById(extraServiceId)
                .ifPresent(this::markExtraReadyIfEligible);
    }

    private PublicationCheckResponse productCheck(Product product) {
        List<String> reasons = new ArrayList<>();
        List<ProductVariant> variants = product.getVariants() == null
                ? List.of()
                : product.getVariants();
        if (variants.isEmpty()) {
            reasons.add("Sản phẩm chưa có biến thể");
        }
        boolean validPrice = variants.stream().anyMatch(variant ->
                variant.getPrice() != null
                        && variant.getPrice().compareTo(BigDecimal.ZERO) > 0
        );
        if (!validPrice) {
            reasons.add("Chưa có biến thể với giá bán hợp lệ");
        }
        boolean available = variants.stream().anyMatch(variant ->
                variant.getInventoryItemId() != null
                        && inventoryStockRepository
                        .findByInventoryItemId(variant.getInventoryItemId())
                        .map(stock -> stock.getAvailableQuantity() > 0)
                        .orElse(false)
        );
        if (!available) {
            reasons.add("Chưa có biến thể còn tồn khả dụng");
        }
        return PublicationCheckResponse.builder()
                .id(product.getId())
                .catalogType("PRODUCT")
                .currentStatus(product.getPublicationStatus())
                .publishable(reasons.isEmpty())
                .reasons(reasons)
                .build();
    }

    private PublicationCheckResponse extraCheck(ExtraService extraService) {
        List<String> reasons = new ArrayList<>();
        if (!Boolean.TRUE.equals(extraService.getIsActive())) {
            reasons.add("Dịch vụ đang bị tắt");
        }
        if (extraService.getPrice() == null
                || extraService.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            reasons.add("Giá bán không hợp lệ");
        }
        if (Boolean.TRUE.equals(extraService.getTrackStock())) {
            boolean available = extraService.getInventoryItemId() != null
                    && inventoryStockRepository
                    .findByInventoryItemId(extraService.getInventoryItemId())
                    .map(stock -> stock.getAvailableQuantity() > 0)
                    .orElse(false);
            if (!available) {
                reasons.add("Dịch vụ chưa có tồn khả dụng");
            }
        }
        return PublicationCheckResponse.builder()
                .id(extraService.getId())
                .catalogType("EXTRA_SERVICE")
                .currentStatus(extraService.getPublicationStatus())
                .publishable(reasons.isEmpty())
                .reasons(reasons)
                .build();
    }

    private void markProductReadyIfEligible(Product product) {
        if (product.getPublicationStatus() == PublicationStatus.DRAFT
                && productCheck(product).isPublishable()) {
            product.setPublicationStatus(PublicationStatus.READY_TO_PUBLISH);
            productRepository.save(product);
        }
    }

    private void markExtraReadyIfEligible(ExtraService extraService) {
        if (extraService.getPublicationStatus() == PublicationStatus.DRAFT
                && extraCheck(extraService).isPublishable()) {
            extraService.setPublicationStatus(
                    PublicationStatus.READY_TO_PUBLISH
            );
            extraServiceRepository.save(extraService);
        }
    }

    private void requirePublishable(PublicationCheckResponse check) {
        if (!check.isPublishable()) {
            throw new IllegalStateException(
                    "Chưa thể lên kệ: " + String.join("; ", check.getReasons())
            );
        }
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy sản phẩm"
                ));
    }

    private ExtraService getExtraService(Long id) {
        return extraServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy dịch vụ thêm"
                ));
    }
}
