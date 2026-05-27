package vn.phankyanh.shopbongda.review.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.review.domain.Review;
import vn.phankyanh.shopbongda.review.dto.request.ReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.RatingResponse;
import vn.phankyanh.shopbongda.review.dto.response.ReviewResponse;
import vn.phankyanh.shopbongda.review.mapper.ReviewMapper;
import vn.phankyanh.shopbongda.review.repository.ReviewRepository;
import vn.phankyanh.shopbongda.review.service.ReviewService;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {

        return reviewRepository.findByProductIdWithUser(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public RatingResponse getAverageRating(Long productId) {

        Double avg = reviewRepository.findAverageRatingByProductId(productId);

        Long count = reviewRepository.countByProductId(productId);

        if (avg == null) {
            avg = 0.0;
        }

        return new RatingResponse(avg, count);
    }

    @Override
    public ReviewResponse createReview(
            Long orderItemId,
            ReviewRequest request,
            Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        if (!orderItem.getOrder().getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền đánh giá đơn này");
        }

        if (!orderItem.getOrder().getOrderStatus().equals(OrderStatus.DELIVERED)
                && !orderItem.getOrder().getOrderStatus().equals(OrderStatus.COMPLETED)) {

            throw new RuntimeException("Chỉ được đánh giá sau khi nhận hàng");
        }

        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            throw new RuntimeException("Đơn hàng này đã được đánh giá");
        }

        Review review = reviewMapper.toReview(request);

        review.setOrderItem(orderItem);

        review.setProduct(orderItem.getProductVariant().getProduct());

        review.setUser(user);

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public boolean canUserReview(Long userId, Long orderItemId) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        boolean isOwner =
                orderItem.getOrder().getUser().getId().equals(userId);

        boolean reviewed =
                reviewRepository.existsByOrderItemId(orderItemId);

        boolean delivered =
                orderItem.getOrder().getOrderStatus().equals(OrderStatus.DELIVERED)
                || orderItem.getOrder().getOrderStatus().equals(OrderStatus.COMPLETED);

        return isOwner && !reviewed && delivered;
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());

        boolean isOwner = review.getUser().getId().equals(userId);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Không có quyền xóa review");
        }

        reviewRepository.delete(review);
    }
}