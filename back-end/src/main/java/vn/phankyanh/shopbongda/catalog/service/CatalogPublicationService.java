package vn.phankyanh.shopbongda.catalog.service;

import vn.phankyanh.shopbongda.catalog.dto.PublicationCheckResponse;
import vn.phankyanh.shopbongda.extra.dto.response.ExtraServiceResponse;
import vn.phankyanh.shopbongda.product.dto.response.ProductResponse;

public interface CatalogPublicationService {
    PublicationCheckResponse checkProduct(Long id);
    ProductResponse publishProduct(Long id);
    ProductResponse unpublishProduct(Long id);

    PublicationCheckResponse checkExtraService(Long id);
    ExtraServiceResponse publishExtraService(Long id);
    ExtraServiceResponse unpublishExtraService(Long id);

    void refreshReadinessForInventoryItem(Long inventoryItemId);
    void refreshExtraServiceReadiness(Long extraServiceId);
}
