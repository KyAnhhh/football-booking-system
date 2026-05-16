package vn.phankyanh.shopbongda.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "field.id", target = "fieldId")
    @Mapping(source = "field.name", target = "fieldName")
    @Mapping(source = "payment.id", target = "paymentId")
    @Mapping(source = "status", target = "status")
    BookingResponse toResponse(Booking booking);
}
