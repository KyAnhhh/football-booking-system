package vn.phankyanh.shopbongda.field.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FieldSuggestionResponse {
    private LocalDate requestedDate;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;
    private Long requestedFieldId;
    private String requestedFieldType;
    private boolean hasExactAvailable;
    private List<SuggestedFieldSlotResponse> suggestions;
}
