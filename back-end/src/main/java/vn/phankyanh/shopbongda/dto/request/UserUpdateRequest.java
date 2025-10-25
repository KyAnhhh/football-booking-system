package vn.phankyanh.shopbongda.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.phankyanh.shopbongda.domain.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @NotBlank(message = "Phone không được để trống")
    private String phone;

    @NotBlank(message = "Status không được để trống")
    private String status = "active";

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_CLIENT;
}
