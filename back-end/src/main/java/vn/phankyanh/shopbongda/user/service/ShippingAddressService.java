package vn.phankyanh.shopbongda.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.user.domain.ShippingAddress;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.user.dto.response.ShippingAddressResponse;
import vn.phankyanh.shopbongda.user.mapper.ShippingAddressMapper;
import vn.phankyanh.shopbongda.user.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {
    private final ShippingAddressRepository shippingAddressRepository;
    private final ShippingAddressMapper shippingAddressMapper;
    private final UserRepository userRepository;

    public ShippingAddressResponse handleCreateShippingAddress(ShippingAddressRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        // ánh xạ với User
        ShippingAddress shippingAddress = shippingAddressMapper.toShippingAddress(request);
        shippingAddress.setUser(user);

        this.shippingAddressRepository.save(shippingAddress);
        return shippingAddressMapper.toResponse(shippingAddress);
    }

    public ShippingAddressResponse handleGetMyAddress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        if(user.getShippingAddresses() == null || user.getShippingAddresses().isEmpty()) {
            return null; // Trả về null nếu user chưa có địa chỉ giao hàng nào
        }
        return shippingAddressMapper.toResponse(user.getShippingAddresses().get(0));
    }

    public ShippingAddressResponse handleUpdate(Long id, ShippingAddressRequest request){
        ShippingAddress shippingAddress = this.shippingAddressRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Not found with id "+ id));
        shippingAddressMapper.updateShippingAddress(shippingAddress, request);
        this.shippingAddressRepository.save(shippingAddress);
        return shippingAddressMapper.toResponse(shippingAddress);
    }


    public ShippingAddressResponse handleGetShippingAddressById(Long id){
        ShippingAddress shippingAddress = this.shippingAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id "+ id));
        return shippingAddressMapper.toResponse(shippingAddress);
    }

    public void handleDelete(Long id)
    {
        if(!shippingAddressRepository.existsById(id)){
            throw new RuntimeException("Not found with id "+ id);
        }
        this.shippingAddressRepository.deleteById(id);
    }


}
