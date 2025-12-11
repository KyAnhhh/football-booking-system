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
    private String username;

    private String email;

    private String password;

    private String phone;

    private String status = "active";

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_CLIENT;
}
