package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.ShippingAddress;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.request.ShippingAddressRequest;
import vn.phankyanh.shopbongda.dto.response.ShippingAddressResponse;
import vn.phankyanh.shopbongda.mapper.ShippingAddressMapper;
import vn.phankyanh.shopbongda.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

import java.text.ParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {
    private final ShippingAddressRepository shippingAddressRepository;
    private final ShippingAddressMapper shippingAddressMapper;
    private final UserRepository userRepository;

    public ShippingAddressResponse handleCreateShippingAddress(ShippingAddressRequest request, String authHeader) throws ParseException {
        String token = authHeader.replace("Bearer ", "");
        JwtInfo info = JwtService.parseToken(token);

        User user = userRepository.findById(info.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id " + info.getId()));
        // ánh xạ với User
        ShippingAddress shippingAddress = shippingAddressMapper.toShippingAddress(request);
        shippingAddress.setUser(user);

        this.shippingAddressRepository.save(shippingAddress);
        return shippingAddressMapper.toResponse(shippingAddress);
    }
    public List<ShippingAddressResponse> handleGetAllShippingAddress(){
        return this.shippingAddressRepository.findAll()
                .stream()
                .map(shippingAddressMapper::toResponse)
                .toList();
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
