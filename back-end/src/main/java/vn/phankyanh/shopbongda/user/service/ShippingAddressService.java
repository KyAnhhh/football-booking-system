package vn.phankyanh.shopbongda.user.service;

import java.util.List;
import vn.phankyanh.shopbongda.user.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.user.dto.response.ShippingAddressResponse;

public interface ShippingAddressService {

    ShippingAddressResponse createShippingAddress(
            ShippingAddressRequest request,
            Long userId
    );

    List<ShippingAddressResponse> getMyAddresses(Long userId);

    ShippingAddressResponse update(
            Long id,
            ShippingAddressRequest request,
            Long userId
    );

    ShippingAddressResponse getShippingAddressById(Long id, Long userId);

    void delete(Long id, Long userId);
}
