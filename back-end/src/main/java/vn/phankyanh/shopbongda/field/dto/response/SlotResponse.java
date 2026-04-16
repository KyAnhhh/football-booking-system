package vn.phankyanh.shopbongda.field.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SlotResponse {
    private String start;
    private String end;
    private boolean available;
}
