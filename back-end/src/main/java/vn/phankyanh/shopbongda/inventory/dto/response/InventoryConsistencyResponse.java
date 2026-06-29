package vn.phankyanh.shopbongda.inventory.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryConsistencyResponse {
    private boolean valid;
    private LocalDateTime checkedAt;
    private long itemsChecked;
    private long stocksChecked;
    private long activeReservationsChecked;
    private int issueCount;
    private List<String> issues;
}
