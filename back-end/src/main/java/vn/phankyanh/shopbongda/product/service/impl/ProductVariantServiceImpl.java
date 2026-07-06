package vn.phankyanh.shopbongda.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.inventory.domain.InventoryItemType;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryStockResponse;
import vn.phankyanh.shopbongda.inventory.service.InventoryService;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.domain.Size;
import vn.phankyanh.shopbongda.product.dto.request.ProductVariantRequest;
import vn.phankyanh.shopbongda.product.dto.response.ProductVariantResponse;
import vn.phankyanh.shopbongda.product.mapper.ProductVariantMapper;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.product.repository.SizeRepository;
import vn.phankyanh.shopbongda.product.service.ProductVariantService;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final ProductVariantMapper variantMapper;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public ProductVariantResponse createProductVariant(ProductVariantRequest request) {

        if (productVariantRepository.existsByProductIdAndSizeId(
                request.getProductId(),
                request.getSizeId())
        ) {
            throw new RuntimeException("Product variant already exists");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        ProductVariant productVariant = variantMapper.toCreateProductVariant(request);

        productVariant.setProduct(product);
        productVariant.setSize(size);

        product.getVariants().add(productVariant);

        productRepository.save(product);

        ProductVariant saved = productVariantRepository
                .findByProductIdAndSizeId(
                        request.getProductId(),
                        request.getSizeId()
                )
                .orElseThrow(() -> new RuntimeException(
                        "Failed to retrieve saved product variant"
                ));

        Long inventoryItemId = request.getInventoryItemId();
        if (inventoryItemId == null) {
            inventoryItemId = inventoryService.createLinkedItem(
                    "PV-" + saved.getId(),
                    product.getName() + " - " + size.getCode(),
                    InventoryItemType.PRODUCT_VARIANT,
                    saved.getId(),
                    "cái"
            );
        }

        saved.setInventoryItemId(inventoryItemId);
        refreshStockFromInventory(saved);

        productVariantRepository.save(saved);

        return variantMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductVariantResponse updateProductVariant(
            Long id,
            ProductVariantRequest request
    ) {

        ProductVariant productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        ProductVariant updatedProductVariant =
                variantMapper.updateProductVariant(request, productVariant);

        updatedProductVariant.setProduct(product);
        updatedProductVariant.setSize(size);

        if (request.getInventoryItemId() != null) {
            updatedProductVariant.setInventoryItemId(request.getInventoryItemId());
        }

        if (updatedProductVariant.getInventoryItemId() == null) {
            Long inventoryItemId = inventoryService.createLinkedItem(
                    "PV-" + updatedProductVariant.getId(),
                    product.getName() + " - " + size.getCode(),
                    InventoryItemType.PRODUCT_VARIANT,
                    updatedProductVariant.getId(),
                    "cái"
            );
            updatedProductVariant.setInventoryItemId(inventoryItemId);
        }
        refreshStockFromInventory(updatedProductVariant);

        return variantMapper.toResponse(
                productVariantRepository.save(updatedProductVariant)
        );
    }

    @Override
    public List<ProductVariantResponse> getAllProductVariant() {

        return productVariantRepository.findAll()
                .stream()
                .map(variantMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductVariantResponse> getByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(variantMapper::toResponse)
                .toList();
    }

    @Override
    public ProductVariantResponse getProductVariantById(Long id) {

        ProductVariant productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        return variantMapper.toResponse(productVariant);
    }

    @Override
    public void deleteProductVariant(Long id) {

        if (!productVariantRepository.existsById(id)) {
            throw new RuntimeException("Product variant not found");
        }

        productVariantRepository.deleteById(id);
    }

    private void refreshStockFromInventory(ProductVariant variant) {
        InventoryStockResponse stock =
                inventoryService.getStockByItemId(
                        variant.getInventoryItemId()
                );
        variant.setStock(stock.getAvailableQuantity());
    }
}
