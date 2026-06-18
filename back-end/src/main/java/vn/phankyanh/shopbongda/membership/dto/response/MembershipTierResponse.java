package vn.phankyanh.shopbongda.membership.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MembershipTierResponse {

    private Long id;

    private String name;

    private Integer requiredPoints;

    private Integer discountPercent;

    private Boolean isActive;
}
