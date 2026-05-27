package vn.phankyanh.shopbongda.field.dto.request;

import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.field.domain.FieldStatus;

@Getter
@Setter
public class FieldStatusUpdateRequest {
    private FieldStatus status;
}
