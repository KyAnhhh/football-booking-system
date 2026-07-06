package vn.phankyanh.shopbongda.membership.dto.request;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipTierRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(0)
    private Integer requiredPoints;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer discountPercent;

    @NotNull
    private Boolean isActive;
}
