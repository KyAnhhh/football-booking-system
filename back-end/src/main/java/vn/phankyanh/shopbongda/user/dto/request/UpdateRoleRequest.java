package vn.phankyanh.shopbongda.user.dto.request;

import lombok.Data;
import vn.phankyanh.shopbongda.user.domain.Role;

@Data
public class UpdateRoleRequest {
    private Role role;

}
