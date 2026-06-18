package vn.phankyanh.shopbongda.extra.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
public class BookingExtraServiceRequest {

    @NotNull
    private Long serviceId;

    @NotNull
    @Positive
    private Integer quantity;
}
