package vn.phankyanh.shopbongda.wishlist.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.catalog.domain.PublicationStatus;
import vn.phankyanh.shopbongda.product.domain.Product;
import vn.phankyanh.shopbongda.product.domain.ProductImage;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.wishlist.domain.WishlistItem;
import vn.phankyanh.shopbongda.wishlist.dto.WishlistItemResponse;
import vn.phankyanh.shopbongda.wishlist.repository.WishlistRepository;
import vn.phankyanh.shopbongda.wishlist.service.WishlistService;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getByUser(Long userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WishlistItemResponse add(Long userId, Long productId) {
        WishlistItem existing = wishlistRepository
                .findByUserIdAndProductId(userId, productId)
                .orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getPublicationStatus() != PublicationStatus.ACTIVE) {
            throw new IllegalStateException("Only active products can be added to wishlist");
        }

        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        return toResponse(wishlistRepository.save(item));
    }

    @Override
    @Transactional
    public void remove(Long userId, Long productId) {
        wishlistRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(wishlistRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean contains(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    private WishlistItemResponse toResponse(WishlistItem item) {
        Product product = item.getProduct();
        String imageUrl = product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .or(() -> product.getImages().stream().findFirst())
                .map(ProductImage::getImageUrl)
                .orElse(null);
        BigDecimal minimumPrice = product.getVariants().stream()
                .map(ProductVariant::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return WishlistItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(imageUrl)
                .minimumPrice(minimumPrice)
                .discount(product.getDiscount())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
