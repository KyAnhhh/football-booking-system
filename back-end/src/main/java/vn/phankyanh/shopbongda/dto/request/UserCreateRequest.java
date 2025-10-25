package vn.phankyanh.shopbongda.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vn.phankyanh.shopbongda.domain.Role;

@Getter
public class UserCreateRequest {

    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @NotBlank(message = "Phone không được để trống")
    private String phone;

    @NotBlank(message = "Role không được để trống")
    private final Role role = Role.ROLE_CLIENT;

    @NotBlank(message = "Status không được để trống")
    private final String status = "active";
}
