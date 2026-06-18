package vn.phankyanh.shopbongda.holiday.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class HolidayResponse {

    private Long id;

    private String name;

    private LocalDate holidayDate;

    private String description;

    private Boolean isActive;
}
