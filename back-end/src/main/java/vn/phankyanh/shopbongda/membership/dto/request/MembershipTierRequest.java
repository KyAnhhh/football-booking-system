package vn.phankyanh.shopbongda.membership.dto.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipTierRequest {

    private String name;

    private Integer requiredPoints;

    private Integer discountPercent;

    private Boolean isActive;
}
