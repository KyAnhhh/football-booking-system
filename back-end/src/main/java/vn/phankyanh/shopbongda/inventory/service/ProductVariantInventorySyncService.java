package vn.phankyanh.shopbongda.inventory.service;

import vn.phankyanh.shopbongda.inventory.dto.response.ProductVariantSyncPreviewResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.ProductVariantSyncResultResponse;

public interface ProductVariantInventorySyncService {

    ProductVariantSyncPreviewResponse preview();

    ProductVariantSyncResultResponse sync();
}
