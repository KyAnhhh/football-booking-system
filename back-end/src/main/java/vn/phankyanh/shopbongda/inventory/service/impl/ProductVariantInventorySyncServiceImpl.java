package vn.phankyanh.shopbongda.inventory.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItem;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.dto.response.ProductVariantSyncIssueResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.ProductVariantSyncPreviewResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.ProductVariantSyncResultResponse;
import vn.phankyanh.shopbongda.inventory.repository.InventoryItemRepository;
import vn.phankyanh.shopbongda.inventory.service.ProductVariantInventorySyncService;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;

@Service
@RequiredArgsConstructor
public class ProductVariantInventorySyncServiceImpl
        implements ProductVariantInventorySyncService {

    private final ProductVariantRepository variantRepository;
    private final InventoryItemRepository itemRepository;
    private final ProductVariantInventorySyncWorker worker;

    @Override
    @Transactional(readOnly = true)
    public ProductVariantSyncPreviewResponse preview() {
        List<ProductVariant> unlinked =
                variantRepository.findAllByInventoryItemIdIsNullOrderByIdAsc();

        long conflicts = unlinked.stream()
                .filter(this::hasItemCodeConflict)
                .count();
        long total = variantRepository.count();

        return ProductVariantSyncPreviewResponse.builder()
                .totalVariants(total)
                .linkedVariants(total - unlinked.size())
                .unlinkedVariants(unlinked.size())
                .conflictVariants(conflicts)
                .build();
    }

    @Override
    public ProductVariantSyncResultResponse sync() {
        List<Long> variantIds = variantRepository
                .findAllByInventoryItemIdIsNullOrderByIdAsc()
                .stream()
                .map(ProductVariant::getId)
                .toList();

        long success = 0;
        long skipped = 0;
        List<Long> createdItemIds = new ArrayList<>();
        List<ProductVariantSyncIssueResponse> errors = new ArrayList<>();

        for (Long variantId : variantIds) {
            try {
                ProductVariantInventorySyncWorker.SyncOutcome outcome =
                        worker.syncOne(variantId);
                if (outcome.status()
                        == ProductVariantInventorySyncWorker.SyncStatus.SKIPPED) {
                    skipped++;
                } else {
                    success++;
                    createdItemIds.add(outcome.inventoryItemId());
                }
            } catch (RuntimeException ex) {
                errors.add(ProductVariantSyncIssueResponse.builder()
                        .variantId(variantId)
                        .itemCode("PV-" + variantId)
                        .message(ex.getMessage())
                        .build());
            }
        }

        return ProductVariantSyncResultResponse.builder()
                .totalRows(variantIds.size())
                .successRows(success)
                .skippedRows(skipped)
                .failedRows(errors.size())
                .createdInventoryItemIds(createdItemIds)
                .errors(errors)
                .build();
    }

    private boolean hasItemCodeConflict(ProductVariant variant) {
        InventoryItem item = itemRepository.findByItemCode(
                "PV-" + variant.getId()
        ).orElse(null);

        return item != null
                && (item.getItemType() != InventoryItemType.PRODUCT_VARIANT
                || !variant.getId().equals(item.getReferenceId()));
    }
}
