package vn.phankyanh.shopbongda.calendar.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFieldCalendarResponse {
    private LocalDate date;
    private List<LocalTime> slotStarts;
    private List<FieldRow> fields;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldRow {
        private Long fieldId;
        private String fieldName;
        private String fieldType;
        private String fieldStatus;
        private List<SlotItem> slots;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotItem {
        private LocalTime startTime;
        private LocalTime endTime;
        private String type;
        private String status;
        private Long bookingId;
        private Long maintenanceId;
        private String customerName;
        private String fieldName;
        private String reason;
        private BigDecimal price;
    }
}
