package vn.phankyanh.shopbongda.dashboard.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.domain.BookingStatus;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.dashboard.dto.response.AdminOverviewResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.RevenueChartResponse;
import vn.phankyanh.shopbongda.dashboard.service.DashboardService;
import vn.phankyanh.shopbongda.dashboard.service.MonthlyRevenueAggregator;
import vn.phankyanh.shopbongda.extra.repository.BookingExtraServiceRepository;
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.inventory.repository.InventoryStockRepository;
import vn.phankyanh.shopbongda.maintenance.domain.Maintenance;
import vn.phankyanh.shopbongda.maintenance.repository.MaintenanceRepository;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.order.repository.OrderRepository;
import vn.phankyanh.shopbongda.payment.domain.PaymentMethod;
import vn.phankyanh.shopbongda.payment.domain.PaymentStatus;
import vn.phankyanh.shopbongda.payment.domain.PaymentType;
import vn.phankyanh.shopbongda.payment.repository.PaymentRepository;
import vn.phankyanh.shopbongda.product.repository.ProductRepository;
import vn.phankyanh.shopbongda.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService  {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderItemRepository orderItemRepository;

    private final BookingRepository bookingRepository;

    private final MaintenanceRepository maintenanceRepository;

    private final FootballFieldRepository footballFieldRepository;

    private final PaymentRepository paymentRepository;

    private final InventoryStockRepository inventoryStockRepository;

    private final BookingExtraServiceRepository bookingExtraServiceRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {

        BigDecimal orderRevenue = nullToZero(orderRepository.sumRevenue());
        BigDecimal bookingRevenue = nullToZero(bookingRepository.sumCompletedFieldRevenue());
        BigDecimal extraServiceRevenue = nullToZero(
                bookingExtraServiceRepository.sumRecognizedRevenue(
                        List.of(BookingStatus.COMPLETED)
                )
        );
        BigDecimal revenue = bookingRevenue
                .add(extraServiceRevenue)
                .add(orderRevenue);

        Long sold =
                orderItemRepository.sumProductsSold();

        if (sold == null) {
            sold = 0L;
        }

        Long extraSold = bookingExtraServiceRepository.sumSoldQuantity(
                List.of(BookingStatus.COMPLETED)
        );
        if (extraSold == null) {
            extraSold = 0L;
        }

        return DashboardSummaryResponse.builder()
                .totalUsers(userRepository.count())
                .totalOrders(orderRepository.count())
                .totalProducts(productRepository.count())
                .totalBooking(bookingRepository.count())
                .totalRevenue(revenue)
                .bookingRevenue(bookingRevenue)
                .extraServiceRevenue(extraServiceRevenue)
                .orderRevenue(orderRevenue)
                .totalProductsSold(sold)
                .totalExtraServicesSold(extraSold)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueChartResponse> getMonthlyRevenue() {

        return MonthlyRevenueAggregator.aggregate(
                bookingRepository.getMonthlyRevenue(),
                bookingExtraServiceRepository.getMonthlyRevenue(),
                orderRepository.getMonthlyRevenue()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOverviewResponse getAdminOverview(
            BookingStatus bookingStatus,
            OrderStatus orderStatus
    ) {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        BigDecimal orderRevenue = nullToZero(orderRepository.sumRevenue());
        BigDecimal bookingRevenue = nullToZero(bookingRepository.sumCompletedFieldRevenue());
        BigDecimal extraServiceRevenue = nullToZero(
                bookingExtraServiceRepository.sumRecognizedRevenue(
                        List.of(BookingStatus.COMPLETED)
                )
        );
        BigDecimal totalRevenue = bookingRevenue
                .add(extraServiceRevenue)
                .add(orderRevenue);

        BigDecimal todayBookingRevenue =
                bookingRepository.sumTodayBookingRevenue(
                        startOfDay,
                        endOfDay
                );

        if (todayBookingRevenue == null) {
            todayBookingRevenue = BigDecimal.ZERO;
        }

        List<Maintenance> activeMaintenances =
                maintenanceRepository.findActiveMaintenancesAt(now);

        Map<Long, FootballField> fieldsById =
                footballFieldRepository.findAllById(
                        activeMaintenances.stream()
                                .map(Maintenance::getFieldId)
                                .distinct()
                                .toList()
                )
                .stream()
                .collect(
                        Collectors.toMap(
                                FootballField::getId,
                                Function.identity()
                        )
                );

        List<AdminOverviewResponse.BookingItem> bookingItems =
                bookingRepository.findTodayDashboardBookings(
                                startOfDay,
                                endOfDay,
                                bookingStatus,
                                PageRequest.of(0, 8)
                        )
                        .stream()
                        .map(booking ->
                                AdminOverviewResponse.BookingItem.builder()
                                        .id(booking.getId())
                                        .userName(booking.getUser().getUsername())
                                        .fieldName(booking.getField().getName())
                                        .startTime(booking.getStartTime())
                                        .endTime(booking.getEndTime())
                                        .totalPrice(booking.getTotalPrice())
                                        .status(booking.getStatus().name())
                                        .build()
                        )
                        .toList();

        List<AdminOverviewResponse.MaintenanceItem> maintenanceItems =
                activeMaintenances.stream()
                        .limit(8)
                        .map(maintenance -> {
                            FootballField field =
                                    fieldsById.get(maintenance.getFieldId());

                            return AdminOverviewResponse.MaintenanceItem
                                    .builder()
                                    .id(maintenance.getId())
                                    .fieldId(maintenance.getFieldId())
                                    .fieldName(field != null
                                            ? field.getName()
                                            : "Sân #" + maintenance.getFieldId())
                                    .startTime(maintenance.getStartTime())
                                    .endTime(maintenance.getEndTime())
                                    .reason(maintenance.getReason())
                                    .build();
                        })
                        .toList();

        List<AdminOverviewResponse.OrderItem> orderItems =
                orderRepository.findRecentDashboardOrders(
                                orderStatus,
                                PageRequest.of(0, 8)
                        )
                        .stream()
                        .map(order ->
                                AdminOverviewResponse.OrderItem.builder()
                                        .id(order.getId())
                                        .customerName(order.getShippingAddress().getFullName())
                                        .phone(order.getShippingAddress().getPhone())
                                        .totalAmount(order.getTotalAmount())
                                        .orderStatus(order.getOrderStatus().name())
                                        .paymentMethod(order.getPaymentMethod().name())
                                        .orderDate(order.getOrderDate())
                                        .build()
                        )
                        .toList();

        List<AdminOverviewResponse.PaymentItem> pendingPaymentItems =
                paymentRepository.findDashboardPayments(
                                PaymentStatus.PENDING,
                                PaymentMethod.VIETQR,
                                PageRequest.of(0, 6)
                        )
                        .stream()
                        .map(payment -> {
                            Long targetId = null;
                            String customerName = "Khách hàng";

                            if (payment.getPaymentType() == PaymentType.ORDER
                                    && payment.getOrder() != null) {
                                targetId = payment.getOrder().getId();
                                if (payment.getOrder().getShippingAddress() != null) {
                                    customerName = payment.getOrder()
                                            .getShippingAddress()
                                            .getFullName();
                                }
                            } else if (payment.getPaymentType() == PaymentType.BOOKING) {
                                var booking = bookingRepository
                                        .findByPaymentId(payment.getId());
                                if (booking.isPresent()) {
                                    targetId = booking.get().getId();
                                    if (booking.get().getUser() != null) {
                                        customerName = booking.get().getUser().getUsername();
                                    }
                                }
                            }

                            return AdminOverviewResponse.PaymentItem
                                    .builder()
                                    .id(payment.getId())
                                    .targetType(payment.getPaymentType().name())
                                    .targetId(targetId)
                                    .customerName(customerName)
                                    .totalAmount(payment.getTotalAmount())
                                    .paymentMethod(payment.getPaymentMethod().name())
                                    .paymentStatus(payment.getPaymentStatus().name())
                                    .updatedAt(payment.getUpdatedAt())
                                    .build();
                        })
                        .toList();

        List<AdminOverviewResponse.LowStockItem> lowStockItems =
                inventoryStockRepository.findDashboardLowStocks(PageRequest.of(0, 6))
                        .stream()
                        .map(stock -> AdminOverviewResponse.LowStockItem
                                .builder()
                                .id(stock.getInventoryItem().getId())
                                .itemCode(stock.getInventoryItem().getItemCode())
                                .name(stock.getInventoryItem().getName())
                                .itemType(stock.getInventoryItem().getItemType().name())
                                .availableQuantity(stock.getAvailableQuantity())
                                .lowStockThreshold(stock.getInventoryItem().getLowStockThreshold())
                                .unit(stock.getInventoryItem().getUnit())
                                .build()
                        )
                        .toList();

        return AdminOverviewResponse.builder()
                .totalRevenue(totalRevenue)
                .bookingRevenue(bookingRevenue)
                .extraServiceRevenue(extraServiceRevenue)
                .orderRevenue(orderRevenue)
                .todayBookingRevenue(todayBookingRevenue)
                .todayBookings(
                        bookingRepository
                                .countByStartTimeGreaterThanEqualAndStartTimeLessThan(
                                        startOfDay,
                                        endOfDay
                                )
                )
                .activeMaintenances((long) activeMaintenances.size())
                .newOrders(orderRepository.countByOrderStatus(OrderStatus.PENDING))
                .pendingVietQrPayments(
                        paymentRepository.countByPaymentStatusAndPaymentMethod(
                                PaymentStatus.PENDING,
                                PaymentMethod.VIETQR
                        )
                )
                .lowStockItems(inventoryStockRepository.countLowStocks())
                .todayBookingItems(bookingItems)
                .activeMaintenanceItems(maintenanceItems)
                .newOrderItems(orderItems)
                .pendingPaymentItems(pendingPaymentItems)
                .lowStockItemList(lowStockItems)
                .build();
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
