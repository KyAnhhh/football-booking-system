package vn.phankyanh.shopbongda.dashboard.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.booking.repository.BookingRepository;
import vn.phankyanh.shopbongda.dashboard.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.dashboard.dto.response.RevenueChartResponse;
import vn.phankyanh.shopbongda.dashboard.service.DashboardService;
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

    @Override
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
}