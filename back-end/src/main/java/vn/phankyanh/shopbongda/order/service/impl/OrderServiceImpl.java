package vn.phankyanh.shopbongda.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryStockResponse;
import vn.phankyanh.shopbongda.inventory.dto.response.InventoryReservationResponse;
import vn.phankyanh.shopbongda.inventory.domain.InventoryReferenceType;
import vn.phankyanh.shopbongda.inventory.service.InventoryService;
import vn.phankyanh.shopbongda.notification.service.NotificationService;
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
import vn.phankyanh.shopbongda.order.service.OrderService;
import vn.phankyanh.shopbongda.order.service.OrderPriceCalculator;
import vn.phankyanh.shopbongda.payment.domain.Payment;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.payment.service.RefundService;
import vn.phankyanh.shopbongda.payment.domain.PaymentType;
import vn.phankyanh.shopbongda.payment.service.PendingPaymentExpirationPolicy;
import vn.phankyanh.shopbongda.product.domain.ProductVariant;
import vn.phankyanh.shopbongda.product.repository.ProductVariantRepository;
import vn.phankyanh.shopbongda.shared.service.email.EmailService;
import vn.phankyanh.shopbongda.user.domain.ShippingAddress;
import vn.phankyanh.shopbongda.user.domain.User;
import vn.phankyanh.shopbongda.user.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.user.repository.UserRepository;
import vn.phankyanh.shopbongda.user.service.ShippingService;
import vn.phankyanh.shopbongda.catalog.domain.PublicationStatus;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ShippingService shippingService;
    private final EmailService emailService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final RefundService refundService;

    @Override
    public Page<OrderResponse> getAllOrders(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            OrderStatus status
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Order> ordersPage;

        if (status != null && search != null && !search.trim().isEmpty()) {

            ordersPage = orderRepository.findByOrderStatusAndShippingFullName(
                    status,
                    search.trim(),
                    pageable
            );

        } else if (status != null) {

            ordersPage = orderRepository.findByOrderStatus(status, pageable);

        } else if (search != null && !search.trim().isEmpty()) {

            ordersPage = orderRepository.search(search.trim(), pageable);

        } else {

            ordersPage = orderRepository.findAll(pageable);
        }

        return ordersPage.map(orderMapper::toOrderResponse);
    }

    @Override
    public OrderResponse getOrderById(Long id, Long userId) {

        Order order = orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse getOrderForQr(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền truy cập đơn hàng");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order not payable");
        }

        if (order.getPaymentMethod() != PaymentMethod.VIETQR) {
            throw new RuntimeException("Đơn hàng không sử dụng VietQR");
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse markPaymentSubmitted(Long orderId, Long userId) {
        Order order = orderRepository.findByIdWithPayment(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền truy cập đơn hàng");
        }

        if (order.getOrderStatus() != OrderStatus.PENDING
                || order.getPaymentMethod() != PaymentMethod.VIETQR) {
            throw new RuntimeException(
                    "Đơn hàng không thể gửi yêu cầu kiểm tra chuyển khoản"
            );
        }

        Payment payment = order.getPayment();
        if (payment == null) {
            throw new RuntimeException("Order has no payment");
        }

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Đơn hàng đã được thanh toán");
        }

        if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
            return orderMapper.toOrderResponse(order);
        }

        if (payment.getPaymentStatus() != PaymentStatus.UNPAID) {
            throw new RuntimeException(
                    "Trạng thái thanh toán không cho phép cập nhật"
            );
        }

        payment.setPaymentStatus(PaymentStatus.PENDING);
        notificationService.notifyOrderPaymentSubmitted(order);

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getMyOrders(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse createOrder(OrderRequest request, Long userId) {
        validateOrderPaymentMethod(request.getPaymentMethod());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShippingAddress shippingAddress = shippingAddressRepository
                .findByIdAndUserId(
                        request.getShippingAddressId(),
                        userId
                )
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .orderDate(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            ProductVariant variant = productVariantRepository
                    .findById(itemRequest.getProductVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            if (variant.getInventoryItemId() == null
                    && variant.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException(
                        "Not enough stock for variant: "
                                + variant.getProduct().getName()
                );
            }

            BigDecimal unitPrice = variant.getPrice();

            Integer discount = variant.getProduct().getDiscount() != null
                ? variant.getProduct().getDiscount()
                : 0;

            BigDecimal discountAmount = unitPrice
                    .multiply(BigDecimal.valueOf(discount))
                    .divide(BigDecimal.valueOf(100));

            BigDecimal finalPrice = unitPrice.subtract(discountAmount);

            BigDecimal total = finalPrice.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
                    .productId(variant.getProduct().getId())
                    .productName(variant.getProduct().getName())
                    .sizeName(variant.getSize().getName())
                    .imageUrl(
                            variant.getProduct().getImages().isEmpty()
                                    ? null
                                    : variant.getProduct()
                                            .getImages()
                                            .get(0)
                                            .getImageUrl()
                    )
                    .unitPrice(unitPrice)
                    .discountAmount(discountAmount)
                    .finalPrice(finalPrice)
                    .quantity(itemRequest.getQuantity())
                    .total(total)
                    .stockDeducted(false)
                    .stockReserved(false)
                    .stockReturned(false)
                    .build();

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        BigDecimal shippingCost =
                shippingService.calculate(orderItems, shippingAddress);

        order.setShippingCost(shippingCost);

        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(shippingCost);

        order.setTotalAmount(totalAmount);

        Payment payment = Payment.builder()
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentType(PaymentType.ORDER)
                .build();

        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        reserveStockForOrder(savedOrder, userId);

        savedOrder = orderRepository.save(savedOrder);

        notificationService.notifyOrderCreated(savedOrder);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public void deleteOrder(Long id) {

        Order order = orderRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new RuntimeException("Not found with id: " + id));
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Không thể xóa đơn đã xử lý; hãy giữ lịch sử"
            );
        }

        returnStockForOrder(order, null);
        order.setOrderStatus(OrderStatus.CANCELLED);
        Payment payment = order.getPayment();
        updatePaymentForCancellation(order, "Order cancelled");
    }

    @Override
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        validateOrderPaymentMethod(request.getPaymentMethod());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        Set<Long> requestedItemIds =
                new HashSet<>(request.getCartItemIds());
        if (requestedItemIds.size() != request.getCartItemIds().size()) {
            throw new IllegalArgumentException(
                    "Danh sách sản phẩm checkout bị trùng"
            );
        }

        List<CartItem> selectedItems = cart.getItems().stream()
            .filter(item -> requestedItemIds.contains(item.getId()))
            .toList();

        if (selectedItems.size() != requestedItemIds.size()) {
            throw new IllegalArgumentException(
                    "Có sản phẩm không thuộc giỏ hàng hiện tại"
            );
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

            if (variant.getInventoryItemId() == null
                    && variant.getStock() < cartItem.getTotalQuantity()) {

                throw new RuntimeException(
                    "Not enough stock for product: "
                        + cartItem.getProductName()
                );
            }

            int discountPercent = variant.getProduct().getDiscount() == null
                    ? 0
                    : variant.getProduct().getDiscount();
            OrderPriceCalculator.Price price =
                    OrderPriceCalculator.calculate(
                            cartItem.getPriceAtAddedTime(),
                            discountPercent,
                            cartItem.getTotalQuantity()
                    );
            BigDecimal itemTotal = price.total();

            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productVariant(variant)
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .sizeName(variant.getSize().getName())
                .quantity(cartItem.getTotalQuantity())
                .unitPrice(price.unitPrice())
                .discountAmount(price.discountAmount())
                .finalPrice(price.finalPrice())
                .total(itemTotal)
                .stockDeducted(false)
                .stockReserved(false)
                .stockReturned(false)
                .imageUrl(
                    variant.getProduct().getImages().isEmpty()
                        ? null
                        : variant.getProduct()
                            .getImages()
                            .get(0)
                            .getImageUrl()
                )
                .build();

            orderItems.add(orderItem);

            totalAmount = totalAmount.add(itemTotal);
        }

        order.setOrderItems(orderItems);

        BigDecimal shippingCost =
            shippingService.calculate(orderItems, shippingAddress);

        BigDecimal finalTotalAmount = totalAmount.add(shippingCost);

        Payment payment = Payment.builder()
            .totalAmount(finalTotalAmount)
            .paymentMethod(request.getPaymentMethod())
            .paymentStatus(PaymentStatus.UNPAID)
            .paymentType(PaymentType.ORDER)
            .build();

        order.setShippingCost(shippingCost);

        order.setPayment(payment);

        order.setTotalAmount(finalTotalAmount);

        Order savedOrder = orderRepository.save(order);

        reserveStockForOrder(savedOrder, userId);

        orderRepository.save(savedOrder);

        selectedItems.forEach(cartItemRepository::delete);

        cart.getItems().removeAll(selectedItems);

        BigDecimal cartTotal = cart.getItems().stream()
            .map(i -> i.getPriceAtAddedTime()
                .multiply(BigDecimal.valueOf(i.getTotalQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(cartTotal);

        notificationService.notifyOrderCreated(savedOrder);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() == newStatus) {
            return orderMapper.toOrderResponse(order);
        }

        if (!isValidStatusTransition(order.getOrderStatus(), newStatus)) {
            throw new RuntimeException("Invalid order status transition");
        }

        OrderStatus previousStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);

        boolean shouldSendEmail = false;

        if (newStatus == OrderStatus.CONFIRMED) {
            Payment payment = order.getPayment();
            if (order.getPaymentMethod() == PaymentMethod.VIETQR
                    && (payment == null
                    || payment.getPaymentStatus() != PaymentStatus.PAID)) {
                throw new IllegalStateException(
                        "Thanh toán VietQR chưa được xác nhận"
                );
            }

            commitStockForOrder(order, null);

        } else if (newStatus == OrderStatus.DELIVERED) {

            Payment payment = order.getPayment();

            if (payment != null
                    && payment.getPaymentMethod() == PaymentMethod.COD
                    && payment.getPaymentStatus() == PaymentStatus.UNPAID) {

                payment.setPaymentStatus(PaymentStatus.PAID);
                payment.setPaymentDate(LocalDateTime.now());
            }

            order.setDeliveryDate(LocalDateTime.now());

            shouldSendEmail = true;

        } else if (newStatus == OrderStatus.SHIPPING) {

            shouldSendEmail = true;

        } else if (newStatus == OrderStatus.CANCELLED) {

            returnStockForOrder(order, null);

            Payment payment = order.getPayment();

            updatePaymentForCancellation(order, "Order cancelled by status update");

            shouldSendEmail = true;
        }

        if (shouldSendEmail) {

            emailService.sendEmailWhenOrderStatusChange(
                    order.getUser().getEmail(),
                    order.getId(),
                    newStatus
            );
        }

        notificationService.notifyUserOrderStatusChanged(order, previousStatus);

        return orderMapper.toOrderResponse(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) {

        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + orderId
                        )
                );

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn này");
        }

        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new RuntimeException("Không thể hủy đơn hàng này");
        }

        OrderStatus previousStatus = order.getOrderStatus();
        order.setOrderStatus(OrderStatus.CANCELLED);

        order.setUpdatedAt(LocalDateTime.now());

        returnStockForOrder(order, userId);

        Payment payment = order.getPayment();
        updatePaymentForCancellation(order, "Customer cancelled order");

        orderRepository.save(order);
        notificationService.notifyUserOrderStatusChanged(order, previousStatus);
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expirePendingVietQrOrders() {
        List<Long> orderIds = orderRepository.findPendingIdsCreatedBefore(
                PaymentMethod.VIETQR,
                LocalDateTime.now().minusMinutes(30)
        );

        for (Long orderId : orderIds) {
            Order order = orderRepository.findByIdForUpdate(orderId)
                    .orElse(null);
            if (order == null
                    || order.getOrderStatus() != OrderStatus.PENDING) {
                continue;
            }
            Payment payment = order.getPayment();
            if (payment == null
                    || !PendingPaymentExpirationPolicy.shouldExpire(
                            payment.getPaymentStatus()
                    )) {
                continue;
            }
            OrderStatus previousStatus = order.getOrderStatus();
            returnStockForOrder(order, null);
            order.setOrderStatus(OrderStatus.CANCELLED);
            updatePaymentForCancellation(order, "VietQR order expired");
            notificationService.notifyUserOrderStatusChanged(
                    order,
                    previousStatus
            );
        }
    }

    @Override
    public void confirmReceive(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + orderId
                        )
                );

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException(
                    "User không có quyền cập nhật đơn hàng này"
            );
        }

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException(
                    "Chỉ có thể xác nhận nhận hàng khi đơn đã được giao"
            );
        }

        OrderStatus previousStatus = order.getOrderStatus();
        order.setOrderStatus(OrderStatus.COMPLETED);

        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        notificationService.notifyUserOrderStatusChanged(order, previousStatus);
    }

    private boolean isValidStatusTransition(
            OrderStatus current,
            OrderStatus newStatus
    ) {

        if (current == OrderStatus.PENDING) {

            return newStatus == OrderStatus.CONFIRMED
                    || newStatus == OrderStatus.CANCELLED;

        } else if (current == OrderStatus.CONFIRMED) {

            return newStatus == OrderStatus.SHIPPING
                    || newStatus == OrderStatus.CANCELLED;

        } else if (current == OrderStatus.SHIPPING) {

            return newStatus == OrderStatus.DELIVERED;

        } else if (current == OrderStatus.DELIVERED) {

            return newStatus == OrderStatus.COMPLETED;
        }

        return false;
    }

    private void reserveStockForOrder(
            Order order,
            Long actorId
    ) {
        for (OrderItem item : order.getOrderItems()) {
            if (Boolean.TRUE.equals(item.getStockReserved())
                    || Boolean.TRUE.equals(item.getStockDeducted())) {
                continue;
            }

            ProductVariant variant = item.getProductVariant();
            if (variant.getProduct().getPublicationStatus()
                    != PublicationStatus.ACTIVE) {
                throw new IllegalStateException(
                        "Sản phẩm chưa được mở bán: " + item.getProductName()
                );
            }

            if (variant.getInventoryItemId() != null) {
                InventoryReservationResponse reservation =
                        inventoryService.reserveStock(
                        variant.getInventoryItemId(),
                        item.getQuantity(),
                        InventoryReferenceType.ORDER,
                        order.getId(),
                        item.getId(),
                        actorId
                );

                item.setInventoryReservationId(reservation.getId());
                item.setStockReserved(true);
                syncVariantStockFromInventory(variant);
            } else {
                if (variant.getStock() < item.getQuantity()) {
                    throw new RuntimeException(
                            "Not enough stock for product: "
                                    + item.getProductName()
                    );
                }

                variant.setStock(
                        variant.getStock() - item.getQuantity()
                );
                variant.setSoldQuantity(
                        safeNumber(variant.getSoldQuantity()) + item.getQuantity()
                );
                item.setStockDeducted(true);
            }

            item.setStockReturned(false);
        }
    }

    private void commitStockForOrder(
            Order order,
            Long actorId
    ) {
        for (OrderItem item : order.getOrderItems()) {
            if (!Boolean.TRUE.equals(item.getStockReserved())
                    || item.getInventoryReservationId() == null) {
                continue;
            }

            InventoryStockResponse costStock =
                    inventoryService.getStockByItemId(
                            item.getProductVariant().getInventoryItemId()
                    );
            inventoryService.commitReservation(
                    item.getInventoryReservationId(),
                    actorId
            );
            BigDecimal unitCost = costStock.getAverageUnitCost();
            BigDecimal totalCost = unitCost.multiply(
                    BigDecimal.valueOf(item.getQuantity())
            );
            item.setUnitCost(unitCost);
            item.setTotalCost(totalCost);
            item.setGrossProfit(item.getTotal().subtract(totalCost));

            ProductVariant variant = item.getProductVariant();
            syncVariantStockFromInventory(variant);
            variant.setSoldQuantity(
                    safeNumber(variant.getSoldQuantity()) + item.getQuantity()
            );
            item.setStockReserved(false);
            item.setStockDeducted(true);
            item.setStockReturned(false);
        }
    }

    private void returnStockForOrder(
            Order order,
            Long actorId
    ) {
        for (OrderItem item : order.getOrderItems()) {
            if ((!Boolean.TRUE.equals(item.getStockDeducted())
                    && !Boolean.TRUE.equals(item.getStockReserved()))
                    || Boolean.TRUE.equals(item.getStockReturned())) {
                continue;
            }

            ProductVariant variant = item.getProductVariant();

            if (variant.getInventoryItemId() != null) {
                if (Boolean.TRUE.equals(item.getStockReserved())) {
                    inventoryService.releaseReservation(
                            item.getInventoryReservationId(),
                            actorId
                    );
                    item.setStockReserved(false);
                } else {
                    inventoryService.returnForOrder(
                            variant.getInventoryItemId(),
                            item.getQuantity(),
                            order.getId(),
                            actorId
                    );
                    variant.setSoldQuantity(
                            Math.max(
                                    0,
                                    safeNumber(variant.getSoldQuantity())
                                            - item.getQuantity()
                            )
                    );
                }

                syncVariantStockFromInventory(variant);
            } else {
                variant.setStock(
                        safeNumber(variant.getStock()) + item.getQuantity()
                );
                variant.setSoldQuantity(
                        Math.max(
                                0,
                                safeNumber(variant.getSoldQuantity())
                                    - item.getQuantity()
                        )
                );
            }
            item.setStockReturned(true);
        }
    }

    private void syncVariantStockFromInventory(ProductVariant variant) {
        InventoryStockResponse stock =
                inventoryService.getStockByItemId(
                        variant.getInventoryItemId()
                );

        variant.setStock(stock.getAvailableQuantity());
    }

    private int safeNumber(Integer value) {
        return value == null ? 0 : value;
    }

    private void updatePaymentForCancellation(Order order, String reason) {
        Payment payment = order.getPayment();
        if (payment == null) {
            return;
        }
        if (payment.getPaymentStatus() == PaymentStatus.PAID
                || payment.getPaymentStatus() == PaymentStatus.PENDING) {
            refundService.createForCancellation(
                    payment, order.getUser(), order.getId(), null, reason
            );
        } else if (payment.getPaymentStatus() == PaymentStatus.UNPAID) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }
        payment.setPaymentDate(LocalDateTime.now());
    }

    private void validateOrderPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod != PaymentMethod.COD
                && paymentMethod != PaymentMethod.VIETQR) {
            throw new IllegalArgumentException(
                    "Đơn hàng hiện chỉ hỗ trợ COD hoặc VIETQR"
            );
        }
    }
}
