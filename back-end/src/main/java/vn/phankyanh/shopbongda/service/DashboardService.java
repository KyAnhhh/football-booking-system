package vn.phankyanh.shopbongda.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.phankyanh.shopbongda.dto.response.DashboardSummaryResponse;
import vn.phankyanh.shopbongda.repository.OrderItemRepository;
import vn.phankyanh.shopbongda.repository.OrderRepository;
import vn.phankyanh.shopbongda.repository.ProductRepository;
import vn.phankyanh.shopbongda.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DashboardService  {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public DashboardSummaryResponse getDashboardSummary() {
        return DashboardSummaryResponse.builder()
                .totalUsers(userRepository.count())
                .totalOrders(orderRepository.count())
                .totalProducts(productRepository.count())
                .totalProductsSold(orderItemRepository.sumProductsSold())
                .totalRevenue(orderItemRepository.getTotalRevenue())
                .build();
    }
}