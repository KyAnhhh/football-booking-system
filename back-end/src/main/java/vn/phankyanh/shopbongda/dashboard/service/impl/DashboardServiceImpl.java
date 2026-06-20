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
import vn.phankyanh.shopbongda.field.domain.FootballField;
import vn.phankyanh.shopbongda.field.repository.FootballFieldRepository;
import vn.phankyanh.shopbongda.maintenance.domain.Maintenance;
import vn.phankyanh.shopbongda.maintenance.repository.MaintenanceRepository;
import vn.phankyanh.shopbongda.order.domain.OrderStatus;
import vn.phankyanh.shopbongda.order.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.order.repository.OrderRepository;
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

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary() {

        BigDecimal revenue =
                orderItemRepository.getTotalRevenue();

        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }

        Long sold =
                orderItemRepository.sumProductsSold();

        if (sold == null) {
            sold = 0L;
        }

        return DashboardSummaryResponse.builder()
                .totalUsers(userRepository.count())
                .totalOrders(orderRepository.count())
                .totalProducts(productRepository.count())
                .totalBooking(bookingRepository.count())
                .totalRevenue(revenue)
                .totalProductsSold(sold)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueChartResponse> getMonthlyRevenue() {

        return bookingRepository
                .getMonthlyRevenue()
                .stream()
                .map(obj -> new RevenueChartResponse(
                        "Month " + obj[0],
                        (BigDecimal) obj[1]
                ))
                .toList();
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

        BigDecimal totalRevenue = orderRepository.sumRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

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

        return AdminOverviewResponse.builder()
                .totalRevenue(totalRevenue)
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
                .todayBookingItems(bookingItems)
                .activeMaintenanceItems(maintenanceItems)
                .newOrderItems(orderItems)
                .build();
    }
}
