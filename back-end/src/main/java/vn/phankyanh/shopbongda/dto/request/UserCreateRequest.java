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

    @NotBlank(message = "Role không được để trống")
    private Role role;
}
