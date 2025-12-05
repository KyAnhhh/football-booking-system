package vn.phankyanh.shopbongda.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.CartRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.CartResponse;
import vn.phankyanh.shopbongda.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ApiResponse<CartResponse> createCart(@RequestBody CartRequest request, @RequestHeader("Authorization") String authHeader) throws ParseException {
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.cartService.handleCreateCart(request, authHeader));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<CartResponse>> getAllCart(){
        ApiResponse<List<CartResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.cartService.handleGetAllCart());
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<CartResponse> getCartById(@PathVariable("id") Long id){
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.cartService.handleGetCartById(id));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<CartResponse> updateCart(@PathVariable("id") Long id, @RequestBody CartRequest request){
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.cartService.handleUpdateCart(id, request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCart(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.cartService.handleDeleteCart(id);
        apiResponse.setMessage("Deleted Cart with id: " + id);
        return apiResponse;
    }
}
