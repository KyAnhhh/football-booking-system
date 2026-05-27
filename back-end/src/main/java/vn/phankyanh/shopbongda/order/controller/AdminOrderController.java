package vn.phankyanh.shopbongda.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.order.service.OrderService;
import vn.phankyanh.shopbongda.shared.dto.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<Page<OrderResponse>> getAllOrders(

            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderStatus status
    ) {

        ApiResponse<Page<OrderResponse>> apiResponse =
                new ApiResponse<>();

        apiResponse.setData(
                orderService.getAllOrders(
                        pageNo,
                        pageSize,
                        sortBy,
                        sortDir,
                        search,
                        status
                )
        );

        return apiResponse;
    }

    @PutMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatus status
    ) {

        ApiResponse<OrderResponse> apiResponse =
                new ApiResponse<>();

        apiResponse.setData(
                orderService.updateOrderStatus(id, status)
        );

        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrder(
            @PathVariable Long id
    ) {

        orderService.deleteOrder(id);

        ApiResponse<String> apiResponse =
                new ApiResponse<>();

        apiResponse.setMessage(
                "Deleted order with id: " + id
        );

        return apiResponse;
    }
}