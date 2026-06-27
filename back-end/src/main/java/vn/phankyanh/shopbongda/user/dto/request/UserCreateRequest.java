package vn.phankyanh.shopbongda.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import vn.phankyanh.shopbongda.user.domain.Role;

@Data
public class UserCreateRequest {

    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @NotBlank(message = "Phone không được để trống")
    private String phone;

    private Role role = Role.ROLE_CLIENT;

    private String status = "active";
}
