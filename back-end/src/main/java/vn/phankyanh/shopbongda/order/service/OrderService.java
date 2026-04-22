package vn.phankyanh.shopbongda.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.order.domain.Cart;
import vn.phankyanh.shopbongda.order.domain.CartItem;
import vn.phankyanh.shopbongda.order.domain.Order;
import vn.phankyanh.shopbongda.order.domain.OrderItem;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.dto.request.CheckoutRequest;
import vn.phankyanh.shopbongda.order.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.order.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.order.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.order.mapper.OrderMapper;
import vn.phankyanh.shopbongda.order.repository.CartItemRepository;
import vn.phankyanh.shopbongda.order.repository.CartRepository;
import vn.phankyanh.shopbongda.order.repository.OrderRepository;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.shared.service.email.EmailService;
import vn.phankyanh.shopbongda.user.domain.ShippingAddress;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.user.service.ShippingService;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ShippingService shippingService;
    private final EmailService emailService;

    public Page<OrderResponse> handleGetAllOrder(int pageNo, int pageSize, String sortBy, String sortDir, String search, OrderStatus status) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Order> ordersPage;

        if (status != null && search != null && !search.trim().isEmpty()) {
            // Nếu có status + search
            ordersPage = orderRepository.findByOrderStatusAndShippingFullName(status, search.trim(), pageable);
        } else if (status != null) {
            // Chỉ filter theo status
            ordersPage = orderRepository.findByOrderStatus(status, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            // Chỉ search theo keyword
            ordersPage = orderRepository.search(search.trim(), pageable);
        } else {
            // Lấy tất cả
            ordersPage = orderRepository.findAll(pageable);
        }

        // Chuyển Order -> OrderResponse
        return ordersPage.map(orderMapper::toOrderResponse);
    }

    public OrderResponse handleGetOrderById(Long id, Long userId){
        Order order = this.orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        if (!order.getUser().getId().equals(userId)) {
        throw new RuntimeException("Access denied");
    }
        return  orderMapper.toOrderResponse(order);
    }

    public OrderResponse getOrderForQr(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order not payable");
        }

        return orderMapper.toOrderResponse(order);
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

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
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

            Integer discount = variant.getProduct().getDiscount();

            BigDecimal discountAmount = unitPrice
                    .multiply(BigDecimal.valueOf(discount))
                    .divide(BigDecimal.valueOf(100));

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

        BigDecimal shippingCost = shippingService.calculate(orderItems, shippingAddress);
        order.setShippingCost(shippingCost);

        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(shippingCost);

        order.setTotalAmount(totalAmount);

        // Sử dụng paymentMethod từ request
        Payment payment = Payment.builder()
                        .totalAmount(totalAmount)
                        .paymentMethod(request.getPaymentMethod())
                        .paymentStatus(PaymentStatus.UNPAID)
                        .type("Giao hàng tận nơi")
                        .build();

        order.setPayment(payment);

        return orderMapper.toOrderResponse(this.orderRepository.save(order));
    }


    //update
    public Order handleUpdateOrder(Long id, OrderRequest request, Long userId){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: "+ id));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("User không có quyền cập nhật đơn hàng này");
        }
        
        orderMapper.updateOrder(order , request);
        return orderRepository.save(order);

    }
    
    //delete
    public void handleDeleteOrder(Long id){
        if(!orderRepository.existsById(id)){
            throw new RuntimeException("Not found with id: "+ id);
        }
        this.orderRepository.deleteById(id);
    }

    @Transactional
    public OrderResponse handleUpdateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findByIdWithPayment(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if(order.getOrderStatus() == OrderStatus.DELIVERED){
            throw new RuntimeException("Order already delivered");
        }

        if(!isValidStatusTransition(order.getOrderStatus(), newStatus)) {
            throw new RuntimeException("Invalid orderstatus transition");
        }

        if (order.getOrderStatus() == newStatus) {
            return orderMapper.toOrderResponse(order);
        }

        order.setOrderStatus(newStatus);

        boolean shouldSendEmail = false;

        if (newStatus == OrderStatus.DELIVERED) {

            Payment payment = order.getPayment();
            if (payment != null && payment.getPaymentStatus() == PaymentStatus.UNPAID) {
                payment.setPaymentStatus(PaymentStatus.PAID);
                payment.setPaymentDate(LocalDateTime.now());
            }

            order.setDeliveryDate(LocalDateTime.now());

            shouldSendEmail = true;
            

        } else if(newStatus == OrderStatus.SHIPPING){

            shouldSendEmail = true;

        } else if (newStatus == OrderStatus.CANCELLED) {
            // Hoàn trả stock
            for (OrderItem item : order.getOrderItems()) {
                ProductVariant variant = item.getProductVariant();
                variant.setStock(variant.getStock() + item.getQuantity());
                variant.setSoldQuantity(Math.max(0, variant.getSoldQuantity() - item.getQuantity()));
            }

            // Nếu đơn hàng bị hủy và chưa thanh toán, cập nhật trạng thái thanh toán thành thất bại
            Payment payment = order.getPayment();
            if (payment != null && payment.getPaymentStatus() == PaymentStatus.UNPAID) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setPaymentDate(LocalDateTime.now());
            }
            shouldSendEmail = true;
        }

        if(shouldSendEmail){
            emailService.sendEmailWhenOrderStatusChange(
                order.getUser().getEmail(), 
                order.getId(), 
                newStatus);
        }

        return orderMapper.toOrderResponse(order);
    }


    //Hủy đơn hàng khi status !PENDING     
    @Transactional
    public void handleCancelOrder(Long orderId) {

        Order order = orderRepository.findByIdWithOrderItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if(!order.getOrderStatus().equals(OrderStatus.PENDING)){
            throw new RuntimeException("Không thể hủy đơn hàng này");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        for(OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStock(variant.getStock() + item.getQuantity());
            variant.setSoldQuantity(variant.getSoldQuantity() - item.getQuantity());
        }

        this.orderRepository.save(order);
    }

    // checkout in cart
    @Transactional
    public OrderResponse handleCheckout(Long userId, CheckoutRequest request) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> selectedItems = cart.getItems().stream()
            .filter(item -> request.getCartItemIds().contains(item.getId()))
            .toList();

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("No cart items selected for checkout");
        }

        ShippingAddress shippingAddress = shippingAddressRepository
            .findByIdAndUserId(request.getShippingAddressId(), userId)
            .orElseThrow(() -> new RuntimeException("Shipping address not found"));
        Order order = Order.builder()
            .user(user)
            .shippingAddress(shippingAddress)
            .orderDate(LocalDateTime.now())
            .orderStatus(OrderStatus.PENDING)
            .paymentMethod(request.getPaymentMethod())
            .createdAt(LocalDateTime.now())
            .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : selectedItems) {

            ProductVariant variant = cartItem.getProductVariant();

            if (variant.getStock() < cartItem.getTotalQuantity()) {
                throw new RuntimeException(
                    "Not enough stock for product: " + cartItem.getProductName()
                );
            }

            // Trừ stock
            variant.setStock(variant.getStock() - cartItem.getTotalQuantity());
            variant.setSoldQuantity(
                variant.getSoldQuantity() + cartItem.getTotalQuantity()
            );

            BigDecimal unitPrice = variant.getPrice();
            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal finalPrice = unitPrice.subtract(discountAmount);

            BigDecimal itemTotal = cartItem.getPriceAtAddedTime()
                .multiply(BigDecimal.valueOf(cartItem.getTotalQuantity()));

            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productVariant(variant)
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .sizeName(variant.getSize().getName())
                .quantity(cartItem.getTotalQuantity())
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .unitPrice(cartItem.getPriceAtAddedTime())
                .total(itemTotal)
                .imageUrl(variant.getProduct().getImages().isEmpty()
                            ? null
                            : variant.getProduct().getImages().get(0).getImageUrl())
                .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setOrderItems(orderItems);

        BigDecimal shippingCost = shippingService.calculate(orderItems, shippingAddress);

        BigDecimal finalTotalAmount = totalAmount.add(shippingCost);

        Payment payment = Payment.builder()
            .totalAmount(finalTotalAmount)
            .paymentMethod(request.getPaymentMethod())
            .paymentStatus(PaymentStatus.UNPAID)
            .type("Giao hàng tận nơi")
            .build();
        
        order.setShippingCost(shippingCost);

        order.setPayment(payment);

        order.setTotalAmount(finalTotalAmount);

        orderRepository.save(order);

        selectedItems.forEach(cartItemRepository::delete);
        cart.getItems().removeAll(selectedItems);

        BigDecimal cartTotal = cart.getItems().stream()
            .map(i -> i.getPriceAtAddedTime()
                .multiply(BigDecimal.valueOf(i.getTotalQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(cartTotal);

        return orderMapper.toOrderResponse(order);
    }
    public void handleConfirmReceive(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("User không có quyền cập nhật đơn hàng này");
        }

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Chỉ có thể xác nhận nhận hàng khi đơn đã được giao");
        }

        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus newStatus) {
        if (current == OrderStatus.PENDING) {
            return newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
        }else if(current == OrderStatus.CONFIRMED) {
            return newStatus == OrderStatus.SHIPPING;
        } else if (current == OrderStatus.SHIPPING) {
            return newStatus == OrderStatus.DELIVERED;
        } else if (current == OrderStatus.DELIVERED) {
            return newStatus == OrderStatus.COMPLETED;
        }
        return false;
    }
}
