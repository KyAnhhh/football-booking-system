package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.OrderItemResponse;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.mapper.OrderItemMapper;
import vn.phankyanh.shopbongda.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.repository.OrderRepository;
import vn.phankyanh.shopbongda.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public List<OrderItemResponse> handleGetAllOrderItem(){
        return this.orderItemRepository.findAll()
                .stream()
                .map(orderItemMapper::toOrderItemResponse)
                .toList();
    }

    public OrderItemResponse handleCreateOrderItem(OrderItemRequest request){

        // Lấy product từ DB
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Lấy order nếu có
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));


        BigDecimal price = product.getPrice(); // lấy giá gốc
        BigDecimal total = price.multiply(BigDecimal.valueOf(request.getQuantity())); // tỉnh tổng số tiền dựa trên quantity

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .order(order)
                .quantity(request.getQuantity())
                .price(price)
                .total(total)
                .build();


        this.orderItemRepository.save(orderItem);
        return orderItemMapper.toOrderItemResponse(orderItem);
    }

    public OrderItemResponse handleUpdateOrderItem(Long id, OrderItemRequest request) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        BigDecimal price = product.getPrice();
        BigDecimal total = price.multiply(BigDecimal.valueOf(request.getQuantity()));

        orderItemMapper.updateOrderItem(orderItem, request);
        orderItem.setPrice(price);
        orderItem.setTotal(total);

         this.orderItemRepository.save(orderItem);
        return orderItemMapper.toOrderItemResponse(orderItem);
    }


    public OrderItemResponse handleGetOrderItemById(Long id){
        OrderItem orderItem = this.orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return  orderItemMapper.toOrderItemResponse(orderItem);
    }

    public void handleDeleteOrderItem(Long id){
        if(!orderItemRepository.existsById(id)){
            throw new RuntimeException("Not found with id: "+ id);
        }
        this.orderItemRepository.deleteById(id);
    }
}
