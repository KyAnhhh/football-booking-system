package vn.phankyanh.shopbongda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.OrderStatus;
import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.domain.Review;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.ReviewRequest;
import vn.phankyanh.shopbongda.dto.response.RatingResponse;
import vn.phankyanh.shopbongda.dto.response.ReviewResponse;
import vn.phankyanh.shopbongda.mapper.ReviewMapper;
import vn.phankyanh.shopbongda.repository.OrderRepository;
import vn.phankyanh.shopbongda.repository.ProductRepository;
import vn.phankyanh.shopbongda.repository.ReviewRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

     public List<ReviewResponse> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdWithUser(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
        }   

    public RatingResponse getAverageRating(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        Long count = reviewRepository.countByProductId(productId);

        if (avg == null) {
            avg = 0.0;
        }
        return new RatingResponse(avg, count);
    }

    public ReviewResponse createReview(ReviewRequest request, Long userId, Long productId) {
        // Kiểm tra nếu user đã review sản phẩm này chưa
        if (!canUserReviewProduct(userId, productId)) {
            throw new RuntimeException("User không có quyền review sản phẩm này");
        }

        if(request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating phải từ 1 đến 5");
        }

        // nhanh hơn và k cần load full object
        User user = userRepository.getReferenceById(userId);
        Product product = productRepository.getReferenceById(productId);

        Review review = reviewMapper.toReview(request);
        review.setUser(user);
        review.setProduct(product);
        
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    public boolean canUserReviewProduct(Long userId, Long productId) {

        // đã review chưa
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            return false;
        }

        // có order DELIVERED chứa product không
        return orderRepository.existsDeliveredOrderProduct(userId, productId);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review không tồn tại"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("User không có quyền xóa review này");
        }
        
        reviewRepository.delete(review);
    }
}
