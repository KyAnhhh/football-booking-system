package vn.phankyanh.shopbongda.review.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.order.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.review.domain.Review;
import vn.phankyanh.shopbongda.review.dto.request.ReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.RatingResponse;
import vn.phankyanh.shopbongda.review.dto.response.ReviewResponse;
import vn.phankyanh.shopbongda.review.mapper.ReviewMapper;
import vn.phankyanh.shopbongda.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final OrderItemRepository orderItemRepository;

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

    public ReviewResponse createReview(Long orderItemId, ReviewRequest request) {

        boolean exists = reviewRepository.existsByOrderItemId(orderItemId);

        if (exists) {
            throw new RuntimeException("Đơn hàng này đã được đánh giá");
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        Review review = reviewMapper.toReview(request);

        review.setOrderItem(orderItem);
        review.setProduct(orderItem.getProductVariant().getProduct());
        review.setUser(orderItem.getOrder().getUser());

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    public boolean canUserReview(Long userId, Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow();

        boolean isOwner = orderItem.getOrder().getUser().getId().equals(userId);

        boolean reviewed = reviewRepository.existsByOrderItemId(orderItemId);

        return isOwner && !reviewed;
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
