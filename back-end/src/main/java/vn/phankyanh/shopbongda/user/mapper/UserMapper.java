package vn.phankyanh.shopbongda.user.mapper;

import org.mapstruct.Mapper;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.response.UserCreateResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserCreateResponse toUserCreateResponse(User user);
}
