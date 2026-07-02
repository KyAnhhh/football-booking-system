package vn.phankyanh.shopbongda.dashboard.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import vn.phankyanh.shopbongda.dashboard.dto.response.RevenueChartResponse;

public final class MonthlyRevenueAggregator {

    private MonthlyRevenueAggregator() {
    }

    public static List<RevenueChartResponse> aggregate(
            List<Object[]> bookingRevenue,
            List<Object[]> orderRevenue
    ) {
        Map<YearMonth, BigDecimal> bookingTotals =
                toMonthlyTotals(bookingRevenue);
        Map<YearMonth, BigDecimal> orderTotals =
                toMonthlyTotals(orderRevenue);
        TreeMap<YearMonth, Boolean> periods = new TreeMap<>();
        bookingTotals.keySet().forEach(period -> periods.put(period, true));
        orderTotals.keySet().forEach(period -> periods.put(period, true));

        if (periods.isEmpty()) {
            return List.of();
        }

        YearMonth cursor = periods.firstKey();
        YearMonth end = periods.lastKey().isAfter(YearMonth.now())
                ? periods.lastKey()
                : YearMonth.now();
        List<RevenueChartResponse> result = new ArrayList<>();

        while (!cursor.isAfter(end)) {
            BigDecimal booking = bookingTotals.getOrDefault(
                    cursor,
                    BigDecimal.ZERO
            );
            BigDecimal order = orderTotals.getOrDefault(
                    cursor,
                    BigDecimal.ZERO
            );
            result.add(new RevenueChartResponse(
                    "%02d/%d".formatted(
                            cursor.getMonthValue(),
                            cursor.getYear()
                    ),
                    booking,
                    order,
                    booking.add(order)
            ));
            cursor = cursor.plusMonths(1);
        }

        return result;
    }

    private static Map<YearMonth, BigDecimal> toMonthlyTotals(
            List<Object[]> source
    ) {
        Map<YearMonth, BigDecimal> totals = new TreeMap<>();
        for (Object[] row : source) {
            YearMonth period = YearMonth.of(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue()
            );
            totals.merge(
                    period,
                    (BigDecimal) row[2],
                    BigDecimal::add
            );
        }
        return totals;
    }
}
