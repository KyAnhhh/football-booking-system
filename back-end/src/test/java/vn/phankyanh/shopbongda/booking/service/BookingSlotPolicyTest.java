package vn.phankyanh.shopbongda.booking.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class BookingSlotPolicyTest {

    private static final LocalDate DATE = LocalDate.of(2030, 1, 1);

    @Test
    void acceptsLastCompleteSlot() {
        assertDoesNotThrow(() -> BookingSlotPolicy.validate(
                DATE.atTime(22, 0),
                DATE.atTime(23, 30)
        ));
    }

    @Test
    void rejectsPartialSlotAtEndOfDay() {
        assertThrows(IllegalArgumentException.class, () ->
                BookingSlotPolicy.validate(
                        DATE.atTime(23, 30),
                        DATE.atTime(23, 45)
                ));
    }

    @Test
    void rejectsStartOutsideSlotGrid() {
        assertThrows(IllegalArgumentException.class, () ->
                BookingSlotPolicy.validate(
                        DATE.atTime(7, 30),
                        DATE.atTime(9, 0)
                ));
    }
}
