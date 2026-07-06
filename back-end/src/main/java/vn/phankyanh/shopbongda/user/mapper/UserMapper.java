package vn.phankyanh.shopbongda.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "membershipTierName", ignore = true)
    @Mapping(target = "membershipDiscountPercent", ignore = true)
    @Mapping(target = "nextMembershipTierName", ignore = true)
    @Mapping(target = "nextMembershipRequiredPoints", ignore = true)
    @Mapping(target = "pointsToNextTier", ignore = true)
    @Mapping(target = "membershipProgressPercent", ignore = true)
    UserCreateResponse toUserCreateResponse(User user);
}
