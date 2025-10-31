package vn.phankyanh.shopbongda.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.ApiResponse;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    public ApiResponse<List<Order>> getAllOrder(){
        ApiResponse<List<Order>> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleGetAllOrder());
        return apiResponse;
    }

    public ApiResponse<OrderResponse> getOrderById(@PathVariable("id") Long id){
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleGetOrderById(id));
        return apiResponse;
    }

    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        ApiResponse<OrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleCreateOrder(request));
        return apiResponse;
    }

    public ApiResponse<Order> updateOrder(@PathVariable("id") Long id, @RequestBody OrderRequest request){
        ApiResponse<Order> apiResponse = new ApiResponse<>();
        apiResponse.setData(this.orderService.handleUpdateOrder(id, request));
        return apiResponse;
    }

    public ApiResponse<String> deleteOrder(@PathVariable("id") Long id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.orderService.handleDeleteOrder(id);
        apiResponse.setMessage("Deleted order with id: " + id);
        return apiResponse;
    }
}
