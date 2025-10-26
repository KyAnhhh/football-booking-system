package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.response.UserCreateResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserCreateResponse toUserCreateResponse(User user);
}
