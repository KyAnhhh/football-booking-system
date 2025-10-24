package vn.phankyanh.shopbongda.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.phankyanh.shopbongda.entity.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {

    @Size(min = 6, max = 30, message = "USERNAME_INVALID")
    private String username;

    @Email(
            message = "EMAIL_INVALID"
    )
    private String email;

    @Size(min = 6, message = "PASSWORD_INVALID")
    private String password;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "PHONE_INVALID"
    )
    private String phone;


}
