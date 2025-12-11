package vn.phankyanh.shopbongda.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.domain.Cart;
import vn.phankyanh.shopbongda.domain.CartItem;
import vn.phankyanh.shopbongda.domain.Order;
import vn.phankyanh.shopbongda.domain.OrderItem;
import vn.phankyanh.shopbongda.domain.Payment;
import vn.phankyanh.shopbongda.domain.Product;
import vn.phankyanh.shopbongda.domain.ShippingAddress;
import vn.phankyanh.shopbongda.domain.User;
import vn.phankyanh.shopbongda.dto.JwtInfo;
import vn.phankyanh.shopbongda.dto.request.OrderItemRequest;
import vn.phankyanh.shopbongda.dto.request.OrderRequest;
import vn.phankyanh.shopbongda.dto.response.OrderResponse;
import vn.phankyanh.shopbongda.mapper.OrderMapper;
import vn.phankyanh.shopbongda.repository.CartRepository;
import vn.phankyanh.shopbongda.repository.OrderRepository;
import vn.phankyanh.shopbongda.repository.PaymentRepository;
import vn.phankyanh.shopbongda.repository.ProductRepository;
import vn.phankyanh.shopbongda.repository.ShippingAddressRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

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
    private final CartRepository cartRepository;

    public List<OrderResponse> handleGetAllOrder(){
        return this.orderRepository.findAllWithOrderItems()
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Transactional
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
        Integer shippingCost = request.getShippingCost();
        order.setShippingCost(shippingCost != null ? shippingCost : 0);
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
                    .productId(product.getId())
                    .productName(product.getName())
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

        Order savedOrder = this.orderRepository.save(order);
        // Reload order với JOIN FETCH để eager load product
        return orderMapper.toOrderResponse(
            this.orderRepository.findByIdWithOrderItems(savedOrder.getId()).orElse(savedOrder)
        );
    }

    public Order handleUpdateOrder(Long id, OrderRequest request){
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: "+ id));

        // thiếu update sp mới và xóa sp cũ và tính tiền
        orderMapper.updateOrder(order , request);
        return orderRepository.save(order);

    }

    public OrderResponse handleUpdateOrderStatus(Long orderId, String newStatus) {

        Order order = orderRepository.findByIdWithOrderItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        String oldStatus = order.getStatus();   // lấy trạng thái cũ

        order.setStatus(newStatus);

        // Chỉ cập nhật stock + soldQuantity khi trạng thái chuyển sang "Đã giao hàng"
        if (!oldStatus.equals("Đã giao hàng") && newStatus.equals("Đã giao hàng")) {
            order.setDeliveryDate(LocalDateTime.now());
            for (OrderItem item : order.getOrderItems()) {
                Product p = item.getProduct();

                // Tăng sản phẩm đã bán
                p.setSoldQuantity(p.getSoldQuantity() + item.getQuantity());

                // Giảm đồ tồn kho
                p.setStock(p.getStock() - item.getQuantity());

                productRepository.save(p);
            }
        }

        // Lưu order vào DB
        orderRepository.save(order);

        // Trả về response
        return orderMapper.toOrderResponse(order);
    }


    public OrderResponse handleGetOrderById(Long id){
        Order order = this.orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() -> new RuntimeException("Not found with id: " + id));
        return  orderMapper.toOrderResponse(order);
    }

    public List<OrderResponse> handleGetMyOrders(String authHeader) throws ParseException {
        String token = authHeader.replace("Bearer ", "");
        JwtInfo info = JwtService.parseToken(token);

        List<Order> orders = orderRepository.findByUserId(info.getId());
        return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
    }


    public void handleDeleteOrder(Long id){
        if(!orderRepository.existsById(id)){
            throw new RuntimeException("Not found with id: "+ id);
        }
        this.orderRepository.deleteById(id);
    }

    // Checkout giỏ hàng thành Order
    @Transactional
    public OrderResponse handleCheckoutCart(Long cartId, Long shippingAddressId, String authHeader) throws ParseException {
        Cart cart = cartRepository.findByIdWithItemsAndUser(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        ShippingAddress shippingAddress = shippingAddressRepository.findById(shippingAddressId)
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));
        
        // Lấy user từ token
        String token = authHeader.replace("Bearer ", "");
        JwtInfo info = JwtService.parseToken(token);
        
        User user = userRepository.findById(info.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Tạo Order từ Cart
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setShippingCost(0);
        order.setOrderDate(LocalDateTime.now());
        
        // Chuyển CartItem -> OrderItem
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (CartItem cartItem : cart.getItems()) {
            // use denormalized productId to load product entity to avoid lazy proxy issues
            Long prodId = cartItem.getProductId();
            Product product = productRepository.findById(prodId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + prodId));

            BigDecimal price = cartItem.getPriceAtAddedTime();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(cartItem.getTotalQuantity()));

            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .productId(product.getId())
                .productName(product.getName())
                .quantity(cartItem.getTotalQuantity())
                .price(price)
                .total(itemTotal)
                .discount(0)
                .build();

            orderItems.add(orderItem);
            total = total.add(itemTotal);
        }
        
        // Tạo Payment
        Payment payment = paymentService.createPayment(total, "Tiền mặt", "Giao hàng tận nơi");
        paymentRepository.save(payment);
        
        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        order.setPayment(payment);
        
        Order savedOrder = this.orderRepository.save(order);
        
        // Xóa giỏ hàng sau khi checkout thành công
        cartRepository.deleteById(cartId);
        
        return orderMapper.toOrderResponse(
            this.orderRepository.findByIdWithOrderItems(savedOrder.getId()).orElse(savedOrder)
        );
    }
}
