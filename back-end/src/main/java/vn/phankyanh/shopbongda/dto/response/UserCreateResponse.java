package vn.phankyanh.shopbongda.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.phankyanh.shopbongda.domain.Role;

@Getter
@Setter
@Builder
public class UserCreateResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private String status = "active";
}
