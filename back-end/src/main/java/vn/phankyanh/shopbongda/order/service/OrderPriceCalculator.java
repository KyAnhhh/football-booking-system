package vn.phankyanh.shopbongda.order.service;

import java.math.BigDecimal;

public final class OrderPriceCalculator {

    private OrderPriceCalculator() {
    }

    public static Price calculate(
            BigDecimal unitPrice,
            Integer discountPercent,
            Integer quantity
    ) {
        if (unitPrice == null || unitPrice.signum() < 0
                || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Dữ liệu tính giá không hợp lệ");
        }
        int discount = discountPercent == null ? 0 : discountPercent;
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá không hợp lệ");
        }

        BigDecimal discountAmount = unitPrice
                .multiply(BigDecimal.valueOf(discount))
                .divide(BigDecimal.valueOf(100));
        BigDecimal finalPrice = unitPrice.subtract(discountAmount);
        return new Price(
                unitPrice,
                discountAmount,
                finalPrice,
                finalPrice.multiply(BigDecimal.valueOf(quantity))
        );
    }

    public record Price(
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalPrice,
            BigDecimal total
    ) {
    }
}
