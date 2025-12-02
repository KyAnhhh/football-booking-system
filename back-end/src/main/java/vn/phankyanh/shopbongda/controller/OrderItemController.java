package vn.phankyanh.shopbongda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.OrderItemResponse;
import vn.phankyanh.shopbongda.service.OrderItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/orderItems")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @PostMapping
    public ApiResponse<OrderItemResponse> createOrder(@RequestBody OrderItemRequest request) {
        ApiResponse<OrderItemResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderItemService.handleCreateOrderItem(request));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<OrderItemResponse>> getAllOrder(){
        ApiResponse<List<OrderItemResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderItemService.handleGetAllOrderItem());
        return apiResponse;
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderItemResponse> getOrderById(@PathVariable("id") Long id){
        ApiResponse<OrderItemResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderItemService.handleGetOrderItemById(id));
        return apiResponse;
    }

    @PutMapping("/{id}")
    public ApiResponse<OrderItemResponse> updateOrder(@PathVariable("id") Long id, @RequestBody OrderItemRequest request){
        ApiResponse<OrderItemResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderItemService.handleUpdateOrderItem(id, request));
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrder(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.orderItemService.handleDeleteOrderItem(id);
        apiResponse.setMessage("Deleted order with id: " + id);
        return apiResponse;
    }
}
