package vn.phankyanh.shopbongda.inventory.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItem;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.repository.InventoryItemRepository;
import vn.phankyanh.shopbongda.inventory.service.InventoryService;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;

@Service
@RequiredArgsConstructor
public class ProductVariantInventorySyncWorker {

    private final ProductVariantRepository variantRepository;
    private final InventoryItemRepository itemRepository;
    private final InventoryService inventoryService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncOutcome syncOne(Long variantId) {
        ProductVariant variant = variantRepository.findByIdForUpdate(variantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy ProductVariant"
                ));

        if (variant.getInventoryItemId() != null) {
            return SyncOutcome.skipped(variant);
        }

        String itemCode = "PV-" + variant.getId();
        InventoryItem existingItem = itemRepository.findByItemCode(itemCode)
                .orElse(null);

        if (existingItem != null) {
            boolean matchesVariant =
                    existingItem.getItemType() == InventoryItemType.PRODUCT_VARIANT
                            && variant.getId().equals(existingItem.getReferenceId());
            if (!matchesVariant) {
                throw new IllegalStateException(
                        "Mã kho " + itemCode + " đang thuộc đối tượng khác"
                );
            }

            variant.setInventoryItemId(existingItem.getId());
            variantRepository.save(variant);
            return SyncOutcome.linkedExisting(variant, existingItem.getId());
        }

        Long inventoryItemId = inventoryService.createLinkedItem(
                itemCode,
                variant.getProduct().getName() + " - " + variant.getSize().getCode(),
                InventoryItemType.PRODUCT_VARIANT,
                variant.getId(),
                "sản phẩm"
        );

        variant.setInventoryItemId(inventoryItemId);
        variant.setStock(0);
        variantRepository.save(variant);

        return SyncOutcome.created(variant, inventoryItemId);
    }

    public record SyncOutcome(
            SyncStatus status,
            Long variantId,
            Long inventoryItemId,
            String productName,
            String sizeCode
    ) {
        static SyncOutcome created(ProductVariant variant, Long itemId) {
            return from(SyncStatus.CREATED, variant, itemId);
        }

        static SyncOutcome linkedExisting(ProductVariant variant, Long itemId) {
            return from(SyncStatus.LINKED_EXISTING, variant, itemId);
        }

        static SyncOutcome skipped(ProductVariant variant) {
            return from(SyncStatus.SKIPPED, variant, variant.getInventoryItemId());
        }

        private static SyncOutcome from(
                SyncStatus status,
                ProductVariant variant,
                Long itemId
        ) {
            return new SyncOutcome(
                    status,
                    variant.getId(),
                    itemId,
                    variant.getProduct().getName(),
                    variant.getSize().getCode()
            );
        }
    }

    public enum SyncStatus {
        CREATED,
        LINKED_EXISTING,
        SKIPPED
    }
}
