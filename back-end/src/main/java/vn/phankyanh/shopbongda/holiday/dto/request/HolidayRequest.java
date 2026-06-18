package vn.phankyanh.shopbongda.holiday.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class HolidayRequest {

    private String name;

    private LocalDate holidayDate;

    private String description;
}
