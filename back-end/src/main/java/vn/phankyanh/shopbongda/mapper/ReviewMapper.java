package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.phankyanh.shopbongda.domain.Review;
import vn.phankyanh.shopbongda.dto.request.ReviewRequest;
import vn.phankyanh.shopbongda.dto.response.ReviewResponse;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target= "orderItem", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toReview(ReviewRequest request);
    
    @Mapping(source = "user.username", target = "userName")
    ReviewResponse toResponse(Review review);
}
