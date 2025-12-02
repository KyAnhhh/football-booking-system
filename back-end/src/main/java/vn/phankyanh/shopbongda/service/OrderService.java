package vn.phankyanh.shopbongda.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.phankyanh.shopbongda.domain.*;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.mapper.OrderMapper;
import vn.phankyanh.shopbongda.repository.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final PaymentService paymentService;

    public List<OrderResponse> handleGetAllOrder(){
        return this.orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse handleCreateOrder(OrderRequest request, String authHeader) throws ParseException {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Order must have at least one item");
        }
        String token = authHeader.replace("Bearer ", "");
        JwtInfo info = JwtService.parseToken(token);

        User user = userRepository.findById(info.getId())
                        .orElseThrow(() -> new RuntimeException("Order not found with id " + info.getId()));

        ShippingAddress shippingAddress = shippingAddressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setShippingCost(request.getShippingCost() != null ? request.getShippingCost() : 0);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for(OrderItemRequest itemRequest : request.getItems()){
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id" +  itemRequest.getProductId()));

            BigDecimal price = product.getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(price) // lưu giá tại thời điểm mua
                    .total(itemTotal)
                    .discount(0)  // có thể để mặc định hoặc lấy từ request
                    .build();

            orderItems.add(orderItem);
            total = total.add(itemTotal);
        }

        // do chưa cần tới payment qua các phương thức banking nên sử dụng payment đc khởi tạo mặc định trước
        Payment payment = paymentService.createPayment(total, "Tiền mặt", "Giao hàng tận nơi");

        paymentRepository.save(payment);

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        order.setPayment(payment);

        this.orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public Order handleUpdateOrder(Long id, OrderRequest request){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: "+ id));

        // thiếu update sp mới và xóa sp cũ và tính tiền
        orderMapper.updateOrder(order , request);
        return orderRepository.save(order);

    }

    public OrderResponse handleUpdateOrderStatus(Long orderId, String newStatus){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus("Đã giao hàng");
        if("Đã giao hàng".equals(newStatus)){
            order.setDeliveryDate(LocalDateTime.now());
        }

        this.orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse handleGetOrderById(Long id){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return  orderMapper.toOrderResponse(order);
    }

    public void handleDeleteOrder(Long id){
        if(!orderRepository.existsById(id)){
            throw new RuntimeException("Not found with id: "+ id);
        }
        this.orderRepository.deleteById(id);
    }
}
