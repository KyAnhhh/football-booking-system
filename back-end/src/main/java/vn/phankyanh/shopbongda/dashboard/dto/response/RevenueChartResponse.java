package vn.phankyanh.shopbongda.dashboard.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RevenueChartResponse  {
    private String month;
    private BigDecimal revenue;
}
