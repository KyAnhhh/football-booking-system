package vn.phankyanh.shopbongda.user.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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
        shippingAddress.setAddress(formatAddress(request));

        return shippingAddressMapper.toResponse(
                shippingAddressRepository.save(shippingAddress)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingAddressResponse> getMyAddresses(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id " + userId);
        }

        return shippingAddressRepository
                .findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(shippingAddressMapper::toResponse)
                .toList();
    }

    @Override
    public ShippingAddressResponse update(
            Long id,
            ShippingAddressRequest request,
            Long userId
    ) {

        ShippingAddress shippingAddress =
                shippingAddressRepository.findByIdAndUserId(id, userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipping address not found"
                                )
                        );

        shippingAddressMapper.updateShippingAddress(
                shippingAddress,
                request
        );
        shippingAddress.setAddress(formatAddress(request));

        return shippingAddressMapper.toResponse(
                shippingAddressRepository.save(shippingAddress)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingAddressResponse getShippingAddressById(
            Long id,
            Long userId
    ) {

        ShippingAddress shippingAddress =
                shippingAddressRepository.findByIdAndUserId(id, userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipping address not found"
                                )
                        );

        return shippingAddressMapper.toResponse(shippingAddress);
    }

    @Override
    public void delete(Long id, Long userId) {
        ShippingAddress shippingAddress =
                shippingAddressRepository.findByIdAndUserId(id, userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Shipping address not found"
                                )
                        );

        shippingAddressRepository.delete(shippingAddress);
    }

    private String formatAddress(ShippingAddressRequest request) {
        return String.join(
                ", ",
                request.getAddressLine().trim(),
                request.getWard().trim(),
                request.getDistrict().trim(),
                request.getCity().trim()
        );
    }
}
