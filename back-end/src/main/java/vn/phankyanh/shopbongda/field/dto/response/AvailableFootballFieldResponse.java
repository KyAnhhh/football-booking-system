package vn.phankyanh.shopbongda.field.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AvailableFootballFieldResponse {
    private Long id;
    private String name;
    private String type;
    private String imageUrl;
    private BigDecimal pricePerHour;
    private String status;
    private String address;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal slotPrice;
    private boolean available;
}
