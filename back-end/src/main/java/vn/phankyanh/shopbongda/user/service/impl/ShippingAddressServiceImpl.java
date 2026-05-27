package vn.phankyanh.shopbongda.user.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.user.domain.ShippingAddress;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.user.dto.response.ShippingAddressResponse;
import vn.phankyanh.shopbongda.user.mapper.ShippingAddressMapper;
import vn.phankyanh.shopbongda.user.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.user.service.ShippingAddressService;

@Service
@RequiredArgsConstructor
public class ShippingAddressServiceImpl implements ShippingAddressService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final ShippingAddressMapper shippingAddressMapper;
    private final UserRepository userRepository;

    @Override
    public ShippingAddressResponse createShippingAddress(
            ShippingAddressRequest request,
            Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id " + userId
                        )
                );

        ShippingAddress shippingAddress =
                shippingAddressMapper.toShippingAddress(request);

        shippingAddress.setUser(user);

        shippingAddressRepository.save(shippingAddress);

        return shippingAddressMapper.toResponse(shippingAddress);
    }

    @Override
    public ShippingAddressResponse getMyAddress(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id " + userId
                        )
                );

        if (user.getShippingAddresses() == null
                || user.getShippingAddresses().isEmpty()) {

            return null;
        }

        return shippingAddressMapper.toResponse(
                user.getShippingAddresses().get(0)
        );
    }

    @Override
    public ShippingAddressResponse update(
            Long id,
            ShippingAddressRequest request
    ) {

        ShippingAddress shippingAddress =
                shippingAddressRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Not found with id " + id
                                )
                        );

        shippingAddressMapper.updateShippingAddress(
                shippingAddress,
                request
        );

        shippingAddressRepository.save(shippingAddress);

        return shippingAddressMapper.toResponse(shippingAddress);
    }

    @Override
    public ShippingAddressResponse getShippingAddressById(Long id) {

        ShippingAddress shippingAddress =
                shippingAddressRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Not found with id " + id
                                )
                        );

        return shippingAddressMapper.toResponse(shippingAddress);
    }

    @Override
    public void delete(Long id) {

        if (!shippingAddressRepository.existsById(id)) {
            throw new RuntimeException("Not found with id " + id);
        }

        shippingAddressRepository.deleteById(id);
    }
}