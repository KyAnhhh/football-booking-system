package vn.phankyanh.shopbongda.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class OrderPriceCalculatorTest {

    @Test
    void calculatesDiscountAndLineTotal() {
        OrderPriceCalculator.Price price =
                OrderPriceCalculator.calculate(
                        new BigDecimal("200000"),
                        10,
                        3
                );

        assertEquals(new BigDecimal("20000"), price.discountAmount());
        assertEquals(new BigDecimal("180000"), price.finalPrice());
        assertEquals(new BigDecimal("540000"), price.total());
    }
}
