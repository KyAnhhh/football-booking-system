package vn.phankyanh.shopbongda.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.phankyanh.shopbongda.user.domain.ShippingAddress;
import vn.phankyanh.shopbongda.user.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.user.dto.response.ShippingAddressResponse;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(source = "receiverName", target = "fullName")
    ShippingAddress toShippingAddress(ShippingAddressRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "fullName", target = "receiverName")
    @Mapping(source = "address", target = "formattedAddress")
    ShippingAddressResponse toResponse(ShippingAddress shippingAddress);

    @Mapping(target = "user", ignore = true) // tránh ghi đè user
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(source = "receiverName", target = "fullName")
    void updateShippingAddress(@MappingTarget ShippingAddress shippingAddress, ShippingAddressRequest request);
}
