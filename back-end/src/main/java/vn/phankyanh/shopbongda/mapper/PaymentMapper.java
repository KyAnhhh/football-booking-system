package vn.phankyanh.shopbongda.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.dto.response.PaymentResponse;

@Mapper(componentModel= "spring")
public interface PaymentMapper {
    
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "paymentDate", source = "payment.paymentDate")
    @Mapping(target = "orderStatus", source = "order.orderStatus")
    PaymentResponse toPaymentResponse(Payment payment);
}
