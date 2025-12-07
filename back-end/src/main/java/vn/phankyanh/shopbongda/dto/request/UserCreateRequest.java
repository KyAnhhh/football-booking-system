package vn.phankyanh.shopbongda.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import vn.phankyanh.shopbongda.domain.Role;

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

    private final Role role = Role.ROLE_CLIENT;

    private final String status = "active";
}
