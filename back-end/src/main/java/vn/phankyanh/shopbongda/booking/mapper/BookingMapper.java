package vn.phankyanh.shopbongda.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.phankyanh.shopbongda.booking.domain.Booking;
import vn.phankyanh.shopbongda.booking.dto.response.BookingResponse;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    
    @Mapping(target = "userId", expression="java(booking.getUser().getId())")
    @Mapping(target = "fieldId", expression="java(booking.getField().getId())")
    @Mapping(target = "fieldName", expression="java(booking.getField().getName())")
    @Mapping(target = "status", expression="java(booking.getStatus().toString())")
    BookingResponse toResponse(Booking booking);
}
