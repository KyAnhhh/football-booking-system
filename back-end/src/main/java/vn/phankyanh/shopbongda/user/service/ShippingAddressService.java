package vn.phankyanh.shopbongda.user.service;

import vn.phankyanh.shopbongda.user.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.user.dto.response.ShippingAddressResponse;

public interface ShippingAddressService {

    ShippingAddressResponse createShippingAddress(
            ShippingAddressRequest request,
            Long userId
    );

    ShippingAddressResponse getMyAddress(Long userId);

    ShippingAddressResponse update(
            Long id,
            ShippingAddressRequest request
    );

    ShippingAddressResponse getShippingAddressById(Long id);

    void delete(Long id);
}