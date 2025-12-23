package vn.phankyanh.shopbongda.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.OrderItemResponse;
import vn.phankyanh.shopbongda.service.OrderItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/orderItems")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/order/{orderId}")
    public ApiResponse<List<OrderItemResponse>> getByOrder(@PathVariable("orderId") Long orderId, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        ApiResponse<List<OrderItemResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderItemService.handleGetOrderItemByOrderId(orderId, userId));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<OrderItemResponse> updateOrder(@PathVariable("id") Long id, @RequestBody OrderItemRequest request, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        ApiResponse<OrderItemResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderItemService.handleUpdateQuantity(id, request.getQuantity(), userId));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrder(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.orderItemService.handleDeleteOrderItem(id, userId);
        apiResponse.setMessage("Deleted order with id: " + id);
        return apiResponse;
    }
}
