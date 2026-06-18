package vn.phankyanh.shopbongda.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.dto.response.BookingExtraServiceResponse;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;
import vn.phankyanh.shopbongda.extra.domain.BookingExtraService;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "field.id", target = "fieldId")
    @Mapping(source = "field.name", target = "fieldName")
    @Mapping(source = "field.imageUrl", target = "fieldImage")
    @Mapping(source = "field.address", target = "fieldAddress")
    @Mapping(source = "payment.id", target = "paymentId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalPrice", target = "finalPrice")
    BookingResponse toResponse(Booking booking);

    @Mapping(source = "extraService.id", target = "serviceId")
    @Mapping(source = "extraService.name", target = "serviceName")
    BookingExtraServiceResponse toExtraServiceResponse(
            BookingExtraService extraService
    );
}
