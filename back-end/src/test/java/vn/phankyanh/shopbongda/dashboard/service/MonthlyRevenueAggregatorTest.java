package vn.phankyanh.shopbongda.dashboard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class MonthlyRevenueAggregatorTest {

    @Test
    void combinesBookingAndOrderRevenueWithoutMixingYears() {
        List<Object[]> bookings = List.of(
                new Object[] {2025, 7, new BigDecimal("100000")},
                new Object[] {2026, 7, new BigDecimal("200000")}
        );
        List<Object[]> orders = List.<Object[]>of(
                new Object[] {2026, 7, new BigDecimal("300000")}
        );
        List<Object[]> extras = List.<Object[]>of(
                new Object[] {2026, 7, new BigDecimal("50000")}
        );

        var result = MonthlyRevenueAggregator.aggregate(bookings, extras, orders);

        assertEquals("07/2025", result.get(0).getMonth());
        assertEquals(new BigDecimal("100000"), result.get(0).getBookingRevenue());
        assertEquals(BigDecimal.ZERO, result.get(0).getExtraServiceRevenue());
        assertEquals(BigDecimal.ZERO, result.get(0).getOrderRevenue());
        assertEquals(new BigDecimal("100000"), result.get(0).getRevenue());
        var july2026 = result.stream()
                .filter(item -> "07/2026".equals(item.getMonth()))
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("200000"), july2026.getBookingRevenue());
        assertEquals(new BigDecimal("50000"), july2026.getExtraServiceRevenue());
        assertEquals(new BigDecimal("300000"), july2026.getOrderRevenue());
        assertEquals(new BigDecimal("550000"), july2026.getRevenue());
    }
}
