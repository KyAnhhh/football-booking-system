package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.domain.ShippingAddress;
import vn.phankyanh.shopbongda.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.dto.response.ShippingAddressResponse;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShippingAddress toShippingAddress(ShippingAddressRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "address", target = "address")
    ShippingAddressResponse toResponse(ShippingAddress shippingAddress);

    @Mapping(target = "user", ignore = true) // tránh ghi đè user
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateShippingAddress(@MappingTarget ShippingAddress shippingAddress, ShippingAddressRequest request);
}
