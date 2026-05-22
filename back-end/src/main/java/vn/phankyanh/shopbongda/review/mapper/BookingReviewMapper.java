package vn.phankyanh.shopbongda.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.phankyanh.shopbongda.review.domain.BookingReview;
import vn.phankyanh.shopbongda.review.dto.request.BookingReviewRequest;
import vn.phankyanh.shopbongda.review.dto.response.BookingReviewResponse;

@Mapper(componentModel = "spring")
public interface BookingReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    BookingReview toReview(BookingReviewRequest request);
    
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "booking.field.name", target = "fieldName")
    BookingReviewResponse toResponse(BookingReview bookingReview);
}
