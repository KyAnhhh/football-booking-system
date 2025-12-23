package vn.phankyanh.shopbongda.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.domain.OrderStatus;
import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.domain.ProductVariant;
import vn.phankyanh.shopbongda.domain.ShippingAddress;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.mapper.OrderMapper;
import vn.phankyanh.shopbongda.repository.OrderRepository;
import vn.phankyanh.shopbongda.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final PaymentService paymentService;
    private final ProductVariantRepository productVariantRepository;

    public List<OrderResponse> handleGetAllOrder(){
        return this.orderRepository.findAllWithOrderItems()
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse handleGetOrderById(Long id, Long userId){
        Order order = this.orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        if (!order.getUser().getId().equals(userId)) {
        throw new RuntimeException("Access denied");
    }
        return  orderMapper.toOrderResponse(order);
    }

    public List<OrderResponse> handleGetMyOrders(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse handleCreateOrder(OrderRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user == null) {
                throw new RuntimeException("USER IS NULL - JWT FILTER FAILED");
            }

        ShippingAddress shippingAddress = shippingAddressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        BigDecimal shippingCost = request.getShippingCost() != null
                ? request.getShippingCost()
                : BigDecimal.ZERO;

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .shippingCost(shippingCost)
                .status(OrderStatus.CHO_XAC_NHAN)
                .orderDate(LocalDateTime.now())
                .build();
        


        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItemRequest itemRequest : request.getItems()){
            ProductVariant variant = productVariantRepository.findById(itemRequest.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

            if(variant.getStock() < itemRequest.getQuantity()){
                throw new RuntimeException("Not enough stock for variant: " + variant.getProduct().getName());
            }

            // Giảm tồn kho ngay lập tức
            variant.setStock(variant.getStock() - itemRequest.getQuantity());
            variant.setSoldQuantity(variant.getSoldQuantity() + itemRequest.getQuantity());


            BigDecimal unitPrice = variant.getPrice();
            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal finalPrice = unitPrice.subtract(discountAmount);
            BigDecimal total = finalPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
                    .productId(variant.getProduct().getId())
                    .productName(variant.getProduct().getName())
                    .sizeName(variant.getSize().getName())
                    .imageUrl(variant.getProduct().getImages().isEmpty()
                            ? null
                            : variant.getProduct().getImages().get(0).getImageUrl())
                    .unitPrice(unitPrice)
                    .discountAmount(discountAmount)
                    .finalPrice(finalPrice)
                    .quantity(itemRequest.getQuantity())
                    .total(total)
                    .build();

            orderItems.add(orderItem);
            
        }
        order.setOrderItems(orderItems);

        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(shippingCost);

        // do chưa cần tới payment qua các phương thức banking nên sử dụng payment đc khởi tạo mặc định trước
        Payment payment = paymentService.createPayment(totalAmount, "Tiền mặt", "Giao hàng tận nơi");

        order.setPayment(payment);
        order.setTotalAmount(totalAmount);

        return orderMapper.toOrderResponse(this.orderRepository.save(order));
    }

    public Order handleUpdateOrder(Long id, OrderRequest request){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: "+ id));

        // thiếu update sp mới và xóa sp cũ và tính tiền
        orderMapper.updateOrder(order , request);
        return orderRepository.save(order);

    }
    
    //xóa
    public void handleDeleteOrder(Long id){
        if(!orderRepository.existsById(id)){
            throw new RuntimeException("Not found with id: "+ id);
        }
        this.orderRepository.deleteById(id);
    }

    @Transactional
    public OrderResponse handleUpdateOrderStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findByIdWithOrderItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
       
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    //Hủy đơn hàng khi status !CHO_XAC_NHAN     
    @Transactional
    public void handleCancelOrder(Long orderId) {

        Order order = orderRepository.findByIdWithOrderItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if(!order.getStatus().equals(OrderStatus.CHO_XAC_NHAN)){
            throw new RuntimeException("Không thể hủy đơn hàng này");
        }
        order.setStatus(OrderStatus.DA_HUY);
        order.setUpdatedAt(LocalDateTime.now());

        for(OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStock(variant.getStock() + item.getQuantity());
            variant.setSoldQuantity(variant.getSoldQuantity() - item.getQuantity());
        }

        this.orderRepository.save(order);
    }
}
